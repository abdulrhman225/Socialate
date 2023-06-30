package com.example.postappwithkolin.UI.Fragment;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.postappwithkolin.Model.Post_recycler;
import com.example.postappwithkolin.Model.UserPost;
import com.example.postappwithkolin.R;
import com.example.postappwithkolin.SourceData.SAGDataFromDataBase;
import com.example.postappwithkolin.UI.MainActivity;
import com.example.postappwithkolin.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    SAGDataFromDataBase model = new SAGDataFromDataBase();
    Post_recycler rv;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();



        binding.profileUserName.setText(mAuth.getCurrentUser().getDisplayName());
        Picasso.get().load(mAuth.getCurrentUser().getPhotoUrl()).into(binding.profilePhoto);

        model = new ViewModelProvider(this).get(SAGDataFromDataBase.class);
        model.getPostsWitSameUserName(binding.profileUserName.getText().toString());

        model.getMutable().observe(getViewLifecycleOwner(), new Observer<ArrayList<UserPost>>() {
            @Override
            public void onChanged(ArrayList<UserPost> userPosts) {
                rv = new Post_recycler(userPosts, new Post_recycler.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {

                    }
                }, new Post_recycler.onCommentButtonClick() {
                    @Override
                    public void onCommentClick(int position) {

                    }
                });
                binding.profilePosts.setAdapter(rv);
                binding.profilePosts.setLayoutManager(new LinearLayoutManager(getContext()));
                binding.profilePosts.setHasFixedSize(true);
            }
        });

        return v;
    }

}