package com.example.albertomariopirovano.safecar.utils;

import android.util.Log;

import com.example.albertomariopirovano.safecar.R;
import com.example.albertomariopirovano.safecar.firebase_model.Trip;
import com.example.albertomariopirovano.safecar.realm_model.LocalModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

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
        badges.add(new Badge("20 Trips", "NumTrip", R.drawable.ic_airport_shuttle_black_24px, 20));
        badges.add(new Badge("100 Trip", "NumTrip", R.drawable.ic_airport_shuttle_black_24px, 100));
        badges.add(new Badge("First KM Driven", "KM", R.drawable.ic_whatshot_black_24px, 1));
        badges.add(new Badge("50 KM Driven", "KM", R.drawable.ic_whatshot_black_24px, 50));
        badges.add(new Badge("100 KM Driven", "KM", R.drawable.ic_whatshot_black_24px, 100));
        badges.add(new Badge("First Hour Driven", "Duration", R.drawable.ic_timer_black_24px, 1));
        badges.add(new Badge("50 Hours Driven", "Duration", R.drawable.ic_timer_black_24px, 50));
        badges.add(new Badge("100 Hours Driven", "Duration", R.drawable.ic_timer_black_24px, 100));
        badges.add(new Badge("500 DSI Trip", "DSI", R.drawable.ic_adjust_black_24dp, 500));
        badges.add(new Badge("1000 DSI Trip", "DSI", R.drawable.ic_adjust_black_24dp, 1000));
        badges.add(new Badge("2000 DSI Trip", "DSI", R.drawable.ic_adjust_black_24dp, 2000));
    }

    public static void checkBadges(ArrayList<Badge> badges, LocalModel localModel) {
        Float totKM = 0.0f;
        Double totHours = 0.0;
        int maxDSI = Integer.MIN_VALUE;
        int numTrips = localModel.getTrips().size();

        for (Trip t : localModel.getTrips()) {
            totKM += t.getKm();
            totHours += t.getTimeDuration();
            if(t.getFinalDSI() > maxDSI){
                maxDSI = t.getFinalDSI();
            }
        }

        for (int i = 0; i < badges.size(); i++) {
            switch(badges.get(i).getBadgeType()) {
                case "NumTrip":
                    if(numTrips >= badges.get(i).getUnlockingCondition())
                        badges.get(i).setUnlocked(Boolean.TRUE);
                    break;
                case "KM":
                    if(totKM >= badges.get(i).getUnlockingCondition())
                        badges.get(i).setUnlocked(Boolean.TRUE);
                    break;
                case "Duration":
                    if(totHours >= badges.get(i).getUnlockingCondition())
                        badges.get(i).setUnlocked(Boolean.TRUE);
                    break;
                case "DSI":
                    if(maxDSI >= badges.get(i).getUnlockingCondition())
                        badges.get(i).setUnlocked(Boolean.TRUE);
                    break;
                default:
                    break;
            }
        }
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

    public void setUnlocked(boolean unlocked) {
        isUnlocked = unlocked;
    }
}
