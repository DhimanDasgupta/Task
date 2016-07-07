package com.dhiman_da.task.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by dhiman_da on 7/7/2016.
 */

public class TaskItem implements Parcelable {
    public static final String ONE_OFF_TASK = "one_off_task";
    public static final String NOW_TASK = "now_task";
    public static final String PENDING_STATUS = "pending";
    public static final String EXECUTED_STATUS = "executed";
    public static final String FAILED_STATUS = "failed_status";

    private static final String TASK_ID = "task_id";
    private static final String TASK_TYPE = "task_type";
    private static final String TASK_STATUS = "task_status";

    private String mTaskId;
    private String mTaskType;
    private String mTaskStatus;

    private TaskItem(String taskId, String taskType, String taskStatus) {
        mTaskId = taskId;
        mTaskType = taskType;
        mTaskStatus = taskStatus;
    }

    private TaskItem(Parcel in) {
        mTaskId = in.readString();
        mTaskType = in.readString();
        mTaskStatus = in.readString();
    }

    public static final Creator<TaskItem> CREATOR = new Creator<TaskItem>() {
        @Override
        public TaskItem createFromParcel(Parcel in) {
            return new TaskItem(in);
        }

        @Override
        public TaskItem[] newArray(int size) {
            return new TaskItem[size];
        }
    };

    public String getTaskId() {
        return mTaskId;
    }

    public String getTaskType() {
        return mTaskType;
    }

    public String getTaskStatus() {
        return mTaskStatus;
    }

    public void setTaskId(String taskId) {
        mTaskId = taskId;
    }

    public void setTaskType(String taskType) {
        mTaskType = taskType;
    }

    public void setTaskStatus(String taskStatus) {
        mTaskStatus = taskStatus;
    }

    @Override
    public String toString() {
        return "TaskItem{" +
                "mTaskId='" + mTaskId + '\'' +
                ", mTaskType='" + mTaskType + '\'' +
                ", mTaskStatus='" + mTaskStatus + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mTaskId);
        parcel.writeString(mTaskType);
        parcel.writeString(mTaskStatus);
    }

    public static final class TaskBuilder {
        private String mTaskId;
        private String mTaskType;
        private String mTaskStatus;

        public TaskBuilder() {

        }

        public TaskBuilder taskId(final String taskId) {
            mTaskId = taskId;
            return this;
        }

        public TaskBuilder taskType(final String taskType) {
            mTaskType = taskType;
            return this;
        }

        public TaskBuilder taskStatus(final String taskStatus) {
            mTaskStatus = taskStatus;
            return this;
        }

        public TaskItem build() {
            return new TaskItem(mTaskId, mTaskType, mTaskStatus);
        }
    }

    public Bundle toBundle() {
        Bundle args = new Bundle();

        args.putString(TASK_ID, mTaskId);
        args.putString(TASK_TYPE, mTaskType);
        args.putString(TASK_STATUS, mTaskStatus);

        return args;
    }

    public static TaskItem fromBundle(@NonNull Bundle args) {
        if (args != null) {
            TaskItem item = new TaskItem(args.getString(TASK_ID),
                    args.getString(TASK_TYPE),
                    args.getString(TASK_STATUS));

            return item;
        }

        return null;
    }
}
