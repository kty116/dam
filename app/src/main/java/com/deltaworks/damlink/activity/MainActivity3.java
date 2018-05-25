package com.deltaworks.damlink.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.deltaworks.damlink.R;
import com.deltaworks.damlink.databinding.ActivityMainBinding;
import com.deltaworks.damlink.model.TokenModel;
import com.deltaworks.damlink.push.MyFirebaseInstanceIDService;
import com.deltaworks.damlink.retrofit.RetrofitLib;
import com.deltaworks.damlink.util.EditImageUtil;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpCookie;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity3 extends AppCompatActivity {

    public final String TAG = MainActivity3.class.getSimpleName();
    private ActivityMainBinding binding;
    private PagerAdapter pagerAdapter;
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;
//    public static boolean sVisibleActivity;  //화면 보이면 노티 눌렀을때 다시 액티비티 켜지지 않게 설정하는 변수

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == RESULT_OK &&
                (requestCode == PhotoPicker.REQUEST_CODE || requestCode == PhotoPreview.REQUEST_CODE)) {

            List<String> photos = null;
            if (intent != null) {  //선택된 사진 있을때
                Log.d(TAG, "onActivityResult: intent가 널이 아닐때");
                photos = intent.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
            }

            if (photos != null) { //선택된 사진 없
                if (Build.VERSION.SDK_INT >= 21) {
                    Log.d(TAG, "onActivityResult: photos가 널이 아닐때");

                    boolean fileSizeIsLessThan10MB = true;
                    int count = 0;

                    ArrayList<String> photosList = (ArrayList) photos;
                    for (String uri : photosList) {
                        File file = new File(String.valueOf(uri));
                        if (file.exists()) {
                            count++;
                            Log.d(TAG, "onActivityResult: 사진 갯수" + count);
                            long fileSize = file.length();
                            Log.d(TAG, "onActivityResult: " + fileSize + "10485760");
                            if (fileSize >= 10485760) {
                                fileSizeIsLessThan10MB = false;
                            }
                        }
                    }


                    ArrayList<Uri> list = new ArrayList<>();
//                    int count = 0;
//                    for (Uri uri : list) {
//                        Log.d(TAG, "onActivityResult: ");
//                        File file = new File(String.valueOf(uri));
//                        if (file.exists()) {
//                            count++;
//                            Log.d(TAG, "onActivityResult: 사진 갯수"+count);
//                            long fileSize = file.length();
//                            Log.d(TAG, "onActivityResult: "+fileSize + "10485760");
//                            if(fileSize >= 10485760){
//                                fileSizeIsLessThan10MB = false;
//                            }
//                        }
//                    }
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
                            }catch (Exception e){
                                //sdcard에 사진이 저장된 상태고 현재 usb 저장소를 사용중이라면 sdcard에 lock이 걸려 file에 접근을 할수 없기 때문에
                                // fileNotFoundException이 나온다
                            }
                        }
                    }

                    if (fileSizeIsLessThan10MB) {
                        mUMA.onReceiveValue(re);
                    } else {
                        Toast.makeText(this, "사진 크기는 10MB 이하여야합니다.", Toast.LENGTH_SHORT).show();
                    }

                    mUMA = null;

                } else {
                    Log.d(TAG, "onActivityResult: ");
                    if (mUM == null) {
                        mUM.onReceiveValue(null);
                    }
                    Uri result = Uri.parse(photos.get(0));
                    mUM.onReceiveValue(result);
                    mUM = null;
                }
            }
        } else {
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

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        editImageUtil = new EditImageUtil();
        mRetrofitLib = new RetrofitLib();

//        refreshedToken = FirebaseInstanceId.getInstance().getToken();
//        Log.d(TAG, "shouldOverrideUrlLoading: "+refreshedToken);


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

                if (MyFirebaseInstanceIDService.refreshedToken != null) {  //토큰값이 새로 생겼을때

                    if (isTokenLogin == false) {  //토큰값 안넘겨졌을때 false일때(계속 서버에 넘기는 작업), false고 토큰값이 서버로 잘 넘겨졌을때 true로 변함

                        CookieManager cookieManager = CookieManager.getInstance();
                        String cookies = cookieManager.getCookie(url);


                        Log.d(TAG, "shouldOverrideUrlLoading: " + cookies);

                        if (cookies != null) {

                            if (cookies.contains("access_token")) {
                                int accessTokenIndex = cookies.indexOf("access_token=") + 13;
                                String accessToken = cookies.substring(accessTokenIndex);
                                if(accessToken.contains(";")){
                                    Log.d(TAG, "onPageFinished: "+accessToken);
                                    accessToken = accessToken.substring(0,accessToken.indexOf(";"));
                                }
                                Log.d(TAG, "onPageFinished: "+accessToken);
//                        mAccessToken = accessTokenString.substring(0, accessTokenString.indexOf(";"));

                                Log.d(TAG, "shouldOverrideUrlLoading: " + accessToken);

//                            isAccessToken = "1";  //값 있음

                                sendTokenToServer(MyFirebaseInstanceIDService.refreshedToken, accessToken);
                            }
                            //쿠키에 값이 있으면 로그인 된 상태
//
//                            Log.d(TAG, "shouldOverrideUrlLoading: 리플레쉬된 토큰값 있음");
//                            sendTokenToServer();


//                            try {
//                                String tokenUrl = "token=" + URLEncoder.encode(MyFirebaseInstanceIDService.refreshedToken,"UTF-8");
//                                binding.webView.postUrl(getString(R.string.token_url),tokenUrl.getBytes());
//
//                            } catch (UnsupportedEncodingException e) {
//                                e.printStackTrace();
//                            }


//                        }

                        } else {
                            isAccessToken = null;
                        }

//                    Log.d(TAG, "shouldOverrideUrlLoading: " + mRememberMe);
                    }

//                if(mRememberMe!=null) {
//                    String tokenUrl = "token=" + URLEncoder.encode()
//                }

//                if (url.equals(getString(R.string.test_url))) {
//                    Log.d(TAG, "onPageFinished: 메인 엑세스로 접근");
//                    isMainAccess = true;
                }
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
        binding.webView.loadUrl(getString(R.string.test_url));

    }

    public void sendTokenToServer(String deviceToken, String accessToken) {


        Call<TokenModel> tokenRequestCall = mRetrofitLib.getRetrofit(this).sendToken(deviceToken, accessToken);

        tokenRequestCall.enqueue(new Callback<TokenModel>() {
            @Override
            public void onResponse(Call<TokenModel> call, Response<TokenModel> response) {
                if (response.isSuccessful()) {

                    Log.d(TAG, "json 값: " + response.body().toString());

                    if (response.body().getValid() == "true") {  //access 토큰값이 제대로 된 값이 아닐때
                        isTokenLogin = true;
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

        } else if (url.contains(getString(R.string.test_url))) {
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
//        sVisibleActivity = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
//        sVisibleActivity = false;

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

//            String url = String.valueOf(mUrl);
//            Log.d(TAG, "onKeyDown: " + url);

//            if (url.contains("login")) {
//                Log.d(TAG, "onKeyDown: login");
//                if (mRememberMe == null) {
//                    Log.d(TAG, "onKeyDown: mRememberMe == null");
//                    binding.webView.goBack();
//                } else {
//                    Log.d(TAG, "onKeyDown: mRememberMe != null");
//                }
//            } else {
//            if (!isLogin) {
//                Log.d(TAG, "onKeyDown: login x");
            binding.webView.goBack();
//            } else {
//                onBackPressed();
//            }
//            }


//            Log.d(TAG, "onKeyDown: "+mUri);
//            if (mUri.contains("login")) {  //로그인 포함되면
//                if (mRememberMe != null) {  //세션값이 널이 아닐때
//                    Log.d(TAG, "onKeyDown: " + mUri);
//                    onBackPressed();
//                } else {
//                    binding.webView.goBack();
//                }
//            } else {
//                binding.webView.goBack();
//            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        sVisibleActivity = false;
    }

    public class MyJavascriptInterface {

        @JavascriptInterface
        public void kakaoNavi(final String url) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
//            binding.webView.post(new Runnable() {
//                @Override
//                public void run() {
//                    Log.d(TAG, "run: "+url);
//                    Log.d(TAG, "kakaoNavi: ");
//                    binding.webView.loadUrl(url);
//                }
//            });
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
