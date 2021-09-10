package com.example.proyecto_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.proyecto_app.Fragments.CuentaFragments;
import com.example.proyecto_app.Fragments.HomeFragments;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private FloatingActionButton fab;
    private BottomNavigationView navigationView;
    private static final int GALLERY_ADD_POST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_homeContainer, new HomeFragments(),HomeFragments.class.getSimpleName()).commit();
        init();
    }
    private void init(){
        navigationView = findViewById(R.id.btn_navegacion);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            startActivityForResult(i,GALLERY_ADD_POST);

        });

       navigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
           @Override
           public void onNavigationItemReselected(@NonNull  MenuItem item) {
               switch (item.getItemId()){
                   case R.id.item_home: {
                       Fragment cuenta = fragmentManager.findFragmentByTag(CuentaFragments.class.getSimpleName());
                       if(cuenta != null){
                           fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(CuentaFragments.class.getSimpleName())).commit();
                           fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag(HomeFragments.class.getSimpleName())).commit();
                       }
                       break;
                   }
                   case R.id.item_cuenta:{
                       Fragment cuenta = fragmentManager.findFragmentByTag(CuentaFragments.class.getSimpleName());
                       fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(HomeFragments.class.getSimpleName())).commit();
                       if(cuenta != null){
                           fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag(CuentaFragments.class.getSimpleName())).commit();
                       }else{
                           fragmentManager.beginTransaction().add(R.id.frame_homeContainer, new CuentaFragments(),  CuentaFragments.class.getSimpleName()).commit();
                       }
                       break;
                   }

               }
           }
       });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_ADD_POST && resultCode == RESULT_OK){
            Uri imgUri = data.getData();
            Intent i = new Intent(HomeActivity.this,AddPostActivity.class);
            i.setData(imgUri);
            startActivity(i);
        }
    }
}