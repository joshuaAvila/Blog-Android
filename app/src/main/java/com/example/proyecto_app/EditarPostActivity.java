package com.example.proyecto_app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.proyecto_app.Fragments.HomeFragments;
import com.example.proyecto_app.Modelo.Post;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class EditarPostActivity extends AppCompatActivity {

    private int position = 0, id= 0;
    private EditText txt_description;
    private Button btn_guardar;
    private ProgressDialog dialog;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_post);
        init();
    }
    private  void init(){
        sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        txt_description = findViewById(R.id.txt_postDescriptionEditPost);
        btn_guardar = findViewById(R.id.btn_EditPost);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        position = getIntent().getIntExtra("position",0);
        id = getIntent().getIntExtra("postId",0);
        txt_description.setText(getIntent().getStringExtra("text"));

        btn_guardar.setOnClickListener(v->{
            if(!txt_description.getText().toString().isEmpty()){
                guardarPost();
            }
        });
    }

    private void guardarPost(){
        dialog.setMessage("Guardando..");
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Constantes.update_post,response -> {
            try {
                JSONObject object = new JSONObject(response);
                if(object.getBoolean("success")){
                    //Actualizar el post en el recyclerView
                    Post post = HomeFragments.arrayList.get(position);
                    post.setDescription(txt_description.getText().toString());
                    HomeFragments.arrayList.set(position,post);
                    HomeFragments.recyclerView.getAdapter().notifyItemChanged(position);
                    HomeFragments.recyclerView.getAdapter().notifyDataSetChanged();
                    finish();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        },error -> {

        }){
            //agregar token al header
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
                map.put("id",id+"");
                map.put("description", txt_description.getText().toString());
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(EditarPostActivity.this);
        queue.add(request);
    }
    public void cancelarEdicion(View view){
        super.onBackPressed();
    }
}