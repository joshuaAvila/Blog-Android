package com.example.proyecto_app.Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.proyecto_app.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SignInFragment extends Fragment {
    private View view;
    private TextInputLayout layoutEmail,layoutPassword;
    private TextInputEditText txtEmail, txtPassword;
    private TextView txtregistrarse;
    private Button btnInicio;
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


        txtregistrarse.setOnClickListener(v->{
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layoutContainer,new SingUpFragment()).commit();
        });

        btnInicio.setOnClickListener(v->{
            if(validate()){

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





}
