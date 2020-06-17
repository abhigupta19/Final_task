package com.todolist.slymp.todolist;

import android.graphics.Color;
import android.util.Log;

import java.util.Date;

import static android.content.ContentValues.TAG;
import static android.graphics.Color.rgb;

public class Item {

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String title;

    private String status;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Item(String title ,int id, String status) {
            super();
            this.title = title;
            this.status=status;
            this.id=id;

    }
}
