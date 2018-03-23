package com.deltaworks.damlink.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.deltaworks.damlink.R;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {

    private Handler mHandler;
    private boolean isPermissionCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        permissionCheck();

    }

    /**
     * 퍼미션 체크
     */
    public void permissionCheck() {

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //마시멜로우 이상인지 체크

            int[] permissionChecks = new int[1];

            permissionChecks[0] = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            permissionChecks[0] = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            permissionChecks[0] = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

            if (permissionChecks[0] == PackageManager.PERMISSION_DENIED) {  //하나라도 허락안된거 있으면
                setPermissionCheck();
            } else {
                //퍼미션값 다 있으면
                Log.d("ddddddd", "run: 퍼미션값 다있음");
                splashThread();
            }
//        } else {
////            마시멜로우 미만
////            퍼미션 체크 x
//            Log.d("ddddddd", "run: 퍼미션 체크 안함");
//            splashThread();
//        }
    }

    public void setPermissionCheck() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {  // 퍼미션 체크 모두 승인 받으면
                Log.d("ddddddd", "run: 퍼미션 모두 승인");
                splashThread();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(SplashActivity.this, "If you deny permission, this service will not be available.", Toast.LENGTH_LONG).show();
                Log.d("ddddddd", "run: 퍼미션 모두 거절");
                finish();
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
//                .setRationaleMessage("구글 로그인을 하기 위해서는 주소록 접근 권한이 필요해요")
                .setDeniedMessage("If you deny permission, this service will not be available.\nSet permissions.\n[setting] > [permission]")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();
    }

    public void splashThread() {

        mHandler = new Handler();
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                Log.d("ddddddd", "run: 스레드");
                try {
                    Thread.sleep(1000);
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
