package com.example.proyecto_app;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.proyecto_app.Fragments.HomeFragments;
import com.example.proyecto_app.Modelo.Post;
import com.example.proyecto_app.Modelo.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddPostActivity extends AppCompatActivity {

    private Button btn_addPost;
    private ImageView img_addPost;
    private EditText txt_descriptionAdd;
    private Bitmap bitmap = null;
    private static final int GALLERY_CHANGE_POST = 3;
    private ProgressDialog dialog;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        init();
    }
    private void init(){
        sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        btn_addPost = findViewById(R.id.btn_addPost);
        img_addPost = findViewById(R.id.img_addPost);
        txt_descriptionAdd = findViewById(R.id.txt_postDescriptionAddPost);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);

        img_addPost.setImageURI(getIntent().getData());
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),getIntent().getData());
        } catch (IOException e) {
            e.printStackTrace();
        }

        btn_addPost.setOnClickListener( v -> {
            if(!txt_descriptionAdd.getText().toString().isEmpty()){
                post();
//                notificaciones();
            }else{
                Toast.makeText(this, "La description del post es requerida", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void post(){
         dialog.setMessage("Publicando..");
         dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST,Constantes.create_post,response -> {
            try {
                JSONObject object = new JSONObject(response);
                if(object.getBoolean("success")){
                    JSONObject postObject = object.getJSONObject("post");
                    JSONObject userObject = postObject.getJSONObject("user");

                    User user = new User();
                    user.setId(userObject.getInt("id"));
                    user.setUserName(userObject.getString("name")+" "+ userObject.getString("apellido"));
                    user.setFoto(userObject.getString("foto"));

                    Post post = new Post();
                    post.setId(postObject.getInt("id"));
                    post.setSelfLike(false);
                    post.setFoto(postObject.getString("foto"));
                    post.setDescription(postObject.getString("description"));
                    post.setComentarios(0);
                    post.setLikes(0);
                    post.setFecha(postObject.getString("created_at"));

                    HomeFragments.arrayList.add(0,post);
                    HomeFragments.recyclerView.getAdapter().notifyItemInserted(0);
                    HomeFragments.recyclerView.getAdapter().notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            dialog.dismiss();
            startActivity( new Intent( AddPostActivity.this, HomeActivity.class));
            finish();
            notificaciones();
        }, error -> {
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
                map.put("description", txt_descriptionAdd.getText().toString().trim());
                map.put("foto",bitmapToString(bitmap));
                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(AddPostActivity.this);
        queue.add(request);
    }

    private String bitmapToString(Bitmap bitmap){
        if(bitmap != null){
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
            byte[] array = byteArrayOutputStream.toByteArray();
           return Base64.encodeToString(array,Base64.DEFAULT);
        }
        return "";
    }
    public void cancelPost(View view){
        super.onBackPressed();
    }

    public  void cambiarFoto(View view){
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i,GALLERY_CHANGE_POST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_CHANGE_POST && resultCode == RESULT_OK){
            Uri imgUri = data.getData();
            img_addPost.setImageURI(imgUri);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),getIntent().getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void notificaciones() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "tutorialspoint_01";
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel;
            notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notificación",NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("Has Publicado algo");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{0,1000,1000,500});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.iconoblogapp)
                .setTicker("Publicación")
                .setLights(Color.MAGENTA,1000,1000)
                .setContentTitle("Notificación")
                .setContentText("Has subido una nueva publicación")
                .setContentInfo("information");
        notificationManager.notify(1, notificationBuilder.build());
    }
}