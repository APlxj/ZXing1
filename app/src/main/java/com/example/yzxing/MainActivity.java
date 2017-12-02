package com.example.yzxing;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.qrcode.Constant;
import com.example.qrcode.ScannerActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Set;


public class MainActivity extends AppCompatActivity {

    private final int REQUEST_PERMISION_CODE_CAMARE = 0;
    private final int RESULT_REQUEST_CODE = 1;
    private static final String TAG = "MainActivity";

    private HashMap<String, Set> mHashMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button mScanner = (Button) findViewById(R.id.scanner);
        mScanner.setOnClickListener(mScannerListener);

        Set<BarcodeFormat> codeFormats = EnumSet.of(BarcodeFormat.QR_CODE
                , BarcodeFormat.CODE_128
                , BarcodeFormat.CODE_93 );
        mHashMap.put(ScannerActivity.BARCODE_FORMAT, codeFormats);
    }

    private View.OnClickListener mScannerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                goScanner();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISION_CODE_CAMARE);
            }
        }
    };

    public void cretaZxing(View view) {
        startActivity(new Intent(MainActivity.this, CretaActivity.class));
    }

    private void goScanner() {
        Intent intent = new Intent(this, ScannerActivity.class);
        //这里可以用intent传递一些参数，比如扫码聚焦框尺寸大小，支持的扫码类型。
        //设置扫码框的宽
        intent.putExtra(Constant.EXTRA_SCANNER_FRAME_WIDTH, 400);
        //设置扫码框的高
        intent.putExtra(Constant.EXTRA_SCANNER_FRAME_HEIGHT, 400);
        //设置扫码框距顶部的位置
        intent.putExtra(Constant.EXTRA_SCANNER_FRAME_TOP_PADDING, 100);
        Bundle bundle = new Bundle();
        //设置支持的扫码类型
        bundle.putSerializable(Constant.EXTRA_SCAN_CODE_TYPE, mHashMap);
        intent.putExtras(bundle);
        startActivityForResult(intent, RESULT_REQUEST_CODE);
    }


    /**
     * 扫描图片二维码的方法
     *
     * @param path
     * @return
     */
    public String scanningImage(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        //解码配置
        Hashtable<DecodeHintType, String> hints = new Hashtable<>();
        hints.put(DecodeHintType.CHARACTER_SET, "UTF8"); //设置二维码内容的编码
        //加载图片
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 先获取原大小
        options.inJustDecodeBounds = false; // 获取新的大小
        int sampleSize = (int) (options.outHeight / (float) 200);
        if (sampleSize <= 0) sampleSize = 1;
        options.inSampleSize = sampleSize;
        Bitmap scanBitmap = BitmapFactory.decodeFile(path, options);
        int width = scanBitmap.getWidth();
        int height = scanBitmap.getHeight();
        int[] lPixels = new int[width * height];
        scanBitmap.getPixels(lPixels, 0, width, 0, 0, width, height);
        RGBLuminanceSource source = new RGBLuminanceSource(width, height, lPixels);
        //解码
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        try {
            return reader.decode(bitmap1, hints).getText();
        } catch (NotFoundException | ChecksumException | FormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISION_CODE_CAMARE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    goScanner();
                }
                return;
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESULT_REQUEST_CODE:
                    if (data == null) return;
                    String type = data.getStringExtra(Constant.EXTRA_RESULT_CODE_TYPE);
                    String content = data.getStringExtra(Constant.EXTRA_RESULT_CONTENT);
                    Toast.makeText(MainActivity.this, "codeType:" + type
                            + "-----content:" + content, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
