package com.example.postappwithkolin.UI.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.postappwithkolin.Model.Post_recycler;
import com.example.postappwithkolin.Model.UserPost;
import com.example.postappwithkolin.Room.Post.postTable;
import com.example.postappwithkolin.SourceData.SAGDataFromDataBase;
import com.example.postappwithkolin.UI.MainActivity;
import com.example.postappwithkolin.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.annotations.NonNull;

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




    public static boolean isEmpty = false;

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
        boolean isTheDeviceConnectedToInternet = MainActivity.Companion.isConnected();

        if (isTheDeviceConnectedToInternet) {
           getTheLivePostsData();

        }
        else
        {
         getTheDataFromLocalRoom();
        }

    }

    public void getTheLivePostsData(){
        model.getPost();

        model.getMutable().observe(getViewLifecycleOwner(), new androidx.lifecycle.Observer<ArrayList<UserPost>>() {
            @Override
            public void onChanged(ArrayList<UserPost> userPosts) {

                PutTheDataInRecyclerView(userPosts);
                updateTheContentOfRoomDataBase(userPosts);
            }
        });


    }

    public void PutTheDataInRecyclerView(ArrayList<UserPost> userPosts){
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

    public void updateTheContentOfRoomDataBase(ArrayList<UserPost> userPosts){
       ResultPost resultPost = new ResultPost();
       resultPost.UpdateRoomData(userPosts);
    }

    public List<postTable> convertListFromUserPostToPostTable(ArrayList<UserPost> userPosts){
        List<postTable> post = new ArrayList<>();
        for (UserPost Posts : userPosts){
            post.add(new postTable(Posts.getUserPhoto() , Posts.getPostImage() , Posts.getUserName() , Posts.getPostComment()));
        }
        return post;
    }

    public void getTheDataFromLocalRoom(){
        MainActivity.Companion.getDatabase().postDao().getAllPosts().observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.computation())
                .subscribe(new Observer<List<postTable>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull List<postTable> postTables) {

                        ArrayList<UserPost> post = convertListFromPostTableToUserPost(postTables);
                        putLocalDataInRecyclerView(post);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public ArrayList<UserPost> convertListFromPostTableToUserPost(List<postTable> postTables){
        ArrayList<UserPost> post = new ArrayList<>();

        for (postTable Posts : postTables) {
            post.add(new UserPost(Posts.getUserName(), Posts.getPostName(), Posts.getPostImage(), Posts.getUserImage()));

        }
        return post;
    }

    public void putLocalDataInRecyclerView(ArrayList<UserPost> post){
        rv = new Post_recycler(post, new Post_recycler.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
            }
        }, new Post_recycler.onCommentButtonClick() {
            @Override
            public void onCommentClick(int position) {
            }
        });
        binding.MainRecycler.setAdapter(rv);
        binding.MainRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.MainRecycler.setHasFixedSize(true);
    }


    public interface OnItemClickListener1 {
        void onItemClick1(int position);
    }

    public interface onCommentClick {
        void onCommentclick(int position);
    }


    public class ResultPost{
        public void Insert(ArrayList<UserPost> userPosts){
            List<postTable> post = convertListFromUserPostToPostTable(userPosts);

            MainActivity.Companion.getDatabase().postDao().Insert(post)
                    .subscribeOn(Schedulers.computation())
                    .subscribe();
        }

        public void Delete (){
            MainActivity.Companion.getDatabase().postDao().deleteFromPostTable()
                    .subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe();
        }

        public void UpdateRoomData(ArrayList<UserPost> userPosts){
            MainActivity.Companion.getDatabase().postDao().getAllPosts()
                    .subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<postTable>>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull List<postTable> postTables) {
                            if(userPosts.size() > postTables.size()) {
                                Delete();
                                Insert(userPosts);
                            }



                        }

                        @Override
                        public void onError(@NonNull Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }

    }

}
