package org.bbt.kiakoa.tools;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;

import org.bbt.kiakoa.fragment.LoanDetails.LoanDetailsFragment;

/**
 * Several tool methods
 */
public class Miscellaneous {

    /**
     * For logs
     */
    private static final String TAG = "Miscellaneous";

    /**
     * Intent to take a picture from camera
     */
    private static final Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

    /**
     * Check is internet is available
     *
     * @param context a context
     * @return internet available or not
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        } else {
            return false;
        }
    }

    /**
     * Launch activity to take a picture
     *
     * @param fragment    fragment requiring activity to start
     */
    public static void takePicture(Fragment fragment) {
        Log.i(TAG, "take picture action requested");
        fragment.startActivityForResult(takePictureIntent, LoanDetailsFragment.REQUEST_CODE_PICTURE);
    }

    /**
     * Launch activity to select a local image from
     *
     * @param fragment    fragment requiring activity to start
     */
    public static void showImageGallery(Fragment fragment) {
        Log.i(TAG, "show gallery action requested");
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        fragment.startActivityForResult(Intent.createChooser(intent, "Select image"), LoanDetailsFragment.REQUEST_CODE_PICTURE);
    }

    /**
     * Resize a {@link Bitmap}
     *
     * @param realImage    image to resize
     * @param maxImageSize desired image size
     * @return resized image
     */
    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize) {

        float ratio = Math.min(maxImageSize / realImage.getWidth(), maxImageSize / realImage.getHeight());
        int width = Math.round(ratio * realImage.getWidth());
        int height = Math.round(ratio * realImage.getHeight());

        return Bitmap.createScaledBitmap(realImage, width, height, true);
    }

}
