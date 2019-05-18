package com.example.a84045.lbstest1;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Welcome1 extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageView;

    private ViewPager pager;

    private TextView textView;

    private List<View> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome1);
        init();
        initViewPager();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.welcome_guide_btn:
                startActivity(new Intent(getBaseContext(), Login.class));
                finish();
                break;
            default:
                break;
        }
    }

    //初始化
    public void init() {
        textView =findViewById(R.id.welcome_guide_text);
        imageView = findViewById(R.id.welcome_guide_btn);
        pager = findViewById(R.id.welcome_pager);
        imageView.setOnClickListener(this);
        textView.setText("<<<  向左滑动");
        list = new ArrayList<View>();
    }

    //初始化ViewPager的方法
    public void initViewPager() {
        pager.setBackgroundColor(Color.parseColor("#FFFFFF"));
        ImageView iv1 = new ImageView(this);
        iv1.setImageResource(R.mipmap.guide_01);
        ImageView iv2 = new ImageView(this);
        iv2.setImageResource(R.mipmap.guide_02);
        ImageView iv3 = new ImageView(this);
        iv3.setImageResource(R.mipmap.guide_03);
        list.add(iv1);
        list.add(iv2);
        list.add(iv3);

        pager.setAdapter(new MyPagerAdapter());
        //监听ViewPager滑动效果
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            //页卡被选中的方法
            @Override
            public void onPageSelected(int arg0) {
                //如果是第三个页面
                if (arg0 == 2) {
                    imageView.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.GONE);
                } else {
                    textView.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }



    //定义ViewPager的适配器
    class MyPagerAdapter extends PagerAdapter {
        //计算需要多少item显示
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        //初始化item实例方法
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(list.get(position));
            return list.get(position);
        }

        //item销毁的方法
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(list.get(position));
        }
    }
}
