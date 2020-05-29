package com.example.cinematic.Classes;

public class Cast {
    String posterPath;
    String name;
    String id;

    public Cast(String posterPath, String name, String id) {
        this.posterPath = posterPath;
        this.name = name;
        this.id = id;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
