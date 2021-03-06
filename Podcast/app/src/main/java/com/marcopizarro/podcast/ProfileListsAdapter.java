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

public class ProfileListsAdapter extends RecyclerView.Adapter<ProfileListsAdapter.ViewHolder> {

    private Context context;
    private List<com.marcopizarro.podcast.List> lists;
    public static final String TAG = "PostsAdapter";

    public ProfileListsAdapter(Context context, List<com.marcopizarro.podcast.List> lists) {
        this.context = context;
        this.lists = lists;
    }

    public void clear() {
        lists.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<com.marcopizarro.podcast.List> list) {
        lists.addAll(list);
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
        com.marcopizarro.podcast.List list = lists.get(position);
        holder.bind(list);
    }

    @Override
    public int getItemCount() {
        return lists.size();
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
                        Intent intent = new Intent(view.getContext(), ListActivty.class);
                        intent.putExtra("list", Parcels.wrap(lists.get(position)));
                        view.getContext().startActivity(intent);
                    }
                }
            });
        }

        public void bind(com.marcopizarro.podcast.List list) {
            setPlaceholders();
            setContent(list);
        }

        private void setContent(com.marcopizarro.podcast.List list) {
            Glide.with(context)
                    .load(list.getCover().getUrl())
                    .into(ivPostImage);
            tvPostTitle.setText(list.getName());
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
