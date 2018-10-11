## 第一步，引入包
> compile 'com.journeyapps:zxing-android-embedded:3.5.0'

## 然后在Activity或fragment中调用即可:
``` java
   new IntentIntegrator(this)
           .setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES)// 扫码的类型,可选：一维码，二维码，一/二维码
           .setPrompt("请对准二维码")// 设置提示语
           .setCameraId(0)// 选择摄像头,可使用前置或者后置
           .setBeepEnabled(false)// 是否开启声音,扫完码之后会"哔"的一声
           .setBarcodeImageEnabled(true)// 扫完码之后生成二维码的图片
           .initiateScan();// 初始化扫码
```
扫完码之后会在onActivityResult方法中回调结果
``` java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
    if(result != null) {
        if(result.getContents() == null) {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
        }
    } else {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
```




