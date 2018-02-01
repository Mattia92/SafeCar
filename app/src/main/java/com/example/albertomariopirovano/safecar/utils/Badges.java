package com.example.albertomariopirovano.safecar.utils;

import com.example.albertomariopirovano.safecar.R;

import java.util.Arrays;

/**
 * Created by mattiacrippa on 31/01/18.
 */

public class Badges {

    private String[] badgeName;
    private String[] badgeType;
    private int[] lockedIcon;
    private int[] unlockedIcon;
    private int[] unlockingCondition;
    private boolean[] isUnlocked;


    public Badges() {
        badgeName = new String[]{
                "First Trip",
                "5 Trips",
                "20 Trips",
                "100 Trips",
                "20 KM Driven",
                "100 KM Driven",
                "250 KM Driven",
                "1000 KM Driven",
                "1 Hour Driven",
                "10 Hours Driven",
                "100 Hours Driven",
                "500 Hours Driven",
                "100 DSI Trip",
                "500 DSI Trip",
                "1000 DSI Trip"
        };

        badgeType = new String[]{
                "NumTrip",
                "NumTrip",
                "NumTrip",
                "NumTrip",
                "KM",
                "KM",
                "KM",
                "KM",
                "Duration",
                "Duration",
                "Duration",
                "Duration",
                "DSI",
                "DSI",
                "DSI"
        };

        lockedIcon = new int[]{
                R.drawable.ic_airport_shuttle_black_24px,
                R.drawable.ic_airport_shuttle_black_24px,
                R.drawable.ic_airport_shuttle_black_24px,
                R.drawable.ic_airport_shuttle_black_24px,
                R.drawable.ic_whatshot_black_24px,
                R.drawable.ic_whatshot_black_24px,
                R.drawable.ic_whatshot_black_24px,
                R.drawable.ic_whatshot_black_24px,
                R.drawable.ic_timer_black_24px,
                R.drawable.ic_timer_black_24px,
                R.drawable.ic_timer_black_24px,
                R.drawable.ic_timer_black_24px,
                R.drawable.ic_adjust_black_24dp,
                R.drawable.ic_adjust_black_24dp,
                R.drawable.ic_adjust_black_24dp
        };

        unlockedIcon = new int[]{

        };

        unlockingCondition = new int[]{
                1,
                5,
                20,
                100,
                20,
                100,
                250,
                1000,
                1,
                10,
                100,
                500,
                100,
                500,
                1000
        };

        isUnlocked = new boolean[badgeName.length];
        Arrays.fill(isUnlocked, Boolean.FALSE);
    }

    public String[] getBadgeName() {
        return badgeName;
    }

    public String getBadgeNameAt(int i) {
        return badgeName[i];
    }

    public int[] getLockedIcon() {
        return lockedIcon;
    }

    public int getLockedIconAt(int i) {
        return lockedIcon[i];
    }

    public int[] getUnlockedIcon() {
        return unlockedIcon;
    }

    public int[] getUnlockingCondition() {
        return unlockingCondition;
    }

    public boolean[] getIsUnlocked() {
        return isUnlocked;
    }

}
