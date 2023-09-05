package com.pichs.xsql.demo

import com.pichs.xsql.annotation.XSqlField
import com.pichs.xsql.annotation.XSqlTable
import com.pichs.xsql.annotation.XSqlUnique


@XSqlTable(value = "user_info")
data class UserInfo(
    @XSqlUnique
    @XSqlField(value = "name")
    var name: String? = "",

    @XSqlField(value = "age")
    var age: Int? = 0,

    var tag:String? = ""
){
    override fun toString(): String {
        return "{\"name\":\"$name\", \"age\":$age, \"tag\":\"$tag\"}"
    }
}
