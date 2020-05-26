package com.shadorc.onedayonewallpaper.api.wallhaven;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

public class WallhavenResponse {

    @JsonProperty("data")
    private List<Wallpaper> wallpapers;

    public List<Wallpaper> getWallpapers() {
        return Collections.unmodifiableList(this.wallpapers);
    }

    @Override
    public String toString() {
        return "WallhavenResponse{" +
                "wallpapers=" + this.wallpapers +
                '}';
    }
}
