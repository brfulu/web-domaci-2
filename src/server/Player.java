package server;

import common.StickType;

import java.util.Objects;

public class Player {
    private String id;
    private int points;
    private StickType bid;

    public Player(String id) {
        this.id = id;
    }

    public void inceremntPoints() {
        points++;
    }

    public String getId() {
        return id;
    }

    public int getPoints() {
        return points;
    }

    public StickType getBid() {
        return bid;
    }

    public void setBid(StickType bid) {
        this.bid = bid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(id, player.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
