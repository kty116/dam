package com.deltaworks.damlink.push;

import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.deltaworks.damlink.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Administrator on 2018-03-21.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {  //푸시 받을때 콜백
        super.onMessageReceived(remoteMessage);
        Log.d("onCreate", "onMessageReceived: "+remoteMessage.getData().toString());
        createNoti(remoteMessage.getData().toString());
    }


    public void createNoti(String text) {

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "0")
                .setContentTitle("DTG")
                .setContentText(text)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentText(text);  //연결 상태에 따라 text 다르게 설정

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(3, builder.build());
        }
    }
