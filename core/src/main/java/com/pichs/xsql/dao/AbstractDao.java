package com.pichs.xsql.dao;

import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.pichs.xsql.annotation.XSqlField;
import com.pichs.xsql.annotation.XSqlPrimaryKey;
import com.pichs.xsql.annotation.XSqlTable;
import com.pichs.xsql.annotation.XSqlUnique;
import com.pichs.xsql.base.Database;
import com.pichs.xsql.base.DatabaseOpenHelper;
import com.pichs.xsql.base.DatabaseStatement;
import com.pichs.xsql.model.NameField;
import com.pichs.xsql.utils.DataBaseUtil;
import com.pichs.xsql.utils.SqlUtils;
import com.pichs.xsql.utils.TableUtil;
import com.pichs.xsql.utils.XSqlLog;
import com.pichs.xsql.where.Where;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据库操作类
 * 基础操作类
 *
 * @param <T> 实体类
 */
public abstract class AbstractDao<T> implements IBaseDao<T> {
    private static final String TAG = "AbstractDao";
    protected String mTableName;
    private final Object mLock = new Object();
    /**
     * 实体类
     */
    protected Class<T> mEntityClass;
    /**
     * 缓存数据库名字，对应的entry的Field对象
     * string： 数据库的字段
     * Field：实体对象的属性
     */
    protected ConcurrentHashMap<String, Field> mNameFieldCache;
    /**
     * 是否初始化
     */
    protected boolean isInit = false;
    /**
     * 数据库对象
     */
    protected Database mDatabase;
    /**
     * 数据库所有的字段
     * 字段缓存，自动升级时使用。
     */
    protected List<String> allColumnList;

    /**
     * 初始化
     *
     * @param openHelper  数据库帮助类
     * @param entityClass 实体类
     * @return 是否初始化成功
     * @throws SQLException 异常
     */
    public synchronized boolean init(DatabaseOpenHelper openHelper, Class<T> entityClass) throws SQLException {
        if (!isInit) {
            mEntityClass = entityClass;
            XSqlTable sqlTable = entityClass.getAnnotation(XSqlTable.class);
            if (sqlTable == null) {
                return false;
            }
            mTableName = sqlTable.value().trim();
            if (TextUtils.isEmpty(mTableName)) {
                return false;
            }
            mDatabase = openHelper.getWriteDatabase();
            if (!mDatabase.isOpen()) {
                return false;
            }
            XSqlLog.d("AbstractDao", "DATABASE  : Path= " + mDatabase.getSQLiteDatabase().getPath());
            if (!autoCreateTable()) {
                XSqlLog.d("AbstractDao", "DATABASE : autoCreateTable failed");
                return false;
            }
            allColumnList = TableUtil.getTableColumns(mDatabase, mTableName);
            XSqlLog.d("AbstractDao", "DATABASE : allColumnList=" + allColumnList);
            if (allColumnList == null) {
                return false;
            }
            if (!autoUpgradeTable()) {
                return false;
            }
            if (!initCache()) {
                return false;
            }
            isInit = true;
        }
        return true;
    }

    private boolean autoUpgradeTable() {
        if (upgradeTable()) {
            return true;
        }
        return true;
    }

    /**
     * 自动建表
     *
     * @return 是否建表成功
     */
    protected abstract boolean createTable();

    /**
     * 自动升级表
     *
     * @return 是否升级成功
     */
    protected abstract boolean upgradeTable();

    /**
     * 初始化缓存，value-field
     *
     * @return 是否初始化缓存成功
     */
    private boolean initCache() {
        List<String> colList = TableUtil.getTableColumns(mDatabase, mTableName);
        if (colList == null) {
            return false;
        }
        mNameFieldCache = new ConcurrentHashMap<>();
        for (String column : colList) {
            Field[] colFields = mEntityClass.getDeclaredFields();
            if (colFields == null || colFields.length == 0) {
                // 没属性，玩不起啊
                return false;
            }
            Field resultField = null;
            for (Field colField : colFields) {
                XSqlField annotation = colField.getAnnotation(XSqlField.class);
                if (annotation == null) {
                    continue;
                }
                if (column.equals(annotation.value())) {
                    resultField = colField;
                    break;
                }
            }
            if (resultField != null) {
                mNameFieldCache.put(column, resultField);
            }
        }
        return true;
    }


    /**
     * 自动建表
     *
     * @return 是否创建成功
     * @throws SQLException 数据库异常
     */
    private boolean autoCreateTable() throws SQLException {
        if (createTable()) {
            return true;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ")
                .append(mTableName)
                .append(" (");
        makeCreateTableSql(sb);
        this.mDatabase.execSQL(sb.toString());
        return true;
    }

    /**
     * 不支持char和Character
     *
     * @param sb String缓存
     * @throws SQLException 数据库异常
     */

    protected void makeCreateTableSql(StringBuilder sb) throws SQLException {
        Field[] fields = mEntityClass.getDeclaredFields();
        int primaryKeyCount = 0;
        int uniqueCount = 0;
        for (Field field : fields) {
            // 不加注解XSqlField的属性不添加到数据库
            XSqlField annotation = field.getAnnotation(XSqlField.class);
            if (annotation == null) {
                continue;
            }
            XSqlUnique sqlUniqueAnnotation = field.getAnnotation(XSqlUnique.class);
            if (sqlUniqueAnnotation != null && sqlUniqueAnnotation.value()) {
                uniqueCount++;
            }
            XSqlPrimaryKey primaryKeyAnnotation = field.getAnnotation(XSqlPrimaryKey.class);
            if (primaryKeyAnnotation != null && primaryKeyAnnotation.value()) {
                if (!SqlUtils.isPrimaryKeySupportType(field)) {
                    throw new SQLException("sql table: " + mTableName + "  generateId is a not support type " + field.getType().getName() + " , just support int and long type!");
                }
                primaryKeyCount++;
            }
            if (!SqlUtils.isSupportClassType(field)) {
                throw new SQLException("sql table: " + mTableName + "  field is a not support type " + field.getType().getName() + " , just support Integer, Long, Double, Float, byte[], String, Boolean type!");
            }
            // 不支持的类型 添加到数据库抛异常
            String fieldType = DataBaseUtil.getFieldToSqlType(field);
            // 支持处理
            String fieldName = annotation.value().trim();
            if (TextUtils.isEmpty(fieldName)) {
                throw new SQLException("sql table: " + mTableName + "  XSqlField can not be space or empty ,please rename it");
            }
            sb.append(fieldName)
                    .append(" ")
                    .append(fieldType)
                    .append(",");
        }
        int index = sb.lastIndexOf(",");
        if (index != -1) {
            sb.deleteCharAt(index);
        }
        sb.append(")");

        // 没有一个唯一的字段，抛异常
        // TODO: 2020/12/18 1111---> 去除限制，没有唯一键仍可以创建表
//        if (primaryKeyCount + uniqueCount == 0) {
//            throw new SQLException("sql table: " + mTableName + "  has no  unique column or generateId column, please set at least one!");
//        }

        // 有两个自增长键, 错误，不符合规则
        if (primaryKeyCount > 1) {
            throw new SQLException("sql table: " + mTableName + " has " + primaryKeyCount + " generateId, SqliteDatabase only support one generateId!");
        }
    }

    /**
     * 插入数据
     *
     * @param entity 实体类
     * @return 写入成功与否 [1 is success, -1 is failed]
     */
    @Override
    public long insert(T entity) {
        if (isInit) {
            Object[] binds = DataBaseUtil.buildInsertSqlAndBindArgs(mTableName, entity, mNameFieldCache);
            DatabaseStatement stmt = mDatabase.compileStatement((String) binds[0]);
            long result;
            if (mDatabase.isDbLockedByCurrentThread()) {
                result = insertInsideTx(binds[1], stmt);
            } else {
                beginTransaction();
                try {
                    result = insertInsideTx(binds[1], stmt);
                    setTransactionSuccessful();
                } finally {
                    endTransaction();
                }
            }
            return result <= 0 ? 0 : 1;
        }
        return 0;
    }

    /**
     * 批量插入数据
     *
     * @param entities 实体类集合
     * @return 是否插入成功 [1 is success, 0 is failed]
     */
    @Override
    public long insertInTx(List<T> entities) {
        if (!isInit) {
            return 0;
        }
        long result = 0;
        beginTransaction();
        try {
            synchronized (mLock) {
                for (T entity : entities) {
                    Object[] binds = DataBaseUtil.buildInsertSqlAndBindArgs(mTableName, entity, mNameFieldCache);
                    DatabaseStatement stmt = mDatabase.compileStatement((String) binds[0]);
                    DataBaseUtil.bindValues(stmt, (Object[]) binds[1]);
                    result = stmt.executeInsert();
                }
            }
            setTransactionSuccessful();
        } catch (Exception e) {
            return result;
        } finally {
            endTransaction();
        }
        return result <= 0 ? 0 : 1;
    }


    /**
     * 更新数据
     *
     * @param entity 实体类
     * @return 更新成功与否 [1 is success, 0 is failed]
     */
    @Override
    public int update(T entity) {
        if (!isInit) {
            return 0;
        }
        synchronized (mLock) {
            List<NameField> list = DataBaseUtil.getPrimaryKey(mNameFieldCache);
            if (list == null) {
                list = DataBaseUtil.getUnique(mNameFieldCache);
            } else {
                Field field = list.get(0).getField();
                String args = DataBaseUtil.getFieldValue(entity, field);
                if (args == null) {
                    list = DataBaseUtil.getUnique(mNameFieldCache);
                }
            }
            XSqlLog.d("AbstractDao", "update: list=" + list);
            String whereCause = "";
            String[] argsArr = null;
            if (list != null && list.size() > 0) {
                String colName = list.get(0).getName();
                Field field = list.get(0).getField();
                String args = DataBaseUtil.getFieldValue(entity, field);
                XSqlLog.d("Abs BaseDao", "update: args=" + args);
                if (args != null) {
                    argsArr = new String[]{args};
                    whereCause = colName + "=?";
                }
            }

            if (TextUtils.isEmpty(whereCause) || argsArr == null || argsArr.length == 0) {
//                Object[] objs = makeConditionSql(entity);
//                if (objs != null && objs[0] != null) {
//                    argsArr = (String[]) objs[1];
//                    whereCause = (String) objs[0];
//                }
                return 0;
            }

            int result = 0;
            if (mDatabase.isDbLockedByCurrentThread()) {
                result = mDatabase.getSQLiteDatabase().update(mTableName, DataBaseUtil.makeContentValues(entity, mNameFieldCache), whereCause, argsArr);
            } else {
                beginTransaction();
                try {
                    result = mDatabase.getSQLiteDatabase().update(mTableName, DataBaseUtil.makeContentValues(entity, mNameFieldCache), whereCause, argsArr);
                    setTransactionSuccessful();
                } finally {
                    endTransaction();
                }
            }
            return result <= 0 ? 0 : 1;
        }
    }

    /**
     * 更新数据
     *
     * @param where  条件
     * @param entity 实体类
     * @return 更新成功与否 [1 is success, 0 is failed]
     */
    @Override
    public int update(Where where, T entity) {
        if (!isInit || where == null) {
            return 0;
        }
        synchronized (mLock) {
            if (TextUtils.isEmpty(where.getWhereCause())) {
                return 0;
            }
            return mDatabase.getSQLiteDatabase().update(mTableName, DataBaseUtil.makeContentValues(entity, mNameFieldCache), where.getWhereCause(), where.getArgs());
        }
    }

    /**
     * 删除数据
     *
     * @param entity 实体类
     * @return 是否删除成功 [1 is success, 0 is failed]
     */
    @Override
    public int delete(T entity) {
        if (isInit) {
            synchronized (mLock) {
                List<NameField> list = DataBaseUtil.getPrimaryKey(mNameFieldCache);
                if (list == null) {
                    list = DataBaseUtil.getUnique(mNameFieldCache);
                } else {
                    Field field = list.get(0).getField();
                    String args = DataBaseUtil.getFieldValue(entity, field);
                    if (args == null) {
                        list = DataBaseUtil.getUnique(mNameFieldCache);
                    }
                }
                XSqlLog.d("Abs BaseDao", "update: list=" + list);
                String whereCause = "";
                String[] argsArr = null;
                if (list != null && list.size() > 0) {
                    String colName = list.get(0).getName();
                    Field field = list.get(0).getField();
                    String args = DataBaseUtil.getFieldValue(entity, field);
                    XSqlLog.d("Abs BaseDao", "update: args=" + args);
                    if (args != null) {
                        argsArr = new String[]{args};
                        whereCause = colName + "=?";
                    }
                }
                if (TextUtils.isEmpty(whereCause) || argsArr == null || argsArr.length == 0) {
                    Object[] objs = makeConditionSql(entity);
                    if (objs != null && objs[0] != null) {
                        argsArr = (String[]) objs[1];
                        whereCause = (String) objs[0];
                    }
                }
                int result = 0;
                // 没条件 直接返回失败
                if (TextUtils.isEmpty(whereCause) || argsArr == null || argsArr.length == 0) {
                    return result;
                }
                if (mDatabase.isDbLockedByCurrentThread()) {
                    result = mDatabase.getSQLiteDatabase().delete(mTableName, whereCause, argsArr);
                } else {
                    beginTransaction();
                    try {
                        result = mDatabase.getSQLiteDatabase().delete(mTableName, whereCause, argsArr);
                        setTransactionSuccessful();
                    } finally {
                        endTransaction();
                    }
                }
                return result;
            }
        }
        return 0;
    }

    /**
     * 删除数据
     *
     * @param where 条件
     * @return 是否删除成功 [1 is success, 0 is failed]
     */
    @Override
    public int delete(Where where) {
        if (!isInit || where == null) {
            return 0;
        }
        synchronized (mLock) {
            if (TextUtils.isEmpty(where.getWhereCause())) {
                return 0;
            }
            return mDatabase.getSQLiteDatabase().delete(mTableName, where.getWhereCause(), where.getArgs());
        }
    }

    /**
     * 删除所有的数据
     *
     * @return 是都删除成功 [1 is success, 0 is failed]
     */
    @Override
    public int deleteAll() {
        if (isInit) {
            synchronized (mLock) {
                try {
                    beginTransaction();
                    String sql = "DELETE FROM " + mTableName;
                    mDatabase.execSQL(sql);
                    String sql2 = "UPDATE sqlite_sequence SET seq = 0 WHERE name = '" + mTableName + "'";
                    mDatabase.execSQL(sql2);
                    setTransactionSuccessful();
                    return 1;
                } catch (SQLException e) {
                    return 0;
                } finally {
                    endTransaction();
                }
            }
        }
        return 0;
    }


    /**
     * 查询数据
     *
     * @param entity 实体类
     * @return 查询数据结果列表
     */
    @Override
    public List<T> query(T entity) {
        if (!isInit) {
            return null;
        }
        synchronized (mLock) {
            List<NameField> list = DataBaseUtil.getPrimaryKey(mNameFieldCache);
            if (list == null) {
                list = DataBaseUtil.getUnique(mNameFieldCache);
            } else {
                Field field = list.get(0).getField();
                String args = DataBaseUtil.getFieldValue(entity, field);
                if (args == null) {
                    list = DataBaseUtil.getUnique(mNameFieldCache);
                }
            }
            String sqlPrefix = "SELECT * FROM " + mTableName + " WHERE ";
            if (list != null) {
                String colName = list.get(0).getName();
                Field field = list.get(0).getField();
                String args = DataBaseUtil.getFieldValue(entity, field);
                XSqlLog.d("Abs BaseDao", "query: args=" + args);
                if (args != null) {
                    Cursor cursor = mDatabase.rawQuery(sqlPrefix + colName + " =?", new String[]{args});
                    return DataBaseUtil.getResult(mEntityClass, cursor, mNameFieldCache);
                }
            }
            Object[] objs = makeConditionSql(entity);
            if (objs != null && objs[0] != null) {
                Cursor cursor = mDatabase.rawQuery(sqlPrefix + (String) objs[0], (String[]) objs[1]);
                return DataBaseUtil.getResult(mEntityClass, cursor, mNameFieldCache);
            }
            return null;
        }
    }

    /**
     * 生成条件sql语句，和 条件对象
     *
     * @param entity 实体类
     * @return Object[0] sql语句，Object[1] 条件对象
     */
    private Object[] makeConditionSql(T entity) {
        if (entity == null) {
            return null;
        }
        Object[] objects = new Object[2];
        List<String> argList = new ArrayList<>();
        StringBuffer sqlSB = new StringBuffer();
        for (Map.Entry<String, Field> fieldEntry : mNameFieldCache.entrySet()) {
            String fieldValue = DataBaseUtil.getFieldValue(entity, fieldEntry.getValue());
            if (fieldValue == null) {
                continue;
            }
            sqlSB
                    .append(fieldEntry.getKey())
                    .append("=?")
                    .append(" AND");
            argList.add(fieldValue);
        }
        if (sqlSB.length() > 4) {
            sqlSB.delete(sqlSB.length() - 4, sqlSB.length());
        }
        objects[0] = sqlSB.toString();
        String[] args = new String[argList.size()];
        objects[1] = argList.toArray(args);
        return objects;
    }

    /**
     * 查询数据
     *
     * @param where 条件
     * @return 查询数据结果列表
     */
    @Override
    public List<T> query(Where where) {
        if (!isInit || where == null) {
            return null;
        }
        synchronized (mLock) {
            String sqlPrefix = "SELECT * FROM " + mTableName;
            String sql = sqlPrefix + Where.combineWhere(where, true, true, true);
            Cursor cursor = mDatabase.rawQuery(sql, where.getArgs());
            return DataBaseUtil.getResult(mEntityClass, cursor, mNameFieldCache);
        }
    }

    /**
     * 查询所有数据
     *
     * @return 查询数据结果列表
     */
    @Override
    public List<T> queryAll() {
        if (isInit) {
            String sql = "SELECT * FROM " + mTableName;
            Cursor cursor = mDatabase.rawQuery(sql, null);
            return DataBaseUtil.getResult(mEntityClass, cursor, mNameFieldCache);
        }
        return null;
    }

    /**
     * 查询最后一条数据
     *
     * @return 查询数据结果列表
     */
    @Override
    public List<T> queryLastOne() {
        if (isInit) {
            String sql = "SELECT * FROM " + mTableName + " LIMIT 1 OFFSET (SELECT COUNT(*) - 1  FROM " + mTableName + ")";
            Cursor cursor = mDatabase.rawQuery(sql, null);
            return DataBaseUtil.getResult(mEntityClass, cursor, mNameFieldCache);
        }
        return null;
    }

    /**
     * 获取数据库的数据总数
     *
     * @return long 数据库总长度
     */
    public long getCount() {
        String sql = "SELECT COUNT(*) FROM \"" + mTableName + '"';
        return mDatabase.compileStatement(sql).simpleQueryForLong();
    }

    /**
     * 返回数据库版本
     *
     * @return 数据库版本
     */
    protected int getVersion() {
        return mDatabase.getSQLiteDatabase().getVersion();
    }

    /**
     * 开启事务
     */
    @Override
    public void beginTransaction() {
        mDatabase.beginTransaction();
    }

    /**
     * 结束事务
     */
    @Override
    public void endTransaction() {
        mDatabase.endTransaction();
    }

    /**
     * 在事务中执行
     */
    @Override
    public void inTransaction() {
        mDatabase.inTransaction();
    }

    /**
     * 设置事务执行成功并提交数据
     */
    @Override
    public void setTransactionSuccessful() {
        mDatabase.setTransactionSuccessful();
    }

    /**
     * 插入数据
     *
     * @param bind 绑定的数据
     * @param stmt 数据库操作对象
     * @return 是否插入成功 [1 is success, 0 is failed]
     */
    private long insertInsideTx(Object bind, DatabaseStatement stmt) {
        long result = -1;
        try {
            synchronized (mLock) {
                DataBaseUtil.bindValues(stmt, (Object[]) bind);
                result = stmt.executeInsert();
            }
        } catch (Exception e) {
            return result;
        }
        return result <= 0 ? -1 : 1;
    }

}
