package com.example.trip.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trip.R;
import com.example.trip.ui.Group.GroupDetails;
import com.example.trip.ui.Group.MessageChatActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.ViewGroupDetailsHolder> {
private Context context;
private ArrayList<GroupDetails>groupDetailsList;

public GroupListAdapter(Context context, ArrayList<GroupDetails> groupDetailsList) {
        this.context = context;
        this.groupDetailsList = groupDetailsList;
    }

    @NonNull
    @Override
    public ViewGroupDetailsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.c_group_list_cardview,parent,false);
        return new ViewGroupDetailsHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewGroupDetailsHolder holder, int position) {
        GroupDetails model=groupDetailsList.get(position);
        String groupImg=model.getGroupPic();
        String groupTitle=model.getGroupName();
        String dateTime=model.getCreateBy();
        String groupID=model.getGroupID();
        holder.name.setText(groupTitle);
        Picasso.with(holder.groupP.getContext()).load(groupImg).into(holder.groupP);
        holder.time.setText(dateTime);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //to message page
                Intent i=new Intent(context, MessageChatActivity.class);
                i.putExtra("groupID",groupID);
                context.startActivity(i);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final androidx.appcompat.app.AlertDialog.Builder builder=new AlertDialog.Builder(context);
                TextView title = new TextView(context);
                title.setText("Group Message");
                title.setPadding(10, 10, 10, 10);
                title.setTextSize(28);
                title.setTypeface(title.getTypeface(), Typeface.BOLD);
                builder.setCustomTitle(title);
                builder.setMessage("Are you sure to delete?");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DatabaseReference mquery= FirebaseDatabase.getInstance().getReference("Group");
                        mquery.child(groupID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    if (dataSnapshot.hasChild("groupPic")) {
                                        String db_image = dataSnapshot.child("groupPic").getValue().toString();
                                        StorageReference fs = FirebaseStorage.getInstance().getReferenceFromUrl(db_image);
                                        fs.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                mquery.child(groupID).removeValue();
                                                notifyDataSetChanged();
                                                Toast.makeText(context, "Successfully deleted", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        Toast.makeText(context,"Successfully deleted",Toast.LENGTH_SHORT).show();
                                        mquery.child(groupID).removeValue();
                                        notifyDataSetChanged();
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
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return groupDetailsList.size();
    }


    // view holder class
    class ViewGroupDetailsHolder extends RecyclerView.ViewHolder {
        TextView name,time,message,sender;
        ImageView groupP;
        View v;

        public ViewGroupDetailsHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.view_groupName);
            groupP=itemView.findViewById(R.id.view_group_pic);
            time=itemView.findViewById(R.id.view_groupTime);



            v=itemView;
        }
    }

}
