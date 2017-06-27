package com.toocms.freeman.ui;

import android.content.Context;
import android.util.AttributeSet;

import cn.zero.android.common.view.ToggleButton;

/**
 * @author Yiku
 * @date 2017/6/6 9:57
 */
public class FreeManToggle extends ToggleButton {

    private boolean flag = true;

    public FreeManToggle(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FreeManToggle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (!flag){
            return;
        }
        flag = false;
        super.onLayout(changed, left, top, right, bottom);
    }
}
