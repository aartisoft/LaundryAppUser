package com.smartloan.smtrick.user_laundryapp.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smartloan.smtrick.user_laundryapp.R;
import com.smartloan.smtrick.user_laundryapp.Adapters.Service_Providers_Adapter;
import com.smartloan.smtrick.user_laundryapp.Models.User;

import java.util.ArrayList;
import java.util.List;

public class Fragment_Users extends Fragment {

    private DatabaseReference mdatabase;

    private List<User> mUsers;
    private ProgressDialog progressDialog;

    private RecyclerView userrecycler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        userrecycler = (RecyclerView) view.findViewById(R.id.userRecycler);
        userrecycler.setHasFixedSize(true);
        userrecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        progressDialog = new ProgressDialog(getContext());
        mUsers = new ArrayList<>();

        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        mdatabase = FirebaseDatabase.getInstance().getReference("users");
        mdatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {

                    User users = userSnapshot.getValue(User.class);
                    mUsers.add(users);
                }
                Service_Providers_Adapter usersAdapter = new Service_Providers_Adapter(mUsers);
                userrecycler.setAdapter(usersAdapter);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });


        return view;
    }
}
