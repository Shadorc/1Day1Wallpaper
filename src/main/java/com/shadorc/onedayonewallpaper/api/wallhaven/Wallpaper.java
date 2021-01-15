package com.shadorc.onedayonewallpaper.api.wallhaven;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

    // Tags are present only using the wallpaper endpoint
    public Optional<List<Tag>> getTags() {
        return Optional.ofNullable(this.tags).map(Collections::unmodifiableList);
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
                ", tags=" + this.tags +
                '}';
    }
}
