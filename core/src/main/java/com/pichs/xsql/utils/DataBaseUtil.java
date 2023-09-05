package com.pichs.xsql.utils;

import android.content.ContentValues;
import android.database.Cursor;

import com.pichs.xsql.annotation.XSqlPrimaryKey;
import com.pichs.xsql.annotation.XSqlUnique;
import com.pichs.xsql.convert.ConvertFactory;
import com.pichs.xsql.base.DatabaseStatement;
import com.pichs.xsql.model.NameField;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import java.util.Map;

public class DataBaseUtil {
    private static final String BLANK_SPACE = " ";

    /**
     * 获取SQL类型
     *
     * @param field java数据类型，支持基本数据类型，和String，byte[](暂不支持其他类型）
     * @return SQL类型 {field}
     * @throws SQLException 数据库异常
     */
    public static String getFieldToSqlType(Field field) throws SQLException {
        String sqlFieldType = ConvertFactory.getConverter(field.getType()).getSqlColumnType().getValue();
        if (isPrimaryKey(field)) {
            return BLANK_SPACE + sqlFieldType + SqlCheck.PRIMARY_KEY;
        } else if (isUnique(field)) {
            return BLANK_SPACE + sqlFieldType + SqlCheck.UNIQUE;
        } else {
            return BLANK_SPACE + sqlFieldType;
        }
    }

    /**
     * 创建contentValues
     *
     * @param entity   实体对象
     * @param cacheMap 缓存
     * @param <T>      泛型
     * @return ContentValues
     */
    public static <T> ContentValues makeContentValues(T entity, Map<String, Field> cacheMap) {
        ContentValues contentValues = new ContentValues();
        for (Map.Entry<String, Field> next : cacheMap.entrySet()) {
            // 属性
            Field field = next.getValue();
            // 表中字段
            field.setAccessible(true);

            // 判断自增键，如果是则不添加到数据库
            if (isPrimaryKey(field)) {
                continue;
            }

            String key = next.getKey();
            try {
                Object object = field.get(entity);
                if (object == null) {
                    continue;
                }
                String fieldTypeName = field.getType().getName();
                if (fieldTypeName.equals(String.class.getName())) {
                    String value = (String) object;
                    contentValues.put(key, value);
                } else if (fieldTypeName.equals(Long.class.getName())) {
                    long value = (long) object;
                    contentValues.put(key, value);
                } else if (fieldTypeName.equals(Integer.class.getName())) {
                    int value = (int) object;
                    contentValues.put(key, value);
                } else if (fieldTypeName.equals(Double.class.getName())) {
                    double value = (double) object;
                    contentValues.put(key, value);
                } else if (fieldTypeName.equals(Float.class.getName())) {
                    float value = (float) object;
                    contentValues.put(key, value);
                } else if (fieldTypeName.equals(Boolean.class.getName())) {
                    boolean bool = (boolean) object;
                    contentValues.put(key, bool);
                } else if (fieldTypeName.equals(byte[].class.getName())) {
                    byte[] value = (byte[]) object;
                    contentValues.put(key, value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return contentValues;
    }

    /**
     * 判断是否是自增的id
     *
     * @param field Field对象
     * @return true or false
     */
    public static boolean isPrimaryKey(Field field) {
        XSqlPrimaryKey primaryKey = field.getAnnotation(XSqlPrimaryKey.class);
        return primaryKey != null && primaryKey.value();
    }

    /**
     * 是否是SqlUnique注解的字段
     *
     * @param field Field对象
     * @return true or false
     */
    public static boolean isUnique(Field field) {
        XSqlUnique unique = field.getAnnotation(XSqlUnique.class);
        return unique != null && unique.value();
    }

    /**
     * 获取查询实体对象列表数据
     *
     * @param entityClass 实体
     * @param cursor      Cursor对象
     * @param cacheMap    缓存
     * @param <T>         泛型
     * @return 实体列表数据
     */
    public synchronized static <T> List<T> getResult(Class<T> entityClass, Cursor cursor, Map<String, Field> cacheMap) {
        List<T> result = new ArrayList<>();
        try {
            T item;
            while (cursor.moveToNext()) {
                item = entityClass.newInstance();
                for (Map.Entry<String, Field> next : cacheMap.entrySet()) {
                    // 数据库中的列名（字段）
                    String colName = next.getKey();
                    // 根据列名后去游标位置
                    int colIndex = cursor.getColumnIndex(colName);
                    if (colIndex == -1) {
                        continue;
                    }
                    // 根据存入类型获取数据
                    Field field = next.getValue();
                    field.setAccessible(true);

                    String fieldTypeName = field.getType().getName();
                    // 不支持char 类型数据存储
                    if (fieldTypeName.equals(String.class.getName())) {
                        field.set(item, cursor.getString(colIndex));
                    } else if (fieldTypeName.equals(Long.class.getName())) {
                        field.set(item, cursor.getLong(colIndex));
                    } else if (fieldTypeName.equals(Integer.class.getName())) {
                        field.set(item, cursor.getInt(colIndex));
                    } else if (fieldTypeName.equals(Double.class.getName())) {
                        field.set(item, cursor.getDouble(colIndex));
                    } else if (fieldTypeName.equals(Float.class.getName())) {
                        field.set(item, cursor.getFloat(colIndex));
                    } else if (fieldTypeName.equals(Boolean.class.getName())) {
                        field.set(item, (cursor.getInt(colIndex) != 0));
                    } else if (fieldTypeName.equals(byte[].class.getName())) {
                        field.set(item, cursor.getBlob(colIndex));
                    }
                }
                result.add(item);
            }
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return result;
    }

    /**
     * update sql 拼接
     *
     * @param table          表名
     * @param entity         实体
     * @param nameFieldCache 缓存
     * @param <T>            泛型
     * @return 对象数据
     */
    public static <T> Object[] buildInsertSqlAndBindArgs(String table, T entity, Map<String, Field> nameFieldCache) {
        ContentValues initialValues = makeContentValues(entity, nameFieldCache);
        StringBuilder sql = new StringBuilder();
        Object[] bindArgs = null;
        sql.append("INSERT");
        sql.append(" INTO ");
        sql.append(table);
        sql.append('(');
        synchronized (table) {
            int size = initialValues != null ? initialValues.size() : 0;
            if (size > 0) {
                bindArgs = new Object[size];
                int i = 0;
                for (String colName : initialValues.keySet()) {
                    sql.append((i > 0) ? "," : "");
                    sql.append(colName);
                    bindArgs[i++] = initialValues.get(colName);
                }
                sql.append(')');
                sql.append(" VALUES (");
                for (i = 0; i < size; i++) {
                    sql.append((i > 0) ? ",?" : "?");
                }
            } else {
                sql.append(") VALUES (NULL");
            }
            sql.append(')');
        }
        return new Object[]{sql.toString(), bindArgs};
    }

    /**
     * update sql 拼接
     *
     * @param table          表名
     * @param entity         实体
     * @param nameFieldCache 缓存
     * @param <T>            泛型
     * @return 对象数据
     */
    public static <T> Object[] buildUpdateSqlAndBindArgs(String table, T entity, Map<String, Field> nameFieldCache) {
        ContentValues initialValues = makeContentValues(entity, nameFieldCache);
        StringBuilder sql = new StringBuilder();
        Object[] bindArgs = null;
        sql.append("UPDATE");
        sql.append(" SET ");
        sql.append(table);
        sql.append('(');
        synchronized (table) {
            int size = initialValues != null ? initialValues.size() : 0;
            if (size > 0) {
                bindArgs = new Object[size];
                int i = 0;
                for (String colName : initialValues.keySet()) {
                    sql.append((i > 0) ? "," : "");
                    sql.append(colName);
                    bindArgs[i++] = initialValues.get(colName);
                }
                sql.append(')');
                sql.append(" VALUES (");
                for (i = 0; i < size; i++) {
                    sql.append((i > 0) ? ",?" : "?");
                }
            } else {
                sql.append(") VALUES (NULL");
            }
            sql.append(')');
        }
        return new Object[]{sql.toString(), bindArgs};
    }

    /**
     * bindValues
     *
     * @param stmt     数据库DatabaseStatement对象
     * @param bindArgs 参数
     */
    public static void bindValues(DatabaseStatement stmt, Object[] bindArgs) {
        stmt.clearBindings();
        // 开始插入
        if (bindArgs == null || bindArgs.length == 0) {
            return;
        }
        for (int i = 1; i <= bindArgs.length; i++) {
            Object object = bindArgs[i - 1];
            if (object == null) {
                continue;
            }
            String fieldTypeName = object.getClass().getName();
            if (fieldTypeName.equals(String.class.getName())) {
                String value = (String) object;
                stmt.bindString(i, value);
            } else if (fieldTypeName.equals(Long.class.getName())) {
                long value = (long) object;
                stmt.bindLong(i, value);
            } else if (fieldTypeName.equals(Integer.class.getName())) {
                int value = (int) object;
                stmt.bindLong(i, value);
            } else if (fieldTypeName.equals(Double.class.getName())) {
                double value = (double) object;
                stmt.bindDouble(i, value);
            } else if (fieldTypeName.equals(Float.class.getName())) {
                float value = (float) object;
                stmt.bindDouble(i, value);
            } else if (fieldTypeName.equals(byte[].class.getName())) {
                byte[] value = (byte[]) object;
                stmt.bindBlob(i, value);
            } else if (fieldTypeName.equals(Boolean.class.getName())) {
                boolean bool = (boolean) object;
                stmt.bindLong(i, bool ? 1 : 0);
            }
        }
    }

    /**
     * 获取XSqlPrimaryKey注解的字段。
     *
     * @param cacheMap 缓存
     * @return NameField 列表
     */
    public static List<NameField> getPrimaryKey(Map<String, Field> cacheMap) {
        for (Map.Entry<String, Field> stringFieldEntry : cacheMap.entrySet()) {
            Field field = stringFieldEntry.getValue();
            String name = stringFieldEntry.getKey();
            XSqlPrimaryKey generatedId = field.getAnnotation(XSqlPrimaryKey.class);
            if (generatedId != null) {
                List<NameField> list = new ArrayList<>();
                list.add(new NameField(name, field));
                return list;
            }
        }
        return null;
    }

    /**
     * 获取SqlUnique注解的字段。
     *
     * @param cacheMap 缓存
     * @return NameField 列表
     */
    public static List<NameField> getUnique(Map<String, Field> cacheMap) {
        for (Map.Entry<String, Field> stringFieldEntry : cacheMap.entrySet()) {
            Field field = stringFieldEntry.getValue();
            String name = stringFieldEntry.getKey();
            XSqlUnique sqlUnique = field.getAnnotation(XSqlUnique.class);
            if (sqlUnique != null && sqlUnique.value()) {
                List<NameField> list = new ArrayList<>();
                list.add(new NameField(name, field));
                return list;
            }
        }
        return null;
    }


    /**
     * 获取Field 对应的 value
     *
     * @param entity 类型对象
     * @param field  Field对象
     * @param <T>    泛型
     * @return Value
     */
    public static <T> String getFieldValue(T entity, Field field) {
        String args = null;
        try {
            field.setAccessible(true);
            Object object = field.get(entity);
            if (object == null) {
                return null;
            }
            String fieldTypeName = field.getType().getName();
            if (fieldTypeName.equals(String.class.getName())) {
                args = (String) object;
            } else if (fieldTypeName.equals(Long.class.getName())) {
                args = String.valueOf((Long) object);
            } else if (fieldTypeName.equals(Integer.class.getName())) {
                args = String.valueOf((Integer) object);
            } else if (fieldTypeName.equals(Double.class.getName())) {
                args = String.valueOf((Double) object);
            } else if (fieldTypeName.equals(Float.class.getName())) {
                args = String.valueOf((Float) object);
            } else if (fieldTypeName.equals(byte[].class.getName())) {
                args = Arrays.toString((byte[]) object);
            } else if (fieldTypeName.equals(Boolean.class.getName())) {
                args = String.valueOf((Boolean) object ? 1 : 0);
            }
            return args;
        } catch (IllegalAccessException e) {
            return null;
        }
    }

}
