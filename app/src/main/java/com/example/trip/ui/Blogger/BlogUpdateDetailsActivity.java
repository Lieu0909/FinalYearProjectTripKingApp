package com.example.trip.ui.Blogger;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.trip.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.snackbar.Snackbar;
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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BlogUpdateDetailsActivity extends AppCompatActivity {
private String passBID,passBDID;
private DatabaseReference db;
private Button update;
private ImageView img;
private EditText name, descript,budget;
private static final int Image_Request_Code=1;
Uri FilePathUri;
private FirebaseStorage storage;
private StorageReference StorageRef;
private String image;
private String apiKey="AIzaSyB4MxAtXFh_e0MmRa14OX9dJsDtOP-9Fzs";
private  int AUTOCOMPLETE_REQUEST_CODE=2;
// Specify the fields to return.
List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.ADDRESS);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_update_details);
        Toolbar toolbar=findViewById(R.id.toolbar_updateBlogDetails);
        setSupportActionBar(toolbar);
        img=findViewById(R.id.u_dest_Pic);
        name=findViewById(R.id.u_dest_name);
        descript=findViewById(R.id.u_description);
        budget=findViewById(R.id.u_budget);
        update=findViewById(R.id.u_button);

        Intent intent=getIntent();
        passBID=getIntent().getStringExtra("blogID");
        passBDID=getIntent().getStringExtra("blogDID");
       //bID.setText(passBID);
        db=FirebaseDatabase.getInstance().getReference("Blog").child(passBID).child("BlogDetails").child(passBDID);
        if(!Places.isInitialized()){
            // Initialize the SDK
            Places.initialize(BlogUpdateDetailsActivity.this, apiKey);
        }
        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(getApplicationContext());

        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
        placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.ADDRESS);

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,Image_Request_Code);
            }
        });
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAuto = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, placeFields).build(getApplicationContext());
                startActivityForResult(intentAuto, AUTOCOMPLETE_REQUEST_CODE);
            }
        });


        //retrieve for menu
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    Map<String, String> map = (Map<String, String>) dataSnapshot.getValue();
                    image = map.get("destinationPic");
                    String u_name = map.get("destinationName");
                    String u_des = map.get("destinationDescription");
                    String u_budget = String.valueOf(map.get("destinationBudget"));
                    Picasso.with(getApplicationContext()).load(image).into(img);
                    name.setText(u_name);
                    descript.setText(u_des);
                    budget.setText(String.valueOf(u_budget));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //update
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(name.getText().toString()) && !TextUtils.isEmpty(descript.getText().toString()) && !TextUtils.isEmpty(budget.getText().toString())){
                    DatabaseReference blogUpdate = FirebaseDatabase.getInstance().getReference("Blog").child(passBID).child("BlogDetails").child(passBDID);
                    blogUpdate.child("destinationName").setValue(name.getText().toString());
                    blogUpdate.child("destinationDescription").setValue(descript.getText().toString());
                    blogUpdate.child("destinationBudget").setValue(Float.parseFloat(budget.getText().toString()));


                    Toast.makeText(getApplicationContext(),"Info Updated",Toast.LENGTH_SHORT).show();
                    Intent back=new Intent(BlogUpdateDetailsActivity.this, BlogWriteDetails.class);
                    startActivity(back);
                } else {
                    Toast.makeText(getApplicationContext(),"Please Select Image or Fill The Blank",Toast.LENGTH_SHORT).show();
                }
            }
            });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Image_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {

            FilePathUri = data.getData();

            Picasso.with(this).load(FilePathUri).into(img);
            uploadBlogDetails();
        }
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == AutocompleteActivity.RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i("tag", "Place: " + place.getName() + ", " + place.getLatLng());
                name.setText(place.getName());
                //tvPlaceId.setText(place.getId());
                //tvLatLon.setText(String.valueOf(place.getLatLng()));

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("tag", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }
    public String getFileExtension(Uri uriImage){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(FilePathUri));
    }
    private void uploadBlogDetails(){
        if(!TextUtils.isEmpty(name.getText().toString()) && !TextUtils.isEmpty(descript.getText().toString()) && !TextUtils.isEmpty(budget.getText().toString())){
            String lastSegment=Uri.parse(image).getLastPathSegment();

            storage= FirebaseStorage.getInstance();
            StorageRef=storage.getReference().child(lastSegment);
            final ProgressDialog pd=new ProgressDialog(this);
            pd.setTitle("Uploading Image...");
            pd.show();
            StorageRef.putFile(FilePathUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            pd.dismiss();
                            StorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    DatabaseReference blogImage=FirebaseDatabase.getInstance().getReference("Blog").child(passBID).child("BlogDetails").child(passBDID);;

                                    blogImage.child("destinationPic").setValue(String.valueOf(uri)).addOnSuccessListener(new OnSuccessListener<Void>() {
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
        else {

            Toast.makeText(BlogUpdateDetailsActivity.this, "Please Select Image or Fill The Blank", Toast.LENGTH_LONG).show();

        }
    }
}
