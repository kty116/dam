package com.deltaworks.damlink.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.deltaworks.damlink.R;
import com.deltaworks.damlink.databinding.ActivityMainBinding;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;

public class MainActivity3 extends AppCompatActivity {

    public final String TAG = MainActivity3.class.getSimpleName();
    private ActivityMainBinding binding;
    private PagerAdapter pagerAdapter;
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;
    public static boolean sVisibleActivity;  //화면 보이면 노티 눌렀을때 다시 액티비티 켜지지 않게 설정하는 변수

    //    private ValueCallback<Uri> filePathCallbackNormal;
//    private ValueCallback<Uri[]> filePathCallbackLollipop;
//    private Uri mCapturedImageURI;
//    private final static int FILECHOOSER_NORMAL_REQ_CODE = 1;
//    private final static int FILECHOOSER_LOLLIPOP_REQ_CODE = 2;
    private boolean isChoosed = false;

    private String mCM;
    private ValueCallback<Uri> mUM;
    private ValueCallback<Uri[]> mUMA;
    private final static int FCR = 1;


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
//        super.onActivityResult(requestCode, resultCode, intent);
//        if (Build.VERSION.SDK_INT >= 21) {
//            Uri[] results = null;
//            //Check if response is positive
//            if (resultCode == Activity.RESULT_OK) {
//                if (requestCode == FCR) {
//                    if (null == mUMA) {
//                        return;
//                    }
//                    if (intent == null || intent.getData() == null) {
//                        //Capture Photo if no image available
//                        if (mCM != null) {
//                            results = new Uri[]{Uri.parse(mCM)};
//                        }
//                    } else {
//                        String dataString = intent.getDataString();
//                        if (dataString != null) {
//                            results = new Uri[]{Uri.parse(dataString)};
//                        }
//                    }
//                }
//            }
//            mUMA.onReceiveValue(results);
//            mUMA = null;
//        } else {
//            if (requestCode == FCR) {
//                if (null == mUM) return;
//                Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
//                mUM.onReceiveValue(result);
//                mUM = null;
//            }
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == RESULT_OK &&
                (requestCode == PhotoPicker.REQUEST_CODE || requestCode == PhotoPreview.REQUEST_CODE)) {


            List<String> photos = null;
            if (intent != null) {
                photos = intent.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
            }

            if (photos != null) {
//                for (int i = 0; i < photos.size(); i++) {
//                    results[i] = Uri.parse(photos.get(i));
//                }
                if (Build.VERSION.SDK_INT >= 21) {
                    Uri[] results;


                    for (int i = 0; i < photos.size(); i++) {
                        results = new Uri[]{Uri.parse(photos.get(i))};
                        mUMA.onReceiveValue(results);

                    }

//                    for (int i = 0; i < results.length; i++) {
//                        Log.d(TAG, "onActivityResult: " + photos.get(i).toString());
//
//                    }
                    mUMA = null;
                } else {
                    for (int i = 0; i < photos.size(); i++) {
                        mUM.onReceiveValue((Uri) photos);
                    }
                    mUM = null;
                }


//                if (Build.VERSION.SDK_INT >= 21) {
//                    Uri[] results = null;
                //Check if response is positive
//                if (null == mUMA) {
//                    return;
//                }
//                if (intent == null || intent.getData() == null) {
//                    //Capture Photo if no image available
//                    if (mCM != null) {
//                        results = new Uri[]{Uri.parse(mCM)};
//                    }
//                } else {
//                    String dataString = intent.getDataString();
//                    if (dataString != null) {
//                        results = new Uri[]{Uri.parse(dataString)};
//                    }
//                }
//                    mUMA.onReceiveValue(results);
//                    mUMA = null;
//                } else {
////                if (requestCode == FCR) {
////                    if (null == mUM) return;
////                    Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
//                    mUM.onReceiveValue(result);
//                    mUM = null;
//                }
            }


        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        binding.webView.setWebChromeClient(new WebChromeClient() {

            //For Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                mUM = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "File Chooser"), FCR);
            }

            // For Android 3.0+, above method not supported in some android 3+ versions, in such case we use this
            public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
                mUM = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(
                        Intent.createChooser(i, "File Browser"),
                        FCR);
            }

            //For Android 4.1+
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                mUM = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "File Chooser"), FCR);
            }

            //For Android 5.0+
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {

                if (mUMA != null) {
                    mUMA.onReceiveValue(null);
                }
                mUMA = filePathCallback;

                PhotoPicker.builder()
                        .setPhotoCount(5)
                        .setGridColumnCount(4)
                        .start(MainActivity3.this);

//                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//                    File photoFile = null;
//                    try {
//                        photoFile = createImageFile();
//                        takePictureIntent.putExtra("PhotoPath", mCM);
//                    } catch (IOException ex) {
//                        Log.e(TAG, "Image file creation failed", ex);
//                    }
//                    if (photoFile != null) {
//                        mCM = "file:" + photoFile.getAbsolutePath();
//                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
//                    } else {
//                        takePictureIntent = null;
//                    }
//                }
//                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);  //타입에 맞는 값이 있는 리스트들
//                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
//                contentSelectionIntent.setType("image/*");
//                Intent[] intentArray;
//                if (takePictureIntent != null) {
//                    intentArray = new Intent[]{takePictureIntent};
//                } else {
//                    intentArray = new Intent[0];
//                }
//
//                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
//                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
//                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
//                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
//                startActivityForResult(chooserIntent, FCR);
                return true;
            }
//            // For Android < 3.0
//            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
//                openFileChooser(uploadMsg, "");
//            }
//
//            // For Android 3.0+
//            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
//                filePathCallbackNormal = uploadMsg;
//                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
//                i.addCategory(Intent.CATEGORY_OPENABLE);
//                i.setType("*/*");
//                startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_NORMAL_REQ_CODE);
//            }
//
//            // For Android 4.1+
//            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
//                openFileChooser(uploadMsg, acceptType);
//            }
//
//
//            // For Android 5.0+
//            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
//
//                if (filePathCallbackLollipop != null) {
//                    filePathCallbackLollipop = null;
//                }
//                filePathCallbackLollipop = filePathCallback;
//
//                Log.d(TAG, "onShowFileChooser: ddddd");
//                setDialog();
//
////
////                Intent captureIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
////                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
////
////                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
////                i.addCategory(Intent.CATEGORY_OPENABLE);
////                i.setType("image/*");
////
////                // Create file chooser intent
////                Intent chooserIntent = Intent.createChooser(i, "File Chooser");
////                // Set camera intent to file chooser
////                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{captureIntent});
////
////                // On select image call onActivityResult method of activity
////                startActivityForResult(chooserIntent, FILECHOOSER_LOLLIPOP_REQ_CODE);
//                return false;
//
//
//            }
        });
//
        binding.webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                binding.progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                binding.progressBar.setVisibility(View.INVISIBLE);
            }

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

        });

        WebSettings webSettings = binding.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        binding.webView.loadUrl("http://work.deltaworks.co.kr:8090");

    }

    private File createImageFile() throws IOException {
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "img_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    public void camera() {
//        if (filePathCallbackLollipop != null) {
//            filePathCallbackLollipop = null;
//        }
//        filePathCallbackLollipop = filePathCallback;

//        File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "AndroidExampleFolder");
//        if (!imageStorageDir.exists()) {
//             Create AndroidExampleFolder at sdcard
//            imageStorageDir.mkdirs();
//        }
//
//         Create camera captured image file path and name
//        File file = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
//        mCapturedImageURI = Uri.fromFile(file);

//        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
//
//        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
//        i.addCategory(Intent.CATEGORY_OPENABLE);
//        i.setType("image/*");
//
//        // Create file chooser intent
//        Intent chooserIntent = Intent.createChooser(i, "Image Chooser");
//        // Set camera intent to file chooser
//        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{captureIntent});
//
//        // On select image call onActivityResult method of activity
//        startActivityForResult(chooserIntent, FILECHOOSER_LOLLIPOP_REQ_CODE);

//        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
//
//        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
//        i.addCategory(Intent.CATEGORY_OPENABLE);
//        i.setType("image/*");
//
//        // Create file chooser intent
//        Intent chooserIntent = Intent.createChooser(i, "Image Chooser");
//        // Set camera intent to file chooser
//        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{captureIntent});
//
//        // On select image call onActivityResult method of activity
//        startActivityForResult(chooserIntent, FILECHOOSER_LOLLIPOP_REQ_CODE);
    }

    public void gallery() {


    }

    private void setDialog() {

        final String[] items = {"사진 촬영", "앨범 선택"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity3.this);
        builder.setTitle("업로드할 이미지 선택")
                .setCancelable(true)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (items[which]) {
                            case "사진 촬영":

                                camera();
                                isChoosed = true;
                                break;

                            case "앨범 선택":
                                gallery();
                                isChoosed = true;
                                break;
                        }
                    }
                })
                .show();
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
