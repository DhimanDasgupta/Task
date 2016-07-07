package com.dhiman_da.task.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.dhiman_da.task.model.TaskItem;
import com.dhiman_da.task.utils.TaskUtils;

/**
 * Created by dhiman_da on 7/7/2016.
 */

public class AddTaskLoader extends AsyncTaskLoader<TaskItem> {
    private Context mContext;

    private TaskItem mTaskItem;
    private TaskItem mResult;

    public AddTaskLoader(Context context, final TaskItem taskItem) {
        super(context);

        mContext = context;
        mTaskItem = taskItem;
    }

    @Override
    public TaskItem loadInBackground() {
        TaskUtils.addTaskItemToFile(mContext, mTaskItem);
        return mTaskItem;
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
    public void onCanceled(TaskItem taskItem) {
        super.onCanceled(taskItem);
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
}
