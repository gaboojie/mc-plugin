package org.gabooj.landprotection;

import java.util.List;

public class PlayerLandInfo {

    public List<Integer> friends;
    public String name;

    public PlayerLandInfo(String name, List<Integer> friends) {
        this.friends = friends;
        this.name = name;
    }

}
