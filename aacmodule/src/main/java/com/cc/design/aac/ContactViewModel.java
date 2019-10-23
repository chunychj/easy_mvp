package com.cc.design.aac;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @类描述：
 * @作者： zhenglecheng
 * @创建时间： 2019/10/23 18:30
 */
public class ContactViewModel extends ViewModel {

    private LiveData<List<String>> contacts;

    public LiveData getContacts() {
        if(contacts == null){
            contacts = new MutableLiveData<>();
        }
        return contacts;
    }
}
