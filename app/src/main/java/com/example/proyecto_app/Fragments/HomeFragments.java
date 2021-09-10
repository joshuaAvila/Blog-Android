package com.example.proyecto_app.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.proyecto_app.Adapter.PostAdapter;
import com.example.proyecto_app.Constantes;
import com.example.proyecto_app.HomeActivity;
import com.example.proyecto_app.Modelo.Post;
import com.example.proyecto_app.Modelo.User;
import com.example.proyecto_app.R;
import com.google.android.material.appbar.MaterialToolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeFragments extends Fragment {
    private View view;
    public static RecyclerView recyclerView;
    public static ArrayList<Post> arrayList;
    private SwipeRefreshLayout refreshLayout;
    private PostAdapter postAdapter;
    private MaterialToolbar toolbar;
    private SharedPreferences sharedPreferences;


    public HomeFragments(){}


    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_home,container,false);
        init();
        return view;
    }

    private void init(){
        sharedPreferences = getContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        recyclerView = view.findViewById(R.id.recycler_home);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        refreshLayout = view.findViewById(R.id.swipe_home);
        toolbar =  view.findViewById(R.id.toolBar_menu);
        ((HomeActivity)getContext()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);

        getPost();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPost();
            }
        });
    }

    private void getPost() {
        arrayList = new ArrayList<>();
        refreshLayout.setRefreshing(true);

        StringRequest request = new StringRequest(Request.Method.GET, Constantes.posts, response -> {

            try {
                JSONObject object = new JSONObject(response);
                    if(object.getBoolean("success")){
                        JSONArray array = new JSONArray(object.getString("posts"));
                        for(int i = 0;  i < array.length(); i++){
                            JSONObject postObject = array.getJSONObject(i);
                            JSONObject userObject =  postObject.getJSONObject("user");

                            User user = new User();
                            user.setId(userObject.getInt("id"));
                            user.setUserName(userObject.getString("name")+ " "+userObject.getString("apellido"));
                            user.setFoto(userObject.getString("foto"));

                            Post post = new Post();
                            post.setId(postObject.getInt("id"));
                            post.setUser(user);
                            post.setLikes(postObject.getInt("likesCount"));
                            post.setComentarios(postObject.getInt("commentsCount"));
                            post.setDescription(postObject.getString("description"));
                            post.setFecha(postObject.getString("created_at"));
                            post.setFoto(postObject.getString("foto"));
                            post.setSelfLike(postObject.getBoolean("selfLike"));

                            arrayList.add(post);

                        }
                        postAdapter = new PostAdapter(getContext(),arrayList);
                        recyclerView.setAdapter(postAdapter);
                    }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            refreshLayout.setRefreshing(false);

        }, error -> {

            error.printStackTrace();
            refreshLayout.setRefreshing(false);
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
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull  MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search,menu);
        MenuItem item = menu.findItem(R.id.buscar);
        SearchView searchView = (SearchView)item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                postAdapter.getFilter().filter(newText);
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }
}
