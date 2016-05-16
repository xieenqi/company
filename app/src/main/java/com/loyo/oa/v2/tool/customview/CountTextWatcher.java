package com.loyo.oa.v2.tool.customview;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

/**
 * Created by yyy on 16/5/16.
 */
public class CountTextWatcher implements TextWatcher {

    TextView tv;

    public CountTextWatcher(TextView textView){
        tv = textView;
    }

    private CharSequence editcount;

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        editcount = s;
    }

    @Override
    public void afterTextChanged(Editable s) {
        tv.setText(editcount.length()+"/500");
    }
}
