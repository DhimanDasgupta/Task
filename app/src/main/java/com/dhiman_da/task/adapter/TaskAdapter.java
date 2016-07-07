package com.dhiman_da.task.adapter;

import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dhiman_da.task.R;
import com.dhiman_da.task.model.TaskItem;

import java.util.List;

/**
 * Created by dhiman_da on 7/7/2016.
 */

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    private static final String TAG = "TaskAdapter";

    private List<TaskItem> mTaskItems;

    public TaskAdapter(List<TaskItem> taskItems) {
        mTaskItems = taskItems;
    }

    @Override
    public TaskAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_task, viewGroup, false);
        final ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TaskAdapter.ViewHolder viewHolder, final int position) {
        final TaskItem taskItem = mTaskItems.get(position);
        viewHolder.getLabelTextView().setText(taskItem.getTaskType());
        viewHolder.getStatusTextView().setText(taskItem.getTaskStatus() + "(" + taskItem.getTaskId() + ")");

        if (taskItem.getTaskStatus().equalsIgnoreCase(TaskItem.EXECUTED_STATUS) ||
                taskItem.getTaskStatus().equalsIgnoreCase(TaskItem.FAILED_STATUS)) {
            viewHolder.getDeleteButton().setVisibility(View.GONE);
        } else {
            viewHolder.getDeleteButton().setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mTaskItems.size();
    }

    public void setTaskItems(List<TaskItem> taskItems) {
        mTaskItems.clear();
        mTaskItems.addAll(taskItems);
        notifyDataSetChanged();
    }

    public List<TaskItem> getTaskItems() {
        return mTaskItems;
    }

    public void addTaskItem(TaskItem taskItem) {
        mTaskItems.add(0, taskItem);
        notifyItemInserted(0);
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

    public void removeTaskAt(final int position) {
        if (position < 0 && position > mTaskItems.size() - 1) {
            // Invalid index
        }

        mTaskItems.remove(position);
        notifyItemRemoved(position);
    }

    public void updateTaskItemStatus(String id, String status) {
        for (int i = 0; i < mTaskItems.size(); i++) {
            TaskItem taskItem = mTaskItems.get(i);
            if (taskItem.getTaskId().equals(id)) {
                taskItem.setTaskStatus(status);
                notifyItemChanged(i);
                break;
            }
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final AppCompatTextView mLabelTextView;
        private final AppCompatTextView mStatusTextView;
        private final AppCompatButton mDeleteButton;

        public ViewHolder(View v) {
            super(v);
            mLabelTextView = (AppCompatTextView) v.findViewById(R.id.taskLabel);
            mStatusTextView = (AppCompatTextView) v.findViewById(R.id.taskStatus);
            mDeleteButton = (AppCompatButton) v.findViewById(R.id.deleteButton);
        }

        public AppCompatTextView getLabelTextView() {
            return mLabelTextView;
        }

        public AppCompatTextView getStatusTextView() {
            return mStatusTextView;
        }

        public AppCompatButton getDeleteButton() {
            return mDeleteButton;
        }
    }
}
