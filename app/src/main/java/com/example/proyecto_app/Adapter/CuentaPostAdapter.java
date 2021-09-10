package com.example.proyecto_app.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_app.Constantes;
import com.example.proyecto_app.Modelo.Post;
import com.example.proyecto_app.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CuentaPostAdapter extends RecyclerView.Adapter<CuentaPostAdapter.CuentasPostHolder> {

    private Context context;
    private ArrayList<Post> arrayList;

    public CuentaPostAdapter(Context context, ArrayList<Post> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }


    @NonNull
    @Override
    public CuentasPostHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_cuenta_post,parent,false);
        return new CuentasPostHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull  CuentaPostAdapter.CuentasPostHolder holder, int position) {

        Picasso.get().load(arrayList.get(position).getFoto()).into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class CuentasPostHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;
        public CuentasPostHolder(View itemView){
            super(itemView);
            imageView = itemView.findViewById(R.id.img_cuentaPost);
        }
    }

}
