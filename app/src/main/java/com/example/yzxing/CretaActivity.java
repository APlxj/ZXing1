package com.example.yzxing;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.qrcode.utils.ZingBitmapUtil;

/**
 * 类描述：
 * 创建人：swallow.li
 * 创建时间：
 * Email: swallow.li@kemai.cn
 * 修改备注：
 */
public class CretaActivity extends Activity {

    ImageView iv_zxing, iv_barcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creta);
        iv_zxing = (ImageView) findViewById(R.id.iv_zxing);
        iv_barcode = (ImageView) findViewById(R.id.iv_barcode);

        iv_zxing.setImageBitmap(ZingBitmapUtil.createQRImage(500, 500, "1322265057@qq.com", BitmapFactory.decodeResource(getResources(),
                R.drawable.photo)/*null*/));
        iv_barcode.setImageBitmap(ZingBitmapUtil.createBarCode("1322265"));
    }
}
