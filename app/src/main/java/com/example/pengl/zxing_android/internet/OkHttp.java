package com.example.pengl.zxing_android.internet;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * @author Administrator
 * @version $Rev$
 * @des ${UTODO}
 * @updateAuthor ${Author}$
 * @updateDate2017/10/5.
 */

public class OkHttp {
    public final int POSTOK = 0x2017;
    public final int GETOK = 0x2020;
    public final int GETIMGOK = 0x2030;
    public final int WRANG = 0x22;
    public final int EXCEPTION = 0x30;
    private final int SUC = 0x40;
    private Handler handler;
    private Context mContext;

    public OkHttp(Context context, Handler handler) {
        this.mContext = context;
        this.handler = handler;
    }

    public void postDataFromInternet(final String path, final RequestBody requestBody) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    //用post提交键值对格式的数据
                    Request request = new Request.Builder()
                            .url(path)
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        Message msg = new Message();
                        msg.what = POSTOK;
                        msg.obj = responseData;
                        handler.sendMessage(msg);
                    } else {
                        //TODO 错误报告
                        Message msg = new Message();
                        msg.what = WRANG;
                        handler.sendMessage(msg);
                    }
                } catch (IOException e) {
                    Message msg = new Message();
                    msg.what = EXCEPTION;
                    handler.sendMessage(msg);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void postData2Internet(final String path, final RequestBody requestBody) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    //用post提交键值对格式的数据
                    Request request = new Request.Builder()
                            .url(path)
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        Message msg = new Message();
                        msg.obj = response.body().string();
                        msg.what = SUC;
                        handler.sendMessage(msg);
                    } else {
                        //TODO 错误报告
                        Message msg = new Message();
                        msg.what = WRANG;
                        handler.sendMessage(msg);
                    }
                } catch (IOException e) {
                    Message msg = new Message();
                    msg.what = EXCEPTION;
                    handler.sendMessage(msg);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void getDataFromInternet(final String path) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    //用post提交键值对格式的数据
                    Request request = new Request.Builder()
                            .url(path)
                            .build();
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        Message msg = new Message();
                        msg.what = GETOK;
                        msg.obj = responseData;
                        handler.sendMessage(msg);
                    } else {
                        //TODO 错误报告
                        Message msg = new Message();
                        msg.what = WRANG;
                        handler.sendMessage(msg);
                    }
                } catch (IOException e) {
                    Message msg = new Message();
                    msg.what = EXCEPTION;
                    handler.sendMessage(msg);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void getImageFromInternet(final String path) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    //用post提交键值对格式的数据
                    Request request = new Request.Builder()
                            .url(path)
                            .build();
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                        Message msg = new Message();
                        msg.what = GETIMGOK;
                        msg.obj = bitmap;
                        handler.sendMessage(msg);
                    } else {
                        //TODO 错误报告
                        Message msg = new Message();
                        msg.what = WRANG;
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    Message msg = new Message();
                    msg.what = EXCEPTION;
                    handler.sendMessage(msg);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void uploadMultiFile(final String requesturl,final String fileName,final String deviceid,
                                final String apperid,final String appername,final String damagedepict,
                                final  String datetime,final File ...files) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //File file = new File(fileDir);
                //application/octet-stream
                //RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
//                RequestBody requestBody = new MultipartBody.Builder()
//                        .setType(MultipartBody.FORM)
//                        .addFormDataPart("image", fileName, fileBody)
//                        .build();
                MultipartBody.Builder builder = new MultipartBody.Builder();
                for(int i=0;i<files.length;i++){
                    RequestBody fileBody1 = RequestBody.create(MediaType.parse("application/octet-stream"), files[i]);
                    builder.addFormDataPart("image"+i,fileName+"_"+i+".jpg",fileBody1);
                }
                RequestBody requestBody=builder
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("deviceid",deviceid)
                        .addFormDataPart("applierid",apperid)
                        .addFormDataPart("appliername",appername)
                        .addFormDataPart("damagedepict",damagedepict)
                        .addFormDataPart("datetime",datetime)
                        .build();
                Request request = new Request.Builder()
                        .url(requesturl)
                        .post(requestBody)
                        .build();

                final OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();
                OkHttpClient okHttpClient  = httpBuilder
                        //设置超时
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(15, TimeUnit.SECONDS)
                        .build();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseData = response.body().string();

                        Message msg = new Message();
                        msg.what = 0x2018;
                        msg.obj = responseData;
                        handler.sendMessage(msg);
                    }
                });
            }
        }).start();
    }
}
