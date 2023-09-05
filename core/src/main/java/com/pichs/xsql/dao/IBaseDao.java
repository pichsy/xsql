package com.pichs.xsql.dao;

import com.pichs.xsql.where.Where;

import java.util.List;

public interface IBaseDao<T> {

    long insert(T entity);

    long insertInTx(List<T> entities);

    int update(T entity);

    int update(Where where, T entity);

    int delete(T entity);

    int delete(Where where);

    int deleteAll();

    List<T> query(T entry);

    List<T> query(Where where);

    List<T> queryAll();

    List<T> queryLastOne();

    void beginTransaction();

    void endTransaction();

    void inTransaction();

    void setTransactionSuccessful();

}
