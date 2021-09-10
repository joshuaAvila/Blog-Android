package com.example.proyecto_app.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.proyecto_app.AuthActivity;
import com.example.proyecto_app.Constantes;
import com.example.proyecto_app.HomeActivity;
import com.example.proyecto_app.R;
import com.example.proyecto_app.UserInfoActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class SingUpFragment  extends Fragment {
    private View view;
    private TextInputLayout layoutEmail,layoutPassword, layoutConfirm;
    private TextInputEditText txtEmail, txtPassword, txtConfirm;
    private TextView txtIniciarSesion;
    private Button  btnRegistro;
    private ProgressDialog dialog;

    public SingUpFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_sign_up,container,false);
        init();
        return view;
    }

    private void init(){
        layoutPassword = view.findViewById(R.id.txt_LayoutPasswordRegistro);
        layoutEmail = view.findViewById(R.id.txt_LayoutEmailRegistro);
        layoutConfirm = view.findViewById(R.id.txt_LayoutConfirmPassword);
        txtConfirm = view.findViewById(R.id.txt_Confirmpassword);
        txtPassword = view.findViewById(R.id.txt_passwordRegistro);
        txtIniciarSesion = view.findViewById(R.id.txt_iniciarSesion);
        txtEmail = view.findViewById(R.id.txt_emailRegistro);
        btnRegistro = view.findViewById(R.id.btn_registro);
        dialog = new ProgressDialog(getContext());
        dialog.setCancelable(false);

        txtIniciarSesion.setOnClickListener(v->{
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layoutContainer,new SignInFragment()).commit();
        });

        btnRegistro.setOnClickListener(v->{
            if(validate()){
                register();
            }
        });

        txtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!txtEmail.getText().toString().isEmpty()){
                    layoutEmail.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(txtPassword.getText().toString().length() > 7){
                    layoutPassword.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(txtConfirm.getText().toString().equals(txtPassword.getText().toString())){
                    layoutConfirm.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private boolean validate(){

        if(txtEmail.getText().toString().isEmpty()){
            layoutEmail.setErrorEnabled(true);
            layoutEmail.setError("El correo es requerido");
            return false;
        }
        if(txtPassword.getText().toString().length()<8){
            layoutPassword.setErrorEnabled(true);
            layoutPassword.setError("El Password requiere al menos 8 caracteres");
            return false;
        }
        if(!txtConfirm.getText().toString().equals(txtPassword.getText().toString())){
            layoutConfirm.setErrorEnabled(true);
            layoutConfirm.setError("Las contraseñas no son iguales");
            return false;
        }

        return true;
    }

    private void register(){
        dialog.setMessage("Registering");
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Constantes.register, response -> {
            try {
                JSONObject object= new JSONObject(response);
                if(object.getBoolean("success")){
                    JSONObject user = object.getJSONObject("user");
                    //make shared preference user
                    SharedPreferences userPreference = getActivity().getApplicationContext().getSharedPreferences("user",getContext().MODE_PRIVATE);
                    SharedPreferences.Editor editor = userPreference.edit();
                    editor.putString("token", object.getString("token"));
                    editor.putInt("id", user.getInt("id"));
                    editor.putString("name", user.getString("name"));
                    editor.putString("apellido", user.getString("apellido"));
                    editor.putString("foto", user.getString("foto"));
                    editor.putBoolean("isLoggedIn",true);
                    editor.apply();



                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            dialog.dismiss();

            Toast.makeText(getContext(), "Registrado Exitosamente", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(((AuthActivity)getContext()), UserInfoActivity.class));
            ((AuthActivity) getContext()).finish();



        }, error -> {
            error.printStackTrace();
            dialog.dismiss();
        }){
            //agregar parametros
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("email",txtEmail.getText().toString().trim());
                map.put("password",txtPassword.getText().toString());
                return map;
            }
        };

        //agregar this request to request queue
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }
}

