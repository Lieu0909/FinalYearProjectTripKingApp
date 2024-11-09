package com.example.trip.ui.Group;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
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

import java.util.Map;

public class GroupInfoActivity extends AppCompatActivity {
TextView participantnameTv;
EditText groupNameTv;
ImageView profilePic,groupPicView;
ImageButton doneBtn;
private static final int Image_Request_Code=1;
Uri FilePathUri;
private FirebaseStorage storage;
private StorageReference StorageRef;
DatabaseReference groupRef,mquery;
String passBID,image;
RecyclerView recyclerView;
FirebaseRecyclerOptions<Participants> options;
FirebaseRecyclerAdapter<Participants, GroupInfoActivity.participantsViewHolder> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);
        Toolbar toolbar=findViewById(R.id.toolbar_groupInfo);
        setSupportActionBar(toolbar);
        groupNameTv = findViewById(R.id.i_groupName);
        groupPicView = findViewById(R.id.i_groupPic);
        doneBtn = findViewById(R.id.i_groupNamebtn);
        recyclerView = findViewById(R.id.rcv_participants_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        passBID = getIntent().getStringExtra("groupID");

        groupRef = FirebaseDatabase.getInstance().getReference().child("Group").child(passBID);
        groupRef.child("Participants").orderByChild("role").equalTo("Participants").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    String parent = childSnapshot.getKey();
                    loadParticipantInfo(parent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        //retrieve for menu
        groupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, String> map = (Map<String, String>) dataSnapshot.getValue();
                image = map.get("groupPic");
                String g_name = map.get("groupName");
                Picasso.with(getApplicationContext()).load(image).into(groupPicView);
                groupNameTv.setText(g_name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupRef.child("groupName").setValue(groupNameTv.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Update Successfully!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        groupPicView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, Image_Request_Code);
            }
        });
    }
    private void loadParticipantInfo(String parent) {

        Query query = groupRef.child("Participants").orderByChild("role").equalTo("Participants");

        options=new FirebaseRecyclerOptions.Builder<Participants>().setQuery(query,Participants.class).build();
        adapter=new FirebaseRecyclerAdapter<Participants, GroupInfoActivity.participantsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull participantsViewHolder holder, int i, @NonNull Participants model) {
                holder.name.setText(model.getName());
                Picasso.with(holder.imgPic.getContext()).load(model.getPicture()).into(holder.imgPic);

                holder.v.setOnLongClickListener(new View.OnLongClickListener() {
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
            public GroupInfoActivity.participantsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.c_participants_list,parent,false);
                return new participantsViewHolder(v);
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);

    }
    class participantsViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView imgPic;
        View v;

        public participantsViewHolder(@NonNull View itemView) {
            super(itemView);

            imgPic=itemView.findViewById(R.id.participants_pic);
            name=itemView.findViewById(R.id.participants_name);

            v = itemView;
        }
    }
    public void showDeleteDialog(String key){
        final androidx.appcompat.app.AlertDialog.Builder builder=new AlertDialog.Builder(this);
        TextView title = new TextView(getApplicationContext());
        title.setText("Group Message");
        title.setPadding(10, 10, 10, 10);
        title.setTextSize(28);
        title.setTypeface(title.getTypeface(), Typeface.BOLD);
        builder.setCustomTitle(title);
        builder.setMessage("Are you sure to delete?");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Group").child(passBID).child("Participants");
                reference.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"Participants deleted Successfully",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Participants delete Failed",Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Image_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {

            FilePathUri = data.getData();

            Picasso.with(this).load(FilePathUri).into(groupPicView);
            uploadPicture();
        }
    }
    public String getFileExtension(Uri uriImage){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(FilePathUri));
    }
    private void uploadPicture(){

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
                                    DatabaseReference groupI= FirebaseDatabase.getInstance().getReference().child("Group").child(passBID);

                                    groupI.child("groupPic").setValue(String.valueOf(uri)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getApplicationContext(),"Updated!",Toast.LENGTH_SHORT).show();
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
