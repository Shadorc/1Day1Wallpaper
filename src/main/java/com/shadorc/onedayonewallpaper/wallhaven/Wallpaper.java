package com.shadorc.onedayonewallpaper.wallhaven;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Wallpaper {

    @JsonProperty("id")
    private String id;
    @JsonProperty("resolution")
    private String resolution;
    @JsonProperty("dimension_x")
    private int dimensionX;
    @JsonProperty("dimension_y")
    private int dimensionY;
    @JsonProperty("path")
    private String path;
    @JsonProperty("short_url")
    private String shortUrl;

    public String getId() {
        return this.id;
    }

    public String getResolution() {
        return this.resolution;
    }

    public int getDimensionX() {
        return this.dimensionX;
    }

    public int getDimensionY() {
        return this.dimensionY;
    }

    public String getPath() {
        return this.path;
    }

    public String getShortUrl() {
        return this.shortUrl;
    }

    @Override
    public String toString() {
        return "Wallpaper{" +
                "id='" + id + '\'' +
                ", resolution='" + resolution + '\'' +
                ", dimensionX=" + dimensionX +
                ", dimensionY=" + dimensionY +
                ", path='" + path + '\'' +
                ", shortUrl='" + shortUrl + '\'' +
                '}';
    }
}
