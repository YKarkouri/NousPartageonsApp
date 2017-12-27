package com.nouspartageons.django.nouspartageonsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private Toolbar mStatusBar;

    private Button mUpdateStatusBtn;

    //Layout
    private TextInputLayout mStatus;

    //Dialogue de progresse
    ProgressDialog mProgress;

    //Firebase
    private FirebaseUser curr_user;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        //toolbar status
        mStatusBar = (Toolbar) findViewById(R.id.status_bar);
        setSupportActionBar(mStatusBar);
        getSupportActionBar().setTitle("Editer status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Setup the dialogprogress
        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Sauvegarde de changements");
        mProgress.setMessage("Veuillez patienter pendant que le status soit modifié!");
        mProgress.setCanceledOnTouchOutside(false);

        //layout
        mStatus = (TextInputLayout) findViewById(R.id.status_input);

        //Initialisation du status
        String status_value = getIntent().getStringExtra("status_value");
        mStatus.getEditText().setText(status_value);

        //Firebase
        curr_user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = curr_user.getUid().toString();

        dbRef = FirebaseDatabase.getInstance().getReference().child("Utilisateurs").child(uid);

        //update button
        mUpdateStatusBtn = (Button) findViewById(R.id.status_update_btn);

        mUpdateStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mProgress.show();

                String status = mStatus.getEditText().getText().toString();
                dbRef.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mProgress.dismiss();
                            Intent statIntent = new Intent(StatusActivity.this, ParametreCompteActivity.class);
                            startActivity(statIntent);
                            finish();

                        }
                        else {
                            mProgress.hide();
                            Toast.makeText(getApplicationContext(), "Il y une erreur lors de la sauvegarde!", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });



    }
}
