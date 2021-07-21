package com.marcopizarro.podcast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;

public class ListActivty extends AppCompatActivity {

    TextView tvListTitle;
    ImageView ivListCover;
    RecyclerView rvListItems;
    ListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Intent i = getIntent();
        List list = (List) Parcels.unwrap(getIntent().getParcelableExtra("list"));

        tvListTitle = findViewById(R.id.tvListTitle);
        ivListCover = findViewById(R.id.ivListCover);

        tvListTitle.setText(list.getName());
        Glide.with(this)
                .load(list.getCover().getUrl())
                .into(ivListCover);

        rvListItems = findViewById(R.id.rvListItems);
        listAdapter = new ListAdapter(this, new ArrayList<>());
        rvListItems.setAdapter(listAdapter);
        rvListItems.setLayoutManager(new LinearLayoutManager(this));

        listAdapter.clear();
        ArrayList<String> listdata = new ArrayList<String>();
        JSONArray jArray = (JSONArray) list.getPodcasts();
        if (jArray != null) {
            for (int j = 0; j < jArray.length(); j++) {
                try {
                    listdata.add(jArray.getString(j));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        listAdapter.addAll(listdata);
    }
}