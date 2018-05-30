package com.deltaworks.damlink.activity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.http.HttpResponseCache;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.deltaworks.damlink.R;
import com.deltaworks.damlink.databinding.ActivityMainBinding;
import com.deltaworks.damlink.model.TokenModel;
import com.deltaworks.damlink.push.MyFirebaseInstanceIDService;
import com.deltaworks.damlink.push.MyFirebaseMessagingService;
import com.deltaworks.damlink.push.PushConnectService;
import com.deltaworks.damlink.retrofit.RetrofitLib;
import com.deltaworks.damlink.util.EditImageUtil;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.RemoteMessage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.PUT;

public class MainActivity3 extends AppCompatActivity {

    public final String TAG = MainActivity3.class.getSimpleName();
    public final int POST = 1;
    public final int DELETE = 2;
    public final int PUT = 3;
    private ActivityMainBinding binding;
    private PagerAdapter pagerAdapter;
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;
    public static boolean sVisibleActivity;  //화면 보이면 노티 눌렀을때 다시 액티비티 켜지지 않게 설정하는 변수


    private URL mUrl;

    private String mCM;
    private ValueCallback<Uri> mUM;
    private ValueCallback<Uri[]> mUMA;
    private final static int FCR = 1;

    private boolean isMainAccess = false;
    private String isAccessToken;
    private boolean isLogin = false;
    private EditImageUtil editImageUtil;
    private RetrofitLib mRetrofitLib;
    private boolean isTokenLogin = false;

    private SharedPreferences mPref;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == RESULT_OK &&
                (requestCode == PhotoPicker.REQUEST_CODE || requestCode == PhotoPreview.REQUEST_CODE)) {

            List<String> photos = null;
            if (intent != null) {
                photos = intent.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
            }

            if (photos != null) { //선택된 사진 없
                if (Build.VERSION.SDK_INT >= 21) {

                    boolean fileSizeIsLessThan10MB = true;
                    int count = 0;
                    long totalFileSize = 0;

                    ArrayList<String> photosList = (ArrayList) photos;
                    for (String uri : photosList) {
                        File file = new File(String.valueOf(uri));
                        if (file.exists()) {
                            count++;
                            long fileSizeByte = file.length();  //선택한 사진 사이즈 (Byte)
                            totalFileSize += fileSizeByte;
                            Log.d(TAG, "onActivityResult: " + totalFileSize);

                        }
                    }

                    long fileSizeMByte = totalFileSize / 1024 / 1024;  //MB로 변환
                    Log.d(TAG, "onActivityResult: " + fileSizeMByte);

                    if (fileSizeMByte > 10) {
                        fileSizeIsLessThan10MB = false;
                    }

                    if (fileSizeIsLessThan10MB) {  //사진크기가 10mb 이하일때
                        ArrayList<Uri> list = new ArrayList<>();

                        Uri[] results = null;
                        Uri[] re = new Uri[photos.size()];
                        if (mUMA == null) {
                            Log.d(TAG, "onActivityResult: 파일 없음");
                            mUMA.onReceiveValue(null);
                        }

                        if (mCM != null) {
                            for (int i = 0; i < photos.size(); i++) {
                                Log.d(TAG, "onActivityResult: dd");
                                list.add(Uri.parse(mCM + photos.get(i)));
                                re[i] = list.get(i);
                                try {
                                    editImageUtil.rotateImage(this, photos.get(i));
                                } catch (Exception e) {
                                    //sdcard에 사진이 저장된 상태고 현재 usb 저장소를 사용중이라면 sdcard에 lock이 걸려 file에 접근을 할수 없기 때문에
                                    // fileNotFoundException이 나온다
                                }
                            }
                        }

                        Log.d(TAG, "onActivityResult: 사진 10MB 이하");
                        mUMA.onReceiveValue(re);

                    } else {
                        //사진크기가 10mb 이상일때
                        mUMA.onReceiveValue(null);
                        Toast.makeText(this, "사진 크기는 10MB 이하여야합니다.", Toast.LENGTH_LONG).show();
                    }

                    mUMA = null;

                } else {
                    if (mUM == null) {
                        mUM.onReceiveValue(null);
                    }
                    Uri result = Uri.parse(photos.get(0));
                    mUM.onReceiveValue(result);
                    mUM = null;
                }
            }
        } else {  //선택된 사진 없을때
            if (mUMA != null) {
                mUMA.onReceiveValue(null);
                mUMA = null;
            } else if (mUM != null) {
                mUM.onReceiveValue(null);
                mUM = null;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        editImageUtil = new EditImageUtil();
        mRetrofitLib = new RetrofitLib();

//        sendTokenToServer(POST);
        WebSettings webSettings = binding.webView.getSettings();

        webSettings.setSaveFormData(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setDomStorageEnabled(true);
        webSettings.setGeolocationEnabled(true);

        if (Build.VERSION.SDK_INT >= 16) {
            webSettings.setAllowFileAccessFromFileURLs(true);
            webSettings.setAllowUniversalAccessFromFileURLs(true);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        binding.webView.getSettings().setJavaScriptEnabled(true);
        // JavaScript의 window.open 허용
        binding.webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        binding.webView.addJavascriptInterface(new MyJavascriptInterface(), "Android");
        binding.webView.setWebChromeClient(new WebChromeClient() {

            //For Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                mUM = uploadMsg;
                setPhotoPicker();
            }

            // For Android 3.0+, above method not supported in some android 3+ versions, in such case we use this
            public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
                mUM = uploadMsg;
                setPhotoPicker();
            }

            //For Android 4.1+
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                mUM = uploadMsg;
                setPhotoPicker();

            }

            //For Android 5.0+
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {

                if (mUMA != null) {
                    mUMA.onReceiveValue(null);
                }
                mUMA = filePathCallback;

                mCM = "file:";
                setPhotoPicker();
                return true;
            }
        });


        binding.webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                binding.progressBar.setVisibility(View.VISIBLE);
                Log.d(TAG, "onPageStarted: " + url);

                try {
                    mUrl = new URL(url);

                } catch (MalformedURLException e) {
                }
                if (url.contains("login")) {
                    if (isAccessToken != null) {
                        isLogin = true;
                        onBackPressed();
                    }
                }

//
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                binding.progressBar.setVisibility(View.INVISIBLE);

//                if (MyFirebaseInstanceIDService.refreshedToken != null) {  //토큰값이 새로 생겼을때
//
//                    if (isTokenLogin == false) {  //토큰값 안넘겨졌을때 false일때(계속 서버에 넘기는 작업), false고 토큰값이 서버로 잘 넘겨졌을때 true로 변함
//
//                        CookieManager cookieManager = CookieManager.getInstance();
//                        String cookies = cookieManager.getCookie(url);
//
//
//                        Log.d(TAG, "shouldOverrideUrlLoading: " + cookies);
//
//                        if (cookies != null) {
//
//                            if (cookies.contains("access_token")) {
//                                int accessTokenIndex = cookies.indexOf("access_token=") + 13;
//                                String accessToken = cookies.substring(accessTokenIndex);
//                                if (accessToken.contains(";")) {
//                                    Log.d(TAG, "onPageFinished: " + accessToken);
//                                    accessToken = accessToken.substring(0, accessToken.indexOf(";"));
//                                }
//                                Log.d(TAG, "onPageFinished: " + accessToken);
//
//                                Log.d(TAG, "shouldOverrideUrlLoading: " + accessToken);
//
//                                sendTokenToServer(MyFirebaseInstanceIDService.refreshedToken, accessToken);
//                            }
//                        } else {
//                            isAccessToken = null;
//                        }
//                    }
//                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {


                Log.d(TAG, "shouldOverrideUrlLoading: " + url);


                view.loadUrl(url);
//
                return true;
            }
        });

        webSettings.setJavaScriptEnabled(true);
        binding.webView.loadUrl(getString(R.string.token_url));

    }

    public void sendTokenToServer(final int howTo, final String userId) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String token = mPref.getString("token", null);

        Log.d(TAG, "sendTokenToServer: " + token);
        Call<TokenModel> tokenRequestCall = null;

        switch (howTo) {
            case POST:
                tokenRequestCall = mRetrofitLib.getRetrofit(this).sendTokenPost(token, userId);
                break;

            case DELETE:
                tokenRequestCall = mRetrofitLib.getRetrofit(this).sendTokenDelete(token, userId);
                break;

            case PUT:
                tokenRequestCall = mRetrofitLib.getRetrofit(this).sendTokenPut(token, userId);
                break;
        }

        tokenRequestCall.enqueue(new Callback<TokenModel>() {
            @Override
            public void onResponse(Call<TokenModel> call, Response<TokenModel> response) {
                if (response.isSuccessful()) {

                    Log.d(TAG, "json 값: " + response.body().getKey());

                    if (response.body().getKey() == null) {  //access 토큰값이 제대로 된 값이 아닐때
                        Log.d(TAG, "onResponse: ");
                        switch (howTo) {
                            case POST:
                                sendTokenToServer(POST, userId);
                                break;
                            case DELETE:
                                sendTokenToServer(DELETE, userId);
                                break;
                            case PUT:
                                sendTokenToServer(PUT, userId);
                                break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<TokenModel> call, Throwable t) {
                Log.d(TAG, "실패" + t.toString());

            }
        });
    }


    public String convertToString(InputStream inputStream) {
        StringBuffer string = new StringBuffer();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                string.append(line + "\n");
            }
        } catch (IOException e) {
        }
        return string.toString();
    }


    public void setPhotoPicker() {

        String url = String.valueOf(mUrl);

        if (url.contains(getString(R.string.content_list))) {
            if (Build.VERSION.SDK_INT >= 21) {
                PhotoPicker.builder()
                        .setPhotoCount(5)
                        .setGridColumnCount(3)
                        .start(MainActivity3.this);
            } else {
                PhotoPicker.builder()
                        .setPhotoCount(1)
                        .setGridColumnCount(3)
                        .start(MainActivity3.this);
            }

        } else if (url.contains(getString(R.string.token_url))) {
            PhotoPicker.builder()
                    .setShowCamera(true)
                    .setPhotoCount(1)
                    .setGridColumnCount(3)
                    .start(MainActivity3.this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        sVisibleActivity = true;
        Log.d(TAG, "onResume: ");

//        String title = "title";
//        String message = "message";
//
//        Intent gattServiceIntent = new Intent(this, PushConnectService.class);
//        gattServiceIntent.setAction(MyFirebaseMessagingService.ACTION_START_NOTI);
//        gattServiceIntent.putExtra("title", title);
//        gattServiceIntent.putExtra("message", message);
//        startService(gattServiceIntent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sVisibleActivity = false;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {

        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            super.onBackPressed();
            Log.d(TAG, "onBackPressed: ");
        } else {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "'뒤로' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onBackPressed: ");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && binding.webView.canGoBack()) {  //메인인데 뒤로 갈 수 있으면
            binding.webView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        sVisibleActivity = false;
    }

    public class MyJavascriptInterface {

        @JavascriptInterface
        public void kakaoNavi(final String url) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        }

        @JavascriptInterface
        public void postDeviceToken(String userId) {
            Log.d(TAG, "postDeviceToken: ");
            sendTokenToServer(POST, userId);
        }

        @JavascriptInterface
        public void deleteDeviceToken(String userId) {
            Log.d(TAG, "deleteDeviceToken: ");
            sendTokenToServer(DELETE, userId);
        }

        @JavascriptInterface
        public void putDeviceToken(String userId) {
            Log.d(TAG, "putDeviceToken: ");
            sendTokenToServer(PUT, userId);
        }
    }

    public static void cookieMaker(String url) {
        //롤리팝 이하 버전 cookiesyncmanager로 사용

        String COOKIES_HEADER = "Set-Cookie";
        try {

            URL url1 = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) url1.openConnection();

            con.connect();

            Map<String, List<String>> headerFields = con.getHeaderFields();
            List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);

            if (cookiesHeader != null) {
                for (String cookie : cookiesHeader) {
                    String cookieName = HttpCookie.parse(cookie).get(0).getName();
                    String cookieValue = HttpCookie.parse(cookie).get(0).getValue();

                    String cookieString = cookieName + "=" + cookieValue;
                    Log.d("d", "cookieMaker: " + cookieString);

//                    CookieManager.getInstance().setCookie("https://example.co.kr", cookieString);

                }
            }

        } catch (IOException e) {
            e.printStackTrace();

        }
    }


}
