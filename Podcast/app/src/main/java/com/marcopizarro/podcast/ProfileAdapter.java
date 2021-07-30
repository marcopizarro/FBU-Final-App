package com.marcopizarro.podcast;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.parceler.Parcels;

import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Show;
import retrofit.client.Response;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {

    private Context context;
    private List<Post> posts;
    public static final String TAG = "PostsAdapter";

    public ProfileAdapter(Context context, List<Post> posts) {
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

        private TextView tvPostUsername;
        private ImageView ivPostImage;
        private TextView tvPostTitle;

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

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Post post = posts.get(position);

                        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
                        query.getInBackground(post.getObjectId(), new GetCallback<Post>() {
                            @Override
                            public void done(Post object, ParseException e) {
                                object.deleteInBackground(new DeleteCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        Toast.makeText(context, "Post Deleted, Please Reload", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                    return false;
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
            StringBuilder stars = new StringBuilder();
            for (double i = 1; i <= post.getRating(); i++) {
                stars.append("★");
            }
            tvPostUsername.setText(stars);
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
