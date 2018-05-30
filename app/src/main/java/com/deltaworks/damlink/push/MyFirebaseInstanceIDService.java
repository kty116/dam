package com.deltaworks.damlink.push;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.deltaworks.damlink.commonLib.TinyDB;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

//import com.google.firebase.iid.FirebaseInstanceId;
//import com.google.firebase.iid.FirebaseInstanceIdService;


/**
 * Created by Administrator on 2018-03-21.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "MyFirebaseIIDService";
    public SharedPreferences mPref;
    //    public static String refreshedToken = null;  //널이면 새로 토큰이 발급되지 않았다. 즉 서버에 토큰값 있다

//    private RetrofitLib retrofitLib = new RetrofitLib();


    @Override
    public void onCreate() {
        mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        super.onCreate();
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() { //앱에서는 토큰값 알 필요없고 서버가 알아야함
        Log.d(TAG, "onTokenRefresh: 토큰 만들어짐");
        //토큰 바뀌면 콜백
        // Get updated InstanceID token.

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        SharedPreferences.Editor editor = mPref.edit();

        editor.putString("token", refreshedToken);

        editor.commit();

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //토큰이 생성되면 서버로 값 보냄
//        sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {

    }
}
