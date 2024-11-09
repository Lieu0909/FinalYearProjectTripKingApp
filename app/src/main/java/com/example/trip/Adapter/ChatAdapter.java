package com.example.trip.Adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trip.R;
import com.example.trip.ui.Group.ChatDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    List<ChatDetails> chatDetailsList;
    private String id,sender;
    private static final int MSG_TYPE_LEFT=0;
    private static final int MSG_TYPE_RIGHT=1;
    Context context;
    FirebaseAuth firebaseAuth;
    Boolean clicked=false;
    DatabaseReference voteRef;
    public ChatAdapter(String id,List<ChatDetails> chatDetailsList, Context context) {
        this.id=id;
        this.chatDetailsList = chatDetailsList;
        this.context = context;
        firebaseAuth=FirebaseAuth.getInstance();

    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==MSG_TYPE_LEFT){
            View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.c_row_chat_left,parent,false);
            return new ChatViewHolder(v);
        }else{
            View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.c_row_chat_right,parent,false);
            return new ChatViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        //getData
        ChatDetails model=chatDetailsList.get(position);
        String message=model.getMessages();
        String senderId=model.getSenderId();
        String timestamp=model.getTime();
        String type=model.getInputType();





        //get data of voting
        String question=model.getQuestions();
        String option1=model.getSelection1();
        String option2=model.getSelection2();
        String option3=model.getSelection3();
        String option4=model.getSelection4();



        //convert timestamp to format
        Calendar cal=Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timestamp));
        String dateTime= DateFormat.format("dd/MM/yyyy hh:mm:aa",cal).toString();
        if(type.equals("text")){
            //text message,hide voting layout
            holder.questionTv.setVisibility(View.GONE);
            holder.opTv1.setVisibility(View.GONE);
            holder.opTv2.setVisibility(View.GONE);
            holder.opTv3.setVisibility(View.GONE);
            holder.opTv4.setVisibility(View.GONE);
            holder.showOptionLayout.setVisibility(View.GONE);
            holder.showOp1.setVisibility(View.GONE);
            holder.showOp2.setVisibility(View.GONE);
            holder.showOp3.setVisibility(View.GONE);
            holder.showOp4.setVisibility(View.GONE);
            holder.resultTv1.setVisibility(View.GONE);
            holder.resultTv2.setVisibility(View.GONE);
            holder.resultTv3.setVisibility(View.GONE);
            holder.resultTv4.setVisibility(View.GONE);
            holder.rlvoting.setVisibility(View.GONE);
            holder.img1.setVisibility(View.GONE);
            holder.img2.setVisibility(View.GONE);
            holder.img3.setVisibility(View.GONE);
            holder.img4.setVisibility(View.GONE);
            //vissible for text message
            holder.messagesTv.setVisibility(View.VISIBLE);
            //set Data
            holder.messagesTv.setText(message);

            setUserName(model,holder);
        }else{
            //hide text message,show voting
            holder.questionTv.setVisibility(View.VISIBLE);
            holder.opTv1.setVisibility(View.VISIBLE);
            holder.opTv2.setVisibility(View.VISIBLE);
            holder.opTv3.setVisibility(View.VISIBLE);
            holder.opTv4.setVisibility(View.VISIBLE);
            holder.showOptionLayout.setVisibility(View.GONE);
            holder.showOp1.setVisibility(View.GONE);
            holder.showOp2.setVisibility(View.GONE);
            holder.showOp3.setVisibility(View.GONE);
            holder.showOp4.setVisibility(View.GONE);
            holder.choicTv.setVisibility(View.GONE);
            holder.resultTv1.setVisibility(View.VISIBLE);
            holder.resultTv2.setVisibility(View.VISIBLE);
            holder.resultTv3.setVisibility(View.VISIBLE);
            holder.resultTv4.setVisibility(View.VISIBLE);
            holder.img1.setVisibility(View.VISIBLE);
            holder.img2.setVisibility(View.VISIBLE);
            holder.img3.setVisibility(View.VISIBLE);
            holder.img4.setVisibility(View.VISIBLE);
            //Invissible for text
            holder.messagesTv.setVisibility(View.GONE);
            //set data
            holder.questionTv.setText(question);
            holder.opTv1.setText(option1);
            holder.opTv2.setText(option2);
            holder.opTv3.setText(option3);
            holder.opTv4.setText(option4);

            String user=firebaseAuth.getUid();
            setUserName(model,holder);

            voteRef = FirebaseDatabase.getInstance().getReference("Group").child(id).child("Voting");
            voteRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(timestamp).child("selection1").hasChild(user)){
                        holder.opTv2.setVisibility(View.GONE);
                        holder.opTv3.setVisibility(View.GONE);
                        holder.opTv4.setVisibility(View.GONE);
                        holder.showOptionLayout.setVisibility(View.VISIBLE);
                        holder.choicTv.setVisibility(View.VISIBLE);
                        holder.showOp1.setVisibility(View.VISIBLE);
                        holder.showOp2.setVisibility(View.VISIBLE);
                        holder.showOp3.setVisibility(View.VISIBLE);
                        holder.showOp4.setVisibility(View.VISIBLE);
                        holder.showOp1.setText(option1);
                        holder.showOp2.setText(option2);
                        holder.showOp3.setText(option3);
                        holder.showOp4.setText(option4);

                    }
                    if (dataSnapshot.child(timestamp).child("selection2").hasChild(user)){
                        holder.opTv1.setVisibility(View.GONE);
                        holder.opTv3.setVisibility(View.GONE);
                        holder.opTv4.setVisibility(View.GONE);
                        holder.showOptionLayout.setVisibility(View.VISIBLE);
                        holder.choicTv.setVisibility(View.VISIBLE);
                        holder.showOp1.setVisibility(View.VISIBLE);
                        holder.showOp2.setVisibility(View.VISIBLE);
                        holder.showOp3.setVisibility(View.VISIBLE);
                        holder.showOp4.setVisibility(View.VISIBLE);
                        holder.showOp1.setText(option1);
                        holder.showOp2.setText(option2);
                        holder.showOp3.setText(option3);
                        holder.showOp4.setText(option4);

                    }
                    if (dataSnapshot.child(timestamp).child("selection3").hasChild(user)){
                        holder.opTv1.setVisibility(View.GONE);
                        holder.opTv2.setVisibility(View.GONE);
                        holder.opTv4.setVisibility(View.GONE);
                        holder.showOptionLayout.setVisibility(View.VISIBLE);
                        holder.choicTv.setVisibility(View.VISIBLE);
                        holder.showOp1.setVisibility(View.VISIBLE);
                        holder.showOp2.setVisibility(View.VISIBLE);
                        holder.showOp3.setVisibility(View.VISIBLE);
                        holder.showOp4.setVisibility(View.VISIBLE);
                        holder.showOp1.setText(option1);
                        holder.showOp2.setText(option2);
                        holder.showOp3.setText(option3);
                        holder.showOp4.setText(option4);

                    }
                    if (dataSnapshot.child(timestamp).child("selection4").hasChild(user)){
                        holder.opTv1.setVisibility(View.GONE);
                        holder.opTv2.setVisibility(View.GONE);
                        holder.opTv3.setVisibility(View.GONE);
                        holder.showOptionLayout.setVisibility(View.VISIBLE);
                        holder.choicTv.setVisibility(View.VISIBLE);
                        holder.showOp1.setVisibility(View.VISIBLE);
                        holder.showOp2.setVisibility(View.VISIBLE);
                        holder.showOp3.setVisibility(View.VISIBLE);
                        holder.showOp4.setVisibility(View.VISIBLE);
                        holder.showOp1.setText(option1);
                        holder.showOp2.setText(option2);
                        holder.showOp3.setText(option3);
                        holder.showOp4.setText(option4);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            holder.setResult(timestamp);
            holder.opTv1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clicked=true;
                    voteRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (clicked.equals(true)){

                                    voteRef.child(timestamp).child("selection1").child(user).setValue(true);
                                    holder.opTv2.setVisibility(View.GONE);
                                    holder.opTv3.setVisibility(View.GONE);
                                    holder.opTv4.setVisibility(View.GONE);
                                    Toast.makeText(context,"Option1 Selected",Toast.LENGTH_SHORT).show();
                                    clicked = false;
                                }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            });
            holder.opTv2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clicked=true;
                    voteRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (clicked.equals(true)){

                                    voteRef.child(timestamp).child("selection2").child(user).setValue(true);
                                    holder.opTv1.setVisibility(View.GONE);
                                    holder.opTv3.setVisibility(View.GONE);
                                    holder.opTv4.setVisibility(View.GONE);
                                    Toast.makeText(context,"Option2 Selected",Toast.LENGTH_SHORT).show();
                                    clicked = false;

                            }

                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            });
            holder.opTv3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clicked=true;
                    voteRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (clicked.equals(true)){

                                    voteRef.child(timestamp).child("selection3").child(user).setValue(true);
                                    holder.opTv1.setVisibility(View.GONE);
                                    holder.opTv2.setVisibility(View.GONE);
                                    holder.opTv4.setVisibility(View.GONE);
                                Toast.makeText(context,"Option3 Selected",Toast.LENGTH_SHORT).show();
                                    clicked = false;

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            });
            holder.opTv4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clicked=true;
                    voteRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (clicked.equals(true)){

                                    voteRef.child(timestamp).child("selection4").child(user).setValue(true);
                                    holder.opTv1.setVisibility(View.GONE);
                                    holder.opTv2.setVisibility(View.GONE);
                                    holder.opTv3.setVisibility(View.GONE);
                                    Toast.makeText(context,"Option4 Selected",Toast.LENGTH_SHORT).show();
                                    clicked = false;
                                }
                            }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            });
        }
        holder.timeTv.setText(dateTime);

       
    }


    private void setUserName(ChatDetails model, ChatViewHolder holder) {
        //getSenderInfo
        DatabaseReference userRef= FirebaseDatabase.getInstance().getReference("Users");
        userRef.child(model.getSenderId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name= (String) dataSnapshot.child("name").getValue();
                holder.nameTv.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return chatDetailsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        //getCurrent signed in user;

        if(chatDetailsList.get(position).getSenderId().equals(firebaseAuth.getUid())){
            return  MSG_TYPE_RIGHT;
        }else{
            return  MSG_TYPE_LEFT;
        }
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView nameTv,messagesTv,timeTv;
        TextView questionTv,resultTv1,resultTv2,resultTv3,resultTv4;
        TextView opTv1,opTv2,opTv3,opTv4,showOp1,showOp2,showOp3,showOp4,choicTv;
        RelativeLayout rlvoting;
        LinearLayout showOptionLayout;
        ImageView img1,img2,img3,img4;
        TextView lastMessageTv,lastsenderTv;
        int voteCount;
        View v;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTv=itemView.findViewById(R.id.showName);
            timeTv=itemView.findViewById(R.id.showTimes);
            messagesTv=itemView.findViewById(R.id.showMessages);
            questionTv=itemView.findViewById(R.id.vv_questionTv);
            rlvoting=itemView.findViewById(R.id.votingLayout);
            opTv1=itemView.findViewById(R.id.vv_option1);
            opTv2=itemView.findViewById(R.id.vv_option2);
            opTv3=itemView.findViewById(R.id.vv_option3);
            opTv4=itemView.findViewById(R.id.vv_option4);
            resultTv1=itemView.findViewById(R.id.oneTv);
            resultTv2=itemView.findViewById(R.id.twoTv);
            resultTv3=itemView.findViewById(R.id.threeTv);
            resultTv4=itemView.findViewById(R.id.fourTv);
            img1=itemView.findViewById(R.id.one);
            img2=itemView.findViewById(R.id.two);
            img3=itemView.findViewById(R.id.three);
            img4=itemView.findViewById(R.id.four);
            showOptionLayout=itemView.findViewById(R.id.votingOptionLL);
            showOp1=itemView.findViewById(R.id.Show1);
            showOp2=itemView.findViewById(R.id.Show2);
            showOp3=itemView.findViewById(R.id.Show3);
            showOp4=itemView.findViewById(R.id.Show4);
            choicTv=itemView.findViewById(R.id.choiceTv);




            v=itemView;
        }
        public void setResult(String timestamp){
            resultTv1=itemView.findViewById(R.id.oneTv);
            resultTv2=itemView.findViewById(R.id.twoTv);
            resultTv3=itemView.findViewById(R.id.threeTv);
            resultTv4=itemView.findViewById(R.id.fourTv);
            voteRef = FirebaseDatabase.getInstance().getReference("Group").child(id).child("Voting");
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String userId = user.getUid();
            String vote = "voters";

            voteRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(timestamp).child("selection1").hasChild(userId)){
                        voteCount = (int)dataSnapshot.child(timestamp).child("selection1").getChildrenCount();
                        resultTv1.setText(Integer.toString(voteCount)+vote);

                    }else{
                        voteCount = (int)dataSnapshot.child(timestamp).child("selection1").getChildrenCount();
                        resultTv1.setText(Integer.toString(voteCount)+vote);
                    }
                    if (dataSnapshot.child(timestamp).child("selection2").hasChild(userId)){
                        voteCount = (int)dataSnapshot.child(timestamp).child("selection2").getChildrenCount();
                        resultTv2.setText(Integer.toString(voteCount)+vote);
                    }else{
                        voteCount = (int)dataSnapshot.child(timestamp).child("selection2").getChildrenCount();
                        resultTv2.setText(Integer.toString(voteCount)+vote);
                    }
                    if (dataSnapshot.child(timestamp).child("selection3").hasChild(userId)){
                        voteCount = (int)dataSnapshot.child(timestamp).child("selection3").getChildrenCount();
                        resultTv3.setText(Integer.toString(voteCount)+vote);
                    } else{
                        voteCount = (int)dataSnapshot.child(timestamp).child("selection3").getChildrenCount();
                        resultTv3.setText(Integer.toString(voteCount)+vote);
                    }
                    if (dataSnapshot.child(timestamp).child("selection4").hasChild(userId)){
                        voteCount = (int)dataSnapshot.child(timestamp).child("selection4").getChildrenCount();
                        resultTv4.setText(Integer.toString(voteCount)+vote);

                    }else {
                        voteCount = (int)dataSnapshot.child(timestamp).child("selection4").getChildrenCount();
                        resultTv4.setText(Integer.toString(voteCount)+vote);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

    }

}}
