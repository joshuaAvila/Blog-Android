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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignInFragment extends Fragment {
    private View view;
    private TextInputLayout layoutEmail,layoutPassword;
    private TextInputEditText txtEmail, txtPassword;
    private TextView txtregistrarse;
    private Button btnInicio;
    private ProgressDialog dialog;
    public SignInFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull  LayoutInflater inflater, @Nullable  ViewGroup container, @Nullable  Bundle savedInstanceState) {
      view = inflater.inflate(R.layout.layout_sign_in,container,false);
      init();
      return view;
    }
    private void init(){
        layoutPassword = view.findViewById(R.id.txt_LayoutPasswordInicio);
        layoutEmail = view.findViewById(R.id.txt_LayoutemailInicio);
        txtPassword = view.findViewById(R.id.txt_passwordInicio);
        txtregistrarse = view.findViewById(R.id.txt_registrarse);
        txtEmail = view.findViewById(R.id.txt_emailInicio);
        btnInicio = view.findViewById(R.id.btn_iniciarSesion);
        dialog = new ProgressDialog(getContext());
        dialog.setCancelable(false);


        txtregistrarse.setOnClickListener(v->{
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layoutContainer,new SingUpFragment()).commit();
        });

        btnInicio.setOnClickListener(v->{
            if(validate()){
                login();
                //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layoutContainer,new HomeFragment()).commit();
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
    }

    private boolean validate(){

        if(txtEmail.getText().toString().isEmpty()){
            layoutEmail.setErrorEnabled(true);
            layoutEmail.setError("El correo es requerido");
            return false;
        }
        if(txtPassword.getText().toString().length() < 8){
            layoutPassword.setErrorEnabled(true);
            layoutPassword.setError("El Password requiere al menos 8 caracteres");
            return false;
        }

        return true;
    }

    private void login(){
        dialog.setMessage("Iniciando..");
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Constantes.login,response -> {
             try {
                 JSONObject object= new JSONObject(response);
                 if(object.getBoolean("success")){
                     JSONObject user = object.getJSONObject("user");
                     //make shared preference user
                     SharedPreferences userPreference = getActivity().getApplicationContext().getSharedPreferences("user",getContext().MODE_PRIVATE);
                     SharedPreferences.Editor editor = userPreference.edit();
                     editor.putString("token", object.getString("token"));
                     editor.putString("name", user.getString("name"));
                     editor.putString("apellido", user.getString("apellido"));
                     editor.putString("foto", user.getString("foto"));
                     editor.putBoolean("isLoggedIn",true);
                     editor.apply();



                 }
            } catch (JSONException  e) {
                e.printStackTrace();
            }

            dialog.dismiss();
            Toast.makeText(getContext(), "Login Exitoso", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(((AuthActivity)getContext()), HomeActivity.class));
            ((AuthActivity) getContext()).finish();



        }, error -> {
            error.printStackTrace();
            dialog.dismiss();
        }){

           //agregar parametros

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map =new HashMap<>();
                map.put("email",txtEmail.getText().toString().trim());
                map.put("password",txtPassword.getText().toString());
                return map;
            }
        };

        //agregar this request to requestqueue
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }





}
