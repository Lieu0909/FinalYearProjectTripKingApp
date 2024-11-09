package com.example.trip.ui.Blogger;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trip.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class PostedBlogFragment extends Fragment {
    View v;
    private FirebaseAuth auth;
    private String currentUserId;
    FirebaseRecyclerOptions<CreateBlogInfo> options;
    FirebaseRecyclerAdapter<CreateBlogInfo, PostedBlogFragment.postedBlogViewHolder> adapter;
    private RecyclerView recyclerView;
    private int likescount;
    private DatabaseReference likesRef;
    DatabaseReference mquery;
    public PostedBlogFragment() {
        // Required empty public constructor
    }
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        v= inflater.inflate(R.layout.fragment_posted_blog, container, false);
        recyclerView=v.findViewById(R.id.rcv_post_blogList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        setUpRecyclerView("");
        likesRef= FirebaseDatabase.getInstance().getReference();




    return v;
}

    private void setUpRecyclerView(String data) {
        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getUid();

        Query query = FirebaseDatabase.getInstance().getReference("Blog").orderByChild("status").equalTo("Share_" + currentUserId);

        options = new FirebaseRecyclerOptions.Builder<CreateBlogInfo>().setQuery(query, CreateBlogInfo.class).build();
        adapter = new FirebaseRecyclerAdapter<CreateBlogInfo, PostedBlogFragment.postedBlogViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull postedBlogViewHolder holder, int i, @NonNull CreateBlogInfo model) {
                final  String postkey = getRef(i).getKey();
                holder.setLikesbuttonStatus(postkey);
                holder.name.setText(model.getTripName());
                holder.date.setText(model.getShareTime());
                holder.description.setText(model.getDescription());
                if(model.getImageURL()==null){
                    Picasso.with(holder.imageView.getContext()).load(R.drawable.sea_background).into(holder.imageView);
                }else{
                    Picasso.with(holder.imageView.getContext()).load(model.getImageURL()).into(holder.imageView);
                }

                holder.v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        notifyItemChanged(i);
                        Intent intent = new Intent(getActivity(), BlogViewPosted.class);
                        intent.putExtra("blogID", getRef(i).getKey());
                        startActivity(intent);

                    }
                });
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
            public PostedBlogFragment.postedBlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.b_post_cardview, parent, false);
                return new postedBlogViewHolder(v);
            }


        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }
    class postedBlogViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView name, date, likes,description;
        View v;

        public postedBlogViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.bs_postImage);
            date = itemView.findViewById(R.id.bs_post_createDay);
            name = itemView.findViewById(R.id.bs_post_title);
            description=itemView.findViewById(R.id.bs_post_description);

            v = itemView;
        }

        public void setLikesbuttonStatus(final String postkey){
            likes = itemView.findViewById(R.id.bs_likes_textview);
            likesRef = FirebaseDatabase.getInstance().getReference("Likes");
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String userId = user.getUid();

            likesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild(postkey)){
                        likescount = (int)dataSnapshot.child(postkey).getChildrenCount();
                        likes.setText(Integer.toString(likescount)+"likes");
                    }else {
                        likescount = (int)dataSnapshot.child(postkey).getChildrenCount();
                        likes.setText(Integer.toString(likescount)+"likes");
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }}
    public void showDeleteDialog(String key){
        final androidx.appcompat.app.AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        TextView title = new TextView(getContext());
        title.setText("Blog Info");
        title.setPadding(10, 10, 10, 10);
        title.setTextSize(28);
        title.setTypeface(title.getTypeface(), Typeface.BOLD);
        builder.setCustomTitle(title);
        builder.setMessage("Are you sure to delete?");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                mquery=FirebaseDatabase.getInstance().getReference("Blog");
                mquery.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            if(dataSnapshot.hasChild("imageURL")){
                                String db_image=dataSnapshot.child("imageURL").getValue().toString();
                                StorageReference fs= FirebaseStorage.getInstance().getReferenceFromUrl(db_image);
                                fs.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mquery.child(key).removeValue();
                                        delete(key);
                                        deletelikes(key);
                                        Toast.makeText(getContext(),"Successfully deleted",Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }else{
                                Toast.makeText(getContext(),"Successfully deleted",Toast.LENGTH_SHORT).show();
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
        Query refl= FirebaseDatabase.getInstance().getReference("Likes").child(key);
        Query ref= FirebaseDatabase.getInstance().getReference("Blog").child(key).child("BlogDetails").orderByChild("blogID").equalTo(key);
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
    private void deletelikes(String key){
        Query ref= FirebaseDatabase.getInstance().getReference("Likes").child(key);
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

}
