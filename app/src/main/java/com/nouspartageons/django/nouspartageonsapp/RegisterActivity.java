package com.nouspartageons.django.nouspartageonsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout mName;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private Button mValiderBtn;

    private Toolbar rToolbar;
    private ProgressDialog mProgress;

    //Firbase Fields
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mProgress = new ProgressDialog(this);

        mName = (TextInputLayout) findViewById(R.id.reg_name);
        mEmail = (TextInputLayout) findViewById(R.id.reg_email);
        mPassword = (TextInputLayout) findViewById(R.id.reg_password);
        mValiderBtn = (Button) findViewById(R.id.reg_btn);



        rToolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(rToolbar);
        getSupportActionBar().setTitle("Création d'un compte");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mValiderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = mName.getEditText().getText().toString();
                String email = mEmail.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();

                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) ) {
                    mProgress.setTitle("Enregistrement de l'utilisateur");
                    mProgress.setMessage("Veuillez patienter pendant que nous créons votre compte");
                    mProgress.setCanceledOnTouchOutside(false);
                    mProgress.show();
                    register_user(name, email, password);
                }


            }
        });
    }

    //Ajout d'un utilistauer dans la base de donnees Firebase
    private void register_user(final String name, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = current_user.getUid();

                            mDb = FirebaseDatabase.getInstance();
                            DatabaseReference dbRef = mDb.getReference().child("Utilisateurs").child(uid);

                            HashMap<String, String> utilisateurs = new HashMap<String, String>();
                            utilisateurs.put("name", name);
                            utilisateurs.put("status", "Bonjour, J'utilise NousPartageons App");
                            utilisateurs.put("image", "default");
                            utilisateurs.put("thumb_image", "default");

                            dbRef.setValue(utilisateurs).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        mProgress.dismiss();

                                        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);

                                        //Fermer les task precedents
                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(mainIntent);
                                        finish();
                                    }
                                }
                            });


                           /*
                            */
                        } else {
                            mProgress.hide();
                            // Si la connexion échoue, affichez un message à l'utilisateur.
                            Toast.makeText(RegisterActivity.this, "Impossible de s'inscrire Veuillez vérifier le formulaire et réessayer.",
                                    Toast.LENGTH_SHORT).show();
                            Log.e("Task erreur", task.getException().toString());
                        }

                    }
                });
    }
}
