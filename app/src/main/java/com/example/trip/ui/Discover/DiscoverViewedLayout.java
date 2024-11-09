package com.example.trip.ui.Discover;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trip.R;
import com.example.trip.ui.Blogger.BlogDetailsInfo;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class DiscoverViewedLayout extends AppCompatActivity {
    TextView t_name,s_date,e_date,likes,passID,description;
    ImageView tripImage;
    RecyclerView recyclerView;
    private int likescount;
    private String passBID,uID,Name,Start,End,destinationN;
    private Integer dayNo;
    private DatabaseReference blogRef,likesRef;
    FirebaseRecyclerOptions<BlogDetailsInfo> options;
    FirebaseAuth auth;
    private FirebaseRecyclerAdapter<BlogDetailsInfo, DiscoverViewedLayout.DiscoverViewHolder> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_viewed_layout);

        Intent intent=getIntent();
        passBID=getIntent().getStringExtra("blogID");
        Toolbar toolbar=findViewById(R.id.toolbar_View);
        setSupportActionBar(toolbar);

        passID=findViewById(R.id.d_passID);
        t_name=findViewById(R.id.d_tripName);
        tripImage=findViewById(R.id.d_imagePic);
        likes=findViewById(R.id.d_likes_textview);
        s_date=findViewById(R.id.d_tripStart);
        e_date=findViewById(R.id.d_tripEnd);
        description=findViewById(R.id.d_tripDescription);
        auth=FirebaseAuth.getInstance();
        uID=auth.getUid();

        blogRef= FirebaseDatabase.getInstance().getReference();
        likesRef= FirebaseDatabase.getInstance().getReference();
        //retrieve likes details
        likesRef.child("Likes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(passBID)){
                    likescount = (int)dataSnapshot.child(passBID).getChildrenCount();
                    likes.setText(Integer.toString(likescount));
                }else {
                    likescount = (int)dataSnapshot.child(passBID).getChildrenCount();
                    likes.setText(Integer.toString(likescount)+"likes");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //retrieve blog details
        blogRef.child("Blog").child(passBID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Name=dataSnapshot.child("tripName").getValue().toString();
                String Description=dataSnapshot.child("description").getValue().toString();
                Start=dataSnapshot.child("dayStart").getValue().toString();
                End=dataSnapshot.child("dayEnd").getValue().toString();
                String image=dataSnapshot.child("imageURL").getValue().toString();
                dayNo= dataSnapshot.child("dayNo").getValue(Integer.class);

                Picasso.with(getApplicationContext()).load(image).into(tripImage);
                t_name.setText(Name);
                description.setText(Description);
                s_date.setText(Start);
                e_date.setText(End);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        recyclerView=findViewById(R.id.rcv_d_Blog);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        setUpRecyclerView("");


    }
   //toolbar/
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_d,menu);
        return true;

    }

    private void setUpRecyclerView(String data) {
        Query query=FirebaseDatabase.getInstance().getReference("Blog").child(passBID).child("BlogDetails").orderByChild("blogID").equalTo(passBID);

        options=new FirebaseRecyclerOptions.Builder<BlogDetailsInfo>().setQuery(query,BlogDetailsInfo.class).build();
        adapter=new FirebaseRecyclerAdapter<BlogDetailsInfo, DiscoverViewedLayout.DiscoverViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull DiscoverViewedLayout.DiscoverViewHolder holder, final int position, @NonNull BlogDetailsInfo model) {
                holder.s_name.setText(model.getDestinationName());
                holder.s_descript.setText(model.getDestinationDescription());
                holder.s_budget.setText("RM"+ String.valueOf(model.getDestinationBudget()));
                Picasso.with(holder.s_Pic.getContext()).load(model.getDestinationPic()).into(holder.s_Pic);
                destinationN=model.destinationName;
            }

            @NonNull
            @Override
            public DiscoverViewedLayout.DiscoverViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.b_view_blog,parent,false);
                return new DiscoverViewHolder(v);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);

    }
    public class DiscoverViewHolder extends RecyclerView.ViewHolder {
        private ImageView s_Pic;
        private TextView s_name,s_descript,s_budget;
        private View v;

        public DiscoverViewHolder(View view) {
            super(view);
            s_Pic = view.findViewById(R.id.vv_Pic);
            s_name = view.findViewById(R.id.vv_name);
            s_descript = view.findViewById(R.id.vv_description);
            s_budget = view.findViewById(R.id.vv_budget);
            v=itemView;
        }

    }}
