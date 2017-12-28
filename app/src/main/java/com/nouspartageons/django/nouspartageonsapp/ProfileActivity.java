package com.nouspartageons.django.nouspartageonsapp;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;


public class ProfileActivity extends AppCompatActivity {

    private ImageView mProfileImage;
    private TextView mProfileName, mProfileStatus, mProfileFriendsCount;
    private Button mProfileSendReqBtn, mDeclineBtn;

    private DatabaseReference dbRef;

    private ProgressDialog mProgressDialog;

    private DatabaseReference mFriendReqDatabase;
    private DatabaseReference mFriendDatabase;

    private FirebaseUser mCurr_user;

    private String mCurr_state;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String uid = getIntent().getStringExtra("user_id");

        dbRef = FirebaseDatabase.getInstance().getReference().child("Utilisateurs").child(uid);
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mCurr_user = FirebaseAuth.getInstance().getCurrentUser();

        //lAYOUT
        mProfileImage = (ImageView) findViewById(R.id.profile_image);
        mProfileName = (TextView) findViewById(R.id.profile_name);
        mProfileStatus = (TextView) findViewById(R.id.profile_status);
        mProfileFriendsCount = (TextView) findViewById(R.id.profile_totalFriends);
        mProfileSendReqBtn = (Button) findViewById(R.id.profile_send_req_btn);
        mDeclineBtn = (Button) findViewById(R.id.profile_decline_btn);

        mCurr_state = "not_friends";

        //Progress Dialog
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Chargement de données");
        mProgressDialog.setMessage("Veuillez patienter");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image =dataSnapshot.child("image").getValue().toString();

                mProfileName.setText(name);
                mProfileStatus.setText(status);

                Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.user).into(mProfileImage);
                mProgressDialog.dismiss();
                //--------------- La liste des amis / Invitations-----
                mFriendReqDatabase.child(mCurr_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(uid)) {

                            String req_type = dataSnapshot.child(uid).child("request_type").getValue().toString();

                            if (req_type.equals("received")) {

                                mCurr_state = "req_received";
                                mProfileSendReqBtn.setText("ACCEPTER LA DEMANDE");

                                mDeclineBtn.setVisibility(View.VISIBLE);
                                mDeclineBtn.setEnabled(true);


                            } else if (req_type.equals("sent")) {

                                mCurr_state = "req_sent";
                                mProfileSendReqBtn.setText("ANNULER LA DEMANDE");

                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                mDeclineBtn.setEnabled(false);

                            }


                        } else {
                            mFriendDatabase.child(mCurr_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(uid)) {
                                        mCurr_state = "friends";
                                        mProfileSendReqBtn.setText("RETIRER DE LA LISTE DES AMIS");
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    mProgressDialog.dismiss();
                                }
                            });
                        }

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        mProgressDialog.dismiss();

                    }
                });


                //Si l user est le user courant   disactiver les boutons d invitations
                if(mCurr_user.getUid().equals(uid)){

                    mDeclineBtn.setEnabled(false);
                    mDeclineBtn.setVisibility(View.INVISIBLE);

                    mProfileSendReqBtn.setEnabled(false);
                    mProfileSendReqBtn.setVisibility(View.INVISIBLE);
                }


                mProfileSendReqBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mProfileSendReqBtn.setEnabled(false);

                        //L'ENVOI D UNE INNVITATION
                        if (mCurr_state.equals("not_friends")) {
                            mFriendReqDatabase.child(mCurr_user.getUid()).child(uid).child("request_type").setValue("sent")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        mCurr_state = "req_sent";
                                        mProfileSendReqBtn.setText("ANNULER L'INVITATION");

                                        mFriendReqDatabase.child(uid).child(mCurr_user.getUid()).child("request_type")
                                                .setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(ProfileActivity.this, "L'invitation est envoyée avec succée", Toast.LENGTH_SHORT)
                                                        .show();
                                            }
                                        });
                                    }
                                    else {
                                        Toast.makeText(ProfileActivity.this, "Impossible d'envoyer l'invitation veuillez réessayer.", Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                }
                            });

                            mProfileSendReqBtn.setEnabled(true);
                        }

                        //ANNULER L'INNVITATION
                        if (mCurr_state.equals("req_sent")) {

                            mFriendReqDatabase.child(mCurr_user.getUid()).child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mFriendReqDatabase.child(uid).child(mCurr_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            mCurr_state = "not_friends";
                                            mProfileSendReqBtn.setText("ENVOYER INVITATION");

                                            Toast.makeText(ProfileActivity.this, "L'invitation est annulée avec succée", Toast.LENGTH_SHORT)
                                                    .show();
                                        }
                                    });
                                    mProfileSendReqBtn.setEnabled(true);
                                }
                            });

                        }

                        //ACCEPTER INVITATION
                        if (mCurr_state.equals("req_received")) {

                            final String currentDate = DateFormat.getDateTimeInstance().format(new Date());
                            mFriendDatabase.child(mCurr_user.getUid()).child(uid).setValue(currentDate)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                mFriendDatabase.child(uid).child(mCurr_user.getUid()).setValue(currentDate)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                mFriendReqDatabase.child(mCurr_user.getUid()).child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        mFriendReqDatabase.child(uid).child(mCurr_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {

                                                                                mCurr_state = "friends";
                                                                                mProfileSendReqBtn.setText("RETIRER DE LA LISTE DES AMIS");

                                                                            }
                                                                        });
                                                                        mProfileSendReqBtn.setEnabled(true);
                                                                    }
                                                                });
                                                            }
                                                        }
                                                );
                                            }
                                        }
                                    }
                            );
                        }

                        //RETIRER DE LA LISTE DES AMIS
                        if (mCurr_state.equals("friends")) {

                            mFriendDatabase.child(mCurr_user.getUid()).child(uid).removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            mFriendDatabase.child(uid).child(mCurr_user.getUid()).removeValue()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                            mCurr_state = "not_friends";
                                                            mProfileSendReqBtn.setText("ENVOYER INVITATION");

                                                        }
                                                    });
                                            mProfileSendReqBtn.setEnabled(true);
                                        }
                                    });

                        }


                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //La lecture a echouée
                Log.w("Erreur DB", "La lecture a echouée", databaseError.toException());
            }
        });
    }
}
