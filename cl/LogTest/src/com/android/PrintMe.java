package com.android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class PrintMe extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("test", "asdf");
        setContentView(R.layout.main);
    }
}