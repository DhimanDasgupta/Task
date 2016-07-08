package com.dhiman_da.task;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by dhiman_da on 7/8/2016.
 */

public class BaseActivity extends AppCompatActivity {
    @Override
    public boolean isInMultiWindowMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return super.isInMultiWindowMode();
        } else {
            return false;
        }
    }
}
