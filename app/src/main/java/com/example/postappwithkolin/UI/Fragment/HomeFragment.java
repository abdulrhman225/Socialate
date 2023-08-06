package com.example.postappwithkolin.UI.Fragment;

import static com.google.firebase.messaging.Constants.MessageNotificationKeys.TAG;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.postappwithkolin.Model.Post_recycler;
import com.example.postappwithkolin.Model.UserPost;
import com.example.postappwithkolin.Room.Post.postDataBase;
import com.example.postappwithkolin.Room.Post.postTable;
import com.example.postappwithkolin.Room.Post.resultPosts;
import com.example.postappwithkolin.SourceData.SAGDataFromDataBase;
import com.example.postappwithkolin.UI.MainActivity;
import com.example.postappwithkolin.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    SAGDataFromDataBase model = new SAGDataFromDataBase();

    public static ArrayList<UserPost> posts = new ArrayList<>();
    Post_recycler rv;

    OnItemClickListener1 onItemClickListener1;
    onCommentClick commentClick;

    public static  boolean isEmpty = false;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onItemClickListener1 = (OnItemClickListener1) context;
        commentClick = (onCommentClick) context;
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
        binding = FragmentHomeBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();

        binding.mainUserName.setText(mAuth.getCurrentUser().getDisplayName());
        Picasso.get().load(mAuth.getCurrentUser().getPhotoUrl()).into(binding.mainIVUserImage);


        model = new ViewModelProvider(HomeFragment.this).get(SAGDataFromDataBase.class);
        updateData();


        return v;
    }


    public void updateData() {
        Toast.makeText(getContext(), MainActivity.Companion.isConnected()+"", Toast.LENGTH_SHORT).show();

        if(MainActivity.Companion.isConnected()) {
            model.getPost();
            model.getMutable().observe(getViewLifecycleOwner(), new Observer<ArrayList<UserPost>>() {
                @Override
                public void onChanged(ArrayList<UserPost> userPosts) {
                    posts.addAll(userPosts);
                    rv = new Post_recycler(userPosts, new Post_recycler.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            onItemClickListener1.onItemClick1(position);
                        }
                    }, new Post_recycler.onCommentButtonClick() {
                        @Override
                        public void onCommentClick(int position) {
                            commentClick.onCommentclick(position);
                        }
                    });
                    binding.MainRecycler.setAdapter(rv);
                    binding.MainRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
                    binding.MainRecycler.setHasFixedSize(true);

                }
            });
        }else{
            MainActivity.Companion.getDatabase().postDao().getAllPosts().observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new io.reactivex.rxjava3.core.Observer<List<postTable>>() {
                        @Override
                        public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@io.reactivex.rxjava3.annotations.NonNull List<postTable> postTables) {

                            if (!postTables.isEmpty()) {
                                ArrayList<UserPost> post = new ArrayList();

                                for (postTable po : postTables) {
                                    post.add(new UserPost(po.getUserName(), po.getPostName(), po.getPostImage(), po.getUserImage()));

                                }


                                posts.addAll(post);
                                rv = new Post_recycler(post, new Post_recycler.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(int position) {
                                        onItemClickListener1.onItemClick1(position);
                                    }
                                }, new Post_recycler.onCommentButtonClick() {
                                    @Override
                                    public void onCommentClick(int position) {
                                        commentClick.onCommentclick(position);
                                    }
                                });
                                binding.MainRecycler.setAdapter(rv);
                                binding.MainRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
                                binding.MainRecycler.setHasFixedSize(true);

                            }
                        }

                        @Override
                        public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }

    }

    public interface OnItemClickListener1 {
        void onItemClick1(int position);
    }

    public interface onCommentClick {
        void onCommentclick(int position);
    }
}
