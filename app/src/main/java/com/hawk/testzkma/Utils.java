package com.hawk.testzkma;

import android.app.Activity;
import android.content.Context;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {
    public static String StringToSha256String(String inputStr) {
        if(inputStr == null)
            return null;
        try {
            byte[] inputByte = inputStr.getBytes();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(inputByte);
            return new String(Base64.encode(hash, Base64.DEFAULT));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public final static boolean isDeviceSupportHardwareWallet(Context context) {
        final String feature = "com.htc.hardware.wallet";
        final PackageManager packageManager = context.getPackageManager();
        final FeatureInfo[] featuresList = packageManager.getSystemAvailableFeatures();
        for (FeatureInfo f : featuresList) {
            if (f.name != null && f.name.equals(feature)) {
                Log.i("Utils", "isDeviceSupportHardwareWallet  true , " + f.name);
                return true;
            }
        }

        return false;
    }

    static public String GetSampleRawJsonString(Activity activity, int resId){
        InputStream is = activity.getResources().openRawResource(resId);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024*1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
        finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return writer.toString();
    }

}
