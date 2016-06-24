package com.loyo.oa.v2.common.commonview;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.util.AttributeSet;
import android.widget.TextView;

import com.loyo.oa.v2.tool.LogUtil;

/**
 * TextView长度过长字体动态变小  字体大小一般设置
 */
public class CustomTextView extends TextView {
    private static float DEFAULT_MIN_TEXT_SIZE = 1; // 最小的字体大小
    private static float DEFAULT_MAX_TEXT_SIZE = 18;// 验证大部分手机情况下无效值

    // Attributes
    private Paint testPaint;
    private float minTextSize, maxTextSize;

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialise();
    }

    private void initialise() {
        testPaint = new Paint();
        testPaint.set(this.getPaint()); // 获取模拟的paint

        // max size defaults to the intially specified text size unless it is
        // too small
        maxTextSize = this.getTextSize();// 获取单个字体的像素
        if (maxTextSize <= DEFAULT_MIN_TEXT_SIZE) {
            maxTextSize = DEFAULT_MAX_TEXT_SIZE;
        }
        minTextSize = DEFAULT_MIN_TEXT_SIZE;
        LogUtil.d("字体设置最大号：" + this.getTextSize());
    }

    ;

    /**
     * Re size the font so the specified text fits in the text box * assuming
     * the text box is the specified width.
     */
    private void refitText(String text, int textWidth) {
        if (textWidth > 0) {
            int availableWidth = textWidth - this.getPaddingLeft()
                    - this.getPaddingRight();// 获取改TextView的画布可用大小        

            float trySize = maxTextSize;
            float scaled = getContext().getResources().getDisplayMetrics().scaledDensity;
            testPaint.setTextSize(trySize * scaled);// 模拟注意乘以scaled
            while ((trySize > minTextSize)
                    && (testPaint.measureText(text) > availableWidth)) {
                trySize -= 2;
                FontMetrics fm = testPaint.getFontMetrics();
                float scaled1 = (float) (this.getHeight() / (Math
                        .ceil(fm.descent - fm.top) + 2));
                float scaled2 = (float) ((testPaint.measureText(text) / availableWidth));
                if (scaled1 >= 1.75 & scaled1 >= scaled2) {// 注意1.75是三星s4 小米3
                    // 的适合数值（当然包括我的联想了)
                    break;
                }
                if (trySize <= minTextSize) {
                    trySize = minTextSize;
                    break;
                }
                testPaint.setTextSize(trySize * scaled);
            }
            this.setTextSize(trySize);// 等同于this.getPaint().set(trySize*scaled);
        }
    }

    ;

    @Override
    protected void onTextChanged(CharSequence text, int start, int before,
                                 int after) {
        super.onTextChanged(text, start, before, after);
        refitText(text.toString(), this.getWidth());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w != oldw) {
            refitText(this.getText().toString(), w);
        }
    }
}
