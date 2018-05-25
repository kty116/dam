package com.deltaworks.damlink.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;

/**
 * Created by Administrator on 2018-05-24.
 */

public class EditImageUtil {

    public static final String TAG = EditImageUtil.class.getSimpleName();
    private ExifInterface mExifInterface;

    /**
     * 사진 회전
     *
     * @return
     */
    public void rotateImage(Context context, String imageUri) {

        Bitmap bitmap = null;
        int orientation = -1;

        Log.d(TAG, "rotateImage: " + imageUri);


        try {

            mExifInterface = new ExifInterface(imageUri);

            switch (getOrientation()) {
                case ExifInterface.ORIENTATION_NORMAL:
                    Log.d(TAG, "rotateImage: ORIENTATION_NORMAL");
                    orientation = -1; //디폴트값
                    break;

                case ExifInterface.ORIENTATION_ROTATE_90:
                    Log.d(TAG, "rotateImage: ORIENTATION_ROTATE_90");
                    orientation = 90;
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    Log.d(TAG, "rotateImage: ORIENTATION_ROTATE_180");
                    orientation = 180;
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    Log.d(TAG, "rotateImage: ORIENTATION_ROTATE_270");
                    orientation = 270;
                    break;
            }

            Log.d(TAG, "rotateImage: " + orientation);

            if (orientation != -1) {


                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse("file://" + imageUri));

                Bitmap rotationBitmap = rotateImage(bitmap, orientation);  //이미지 회전값 만큼 회전

                File file = new File(imageUri);

                saveImage(file, rotationBitmap);

                saveExif(file);  //원래 exif값 유지하고 사진 orientation 수정
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public int getOrientation() {

        int orientationValue = mExifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
        return orientationValue;

    }

    public void setOrientation(ExifInterface finalExif) {
        finalExif.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(ExifInterface.ORIENTATION_NORMAL));
    }

    public Bitmap rotateImage(Bitmap src, float degree) {

        Matrix matrix = new Matrix();
        // 회전 각도 셋팅
        matrix.postRotate(degree);
        // 이미지와 Matrix 를 셋팅해서 Bitmap 객체 생성
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(),
                src.getHeight(), matrix, true);
    }

    public void saveImage(File file, Bitmap bitmap) {


        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
//            Log.d("----------", "getAlbumStorageDir: " + mFolder + "/" + mFileName);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            //파일 저장 후 미디어 스캐닝
//            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
//                    Uri.parse("file://" + mFolder + "/" + mFileName)));
            Log.d(TAG, "saveImage: 저장 완료");


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * exif 원래 값 유지하고 orientation 값 회전 후 넣기
     * @param file
     * @return
     */
    public ExifInterface saveExif(File file) {

        ExifInterface originalExif = mExifInterface;

        ExifInterface finalExif = null;

        try {
            finalExif = new ExifInterface(file.getAbsolutePath());
            copyExifWithoutLengthWidth(originalExif, finalExif);
            finalExif.saveAttributes();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "setOrientation: "+finalExif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1));
        return finalExif;

    }


    private void copyExifWithoutLengthWidth(ExifInterface originalExif, ExifInterface finalExif) {

        for (Field f : ExifInterface.class.getFields()) {

            String name = f.getName();

            if (!name.startsWith("TAG_")) {

                continue;

            }

            String key = null;

            try {

                key = (String) f.get(null);

            } catch (Exception e) {

                continue;

            }

            if (key == null) {

                continue;

            }

            if (key.equals(ExifInterface.TAG_IMAGE_LENGTH) || key.equals(ExifInterface.TAG_IMAGE_WIDTH)) {

                continue;

            }

            String value = originalExif.getAttribute(key);

            if (value == null) {

                continue;

            }

            finalExif.setAttribute(key, value);

        }

        setOrientation(finalExif);

    }

}
