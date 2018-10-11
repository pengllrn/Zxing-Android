package com.example.pengl.zxing_android;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.pengl.zxing_android.internet.OkHttp;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.RequestBody;


public class SecondActivity extends AppCompatActivity {
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case 0x2017:
                    String responseMes = (msg.obj).toString();
                    try {
                        JSONObject jobj=new JSONObject(responseMes);
                        String code=jobj.getString("code");
                        if(code.equals("10006"))
                            Toast.makeText(getApplicationContext(),"登录成功",Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getApplicationContext(),"登录失败："+code,Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    finish();
                    break;
                default:
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Button btn_sure = (Button) findViewById(R.id.btn_sure);
        Intent intent = getIntent();
        final String user = intent.getStringExtra("user");
        final String lgToken=intent.getStringExtra("lgToken");

        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OkHttp okHttp=new OkHttp(getApplicationContext(),mHandler);
                RequestBody requestBody=new FormBody.Builder().add("user",user).build();
                okHttp.postDataFromInternet(lgToken,requestBody);
            }
        });
    }
}
