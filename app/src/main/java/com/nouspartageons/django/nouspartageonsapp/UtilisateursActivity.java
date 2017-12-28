package com.nouspartageons.django.nouspartageonsapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class UtilisateursActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    ProgressDialog mProgressDialog;

    private RecyclerView listUsers;

    private DatabaseReference dbRef;

    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_utilisateurs);

        mToolbar = (Toolbar) findViewById(R.id.users_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Liste des utilisateurs");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbRef = FirebaseDatabase.getInstance().getReference().child("Utilisateurs");

        //Progress Dialog
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Chargement des utilisateurs");
        mProgressDialog.setMessage("Veuillez patienter");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        listUsers = (RecyclerView) findViewById(R.id.users_liste);
        listUsers.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        listUsers.setLayoutManager(mLayoutManager);

    }

    @Override
    protected void onStart() {
        super.onStart();

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
                }
                );
            }
            @Override
            public void onBindViewHolder(UsersViewHolder viewHolder, int position) {
                Utilisateur users = getItem(position);
                super.onBindViewHolder(viewHolder, position);
                populateViewHolder(viewHolder, users, position);
                mProgressDialog.dismiss();
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

        public void setUserImage(String image, Context ctx){
            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_single_image);
            Picasso.with(ctx).load(image).placeholder(R.drawable.user).into(userImageView);
        }

    }
}
