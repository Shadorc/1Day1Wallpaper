package com.shadorc.onedayonewallpaper.api.wallhaven;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

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
    @JsonProperty("tags")
    private List<Tag> tags;

    public String getId() {
        return this.id;
    }

    public String getResolution() {
        return this.resolution;
    }

    public String getPath() {
        return this.path;
    }

    public String getShortUrl() {
        return this.shortUrl;
    }

    public int getFileSize() {
        return this.fileSize;
    }

    public float getRatio() {
        return this.ratio;
    }

    public List<Tag> getTags() {
        return Collections.unmodifiableList(this.tags);
    }

    @Override
    public String toString() {
        return "Wallpaper{" +
                "id='" + this.id + '\'' +
                ", resolution='" + this.resolution + '\'' +
                ", path='" + this.path + '\'' +
                ", shortUrl='" + this.shortUrl + '\'' +
                ", fileSize=" + this.fileSize +
                ", ratio=" + this.ratio +
                '}';
    }
}
