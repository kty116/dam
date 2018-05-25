package com.deltaworks.damlink.push;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.deltaworks.damlink.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;
//import com.google.firebase.messaging.FirebaseMessagingService;
//import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Administrator on 2018-03-21.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static final String PUSH_COUNT = "push_count";
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

        Log.d("onCreate", "onMessageReceived: " + remoteMessage.getData().toString());

        int pushCount = mPref.getInt(PUSH_COUNT, 0);

        pushCount++;

        Log.d(TAG, "onMessageReceived: "+pushCount);

        SharedPreferences.Editor editor = mPref.edit();
        editor.putInt("push_count", pushCount);
        editor.commit();

        setBadge(this, pushCount);

        String title = remoteMessage.getData().get("title");
        String message = remoteMessage.getData().get("message");


        createNoti(title, message);

    }


    public void createNoti(String title, String message) {

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //API 26버전 이상부터는 notification에 channel값을 설정해줘야함

            channel = new NotificationChannel("default", "Channel name", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Channel description");
            mNotificationManager.createNotificationChannel(channel);
        }

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default")
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setVibrate(new long[]{1, 1000});
//                .setContentIntent()

        mNotificationManager.notify(mNotifyID, builder.build());

//        if (firstNoti) {
//            mBLEStateNoti = new NotificationCompat.Builder(this, "0")
//                    .setContentTitle("DTG")
//                    .setSmallIcon(R.mipmap.ic_launcher)
//                    .setContentText("오늘도 좋은 하루 되세요~~!")  //연결 상태에 따라 text 다르게 설정
//                    .setContentIntent(clickNotiPendingIntent());  //노티 클릭설정
//
////                    .addAction(R.drawable.ic_launcher_background, "CLOSE", closeNotiPendingIntent());
//
//            startForeground(1, mBLEStateNoti.build());
//
//        } else {
//            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
////            mBLEStateNoti.setContentText(text);
//            mNotificationManager.notify(1, mBLEStateNoti.build());
//        }


    }

    public static void setBadge(final Context context, final int count) {
        Log.d(TAG, "setBadge: ");
        final String launcherClassName = getLauncherClassName(context);

        if (launcherClassName == null) {
            return;
        }

        final Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra("badge_count", count > 0 ? count : null);
        intent.putExtra("badge_count_package_name", context.getPackageName());
        intent.putExtra("badge_count_class_name", launcherClassName);

        context.sendBroadcast(intent);
    }

    private static String getLauncherClassName(Context context) {
        final PackageManager pm = context.getPackageManager();
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);

        for (final ResolveInfo resolveInfo : resolveInfos) {
            final String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
            if (pkgName.equalsIgnoreCase(context.getPackageName())) {
                String className = resolveInfo.activityInfo.name;
                return className;
            }
        }

        return null;
    }
}
