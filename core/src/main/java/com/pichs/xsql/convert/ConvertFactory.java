package com.pichs.xsql.convert;

import com.pichs.xsql.model.SqlColumnType;

import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

public class ConvertFactory {

    private ConvertFactory() {
    }

    /**
     * 获取Convert
     *
     * @param fieldClass Field的Class对象
     * @return BaseConverter {@link BaseConverter}
     * 不支持char类型
     * @throws SQLException 数据库异常
     */
    public static BaseConverter getConverter(Class fieldClass) throws SQLException {
        BaseConverter result = null;
        if (mConvertMap.containsKey(fieldClass.getName())) {
            result = mConvertMap.get(fieldClass.getName());
        }
        if (result == null) {
            throw new SQLException("SQL unsupported type : " + fieldClass.getName());
        }
        return result;
    }

    /**
     * 获取在数据库中的 字段属性类型（integer,text,blob,byte[]）
     *
     * @param fieldClass 成员变量类型
     * @return SqlColumnType对象
     * @throws SQLException 数据库异常
     */
    public SqlColumnType getSqlColumnType(Class fieldClass) throws SQLException {
        return getConverter(fieldClass).getSqlColumnType();
    }

    // 初始化map数据
    public static final ConcurrentHashMap<String, BaseConverter> mConvertMap;

    /**
     * 定义类型只支持以下参数。
     * 只支持装箱后的对象，不要使用基础类型，
     * 如：int，long等，
     * 因为面对对象数据库只支持对象，
     * 基础类型 会生成默认值。跟对象特性相违背，对象初始值应为 null,
     * 强大之处在使用时才能体会到。
     * String 字符串
     * Boolean 布尔
     * Integer 整数
     * Long 整数
     * Double 浮点
     * Float 浮点
     * byte[] 二进制
     */
    static {
        mConvertMap = new ConcurrentHashMap<>();
        // 字符串
        StringConverter stringConverter = new StringConverter();
        mConvertMap.put(String.class.getName(), stringConverter);

        // 布尔
        BooleanConverter booleanConverter = new BooleanConverter();
        mConvertMap.put(Boolean.class.getName(), booleanConverter);

        // 整数
        IntegerConverter integerConverter = new IntegerConverter();
        mConvertMap.put(Integer.class.getName(), integerConverter);
        mConvertMap.put(Long.class.getName(), integerConverter);

        // 浮点
        DoubleConverter doubleConverter = new DoubleConverter();
        mConvertMap.put(Double.class.getName(), doubleConverter);
        mConvertMap.put(Float.class.getName(), doubleConverter);

        // 二进制
        ByteArrayConverter byteArrayConverter = new ByteArrayConverter();
        mConvertMap.put(byte[].class.getName(), byteArrayConverter);
    }


}
