package com.marcopizarro.podcast;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
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

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private Context context;
    private List<Post> posts;
    public static final String TAG = "PostsAdapter";

    public PostsAdapter(Context context, List<Post> posts) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
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

        private TextView tvPostUsername;
        private TextView tvPostPublisher;
        private ImageView ivPostImage;
        private TextView tvPostTitle;
        private TextView tvPostDesc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvPostUsername = itemView.findViewById(R.id.tvPostUsername);
            ivPostImage = itemView.findViewById(R.id.ivShowPhoto);
            tvPostTitle = itemView.findViewById(R.id.tvShowTitle);
            tvPostPublisher = itemView.findViewById(R.id.tvShowPublisher);
            tvPostDesc = itemView.findViewById(R.id.tvPostDesc);

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
                                Pair<View, String> p1 = Pair.create((View)ivPostImage, "profile");
                                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, p1);
                                view.getContext().startActivity(intent, options.toBundle());
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

        private void setContent(Post post, Show show) {
            Glide.with(context)
                    .load(show.images.get(0).url)
                    .into(ivPostImage);
            tvPostTitle.setText(show.name);
            tvPostPublisher.setText(show.publisher);
            StringBuilder stars = new StringBuilder();
            for (double i = 1; i <= post.getRating(); i++) {
                stars.append("★");
            }

            tvPostDesc.setText(post.getCaption());

            tvPostUsername.setText(String.format("%s rated %s", post.getUser().getUsername(), stars));
            tvPostUsername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), ProfileActivity.class);
                    intent.putExtra("user", Parcels.wrap(post.getUser()));
                    itemView.getContext().startActivity(intent);
                }
            });
        }

        private void setPlaceholders() {
            tvPostTitle.setText("");
            tvPostPublisher.setText("");
            Glide.with(context)
                    .load(R.drawable.loading_circle)
                    .into(ivPostImage);
            tvPostUsername.setText("");
            tvPostDesc.setText("");
        }
    }
}
