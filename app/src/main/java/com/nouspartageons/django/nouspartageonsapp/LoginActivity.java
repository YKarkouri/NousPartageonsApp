package com.nouspartageons.django.nouspartageonsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private TextInputLayout mLoginEmail;
    private TextInputLayout mLoginPassword;

    private Button mLogin_btn;

    private ProgressDialog mLoginProgress;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Instaciation de mAuth
        mAuth = FirebaseAuth.getInstance();

        //LoginToolbar
        mToolbar = findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Connexion");

        //LoginProgress
        mLoginProgress = new ProgressDialog(this);
        mLoginProgress.setTitle("Se connecter");
        mLoginProgress.setMessage("Veuillez patienter pendant que nous vérifions votre authentification");
        mLoginProgress.setCanceledOnTouchOutside(false);


        //LoginFields
        mLogin_btn = (Button) findViewById(R.id.login_btn);
        mLoginEmail = (TextInputLayout) findViewById(R.id.login_email);
        mLoginPassword = (TextInputLayout) findViewById(R.id.l_login_password);

        mLogin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mLoginEmail.getEditText().getText().toString();
                String password = mLoginPassword.getEditText().getText().toString();

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                    mLoginProgress.show();
                    loginUser(email, password);
                }

            }
        });


    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Connexion réussie
                            mLoginProgress.dismiss();
                            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);

                            // Fermer tous les activités precedente
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                            finish();
                        } else {
                            // Si la connexion échoue, affichez un message à l'utilisateur.
                            mLoginProgress.hide();
                            Toast.makeText(LoginActivity.this, "Impossible de se connecter Veuillez vérifier le formulaire ou la connexion Internet et réessayer.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
}
