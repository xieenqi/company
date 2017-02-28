package com.loyo.oa.v2.order.cell;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loyo.oa.v2.R;

/**
 * Created by EthanGong on 2017/2/28.
 */

public class AddButtonCell extends ProductAddBaseCell {

    TextView textView;

    public static AddButtonCell instance(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cell_add_button, parent, false);
        return new AddButtonCell(itemView);
    }

    private AddButtonCell(View itemView) {
        super(itemView);
        textView = (TextView) itemView.findViewById(R.id.textView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.addProduct();
                }
            }
        });
    }

    public void setText(String text) {
        textView.setText(text);
    }
}
