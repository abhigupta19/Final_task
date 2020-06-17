package com.todolist.slymp.todolist.db;

import android.provider.BaseColumns;

public class TaskContract {
    public static final String DB_NAME = "com.todolist.slymp.todolist.db";
    public static final int DB_VERSION = 8;

    public class TaskEntry implements BaseColumns {
        public static final String TABLE = "tasksDB";

        public static final String COL_TASK_TITLE = "title";
        public static final String COL_TASK_STATUS = "status";
    }
}