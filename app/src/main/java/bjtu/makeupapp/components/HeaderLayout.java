package bjtu.makeupapp.components;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import bjtu.makeupapp.activity.R;


/**
 * Created by lenovo on 2017/8/25.
 */

public class HeaderLayout extends LinearLayout {

    public HeaderLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.header,this);
    }
}
