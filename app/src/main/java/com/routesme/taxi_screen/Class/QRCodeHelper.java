package com.routesme.taxi_screen.Class;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.Base64;
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
    /**
     * private constructor of this class only access by stying in this class.
     */
    private QRCodeHelper(Context context) {
        mHeight = (int) (context.getResources().getDisplayMetrics().heightPixels / 2.4);
        mWidth = (int) (context.getResources().getDisplayMetrics().widthPixels / 1.3);
        //Log.e("Dimension = %s", mHeight + "");
        //Log.e("Dimension = %s", mWidth + "");
    }
    /**
     * This method is for singleton instance od this class.
     *
     * @return the QrCode instance.
     */
    public static QRCodeHelper newInstance(Context context) {
        if (qrCodeHelper == null) {
            qrCodeHelper = new QRCodeHelper(context);
        }
        return qrCodeHelper;
    }
    /**
     * This method is called generate function who generate the qrcode and return it.
     *
     * @return qrcode image with encrypted user in it.
     */
    public Bitmap getQRCOde() {

        Bitmap yourLogo = BitmapFactory.decodeResource(App.Companion.getInstance().getResources(), R.drawable.best);
        Bitmap generatedQrCode = generate();
        Bitmap merge = mergeBitmaps(yourLogo, generatedQrCode);

        return merge;
    }



    public static Bitmap imageFromString(String imageData) {
        String data = imageData.substring(imageData.indexOf(",") + 1);
        byte[] imageAsBytes = Base64.decode(data.getBytes(), Base64.DEFAULT);
        String  svgAsString = new String(imageAsBytes, StandardCharsets.UTF_8);

        SVG  svg = null;
        try {
            svg = SVG.getFromString(svgAsString);
        } catch (SVGParseException e) {
            e.printStackTrace();
        }

        // Create a bitmap and canvas to draw onto
        float   svgWidth = (svg.getDocumentWidth() != -1) ? svg.getDocumentWidth() : 500f;
        float   svgHeight = (svg.getDocumentHeight() != -1) ? svg.getDocumentHeight() : 500f;

        Bitmap  newBM = Bitmap.createBitmap(20, 20, Bitmap.Config.ARGB_8888);
        Canvas  bmcanvas = new Canvas(newBM);

        // Clear background to white if you want
        bmcanvas.drawRGB(255, 255, 255);

        // Render our document onto our canvas
        svg.renderToCanvas(bmcanvas);

        return newBM;
    }


    /**
     * Simply setting the correctionLevel to qrcode.
     *
     * @param level ErrorCorrectionLevel for Qrcode.
     * @return the instance of QrCode helper class for to use remaining function in class.
     */
    public QRCodeHelper setErrorCorrectionLevel(ErrorCorrectionLevel level) {
        mErrorCorrectionLevel = level;
        return this;
    }
    /**
     * Simply setting the encrypted to qrcode.
     *
     * @param content encrypted content for to store in qrcode.
     * @return the instance of QrCode helper class for to use remaining function in class.
     */
    public QRCodeHelper setContent(String content) {
        mContent = content;
        return this;
    }
    /**
     * Simply setting the width and height for qrcode.
     *
     * @param width  for qrcode it needs to greater than 1.
     * @param height for qrcode it needs to greater than 1.
     * @return the instance of QrCode helper class for to use remaining function in class.
     */
    public QRCodeHelper setWidthAndHeight(@IntRange(from = 1) int width, @IntRange(from = 1) int height) {
        mWidth = width;
        mHeight = height;
        return this;
    }
    /**
     * Simply setting the margin for qrcode.
     *
     * @param margin for qrcode spaces.
     * @return the instance of QrCode helper class for to use remaining function in class.
     */
    public QRCodeHelper setMargin(@IntRange(from = 0) int margin) {
        mMargin = margin;
        return this;
    }
    /**
     * Generate the qrcode with giving the properties.
     *
     * @return the qrcode image.
     */
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

        Bitmap resizeLogo = Bitmap.createScaledBitmap(logo, canvasWidth / 5, canvasHeight / 5, true);
        int centreX = (canvasWidth - resizeLogo.getWidth()) /2;
        int centreY = (canvasHeight - resizeLogo.getHeight()) / 2;
        canvas.drawBitmap(resizeLogo, centreX, centreY, null);
        return combined;
    }

}
