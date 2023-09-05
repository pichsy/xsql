package com.pichs.xsql.utils;

import android.database.Cursor;

import com.pichs.xsql.base.Database;
import java.util.ArrayList;
import java.util.List;

public class TableUtil {

    /**
     * 获取数据库的所有的字段
     * @param db 数据库对象
     * @param tableName 数据库表名。
     * @return 字段集合
     */
    public static List<String> getTableColumns(Database db, String tableName) {
        XSqlLog.d("TableUtil", ":: getTableColumns");
        String sql = "Pragma table_info(" + tableName + ")";
        Cursor cursor = db.rawQuery(sql, null);
        try {
            List<String> columnsList = new ArrayList<>();
            while (cursor.moveToNext()) {
                String column = cursor.getString(1);
                if (column != null) {
                    XSqlLog.d("TableUtil", "getTableColumns： column=" + column);
                    columnsList.add(column);
                }
            }
            return columnsList;
        } catch (Exception e) {
            return null;
        } finally {
            cursor.close();
        }
    }
}
