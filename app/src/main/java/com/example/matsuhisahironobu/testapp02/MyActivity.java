package com.example.matsuhisahironobu.testapp02;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;

import java.util.List;

public class MyActivity extends ListActivity {
    // DB の設定
    public static final String TABLE_NAME   = "memo_data";
    public static final String COLUMN_ID    = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_BODY  = "body";
    public static final String SQL_WHERE_ID = COLUMN_ID + " = ?";

    public static final String EXTRA_ID    = "id";
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_BODY  = "body";
    private static final int REQUEST_ADD   = 1;
    private static final int REQUEST_EDIT  = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        //
        MemoDBOpenHelper helper = new MemoDBOpenHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();

        // クエリー発行
        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null);
        String[] from = new String[] {COLUMN_TITLE, COLUMN_BODY};
        int[] to = new int[] {android.R.id.text1, android.R.id.text2};

        // ListView に表示する
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, c,from,to, 0);
        setListAdapter(adapter);
    }

    // アクションバーの生成
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    // メニューの機能
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        boolean result;
        MemoDBOpenHelper helper;
        SQLiteDatabase db;

        switch (item.getItemId()) {
            case R.id.operate_additem:
            //
            helper = new MemoDBOpenHelper(this);
            db = helper.getWritableDatabase();

            // データ追加
            ContentValues values = new ContentValues();
            values.put("title", "行の追加");
            values.put("body", "サンプル");
            db.insert("memo_data", null, values);
            db.close();

            reloadCursor();
            result = true;
            break;

            case R.id.operate_deleteitem:
                SimpleCursorAdapter adapter = (SimpleCursorAdapter) getListAdapter();
                if(adapter.getCount() > 0)
                {
                    long id = adapter.getItemId(adapter.getCount() - 1);

                    helper = new MemoDBOpenHelper(this);
                    db = helper.getWritableDatabase();

                    db.delete("memo_data", "_id = ?", new String[]{ Long.toString(id) });
                    reloadCursor();
                }

                result = true;
                break;

            default:
                result = true;
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void reloadCursor()
    {
        MemoDBOpenHelper helper = new MemoDBOpenHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor c = db.query("memo_data", null, null, null, null, null, null);

        SimpleCursorAdapter adapter = (SimpleCursorAdapter) getListAdapter();
        adapter.swapCursor(c);
    }
}
