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

public class KeywordExtractTest {
    @Test
    public void keywordExtractTest(){
        List<String> descriptions = new ArrayList<String>();
        descriptions.add("One minute of the world's most shareable news - updated every half an hour, 24/7");
        descriptions.add("News comes at you fast. Join us at the end of your day to understand it. Today, Explained is your all killer, no filler, Monday to Friday news explainer hosted by Sean Rameswaram and featuring the finest reporters from the Vox Media Podcast Network and beyond");
        descriptions.add("Big tech is changing every aspect of our world. But how? And at what cost? In this special four-part series, Recode teams up with Eater to unbox the evolving world of food delivery. Find out how the rise of investor-backed third-party delivery apps has dramatically changed consumer behavior, helped create a modern gig workforce, disrupted small businesses, and potentially changed our relationship with food forever. New episodes every Tuesday starting June 22. From Recode, Eater, and the Vox Media Podcast Network. Hosted by Ahmed Ali Akbar");

        System.out.println(KeywordExtractor.getFrequecy(descriptions));
        assertEquals(KeywordExtractor.getFrequecy(descriptions), "news");
    }
}