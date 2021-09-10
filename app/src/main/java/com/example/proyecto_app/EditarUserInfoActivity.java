package com.example.proyecto_app;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditarUserInfoActivity extends AppCompatActivity {

    private TextInputLayout layoutNombre, layoutApellido;
    private TextInputEditText txt_nombre, txt_apellido;
    private TextView txt_seleccionarFoto;
    private Button btn_continuar;
    private CircleImageView img_userinfo;
    private static final int GALLERY_ADD_PROFILE = 1;
    private Bitmap bitmap = null;
    private SharedPreferences userPref ;
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_user_info);

        initt();
    }

    private void initt() {
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        userPref =  getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        layoutNombre = findViewById(R.id.txt_editarlayoutNombreUserinfo);
        layoutApellido = findViewById(R.id.txt_EditarlayoutApellidoUserinfo);
        txt_nombre = findViewById(R.id.txt_editarNombreUserinfo);
        txt_apellido = findViewById(R.id.txt_EditarapellidoUserinfo);
        txt_seleccionarFoto = findViewById(R.id.txt_editarSeleccionarFoto);
        btn_continuar = findViewById(R.id.btn_editarGuardar);
        img_userinfo = findViewById(R.id.img_editarUserInfo);

        Picasso.get().load(getIntent().getStringExtra("imgUrl")).into(img_userinfo);
        txt_nombre.setText(userPref.getString("name",""));
        txt_apellido.setText(userPref.getString("apellido",""));


        txt_seleccionarFoto.setOnClickListener(v->{
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            startActivityForResult(i,GALLERY_ADD_PROFILE);

        });

        btn_continuar.setOnClickListener(v->{
            if(validate()){
                actualizarPerfil();
            }
        });
    }

    private void actualizarPerfil() {
        dialog.setMessage("Actualizando..");
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Constantes.save_userInfo,response -> {
            try {
                JSONObject object = new JSONObject(response);
                if(object.getBoolean("success")){
                    SharedPreferences.Editor editor = userPref.edit();
                    editor.putString("name",txt_nombre.getText().toString().trim());
                    editor.putString("apellido",txt_apellido.getText().toString().trim());
                    editor.apply();
                    finish();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Toast.makeText(this, "Perfil Actualizado", Toast.LENGTH_SHORT).show();
            dialog.dismiss();



        },error -> {
            error.printStackTrace();
            dialog.dismiss();
        }){


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = userPref.getString("token","");
                HashMap<String,String> map = new HashMap<>();
                map.put("Authorization","Bearer "+token);
                return map;
            }

            //add parametros

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("name",txt_nombre.getText().toString().trim());
                map.put("apellido",txt_apellido.getText().toString().trim());
                map.put("foto",bitmapToString(bitmap));
                return map;
            }

        };
        RequestQueue queue = Volley.newRequestQueue(EditarUserInfoActivity.this);
        queue.add(request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_ADD_PROFILE && resultCode==RESULT_OK ){
            Uri imgUri= data.getData();
            img_userinfo.setImageURI(imgUri);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imgUri);
            }catch ( IOException e){
                e.printStackTrace();
            }

        }
    }

    private boolean validate(){
        if(txt_nombre.getText().toString().isEmpty()){
            layoutNombre.setErrorEnabled(true);
            layoutNombre.setError("El Nombre es requerido");
            return false;
        }
        if(txt_apellido.getText().toString().isEmpty()){
            layoutApellido.setErrorEnabled(true);
            layoutApellido.setError("El Apellido es requerido");
            return false;
        }

        return  true;
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
}