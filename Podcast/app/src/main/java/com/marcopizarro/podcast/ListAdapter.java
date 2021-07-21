package com.marcopizarro.podcast;

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

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private Context context;
    private List<String> shows;
    public static final String TAG = "PostsAdapter";

    public ListAdapter(Context context, List<String> shows) {
        this.context = context;
        this.shows = shows;
    }

    public void clear() {
        shows.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<String> shows) {
        this.shows.addAll(shows);
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
        String show = shows.get(position);
        holder.bind(show);
    }

    @Override
    public int getItemCount() {
        return shows.size();
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

                        spotify.getShow(shows.get(position), new SpotifyCallback<Show>() {
                            @Override
                            public void failure(SpotifyError error) {

                            }

                            @Override
                            public void success(Show show, Response response) {
//                                Intent intent = new Intent(view.getContext(), DetailActivity.class);
//                                intent.putExtra("list", Parcels.wrap(show));
//                                view.getContext().startActivity(intent);
                            }
                        });
                    }
                }
            });
        }

        public void bind(String show) {
            setPlaceholders();

            SpotifyApi api = new SpotifyApi();
            api.setAccessToken(MainActivity.getAuthToken());
            SpotifyService spotify = api.getService();

            spotify.getShow(show, new SpotifyCallback<Show>() {
                @Override
                public void failure(SpotifyError error) {
                    Log.i(TAG, "error fetching", error);
                }

                @Override
                public void success(Show show, Response response) {
                    setContent(show);
                }
            });

        }

        private void setContent(Show show) {
            Glide.with(context)
                    .load(show.images.get(0).url)
                    .into(ivPostImage);
            tvPostTitle.setText(show.name);
            tvPostPublisher.setText(show.publisher);

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
