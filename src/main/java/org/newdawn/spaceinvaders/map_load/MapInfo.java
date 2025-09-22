package org.newdawn.spaceinvaders.map_load;

import java.nio.file.Path;

public class MapInfo {
    private String hash;
    private Path path;
    private String title;

    public MapInfo(String hash, Path path, String title) {
        this.hash = hash;
        this.path = path;
        this.title = title;
    }

    @Override
    public String toString() {
        return "MapInfo " + title + " " + path + " " + hash;
    }
}
