package com.marcopizarro.podcast;

import android.util.Log;
import android.widget.Toast;

import org.junit.Test;

import static org.junit.Assert.*;
import com.marcopizarro.podcast.KeywordExtractor;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ExampleUnitTest {
    @Test
    public void compatScoreTest(){
        System.out.println(KeywordExtractor.getFrequecy("A man walked up to the aerospace engineer and then ran away away with aerospace"));

//        ParseUser.logInInBackground("marco", "123", new LogInCallback() {
//            @Override
//            public void done(ParseUser user, ParseException e) {
//                if (e != null) {
//                    Log.i("EUT", "Error Logging in", e);
//                } else {
//                    List<Post> posts = new ArrayList<Post>();
//                    Post post = new Post();
//                    ParseObject.create("Post");
//                    post.setRating(0);
//                    posts.add(post);
//                    assertEquals(100.00, CompareFragment.getCompatibilityScore(posts, 2), 0);
//                }
//            }
//        });
    }
}