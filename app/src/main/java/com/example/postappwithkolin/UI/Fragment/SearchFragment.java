package com.example.postappwithkolin.UI.Fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.postappwithkolin.Model.UserInformation;
import com.example.postappwithkolin.Model.users_rv;
import com.example.postappwithkolin.R;
import com.example.postappwithkolin.Room.Users.UserTable;
import com.example.postappwithkolin.SourceData.SAGDataFromDataBase;
import com.example.postappwithkolin.UI.MainActivity;
import com.example.postappwithkolin.databinding.FragmentSearchBinding;
import com.google.firebase.auth.UserInfo;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    FragmentSearchBinding binding;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ArrayList<UserInformation> users = new ArrayList<>();


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    onChangeListener1 onChangeListener;
    onCompleteListener1 onCompleteListener;
    users_rv rv;
    public static ArrayList<UserInformation> informations = new ArrayList<>();
    public static RecyclerView recyclerView;
    SAGDataFromDataBase model = new SAGDataFromDataBase();

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onChangeListener = (onChangeListener1) context;
        onCompleteListener = (onCompleteListener1) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();

        recyclerView = binding.searchRv;


        binding.SearchFragmentSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (MainActivity.Companion.isConnected())
                    onChangeListener.onChange(newText, informations);
                return true;
            }
        });

        model = new ViewModelProvider(this).get(SAGDataFromDataBase.class);
        updateData();

        return v;
    }


    public void updateData() {
        if (MainActivity.Companion.isConnected()) {
            model.getAllUsers();
            model.get_mutable().observe(getViewLifecycleOwner(), new Observer<ArrayList<UserInformation>>() {
                @Override
                public void onChanged(ArrayList<UserInformation> userInformations) {
                    informations = userInformations;
                    rv = new users_rv(userInformations, new users_rv.OnCompleteListener() {
                        @Override
                        public void onComplete(int position) {
                            onCompleteListener.onComplete(position, informations);
                        }
                    });
                    binding.searchRv.setAdapter(rv);
                    binding.searchRv.setLayoutManager(new LinearLayoutManager(getContext()));
                    binding.searchRv.setHasFixedSize(true);
                }
            });
        } else {

            MainActivity.Companion.getDb().userDao().getAllUsers().subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new io.reactivex.rxjava3.core.Observer<List<UserTable>>() {
                        @Override
                        public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@io.reactivex.rxjava3.annotations.NonNull List<UserTable> userTables) {

                            for (UserTable user : userTables) {
                                users.add(new UserInformation(user.getUserName(), user.getEmail(), user.getUserPhoto()));
                            }

                            informations = users;
                            rv = new users_rv(users, new users_rv.OnCompleteListener() {
                                @Override
                                public void onComplete(int position) {
                                }
                            });
                            binding.searchRv.setAdapter(rv);
                            binding.searchRv.setLayoutManager(new LinearLayoutManager(getContext()));
                            binding.searchRv.setHasFixedSize(true);

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


    public interface onChangeListener1 {
        void onChange(String newText, ArrayList<UserInformation> informations);
    }

    public interface onCompleteListener1 {
        void onComplete(int position, ArrayList<UserInformation> informations);
    }
}