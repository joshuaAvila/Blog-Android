package com.example.proyecto_app.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.proyecto_app.AddPostActivity;
import com.example.proyecto_app.CommentActivity;
import com.example.proyecto_app.Constantes;
import com.example.proyecto_app.EditarPostActivity;
import com.example.proyecto_app.HomeActivity;
import com.example.proyecto_app.Modelo.Post;
import com.example.proyecto_app.R;
import com.squareup.picasso.Picasso;


import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {

    private Context context;
    private ArrayList<Post> lista;
    private ArrayList<Post> listarTodos;
    private SharedPreferences sharedPreferences;


    public PostAdapter(Context context, ArrayList<Post> lista) {
        this.context = context;
        this.lista = lista;
        this.listarTodos = new ArrayList<>(lista);
        sharedPreferences = context.getApplicationContext().getSharedPreferences("user",Context.MODE_PRIVATE);
    }


    @Override
    public PostHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_post,parent,false);
        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.PostHolder holder, int position) {
        Post post = lista.get(position);
        Picasso.get().load(Constantes.url+"storage/perfiles/"+post.getUser().getFoto()).into(holder.img_postPerfil);
        Picasso.get().load(Constantes.url+"storage/posts/"+post.getFoto()).into(holder.img_post);
        holder.txt_name.setText(post.getUser().getUserName());
        holder.txt_comentario.setText("Ver tdos los "+post.getComentarios()+" Comentarios");
        holder.txt_like.setText(post.getLikes()+" Likes");
        holder.txt_fecha.setText(post.getFecha());
        holder.txt_description.setText(post.getDescription());

        holder.btn_like.setImageResource(
                post.isSelfLike()?R.drawable.ic_baseline_favorite_24:R.drawable.ic_baseline_outline
        );

        holder.btn_like.setOnClickListener(v->{
            holder.btn_like.setImageResource(
                    post.isSelfLike()?R.drawable.ic_baseline_outline:R.drawable.ic_baseline_favorite_24
            );

            StringRequest request = new StringRequest(Request.Method.POST,Constantes.like_post,response -> {

                Post nPost = lista.get(position);

                try {
                    JSONObject object = new JSONObject(response);
                    if(object.getBoolean("success")){
                        nPost.setSelfLike(!post.isSelfLike());
                        nPost.setLikes(nPost.isSelfLike()?post.getLikes()+1:post.getLikes()-1);
                        lista.set(position,nPost);
                        notifyItemChanged(position);
                        notifyDataSetChanged();

                    }else{
                        holder.btn_like.setImageResource(
                                post.isSelfLike()?R.drawable.ic_baseline_favorite_24:R.drawable.ic_baseline_outline
                        );
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            },error -> {
                error.printStackTrace();
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    String token = sharedPreferences.getString("token","");
                    HashMap<String,String> map = new HashMap<>();
                    map.put("Authorization","Bearer "+token);
                    return map;

                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String,String> map = new HashMap<>();
                    map.put("id",post.getId()+"");
                    return map;
                }
            };
            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(request);
        });
        if(post.getUser().getId() == sharedPreferences.getInt("id",0)){

            holder.btn_postOption.setVisibility(View.VISIBLE);
        }else{
            holder.btn_postOption.setVisibility(View.GONE);
        }

        holder.btn_postOption.setOnClickListener(v->{
            PopupMenu popupMenu = new PopupMenu(context,holder.btn_postOption);
            popupMenu.inflate(R.menu.menu_post_option);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.item_editar: {
                            Intent i = new Intent(((HomeActivity)context), EditarPostActivity.class);
                            //enviar el post id y la posicion a traves del intent
                            i.putExtra("postId",post.getId());
                            i.putExtra("position",position);
                            i.putExtra("text",post.getDescription());
                            context.startActivity(i);
                            return true;
                        }
                        case R.id.item_eliminar:{
                            deletePost(post.getId(),position);
                            return true;
                        }
                    }
                    return false;
                }
            });
            popupMenu.show();

        });

        holder.txt_comentario.setOnClickListener(v->{
            Intent i = new Intent(((HomeActivity)context), CommentActivity.class);
            i.putExtra("postId",post.getId());
            i.putExtra("postPosition",position);
            context.startActivity(i);
        });
        holder.btn_comentario.setOnClickListener(v->{
            Intent i = new Intent(((HomeActivity)context), CommentActivity.class);
            i.putExtra("postId",post.getId());
            i.putExtra("postPosition",position);
            context.startActivity(i);
        });

    }

    private void deletePost(int postId, int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(("Confirmar"));
        builder.setMessage("Eliminar post?");
        builder.setPositiveButton("ELIMINAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StringRequest request = new StringRequest(Request.Method.POST,Constantes.delete_post, response ->{
                    try {
                        JSONObject object = new JSONObject(response);
                        if(object.getBoolean("success")){
                            lista.remove(position);
                            notifyItemRemoved(position);
                            notifyDataSetChanged();
                            listarTodos.clear();
                            listarTodos.addAll(lista);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }, error -> {

                }){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        String token = sharedPreferences.getString("token","");
                        HashMap<String,String> map = new HashMap<>();
                        map.put("Authorization","Bearer "+token );
                        return map;
                    }
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String,String> map = new HashMap<>();
                        map.put("id",postId+"");
                        return map;
                    }
                };
                RequestQueue queue = Volley.newRequestQueue(context);
                queue.add(request);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }
    @Override
    public int getItemCount() {
        return lista.size();
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Post> filteredList = new ArrayList<>();
            if(constraint.toString().isEmpty()){
                filteredList.addAll(listarTodos);
            }else{
                for (Post post : listarTodos){
                    if(post.getDescription().toLowerCase().contains(constraint.toString().toLowerCase())
                     || post.getUser().getUserName().toLowerCase().contains(constraint.toString().toLowerCase())){
                        filteredList.add(post);
                    }
                }
            }
            FilterResults resultados  = new FilterResults();
            resultados.values = filteredList;
            return resultados;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            lista.clear();
            lista.addAll((Collection<? extends  Post>) results.values);
            notifyDataSetChanged();


        }
    };

    public Filter getFilter() {
        return filter;
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
            btn_postOption.setVisibility(View.GONE);
        }
    }


}
