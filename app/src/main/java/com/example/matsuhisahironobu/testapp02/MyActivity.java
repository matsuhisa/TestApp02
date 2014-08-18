package com.example.matsuhisahironobu.testapp02;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MyActivity extends ListActivity
{
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode == RESULT_OK)
        {
            MemoDBOpenHelper helper = new MemoDBOpenHelper(this);
            SQLiteDatabase db = helper.getWritableDatabase();

            // 追加するデーターを用意する
            ContentValues values = new ContentValues();
            values.put("title", data.getStringExtra("title"));
            values.put("body", data.getStringExtra("body"));

            switch (requestCode)
            {
                case 1:
                    db.insert("memo_data", null, values);
                    break;
                case 2:
                    long id = data.getLongExtra("id", 0);
                    String[] whereargs = new String[]{Long.toString(id)};
                    db.update("memo_data", values, "_id = ?", whereargs);
                    break;
                default:
                    break;
            }
            reloadCursor();
        }
        super.onActivityResult(requestCode, resultCode, data);
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

                Intent intent = new Intent();
                intent.setClass(getApplicationContext(),EditorActivity.class);

                startActivityForResult(intent, 1);
                result = true;

/*
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
*/
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

    /**
     *
     * リストビュークリック時の処理
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        // 読み込み用のDBの取得
        MemoDBOpenHelper helper = new MemoDBOpenHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();

        String[] colums = new String[]{"title", "body"};
        String[] whereargs = new String[]{Long.toString(id)};

        // 値の取得
        Cursor c = db.query(TABLE_NAME, colums, SQL_WHERE_ID, whereargs, null, null, null);
        c.moveToFirst();

        // 編集画面を表示する
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), EditorActivity.class);
        intent.putExtra("id", id);

        intent.putExtra("title", c.getString(0));
        intent.putExtra("body", c.getString(1));

        startActivityForResult(intent, 2);
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
