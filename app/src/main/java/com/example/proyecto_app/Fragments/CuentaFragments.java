package com.example.proyecto_app.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.proyecto_app.Adapter.CuentaPostAdapter;
import com.example.proyecto_app.Adapter.PostAdapter;
import com.example.proyecto_app.AuthActivity;
import com.example.proyecto_app.Constantes;
import com.example.proyecto_app.EditarUserInfoActivity;
import com.example.proyecto_app.HomeActivity;
import com.example.proyecto_app.Modelo.Post;
import com.example.proyecto_app.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CuentaFragments extends Fragment {

    private View view;
    private MaterialToolbar toolbar;
    private CircleImageView img_perfil;
    private TextView txt_nombre, txt_postCuenta;
    private Button btn_editarCuenta;
    private RecyclerView recyclerView;
    private ArrayList<Post> arrayList;
    private SharedPreferences sharedPreferences;
    private CuentaPostAdapter adapter;
    private String img_url ="";

    public CuentaFragments(){}

    @Nullable

    @Override
    public View onCreateView(@NonNull  LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_cuenta,container,false);
        init();
        return view;
    }

    private void init(){
        sharedPreferences = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        toolbar = view.findViewById(R.id.toolBar_cuenta);
        ((HomeActivity)getContext()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
        img_perfil = view.findViewById(R.id.img_cuentaPerfil);
        txt_nombre = view.findViewById(R.id.txt_cuentaNombre);
        txt_postCuenta = view.findViewById(R.id.txt_cuentaPostCuenta);
        btn_editarCuenta = view.findViewById(R.id.btn_editarCuenta);
        recyclerView = view.findViewById(R.id.recycler_cuenta);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));



        btn_editarCuenta.setOnClickListener(v->{
            Intent i = new Intent(((HomeActivity)getContext()), EditarUserInfoActivity.class);
            i.putExtra("imgUrl",img_url);
            startActivity(i);
        });



    }
    private void getData(){
    arrayList = new ArrayList<>();
        StringRequest request = new StringRequest(Request.Method.GET, Constantes.my_posts,response -> {
            try {
                JSONObject object = new JSONObject(response);
                if(object.getBoolean("success")){
                    JSONArray posts = object.getJSONArray("posts");
                    for(int i = 0 ; i< posts.length();i++){
                        JSONObject p = posts.getJSONObject(i);

                        Post post = new Post();
                        post.setFoto(Constantes.url+"storage/posts/"+p.getString("foto"));
                        arrayList.add(post);
                    }
                    JSONObject user = object.getJSONObject("user");
                    txt_nombre.setText(user.getString("name")+" "+user.getString("apellido"));
                    txt_postCuenta.setText(arrayList.size()+"");
                    Picasso.get().load(Constantes.url+"storage/perfiles/"+user.getString("foto")).into(img_perfil);
                    adapter = new CuentaPostAdapter(getContext(),arrayList);
                    recyclerView.setAdapter(adapter);
                    img_url = Constantes.url+"storage/perfiles/"+user.getString("foto");

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }




        }, error -> {
            error.printStackTrace();

        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("token","");
                HashMap<String,String> map = new HashMap<>();
                map.put("Authorization","Bearer "+token);
                return map;

            }
        };
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);

    }

    @Override
    public void onCreateOptionsMenu(@NonNull  Menu menu, @NonNull  MenuInflater inflater) {
        inflater.inflate(R.menu.menu_cuenta,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull  MenuItem item) {

        switch(item.getItemId()){
            case R.id.item_cerrar_sesion:{
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("DESEAS SALIR?");
                builder.setPositiveButton("SALIR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
                    }
                });
                builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
            }
        }
        return super.onOptionsItemSelected(item);
    }
    private void logout(){
        StringRequest request = new StringRequest(Request.Method.GET,Constantes.logout,response -> {
            try {
                JSONObject object = new JSONObject(response);
                if(object.getBoolean("success")){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();
                    startActivity( new Intent(((HomeActivity)getContext()), AuthActivity.class));
                    ((HomeActivity)getContext()).finish();
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
                map.put("Authorization", "Bearer"+token);
                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        if(!hidden){
            getData();
        }
        super.onHiddenChanged(hidden);
    }
    @Override
    public void onResume() {
        super.onResume();
        getData();
    }
}
