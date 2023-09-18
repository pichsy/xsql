package com.pichs.xsql.dao;

import com.pichs.xsql.where.Where;

import java.util.List;

/**
 * 数据库操作接口
 *
 * @param <T> 实体类
 */
public interface IBaseDao<T> {
    /**
     * 插入数据
     *
     * @param entity 实体类
     * @return 是否创建成功
     */
    long insert(T entity);

    /**
     * 插入数据 批量
     *
     * @param entities 实体类
     * @return 是否创建成功
     */
    long insertInTx(List<T> entities);

    /**
     * 更行数据
     *
     * @param entity 实体类
     * @return 是否创建成功
     */
    int update(T entity);

    /**
     * 更行数据 批量
     *
     * @param where 条件
     * @param entity 实体类
     * @return 是否创建成功
     */
    int update(Where where, T entity);

    /**
     * 删除数据
     *
     * @param entity 实体类
     * @return 是否创建成功
     */
    int delete(T entity);

    /**
     * 删除数据 批量
     *
     * @param where 条件
     * @return 是否创建成功
     */
    int delete(Where where);

    /**
     * 删除所有数据
     *
     * @return 是否创建成功
     */
    int deleteAll();

    /**
     * 查询数据
     *
     * @param entry 实体类
     * @return 是否创建成功
     */
    List<T> query(T entry);

    /**
     * 查询数据
     *
     * @param where 条件
     * @return 是否创建成功
     */
    List<T> query(Where where);

    /**
     * 查询所有数据
     *
     * @return 是否创建成功
     */
    List<T> queryAll();

    /**
     * 查询所有数据
     *
     * @return 是否创建成功
     */
    List<T> queryLastOne();

    /**
     * 开启事务
     */
    void beginTransaction();

    /**
     * 结束事务
     */
    void endTransaction();

    /**
     * 事务中
     */
    void inTransaction();

    /**
     * 事务成功
     */
    void setTransactionSuccessful();

}
