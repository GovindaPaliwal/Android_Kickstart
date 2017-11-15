package com.android.kickstart.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;

import com.android.kickstart.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;


public class BitmapUtil {

    /*
     * @param imagePath
     * @return
     * @throws IOException
     */
    public static Bitmap adjustImageOrientation(String imagePath) throws IOException {
        Bitmap image = null;
        File mFile = new File(imagePath);
        if (mFile.exists())
            image = BitmapFactory.decodeFile(mFile.getAbsolutePath());
        else
            return image;

        Bitmap bitmap = null;
        ExifInterface exif = new ExifInterface(imagePath);
        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        int rotate = 0;
        switch (exifOrientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                bitmap = rotateBitmap(image, rotate);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                bitmap = rotateBitmap(image, rotate);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                bitmap = rotateBitmap(image, rotate);
                break;
            default:
                bitmap = image;
        }
        return bitmap;
    }

    /*
     * @param bitmap
     * @param degree
     * @return
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int degree) {
        // Setting pre rotate
        Matrix mMatrix = new Matrix();
        mMatrix.preRotate(degree);
        // Rotating Bitmap & convert to ARGB_8888
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mMatrix, true);
    }

    /*
     * @param bitmap
     * @return
     */
    public static Bitmap cropBitmapToSquare(Bitmap bitmap) {

        Bitmap result = null;
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        if (height <= width) {
            result = Bitmap.createBitmap(bitmap, (width - height) / 2, 0, height, height);
        } else {
            result = Bitmap.createBitmap(bitmap, 0, (height - width) / 2, width, width);
        }
        return result;
    }

    /*
     * @param drawableName
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public static int getDrawableIdByName(String drawableName) throws NoSuchFieldException, IllegalAccessException {
        if (drawableName == null)
            return -1;
        Field field = R.drawable.class.getDeclaredField(drawableName);
        int id = field.getInt(field);
        return id;
    }

    /**
     * @param bitmap
     * @param round
     * @return
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float round) {
        // Source image size
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // create result bitmap output
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        // set canvas for painting
        Canvas canvas = new Canvas(result);
        canvas.drawARGB(0, 0, 0, 0);
        // configure paint
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.TRANSPARENT);
        // configure rectangle for embedding
        Rect rect = new Rect(0, 0, width, height);
        RectF rectF = new RectF(rect);
        // draw Round rectangle to canvas
        canvas.drawRoundRect(rectF, round, round, paint);
        // create Xfer mode
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        // draw source image to canvas
        canvas.drawBitmap(bitmap, rect, rect, paint);
        // return final image
        return result;
    }

    /**
     * @param bitmap
     * @param newSize
     * @return
     */
    public static Bitmap getResizedBitmap(Bitmap bitmap, int newSize) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth, newHeight;
        if (width >= height) {
            newHeight = newSize;
            newWidth = (int) (width * ((float) newSize / height));
        } else {
            newWidth = newSize;
            newHeight = (int) (height * ((float) newSize / width));
        }
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);
        // "RECREATE" THE NEW BITMAP
        System.gc();
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    /**
     * @param bitmap
     * @param newHeight
     * @param newWidth
     * @return
     */
    public static Bitmap getResizedBitmap(Bitmap bitmap, int newHeight, int newWidth) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        System.gc();
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    /*
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable)
            return ((BitmapDrawable) drawable).getBitmap();

        Bitmap mBitmap;
        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            mBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            mBitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(mBitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return mBitmap;
    }

    /*
     * @param context
     * @param url
     * @return
     * @throws IOException
     */
    public static Drawable drawableFromUrl(Context context, String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.connect();
        InputStream input = connection.getInputStream();

        Bitmap mBitmap = BitmapFactory.decodeStream(input);
        return new BitmapDrawable(context.getResources(), mBitmap);
    }

    /*
     * @param context
     * @param maskResourceId
     * @param mBitmap
     * @return
     */
    public static Bitmap getMaskedBitmap(Context context,int maskResourceId, Bitmap mBitmap) {
        Bitmap original = mBitmap;
        Bitmap mask = BitmapFactory.decodeResource(context.getResources(), maskResourceId);
        Bitmap result = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas mCanvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mCanvas.drawBitmap(original, 0, 0, null);
        mCanvas.drawBitmap(mask, 0, 0, paint);
        paint.setXfermode(null);

        if (original != null) {
            original.recycle();
        }

        if (mask != null) {
            mask.recycle();
        }

        return result;
    }

    /*
     * @param context
     * @param mask_img_id
     * @param color
     * @return
     */
    public static Bitmap getMaskedImageByColor(Context context,int mask_img_id, int color) {
        Bitmap mask = BitmapFactory.decodeResource(context.getResources(), mask_img_id);
        Bitmap result = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(), Bitmap.Config.ARGB_8888);
        // and width of the previous bitmap
        Canvas mCanvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mCanvas.drawColor(color);
        mCanvas.drawBitmap(mask, 0, 0, paint);
        paint.setXfermode(null);
        if (mask != null) {
            mask.recycle();
        }
        return result;
    }
}
