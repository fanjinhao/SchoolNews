package com.fayne.android.schoolnews.util;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fan on 2017/11/14.
 */

public class ActivityCollector {
    private static List<Activity> sActivities = new ArrayList<>();

    public static boolean addActivity(Activity activity) {
        return sActivities.add(activity);
    }

    public static boolean removeActivity(Activity activity) {
        return sActivities.remove(activity);
    }

    public static void finishAll() {
        if (sActivities.isEmpty()) {
            return;
        }
        for (Activity activity : sActivities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }
}
