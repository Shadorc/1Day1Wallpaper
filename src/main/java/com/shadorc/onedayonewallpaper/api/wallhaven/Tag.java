package com.shadorc.onedayonewallpaper.api.wallhaven;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Tag {

    @JsonProperty("name")
    private String name;
    @JsonProperty("purity")
    private String purity;

    public String getName() {
        return this.name;
    }

    public String getPurity() {
        return this.purity;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "name='" + this.name + '\'' +
                ", purity='" + this.purity + '\'' +
                '}';
    }
}
