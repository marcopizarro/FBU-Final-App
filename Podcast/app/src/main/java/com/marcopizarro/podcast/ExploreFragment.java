package com.marcopizarro.podcast;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

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
    public static final int REQUEST_CODE = 21;

    private ResultsAdapter resultsAdapter;
    private EditText etQuery;

    MapView mapView;
    GoogleMap gMap;

    private FusedLocationProviderClient fusedLocationClient;

    boolean locationGranted = false;

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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());


        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        } else {
            locationGranted = true;
            makeMap();
        }

        return v;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "location Permission Granted", Toast.LENGTH_SHORT).show();
                locationGranted = true;
                makeMap();
            } else {
                Toast.makeText(getContext(), "location Permission Denied", Toast.LENGTH_SHORT).show();
                makeMap();
            }
        }

    }
    public void makeMap(){
        mapView.getMapAsync(new OnMapReadyCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {
                googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                if (locationGranted) {
                    googleMap.setMyLocationEnabled(true);
                }

                gMap = googleMap;
                gMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.style_json));

                MapsInitializer.initialize(getActivity());

                getDeviceLocation();

                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(43.1, -87.9), 10);
                gMap.animateCamera(cameraUpdate);
                gMap.addMarker(new MarkerOptions()
                        .position(new LatLng(-31.952854, 115.857342))
                        .title("The Daily")
                        .snippet("The New York Times"));
            }
        });
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
    private void getDeviceLocation() {
        try {
            @SuppressLint("MissingPermission") Task<Location> locationResult = fusedLocationClient.getLastLocation();
            locationResult.addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        Location location = task.getResult();
                        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(currentLatLng, 10);
                        gMap.moveCamera(update);
                    }
                }
            });
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }
}