package com.marcopizarro.podcast;

import android.annotation.SuppressLint;
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

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ShowsPager;
import retrofit.client.Response;


public class ExploreFragment extends Fragment {

    public static final String TAG = "ExploreFragment";

    private ResultsAdapter resultsAdapter;
    private EditText etQuery;

    MapView mapView;

    public ExploreFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_explore, container, false);
        mapView = (MapView) v.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {
                googleMap.getUiSettings().setMyLocationButtonEnabled(false);
//                googleMap.setMyLocationEnabled(true);

                MapsInitializer.initialize(getActivity());

                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(43.1, -87.9), 10);
                googleMap.animateCamera(cameraUpdate);
                googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(-31.952854, 115.857342))
                        .title("The Daily")
                        .snippet("The New York Times"));
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        RecyclerView rvResults = view.findViewById(R.id.rvResults);
        resultsAdapter = new ResultsAdapter(getContext(), new ArrayList<>());
        rvResults.setAdapter(resultsAdapter);
        rvResults.setLayoutManager(new LinearLayoutManager(getContext()));

        etQuery = view.findViewById(R.id.etQuery);
        Button btnSearch = view.findViewById(R.id.btnSearch);

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
                        resultsAdapter.addAll(showsPager.shows.items);
                    }
                });
            }
        });
    }
    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}