package com.example.proyecto_app.Adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.proyecto_app.CommentActivity;
import com.example.proyecto_app.Constantes;
import com.example.proyecto_app.Fragments.HomeFragments;
import com.example.proyecto_app.Modelo.Comentarios;
import com.example.proyecto_app.Modelo.Post;
import com.example.proyecto_app.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ComentarioAdapter extends RecyclerView.Adapter<ComentarioAdapter.ComentarioHolder>{

    private Context context ;
    private ArrayList<Comentarios> lista;
    private SharedPreferences sharedPreferences;
    private ProgressDialog dialog;

    public ComentarioAdapter(Context context, ArrayList<Comentarios> lista) {
        this.context = context;
        this.lista = lista;
        dialog = new ProgressDialog(context);
        dialog.setCancelable(false);
        sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ComentarioHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_comentarios,parent,false);
        return new ComentarioHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ComentarioAdapter.ComentarioHolder holder, int position) {
        Comentarios comentarios = lista.get(position);
        Picasso.get().load(comentarios.getUser().getFoto()).into(holder.imgPerfil);
        holder.txt_nombre.setText(comentarios.getUser().getUserName());
        holder.txt_fecha.setText(comentarios.getFecha());
        holder.txt_comentario.setText(comentarios.getComentario());

        if(sharedPreferences.getInt("id",0)!=comentarios.getUser().getId()){
            holder.btn_eliminar.setVisibility(View.GONE);
        }else{
            holder.btn_eliminar.setVisibility(View.VISIBLE);
            holder.btn_eliminar.setOnClickListener(v->{
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Estas Seguro?");
                builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        eliminarComenario(comentarios.getId(),position);
                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            });
        }
    }

    private void eliminarComenario(int comentarioId, int position){
        dialog.setMessage("Eliminando comentario");
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Constantes.delete_comentarios,response -> {

            try {
                JSONObject object = new JSONObject(response);
                if(object.getBoolean("success")){
                    lista.remove(position);
                    Post post = HomeFragments.arrayList.get(CommentActivity.postPosition);
                    post.setComentarios(post.getComentarios()-1);
                    HomeFragments.arrayList.set(CommentActivity.postPosition,post);
                    HomeFragments.recyclerView.getAdapter().notifyDataSetChanged();
                    notifyDataSetChanged();


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            dialog.dismiss();

        },error -> {
            error.printStackTrace();
            dialog.dismiss();
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
                map.put("id",comentarioId+"");
                return map;
            }

        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    class ComentarioHolder extends RecyclerView.ViewHolder{
        private CircleImageView imgPerfil;
        private TextView txt_nombre, txt_fecha,txt_comentario;
        private ImageButton btn_eliminar;

        public ComentarioHolder(@NonNull  View itemView) {
            super(itemView);

            imgPerfil = itemView.findViewById(R.id.img_perfilComentario);
            txt_nombre = itemView.findViewById(R.id.txt_comentarioNombre);
            txt_comentario = itemView.findViewById(R.id.txt_comentarioTexto);
            txt_fecha = itemView.findViewById(R.id.txt_postFecha);
            btn_eliminar = itemView.findViewById(R.id.btn_eliminarComentario);
        }
    }

}
