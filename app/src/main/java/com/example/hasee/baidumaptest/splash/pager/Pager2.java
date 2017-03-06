package com.example.hasee.baidumaptest.splash.pager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.hasee.baidumaptest.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Pager2 extends FrameLayout {

    @BindView(R.id.ivBubble1)
    ImageView ivBubble1;
    @BindView(R.id.ivBubble2)
    ImageView ivBubble2;
    @BindView(R.id.ivBubble3)
    ImageView ivBubble3;

    /*三个构造方法
       * 1.一般仅在代码中使用
       * 2.在代码和布局中都使用
       * 3.在代码布局中，布局中使用并且设置了Style
       * */
    public Pager2(Context context) {
        this(context, null);
    }

    public Pager2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Pager2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.content_pager_2, this, true);
        ButterKnife.bind(this);
        ivBubble1.setVisibility(GONE);
        ivBubble2.setVisibility(GONE);
        ivBubble3.setVisibility(GONE);
    }

    public void showAnimation() {
        /**
         * YoYo动画库的使用
         * YoYo.with(Techniques.Tada)
         .duration(700)
         .playOn(findViewById(R.id.edit_area));
         */

        if (ivBubble1.getVisibility()!=VISIBLE){
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    ivBubble1.setVisibility(VISIBLE);
                    YoYo.with(Techniques.FadeInLeft).duration(300).playOn(ivBubble1);
                }
            },100);
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    ivBubble2.setVisibility(VISIBLE);
                    YoYo.with(Techniques.FadeInLeft).duration(300).playOn(ivBubble2);
                }
            },600);
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    ivBubble3.setVisibility(VISIBLE);
                    YoYo.with(Techniques.FadeInLeft).duration(300).playOn(ivBubble3);
                }
            },1100);

        }
    }
}
