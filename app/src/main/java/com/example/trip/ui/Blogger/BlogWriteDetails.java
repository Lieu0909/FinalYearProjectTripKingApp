package com.example.trip.ui.Blogger;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trip.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

public class BlogWriteDetails extends AppCompatActivity {

String passBID;
private ImageView dest_P;
private EditText dest_title,dest_budget, dest_descrip;
private FirebaseAuth auth;
private String currentUserId;
private DatabaseReference databaseBlogRef;
private FloatingActionButton fab;
private RecyclerView recyclerView;
FirebaseRecyclerOptions<BlogDetailsInfo> options;
private FirebaseRecyclerAdapter<BlogDetailsInfo,WriteDetailsHolder>adapter;
private FirebaseAuth mAuth;
private FirebaseUser mCurrentUser;
private static final int Image_Request_Code=1;
Uri FilePathUri;
StorageReference storageReference;
private String apiKey="AIzaSyB4MxAtXFh_e0MmRa14OX9dJsDtOP-9Fzs";
private  int AUTOCOMPLETE_REQUEST_CODE=2;
// Specify the fields to return.
List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.ADDRESS);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_write_details);
        fab=findViewById(R.id.fab_addBlog);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent=getIntent();
        passBID=getIntent().getStringExtra("passBlogID");

        databaseBlogRef = FirebaseDatabase.getInstance().getReference("Blog").child(passBID);

        storageReference = FirebaseStorage.getInstance().getReference("BlogShare");
        recyclerView=findViewById(R.id.rcv_post_blogDetails);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        setRecyclerViewData(""); // call adding data to array list method
        //writedetails
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder=new AlertDialog.Builder(BlogWriteDetails.this);
                View writeB=getLayoutInflater().inflate(R.layout.b_write_blog,null);
                TextView title = new TextView(getApplicationContext());
                title.setText("Blog Info");
                title.setPadding(10, 10, 10, 10);
                title.setTextSize(28);
                title.setTypeface(title.getTypeface(), Typeface.BOLD);
                builder.setCustomTitle(title);
                builder.setMessage("Add blog details");
                builder.setView(writeB);

                //dialog writting blog
                dest_P=writeB.findViewById(R.id.dest_Pic);
                dest_title=writeB.findViewById(R.id.tv_dest_name);
                dest_budget=writeB.findViewById(R.id.tv_budget);
                dest_descrip=writeB.findViewById(R.id.tv_description);
                if(!Places.isInitialized()){
                    // Initialize the SDK
                    Places.initialize(BlogWriteDetails.this, apiKey);
                }
                // Create a new PlacesClient instance
                PlacesClient placesClient = Places.createClient(getApplicationContext());

                // Set the fields to specify which types of place data to
                // return after the user has made a selection.
                placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.ADDRESS);

                //picture
                dest_P.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent,Image_Request_Code);
                    }
                });
                dest_title.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intentAuto = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, placeFields)
                                .build(getApplicationContext());
                        startActivityForResult(intentAuto, AUTOCOMPLETE_REQUEST_CODE);
                    }
                });
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        uploadBlogDetails();
                        dialog.dismiss();

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

        if (requestCode == Image_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {

            FilePathUri = data.getData();

            Picasso.with(this).load(FilePathUri).into(dest_P);

        }
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == AutocompleteActivity.RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i("tag", "Place: " + place.getName() + ", " + place.getLatLng());
                dest_title.setText(place.getName());
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
        if (FilePathUri != null && !TextUtils.isEmpty(dest_title.getText().toString()) && !TextUtils.isEmpty(dest_descrip.getText().toString()) && !TextUtils.isEmpty(dest_budget.getText().toString())) {
            final ProgressDialog pd=new ProgressDialog(this);
            pd.setTitle("Uploading Details...");
            pd.show();
            StorageReference storageReference2 = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(FilePathUri));
            storageReference2.putFile(FilePathUri)

                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while(!urlTask.isSuccessful());
                            Uri downloadUrl = urlTask.getResult();
                            mAuth = FirebaseAuth.getInstance();
                            mCurrentUser = mAuth.getCurrentUser();
                            String uID=mCurrentUser.getUid();

                            String TempTitle = dest_title.getText().toString().trim();
                            String TempDescription = dest_descrip.getText().toString().trim();
                            String TempBudget = dest_budget.getText().toString();
                            String UploadId = databaseBlogRef.push().getKey();
                            Toast.makeText(getApplicationContext(), "Uploaded Successfully ", Toast.LENGTH_LONG).show();
                            BlogDetailsInfo DetailsUploadInfo = new BlogDetailsInfo(passBID,TempTitle,TempDescription,Float.parseFloat(TempBudget), downloadUrl.toString());
                            databaseBlogRef.child("BlogDetails").child(UploadId).setValue(DetailsUploadInfo);



                        }
                    }).addOnFailureListener(new OnFailureListener() {
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
                    pd.dismiss();
                }
            });

        }
        else {

            Toast.makeText(BlogWriteDetails.this, "Please Select Image or Fill The Blank", Toast.LENGTH_LONG).show();

        }
    }




    private void setRecyclerViewData(String data) {
        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getUid();
        Query query = FirebaseDatabase.getInstance().getReference("Blog").child(passBID).child("BlogDetails").orderByChild("blogID").equalTo(passBID);
        //Query query = FirebaseDatabase.getInstance().getReference("Blog").child("BlogID").orderByChild("uID").equalTo(currentUserId);
        options = new FirebaseRecyclerOptions.Builder<BlogDetailsInfo>().setQuery(query, BlogDetailsInfo.class).build();
        adapter = new FirebaseRecyclerAdapter<BlogDetailsInfo, WriteDetailsHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull WriteDetailsHolder writeDetailsHolder, int i, @NonNull BlogDetailsInfo blogDetailsInfo) {

                writeDetailsHolder.dest_name.setText(blogDetailsInfo.getDestinationName());
                writeDetailsHolder.dest_descript.setText(blogDetailsInfo.getDestinationDescription());
                writeDetailsHolder.dest_budget.setText(String.valueOf(blogDetailsInfo.getDestinationBudget()));
                Picasso.with(writeDetailsHolder.dest_Pic.getContext()).load(blogDetailsInfo.getDestinationPic()).into(writeDetailsHolder.dest_Pic);
                writeDetailsHolder.v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent toUpload=new Intent(BlogWriteDetails.this,BlogUpdateDetailsActivity.class);
                        toUpload.putExtra("blogDID",getRef(i).getKey());
                        toUpload.putExtra("blogID",passBID);
                        startActivity(toUpload);

                    }
                });

                writeDetailsHolder.v.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        String key=getRef(i).getKey();
                        showDeleteDialog(key);
                        notifyDataSetChanged();
                        return true;
                    }
                });

            }

            @NonNull
            @Override
            public WriteDetailsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.b_rvc_write_cardview, parent, false);
                return new WriteDetailsHolder(v);
            }

        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


    }

    public void showDeleteDialog(String key){
        final androidx.appcompat.app.AlertDialog.Builder builder=new AlertDialog.Builder(this);
        TextView title = new TextView(getApplicationContext());
        title.setText("Blog Info");
        title.setPadding(10, 10, 10, 10);
        title.setTextSize(28);
        title.setTypeface(title.getTypeface(), Typeface.BOLD);
        builder.setCustomTitle(title);
        builder.setMessage("Are you sure to delete?");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                DatabaseReference mquery=FirebaseDatabase.getInstance().getReference("Blog").child(passBID).child("BlogDetails");
                mquery.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            if(dataSnapshot.hasChild("destinationPic")){
                                String db_image=dataSnapshot.child("destinationPic").getValue().toString();
                                StorageReference fs= FirebaseStorage.getInstance().getReferenceFromUrl(db_image);
                                fs.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mquery.child(key).removeValue();
                                        delete(key);
                                        Toast.makeText(getApplicationContext(),"Successfully deleted",Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }else{
                                Toast.makeText(getApplicationContext(),"Successfully deleted",Toast.LENGTH_SHORT).show();
                                mquery.child(key).removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                dialog.dismiss();

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
    private void delete(String key){
        Query ref= FirebaseDatabase.getInstance().getReference("Blog").child(passBID).child("BlogDetails").child(key);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    ref.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public class WriteDetailsHolder extends RecyclerView.ViewHolder {
        private ImageView dest_Pic;
        private TextView dest_name,dest_descript,dest_budget;
        private View v;

        public WriteDetailsHolder(View view) {
            super(view);
            dest_Pic = view.findViewById(R.id.b_destPic);
            dest_name = view.findViewById(R.id.b_destName);
            dest_descript = view.findViewById(R.id.b_destDescript);
            dest_budget = view.findViewById(R.id.b_destBudget);
            v=itemView;
        }

    }

}
