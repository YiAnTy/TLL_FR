package bjtu.makeupapp.manager;

import android.net.sip.SipAudioCall;
import android.widget.TextView;

import java.util.List;

import bjtu.makeupapp.activity.R;
import bjtu.makeupapp.model.StyleItem;

/**
 * Created by Logic on 2017/9/3.
 */

public class StyleManager {

    private static StyleManager instance = null;

    private StyleItem styleItems;

    public boolean isJudge() {
        return judge;
    }

    public void setJudge(boolean judge) {
        this.judge = judge;
    }

    private boolean judge = false;

    public static StyleManager getInstance() {
        if (instance == null) {
            instance = new StyleManager();
        }
        return instance;
    }

    public StyleItem getStyleItems() {
        return styleItems;
    }

    private StyleManager() {
        styleItems = new StyleItem(R.drawable.style_one, "烟熏妆");
    }

    public void setStyleItems(StyleItem items) {
        this.styleItems = items;
        setJudge(true);
    }

    //自定义监听器
    public interface TextListener {
        public void updateText();
    }

    //监听器触发时机
    public static class Operater {
        private TextListener textListener;

        //设置监听方法
        public void setTextListener(TextListener textListener) {
            this.textListener = textListener;
        }

        //触发监听器中的方法
        public void doSomething() {
            if (textListener != null && StyleManager.getInstance().isJudge()) {
                StyleManager.getInstance().setJudge(false);
                textListener.updateText();
            }
        }
    }

}
