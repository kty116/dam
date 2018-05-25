package com.deltaworks.damlink.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.deltaworks.damlink.R;
import com.deltaworks.damlink.push.MyFirebaseInstanceIDService;
import com.deltaworks.damlink.push.MyFirebaseMessagingService;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {

    private Handler mHandler;
    private boolean isPermissionCheck;
    private ConnectivityManager mManager;
    private NetworkInfo mMobile;
    private NetworkInfo mWifi;
    private SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_splash);

        mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


        mManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        networkCheck();
    }

    @Override
    protected void onResume() {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putInt(MyFirebaseMessagingService.PUSH_COUNT, 0);
        editor.commit();

        int pushCount = mPref.getInt(MyFirebaseMessagingService.PUSH_COUNT, 0);
        Log.d("dd", "onResume: "+pushCount);

        MyFirebaseMessagingService.setBadge(this, pushCount);
        super.onResume();
    }

    private void noNetwork() {
        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(SplashActivity.this);
        alert_confirm.setMessage("인터넷 연결 확인 후 다시 시도해주세요.").setCancelable(false).setPositiveButton("재접속",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        networkCheck();
                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog alert = alert_confirm.create();
        alert.show();
    }

    public void networkCheck() {
        mMobile = mManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        mWifi = mManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mWifi.isConnected() || mMobile.isConnected()) {  //인터넷 연결 됐을때
            permissionCheck();

        } else {
            //인터넷 연결 안됐을때
            noNetwork();
        }
    }


    /**
     * 퍼미션 체크
     */
    public void permissionCheck() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //마시멜로우 이상인지 체크

            int[] permissionChecks = new int[3];

            permissionChecks[0] = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            permissionChecks[1] = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            permissionChecks[2] = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);


            if (permissionChecks[0] == PackageManager.PERMISSION_DENIED || permissionChecks[1] == PackageManager.PERMISSION_DENIED || permissionChecks[2] == PackageManager.PERMISSION_DENIED
                    ) {  //하나라도 허락안된거 있으면
                setPermissionCheck();
            } else {
                //퍼미션값 다 있으면
                splashThread();
            }
        } else {
//            마시멜로우 미만
//            퍼미션 체크 x
            splashThread();
        }
    }

    public void setPermissionCheck() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {  // 퍼미션 체크 모두 승인 받으면
                splashThread();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(SplashActivity.this, "해당 권한을 거부하면 이 서비스를 이용할 수 없습니다.", Toast.LENGTH_LONG).show();
                finish();
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
//                .setRationaleMessage("구글 로그인을 하기 위해서는 주소록 접근 권한이 필요해요")
                .setDeniedMessage("해당 권한을 거부하면 이 서비스를 이용할 수 없습니다.\n- 권한 승인 변경 방법\n[설정] > [애플리케이션] > [담너머] \n> [권한] > 모두 허용")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();
    }

    public void splashThread() {

        mHandler = new Handler();
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        startActivity(new Intent(getApplicationContext(), MainActivity3.class));
                        finish();
                    }
                });
            }
        });
        thread.start();
    }


}
