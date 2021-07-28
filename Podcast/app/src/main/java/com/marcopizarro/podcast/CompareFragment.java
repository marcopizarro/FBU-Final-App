package com.marcopizarro.podcast;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Albums;
import kaaes.spotify.webapi.android.models.Show;
import kaaes.spotify.webapi.android.models.Shows;
import kaaes.spotify.webapi.android.models.ShowsPager;
import retrofit.client.Response;


public class CompareFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    public static final String TAG = "Compare Fragment";

    private java.util.List<ParseUser> allUsers;
    ParseUser user1;
    ParseUser user2;

    private ImageView ivImage1;
    private ImageView ivImage2;

    private TextView tvName1;
    private TextView tvName2;
    private TextView tvNoItems;
    private TextView tvCompatScore;

    private RecyclerView rvPodInCommon;
    private CompareAdapter adapterPodInCommon;

    private RecyclerView rvRec;
    private ProfileAdapter adapterPodRec;

    public CompareFragment() {
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
        return inflater.inflate(R.layout.fragment_compare, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user1 = MainActivity.getUserParse();


        tvNoItems = view.findViewById(R.id.tvNoItems);
        tvCompatScore = view.findViewById(R.id.tvCompatScore);

        rvPodInCommon = view.findViewById(R.id.rvPodInCommon);
        adapterPodInCommon = new CompareAdapter(getContext(), new ArrayList<>());
        rvPodInCommon.setAdapter(adapterPodInCommon);
        rvPodInCommon.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        rvRec = view.findViewById(R.id.rvRec);
        adapterPodRec = new ProfileAdapter(getContext(), new ArrayList<>());
        rvRec.setAdapter(adapterPodRec);
        rvRec.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        Spinner spinner = (Spinner) view.findViewById(R.id.spinnerCompare);
        spinner.setOnItemSelectedListener(this);

        ArrayList<String> listNames = new ArrayList<String>();
        allUsers = new ArrayList<ParseUser>();
        ParseQuery<ParseUser> queryLists = ParseQuery.getQuery(ParseUser.class);
        queryLists.addDescendingOrder("createdAt");
        queryLists.include(Post.KEY_USER);
        queryLists.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        queryLists.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(java.util.List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    allUsers.addAll(objects);
                    for (int i = 0; i < objects.size(); i++) {
                        listNames.add(objects.get(i).getUsername());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, listNames);
                    spinner.setAdapter(adapter);
                    spinner.setVisibility(View.VISIBLE);
                    setData(0);
                }
            }
        });
    }

    public void setData(int num) {
        user2 = allUsers.get(num);

        ParseQuery<Post> queryRecent = ParseQuery.getQuery(Post.class);
        queryRecent.addDescendingOrder("createdAt");
        queryRecent.include(Post.KEY_USER);
        queryRecent.whereEqualTo(Post.KEY_USER, user1);
        queryRecent.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> postsUser1, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Unable to fetch posts", e);
                    return;
                } else {
                    ParseQuery<Post> queryRecent = ParseQuery.getQuery(Post.class);
                    queryRecent.addDescendingOrder("createdAt");
                    queryRecent.include(Post.KEY_USER);
                    queryRecent.whereEqualTo(Post.KEY_USER, user2);
                    queryRecent.findInBackground(new FindCallback<Post>() {
                        @Override
                        public void done(List<Post> postsUser2, ParseException e) {
                            if (e != null) {
                                Log.e(TAG, "Unable to fetch posts", e);
                                return;
                            } else {
                                List<Post> intersection = intersection(postsUser1, postsUser2);
                                double comp = getCompatibilityScore(intersection, (postsUser1.size() + postsUser2.size()));
                                tvCompatScore.setText(String.format("You Compatibility Score is %2.2f%%", comp));
                                Log.i(TAG, String.valueOf(comp));
                                if (intersection.size() >= 1) {
                                    adapterPodInCommon.clear();
                                    adapterPodInCommon.addAll(intersection);

                                    List<String> descriptions = new ArrayList<String>();
                                    StringBuilder ids = new StringBuilder();
                                    for(int i = 0; i < intersection.size(); i++){
                                        Post post = intersection.get(i);
                                        ids.append(post.getPodcast());
                                        if (i != (intersection.size() - 1)){
                                            ids.append(",");
                                        }
                                    }

                                    SpotifyApi api = new SpotifyApi();
                                    api.setAccessToken(MainActivity.getAuthToken());
                                    SpotifyService spotify = api.getService();

                                    spotify.getShows(ids.toString(), new SpotifyCallback<Shows>() {
                                        @Override
                                        public void failure(SpotifyError error) {
                                            Log.i(TAG, "Failure Getting Podcasts");
                                        }

                                        @Override
                                        public void success(Shows shows, Response response) {
                                            for( Show show : shows.shows){
                                                descriptions.add(show.description);
                                            }
                                            String keyword = KeywordExtractor.getFrequecy(descriptions);
                                            Log.i(TAG, keyword);

                                            SpotifyApi apis = new SpotifyApi();
                                            apis.setAccessToken(MainActivity.getAuthToken());
                                            SpotifyService spotifyy = apis.getService();

                                            spotifyy.searchShows(keyword, new SpotifyCallback<ShowsPager>() {
                                                @Override
                                                public void failure(SpotifyError error) {
                                                    Log.i(TAG, "Failure Getting Recommendations");
                                                }

                                                @Override
                                                public void success(ShowsPager showsPager, Response response) {
                                                   Log.i(TAG, showsPager.shows.items.get(0).external_urls.get("spotify"));
                                                    Log.i(TAG, showsPager.shows.items.get(1).external_urls.get("spotify"));
                                                    Log.i(TAG, showsPager.shows.items.get(2).external_urls.get("spotify"));
                                                    List<Show> queryResults = showsPager.shows.items;
                                                    List<Post> posts = new ArrayList<Post>();

                                                    for(int i = 0; i < 5; i++){
                                                        Show show = queryResults.get(i);


                                                        Post post = new Post();
                                                        post.setRating(0);
                                                        post.setPodcast(show.id);
                                                        posts.add(post);
                                                    }


                                                    adapterPodRec.clear();
                                                    adapterPodRec.addAll(posts);
                                                }
                                            });
                                        }
                                    });

                                    rvPodInCommon.setVisibility(View.VISIBLE);
                                    tvNoItems.setVisibility(View.INVISIBLE);
                                    tvCompatScore.setVisibility(View.VISIBLE);
                                } else {
                                    rvPodInCommon.setVisibility(View.INVISIBLE);
                                    tvNoItems.setVisibility(View.VISIBLE);
                                    tvCompatScore.setVisibility(View.INVISIBLE);
                                }
                            }
                        }
                    });
                }
            }
        });


    }

    public static double getCompatibilityScore(List<Post> intersection, double totalLogs) {
        if (intersection.size() <= 0) {
            return 0;
        } else {
            double average = 0;
            for (Post post : intersection) {
                average += post.getRating();
            }
            double pt1 = 80.0 - Math.abs((average /= intersection.size()) * 18.0);
            double pt2 = 20.0 * (intersection.size() * 2.0 / totalLogs);
            return pt1 + pt2;
        }
    }

    public List<Post> intersection(List<Post> list1, List<Post> list2) {
        List<Post> list = new ArrayList<Post>();
        List<String> spotifyIds = new ArrayList<String>();
        spotifyIds.add("");
        for (int i = 0; i < list1.size(); i++) {
            for (int j = 0; j < list2.size(); j++) {
                if (list1.get(i).getPodcast().equals(list2.get(j).getPodcast())) {
                    Post post1 = list1.get(i);
                    Post post2 = list2.get(j);
                    double differential = post1.getRating() - post2.getRating();
                    if (!spotifyIds.contains(post1.getPodcast())) {
                        post1.setRating(differential);
                        spotifyIds.add(post1.getPodcast());
                        list.add(post1);
                    }
                }
            }
        }
        return list;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        setData(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}