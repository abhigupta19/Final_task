package com.todolist.slymp.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.todolist.slymp.todolist.db.TaskContract;
import com.todolist.slymp.todolist.db.TaskDbHelper;

import java.text.ParseException;
import java.util.ArrayList;

public class MyAdapter extends ArrayAdapter<Item> {

    private final String            TAG = "MyAdapter";
    private final Context           context;
    private final ArrayList<Item>   itemsArrayList;
    private       Activity          ListRef;
    private       TaskDbHelper      mHelper;


    public MyAdapter(Context context, ArrayList<Item> itemsArrayList) {

        super(context, R.layout.item, itemsArrayList);

        this.context = context;
        this.itemsArrayList = itemsArrayList;
        mHelper = new TaskDbHelper(this.context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.item, parent, false);

        final TextView titleView = (TextView) itemView.findViewById(R.id.item_title);
        final ImageView reverse =  itemView.findViewById(R.id.reverse);

        final ImageView submit =  itemView.findViewById(R.id.submit);

        titleView.setText(itemsArrayList.get(position).getTitle());

        if(itemsArrayList.get(position).getStatus().equals("true"))
        {
            titleView.setPaintFlags(titleView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            submit.setImageResource(R.drawable.ic_delete_black_24dp);
            reverse.setVisibility(View.VISIBLE);
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("pppj",itemsArrayList.get(position).getStatus());

                if( itemsArrayList.get(position).getStatus().equals("true"))
                {
                    Log.i("pppj","ll");

                    delete(itemsArrayList.get(position).getId(), position,context,itemsArrayList);
                }
                else
                {
                    itemsArrayList.get(position).setStatus("true");
                    submit.setImageResource(R.drawable.ic_delete_black_24dp);
                        reverse.setVisibility(View.VISIBLE);
                    update(itemsArrayList.get(position).getId(),itemsArrayList.get(position).getTitle(),titleView);
                }
            }
        });
        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemsArrayList.get(position).setStatus("false");
                update2(itemsArrayList.get(position).getId(),reverse,submit,titleView);

            }
        });


//        itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                submit.setImageResource(R.drawable.ic_delete_black_24dp);
//                reverse.setVisibility(View.VISIBLE);
//                update(itemsArrayList.get(position).getId(),itemsArrayList.get(position).getTitle(),titleView);
//
//
//
//            }
//        });


        return itemView;
    }

    private void update2(int id, ImageView reverse, ImageView submit,TextView titleView) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(TaskContract.TaskEntry.COL_TASK_STATUS, "false");
        reverse.setVisibility(View.INVISIBLE);
        submit.setImageResource(R.drawable.ic_done_black_24dp);

        titleView.setPaintFlags(titleView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));


        db.update(TaskContract.TaskEntry.TABLE, values, "_id="+id , null);
        db.close();
    }

    void update(int position,String a,TextView titleView)
    {
                        SQLiteDatabase db = mHelper.getWritableDatabase();
                ContentValues values = new ContentValues();

                values.put(TaskContract.TaskEntry.COL_TASK_TITLE,a);
                values.put(TaskContract.TaskEntry.COL_TASK_STATUS, "true");

                 titleView.setPaintFlags(titleView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


        db.update(TaskContract.TaskEntry.TABLE, values, "_id="+position , null);
                db.close();


    }
    void delete(int position,int p,Context context,ArrayList<Item> a )
    {

        SQLiteDatabase db = mHelper.getWritableDatabase();


        db.delete(TaskContract.TaskEntry.TABLE, "_id="+position , null);
        db.close();
        getContext().startActivity(new Intent(getContext(),ListActivity.class));



    }
}