package com.example.trip.ui.Blogger;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.trip.R;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

public class BlogUploadMenu extends AppCompatActivity {
    String blogID,uID,passed_bID;
    FirebaseAuth fAuth;
    private TextView tv_blogID,tv_tripName,tv_description,tv_dayS,tv_dayE,tName,dStart,dEnd,tDescription;
    private RelativeLayout l_edit,l_write,l_view;
    private ImageView imageT;
    private static final int PICK_IMAGE_REQUEST=1;
    FirebaseDatabase mDB;
    private Uri bImageUri;
    private FirebaseStorage storage;
    private StorageReference StorageRef;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_upload_menu);
        Toolbar toolbar=findViewById(R.id.toolbar_blogUploadMenu);
        setSupportActionBar(toolbar);
        tv_blogID=findViewById(R.id.tv_bID);
        tv_tripName=findViewById(R.id.tv_tripN);
        tv_dayS=findViewById(R.id.tv_dayS);
        tv_dayE=findViewById(R.id.tv_dayE);
        l_edit=findViewById(R.id.cv_editB);
        l_write=findViewById(R.id.cv_writeB);
        l_view=findViewById(R.id.cv_viewB);
        imageT=findViewById(R.id.main_Picture);
        tv_description=findViewById(R.id.tv_tripD);

        fAuth= FirebaseAuth.getInstance();
        mDB=FirebaseDatabase.getInstance();
        final FirebaseUser user=fAuth.getCurrentUser();
        uID=user.getUid();

        //receive passed id from previous "Create Blogger" activity
        Intent intent=getIntent();
        blogID=getIntent().getStringExtra("blogID");
        tv_blogID.setText(blogID);


        //upload image
        imageT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,PICK_IMAGE_REQUEST);
            }
        });


        //retrieve blog data from firebase
        passed_bID=tv_blogID.getText().toString().trim();
        mDB=FirebaseDatabase.getInstance();
        final DatabaseReference blog=mDB.getReference();
        //retrieve for background

        //retrieve for menu
        blog.child("Blog").child(passed_bID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    String t1_Name=dataSnapshot.child("tripName").getValue().toString();
                    String d1_Start=dataSnapshot.child("dayStart").getValue().toString();
                    String d1_End=dataSnapshot.child("dayEnd").getValue().toString();
                    String t1_description=dataSnapshot.child("description").getValue().toString();
                    if(dataSnapshot.child("imageURL").exists()){
                        String image=dataSnapshot.child("imageURL").getValue().toString();
                        Picasso.with(getApplicationContext()).load(image).into(imageT);
                    }else{
                        Picasso.with(getApplicationContext()).load(R.drawable.bb_create_blog).into(imageT);
                    }

                    imageT.setImageAlpha(150);
                    tv_blogID.setText(blogID);
                    tv_tripName.setText(t1_Name);
                    tv_dayS.setText(d1_Start);
                    tv_dayE.setText(d1_End);
                    tv_description.setText(t1_description);
                    /*Map<String, String> map = (Map<String, String>) dataSnapshot.getValue();
                    String image=map.get("imageURL");
                    String t1_Name=map.get("tripName");
                    String d1_Start=map.get("dayStart");
                    String d1_End=map.get("dayEnd");
                    String s=map.get("status");

                    Picasso.with(getApplicationContext()).load(image).into(imageT);
                    imageT.setImageAlpha(150);
                    tv_blogID.setText(blogID);
                    tv_tripName.setText(t1_Name);
                    tv_dayS.setText(d1_Start);
                    tv_dayE.setText(d1_End);*/

                } }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
        //write
        l_write.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toW=new Intent(BlogUploadMenu.this,BlogWriteDetails.class);
                toW.putExtra("passBlogID",tv_blogID.getText().toString());
                startActivity(toW);
            }
        });
        l_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toV=new Intent(BlogUploadMenu.this,BlogViewLayout.class);
                toV.putExtra("passBlogID",tv_blogID.getText().toString());
                startActivity(toV);
            }
        });

        //edit
        l_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder=new AlertDialog.Builder(BlogUploadMenu.this);
                View editMenu=getLayoutInflater().inflate(R.layout.b_edit_menu,null);
                TextView title = new TextView(getApplicationContext());
                title.setText("Blog Info");
                title.setPadding(10, 10, 10, 10);
                title.setTextSize(28);
                title.setTypeface(title.getTypeface(), Typeface.BOLD);
                builder.setCustomTitle(title);
                builder.setMessage("Edit blog details");
                builder.setView(editMenu);

                Calendar calendar=Calendar.getInstance();
                final int year=calendar.get(Calendar.YEAR);
                final int month=calendar.get(Calendar.MONTH);
                final int day=calendar.get(calendar.DAY_OF_MONTH);

                //adapter component
                tName=editMenu.findViewById(R.id.et_BeName);
                dStart=editMenu.findViewById(R.id.et_BeCalendar);
                dEnd=editMenu.findViewById(R.id.et_BeEnd);
                tDescription=editMenu.findViewById(R.id.et_Bedescription);



                dStart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatePickerDialog datePickerDialog=new DatePickerDialog(
                                BlogUploadMenu.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                month=month+1;
                                String date=dayOfMonth+"/"+month+"/"+year;
                                dStart.setText(date);

                            }
                        },year,month,day);
                        //disable future date
                        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                        datePickerDialog.show();

                    }
                });

                dEnd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        DatePickerDialog datePickerDialog=new DatePickerDialog(
                                BlogUploadMenu.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                month=month+1;
                                String date=dayOfMonth+"/"+month+"/"+year;
                                dEnd.setText(date);

                            }
                        },year,month,day);
                        //disable future date
                        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                        datePickerDialog.show();


                    }
                });

                //retrieve for dialogbox
                blog.child("Blog").child(passed_bID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Map<String, String> map = (Map<String, String>) dataSnapshot.getValue();
                        String t_Name=map.get("tripName");
                        String d_Start=map.get("dayStart");
                        String d_End=map.get("dayEnd");
                        String t_descrip=map.get("description");

                        tName.setText(t_Name);
                        dStart.setText(d_Start);
                        dEnd.setText(d_End);
                        tDescription.setText(t_descrip);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                //dialog box operation
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
                final String dateTime=simpleDateFormat.format(calendar.getTime());
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(!TextUtils.isEmpty(tName.getText().toString()) && !TextUtils.isEmpty(dStart.getText().toString()) && !TextUtils.isEmpty(dEnd.getText().toString())&& !TextUtils.isEmpty(tDescription.getText().toString())){
                            DatabaseReference blogUpdate = FirebaseDatabase.getInstance().getReference().child("Blog").child(passed_bID);
                            blogUpdate.child("tripName").setValue(tName.getText().toString());
                            blogUpdate.child("description").setValue(tDescription.getText().toString());
                            blogUpdate.child("dayStart").setValue(dStart.getText().toString());
                            blogUpdate.child("dayEnd").setValue(dEnd.getText().toString());
                            blogUpdate.child("date").setValue(dateTime);

                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Info Updated",Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(),"Please Select Image or Fill The Blank",Toast.LENGTH_SHORT).show();
                        }


                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                final AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            bImageUri = data.getData();
            Picasso.with(this).load(bImageUri).into(imageT);
            uploadPicture();

        }
    }
    private void uploadPicture(){
        //image
        storage= FirebaseStorage.getInstance();
        StorageRef=storage.getReference().child("BlogShare").child(passed_bID +uID +".jpeg");
        final ProgressDialog pd=new ProgressDialog(this);
        pd.setTitle("Uploading Image...");
        pd.show();
        StorageRef.putFile(bImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        pd.dismiss();
                        StorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                DatabaseReference blogImage=FirebaseDatabase.getInstance().getReference("Blog").child(passed_bID);

                                blogImage.child("imageURL").setValue(String.valueOf(uri)).addOnSuccessListener(new OnSuccessListener<Void>() {
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
