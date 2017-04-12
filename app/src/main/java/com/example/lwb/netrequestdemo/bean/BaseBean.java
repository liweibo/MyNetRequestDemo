package com.example.lwb.netrequestdemo.bean;

import java.io.Serializable;

/**
 * Created by lwb on 2017/4/12.
 */

public class BaseBean implements Serializable {
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
