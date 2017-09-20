package org.bbt.kiakoa.tools;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.bbt.kiakoa.R;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;

/**
 * Several tool methods
 */
public class Miscellaneous {

    /**
     * color list
     */
    private static TypedArray colors;

    /**
     * list of available clipart
     */
    public static final ArrayList<SimpleEntry<Integer, Integer>> clipartList;

    static {
        // Be careful ! you can change the "add" order but never change id associated to the drawable.
        clipartList = new ArrayList<>();
        clipartList.add(new SimpleEntry<>(1, R.drawable.clipart_money));
        clipartList.add(new SimpleEntry<>(2, R.drawable.clipart_music));
        clipartList.add(new SimpleEntry<>(3, R.drawable.clipart_movie));
        clipartList.add(new SimpleEntry<>(4, R.drawable.clipart_book));
        clipartList.add(new SimpleEntry<>(5, R.drawable.clipart_tshirt));
        clipartList.add(new SimpleEntry<>(6, R.drawable.clipart_home));
        clipartList.add(new SimpleEntry<>(7, R.drawable.clipart_tool));
        clipartList.add(new SimpleEntry<>(8, R.drawable.clipart_plant));
        clipartList.add(new SimpleEntry<>(9, R.drawable.clipart_pet));
        clipartList.add(new SimpleEntry<>(10, R.drawable.clipart_camera));
        clipartList.add(new SimpleEntry<>(11, R.drawable.clipart_smartphone));
        clipartList.add(new SimpleEntry<>(12, R.drawable.clipart_computer));
        clipartList.add(new SimpleEntry<>(13, R.drawable.clipart_trophy));
        clipartList.add(new SimpleEntry<>(14, R.drawable.clipart_game));
        clipartList.add(new SimpleEntry<>(15, R.drawable.clipart_bar));
        clipartList.add(new SimpleEntry<>(16, R.drawable.clipart_restaurant));
        clipartList.add(new SimpleEntry<>(17, R.drawable.clipart_bike));
        clipartList.add(new SimpleEntry<>(18, R.drawable.clipart_motorcycle));
        clipartList.add(new SimpleEntry<>(19, R.drawable.clipart_car));
        clipartList.add(new SimpleEntry<>(20, R.drawable.clipart_key));
    }

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
     * getter on clipart drawable id with clipart id
     *
     * @param clipartIndex clipart id
     * @return resource id
     */
    public static int getClipartResId(int clipartIndex) {
        int result = R.drawable.ic_item_24dp;
        for (SimpleEntry<Integer, Integer> entry : clipartList) {
            if (entry.getKey() == clipartIndex) {
                return entry.getValue();
            }
        }
        return result;
    }


    /**
     * Pick a color from a name
     *
     * @param name    name
     * @param context a context
     * @return a color
     */
    public static int pickColor(String name, Context context) {

        // init resources if not done
        if (colors == null) {
            colors = context.getResources().obtainTypedArray(R.array.letter_tile_colors);
        }
        // get color
        return colors.getColor(Math.abs(name.hashCode()) % colors.length(), colors.getIndex(0));
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
