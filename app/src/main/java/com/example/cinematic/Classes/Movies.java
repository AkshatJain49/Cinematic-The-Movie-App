package com.example.cinematic.Classes;

public class Movies {

    String posterURL;
    String movieName;
    String id;

    public Movies(String posterURL, String movieName, String id) {
        this.posterURL = posterURL;
        this.movieName = movieName;
        this.id = id;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getPosterURL() {
        return posterURL;
    }

    public void setPosterURL(String posterURL) {
        this.posterURL = posterURL;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
