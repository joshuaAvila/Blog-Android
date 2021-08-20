package com.example.proyecto_app.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.proyecto_app.R;

public class ViewPagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater inflater;

    public ViewPagerAdapter(Context context){
        this.context = context;
    }

    private int images[] = {
            R.drawable.workstation,
            R.drawable.blog2,
            R.drawable.blogpersona
    };

    private String titles[] ={
            "Learn",
            "Create",
            "Enjoy"
    };

    private String desc[] ={
            "Learn Languages English",
            "Create Friends",
            "Enjoy to group"
    };

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout)object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull  ViewGroup container, int position) {
       inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
       View v = inflater.inflate(R.layout.view_pager, container, false);


        ImageView imageView = v.findViewById(R.id.imgViewPager);
        TextView txt_Title = v.findViewById(R.id.txt_titleViewPager);
        TextView txt_Desc = v.findViewById(R.id.txt_descViewPager);

        imageView.setImageResource(images[position]);
        txt_Title.setText(titles[position]);
        txt_Desc.setText(desc[position]);

        container.addView(v);

        return v;
    }

    @Override
    public void destroyItem(@NonNull  ViewGroup container, int position,  Object object) {
       container.removeView((LinearLayout)object);
    }
}
