package com.dhiman_da.task;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.dhiman_da.task.adapter.TaskAdapter;
import com.dhiman_da.task.adapter.TaskItemClickListener;
import com.dhiman_da.task.adapter.TaskItemDecoration;
import com.dhiman_da.task.loaders.AddTaskLoader;
import com.dhiman_da.task.loaders.DeleteTaskLoader;
import com.dhiman_da.task.loaders.LoadTaskLoader;
import com.dhiman_da.task.model.TaskItem;
import com.dhiman_da.task.services.BestTimeService;
import com.dhiman_da.task.services.NowService;
import com.dhiman_da.task.utils.Constants;
import com.dhiman_da.task.utils.TaskUtils;
import com.dhiman_da.task.utils.ViewUtils;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.OneoffTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    private static final String TASK_ID_PREFIX = "task-id";

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;

    private AppCompatButton mNowButton;
    private AppCompatButton mWhenBestButton;

    private TaskAdapter mTaskAdapter;
    private final LoaderManager.LoaderCallbacks<Integer> mDeleteTaskCallbacks = new LoaderManager.LoaderCallbacks<Integer>() {
        @Override
        public Loader<Integer> onCreateLoader(int id, Bundle args) {
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
            });
            return new DeleteTaskLoader(getApplicationContext(), mTaskAdapter.getTaskItems(), TaskItem.fromBundle(args));
        }

        @Override
        public void onLoadFinished(Loader<Integer> loader, Integer data) {
            if (mTaskAdapter != null) {
                mTaskAdapter.removeTaskAt(data.intValue());
                mTaskAdapter.notifyItemRemoved(data.intValue());
            }

            if (mSwipeRefreshLayout != null) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }

        @Override
        public void onLoaderReset(Loader<Integer> loader) {
            if (mSwipeRefreshLayout != null) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    };
    private final LoaderManager.LoaderCallbacks<List<TaskItem>> mLoadTaskCallbacks = new LoaderManager.LoaderCallbacks<List<TaskItem>>() {
        @Override
        public Loader<List<TaskItem>> onCreateLoader(int id, Bundle args) {
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
            });
            return new LoadTaskLoader(getApplicationContext());
        }

        @Override
        public void onLoadFinished(Loader<List<TaskItem>> loader, List<TaskItem> data) {
            if (mTaskAdapter != null) {
                mTaskAdapter.removeTaskItems();
                mTaskAdapter.setTaskItems(data);
                mTaskAdapter.notifyDataSetChanged();
            }

            if (mSwipeRefreshLayout != null) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }

        @Override
        public void onLoaderReset(Loader<List<TaskItem>> loader) {
            if (mSwipeRefreshLayout != null) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    };
    private GcmNetworkManager mGcmNetworkManager;
    private final LoaderManager.LoaderCallbacks<TaskItem> mAddTaskCallbacks = new LoaderManager.LoaderCallbacks<TaskItem>() {
        @Override
        public Loader<TaskItem> onCreateLoader(int id, Bundle args) {
            if (mSwipeRefreshLayout != null) {
                mSwipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(true);
                    }
                });
            }
            return new AddTaskLoader(getApplicationContext(), TaskItem.fromBundle(args));
        }

        @Override
        public void onLoadFinished(Loader<TaskItem> loader, TaskItem data) {
            if (mTaskAdapter != null) {
                mTaskAdapter.addTaskItem(data);
                mTaskAdapter.notifyDataSetChanged();
            }

            if (data.getTaskType().equals(TaskItem.ONE_OFF_TASK)) {
                Bundle bundle = new Bundle();
                bundle.putString(TaskUtils.TASK_ID, data.getTaskId());

                // Schedule oneoff task.
                OneoffTask oneoffTask = new OneoffTask.Builder()
                        .setService(BestTimeService.class)
                        .setTag(data.getTaskId())
                        .setRequiredNetwork(OneoffTask.NETWORK_STATE_CONNECTED)
                        // Use an execution window of 30 seconds or more. Less than 30 seconds would not allow
                        // GcmNetworkManager enough time to optimize the next best time to execute your task.
                        .setExecutionWindow(0, 30)
                        .setExtras(bundle)
                        .build();
                mGcmNetworkManager.schedule(oneoffTask);
            } else {
                // Immediately make network call.
                Intent nowIntent = new Intent(getApplicationContext(), NowService.class);
                nowIntent.putExtra(TaskUtils.TASK_ID, data.getTaskId());
                getApplication().startService(nowIntent);
            }

            if (mSwipeRefreshLayout != null) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }

        @Override
        public void onLoaderReset(Loader<TaskItem> loader) {
            if (mSwipeRefreshLayout != null) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    };
    private BroadcastReceiver mBroadcastReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGcmNetworkManager = GcmNetworkManager.getInstance(this);

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String taskId = intent.getStringExtra(TaskUtils.TASK_ID);
                String status = intent.getStringExtra(TaskUtils.TASK_STATUS);

                mTaskAdapter.updateTaskItemStatus(taskId, status);
            }
        };

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getSupportLoaderManager().initLoader(Constants.LOAD_TASK_LOADER_ID, null, mLoadTaskCallbacks);
                }
            });
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.activity_main_recycler_view);
        if (mRecyclerView != null) {
            mTaskAdapter = new TaskAdapter(new ArrayList<TaskItem>());

            mRecyclerView.addItemDecoration(new TaskItemDecoration((int) ViewUtils.pxFromDp(mRecyclerView.getContext(), 4f)));
            mRecyclerView.addOnItemTouchListener(new TaskItemClickListener(mRecyclerView.getContext(), new TaskItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    getSupportLoaderManager().restartLoader(Constants.DELETE_TASK_LOADER_ID, mTaskAdapter.getTaskItems().get(position).toBundle(), mDeleteTaskCallbacks);
                }
            }));
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext(), LinearLayoutManager.VERTICAL, false));
            mRecyclerView.setAdapter(mTaskAdapter);
        }

        mNowButton = (AppCompatButton) findViewById(R.id.activity_main_now_button);
        if (mNowButton != null) {
            mNowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String taskId = TASK_ID_PREFIX + Calendar.getInstance().getTimeInMillis();
                    final TaskItem taskItem = new TaskItem.TaskBuilder().
                            taskId(taskId).
                            taskType(TaskItem.NOW_TASK).
                            taskStatus(TaskItem.PENDING_STATUS).
                            build();

                    getSupportLoaderManager().restartLoader(Constants.ADD_TASK_LOADER_ID, taskItem.toBundle(), mAddTaskCallbacks);
                    Log.d(TAG, "Creating a Now Task. " + taskId);
                }
            });
        }

        mWhenBestButton = (AppCompatButton) findViewById(R.id.activity_main_when_best_button);
        if (mWhenBestButton != null) {
            mWhenBestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String taskId = TASK_ID_PREFIX + Calendar.getInstance().getTimeInMillis();
                    final TaskItem taskItem = new TaskItem.TaskBuilder().
                            taskId(taskId).
                            taskType(TaskItem.ONE_OFF_TASK).
                            taskStatus(TaskItem.PENDING_STATUS).
                            build();

                    getSupportLoaderManager().restartLoader(Constants.ADD_TASK_LOADER_ID, taskItem.toBundle(), mAddTaskCallbacks);
                    Log.d(TAG, "Scheduling one off task. " + taskId);
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (isInMultiWindowMode()) {
            startListing();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!isInMultiWindowMode()) {
            startListing();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (!isInMultiWindowMode()) {
            stopListing();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (isInMultiWindowMode()) {
            stopListing();
        }
    }

    private void startListing() {
        final LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(mBroadcastReceiver, new IntentFilter(TaskUtils.TASK_UPDATE_FILTER));

        getSupportLoaderManager().initLoader(Constants.LOAD_TASK_LOADER_ID, null, mLoadTaskCallbacks);
    }

    private void stopListing() {
        final LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.unregisterReceiver(mBroadcastReceiver);
    }
}
