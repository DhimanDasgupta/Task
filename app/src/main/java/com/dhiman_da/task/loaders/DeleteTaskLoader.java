package com.dhiman_da.task.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.dhiman_da.task.model.TaskItem;
import com.dhiman_da.task.utils.TaskUtils;

import java.util.List;

/**
 * Created by dhiman_da on 7/7/2016.
 */

public class DeleteTaskLoader extends AsyncTaskLoader<Integer> {
    private Context mContext;
    private TaskItem mTaskItem;
    private List<TaskItem> mTaskItems;

    private Integer mResult;

    public DeleteTaskLoader(final Context context, final List<TaskItem> taskItems, final TaskItem taskItem) {
        super(context);

        mContext = context;
        mTaskItems = taskItems;
        mTaskItem = taskItem;
    }

    @Override
    public Integer loadInBackground() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        TaskUtils.deleteTaskItemFromFile(mContext, mTaskItem);
        return getTaskItemPosition(mTaskItem.getTaskId());
    }

    /**
     * Handles a request to start the Loader.
     * Automatically called by LoaderManager via startLoading.
     */
    @Override
    protected void onStartLoading() {
        if (mResult != null) {
            deliverResult(mResult);
        }

        if (takeContentChanged() || mResult == null) {
            forceLoad();
        }
    }

    /**
     * Handles a request to stop the Loader.
     * Automatically called by LoaderManager via stopLoading.
     */
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    /**
     * Handles a request to cancel a load.
     */
    @Override
    public void onCanceled(Integer integer) {
        super.onCanceled(integer);
    }

    /**
     * Handles a request to completely reset the Loader.
     * Automatically called by LoaderManager via reset.
     */
    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        // At this point we can release the resources associated with 'apps'
        // if needed.
        mResult = null;
    }

    private int getTaskItemPosition(String id) {
        for (int i = 0; i < mTaskItems.size(); i++) {
            TaskItem taskItem = mTaskItems.get(i);
            if (taskItem.getTaskId().equals(id)) {
                return i;
            }
        }
        return -1;
    }
}
