package com.hawk.testzkma;

import android.app.Activity;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.htc.htcwalletsdk.Export.HtcWalletSdkManager;
import com.htc.htcwalletsdk.Export.RESULT;
import com.htc.htcwalletsdk.Native.Type.ByteArrayHolder;
import com.htc.htcwalletsdk.Security.Key.PublicKeyHolder;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    private static Activity sActivity;
    HtcWalletSdkManager mZKMA;
    int intValue = RESULT.UNKNOWN;
    String mZKMA_version;
    String mStrApiVersion;
    long uid;
    String m_androidID;
    String wallet_name = "com.htc.MyWallet";
    String sha256;
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
                // 2-1. init
                intValue = mZKMA.init(getApplicationContext());
                // 2-2. getApiVersion
                mStrApiVersion = mZKMA.getApiVersion();
                // 2-3. register
                m_androidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                sha256 = Utils.StringToSha256String(m_androidID);
                uid = mZKMA.register(wallet_name, sha256);
                // 2-4. If seed is not created for wallet ever, App should create or restore Seed once.
                intValue = mZKMA.restoreSeed(uid);
                if( mZKMA.isSeedExists(uid) == RESULT.SUCCESS ) {

                    // TODO: call ZKMA APIs, ex: get account xPub Key
                    // PublicKeyHolder accountxPubKey = mZKMA.getAccountExtPublicKey(uid, 44, 145, 0);
                    // PublicKeyHolder bip32xPubKey = mZKMA.getBipExtPublicKey(uid, 44, 145, 0, 0,0);
                    String strJson = Utils.GetSampleRawJsonString(sActivity, sActivity.getResources().getIdentifier("bch_jon_mainnet1","raw", sActivity.getPackageName()));
                    ByteArrayHolder signTransactionByteArrayHolder= new ByteArrayHolder();
                    intValue = mZKMA.signTransaction(uid, 145, 0, strJson, signTransactionByteArrayHolder);
                } else {
                    Toast.makeText(sActivity, "restore or create SEED first!", Toast.LENGTH_LONG).show();
                }
                Log.d(TAG, "test end!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
