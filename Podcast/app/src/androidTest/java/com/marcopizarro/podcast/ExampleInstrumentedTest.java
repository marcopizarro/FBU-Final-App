package com.marcopizarro.podcast;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.marcopizarro.podcast", appContext.getPackageName());

    }
    @Test
    public void compatScoreTest(){
        ParseObject.registerSubclass(com.marcopizarro.podcast.Post.class);
        ParseObject.registerSubclass(com.marcopizarro.podcast.List.class);
        Parse.initialize(new Parse.Configuration.Builder(InstrumentationRegistry.getInstrumentation().getTargetContext())
                .applicationId("pxb6ueKFugKQ7KkX5ZLyGFXZLj5qCtBZwnZ7hEpf")
                .clientKey("LrsxRnXL4hCZ1Hf9ADSsvkx3nfvzs42qdvdogtVO")
                .server("https://parseapi.back4app.com/")
                .build());

        ParseUser.logInInBackground("marco", "123", new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.i("EUT", "Error Logging in", e);
                } else {
                    List<Post> posts = new ArrayList<Post>();
                    Post post = new Post();
                    post.setRating(0);
                    posts.add(post);
                    assertEquals(100.00, CompareFragment.getCompatibilityScore(posts, 2), 0);

                    posts = new ArrayList<Post>();
                    post = new Post();
                    post.setRating(1);
                    posts.add(post);
                    assertEquals(82.0, CompareFragment.getCompatibilityScore(posts, 2), 0);
                }
            }
        });
    }
}