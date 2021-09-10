package com.example.proyecto_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.proyecto_app.Adapter.ViewPagerAdapter;

public class OnBoardActivity extends AppCompatActivity {

    private ViewPager view_Pager;
    private Button btn_left, btn_right;
    private ViewPagerAdapter adapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_board);
        init();
    }
    private void init(){
        view_Pager = findViewById(R.id.view_pager);
        btn_left = findViewById(R.id.btnLeft);
        btn_right = findViewById(R.id.btnRight);
        dotsLayout = findViewById(R.id.dotsLayout);
        adapter = new ViewPagerAdapter(this);
        addDots(0);
        view_Pager.addOnPageChangeListener(listener);
        view_Pager.setAdapter(adapter);

        btn_right.setOnClickListener(v->{
            if(btn_right.getText().toString().equals("Next")){
                view_Pager.setCurrentItem(view_Pager.getCurrentItem()+1);
            }else{
                startActivity(new Intent(OnBoardActivity.this,AuthActivity.class));
                finish();
            }
        });
        btn_left.setOnClickListener(v->{

            view_Pager.setCurrentItem(view_Pager.getCurrentItem()+2);
        });
    }

    private void addDots(int position){
        dotsLayout.removeAllViews();
        dots= new TextView[3];
        for(int i = 0; i < dots.length; i++){
            dots[i] = new TextView(this);

            dots[i].setText((Html.fromHtml("&#8226")));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.colorLightGrey));
            dotsLayout.addView(dots[i]);
        }

        if(dots.length > 0){
            dots[position].setTextColor(getResources().getColor(R.color.colorGrey));
        }
    }
    private  ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }
        @Override
        public void onPageSelected(int position) {
            addDots(position);
            if(position == 0){
                btn_left.setVisibility(View.VISIBLE);
                btn_left.setEnabled(true);
                btn_right.setText("Next");
            }else if(position == 1){
                btn_left.setVisibility(View.GONE);
                btn_left.setEnabled(false);
                btn_right.setText("Next");
            }else{
                btn_left.setVisibility(View.VISIBLE);
                btn_left.setEnabled(false);
                btn_right.setText("Finish");
            }

        }
        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

}