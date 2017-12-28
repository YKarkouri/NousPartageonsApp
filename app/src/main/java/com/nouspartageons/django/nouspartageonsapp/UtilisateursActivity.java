package com.nouspartageons.django.nouspartageonsapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UtilisateursActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private RecyclerView listUsers;

    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_utilisateurs);

        mToolbar = (Toolbar) findViewById(R.id.users_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Liste des utilisateurs");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listUsers = (RecyclerView) findViewById(R.id.users_liste);
        listUsers.setHasFixedSize(true);
        listUsers.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();
        dbRef = FirebaseDatabase.getInstance().getReference().child("Utilisateurs");

        FirebaseRecyclerAdapter<Utilisateur, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Utilisateur, UsersViewHolder>(
                Utilisateur.class,
                R.layout.users_single_layout,
                UsersViewHolder.class,
                dbRef) {

            @Override
            protected void populateViewHolder(UsersViewHolder usersViewHolder, Utilisateur users, int pos) {

                usersViewHolder.setDisplayName(users.getName());
                usersViewHolder.setUserStatus(users.getStatus());
                usersViewHolder.setUserImage(users.getImage(), getApplicationContext());

                final String user_id = getRef(pos).getKey();

                usersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent profileIntent = new Intent(UtilisateursActivity.this, ProfileActivity.class);
                        profileIntent.putExtra("user_id", user_id);
                        startActivity(profileIntent);

                    }
                });

            }
        };

        listUsers.setAdapter(firebaseRecyclerAdapter);
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setDisplayName(String name){
            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);
        }

        public void setUserStatus(String status){
            TextView userStatusView = (TextView) mView.findViewById(R.id.user_single_status);
            userStatusView.setText(status);
        }

        public void setUserImage(String thumb_image, Context ctx){
            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_single_image);
            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.user).into(userImageView);
        }

    }
}