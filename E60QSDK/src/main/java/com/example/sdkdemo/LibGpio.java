/***********************************************
 *
 *		DEMO INTERFACE FOR libgpio_jni.so
 *
 ************************************************/

package com.example.sdkdemo;

public class LibGpio {
    private static final String TAG = "LibGpio";

    public static final int GPIO_OUTPUT_MODE = 1;
    public static final int GPIO_INPUT_MODE = 0;

    public static final int GPIO_LED_GREEN = 164;
    public static final int GPIO_LED_RED= 177;
    public static final int GPIO_LED_ONLINE = 11;
    public static final int GPIO_BEEP_ON = 165;
    public static final int RS485_PWR_SEL = 170;
    public static final int GPIO_FIQ_CHECK = 4;  //检查是否掉电

    public native boolean set_gpio_output(int pin, int value);
    public native boolean set_gpio_input(int pin);
    public native boolean get_gpio_value(int pin);
    public native boolean set_gpio_dir(int pin, int dir);
    public native boolean set_gpio_value(int pin, int value);
    public native boolean is_gpio_valid(int pin);

    static {
        System.loadLibrary("gpio_jni");
    };
}
