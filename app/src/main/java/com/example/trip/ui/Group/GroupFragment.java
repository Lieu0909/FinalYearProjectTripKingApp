package com.example.trip.ui.Group;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trip.Adapter.GroupListAdapter;
import com.example.trip.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import java.util.ArrayList;
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;

public class GroupFragment extends Fragment {
ImageView groupPic;
EditText groupName;
String uID,name,picture;
DatabaseReference groupRef;
private static final int PICK_IMAGE_REQUEST=1;
private Uri FilePathUri;
StorageReference storageReference;
private FirebaseAuth mAuth;
private FirebaseUser mCurrentUser;
private ArrayList<GroupDetails>groupDetailsList;
private GroupListAdapter groupListAdapter;
private RecyclerView recyclerView;



    public static GroupFragment newInstance() {
        return new GroupFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_group, container, false);
        //retrieve for list
        recyclerView=v.findViewById(R.id.rcv_groupList);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        loadGroupDetails();

        FloatingActionButton fab = v.findViewById(R.id.fab_addGroup);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                View groupD=getLayoutInflater().inflate(R.layout.c_group_name,null);
                TextView title = new TextView(getContext());
                title.setText("Create Group");
                title.setPadding(10, 10, 10, 10);
                title.setTextSize(28);
                title.setTypeface(title.getTypeface(), Typeface.BOLD);
                builder.setCustomTitle(title);
                builder.setMessage("Group Details");
                builder.setView(groupD);
                groupPic=groupD.findViewById(R.id.v_group_pic);
                groupName=groupD.findViewById(R.id.v_group_name);

                groupPic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent,PICK_IMAGE_REQUEST);
                    }
                });


                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(groupPic.getDrawable()!=null && !TextUtils.isEmpty(groupName.getText().toString())){
                            uploadGroupDetails();
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



        return v;
    }

    private void loadGroupDetails() {
        groupDetailsList=new ArrayList<>();
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Group");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groupDetailsList.size();
                mAuth = FirebaseAuth.getInstance();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    if(ds.child("Participants").child(mAuth.getUid()).exists()){

                        GroupDetails model=ds.getValue(GroupDetails.class);
                        groupDetailsList.add(model);
                    }
                }
                groupListAdapter=new GroupListAdapter(getActivity(),groupDetailsList);
                recyclerView.setAdapter(groupListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode ==PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            FilePathUri = data.getData();

            Picasso.with(getContext()).load(FilePathUri).into(groupPic);

        }
    }
    public String getFileExtension(Uri uriImage){
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(FilePathUri));
    }
    private void uploadGroupDetails(){
        if (FilePathUri != null && !TextUtils.isEmpty(groupName.getText().toString())) {
            final ProgressDialog pd=new ProgressDialog(getContext());
            pd.setTitle("Uploading Details...");
            pd.show();
            storageReference = FirebaseStorage.getInstance().getReference("Group");
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
                            final Calendar calendar=Calendar.getInstance();
                            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
                            String dateTime=simpleDateFormat.format(calendar.getTime());
                            groupRef= FirebaseDatabase.getInstance().getReference("Group");
                            DatabaseReference userRef=FirebaseDatabase.getInstance().getReference("Users").child(uID);
                            userRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    name=dataSnapshot.child("name").getValue().toString();
                                    picture=dataSnapshot.child("imageurl").getValue().toString();

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            String groupID = groupRef.push().getKey();
                            GroupDetails groupDetails = new GroupDetails(groupName.getText().toString(),downloadUrl.toString(),dateTime,uID,groupID);
                            groupRef.child(groupID).setValue(groupDetails).addOnSuccessListener(new OnSuccessListener<Void>(){
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Participants p=new Participants(name,"Participants",picture);
                                    groupRef.child(groupID).child("Participants").child(uID).setValue(p);
                                    Toast.makeText(getContext(), "Uploaded Successfully ", Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener(){
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "Uploaded Failed ", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    pd.dismiss();
                    Toast.makeText(getContext(),"Failed to create",Toast.LENGTH_SHORT).show();
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

            Toast.makeText(getContext(), "Please Select Image or Fill The Blank", Toast.LENGTH_LONG).show();

        }
    }


}
