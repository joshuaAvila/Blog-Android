package com.example.proyecto_app.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_app.Constantes;
import com.example.proyecto_app.Modelo.Post;
import com.example.proyecto_app.R;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {

    private Context context;
    private ArrayList<Post> lista;

    public PostAdapter(Context context, ArrayList<Post> lista) {
        this.context = context;
        this.lista = lista;
    }


    @Override
    public PostHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_post,parent,false);
        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.PostHolder holder, int position) {
        Post post = lista.get(position);
        Picasso.get().load(Constantes.url+"public/fotoPerfiles/"+post.getUser().getFoto()).into(holder.img_postPerfil);
        Picasso.get().load(Constantes.url+"public/fotoPost/"+post.getFoto()).into(holder.img_post);
        holder.txt_name.setText(post.getUser().getUserName());
        holder.txt_comentario.setText("Ver mas "+post.getComentarios()+" Comentarios");
        holder.txt_like.setText(post.getLikes()+" Likes");
        holder.txt_fecha.setText(post.getFecha());
        holder.txt_description.setText(post.getDescription());



    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    class PostHolder extends RecyclerView.ViewHolder{
        private TextView txt_name, txt_description, txt_like, txt_comentario, txt_fecha;
        private CircleImageView img_postPerfil;
        private ImageView img_post;
        private ImageButton btn_postOption, btn_like, btn_comentario;

        public  PostHolder(@NonNull View itemView){
            super(itemView);
            txt_name = itemView.findViewById(R.id.txt_postName);
            txt_comentario = itemView.findViewById(R.id.txt_postComentarios);
            txt_description = itemView.findViewById(R.id.txt_postDescription);
            txt_fecha = itemView.findViewById(R.id.txt_postFecha);
            txt_like =itemView.findViewById(R.id.txt_postLike);
            img_postPerfil = itemView.findViewById(R.id.imgPost_perfil);
            img_post = itemView.findViewById(R.id.img_postFoto);
            btn_postOption = itemView.findViewById(R.id.btn_opciones);
            btn_comentario = itemView.findViewById(R.id.btn_postComentario);
            btn_like = itemView.findViewById(R.id.btn_postLike);
        }
    }


}
