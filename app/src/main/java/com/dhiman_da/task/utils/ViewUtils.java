package com.dhiman_da.task.utils;

import android.content.Context;

/**
 * Created by dhiman_da on 7/7/2016.
 */

public class ViewUtils {
    public static float dpFromPx(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }
}
