package com.github.mikephil.charting.utils;

import android.content.Context;
import android.text.TextPaint;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtil {

    public static int getWindowWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();
    }

    public static int getWindowHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getHeight();
    }

    /**
     * 功能：判断一个字符串是否包含特殊字符
     *
     * @param string 要判断的字符串
     * @return false 提供的参数string包含特殊字符
     */
    public static boolean isConSpeCharacters(String string) {
        return string.replaceAll("[\u4e00-\u9fa5]*[a-z]*[A-Z]*\\d*-*_*\\s*", "").length() == 0;
    }

    public static boolean isConCharacters(String string) {
        return string.replaceAll("[a-z]*[A-Z]*\\d*-*_*\\s*", "").length() == 0;
    }

    //浮点型判断
    public static boolean isDecimal(String str) {
        if (str == null || "".equals(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]*(\\.?)[0-9]*");
        return pattern.matcher(str).matches();
    }

    //整形判断
    public static boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0; ) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier(
                "status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("[1][34578]\\d{9}");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    public static boolean isChar(String mobiles) {
        Pattern p = Pattern.compile("[a-zA-Z]");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    public static boolean isNUM(String mobiles) {
        Pattern p = Pattern.compile("[1-9]");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    public static boolean isZeroString(String str) {
        if (str == null || "".equals(str)) {
            return true;
        }
        Pattern p = Pattern.compile("[0]+");
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 计算TextView 的宽度
     */
    public static float getTextViewLength(TextView textView, String text) {
        TextPaint paint = textView.getPaint();
        // 得到使用该paint写上text的时候,像素为多少
        return paint.measureText(text);
    }

    /**
     * 根据最大长度获取文本需要设置的字体
     */
    public static int getTextSize(TextView textView, String text, int allLength) {
        int size = 13;
        TextPaint paint = textView.getPaint();
        while (paint.measureText(text) >= allLength) {
            size -= 1;
            textView.setTextSize(size);
        }
        return size;
    }
}
