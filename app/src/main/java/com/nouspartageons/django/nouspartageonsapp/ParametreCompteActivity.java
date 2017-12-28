package com.nouspartageons.django.nouspartageonsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class ParametreCompteActivity extends AppCompatActivity {

    //db
    private FirebaseUser current_user;
    private DatabaseReference dbRef;
    private StorageReference mImageStorage;

    //layout
    private TextView mName;
    private CircleImageView mImage;
    private TextView mStatus;

    private ProgressDialog mProgressDialog;

    Button mChangestat;
    Button mChangeImage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parametre_compte);

        mName = (TextView) findViewById(R.id.compte_name);
        mStatus = (TextView) findViewById(R.id.compte_status);
        mImage = (CircleImageView) findViewById(R.id.compte_image);

        mChangestat = (Button) findViewById(R.id.change_status_btn);
        mChangeImage = (Button) findViewById(R.id.change_image_btn);

        mImageStorage = FirebaseStorage.getInstance().getReference();


        mChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galerieIntent = new Intent();
                galerieIntent.setType("image/*");
                galerieIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galerieIntent, "SELECTIONER UNE IMAGE !"), 1);

            }
        });

        mChangestat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String status_value = mStatus.getText().toString();
                Intent paramIntent = new Intent(ParametreCompteActivity.this, StatusActivity.class);

                paramIntent.putExtra("status_value", status_value);
                startActivity(paramIntent);
                finish();
            }
        });

        current_user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = current_user.getUid();


        dbRef = FirebaseDatabase.getInstance().getReference().child("Utilisateurs").child(uid);
        dbRef.keepSynced(true);

        dbRef = FirebaseDatabase.getInstance().getReference().child("Utilisateurs").child(uid);

        // Read from the database
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();

                mName.setText(name);
                mStatus.setText(status);
                //mettre a jour image avec la biblio Picasso
                if (!image.equals("default")) {

                    Picasso.with(ParametreCompteActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.user).into(mImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(ParametreCompteActivity.this).load(image).placeholder(R.drawable.user).into(mImage);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                //La lecture a echouée
                Log.w("Erreur DB", "La lecture a echouée", error.toException());
            }
        });

    }

    // Pour crop image ---
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK){

            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .setMinCropWindowSize(500, 500)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {


                mProgressDialog = new ProgressDialog(ParametreCompteActivity.this);
                mProgressDialog.setTitle("Chargement d'image...");
                mProgressDialog.setMessage("Veuillez patienter la chargement de l'image");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();


                Uri resultUri = result.getUri();
                String current_user_id = current_user.getUid();

                StorageReference filepath = mImageStorage.child("profile_images").child(current_user_id + ".jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful()){
                            final String download_url = task.getResult().getDownloadUrl().toString();
                            dbRef.child("image").setValue(download_url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        mProgressDialog.dismiss();
                                        Toast.makeText(ParametreCompteActivity.this, "Chargement d'image avec succé.", Toast.LENGTH_LONG).show();
                                    }
                                    else {

                                        mProgressDialog.hide();
                                        Toast.makeText(ParametreCompteActivity.this, "Erreur lors de chargement d'image.", Toast.LENGTH_LONG).show();

                                    }
                                }
                            });

                        } else {

                            mProgressDialog.hide();
                            Toast.makeText(ParametreCompteActivity.this, "Erreur lors de chargement d'image.", Toast.LENGTH_LONG).show();
                        }

                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.w("Erreur CropImage", error);
            }
        }


    }

}
