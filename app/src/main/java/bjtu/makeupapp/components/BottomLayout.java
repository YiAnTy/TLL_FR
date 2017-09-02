package bjtu.makeupapp.components;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import bjtu.makeupapp.activity.R;

/**
 * Created by Logic on 2017/9/2.
 */

public class BottomLayout extends LinearLayout {

    public BottomLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.bottom,this);//动态加载布局文件
    }
}

