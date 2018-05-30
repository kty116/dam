package com.deltaworks.damlink.push;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.deltaworks.damlink.R;
import com.deltaworks.damlink.activity.MainActivity3;
import com.deltaworks.damlink.activity.SplashActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
//import com.google.firebase.messaging.FirebaseMessagingService;
//import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Administrator on 2018-03-21.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static final String PUSH_COUNT = "push_count";

    public static final String ACTION_START_NOTI = "action_start_noti";
    private SharedPreferences mPref;
    public static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    public int mNotifyID = 1;

    @Override
    public void onCreate() {

        mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        super.onCreate();
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {  //푸시 받을때 콜백
        super.onMessageReceived(remoteMessage);


        String title = remoteMessage.getData().get("title");
        String message = remoteMessage.getData().get("message");

        Intent gattServiceIntent = new Intent(this, PushConnectService.class);
        gattServiceIntent.setAction(ACTION_START_NOTI);
        gattServiceIntent.putExtra("title",title);
        gattServiceIntent.putExtra("message",message);
        startService(gattServiceIntent);


//        Log.d("onCreate", "onMessageReceived: " + remoteMessage.getData().toString());
//
//        int pushCount = mPref.getInt(PUSH_COUNT, 0);
//
//        pushCount++;
//
//        Log.d(TAG, "onMessageReceived: " + pushCount);
//
//        SharedPreferences.Editor editor = mPref.edit();
//        editor.putInt("push_count", pushCount);
//        editor.commit();
//
//        setBadge(this, pushCount);

//        String title = remoteMessage.getData().get("title");
//        String message = remoteMessage.getData().get("message");


//        createNoti(title, message);

//        Intent popupIntent = new Intent(getApplicationContext(), PopupActivity.class);
//        popupIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        startActivities(new Intent[]{popupIntent});

    }



}
