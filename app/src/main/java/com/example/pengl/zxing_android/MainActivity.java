package com.example.pengl.zxing_android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity {
    private static final long VIBRATE_DURATION = 200L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn_scan_qr = (Button) findViewById(R.id.scan_qr);
        btn_scan_qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new IntentIntegrator(MainActivity.this)
                        //.setCaptureActivity(MyCaptureActivity.class)     //设置自定义的CaptureActivity，在这里自定义扫描布局
                        .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)// 扫码的类型,可选：一维码，二维码，一/二维码
                        .setPrompt("请将二维码放入取景框\n")// 设置提示语
                        .setCameraId(0)// 选择摄像头,可使用前置或者后置
                        .setBeepEnabled(false)// 是否开启声音,扫完码之后会"哔"的一声
                        .setBarcodeImageEnabled(false)// 扫完码之后生成二维码的图片,保存扫描后的图片
                        .initiateScan();// 初始化扫码
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(this,SecondActivity.class);
                intent.putExtra("user","pengllrn");
                intent.putExtra("lgToken",result.getContents());
                startActivity(intent);
                //需要震动权限
                Vibrator vibrator = (Vibrator) this
                        .getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(VIBRATE_DURATION);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
