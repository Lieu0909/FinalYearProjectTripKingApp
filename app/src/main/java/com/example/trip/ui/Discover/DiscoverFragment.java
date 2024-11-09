package com.example.trip.ui.Discover;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import com.example.trip.ui.Blogger.CreateBlogInfo;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class DiscoverFragment extends Fragment {
    View v;
    private FirebaseAuth auth;
    private String currentUserId;
    FirebaseRecyclerOptions<CreateBlogInfo> options;
    FirebaseRecyclerAdapter<CreateBlogInfo, DiscoverFragment.momentBlogViewHolder> adapter;
    private RecyclerView recyclerView,recyclerView_search;
    DatabaseReference mquery,likesrefernce,blogRef;
    Boolean likechecker = false;
    FirebaseRecyclerAdapter<CreateBlogInfo, DiscoverFragment.momentBlogViewHolder>adapter1;
    TextView searchTv;

    public static DiscoverFragment newInstance() {
        return new DiscoverFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        v= inflater.inflate(R.layout.fragment_discover, container, false);

        recyclerView=v.findViewById(R.id.rcv_moment_blogList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        setUpRecyclerView("");
        likesrefernce = FirebaseDatabase.getInstance().getReference("Likes");
        FloatingActionButton fab = v.findViewById(R.id.fab_searchBlog);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                View search=getLayoutInflater().inflate(R.layout.d_search_view,null);
                TextView title = new TextView(getContext());
                title.setText("Search your desire");
                title.setPadding(10, 10, 10, 10);
                title.setTextSize(20);
                title.setTypeface(title.getTypeface(), Typeface.BOLD);
                builder.setCustomTitle(title);
                builder.setView(search);
                recyclerView_search = search.findViewById(R.id.rcv_d_searchlist);
                recyclerView_search.setHasFixedSize(true);
                recyclerView_search.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView_search.setVisibility(View.GONE);

                searchTv=search.findViewById(R.id.d_search_field);

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String word=searchTv.getText().toString();
                        firebaseBlogSearch(word);
                        recyclerView_search.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

        return v;
    }


    private void firebaseBlogSearch(String searchText) {

        Toast.makeText(getContext(), "Started Search", Toast.LENGTH_LONG).show();
        blogRef= FirebaseDatabase.getInstance().getReference("Blog");
        Query firebaseSearchQuery = blogRef.orderByChild("tripName").startAt(searchText).endAt(searchText + "\uf8ff");

        options=new FirebaseRecyclerOptions.Builder<CreateBlogInfo>().setQuery(firebaseSearchQuery,CreateBlogInfo.class).build();
        adapter1=new FirebaseRecyclerAdapter<CreateBlogInfo, momentBlogViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull DiscoverFragment.momentBlogViewHolder holder, final int position, @NonNull CreateBlogInfo model) {
                final  String postkey = getRef(position).getKey();
                holder.name.setText(model.getTripName());
                holder.date.setText(model.getDate());
                Picasso.with(holder.imageView.getContext()).load(model.getImageURL()).into(holder.imageView);
                holder.setLikesbuttonStatus(postkey);
                holder.v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        notifyDataSetChanged();
                        Intent intent = new Intent(getActivity(), DiscoverViewedLayout.class);
                        intent.putExtra("blogID", getRef(position).getKey());
                        startActivity(intent);
                    }
                });

                holder.likebutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        likechecker = true;

                        likesrefernce.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (likechecker.equals(true)){
                                    if (dataSnapshot.child(postkey).hasChild(currentUserId)){
                                        likesrefernce.child(postkey).child(currentUserId).removeValue();
                                        likechecker = false;
                                        notifyDataSetChanged();
                                    }else {
                                        likesrefernce.child(postkey).child(currentUserId).setValue(true);
                                        likechecker = false;
                                        notifyDataSetChanged();

                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }

                        });
                    }
                });


            }

            @NonNull
            @Override
            public DiscoverFragment.momentBlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.b_moment_cardview,parent,false);
                return new momentBlogViewHolder(v);
            }


        };

        adapter1.startListening();
        recyclerView.setAdapter(adapter1);

    }


    private void setUpRecyclerView(String data) {
        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getUid();

        Query query = FirebaseDatabase.getInstance().getReference("Blog").orderByChild("status").startAt("Share_");

        options = new FirebaseRecyclerOptions.Builder<CreateBlogInfo>().setQuery(query, CreateBlogInfo.class).build();

        adapter = new FirebaseRecyclerAdapter<CreateBlogInfo, DiscoverFragment.momentBlogViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull DiscoverFragment.momentBlogViewHolder holder, int i, @NonNull CreateBlogInfo model) {
                final  String postkey = getRef(i).getKey();
                holder.name.setText(model.getTripName());
                holder.date.setText(model.getDate());
                holder.description.setText(model.getDescription());
                Picasso.with(holder.imageView.getContext()).load(model.getImageURL()).into(holder.imageView);

                holder.v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        notifyDataSetChanged();
                        Intent intent = new Intent(getActivity(), DiscoverViewedLayout.class);
                        intent.putExtra("blogID", getRef(i).getKey());
                        startActivity(intent);
                    }
                });
                holder.setLikesbuttonStatus(postkey);

                holder.likebutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        likechecker = true;

                        likesrefernce.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (likechecker.equals(true)){
                                    if (dataSnapshot.child(postkey).hasChild(currentUserId)){
                                        likesrefernce.child(postkey).child(currentUserId).removeValue();
                                        likechecker = false;
                                        notifyDataSetChanged();
                                    }else {
                                        likesrefernce.child(postkey).child(currentUserId).setValue(true);
                                        likechecker = false;
                                        notifyDataSetChanged();

                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }

                        });
                    }
                });
            }

            @NonNull
            @Override
            public DiscoverFragment.momentBlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.b_moment_cardview, parent, false);
                return new momentBlogViewHolder(v);
            }


        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);



    }
    class momentBlogViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView name, date,description;
        ImageButton  likebutton;
        View v;
        TextView likesdisplay;
        int likescount;
        DatabaseReference likesref;

        public momentBlogViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.m_postImage);
            date = itemView.findViewById(R.id.m_post_createDay);
            name = itemView.findViewById(R.id.m_post_title);
            likebutton = itemView.findViewById(R.id.like_btn);
            likesdisplay=itemView.findViewById(R.id.likes_textview);
            description=itemView.findViewById(R.id.m_post_description);

            v = itemView;
        }
        public void setLikesbuttonStatus(final String postkey){
            likebutton = itemView.findViewById(R.id.like_btn);
            likesdisplay = itemView.findViewById(R.id.likes_textview);
            likesref = FirebaseDatabase.getInstance().getReference("Likes");
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String userId = user.getUid();
            String likes = "likes";

            likesref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){

                    if (dataSnapshot.child(postkey).hasChild(userId)){
                        likescount = (int)dataSnapshot.child(postkey).getChildrenCount();
                        likebutton.setImageResource(R.drawable.ic_like);
                        likesdisplay.setText(Integer.toString(likescount)+likes);
                    }else {
                        likescount = (int)dataSnapshot.child(postkey).getChildrenCount();
                        likebutton.setImageResource(R.drawable.ic_dislike);
                        likesdisplay.setText(Integer.toString(likescount)+likes);
                    }

                }}

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }




}
