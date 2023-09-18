# XSql数据库框架

极致简单好用的ORM数据库框架<br>

可自动升级，无需操心数据库升级带来的困扰，面对对像数据库操作


最新版本：[![](https://img.shields.io/maven-central/v/io.github.pichsy.xsql/core)](https://img.shields.io/maven-central/v/io.github.pichsy.xsql/core)

### 引用方式

        // 核心包 必须
         api "io.github.pichsy.xsql:base:2.0.0"
         api "io.github.pichsy.xsql:core:2.0.0"
         // 非必须，建议引用，条件查询会非常方便
         kapt "io.github.pichsy.xsql:compiler:2.0.0"


### 一、 一行代码，无需创建乱七八槽的 dao

    var baseDao = XSql.getDBManager(this).getBaseDao(UserInfo::class.java)
    // 获取baseDao 举个栗子。
    baseDao.insert(UserInfo)
    // 具体使用可以查看详情。

### 二、 字段随意定，支持模糊，区间，分页，排序等常用查询。

##### XSqlTable: 定义表名的注解，不可为空

##### 1、XSqlField:

- 定义字段名字的注解，不可为空，该字段必须为包装类。
- 不添加此注解的字段不添加到数据库。字段类型可随意。
- 仅支持以下7种，Integer，Long，Double，Float，Boolean, String，byte[]
- 不支持 Short，Byte，Date,这几个类型都可以用上面7种类型代替。
- 其他类型请使用String，然后自己转换。
- 本人以为这7种足以应对各种表，越简单，越好用。

##### 2、XSqlPrimaryKey: 定义自增键的注解，必须为Long或Integer。与XSqlField一起用

##### 3、XSqlUnique: 定义唯一值，与XSqlField一起用。

### Table实体类的定义（java）

```java

@XSqlTable("user_info")
public class UserInfo {

    @XSqlField("name")
    public String name;

    @XSqlField("age")
    public Integer age;

    public UserInfo() {
    }

    public UserInfo(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return "{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}


```

#### 注意：kotlin的写法，Kotlin的基础类型一定要带 "?" 否则会报错。

- 问：为什么基础类型上要带 ? 号, 因为kotlin的带问号的基础类型会转成 包装类，不带问号的基础类型会转成基础类型。
- 答：这个框架 做了基础类型(int,long,boolean,double,float)限制，不允许使用基础类型，请使用包装类型，否则会报错。
- 问：为什么一定要使用包装类型?
- 答：因为可以判断为null时不写入数据库或者，不作为条件查询，或者更新，而基础类型不可为null，无法实现（强行判断为n会导致n不能被写入），所以不允许使用基础类型。

### Table实体类的定义（kotin）

```kotlin

@XSqlTable(value = "user_info")
data class UserInfo(
    @XSqlUnique
    @XSqlField(value = "name")
    var name: String? = "",
    @XSqlField(value = "age")
    var age: Int? = 0,
    // 没注解就是忽略的字。
    var tag: String? = ""
)

```

## 增删改查，一行搞定。

- 增删改查，是重中之重，所以详细介绍使用方式和规则。

### 增

1. insert(T entity): 添加一条数据
   规则限制: 优先以XSqlPrimaryKey查询，其次XSqlUnique, 都没有
   就以其他结果合并查询。


2. insertInTx(List<T> entities)：批量添加数据

### 删

1. delete(T entity): 删除匹配相同的数据 <br/>
   (规则限制，对象内所有属性为空时不删除任何数据，保证一定的安全性。)


2. delete(Where where): 根据条件删除<br/>
   (规则限制条件为空时不删除任何数据)


3. deleteAll(): 删除所有数据

### 改

1. update(T entity): 更新数据：
   （规则限制：必须满足有 唯一建或者自增id且不为null 的情况下有效。）
   因为没有唯一建，无法知晓到底根据哪个字段更新更新哪些字段的数据。
   全排列查询效率低下，此种情况建议使用条件更新 （2.）


2. update(Where where, T entity): 条件更新
   （规则限制，where不可为null且条件必须有个一判断。entity中必须至少有一个字段的值不为null）

### 查

1. query(T entity): 根据entity查询数据
   （规则限制：entity中必须至少有一个字段的值不为null）
   规则优先级: 优先以XSqlPrimaryKey查询，其次XSqlUnique,
   都没有就以其他结果合并查询。


2. query(Where where): 条件查询
   如果普通的查询无法满足你的需求，那么就是用条件查询吧，各种花里胡哨的条件查询都有。


3. queryAll(): 查询所有

### 条件查询 Where详解

1. 条件查询代码示例

```kotlin
 Where.Builder()
    .eq(XSqlProperties.UserInfoData.name, "张三")
    .or()// 条件拼接 二选一
    // 或者
    .and()// 条件拼接 二选一
    .eq(XSqlProperties.UserInfoData.name, "李四")
    .orderByDesc(XSqlProperties.UserInfoData.age)
    .orderByAsc(XSqlProperties.UserInfoData.age)
    .like(XSqlProperties.UserInfoData.name, "张")
    .notLike(XSqlProperties.UserInfoData.name, "张")
    .notEq(XSqlProperties.UserInfoData.name, "四")
    .between(XSqlProperties.UserInfoData.age, 10, 20)
    .ge(XSqlProperties.UserInfoData.age, 10)
    .gt(XSqlProperties.UserInfoData.age, 10)
    .le(XSqlProperties.UserInfoData.age, 10)
    .lt(XSqlProperties.UserInfoData.age, 10)
    .startWith(XSqlProperties.UserInfoData.name, "张")
    .endWith(XSqlProperties.UserInfoData.name, "张")
    .page(1, 10)
    .build()

```

2. Where.Builder() 方法详解

| 方法                            | 解释                   |
|-------------------------------|----------------------|
| eq(column, value)             | 该列等于给定值              |
| or()                          | 逻辑或操作，连接两个条件其中之一满足即可 |
| and()                         | 逻辑与操作，连接两个条件都要满足     |
| notEq(column, value)          | 该列不等于给定值             |
| like(column, value)           | 该列的值包含给定值（模糊匹配）      |
| notLike(column, value)        | 该列的值不包含给定值（模糊匹配）     |
| between(column, lower, upper) | 该列的值在给定的范围内          |
| ge(column, value)             | 该列的值大于或等于给定值         |
| gt(column, value)             | 该列的值大于给定值            |
| le(column, value)             | 该列的值小于或等于给定值         |
| lt(column, value)             | 该列的值小于给定值            |
| startWith(column, value)      | 该列的值以给定值开头           |
| endWith(column, value)        | 该列的值以给定值结尾           |
| orderByDesc(column)           | 根据该列降序排列             |
| orderByAsc(column)            | 根据该列升序排列             |
| page(pageNumber, pageSize)    | 查询结果分页，指定页码和每页记录数    |
| build()                       | 生成查询条件对象，用于执行查询      |

#### 以上就是条件查询的全部方法，非常的简单


### 条件查询示例
    
```kotlin

    // 查询年龄大于10岁的数据
    var where = Where.Builder()
        .gt(XSqlProperties.UserInfoData.age, 10)
        .build()
    var list = baseDao.query(where)
    
    // 查询年龄大于10岁的数据，按照年龄降序排列
    where = Where.Builder()
        .gt(XSqlProperties.UserInfoData.age, 10)
        .orderByDesc(XSqlProperties.UserInfoData.age)
        .build()
    
    // 查询名字为张三的数据
    where = Where.Builder()
        .eq(XSqlProperties.UserInfoData.name, "张三")
        .build()
    val list = baseDao.query(where)
    
    // 查询名字为张三或者李四的数据
    where = Where.Builder()
        .eq(XSqlProperties.UserInfoData.name, "张三")
        .or()
        .eq(XSqlProperties.UserInfoData.name, "李四")
        .build()
    val list = baseDao.query(where)
    
    // 查询名字为张三，且年龄为18岁的数据
    where = Where.Builder()
        .eq(XSqlProperties.UserInfoData.name, "张三")
        .and()
        .eq(XSqlProperties.UserInfoData.age, 18)
        .build()
    val list = baseDao.query(where)
    
    // 分页查询, page从1开始
    val page = 1 // page=2,3,4,5,6,7,8,9,10
    where = Where.Builder()
        .page(page, 10)
        .build()
    val list = baseDao.query(where)
    
    // 查询名字以张开头的数据
    where = Where.Builder()
        .startWith(XSqlProperties.UserInfoData.name, "张")
        .build()
    
    // 查询名字以张结尾的数据
    where = Where.Builder()
        .endWith(XSqlProperties.UserInfoData.name, "张")
        .build()
    
    // 查询名字包含张的数据
    where = Where.Builder()
        .like(XSqlProperties.UserInfoData.name, "张")
        .build()
    
    // 查询名字不包含张的数据
    where = Where.Builder()
        .notLike(XSqlProperties.UserInfoData.name, "张")
        .build()
    
    // 查询年龄在10到20之间的数据
    where = Where.Builder()
        .between(XSqlProperties.UserInfoData.age, 10, 20)
        .build()
    
    // 查询年龄大于等于10岁的数据
    where = Where.Builder()
        .ge(XSqlProperties.UserInfoData.age, 10)
        .build()
    
    // 查询年龄大于10岁的数据
    
    where = Where.Builder()
        .gt(XSqlProperties.UserInfoData.age, 10)
        .build()
    
    // 查询年龄小于等于10岁的数据
    where = Where.Builder()
        .le(XSqlProperties.UserInfoData.age, 10)
        .build()
    
    // 查询年龄小于10岁的数据
    where = Where.Builder()
        .lt(XSqlProperties.UserInfoData.age, 10)
        .build()
    
    // 查询所有数据 按照年龄降序排列
    where = Where.Builder()
        .orderByDesc(XSqlProperties.UserInfoData.age)
        .build()

```

### 数据库升级

1. 该框架不需要数据库升级，会自动管理升级，无需操心。
2. 你只需要新增实体类的字段，或者删除实体类的字段即可
3. 注解的话，直接删除注解即可。

   

