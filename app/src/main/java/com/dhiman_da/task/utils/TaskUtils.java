package com.dhiman_da.task.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.dhiman_da.task.model.TaskItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by dhiman_da on 7/7/2016.
 */

public class TaskUtils {
    private static final String TAG = "TaskUtils";

    public static final String TASK_ID = "task_id";
    public static final String TASK_STATUS = "task_status";
    public static final String TASK_UPDATE_FILTER = "task_update_filter";

    private static final String FILE_NAME = "taskfile.dat";
    private static final String ONLINE_LOCATION = "https://google.com";

    /**
     * Make the Network call
     * */
    public static boolean makeNetworkCall() {
        try {
            final URL url = new URL(ONLINE_LOCATION);
            final HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.getInputStream();
            Log.d(TAG, "Network call succesful");

            return true;
        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage());

            return false;
        } catch (IOException ioe) {
            Log.e(TAG, ioe.getMessage());

            return  false;
        }
    }

    public static List<TaskItem> taskItemsFromString(String taskString) {
        Gson gson = new Gson();
        Type taskItemType = new TypeToken<ArrayList<TaskItem>>(){}.getType();
        List<TaskItem> taskItems = gson.fromJson(taskString, taskItemType);
        return taskItems;
    }

    private static String taskItemsToString(List<TaskItem> taskItems) {
        return new Gson().toJson(taskItems);
    }

    public static List<TaskItem> getTaskItemsFromFile(@NonNull Context context) {
        final List<TaskItem> taskItems = new ArrayList<>();
        final File taskFile = new File(context.getFilesDir(), FILE_NAME);
        if (!taskFile.exists()) {
            return taskItems;
        }

        try {
            String taskStr = IOUtils.toString(new FileInputStream(taskFile));
            taskItems.addAll(taskItemsFromString(taskStr));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return taskItems;
    }

    public static TaskItem getTaskItemFromFile(Context context, String id) {
        final List<TaskItem> taskItems = getTaskItemsFromFile(context);
        for (int i = 0; i < taskItems.size(); i++) {
            TaskItem taskItem = taskItems.get(i);
            if (taskItem.getTaskId().equals(id)) {
                return taskItem;
            }
        }
        return null;
    }

    /**
     * Overwrite Tasks in file with those given here.
     */
    private static void saveTaskItemsToFile(Context context, List<TaskItem> taskItems) {
        final String taskStr = taskItemsToString(taskItems);
        final File file = new File(context.getFilesDir(), FILE_NAME);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            IOUtils.write(taskStr, fileOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveTaskItemToFile(Context context, TaskItem taskItem) {
        List<TaskItem> taskItems = getTaskItemsFromFile(context);
        for (int i = 0; i < taskItems.size(); i++) {
            TaskItem ti = taskItems.get(i);
            if (ti.getTaskId().equals(taskItem.getTaskId())) {
                taskItems.set(i, taskItem);
                break;
            }
        }
        saveTaskItemsToFile(context, taskItems);
    }

    /**
     * Add a Task to the front of the task list.
     */
    public static void addTaskItemToFile(Context context, TaskItem taskItem) {
        List<TaskItem> taskItems = getTaskItemsFromFile(context);
        taskItems.add(0, taskItem);
        saveTaskItemsToFile(context, taskItems);
    }

    public static void deleteTaskItemFromFile(Context context, TaskItem taskItem) {
        List<TaskItem> taskItems = getTaskItemsFromFile(context);
        for (int i = 0; i < taskItems.size(); i++) {
            TaskItem ti = taskItems.get(i);
            if (ti.getTaskId().equals(taskItem.getTaskId())) {
                taskItems.remove(i);
                break;
            }
        }
        saveTaskItemsToFile(context, taskItems);
    }
}
