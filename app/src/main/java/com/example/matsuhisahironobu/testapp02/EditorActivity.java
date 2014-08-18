package com.example.matsuhisahironobu.testapp02;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class EditorActivity extends Activity implements View.OnClickListener{
    private long editorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // イベントハンドラの設定
        Button operate_save = (Button) findViewById(R.id.operate_save);
        operate_save.setOnClickListener(this);

        // Extraが指定された場合、その設定を反映する
        Intent intent = getIntent();
        if(intent.hasExtra("id"))
        {
            EditText editor_title = (EditText) findViewById(R.id.editor_title);
            EditText editor_body  = (EditText) findViewById(R.id.editor_body);

            editorId = intent.getLongExtra("id", 0);

            editor_title.setText(intent.getStringExtra("title"));
            editor_body.setText(intent.getStringExtra("body"));
        }else{
            editorId = -1;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.operate_save:
                // 入力のするためのViewオブジェクトを用意する
                EditText editor_title = (EditText) findViewById(R.id.editor_title);
                EditText editor_body  = (EditText) findViewById(R.id.editor_body);

                // 結果を通知するインテントを作成する
                Intent result = new Intent();

                //
                if(editorId != -1)
                {
                    result.putExtra("id",editorId);
                }
                result.putExtra("title", editor_title.getText().toString());

                result.putExtra("body", editor_body.getText().toString());
                setResult(RESULT_OK, result);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
