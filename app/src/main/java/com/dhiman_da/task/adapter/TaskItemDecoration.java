package com.dhiman_da.task.adapter;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by dhiman_da on 7/7/2016.
 */

public class TaskItemDecoration extends RecyclerView.ItemDecoration {
    private int mSpace;

    public TaskItemDecoration(final int spaceInPixel) {
        mSpace = spaceInPixel;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = mSpace;
        outRect.right = mSpace;
        outRect.bottom = mSpace;

        // Add top margin only for the first item to avoid double space between items
        if (parent.getChildAdapterPosition(view) == 0)
            outRect.top = mSpace;

    }
}
