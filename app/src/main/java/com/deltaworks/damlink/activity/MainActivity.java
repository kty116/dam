package com.deltaworks.damlink.activity;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.deltaworks.damlink.R;
import com.deltaworks.damlink.databinding.ActivityMainBinding;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    public final String TAG = MainActivity.class.getSimpleName();
    private ActivityMainBinding binding;
    private PagerAdapter pagerAdapter;
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;
    public static boolean sVisibleActivity;  //화면 보이면 노티 눌렀을때 다시 액티비티 켜지지 않게 설정하는 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

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
        // JavaScript이벤트에 대응할 함수를 정의 한 클래스를 붙여줌
        // 두 번째 파라미터는 사용될 php에도 동일하게 사용해야함
        // web client 를 chrome 으로 설정
        binding.webView.setWebChromeClient(new WebChromeClient());

        binding.webView.addJavascriptInterface(new MyJavascriptInterface(), "Android");
        binding.webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
//                binding.progressLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
////                mUrl = url;
//                Log.d(TAG, "onPageFinished: " + url);
//
                if (url.contains("http://work.deltaworks.co.kr:8090/timeline/time_01")) {

                    //갤러리만 나오게게
////                   view.loadUrl("javascript:window.Android.getTaobaoDataHtml();"); //<html></html> 사이에 있는 모든 html을 넘겨준다.  //웹쪽에 코드 넣어서 웹꺼 가져오게 하는거
//                    Log.d(TAG, "getHtml: " + url);
//
////                    binding.progressLayout.setVisibility(View.GONE);
                }
            }
        });

        binding.webView.loadUrl("http://work.deltaworks.co.kr:8090");

    }

    public class MyJavascriptInterface {

        private File mFolder;
        private String mFileName;

        @JavascriptInterface //킷캣 이상에선 어노테이션을 붙여줘야됨
        public void onImageButtonClick(String html) { //위 자바스크립트가 호출되면 여기로 html이 반환됨


        }

        public void onComfirmButtonClick(){

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sVisibleActivity = true;
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

        if (binding.webView.canGoBack()) {
            binding.webView.goBack();
        } else if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            super.onBackPressed();
        } else {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "'뒤로' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }

    class WebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

            binding.progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            binding.progressBar.setVisibility(View.GONE);

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && binding.webView.canGoBack()) {
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

}
