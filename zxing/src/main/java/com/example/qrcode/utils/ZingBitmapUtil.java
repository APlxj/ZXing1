package com.example.qrcode.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Hashtable;
import java.util.Random;

/**
 * 类描述：
 * 创建人：swallow.li
 * 创建时间：
 * Email: swallow.li@kemai.cn
 * 修改备注：
 */
public class ZingBitmapUtil {

    public static Bitmap createQRImage(int QR_WIDTH, int QR_HEIGHT, String url, Bitmap logoBmp) {
        try {
            //判断URL合法性
            if (url == null || "".equals(url) || url.length() < 1) {
                return null;
            }
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            //图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
            int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
            //下面这里按照二维码的算法，逐个生成二维码的图片，
            //两个for循环是图片横列扫描的结果
            for (int y = 0; y < QR_HEIGHT; y++) {
                for (int x = 0; x < QR_WIDTH; x++) {
                    if (bitMatrix.get(x, y)) {
                        if (x < QR_WIDTH / 2 && y < QR_HEIGHT / 2) {
                            pixels[y * QR_WIDTH + x] = 0xFF0094FF;// 蓝色
                        } else if (x < QR_WIDTH / 2 && y > QR_HEIGHT / 2) {
                            pixels[y * QR_WIDTH + x] = 0xFFFF0000;// 红色
                        } else if (x > QR_WIDTH / 2 && y > QR_HEIGHT / 2) {
                            pixels[y * QR_WIDTH + x] = 0xFF5ACF00;// 绿色
                        } else {
                            pixels[y * QR_WIDTH + x] = 0xFF000000;// 黑色
                        }
                    } else {
                        pixels[y * QR_WIDTH + x] = 0xffffffff;// 白色
                    }
                }
            }
            //生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
            if (null == logoBmp) return bitmap;

            int logoWidth = logoBmp.getWidth();
            int logoHeight = logoBmp.getHeight();
            float scaleFactor = QR_WIDTH * 1.0f / 5 / logoWidth;
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(bitmap, 0, 0, null);
            canvas.scale(scaleFactor, scaleFactor, QR_WIDTH / 2, QR_HEIGHT / 2);
            canvas.drawBitmap(logoBmp, (QR_WIDTH - logoWidth) / 2, (QR_HEIGHT - logoHeight) / 2, null);
            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 用于将给定的内容生成成一维码
     * 注：目前生成内容为中文的话将直接报错，要修改底层jar包的内容
     *
     * @param content 将要生成一维码的内容
     * @return 返回生成好的一维码bitmap
     */
    public static Bitmap createBarCode(String content) {

        // 生成一维条码,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
        Bitmap bitmap = null;
        try {
            BitMatrix matrix = new MultiFormatWriter().encode(content, BarcodeFormat.CODE_93, 500, 200);

            int width = matrix.getWidth();
            int height = matrix.getHeight();
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (matrix.get(x, y)) {
                        pixels[y * width + x] = 0xff30B2B2;
                    }
                }
            }

            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            // 通过像素数组生成bitmap,具体参考api
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
