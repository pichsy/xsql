package com.pichs.xsql.dao;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.pichs.xsql.annotation.XSqlField;
import com.pichs.xsql.annotation.XSqlPrimaryKey;
import com.pichs.xsql.base.Database;
import com.pichs.xsql.config.DBConfig;
import com.pichs.xsql.base.DatabaseOpenHelper;
import com.pichs.xsql.base.DatabaseStatement;
import com.pichs.xsql.utils.DataBaseUtil;
import com.pichs.xsql.utils.XSqlLog;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseDao<T> extends AbstractDao<T> {

    protected int mOldVersion = 1;
    protected int mNewVersion = 1;
    private final static String prefix = "bak_";

    /**
     * 初始化
     *
     * @param dbConfig    DBConfig
     * @param entityClass 实体类
     * @return 是否初始化成功
     * @throws SQLException 异常
     */
    public synchronized boolean init(DBConfig dbConfig, Class<T> entityClass) throws SQLException {
        String _dbName = dbConfig.getDBName();
        if (!TextUtils.isEmpty(dbConfig.getDbDir())) {
            _dbName = dbConfig.getContext().getFilesDir().getAbsolutePath() + File.separator + dbConfig.getDbDir() + File.separator + _dbName;
        }
        return super.init(new DevOpenHelper(dbConfig.getContext(), _dbName, dbConfig.getVersion()), entityClass);
    }

    /**
     * 初始化
     *
     * @param entityClass 实体类
     * @return 是否初始化成功
     * @throws SQLException 异常
     */
    @Override
    public synchronized boolean init(DatabaseOpenHelper openHelper, Class<T> entityClass) throws SQLException {
        return super.init(openHelper, entityClass);
    }

    /**
     * 建表 create table for current EntityClass
     *
     * @return {true: do your self} or {false: user default}
     * {autoCreateTable()}
     */
    @Override
    protected boolean createTable() {
        return false;
    }

    /**
     * 更新表 Upgrade table for current EntityClass
     *
     * @return {true: do your self} or {false: user default}
     * { autoUpdateTable()}
     */
    protected boolean upgradeTable() {
        XSqlLog.d("BaseDao", "upgradeTable");
        if (upLevelTable()) {
            return true;
        }
        return false;
    }

    private boolean upLevelTable() {
        XSqlLog.d("BaseDao", "upLevelTable");
        try {
            // 获取所有现在的数据库需要拷贝字段。
            Map<String, Field> copyColumns = new HashMap<>();
            Map<String, String> addColumnsList = new HashMap<>();
            List<String> removeColumnList = new ArrayList<>(allColumnList);
            // 遍历方法
            Field[] fields = mEntityClass.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                XSqlField sqlField = field.getAnnotation(XSqlField.class);
                if (sqlField == null) {
                    continue;
                }
                String columnName = sqlField.value().trim();
                if (TextUtils.isEmpty(columnName)) {
                    continue;
                }
                // 过滤自增长ID
                if (field.getAnnotation(XSqlPrimaryKey.class) != null && field.getAnnotation(XSqlPrimaryKey.class).value()) {
                    removeColumnList.remove(columnName);
                    continue;
                }
                if (allColumnList.contains(columnName)) {
                    XSqlLog.d("BaseDao", "：： columnName: " + columnName);
                    copyColumns.put(columnName, field);
                    removeColumnList.remove(columnName);
                } else {
                    String fieldToSqlType = DataBaseUtil.getFieldToSqlType(field);
                    XSqlLog.d("BaseDao", "columnName: " + columnName + "  : ###  : fieldToSqlType: " + fieldToSqlType);
                    addColumnsList.put(columnName, fieldToSqlType);
                }
            }

            // 没有需要删除的字段，也没有需要增加的字段。
            if (removeColumnList.size() == 0 && addColumnsList.size() == 0) {
                XSqlLog.d("BaseDao", "：：没有字段变化，无需升级");
                return true;
            }

            // 又增加的字段，直接使用增加字段的方式升级。
            if (removeColumnList.size() == 0) {
                addNewColumns(addColumnsList);
                return true;
            }

            // 有删除的字段,不管有没有新增的字段都要用下面的方式升级。
            beginTransaction();
            try {
                synchronized (BaseDao.class) {
                    XSqlLog.d("BaseDao", "xsql 复杂升级");
                    // 1、 能拷贝的字段必须是上个版本数据库中含有的字段，不支持重命名的字段。
                    // 2、开始重命名表 格式 bak_${mTableName}。
                    renameTable();
                    // 3、创建新表。
                    createNewTable();
                    // 4、复制数据到新表中
                    copyDataToNewTable(copyColumns);
                    // 5、删除临时表 bak_${mTableName}。
                    dropBakTable();
                }
                setTransactionSuccessful();
            } finally {
                endTransaction();
            }
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    private boolean hasPrimaryKey(Map<String, Field> copyColumns) {
        for (Map.Entry<String, Field> entry : copyColumns.entrySet()) {
            XSqlPrimaryKey annotation = entry.getValue().getAnnotation(XSqlPrimaryKey.class);
            if (annotation != null && annotation.value()) {
                return true;
            }
        }
        return false;
    }

    // 大部分情况新增列
    private synchronized void addNewColumns(Map<String, String> newColumns) throws SQLException {
        // 添加列必须一列一列的添加，不能直接添加多列（很蛋疼）
        beginTransaction();
        try {
            StringBuilder sql;
            for (Map.Entry<String, String> map : newColumns.entrySet()) {
                sql = new StringBuilder();
                sql.append("ALTER TABLE ")
                        .append(mTableName)
                        .append(" ADD COLUMN ")
                        .append(map.getKey())
                        .append(" ")
                        .append(map.getValue());
                XSqlLog.d("BaseDao", "::新增字段 Add  Columns %%% sql=" + sql.toString());
                mDatabase.execSQL(sql.toString());
            }
            setTransactionSuccessful();
        } finally {
            endTransaction();
        }
    }

    // 2、重命名表
    private void renameTable() throws SQLException {
        XSqlLog.d("BaseDao", "开始重命名表...: TableName=" + prefix + mTableName);
        String renamSql = "ALTER TABLE " + mTableName + " RENAME TO " + prefix + mTableName;
        mDatabase.execSQL(renamSql);
    }

    // 3、创建新的表。
    private void createNewTable() throws SQLException {
        XSqlLog.d("BaseDao", "创建新的表...TableName=" + mTableName);
        StringBuilder sb = new StringBuilder()
                .append("CREATE TABLE ")
                .append(mTableName)
                .append(" (");
        makeCreateTableSql(sb);
        mDatabase.execSQL(sb.toString());
    }

    // 4、复制数据到新表
    private void copyDataToNewTable(Map<String, Field> copyColumns) {
        Cursor cursor = null;
        try {
            XSqlLog.d("BaseDao", "复制数据到新表...：copyDataToNewTable");
            StringBuilder sqlQuery = new StringBuilder("SELECT ");
            buildSelect(sqlQuery, copyColumns);
            sqlQuery.append(" FROM ")
                    .append(prefix)
                    .append(mTableName);
            // 取数据
            cursor = mDatabase.rawQuery(sqlQuery.toString(), null);
            List<T> result = DataBaseUtil.getResult(mEntityClass, cursor, copyColumns);
            for (T entity : result) {
                Object[] binds = DataBaseUtil.buildInsertSqlAndBindArgs(mTableName, entity, copyColumns);
                XSqlLog.d("copyDataToNewTable", "sql: " + (String) binds[0]);
                DatabaseStatement stmt = mDatabase.compileStatement((String) binds[0]);
                DataBaseUtil.bindValues(stmt, (Object[]) binds[1]);
                stmt.executeInsert();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

    }

    private void buildSelect(StringBuilder sb, Map<String, Field> copyColumns) {
        for (Map.Entry<String, Field> column : copyColumns.entrySet()) {
            String key = column.getKey();
            Field value = column.getValue();
            // 去除自增id的拼接
            if (DataBaseUtil.isPrimaryKey(value)) {
                continue;
            }
            sb.append(key).append(",");
        }
        int index = sb.lastIndexOf(",");
        if (index != -1) {
            sb.deleteCharAt(index);
        }
    }

    // 5、删除删除临时表
    private void dropBakTable() throws SQLException {
        String delTable = "drop table " + prefix + mTableName;
        XSqlLog.d("BaseDao", "删除删除临时表...： " + delTable);
        mDatabase.execSQL(delTable);
    }


    /*
     * 数据库帮助类，
     * 用于升级，数据库，和扩展功能。
     */
    class DevOpenHelper extends DatabaseOpenHelper {

        public DevOpenHelper(Context context, String name, int version) {
            super(context, name, version);
            mNewVersion = version;
        }

        @Override
        public void onUpgrade(Database db, int oldVersion, int newVersion) {
            super.onUpgrade(db, oldVersion, newVersion);
            mOldVersion = oldVersion;
        }

    }

}
