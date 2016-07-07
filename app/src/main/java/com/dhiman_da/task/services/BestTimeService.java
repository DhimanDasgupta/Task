package com.dhiman_da.task.services;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.dhiman_da.task.model.TaskItem;
import com.dhiman_da.task.utils.TaskUtils;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;

/**
 * Created by dhiman_da on 7/7/2016.
 */

public class BestTimeService extends GcmTaskService {
    private static final String TAG = "BestTimeService";

    @Override
    public int onRunTask(TaskParams taskParams) {
        String taskId = taskParams.getExtras().getString(TaskUtils.TASK_ID);
        boolean completed = TaskUtils.makeNetworkCall();

        Log.d(TAG, "One Off scheduled call executed. Task ID: " + taskId);

        // Prepare Intent to send with broadcast.
        Intent taskUpdateIntent = new Intent(TaskUtils.TASK_UPDATE_FILTER);
        taskUpdateIntent.putExtra(TaskUtils.TASK_ID, taskId);
        TaskItem taskItem = TaskUtils.getTaskItemFromFile(getApplicationContext(), taskId);
        if (taskItem == null) {
            return GcmNetworkManager.RESULT_FAILURE;
        }
        if (completed) {
            taskItem.setTaskStatus(TaskItem.EXECUTED_STATUS);
        } else {
            taskItem.setTaskStatus(TaskItem.FAILED_STATUS);
        }
        taskUpdateIntent.putExtra(TaskUtils.TASK_STATUS, taskItem.getTaskStatus());
        TaskUtils.saveTaskItemToFile(getApplicationContext(), taskItem);

        // Notify listeners (MainActivity) that task was completed successfully.
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        localBroadcastManager.sendBroadcast(taskUpdateIntent);
        return GcmNetworkManager.RESULT_SUCCESS;
    }
}
