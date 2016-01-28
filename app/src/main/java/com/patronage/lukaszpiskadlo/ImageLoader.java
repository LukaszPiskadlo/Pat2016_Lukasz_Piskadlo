package com.patronage.lukaszpiskadlo;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

public class ImageLoader {
    private Context context;
    private int imageWidth;
    private int imageHeight;
    private Bitmap placeholderImage;

    private ImageLoader(Context context, int imageWidth, int imageHeight) {
        this.context = context;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

    /**
     * Sets placeholder image
     * @param id image resource id
     */
    public void setPlaceholderImage(int id) {
        placeholderImage = BitmapFactory.decodeResource(context.getResources(), id);
    }

    /**
     * loads image into ImageView
     * @param url image url
     */
    public void loadImage(String url, ImageView imageView) {
        if(url.isEmpty()) {
            return;
        }

        if(cancelPotentialWork(url, imageView)) {
            ImageLoaderTask task = new ImageLoaderTask(url, imageView);
            AsyncDrawable asyncDrawable = new AsyncDrawable(context.getResources(), placeholderImage, task);
            imageView.setImageDrawable(asyncDrawable);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    /**
     * Checks if another task is associated with imageView
     * @return true if task was canceled, false if task is running
     */
    private boolean cancelPotentialWork(String url, ImageView imageView) {
        ImageLoaderTask imageLoaderTask = getImageLoaderTask(imageView);

        if (imageLoaderTask != null) {
            String imageUrl = imageLoaderTask.imageUrl;
            if (imageUrl.isEmpty() || !imageUrl.equals(url)) {
                imageLoaderTask.cancel(true);
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns task associated with this ImageView
     */
    private static ImageLoaderTask getImageLoaderTask(ImageView imageView) {
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getImageLoaderTask();
            }
        }
        return null;
    }

    /**
     * downloads and process images asynchronously
     */
    private class ImageLoaderTask extends AsyncTask<Void, Void, BitmapDrawable> {
        private WeakReference<ImageView> imageViewReference;
        private String imageUrl;

        public ImageLoaderTask(String url, ImageView imageView) {
            imageUrl = url;
            imageViewReference = new WeakReference<>(imageView);
        }

        @Override
        protected BitmapDrawable doInBackground(Void... params) {
            Bitmap image = null;
            BitmapDrawable drawable = null;
            if(!isCancelled() && getAttachedImageView() != null) {
                try {
                    image = downloadImage(imageUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(image != null) {
                drawable = new BitmapDrawable(context.getResources(), image);
            }
            return drawable;
        }

        @Override
        protected void onPostExecute(BitmapDrawable drawable) {
            super.onPostExecute(drawable);

            ImageView imageView = getAttachedImageView();
            if(imageView != null) {
                imageView.setImageDrawable(drawable);
            }
        }

        /**
         * Downloads image from given url
         * @return image
         * @throws IOException
         */
        private Bitmap downloadImage(String url) throws IOException {
            InputStream inputStream = null;
            HttpURLConnection connection = null;
            try {
                // setup connection
                URL urlObj = new URL(url);
                connection = (HttpURLConnection) urlObj.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();

                int responseCode = connection.getResponseCode();
                if(responseCode == HttpURLConnection.HTTP_OK) {
                    inputStream = connection.getInputStream();
                    return decodeImage(inputStream);
                }
                cancel(true);
                return null;
            } finally {
                if(inputStream != null) {
                    inputStream.close();
                }
                if(connection != null) {
                    connection.disconnect();
                }
            }
        }

        /**
         * Decodes image from stream and resize it
         */
        private Bitmap decodeImage(InputStream inputStream) {
            // decode to check dimensions
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, options);

            options.inSampleSize = calculateInSampleSize(options);
            options.inJustDecodeBounds = true;
            return BitmapFactory.decodeStream(inputStream, null, options);
        }

        /**
         * Calculates scaling factor to resize image
         */
        public int calculateInSampleSize(BitmapFactory.Options options) {
            int height = options.outHeight;
            int width = options.outWidth;
            int inSampleSize = 1;

            if(height > imageHeight || width > imageWidth) {
                final int halfHeight = height / 2;
                final int halfWidth = width / 2;

                while((halfHeight / inSampleSize) > imageHeight && (halfWidth / inSampleSize) > imageWidth) {
                    inSampleSize *= 2;
                }
            }
            return inSampleSize;
        }

        /**
         * Returns ImageView associated with this task
         * @return ImageView or null
         */
        private ImageView getAttachedImageView() {
            ImageView imageView = imageViewReference.get();
            ImageLoaderTask imageLoaderTask = getImageLoaderTask(imageView);

            if(this == imageLoaderTask) {
                return imageView;
            }
            return null;
        }
    }

    /**
     * Stores reference to image loader task and sets placeholder image
     */
    private static class AsyncDrawable extends BitmapDrawable {
        private WeakReference<ImageLoaderTask> imageLoaderTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap, ImageLoaderTask imageLoaderTask) {
            super(res, bitmap);
            imageLoaderTaskReference = new WeakReference<>(imageLoaderTask);
        }

        public ImageLoaderTask getImageLoaderTask() {
            return imageLoaderTaskReference.get();
        }
    }
}
