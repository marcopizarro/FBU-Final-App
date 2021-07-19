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
import kaaes.spotify.webapi.android.models.ShowSimple;
import retrofit.client.Response;

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ViewHolder> {

    private Context context;
    private List<Show> shows;
    public static final String TAG = "ResultsAdapter";

    public ResultsAdapter(Context context, List<Show> shows) {
        this.context = context;
        this.shows = shows;
    }

    public void clear() {
        shows.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Show> list) {
        shows.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_show, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Show show = shows.get(position);
        holder.bind(show);
    }

    @Override
    public int getItemCount() {
        return shows.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvShowTitle;
        private TextView tvShowPublisher;
        private ImageView ivShowPhoto;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivShowPhoto = itemView.findViewById(R.id.ivShowPhoto);
            tvShowTitle = itemView.findViewById(R.id.tvShowTitle);
            tvShowPublisher = itemView.findViewById(R.id.tvShowPublisher);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Show show = shows.get(position);
                        Intent intent = new Intent(view.getContext(), DetailActivity.class);
                        intent.putExtra("show", Parcels.wrap(show));
                        view.getContext().startActivity(intent);
                    }
                }
            });
        }

        public void bind(Show show) {
                    Glide.with(context)
                            .load(show.images.get(0).url)
                            .into(ivShowPhoto);
                    tvShowTitle.setText(show.name);
                    tvShowPublisher.setText(show.publisher);
        }
    }
}
