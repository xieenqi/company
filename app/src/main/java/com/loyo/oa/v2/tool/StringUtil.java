package com.loyo.oa.v2.tool;

import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.Log;
import android.widget.TextView;

import java.util.Random;
import java.util.UUID;


public class StringUtil {
    private static final String TAG = "StringUtil";

    protected StringUtil() {
        throw new UnsupportedOperationException(); // 防止子类调用
    }

    public static boolean isEmpty(String s) {
        return (s == null || s.isEmpty() || "".equals(s) || "".equals(s.trim()));
    }

    // 计算出该TextView中文字的长度(像素)
    public static float getTextViewLength(TextView textView, String text) {
        TextPaint paint = textView.getPaint();
        // 得到使用该paint写上text的时候,像素为多少
        float textLength = paint.measureText(text);
        Log.d(TAG, "getTextViewLength textLength:" + textLength);
        return textLength;
    }

    // 计算出该TextView中文字的宽度(像素)
    public static float getTextViewWidth(TextView textView) {
        Rect rect = new Rect();
        TextPaint paint = textView.getPaint();
        paint.getTextBounds((String) textView.getText(), 0, textView.getText().length(), rect);
        Log.d(TAG, "getTextViewWidth height:" + rect.height() + "width:" + rect.width());
        return rect.width();
    }

    // 计算出该TextView中文字的宽度(像素)
    public static float getTextViewWidth(TextView textView, String str) {
        Rect rect = new Rect();
        TextPaint paint = textView.getPaint();
        paint.getTextBounds(str, 0, str.length(), rect);
        Log.d(TAG, "getTextViewWidth height:" + rect.height() + "width:" + rect.width());
        return rect.width();
    }

    public static float getTextWidth(String str, float textSize, int start, int end) {
        Paint pFont = new Paint();
        pFont.setTextSize(textSize);
        Rect rect = new Rect();
        pFont.getTextBounds(str, start, end, rect);
        Log.d(TAG, "getTextWidth height:" + rect.height() + "width:" + rect.width());
        return rect.width();
    }

    /**
     * 生成32位编码
     *
     * @return string
     */
    public static String getUUID() {
        String uuid = UUID.randomUUID().toString().trim();
        return uuid;
    }

    /**
     * 自定义规则生成32位编码
     *
     * @return string
     */
    public static String getUUIDByRules(String rules) {

        String radStr = rules;
        int rpoint = 0;
        StringBuffer generateRandStr = new StringBuffer();
        Random rand = new Random();
        int length = 32;
        for (int i = 0; i < length; i++) {
            if (rules != null) {
                rpoint = rules.length();
                int randNum = rand.nextInt(rpoint);
                generateRandStr.append(radStr.substring(randNum, randNum + 1));
            }
        }
        return generateRandStr + "";
    }
}
