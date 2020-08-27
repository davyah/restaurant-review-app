package com.example.user.restaurantreviewapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.user.restaurantreviewapp.customfonts.EditText_Roboto_Regular;
import com.example.user.restaurantreviewapp.customfonts.MyTextView_Roboto_Regular;
import com.example.user.restaurantreviewapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @BindView(R.id.email)
    EditText_Roboto_Regular emailText;

    @BindView(R.id.pass)
    EditText_Roboto_Regular passwordText;

    @BindView(R.id.forgot_password)
    MyTextView_Roboto_Regular forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        ButterKnife.bind(this);

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performRecovery();
            }
        });
    }

    private void performRecovery() {
//        mAuth.sendPasswordResetEmail(user.getEmail());
//        Toast.makeText(this, getResources().getString(R.string.email_sent), Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.login) void login(){
        if(!emailText.getText().toString().isEmpty()&&!passwordText.getText().toString().isEmpty())
            mAuth.signInWithEmailAndPassword(emailText.getText().toString(),passwordText.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }

    @OnClick(R.id.btn_sign_up)  void signUp(){
        startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
    }
}
