package com.marcopizarro.podcast;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ShowSimple;
import retrofit.client.Response;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private Context context;
    private List<Post> posts;
    public static final String TAG = "PostsAdapter";

    public PostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    // Clean all elements of the recycler
    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
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
            ivPostImage = itemView.findViewById(R.id.ivPostPhoto);
            tvPostTitle = itemView.findViewById(R.id.tvPostTitle);
            tvPostPublisher = itemView.findViewById(R.id.tvPostPublisher);
            tvPostDesc = itemView.findViewById(R.id.tvPostDesc);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition(); // gets item position
                    if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it
//                        Post post = posts.get(position);
//                        Intent intent = new Intent(view.getContext(), DetailsActivity.class);
//                        intent.putExtra("post", Parcels.wrap(post));
//                        view.getContext().startActivity(intent);
                    }
                }
            });
        }

        public void bind(Post post) {
            StringBuilder stars = new StringBuilder();
            for (double i = 1; i <= post.getRating(); i++) {
                stars.append("★");
            }
            tvPostUsername.setText(String.format("%s rated %s", post.getUser().getUsername(), stars));
            tvPostDesc.setText(post.getCaption());

            SpotifyApi api = new SpotifyApi();
            api.setAccessToken(MainActivity.getAuthToken());
            SpotifyService spotify = api.getService();

            spotify.getShow(post.getPodcast(), new SpotifyCallback<ShowSimple>() {
                @Override
                public void failure(SpotifyError error) {
                    Log.i(TAG, "error fetching", error);
                }

                @Override
                public void success(ShowSimple showSimple, Response response) {
                    Log.i(TAG, "success");

                    Glide.with(context)
                            .load(showSimple.images.get(0).url)
//                            .circleCrop()
                            .into(ivPostImage);
                    tvPostTitle.setText(showSimple.name);
                    tvPostPublisher.setText(showSimple.publisher);
                    Log.i(TAG, showSimple.name);
                    Log.i(TAG, showSimple.publisher);
                    Log.i(TAG, showSimple.images.get(0).url);

                }
            });


//            tvPostDesc.setText(post.getDescription());
//            tvPostUsername.setText(post.getUser().getUsername());
//            tvPostTimeAgo.setText(Post.calculateTimeAgo(post.getCreatedAt()));
//
//            ParseFile image = post.getImage();
//            if (image != null) {
//                Glide.with(context)
//                        .load(post.getImage().getUrl())
//                        .into(ivPostImage);
//            }
//            ParseUser user = post.getUser();
//            if  (user != null) {
//                ParseFile image1 = user.getParseFile("image");
//                if (image1 != null) {
//                    Glide.with(context)
//                            .load(image1.getUrl())
//                            .circleCrop()
//                            .into(ivPostUserPhoto);
//                } else {
//                    Glide.with(context)
//                            .load(R.drawable.anon)
//                            .circleCrop()
//                            .into(ivPostUserPhoto);
//                }
//            }


        }
    }
}
