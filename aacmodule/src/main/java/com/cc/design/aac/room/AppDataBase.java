package com.cc.design.aac.room;

import androidx.room.Database;

/**
 * @类描述：
 * @作者： zhenglecheng
 * @创建时间： 2019/10/16 11:14
 */
@Database(entities = {User.class},version = 1)
public abstract class AppDataBase implements Database {
    public abstract UserDao userDao();


}
