package com.example.blank;

public class Note {
    private long id;
    private String heading;
    private String details;
    private int position;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(int newPosition) {
        this.position = newPosition;
    }
}