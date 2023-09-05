package com.pichs.xsql.where;

import android.text.TextUtils;

import com.pichs.xsql.utils.SqlCheck;
import com.pichs.xsql.utils.XSqlLog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 条件类
 */
public class Where {

    /**
     * 条件语句
     * xxx=? or xxx=? and xxx=?
     * 类似这种格式
     * 不含 WHERE 字符串
     */
    private String whereCause;

    /**
     * 条件查询的值集合，跟上面 '?' 1对1
     */
    private String[] args;

    /**
     * 排序
     */
    private String order;

    /**
     * 分页
     */
    private String limit;

    public Where() {
    }

    public Where(String whereCause, String[] args, String order, String limit) {
        this.whereCause = whereCause;
        this.args = args;
        this.order = order;
        this.limit = limit;
    }

    public String getOrder() {
        return order;
    }

    public Where setOrder(String order) {
        this.order = order;
        return this;
    }

    public String getLimit() {
        return limit;
    }

    public Where setLimit(String limit) {
        this.limit = limit;
        return this;
    }

    public String getWhereCause() {
        return whereCause;
    }

    public Where setWhereCause(String whereCause) {
        this.whereCause = whereCause;
        return this;
    }

    public String[] getArgs() {
        return args;
    }

    public Where setArgs(String[] args) {
        this.args = args;
        return this;
    }


    @Override
    public String toString() {
        return "{" +
                "whereCause='" + whereCause + '\'' +
                ", args=" + Arrays.toString(args) +
                ", order='" + order + '\'' +
                ", limit='" + limit + '\'' +
                '}';
    }

    /**
     * 将条件组合起来
     *
     * @param where          Where对象
     * @param startWithWhere 是否以  WHERE  开头
     *                       ：：如果条件允许的情况下，则会以where开头
     * @param limitEnable    是否支持分页 false 则不拼接，true 则拼接
     * @param orderEnable    是否支持排序 false 则不拼接，true 则拼接
     */
    public static String combineWhere(Where where, boolean startWithWhere, boolean limitEnable, boolean orderEnable) {
        XSqlLog.d(" Where: " + where);
        // 全是空的，直接返回空
        if (TextUtils.isEmpty(where.getWhereCause()) && TextUtils.isEmpty(where.getLimit()) && TextUtils.isEmpty(where.getOrder())) {
            return "";
        }
        // 0. SELECT * FROM tableName WHERE age=10 ORDER BY age DESC LIMIT 0,10
        final StringBuilder result = new StringBuilder();

        // 1.检查是否有添加WHERE的必要，满足条件则添加WHERE
        if (!TextUtils.isEmpty(where.getWhereCause()) && startWithWhere) {
            result.append(SqlCheck.WHERE).append(where.getWhereCause());
        }

        // 2.检查是否允许排序
        if (!TextUtils.isEmpty(where.getOrder()) && orderEnable) {
            result.append(SqlCheck.BLANK_SPACE)
                    .append(where.getOrder());
        }

        // 3.是否允许分页
        if (!TextUtils.isEmpty(where.getLimit()) && limitEnable) {
            result.append(SqlCheck.BLANK_SPACE)
                    .append(where.getLimit());
        }
        return result.toString();
    }

    /**
     * 条件查询
     * 对象再牛逼，也只能普通匹配查询，涵盖不了所有情况
     * 使用条件查询，可以进行查询排序，和分页。模糊查询等。
     * 建造类
     */
    public static class Builder {

        private final StringBuilder whereCauseBuilder = new StringBuilder();
        private String order;
        private String limit;
        private final List<String> argList = new ArrayList<>();

        /**
         * 等于
         *
         * @param column 字段 数据库字段
         * @param value  值
         * @return 此对象
         */
        public Builder eq(String column, String value) {
            whereCauseBuilder.append(SqlCheck.BLANK_SPACE)
                    .append("`")
                    .append(column)
                    .append("`")
                    .append("=?");
            argList.add(value);
            return this;
        }

        /**
         * 等于
         *
         * @param column 字段 数据库字段
         * @param value  值
         * @return 此对象
         */
        public Builder eq(String column, double value) {
            whereCauseBuilder.append(SqlCheck.BLANK_SPACE)
                    .append("`")
                    .append(column)
                    .append("`")
                    .append("=?");
            argList.add(value + "");
            return this;
        }

        /**
         * 等于
         *
         * @param column 字段 数据库字段
         * @param value  值
         * @return 此对象
         */
        public Builder eq(String column, long value) {
            whereCauseBuilder.append(SqlCheck.BLANK_SPACE)
                    .append("`")
                    .append(column)
                    .append("`")
                    .append("=?");
            argList.add(value + "");
            return this;
        }

        /**
         * 不等于
         *
         * @param column 字段 数据库字段
         * @param value  值
         * @return 此对象
         */
        public Builder notEq(String column, String value) {
            whereCauseBuilder.append(SqlCheck.BLANK_SPACE)
                    .append("`")
                    .append(column)
                    .append("`")
                    .append("<>?");
            argList.add(value + "");
            return this;
        }

        /**
         * 不等于
         *
         * @param column 字段 数据库字段
         * @param value  值
         * @return 此对象
         */
        public Builder notEq(String column, double value) {
            whereCauseBuilder.append(SqlCheck.BLANK_SPACE)
                    .append("`")
                    .append(column)
                    .append("`")
                    .append("<>?");
            argList.add(value + "");
            return this;
        }

        /**
         * 不等于
         *
         * @param column 字段 数据库字段
         * @param value  值
         * @return 此对象
         */
        public Builder notEq(String column, long value) {
            whereCauseBuilder.append(SqlCheck.BLANK_SPACE)
                    .append("`")
                    .append(column)
                    .append("`")
                    .append("<>?");
            argList.add(value + "");
            return this;
        }

        /**
         * 条件拼接 and
         *
         * @return 此对象
         */
        public Builder and() {
            whereCauseBuilder.append(" AND ");
            return this;
        }

        /**
         * 条件拼接 or
         *
         * @return 此对象
         */
        public Builder or() {
            whereCauseBuilder.append(" OR ");
            return this;
        }

        /**
         * 大于
         *
         * @param column 字段 数据库字段
         * @param value  值
         * @return 此对象
         */
        public Builder gt(String column, String value) {
            whereCauseBuilder.append(SqlCheck.BLANK_SPACE)
                    .append("`")
                    .append(column)
                    .append("`")
                    .append(">?");
            argList.add(value + "");
            return this;
        }

        /**
         * 大于
         *
         * @param column 字段 数据库字段
         * @param value  值
         * @return 此对象
         */
        public Builder gt(String column, double value) {
            whereCauseBuilder.append(SqlCheck.BLANK_SPACE)
                    .append("`")
                    .append(column)
                    .append("`")
                    .append(">?");
            argList.add(value + "");
            return this;
        }

        /**
         * 大于
         *
         * @param column 字段 数据库字段
         * @param value  值
         * @return 此对象
         */
        public Builder gt(String column, long value) {
            whereCauseBuilder.append(SqlCheck.BLANK_SPACE)
                    .append("`")
                    .append(column)
                    .append("`")
                    .append(">?");
            argList.add(value + "");
            return this;
        }

        /**
         * 小于
         *
         * @param column 字段 数据库字段
         * @param value  值
         * @return 此对象
         */
        public Builder lt(String column, String value) {
            whereCauseBuilder.append(SqlCheck.BLANK_SPACE)
                    .append("`")
                    .append(column)
                    .append("`")
                    .append("<?");
            argList.add(value + "");
            return this;
        }

        /**
         * 小于
         *
         * @param column 字段 数据库字段
         * @param value  值
         * @return 此对象
         */
        public Builder lt(String column, double value) {
            whereCauseBuilder.append(SqlCheck.BLANK_SPACE)
                    .append("`")
                    .append(column)
                    .append("`")
                    .append("<?");
            argList.add(value + "");
            return this;
        }

        /**
         * 小于
         *
         * @param column 字段 数据库字段
         * @param value  值
         * @return 此对象
         */
        public Builder lt(String column, long value) {
            whereCauseBuilder.append(SqlCheck.BLANK_SPACE)
                    .append("`")
                    .append(column)
                    .append("`")
                    .append("<?");
            argList.add(value + "");
            return this;
        }

        /**
         * 大于等于
         *
         * @param column 字段 数据库字段
         * @param value  值
         * @return 此对象
         */
        public Builder ge(String column, String value) {
            whereCauseBuilder.append(SqlCheck.BLANK_SPACE)
                    .append("`")
                    .append(column)
                    .append("`")
                    .append(">=?");
            argList.add(value + "");
            return this;
        }

        /**
         * 大于等于
         *
         * @param column 字段 数据库字段
         * @param value  值
         * @return 此对象
         */
        public Builder ge(String column, double value) {
            whereCauseBuilder.append(SqlCheck.BLANK_SPACE)
                    .append("`")
                    .append(column)
                    .append("`")
                    .append(">=?");
            argList.add(value + "");
            return this;
        }

        /**
         * 大于等于
         *
         * @param column 字段 数据库字段
         * @param value  值
         * @return 此对象
         */
        public Builder ge(String column, long value) {
            whereCauseBuilder.append(SqlCheck.BLANK_SPACE)
                    .append("`")
                    .append(column)
                    .append("`")
                    .append(">=?");
            argList.add(value + "");
            return this;
        }

        /**
         * 小于等于
         *
         * @param column 字段 数据库字段
         * @param value  值
         * @return 此对象
         */
        public Builder le(String column, String value) {
            whereCauseBuilder.append(SqlCheck.BLANK_SPACE)
                    .append("`")
                    .append(column)
                    .append("`")
                    .append("<=?");
            argList.add(value + "");
            return this;
        }

        /**
         * 小于等于
         *
         * @param column 字段 数据库字段
         * @param value  值
         * @return 此对象
         */
        public Builder le(String column, double value) {
            whereCauseBuilder.append(SqlCheck.BLANK_SPACE)
                    .append("`")
                    .append(column)
                    .append("`")
                    .append("<=?");
            argList.add(value + "");
            return this;
        }

        /**
         * 小于等于
         *
         * @param column 字段 数据库字段
         * @param value  值
         * @return 此对象
         */
        public Builder le(String column, long value) {
            whereCauseBuilder.append(SqlCheck.BLANK_SPACE)
                    .append("`")
                    .append(column)
                    .append("`")
                    .append("<=?");
            argList.add(value + "");
            return this;
        }

        /**
         * 模糊查询，包含值的数据
         *
         * @param column 字段 数据库字段
         * @param value  值
         * @return 此对象
         */
        public Builder like(String column, String value) {
            whereCauseBuilder.append(SqlCheck.BLANK_SPACE)
                    .append("`")
                    .append(column)
                    .append("`")
                    .append(" LIKE ")
                    .append("'%")
                    .append(value)
                    .append("%'");
            return this;
        }

        /**
         * 模糊查询，不包含值的数据
         *
         * @param column 字段 数据库字段
         * @param value  值
         * @return 此对象
         */
        public Builder notLike(String column, String value) {
            whereCauseBuilder.append(SqlCheck.BLANK_SPACE)
                    .append("`")
                    .append(column)
                    .append("`")
                    .append(" NOT LIKE ")
                    .append("'%")
                    .append(value)
                    .append("%'");
            return this;
        }

        /**
         * 模糊查询，以值开头的数据
         *
         * @param column 字段 数据库字段
         * @param value  值
         * @return 此对象
         */
        public Builder startWith(String column, String value) {
            whereCauseBuilder.append(SqlCheck.BLANK_SPACE)
                    .append("`")
                    .append(column)
                    .append("`")
                    .append(" LIKE '")
                    .append(value)
                    .append("%'");
            return this;
        }

        /**
         * 模糊查询，以值结尾的数据
         *
         * @param column 字段 数据库字段
         * @param value  值
         * @return 此对象
         */
        public Builder endWith(String column, String value) {
            whereCauseBuilder.append(SqlCheck.BLANK_SPACE)
                    .append("`")
                    .append(column)
                    .append("`")
                    .append(" LIKE '%")
                    .append(value)
                    .append("'");
            return this;
        }

        /**
         * 查询Between and，值1和值2之间的数据
         *
         * @param column 字段 数据库字段
         * @param value1 值1
         * @param value2 值2
         * @return 此对象
         */
        public Builder between(String column, String value1, String value2) {
            whereCauseBuilder.append(SqlCheck.BLANK_SPACE)
                    .append("`")
                    .append(column)
                    .append("`")
                    .append(" BETWEEN ")
                    .append("'")
                    .append("?")
                    .append("' AND ")
                    .append("'")
                    .append("?")
                    .append("'");
            argList.add(value1 + "");
            argList.add(value2 + "");
            return this;
        }

        /**
         * 查询Between and，值1和值2之间的数据
         *
         * @param column 字段 数据库字段
         * @param value1 值1
         * @param value2 值2
         * @return 此对象
         */
        public Builder between(String column, double value1, double value2) {
            whereCauseBuilder.append(SqlCheck.BLANK_SPACE)
                    .append("`")
                    .append(column)
                    .append("`")
                    .append(" BETWEEN ")
                    .append("?")
                    .append(" AND ")
                    .append("?");
            argList.add(value1 + "");
            argList.add(value2 + "");
            return this;
        }

        /**
         * 查询Between and，值1和值2之间的数据
         *
         * @param column 字段 数据库字段
         * @param value1 值1
         * @param value2 值2
         * @return 此对象
         */
        public Builder between(String column, long value1, long value2) {
            whereCauseBuilder.append(SqlCheck.BLANK_SPACE)
                    .append("`")
                    .append(column)
                    .append("`")
                    .append(" BETWEEN ")
                    .append("?")
                    .append(" AND ")
                    .append("?");
            argList.add(value1 + "");
            argList.add(value2 + "");
            return this;
        }

        /**
         * 分页，和where语句同用时，必须放在where语句之后
         *
         * @param page page ：从0开始
         * @param size size ：从1开始
         * @return 此对象
         */
        public Builder page(int page, int size) {
            if (page < 0) {
                page = 0;
            }
            if (size < 1) {
                size = 1;
            }
            limit = " LIMIT " + page * size + ", " + size;
            return this;
        }

        /**
         * 逆序排序，从大到小
         *
         * @param column 字段 数据库字段
         * @return 此对象
         */
        public Builder orderByDesc(String... column) {
            if (column == null || column.length == 0) {
                order = null;
                return this;
            }
            StringBuilder sb = new StringBuilder(" ORDER BY ");
            for (String c : column) {
                sb.append(" `").append(c).append("`,");
            }
            sb.deleteCharAt(sb.lastIndexOf(","));
            sb.append(" DESC ");
            order = sb.toString();
            return this;
        }

        /**
         * 正序排序，从小到大
         *
         * @param column 字段 数据库字段
         * @return 此对象
         */
        public Builder orderByAsc(String... column) {
            if (column == null || column.length == 0) {
                order = null;
                return this;
            }
            StringBuilder sb = new StringBuilder(" ORDER BY ");
            for (String c : column) {
                sb.append(" `").append(c).append("`,");
            }
            sb.deleteCharAt(sb.lastIndexOf(","));
            sb.append(" ASC ");
            order = sb.toString();
            return this;
        }

        /**
         * toString
         *
         * @return 拼接后的结果
         */
        public Where build() {
            Where wh = new Where();
            wh.setWhereCause(whereCauseBuilder.toString());
            wh.setArgs(argList.toArray(new String[argList.size()]));
            wh.setLimit(limit);
            wh.setOrder(order);
            return wh;
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "wherebuilder=" + whereCauseBuilder +
                    ", argList=" + argList != null ? Arrays.toString(argList.toArray(new String[argList.size()])) : "[]" +
                    '}';
        }
    }
}
