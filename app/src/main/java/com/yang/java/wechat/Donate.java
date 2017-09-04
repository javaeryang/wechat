package com.yang.java.wechat;

import android.content.ClipboardManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Donate extends AppCompatActivity implements View.OnClickListener{

    private Button copy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);
        copy= (Button) findViewById(R.id.copy);
        copy.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.copy:
                ClipboardManager cm= (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                cm.setText("yy960076413");
                Toast.makeText(Donate.this,"复制成功",Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
