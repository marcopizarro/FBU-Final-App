# Podcastr App

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)

## Overview
### Description
This app allows users to track their podcast listening and log it to share with the world.

### App Evaluation
- **Category:** Social Media
- **Mobile:** This application uses location services to see what people are listening to nearby. Additionally, most people listen to podcasts on the go, so having to go back to a desktop to log them would be counterintuitive.
- **Story:** There is clear value added for users who want to be able to look back and see what podcasts they have logged.
- **Market:** With over 48 million podcast episodes, there are podcasts pertaining to everyone's interests. For those who listen to a lot of podcasts, it would be helpful to have a place to log them.
- **Habit:** The averaage user will create reviews and logs on the app. The average user would return to the app whenever they listen to a podcast or when they want to check up on what others are listening to.
- **Scope:** This app isn't too broad that it will be impossible to build. Even a stripped down version would be interesting to build and add value to the end user. But, there are many technically complex aspects that could be added to make it more complex and interesting.

## Product Spec
### 1. User Stories (Required and Optional)

**Required Must-have Stories**
* User is able to sign up for an account
* User is able to input a podcast that they have listened to 
* User can save podcasts into lists to listen to later
* Users can search for podcasts
* Users can view other user's profiles including listened to podcasts and their lists
* Users can compare their listening tastes to other users (most difficult)

**Optional Nice-to-have Stories**
* User can view what podcasts are being listened to nearby them on a map
* User can listen to podcasts directly in the application 

### 2. Screen Archetypes

* Login 
* Register - User signs up or logs into their account
   * Upon Download/Reopening of the application, the user is prompted to log in to view their home timeline
* Timeline Screen
   * User sees recent activity of other users (reviews, bookmarks, listend to)
* Search/Discover
   * User can search for podcasts or discover podcasts that are popular near them on a map.
* Compare With Friends
   * User can enter another user's handle to compare their listening history
* Profile
   * A profile screen showing the user's top 4 podcasts, recently listened to podcasts, their lists, and average rating.
* Settings Screen
   * Lets people change language and app notification settings.

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Timeline
* Search/Discover
* Compare
* Your Profile

Optional:
* Settings

**Flow Navigation** (Screen to Screen)
* Forced Log-in -> Account creation if no log in is available
* Timeline screen by default
* Tab view for rest of screens

## Wireframes
[Wireframes](https://drive.google.com/drive/folders/10OQvORWZlBiuIBbLY2CMyCae-G5caLoe).


## Schema 
### Models

#### Podcast

   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | unique id for the user post (default field) |
   | publisher        | String| the publisher or author of the podcast |
   | image         | File     | cover photo of podcast (must be square) |
   | description       | String   | short description of podcast |
   | descriptionLong       | String   | long description of podcast |
   | rating    | Number   | the average rating of the podcast |
   | createdAt     | DateTime | date when post is created (default field) |
   | updatedAt     | DateTime | date when post is last updated (default field) |

#### Post

   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | unique id for the user post (default field) |
   | author        | Pointer to User| author of review |
   | podcast         | String | podcast id that is being written about|
   | location         | LatLng | location where podcast was streamed|
   | caption       | String   | caption of review |
   | Rating       | Number   | number of stars given to review |
   | likesCount    | Number   | number of likes of rating |
   | createdAt     | DateTime | date when post is created (default field) |
   | updatedAt     | DateTime | date when post is last updated (default field) |
   
#### User

   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | unique id for the user post (default field) |
   | image         | File     | profile image of user |
   | handle    | String   | the handle of the user |
   | lists | ArrayList<ArrayList<Podcast>>   | array of arrays of bookmarked podcasts |
   | createdAt     | DateTime | date when post is created (default field) |
   | updatedAt     | DateTime | date when post is last updated (default field) |
  
### Network Requests 
  
  * Timeline
    * (GET) Query recent reviews
    
    ```java
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.addDescendingOrder("createdAt");
        query.setLimit(20);
        query.include(Post.KEY_USER);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Unable to fetch posts", e);
                    return;
                } else {

                    postsAdapter.clear();
                    allPosts.addAll(posts);
                    postsAdapter.notifyDataSetChanged();
                }
            }
        });
      ```
    
  * Search / Explore
    * (Get) Query for specific search term(s)
      ```java
        SpotifyApi api = new SpotifyApi();
                api.setAccessToken(MainActivity.getAuthToken());
                SpotifyService spotify = api.getService();

                spotify.searchShows(etQuery.getText().toString(), new SpotifyCallback<ShowsPager>() {
                    @Override
                    public void failure(SpotifyError error) {
                        Log.e(TAG, "err with search", error);
                    }

                    @Override
                    public void success(ShowsPager showsPager, Response response) {
                        resultsAdapter.clear();
                        allShows.addAll(showsPager.shows.items);
                        resultsAdapter.notifyDataSetChanged();
                    }
                });
      ```
    * (GET) Query for podcasts listened to nearby
  * Compare
    * (GET) Query for podcasts listened to by both users
    
    
