package com.shadorc.onedayonewallpaper.wallhaven;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Wallpaper {

    @JsonProperty("id")
    private String id;
    @JsonProperty("resolution")
    private String resolution;
    @JsonProperty("path")
    private String path;
    @JsonProperty("short_url")
    private String shortUrl;
    @JsonProperty("file_size")
    private int fileSize;
    @JsonProperty("ratio")
    private float ratio;

    public String getId() {
        return id;
    }

    public String getResolution() {
        return resolution;
    }

    public String getPath() {
        return path;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public int getFileSize() {
        return fileSize;
    }

    public float getRatio() {
        return ratio;
    }

    @Override
    public String toString() {
        return "Wallpaper{" +
                "id='" + id + '\'' +
                ", resolution='" + resolution + '\'' +
                ", path='" + path + '\'' +
                ", shortUrl='" + shortUrl + '\'' +
                ", fileSize=" + fileSize +
                ", ratio=" + ratio +
                '}';
    }
}
