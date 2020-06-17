package com.todolist.slymp.todolist;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.todolist.slymp.todolist.db.TaskContract;
import com.todolist.slymp.todolist.db.TaskDbHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class ListActivity extends AppCompatActivity {

    private static final String     TAG = "ListActivity";
    private TaskDbHelper            mHelper;
    private ListView                mTaskListView;
    private MyAdapter               mAdapter;
    private boolean                 isAlphabetical;
    private boolean                 isFiltered;
    private String                  searchText;
    private EditText                searchBar;
    private View                    thief;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        thief = (View) findViewById(R.id.focus_thief);
        thief.clearFocus();

        isAlphabetical = true;
        isFiltered = false;
        searchText = "";
        TimeZone.setDefault(TimeZone.getTimeZone("Paris"));
        mHelper = new TaskDbHelper(this);

        mTaskListView = (ListView) findViewById(R.id.task_list);
        searchBar = (EditText) findViewById(R.id.search_bar);

        try {
            updateList();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            updateList();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                isFiltered = (searchBar.getText().length() > 0) ? true : false;
                searchText = searchBar.getText().toString();

                if (isFiltered == false)
                    thief.clearFocus();

                try {
                    updateList();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        };

        searchBar.addTextChangedListener(tw);
    }

    private ArrayList<Item> generateData() throws ParseException {
        ArrayList<Item> items = new ArrayList<Item>();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        String query = "SELECT * FROM " + TaskContract.TaskEntry.TABLE;

        if (isFiltered) {
            query += " WHERE title LIKE '%" + searchText + "%'";
        }

        query += " ORDER BY status DESC;";


        Log.d(TAG, "Query: " +query);

        Cursor cursor = db.rawQuery(query, null);
        renderList(cursor, items);

        cursor.close();
        db.close();
        return items;
    }

    void renderList(Cursor cursor, ArrayList<Item> items) throws ParseException {

        while(cursor.moveToNext()) {
            Item _item = new Item(cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_TITLE)),cursor.getInt(cursor.getColumnIndex(TaskContract.TaskEntry._ID)),
                    cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_STATUS)));



            items.add(_item);
        }
    }



    protected void updateList() throws ParseException {

        mAdapter = new MyAdapter(this, generateData());
        mTaskListView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_task:
                final AlertDialog.Builder alertbox=new AlertDialog.Builder(ListActivity.this);
                View view=getLayoutInflater().inflate(R.layout.alert_box,null,true);
                final EditText editText=view.findViewById(R.id.edittext);
                alertbox.setView(view);
                Button button=view.findViewById(R.id.button_alert);
                alertbox.show();

                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            SQLiteDatabase db = mHelper.getWritableDatabase();
                            ContentValues values = new ContentValues();

                            String textTitle = String.valueOf(editText.getText());
                            values.put(TaskContract.TaskEntry.COL_TASK_TITLE, textTitle);
                            values.put(TaskContract.TaskEntry.COL_TASK_STATUS, "false");
                            db.insertWithOnConflict(TaskContract.TaskEntry.TABLE,
                                    null,
                                    values,
                                    SQLiteDatabase.CONFLICT_REPLACE);
                            db.close();
                            mAdapter.notifyDataSetChanged();
                            startActivity(new Intent(ListActivity.this, ListActivity.class));
                            finish();


                        }
                    });



//                Intent intent = new Intent(ListActivity.this, CreateActivity.class);
//                startActivity(intent);
                return true;

                // TODO : Implement swap order (alphabetical <-> due-time)
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public long getStartOfDayInMillis(String date) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(format.parse(date));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis() / 1000;
    }
    
    public long getEndOfDayInMillis(String date) throws ParseException {
        // Add one day's time to the beginning of the day.
        // 24 hours * 60 minutes * 60 seconds * 1000 milliseconds = 1 day
        return getStartOfDayInMillis(date) + (24 * 60 * 60);
    }
}


