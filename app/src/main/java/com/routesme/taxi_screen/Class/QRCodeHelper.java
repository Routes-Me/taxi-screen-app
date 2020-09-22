package com.routesme.taxi_screen.Class;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;

import androidx.annotation.IntRange;
import androidx.core.content.ContextCompat;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.routesme.taxiscreen.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class QRCodeHelper {
    private static QRCodeHelper qrCodeHelper = null;
    private ErrorCorrectionLevel mErrorCorrectionLevel;
    private int mMargin;
    private String mContent;
    private int mWidth, mHeight;
    private QRCodeHelper(Context context) {
        mHeight = (int) (context.getResources().getDisplayMetrics().heightPixels / 2.4);
        mWidth = (int) (context.getResources().getDisplayMetrics().widthPixels / 1.3);
        //Log.e("Dimension = %s", mHeight + "");
        //Log.e("Dimension = %s", mWidth + "");
    }
    public static QRCodeHelper newInstance(Context context) {
        if (qrCodeHelper == null) {
            qrCodeHelper = new QRCodeHelper(context);
        }
        return qrCodeHelper;
    }
    public Bitmap getQRCOde() {
        Bitmap generatedQrCode = generate();
/*
        @SuppressLint("StaticFieldLeak") LogoAsync logoAsync = new LogoAsync(url){

            @Override
            protected void onPostExecute(Bitmap logo) {
                super.onPostExecute(logo);

               // return bmp;
               // Bitmap generatedQrCode = generate();
              //  Bitmap merge = mergeBitmaps(bmp, generatedQrCode);

               // Bitmap yourLogo = BitmapFactory.decodeResource(App.Companion.getInstance().getResources(), R.drawable.best);
                Bitmap generatedQrCode = generate();

                if (generatedQrCode != null && logo != null){
                    Bitmap mergedQrCode = mergeBitmaps(logo, generatedQrCode);
                    if (mergedQrCode != null){
                        qrCodeImage.setImageBitmap(mergedQrCode);
                    }
                }else if (generatedQrCode != null){
                    qrCodeImage.setImageBitmap(generatedQrCode);
                }

            }
        };
        logoAsync.execute();
*/
        return generatedQrCode;
    }

    public QRCodeHelper setErrorCorrectionLevel(ErrorCorrectionLevel level) {
        mErrorCorrectionLevel = level;
        return this;
    }
    public QRCodeHelper setContent(String content) {
        mContent = content;
        return this;
    }
    public QRCodeHelper setWidthAndHeight(@IntRange(from = 1) int width, @IntRange(from = 1) int height) {
        mWidth = width;
        mHeight = height;
        return this;
    }
    public QRCodeHelper setMargin(@IntRange(from = 0) int margin) {
        mMargin = margin;
        return this;
    }
    private Bitmap generate() {
        Map<EncodeHintType, Object> hintsMap = new HashMap<>();
        hintsMap.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hintsMap.put(EncodeHintType.ERROR_CORRECTION, mErrorCorrectionLevel);
        hintsMap.put(EncodeHintType.MARGIN, mMargin);
        try {
            BitMatrix bitMatrix = new QRCodeWriter().encode(mContent, BarcodeFormat.QR_CODE, mWidth, mHeight, hintsMap);
            int[] pixels = new int[mWidth * mHeight];
            for (int i = 0; i < mHeight; i++) {
                for (int j = 0; j < mWidth; j++) {
                    if (bitMatrix.get(j, i)) {

                        TypedValue outValue = new TypedValue();
                        App.currentActivity.getTheme().resolveAttribute(R.attr.text_color, outValue, true);
                       // cardView.setBackgroundResource(outValue.resourceId);

                        pixels[i * mWidth + j] = ContextCompat.getColor(App.Companion.getInstance(),outValue.resourceId);
                    } else {
                        pixels[i * mWidth + j] = 0x282946;
                    }
                }
            }
            return Bitmap.createBitmap(pixels, mWidth, mHeight, Bitmap.Config.ARGB_8888);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Bitmap mergeBitmaps(Bitmap logo, Bitmap qrcode) {

        Bitmap combined = Bitmap.createBitmap(qrcode.getWidth(), qrcode.getHeight(), qrcode.getConfig());
        Canvas canvas = new Canvas(combined);
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();
        canvas.drawBitmap(qrcode, new Matrix(), null);

        Bitmap resizeLogo = Bitmap.createScaledBitmap(logo, canvasWidth / 4, canvasHeight / 4, true);
        int centreX = (canvasWidth - resizeLogo.getWidth()) /2;
        int centreY = (canvasHeight - resizeLogo.getHeight()) / 2;
        canvas.drawBitmap(resizeLogo, centreX, centreY, null);
        return combined;
    }
}

class LogoAsync extends AsyncTask<Void, Void, Bitmap> {

    String logoUrl;
    public LogoAsync(String url) {
        logoUrl = url;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {

        try {
            URL url = new URL(logoUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
           // InputStream stream = input;
            //SVG svg = SVGParser.getSVGFromInputStream(inputStream);
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }
}

