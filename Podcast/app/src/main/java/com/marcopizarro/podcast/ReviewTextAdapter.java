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

public class ReviewTextAdapter extends RecyclerView.Adapter<ReviewTextAdapter.ViewHolder> {

    private Context context;
    private List<Post> posts;
    public static final String TAG = "ReviewTextAdapter";

    public ReviewTextAdapter(Context context, List<Post> posts) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
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

        private TextView tvDesc;
        private TextView tvName;
        private TextView tvStar;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDesc = itemView.findViewById(R.id.tvDesc);
            tvName = itemView.findViewById(R.id.tvName);
            tvStar = itemView.findViewById(R.id.tvStar);
        }

        @SuppressLint("DefaultLocale")
        public void bind(Post post) {
            tvDesc.setText(post.getCaption());
            tvStar.setText(String.format("%1.1f★", post.getRating()));
            tvName.setText(post.getUser().getUsername());
        }
    }
}
