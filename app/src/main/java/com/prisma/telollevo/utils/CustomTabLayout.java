package com.prisma.telollevo.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import com.google.android.material.tabs.TabLayout;

import java.lang.reflect.Field;

public class CustomTabLayout extends TabLayout {
    public CustomTabLayout(Context context) {
        super(context);
    }

    public CustomTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        try {
            if (getTabCount() == 0)
                return;
            Field field = TabLayout.class.getDeclaredField("scrollableTabMinWidth");
            field.setAccessible(true);
            field.set(this, (int) (getMeasuredWidth() / (float) getTabCount()));
        } catch (Exception e) {
            Log.e("MAIN", "onMeasure: "+e.getMessage() );
            e.printStackTrace();
        }
    }
}
