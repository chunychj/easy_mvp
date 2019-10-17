package com.cc.design.aac.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * @类描述：
 * @作者： zhenglecheng
 * @创建时间： 2019/10/16 11:18
 */
@Entity(tableName = "user")
public class User {
    @PrimaryKey(autoGenerate = true)
    public Integer id = 0;
    @ColumnInfo(name = "user_name")
    public String userName;
}
