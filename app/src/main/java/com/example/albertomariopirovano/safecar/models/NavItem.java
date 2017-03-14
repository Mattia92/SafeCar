package com.example.albertomariopirovano.safecar.models;

/**
 * Created by mattiacrippa on 14/03/17.
 */

public class NavItem {

    private String title;
    private String subtitles;
    private int resIcon;

    public NavItem(String title, String subtitles, int resIcon) {
        super();
        this.resIcon = resIcon;
        this.subtitles = subtitles;
        this.title = title;
    }

    public int getResIcon() {
        return resIcon;
    }

    public void setResIcon(int resIcon) {
        this.resIcon = resIcon;
    }

    public String getSubtitles() {
        return subtitles;
    }

    public void setSubtitles(String subtitles) {
        this.subtitles = subtitles;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
