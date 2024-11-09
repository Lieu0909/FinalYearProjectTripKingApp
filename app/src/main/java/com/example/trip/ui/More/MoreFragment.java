package com.example.trip.ui.More;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.trip.Login_Registration.NewUserMain;
import com.example.trip.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MoreFragment extends Fragment {
private CardView logout;
private FirebaseAuth firebaseAuth;
    public static MoreFragment newInstance() {
        return new MoreFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_more, container, false);

        firebaseAuth=firebaseAuth.getInstance();
        logout=v.findViewById(R.id.select_logout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                Toast.makeText(getActivity(),"Signed Out",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(getActivity(), NewUserMain.class);
                startActivity(intent);

            }
        });

        return v;
    }

}
