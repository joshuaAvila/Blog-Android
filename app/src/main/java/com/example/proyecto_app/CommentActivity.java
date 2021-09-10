package com.example.proyecto_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.proyecto_app.Adapter.ComentarioAdapter;
import com.example.proyecto_app.Fragments.HomeFragments;
import com.example.proyecto_app.Modelo.Comentarios;
import com.example.proyecto_app.Modelo.Post;
import com.example.proyecto_app.Modelo.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CommentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Comentarios> lista;
    private ComentarioAdapter adapter;
    private int postId = 0;
    public  static int postPosition=0;
    private SharedPreferences sharedPreferences;
    private EditText txt_addcomentario;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        init();
    }
    private void init(){
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        postPosition = getIntent().getIntExtra("postPosition",-1);
        sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        recyclerView = findViewById(R.id.recycler_comentarios);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postId = getIntent().getIntExtra("postId",0);
        txt_addcomentario = findViewById(R.id.txt_addComentario);
        getComentarios();
    }
    private void getComentarios(){
        lista = new ArrayList<>();
        StringRequest request = new StringRequest(Request.Method.POST,Constantes.comentarios,response -> {

            try {
                JSONObject object = new JSONObject(response);
                if(object.getBoolean("success")){
                    JSONArray comentarios = new JSONArray(object.getString("comments"));
                    for(int i = 0; i < comentarios.length();i++){
                        JSONObject comentario = comentarios.getJSONObject(i);
                        JSONObject user = comentario.getJSONObject("user");

                        User nUser = new User();
                        nUser.setId(user.getInt("id"));
                        nUser.setFoto(Constantes.url+"storage/perfiles/"+user.getString("foto"));
                        nUser.setUserName(user.getString("name")+" "+user.getString("apellido"));

                        Comentarios nComentarios = new Comentarios();
                        nComentarios.setId(comentario.getInt("id"));
                        nComentarios.setUser(nUser);
                        nComentarios.setFecha(comentario.getString("created_at"));
                        nComentarios.setComentario(comentario.getString("comentario"));
                        lista.add(nComentarios);
                    }
                }
                adapter  = new ComentarioAdapter(this,lista);
                recyclerView.setAdapter(adapter);

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
                map.put("id",postId+"");
                return map;
            }

        };
        RequestQueue queue = Volley.newRequestQueue(CommentActivity.this);
        queue.add(request);
    }

    public void irAtras(View view){
        super.onBackPressed();
    }
        //AGREGA COMENTARIOS
    public void agregarComentario(View view) {
        String comentarioText = txt_addcomentario.getText().toString();
        dialog.setMessage("Agregando Comentario");
        dialog.show();
        if(comentarioText.length()>0){
            StringRequest request = new StringRequest(Request.Method.POST,Constantes.create_comentarios,response -> {

                try {
                    JSONObject object = new JSONObject(response);
                    if(object.getBoolean("success")){
                        JSONObject comentario = object.getJSONObject("comentario");
                        JSONObject user = comentario.getJSONObject("user");

                        Comentarios c = new Comentarios();
                        User u = new User();
                        u.setId(user.getInt("id"));
                        u.setUserName(user.getString("name")+" "+user.getString("apellido"));
                        u.setFoto(Constantes.url+"storage/perfiles/"+user.getString("foto"));
                        c.setUser(u);
                        c.setId(comentario.getInt("id"));
                        c.setFecha(comentario.getString("created_at"));
                        c.setComentario(comentario.getString("comentario"));


                        Post post = HomeFragments.arrayList.get(postPosition);
                        post.setComentarios(post.getComentarios()+1);
                        HomeFragments.arrayList.set(postPosition,post);
                        HomeFragments.recyclerView.getAdapter().notifyDataSetChanged();

                        lista.add(c);
                        recyclerView.getAdapter().notifyDataSetChanged();
                        txt_addcomentario.setText("");

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
                    map.put("id",postId+"");
                    map.put("comentario",comentarioText);
                    return map;
                }


            };
            RequestQueue queue = Volley.newRequestQueue(CommentActivity.this);
            queue.add(request);
        }
    }
}