package com.shangzuo.highvaluecabinet.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.shangzuo.highvaluecabinet.R;


public class HomeItemView extends RelativeLayout {


    public HomeItemView(Context context) {
        super(context);
    }

    public HomeItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.item_home_menu, this, true);
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.HomeItemView);

            String title = a.getString(R.styleable.HomeItemView_name);
            int srcResourceId = a.getResourceId(R.styleable.HomeItemView_src, 0);

            TextView tv_title = findViewById(R.id.tv_name);
            ImageView iv_icon = findViewById(R.id.iv_icon);

            tv_title.setText(title);
            iv_icon.setImageResource(srcResourceId);
        }
    }
}
