package com.marcopizarro.podcast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.parceler.Parcels;

import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Show;
import retrofit.client.Response;

public class CompareAdapter extends RecyclerView.Adapter<CompareAdapter.ViewHolder> {

    private Context context;
    private List<Post> posts;
    public static final String TAG = "PostsAdapter";

    public CompareAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post_profile, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvPostUsername;
        private final ImageView ivPostImage;
        private final TextView tvPostTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvPostUsername = itemView.findViewById(R.id.tvPostUsername);
            ivPostImage = itemView.findViewById(R.id.ivShowPhoto);
            tvPostTitle = itemView.findViewById(R.id.tvShowTitle);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {

                        SpotifyApi api = new SpotifyApi();
                        api.setAccessToken(MainActivity.getAuthToken());
                        SpotifyService spotify = api.getService();

                        spotify.getShow(posts.get(position).getPodcast(), new SpotifyCallback<Show>() {
                            @Override
                            public void failure(SpotifyError error) {
                            }

                            @Override
                            public void success(Show show, Response response) {
                                Intent intent = new Intent(view.getContext(), DetailActivity.class);
                                intent.putExtra("show", Parcels.wrap(show));
                                view.getContext().startActivity(intent);
                            }
                        });
                    }
                }
            });
        }

        public void bind(Post post) {
            setPlaceholders();

            SpotifyApi api = new SpotifyApi();
            api.setAccessToken(MainActivity.getAuthToken());
            SpotifyService spotify = api.getService();

            spotify.getShow(post.getPodcast(), new SpotifyCallback<Show>() {
                @Override
                public void failure(SpotifyError error) {
                    Log.i(TAG, "error fetching", error);
                }

                @Override
                public void success(Show show, Response response) {
                    setContent(post, show);
                }
            });

        }

        @SuppressLint("DefaultLocale")
        private void setContent(Post post, Show show) {
            Glide.with(context)
                    .load(show.images.get(0).url)
                    .into(ivPostImage);
            tvPostTitle.setText(show.name);
            double differential = post.getRating();
            String comparison;
            if (differential > 0) {
                comparison = String.format("You Rated %1.1f★ Higher", differential);
            } else if (differential < 0) {
                differential = -differential;
                comparison = String.format("You Rated %1.1f★ Lower", differential);
            } else {
                comparison = "You Rated This Podcast The Same";
            }
            tvPostUsername.setText(comparison);
        }

        private void setPlaceholders() {
            tvPostTitle.setText("");
            Glide.with(context)
                    .load(R.drawable.loading_circle)
                    .into(ivPostImage);
            tvPostUsername.setText("");
        }
    }
}
