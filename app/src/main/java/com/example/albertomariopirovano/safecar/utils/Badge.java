package com.example.albertomariopirovano.safecar.utils;

import com.example.albertomariopirovano.safecar.R;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by mattiacrippa on 31/01/18.
 */

public class Badge {

    private String badgeName;
    private String badgeType;
    private int badgeIcon;
    private int unlockingCondition;
    private boolean isUnlocked;

    public Badge(String badgeName, String badgeType, int badgeIcon, int unlockingCondition) {
        this.badgeName = badgeName;
        this.badgeType = badgeType;
        this.badgeIcon = badgeIcon;
        this.unlockingCondition = unlockingCondition;
        this.isUnlocked = Boolean.FALSE;
    }

    public static void loadBadges(ArrayList<Badge> badges){
        badges.add(new Badge("First Trip", "NumTrip", R.drawable.ic_airport_shuttle_black_24px, 1));
        badges.add(new Badge("5 Trips", "NumTrip", R.drawable.ic_airport_shuttle_black_24px, 5));
        badges.add(new Badge("20 Trip", "NumTrip", R.drawable.ic_airport_shuttle_black_24px, 20));
        badges.add(new Badge("100 Trip", "NumTrip", R.drawable.ic_airport_shuttle_black_24px, 100));
        badges.add(new Badge("20 KM Driven", "KM", R.drawable.ic_whatshot_black_24px, 20));
        badges.add(new Badge("100 KM Driven", "KM", R.drawable.ic_whatshot_black_24px, 100));
        badges.add(new Badge("250 KM Driven", "KM", R.drawable.ic_whatshot_black_24px, 250));
        badges.add(new Badge("1000 KM Driven", "KM", R.drawable.ic_whatshot_black_24px, 1000));
        badges.add(new Badge("1 Hour Driven", "Duration", R.drawable.ic_timer_black_24px, 1));
        badges.add(new Badge("10 Hours Driven", "Duration", R.drawable.ic_timer_black_24px, 10));
        badges.add(new Badge("100 Hours Driven", "Duration", R.drawable.ic_timer_black_24px, 100));
        badges.add(new Badge("500 Hours Driven", "Duration", R.drawable.ic_timer_black_24px, 500));
        badges.add(new Badge("100 DSI Trip", "DSI", R.drawable.ic_adjust_black_24dp, 100));
        badges.add(new Badge("500 DSI Trip", "DSI", R.drawable.ic_adjust_black_24dp, 500));
        badges.add(new Badge("1000 DSI Trip", "DSI", R.drawable.ic_adjust_black_24dp, 1000));
    }

    public String getBadgeName() {
        return badgeName;
    }

    public String getBadgeType() {
        return badgeType;
    }

    public int getBadgeIcon() {
        return badgeIcon;
    }

    public int getUnlockingCondition() {
        return unlockingCondition;
    }

    public boolean isUnlocked() {
        return isUnlocked;
    }
}
