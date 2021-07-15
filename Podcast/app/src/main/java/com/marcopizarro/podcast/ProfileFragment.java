package com.marcopizarro.podcast;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import kaaes.spotify.webapi.android.models.UserPrivate;


public class ProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment";

    ImageView ivProfileImage;
    TextView tvProfileName;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivProfileImage = getActivity().findViewById(R.id.ivProfileImage);
        tvProfileName = getActivity().findViewById(R.id.tvProfileName);

        UserPrivate userSpotify = MainActivity.getUserSpotify();
        if (userSpotify != null) {
            if (userSpotify.images != null & userSpotify.images.get(0).url != null){
            Glide.with(this)
                    .load(userSpotify.images.get(0).url)
                    .circleCrop()
                    .into(ivProfileImage);
            }
            tvProfileName.setText(userSpotify.display_name);
        }
    }
}