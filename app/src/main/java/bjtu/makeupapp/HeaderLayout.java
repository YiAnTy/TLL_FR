package bjtu.makeupapp;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by lenovo on 2017/8/25.
 */

public class HeaderLayout extends LinearLayout {

    public HeaderLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.header,this);

        final ImageView img_camera_side= (ImageView) findViewById(R.id.img_camera_side);

        // 注册图片按钮监听
        img_camera_side.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getContext(),"切换摄像头",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
