package com.example.trip.Login_Registration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trip.R;
import com.example.trip.ui.MyTrip.TripFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    String uID;
    FirebaseAuth fAuth;
    FirebaseDatabase mDB;
    private EditText e_username,e_email;
    private Button b_password;
    private ImageView profilePic;
    private static final int PICK_IMAGE_REQUEST=1;
    private Uri mImageUri;
    private FirebaseStorage storage;
    private StorageReference StorageRef;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        e_username=findViewById(R.id.eT_username);
        e_email=findViewById(R.id.eT_email);
        b_password=findViewById(R.id.btn_ChangePass);
        profilePic=findViewById(R.id.Image_profile);

        fAuth=FirebaseAuth.getInstance();
        mDB=FirebaseDatabase.getInstance();
        final DatabaseReference table_user=mDB.getReference();
        final FirebaseUser user=fAuth.getCurrentUser();
        uID=user.getUid();

        //retrive data to textfield
        table_user.child("Users").child(uID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, String> map = (Map<String, String>) dataSnapshot.getValue();
                String name=map.get("name");
                String email=map.get("email");
                String image=map.get("imageurl");
               e_username.setText(name);
               e_email.setText(email);
                Picasso.with(getApplicationContext()).load(image).into(profilePic);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        b_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(ProfileActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_password, null);
                final EditText mNPass = mView.findViewById(R.id.etNewPass);
                final EditText mCPass =  mView.findViewById(R.id.etConfirmPass);
                Button mChangePass = mView.findViewById(R.id.btnChangePass);
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                mChangePass.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                            if(mNPass.getText().toString().equals(mCPass.getText().toString())){

                                user.updatePassword(mCPass.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        dialog.dismiss();
                                        Toast.makeText(getApplicationContext(),"Password changed",Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ProfileActivity.this, "Password Reset Failed.", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }else {
                                Toast.makeText(getApplicationContext(),"New password should same as confirm password",Toast.LENGTH_SHORT).show();
                            }

                    }
                });
            }
        });

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,PICK_IMAGE_REQUEST);
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Picasso.with(this).load(mImageUri).into(profilePic);
            uploadPicture();

        }
    }

        public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                Intent i=new Intent(this, TripFragment.class);
                startActivity(i);


        }
            return true;
        }

        private void uploadPicture(){
        //image
            storage=FirebaseStorage.getInstance();
            StorageRef=storage.getReference().child("profileImages").child(uID+".jpeg");
            final ProgressDialog pd=new ProgressDialog(this);
            pd.setTitle("Uploading Image...");
            pd.show();
            StorageRef.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            pd.dismiss();
                            StorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    DatabaseReference profile_pic=FirebaseDatabase.getInstance().getReference().child("Users").child(uID);

                                    profile_pic.child("imageurl").setValue(String.valueOf(uri))
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getApplicationContext(),"Completed!",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                            Snackbar.make(findViewById(android.R.id.content),"Image uploaded", Snackbar.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            pd.dismiss();
                            Toast.makeText(getApplicationContext(),"Failed to upload",Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progressPercent=(100.00*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    pd.setMessage("Progress:"+(int) progressPercent +"%");
                }
            });
        }





}
