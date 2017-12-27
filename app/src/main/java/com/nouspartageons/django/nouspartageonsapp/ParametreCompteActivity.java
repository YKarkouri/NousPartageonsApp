package com.nouspartageons.django.nouspartageonsapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ParametreCompteActivity extends AppCompatActivity {

    //db
    private FirebaseUser current_user;
    private DatabaseReference dbRef;

    //layout
    private TextView mName;
    private CircleImageView mImage;
    private TextView mStatus;

    Button changeStatusBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parametre_compte);

        mName = (TextView) findViewById(R.id.compte_name);
        mStatus = (TextView) findViewById(R.id.compte_status);
        mImage = (CircleImageView) findViewById(R.id.compte_image);

        changeStatusBtn = (Button) findViewById(R.id.change_status_btn);

        changeStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        current_user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = current_user.getUid();

        dbRef = FirebaseDatabase.getInstance().getReference().child("Utilisateurs").child(uid);

        // Read from the database
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                mName.setText(name);
                mStatus.setText(status);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                //La lecture a echouée
                Log.w("Erreur DB", "La lecture a echouée", error.toException());
            }
        });

    }
}
