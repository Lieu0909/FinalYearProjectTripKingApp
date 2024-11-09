package com.example.trip.ui.Group;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

import com.example.trip.Adapter.ChatAdapter;
import com.example.trip.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MessageChatActivity extends AppCompatActivity {
DatabaseReference groupRef,partCount,chatRef;
private String passedId;
ImageView groupPicture;
TextView noParticipant,v_question,v_op1,v_op2,v_op3,v_op4;
Button send,more;
EditText input,question,op1,op2,op3,op4;
int memberCount;
List<ChatDetails>chatDetails;
private ArrayList<ChatDetails>chatDetailsList;
ChatAdapter chatAdapter;
RecyclerView recyclerView;
private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_chat);
        Intent intent=getIntent();
        passedId=getIntent().getStringExtra("groupID");

        groupPicture=findViewById(R.id.groupChat_image);
        noParticipant=findViewById(R.id.noP);
        send=findViewById(R.id.sendMessage);
        input=findViewById(R.id.messageInput);
        more=findViewById(R.id.btn_voting);
        v_question=findViewById(R.id.vv_questionTv);
        v_op1=findViewById(R.id.vv_option1);
        v_op2=findViewById(R.id.vv_option2);
        v_op3=findViewById(R.id.vv_option3);
        v_op4=findViewById(R.id.vv_option4);


        //recyclerview
        recyclerView=findViewById(R.id.messageList);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        readMessgae();
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogVotingBox();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messages=input.getText().toString();
                if(TextUtils.isEmpty(messages)){
                    Toast.makeText(MessageChatActivity.this,"Can't send empty message...",Toast.LENGTH_SHORT).show();
                }else {
                    sendMessage(messages);
                }
            }
        });
        //retrieve group Name
        groupRef= FirebaseDatabase.getInstance().getReference("Group").child(passedId);
        groupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    Map<String, String> map = (Map<String, String>) dataSnapshot.getValue();
                    String name = map.get("groupName");
                    String picture = map.get("groupPic");
                    //mToolbar.setTitle(name);
                    Toolbar myToolbar = (Toolbar) findViewById(R.id.groupChat_toolbar);
                    myToolbar.setTitle(name);
                    setSupportActionBar(myToolbar);
                    Picasso.with(getApplicationContext()).load(picture).into(groupPicture);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        partCount= FirebaseDatabase.getInstance().getReference("Group").child(passedId);
        partCount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    memberCount = (int)dataSnapshot.child("Participants").getChildrenCount();
                    noParticipant.setText(Integer.toString(memberCount)+" members");
            }}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void showDialogVotingBox() {
        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        View votingForm=getLayoutInflater().inflate(R.layout.c_row_create_voting,null);
        TextView title = new TextView(getApplicationContext());
        title.setText("Create Voting Form");
        title.setPadding(10, 10, 10, 10);
        title.setTextSize(28);
        title.setTypeface(title.getTypeface(), Typeface.BOLD);
        builder.setCustomTitle(title);
        builder.setView(votingForm);
        question=votingForm.findViewById(R.id.question);
        op1=votingForm.findViewById(R.id.selection1);
        op2=votingForm.findViewById(R.id.selection2);
        op3=votingForm.findViewById(R.id.selection3);
        op4=votingForm.findViewById(R.id.selection4);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String q=question.getText().toString();
                String ans1=op1.getText().toString();
                String ans2=op2.getText().toString();
                String ans3=op3.getText().toString();
                String ans4=op4.getText().toString();
                if(!TextUtils.isEmpty(q) && !TextUtils.isEmpty(ans1) && !TextUtils.isEmpty(ans2) && !TextUtils.isEmpty(ans3) && !TextUtils.isEmpty(ans4)){
                    mAuth = FirebaseAuth.getInstance();
                    DatabaseReference vRef = FirebaseDatabase.getInstance().getReference("Group").child(passedId).child("Messages");
                    String voteID = vRef.push().getKey();
                    String time=""+System.currentTimeMillis();
                    ChatDetails chatDetails=new ChatDetails("vote",mAuth.getUid(),voteID,time,q,ans1,ans2,ans3,ans4);
                    vRef.child(time).setValue(chatDetails).addOnSuccessListener(new OnSuccessListener<Void>(){
                        @Override
                        public void onSuccess(Void aVoid) {

                            Toast.makeText(getApplicationContext(), "Created Successfully ", Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener(){
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Created Failed ", Toast.LENGTH_LONG).show();
                        }
                    });


                    dialog.dismiss();

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
    //toolbar
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_g,menu);
        return true;

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();

        if(id == R.id.add_member){
            Intent intent=new Intent(getApplicationContext(), GroupMemberActivity.class);
            intent.putExtra("groupID",passedId);
            startActivity(intent);

        }else if(id==R.id.exit){
            leaveGroup();
        }else if(id==R.id.delete){
            deleteGroup();
        }else if(id==R.id.groupInfo){
            Intent i=new Intent(getApplicationContext(),GroupInfoActivity.class);
            i.putExtra("groupID",passedId);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteGroup(){

        DatabaseReference mquery= FirebaseDatabase.getInstance().getReference("Group");
        mquery.child(passedId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("groupPic")) {
                        String db_image = dataSnapshot.child("groupPic").getValue().toString();
                        StorageReference fs = FirebaseStorage.getInstance().getReferenceFromUrl(db_image);
                        fs.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mquery.child(passedId).removeValue();
                                Toast.makeText(getApplicationContext(), "Successfully deleted", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(),"Successfully deleted",Toast.LENGTH_SHORT).show();
                        mquery.child(passedId).removeValue();

                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void leaveGroup(){
        String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Group").child(passedId).child("Participants");

        reference.child(currentuser).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(),"Group left Successfully",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"" +e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void readMessgae(){
        //init list
        chatDetailsList=new ArrayList<>();
        DatabaseReference chatRef= FirebaseDatabase.getInstance().getReference("Group").child(passedId).child("Messages");
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatDetailsList.clear();
                for (DataSnapshot ds:dataSnapshot.getChildren()){

                    ChatDetails model=ds.getValue(ChatDetails.class);
                    chatDetailsList.add(model);
                }
                chatAdapter= new ChatAdapter(passedId,chatDetailsList,MessageChatActivity.this);
                recyclerView.setAdapter(chatAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendMessage(String m){
        chatRef=FirebaseDatabase.getInstance().getReference("Group").child(passedId);

                String time=""+System.currentTimeMillis();
                //fetch message into firebase
                mAuth = FirebaseAuth.getInstance();
               ChatDetails chatDetails=new ChatDetails(m,"text",mAuth.getUid(),time);
               chatRef.child("Messages").child(time).setValue(chatDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                   @Override
                   public void onSuccess(Void aVoid) {
                       Toast.makeText(getApplicationContext(), "Message sent ", Toast.LENGTH_LONG).show();
                       input.setText("");
                   }
               }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       Toast.makeText(getApplicationContext(), "Message Unsent ", Toast.LENGTH_LONG).show();
                   }
               });
            }






}
