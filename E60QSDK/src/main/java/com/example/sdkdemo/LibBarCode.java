package com.example.sdkdemo;


public class LibBarCode {


    static {
        System.loadLibrary("barcode_jni");
    }

    private OnBarCodeProgressListener callbackListener;
    private static LibBarCode mLibBarCode;

    private LibBarCode() {
    }

    public static synchronized LibBarCode getInstance() {
        if (mLibBarCode == null) {
            mLibBarCode = new LibBarCode();
            mLibBarCode.read_barcode();
        }
        return mLibBarCode;
    }


    public void barCodeRead(OnBarCodeProgressListener l) {
        callbackListener = l;
    }

    public native boolean init_barcode();

    public native boolean close_barcode();

    private native boolean read_barcode();

    public native boolean barcode_write(byte[] pkg);

    private int onProgressCallBack(byte[] barcode, int len) {
        if (callbackListener != null) {
            byte[] result = new byte[len - 2];
            System.arraycopy(barcode, 0, result, 0, len - 2);
            callbackListener.onProgressChange(result, len - 2);
        }
        return 1;
    }


    public interface OnBarCodeProgressListener {
        int onProgressChange(byte[] barcode, int len);
    }

}
