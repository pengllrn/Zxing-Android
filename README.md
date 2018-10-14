## 模块一：手机APP的手机
### 安卓二维码扫码库的选择
用安卓做二维码扫码有两个库，Zxing和Zbar。ZXing是纯Java代码实现的，适用于Android平台；Zbar是C实现的，可以供很多语言和平台使用，比如Java、iOS平台、Android平台，Python等等。Zbar的扫码速度以及性能优于ZXing，但使用起来复杂得多。这里我使用的是Zxing来实现的，如果想要用Zbar实现可以参考：[二维码识别之Android完整编译Zbar](https://blog.csdn.net/yanzhenjie1003/article/details/71641368)。从最后的效果来看，Zxing的扫码速度也是非常的快，几乎也是瞬间完成，所以完全可以用Zxing。
不过需要注意的是，我没有直接使用原Zxing库，因为这个库太庞大了，里面包含了很多其他的功能，我们使用不到。这里我使用了一个专门为Android精简后的Zxing库：zxing-android-embedded。
Zxing官方github地址:[https://github.com/zxing/zxing](https://github.com/zxing/zxing)
zxing-android-embedded：[https://github.com/journeyapps/zxing-android-embedded](https://github.com/journeyapps/zxing-android-embedded)。
### 基本使用方法
zxing-android-embedded的使用方法很简单：
#### 第一步：导包
首先在gradle中添加依赖：
``` java
compile 'com.journeyapps:zxing-android-embedded:3.5.0'
```
#### 第二步：创建扫码实例
在Activity或者Fragment中，进行实例的创建和初始化：
``` java
new IntentIntegrator(MainActivity.this)
                        .setCaptureActivity(MyCaptureActivity.class)     //设置自定义的CaptureActivity，在这里自定义扫描布局
                        .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)// 扫码的类型,可选：一维码，二维码，一/二维码
                        .setPrompt("请将二维码放入取景框\n")// 设置提示语
                        .setCameraId(0)// 选择摄像头,可使用前置或者后置
                        .setBeepEnabled(false)// 是否开启声音,扫完码之后会"哔"的一声
                        .setBarcodeImageEnabled(false)// 扫完码之后生成二维码的图片,保存扫描后的图片
                        .initiateScan();// 初始化扫码
```
#### 第三步：处理扫码结果
我们都知道，二维码中包含的其实是一段文本信息，那么扫码之后会得到这段文本信息。当正确的扫码出这段文本之后，需要我们进行一些处理。像微信的扫一扫，当扫出网址的时候会启用WebView打开网址。我们这里只需要将扫码后的信息，传递给下一个Activity即可，然后在下一个Activity处理。
``` java
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

```
其中关键的代码是IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);以及result.getContents()。
### 自定义扫码界面
虽然zxing-android-embedded的使用方法很简单，但是一个不得不承认的事实，这个扫码界面也太不美观了吧，而且还是横屏的？？？
![](https://i.imgur.com/L7apvV8.png)
其实在设置IntentIntegrator时还有一个方法：setCaptureActivity(Activity)，这个方法就是用来设置扫码界面的Activity，当不设置的时候会默认调用库作者自己写的CaptureActivity，首先看一下Activity里面做了什么：
``` java
public class CaptureActivity extends Activity {
    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        barcodeScannerView = initializeContent();

        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();
    }

     //省略代码
}
```
这里主要有两个成员变量：`CaptureManager`和`DecoratedBarcodeView`，它们的作用分别是：
1.CaptureManager是用来拉起扫码和处理扫码结果的类
2.DecoratedBarcodeView则是一个显示扫码界面的自定义View
再简单的看一下DecoratedBarcodeView：
``` java
public class DecoratedBarcodeView extends FrameLayout {
    private BarcodeView barcodeView;
    private ViewfinderView viewFinder;
    private TextView statusView;

    //省略代码
}
```
1.BarcodeView就是背景
2.ViewfinderView就是扫描框
3.TextView为下方提示文字
如图所示，其对应的扫码View如下：
![](https://i.imgur.com/iP3P9KB.png)
#### 自定义界面步骤
>1.新建一个MyCartureActivity
2.设置setCaptureActivity(MyCaptureActivity.class)为我们自己的Activity
3.把自定义的MyCartureActivity加入到AndroidManifest.xml中注册

**MyCartureActivity.java代码如下**：
``` java
public class MyCaptureActivity extends Activity {
    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mycapture);
        barcodeScannerView =  (DecoratedBarcodeView) findViewById(R.id.dbv_custom);

        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();
    }
	//省略其他代码
}
```
这里我们可以看到，自定义的MyCartureActivity只是修改了原CartureActivity部分代码，首先引入了一个我们自定义的layout作为扫码界面：setContentView(R.layout.activity_mycapture);然后绑定layout中的DecoratedBarcodeView。

**activity_mycapture.xml代码如下**：
``` xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:gravity="center_horizontal"
    android:orientation="vertical">

    <!-- 扫描取景框 -->
    <com.journeyapps.barcodescanner.DecoratedBarcodeView
        android:id="@+id/dbv_custom"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        app:zxing_scanner_layout="@layout/custom_barcode_scanner">
        <RelativeLayout
            android:layout_width="match_parent"
            android:layout_height="45dp"
            android:orientation="horizontal"
            android:background="#50000000">
            <!--设置透明度-->
            <android.support.v7.widget.AppCompatImageView
                android:id="@+id/backIv"
                android:layout_width="42dp"
                android:layout_height="match_parent"
                android:padding="7dp"
                app:srcCompat="@drawable/ic_back" />

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="match_parent"
                android:layout_centerInParent="true"
                android:text="扫一扫"
                android:gravity="center"
                android:textColor="#ffffff"
                android:textSize="18sp" />

            <ImageView
                android:id="@+id/select_photo"
                android:layout_width="40dp"
                android:layout_height="match_parent"
                android:layout_alignParentRight="true"
                android:padding="9dp"
                android:layout_marginRight="2dp"
                android:src="@drawable/ic_photo"/>

        </RelativeLayout>
    </com.journeyapps.barcodescanner.DecoratedBarcodeView>

</LinearLayout>

```
注意在activity_mycapture.xml中，为DecoratedBarcodeView绑定了一个布局custom_barcode_scanner.xml，如代码15行所示：
``` xml
app:zxing_scanner_layout="@layout/custom_barcode_scanner"
```
那么最后在来看一下这个布局：
``` xml
<merge xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    xmlns:app="http://schemas.android.com/apk/res-auto">

    <com.journeyapps.barcodescanner.BarcodeView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:id="@+id/zxing_barcode_surface"
        app:zxing_framing_rect_width="250dp"
        app:zxing_framing_rect_height="250dp"/>

    <com.example.pengl.zxing_android.view.MyFinderView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:id="@+id/zxing_viewfinder_view"
        app:zxing_possible_result_points="@color/zxing_custom_possible_result_points"
        app:zxing_result_view="@color/zxing_custom_result_view"
        app:zxing_viewfinder_laser="@color/zxing_custom_viewfinder_laser"
        app:zxing_viewfinder_mask="@color/zxing_custom_viewfinder_mask"/>

    <TextView
        android:id="@+id/zxing_status_view"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="bottom|center_horizontal"
        android:background="@color/zxing_transparent"
        android:text="@string/zxing_msg_default_status"
        android:textColor="@color/zxing_status_text"/>

</merge>
```
之前提到的三个View：BarcodeView，FinderView，TextView又在这里出现了。并且我们修改了ViewFinderView:
MyFinderView.java:
``` java
public class MyFinderView extends ViewfinderView {
    protected final int maskColor;
    protected final int resultColor;
    protected final int laserColor;
    protected final int resultPointColor;

    // 扫描线移动的y
    private int scanLineTop;
    // 扫描线移动速度
    private int SCAN_VELOCITY = 10;
    //扫描线高度
    private int scanLightHeight = 30;
    // 扫描线
    Bitmap scanLight;

    public MyFinderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Resources resources = getResources();
        scanLight = BitmapFactory.decodeResource(resources, R.drawable.scan_line);

        // Get setted attributes on view
        TypedArray attributes = getContext().obtainStyledAttributes(attrs, R.styleable.zxing_finder);
        this.maskColor = attributes.getColor(R.styleable.zxing_finder_zxing_viewfinder_mask,
                resources.getColor(R.color.zxing_viewfinder_mask));
        this.resultColor = attributes.getColor(R.styleable.zxing_finder_zxing_result_view,
                resources.getColor(R.color.zxing_result_view));
        this.laserColor = attributes.getColor(R.styleable.zxing_finder_zxing_viewfinder_laser,
                resources.getColor(R.color.zxing_viewfinder_laser));
        this.resultPointColor = attributes.getColor(R.styleable.zxing_finder_zxing_possible_result_points,
                resources.getColor(R.color.zxing_possible_result_points));
    }

    @SuppressLint("DrawAllocation")
    @Override
    public void onDraw(Canvas canvas) {
        refreshSizes();
        if (framingRect == null || previewFramingRect == null) {
            return;
        }

        Rect frame = framingRect;
        Rect previewFrame = previewFramingRect;

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        // Draw the exterior (i.e. outside the framing rect) darkened
        paint.setColor(resultBitmap != null ? resultColor : maskColor);
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);

        if (resultBitmap != null) {
            // Draw the opaque result bitmap over the scanning rectangle
            paint.setAlpha(CURRENT_POINT_OPACITY);
            canvas.drawBitmap(resultBitmap, null, frame, paint);
        } else {
            //画边框
            drawFrameBounds(canvas,frame);
            //画扫面线
            drawScanLight(canvas,frame);

            float scaleX = frame.width() / (float) previewFrame.width();
            float scaleY = frame.height() / (float) previewFrame.height();

            List<ResultPoint> currentPossible = possibleResultPoints;
            List<ResultPoint> currentLast = lastPossibleResultPoints;
            int frameLeft = frame.left;
            int frameTop = frame.top;
            if (currentPossible.isEmpty()) {
                lastPossibleResultPoints = null;
            } else {
                possibleResultPoints = new ArrayList<>(5);
                lastPossibleResultPoints = currentPossible;
                paint.setAlpha(CURRENT_POINT_OPACITY);
                paint.setColor(resultPointColor);
                for (ResultPoint point : currentPossible) {
                    canvas.drawCircle(frameLeft + (int) (point.getX() * scaleX),
                            frameTop + (int) (point.getY() * scaleY),
                            POINT_SIZE, paint);
                }
            }
            if (currentLast != null) {
                paint.setAlpha(CURRENT_POINT_OPACITY / 2);
                paint.setColor(resultPointColor);
                float radius = POINT_SIZE / 2.0f;
                for (ResultPoint point : currentLast) {
                    canvas.drawCircle(frameLeft + (int) (point.getX() * scaleX),
                            frameTop + (int) (point.getY() * scaleY),
                            radius, paint);
                }
            }

            // Request another update at the animation interval, but only repaint the laser line,
            // not the entire viewfinder mask.
            postInvalidateDelayed(ANIMATION_DELAY,
                    frame.left - POINT_SIZE,
                    frame.top - POINT_SIZE,
                    frame.right + POINT_SIZE,
                    frame.bottom + POINT_SIZE);
        }
    }

    /**
     * 绘制移动扫描线
     *
     * @param canvas
     * @param frame
     */
    private void drawScanLight(Canvas canvas, Rect frame) {
        paint.setColor(laserColor);
        paint.setAlpha(250);

        if (scanLineTop == 0 || scanLineTop + SCAN_VELOCITY >= frame.bottom) {
            scanLineTop = frame.top;
        } else {
            /*缓动动画*/
            SCAN_VELOCITY = (frame.bottom - scanLineTop) / 12;
            SCAN_VELOCITY = (int) (SCAN_VELOCITY > 20 ? Math.ceil(SCAN_VELOCITY) : 20);
            scanLineTop += SCAN_VELOCITY;
        }

        Rect scanRect = new Rect(frame.left, scanLineTop, frame.right,
                scanLineTop + scanLightHeight);
        canvas.drawBitmap(scanLight, null, scanRect, paint);
    }

    /**
     * 绘制取景框边框
     *
     * @param canvas
     * @param frame
     */
    private void drawFrameBounds(Canvas canvas, Rect frame) {

        /*扫描框的四个角*/
        paint.setColor(Color.parseColor("#ffffff"));
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(1);

        /*四个角的长度和宽度*/
        int width = frame.width();
        int corLength = (int) (width * 0.07);
        int corWidth = (int) (corLength * 0.2);


        corWidth = corWidth > 15 ? 15 : corWidth;


        /*角在线外*/
        // 左上角
        canvas.drawRect(frame.left - corWidth, frame.top, frame.left, frame.top
                + corLength, paint);
        canvas.drawRect(frame.left - corWidth, frame.top - corWidth, frame.left
                + corLength, frame.top, paint);
        // 右上角
        canvas.drawRect(frame.right, frame.top, frame.right + corWidth,
                frame.top + corLength, paint);
        canvas.drawRect(frame.right - corLength, frame.top - corWidth,
                frame.right + corWidth, frame.top, paint);
        // 左下角
        canvas.drawRect(frame.left - corWidth, frame.bottom - corLength,
                frame.left, frame.bottom, paint);
        canvas.drawRect(frame.left - corWidth, frame.bottom, frame.left
                + corLength, frame.bottom + corWidth, paint);
        // 右下角
        canvas.drawRect(frame.right, frame.bottom - corLength, frame.right
                + corWidth, frame.bottom, paint);
        canvas.drawRect(frame.right - corLength, frame.bottom, frame.right
                + corWidth, frame.bottom + corWidth, paint);
    }
}
```
MyFinderView主要是绘制了取景框以及扫描线。并且去掉了原来zxing的红色线。
最后看一下效果：
![](https://i.imgur.com/fJYijiw.jpg)
好了，到此为止自定义扫码界面完成！
#### 处理扫描结果
扫码结果返回一段文本信息，那我们要如何处理呢？
在MainActivity中，我们得到了扫码信息，我把它记为`lgToken`，然后把它传入到SencondActivity中进行处理。
这里我对lgToken没有进行过多复杂的处理以及判断，直接把它当成是我们设计的文本（后面讲二维码产生的时候会讲）。然后把它作为url，访问服务器。
看一下SecondeActivity的代码：
``` java
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

```
这里主要有两个点：第一个是用OkHttp3通过POST请求访问网络，把用户信息通过lgToken中的url传到服务器，然后利用hander处理服务器返回来的结果（登录成功或者失败）。
我自定义了一个Okhttp的工具类，优于此处不是重点，所以只贴部分代码：
``` java

	public final int POSTOK = 0x2017;
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
```
好了，扫码app设计完成。最后，完整的工程代码：[https://github.com/pengllrn/Zxing-Android](https://github.com/pengllrn/Zxing-Android)



