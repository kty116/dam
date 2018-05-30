package com.deltaworks.damlink.push;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.deltaworks.damlink.BR;
import com.deltaworks.damlink.R;
import com.deltaworks.damlink.activity.MainActivity3;
import com.deltaworks.damlink.activity.SplashActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class PushConnectService extends Service {

    public static boolean isPushService = true;  //푸시 서비스 살아있음
    public static final String PUSH_COUNT = "push_count";

    public static final String ACTION_CLICK_NOTIBAR = "action_click_notibar";
    public static final String ACTION_DELETE_NOTIBAR = "action_delete_notibar";

    private SharedPreferences mPref;
    public static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    public int mNotifyID = 1;
    private SharedPreferences.Editor mEditor;


    @Override
    public void onCreate() {
        super.onCreate();
        mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Log.d("dddd", "onCreate: pushConnectService");
//        EventBus.getDefault().register(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("dd", "onStartCommand: 123");
        if (intent != null) {
            if (intent.getAction() != null) {
                switch (intent.getAction()) {

                    case ACTION_CLICK_NOTIBAR: //푸시 서비스
                        Log.d("dddd", "onStartCommand: ");

                        if (!MainActivity3.sVisibleActivity) {  //false
                            //메인 액티비티가 보이지 않을때만 화면 새로 띄우기
                            Intent intent1 = new Intent(getApplicationContext(), SplashActivity.class);
                            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent1);
                        } else {

                            /**
                             * 푸시 눌러서 앱으로 갔을때 푸시카운트 0으로 바꿈
                             */
                            mEditor = mPref.edit();
                            mEditor.putInt(MyFirebaseMessagingService.PUSH_COUNT, 0);
                            mEditor.commit();

                            int pushCount1 = mPref.getInt(PUSH_COUNT, 0);
                            Log.d("dd", "onResume: " + pushCount1);

                            PushConnectService.setBadge(this, pushCount1);
                        }

                        stopSelf();
                        break;

                    case MyFirebaseMessagingService.ACTION_START_NOTI:

                        /**
                         * 푸시 왔을때 카운트 올림
                         */
                        String title = intent.getStringExtra("title");
                        String message = intent.getStringExtra("message");

                        int pushCount = mPref.getInt(PUSH_COUNT, 0);

                        pushCount++;

                        Log.d(TAG, "onMessageReceived: " + pushCount);

                        SharedPreferences.Editor editor = mPref.edit();
                        editor.putInt("push_count", pushCount);
                        editor.commit();

                        setBadge(this, pushCount);
                        createNoti(title, message);
                        break;

                    case ACTION_DELETE_NOTIBAR:
                        stopSelf();
                        break;
                }
            }
        }

        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        isPushService = false;
//        EventBus.getDefault().unregister(this);
        Log.d("dddd", "onDestroy: ");
        super.onDestroy();
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
                .setDeleteIntent(deleteNotiPendingIntent())
                .setSound(defaultSoundUri)
                .setVibrate(new long[]{1, 1000})
                .setContentIntent(clickNotiPendingIntent())
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
//                .setContentIntent()

        mNotificationManager.notify(mNotifyID, builder.build());
    }

    private PendingIntent clickNotiPendingIntent() {

        Intent clickNotiIntent = new Intent(this, this.getClass());
        clickNotiIntent.setAction(ACTION_CLICK_NOTIBAR);
//        clickNotiIntent.addCategory(Intent.CATEGORY_HOME);
        PendingIntent pending = PendingIntent.getService(this, 1, clickNotiIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pending;
    }

    private PendingIntent deleteNotiPendingIntent() {

        Intent deleteNotiIntent = new Intent(this, this.getClass());
        deleteNotiIntent.setAction(ACTION_DELETE_NOTIBAR);
//        clickNotiIntent.addCategory(Intent.CATEGORY_HOME);
        PendingIntent pending = PendingIntent.getService(this, 1, deleteNotiIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pending;
    }

    public static void setBadge(final Context context, final int count) {
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
