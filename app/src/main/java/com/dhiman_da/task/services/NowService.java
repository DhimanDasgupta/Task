package com.dhiman_da.task.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.dhiman_da.task.model.TaskItem;
import com.dhiman_da.task.utils.TaskUtils;

/**
 * Created by dhiman_da on 7/7/2016.
 */

public class NowService extends IntentService {
    public static final String TAG = "NowIntentService";

    public NowService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String taskId = intent.getStringExtra(TaskUtils.TASK_ID);
        boolean completed = TaskUtils.makeNetworkCall();
        Intent taskUpdateIntent = new Intent(TaskUtils.TASK_UPDATE_FILTER);
        taskUpdateIntent.putExtra(TaskUtils.TASK_ID, taskId);
        TaskItem taskItem = TaskUtils.getTaskItemFromFile(getApplicationContext(), taskId);
        if (taskItem == null) {
            return;
        }
        if (completed) {
            taskItem.setTaskStatus(TaskItem.EXECUTED_STATUS);
        } else {
            taskItem.setTaskStatus(TaskItem.FAILED_STATUS);
        }
        taskUpdateIntent.putExtra(TaskUtils.TASK_STATUS, taskItem.getTaskStatus());
        TaskUtils.saveTaskItemToFile(getApplicationContext(), taskItem);

        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        localBroadcastManager.sendBroadcast(taskUpdateIntent);
    }
}
