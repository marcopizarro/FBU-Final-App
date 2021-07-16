package com.marcopizarro.podcast;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Show;
import kaaes.spotify.webapi.android.models.ShowsPager;
import retrofit.client.Response;


public class ExploreFragment extends Fragment {

    public static final String TAG = "ExploreFragment";

    private RecyclerView rvResults;
    private ResultsAdapter resultsAdapter;
    private List<Show> allShows;
    private EditText etQuery;
    private Button btnSearch;

    public ExploreFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_explore, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvResults = view.findViewById(R.id.rvResults);
        allShows = new ArrayList<>();
        resultsAdapter = new ResultsAdapter(getContext(), allShows);
        rvResults.setAdapter(resultsAdapter);
        rvResults.setLayoutManager(new LinearLayoutManager(getContext()));

        etQuery = view.findViewById(R.id.etQuery);
        btnSearch = getActivity().findViewById(R.id.btnSearch);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpotifyApi api = new SpotifyApi();
                api.setAccessToken(MainActivity.getAuthToken());
                SpotifyService spotify = api.getService();

                spotify.searchShows(etQuery.getText().toString(), new SpotifyCallback<ShowsPager>() {
                    @Override
                    public void failure(SpotifyError error) {
                        Log.e(TAG, "err with search", error);
                    }

                    @Override
                    public void success(ShowsPager showsPager, Response response) {
                        resultsAdapter.clear();
                        allShows.addAll(showsPager.shows.items);
                        resultsAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }
}