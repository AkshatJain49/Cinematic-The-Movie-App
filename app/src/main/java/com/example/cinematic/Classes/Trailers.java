package com.example.cinematic.Classes;

public class Trailers {
    String trailerTitle;
    String trailerLink;

    public Trailers(String trailerTitle, String trailerLink) {
        this.trailerTitle = trailerTitle;
        this.trailerLink = trailerLink;
    }

    public String getTrailerTitle() {
        return trailerTitle;
    }

    public void setTrailerTitle(String trailerTitle) {
        this.trailerTitle = trailerTitle;
    }

    public String getTrailerLink() {
        return trailerLink;
    }

    public void setTrailerLink(String trailerLink) {
        this.trailerLink = trailerLink;
    }
}
