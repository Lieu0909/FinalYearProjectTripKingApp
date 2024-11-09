package com.example.trip.ui.Group;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import com.example.trip.Login_Registration.User;
import com.example.trip.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GroupMemberActivity extends AppCompatActivity {
    private String passedId;
    EditText memberSearch;
    ImageButton searchBtn;
    DatabaseReference groupRef;
    RecyclerView recyclerView;
    FirebaseRecyclerOptions<User> options;
    FirebaseRecyclerAdapter<User, GroupMemberActivity.NameSearchViewHolder> adapter;
    private ArrayList<Participants> p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_member);
        Toolbar toolbar=findViewById(R.id.toolbar_addMember);
        setSupportActionBar(toolbar);
        Intent intent=getIntent();
        passedId=getIntent().getStringExtra("groupID");
        memberSearch=findViewById(R.id.g_search_field);
        searchBtn=findViewById(R.id.g_search_btn);
        recyclerView = (RecyclerView) findViewById(R.id.rcv_member_name);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchBtn.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                String searchText=memberSearch.getText().toString();
                groupRef= FirebaseDatabase.getInstance().getReference("Users");
                firebasememberSearch(searchText);
            }
        });

    }

    private void firebasememberSearch(String searchText) {

        Toast.makeText(GroupMemberActivity.this, "Started Search", Toast.LENGTH_LONG).show();

        Query firebaseSearchQuery = groupRef.orderByChild("name").startAt(searchText).endAt(searchText + "\uf8ff");

        options=new FirebaseRecyclerOptions.Builder<User>().setQuery(firebaseSearchQuery,User.class).build();
        adapter=new FirebaseRecyclerAdapter<User, GroupMemberActivity.NameSearchViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull GroupMemberActivity.NameSearchViewHolder holder, final int position, @NonNull User model) {
                holder.name.setText(model.getName());
                holder.email.setText(model.getEmail());
                Picasso.with(holder.imgUser.getContext()).load(model.getImageurl()).into(holder.imgUser);

                holder.v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String key=getRef(position).getKey();
                        ShowMessage(key,model.getName(),model.getImageurl());
                }
            });

            }

            @NonNull
            @Override
            public GroupMemberActivity.NameSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.c_user_list_cardview,parent,false);
                return new NameSearchViewHolder(v);
            }


        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);

    }
// View Holder Class

class NameSearchViewHolder extends RecyclerView.ViewHolder {
    TextView name,email;
    ImageView imgUser;
    android.view.View v;

    public NameSearchViewHolder(@NonNull android.view.View itemView) {
        super(itemView);
        name=itemView.findViewById(R.id.view_groupName);
        email=itemView.findViewById(R.id.view_groupEmail);
        imgUser=itemView.findViewById(R.id.view_group_pic);

        v=itemView;
    }
}
    protected void ShowMessage(String key,String name,String picture){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add member?");
        builder.setMessage("Are you sure want to add?");
        // Add the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                groupRef=FirebaseDatabase.getInstance().getReference("Group").child(passedId).child("Participants");
                Participants p=new Participants(name,"Participants",picture);
                groupRef.child(key).setValue(p).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Add Successfully ", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Add Failed ", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
