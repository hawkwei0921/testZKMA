package com.hawk.testzkma;

import android.app.Activity;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.htc.htcwalletsdk.Export.HtcWalletSdkManager;
import com.htc.htcwalletsdk.Export.RESULT;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    private static Activity sActivity;
    HtcWalletSdkManager mZKMA;
    int intValue = RESULT.UNKNOWN;
    String mZKMA_version;
    String mStrApiVersion;
    long uid;
    String wallet_name = "testZKMA";
    String sha256 = "24681012579";
    Handler mHandler;
    HandlerThread mHandlerThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sActivity = this;
        setContentView(R.layout.activity_main);
        mZKMA_version = com.htc.htcwalletsdk.BuildConfig.VERSION_NAME;
        ((TextView)findViewById(R.id.text1)).setText(mZKMA_version);
    }

    public void demoAPIs(View v){ // 1. call ZKMA APIs in background thread
        if( mZKMA == null ) {
            mZKMA = HtcWalletSdkManager.getInstance();
        }
        if( mHandlerThread == null ) {
            mHandlerThread = new HandlerThread("ZKMA_BackgroundThread");
        }
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
        mHandler.post(apiRunnable);
    }

    final Runnable apiRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                // 1-1. init
                intValue = mZKMA.init(getApplicationContext());
                // 1-2. getApiVersion
                mStrApiVersion = mZKMA.getApiVersion();
                // 1-3. register
                uid = mZKMA.register(wallet_name, sha256);
                // 1-4. call ZKMA APIs, ex: create Seed
                intValue = mZKMA.createSeed(uid);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
