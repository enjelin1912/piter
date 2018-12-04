package com.gadogado.piter.Helper.Model;

import java.util.List;

public class Tweet {
    private int tweetID;
    private String message;
    private String date;
    private String image;
    private String hashtag;

    public Tweet(int tweetID, String message, String date, String image, String hashtag) {
        this.tweetID = tweetID;
        this.message = message;
        this.date = date;
        this.image = image;
        this.hashtag = hashtag;
    }

    public int getTweetID() {
        return tweetID;
    }

    public void setTweetID(int tweetID) {
        this.tweetID = tweetID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getHashtag() {
        return hashtag;
    }

    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }
}
