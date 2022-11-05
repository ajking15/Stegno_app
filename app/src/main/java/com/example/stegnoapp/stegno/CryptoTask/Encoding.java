package com.example.stegnoapp.stegno.CryptoTask;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.example.stegnoapp.stegno.CallBacks.CryptCallback;
import com.example.stegnoapp.stegno.Tools.Utils;
import com.example.stegnoapp.ui.encrypt.Encrypt;

import java.util.List;

/**
 * In this class all those method in EnDeCode class are used to encode secret message in image.
 * All the tasks will run in background.
 */
public class Encoding extends AsyncTask<ImageStegno, Integer, ImageStegno> {

    //Tag for Log
    private static final String Tag = Encoding.class.getName();

    private final ImageStegno result;
    //Callback interface for AsyncTask
    private final CryptCallback callbackInterface;
    private int maximumProgress;
    // private final ProgressDialog progressDialog;

    public Encoding(Activity activity, CryptCallback callbackInterface) {
        super();
        // this.progressDialog = new ProgressDialog(activity);
        this.callbackInterface = callbackInterface;
        //making result object
        this.result = new ImageStegno();
    }

    //pre execution of method
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(ImageStegno textStegnography) {
        super.onPostExecute(textStegnography);
        callbackInterface.onEncoded(result);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected ImageStegno doInBackground(ImageStegno... imageSteganographies) {

        maximumProgress = 0;

        if (imageSteganographies.length > 0) {

            ImageStegno textStegnography = imageSteganographies[0];

            //getting image bitmap
            Bitmap bitmap = textStegnography.getImage();

            //getting height and width of original image
            int orgHeight = bitmap.getHeight();
            int orgWidth = bitmap.getWidth();

            //splitting bitmap
            List<Bitmap> src_list = Utils.splitImage(bitmap);

            //encoding encrypted compressed message into image

            List<Bitmap> encoded_list = EnDeCode.encodeMessage(src_list, textStegnography.getEncryptedMessage(), new EnDeCode.ProgressHandler() {
                        @Override
                        public void setTotal(int tot) {
                            maximumProgress = tot;
                            // progressDialog.setMax(maximumProgress);
                            Log.d(Tag, "Total Length: " + tot);
                        }

                        @Override
                        public void increment(int inc) {
                            publishProgress(inc);
                        }

                        @Override
                        public void finished() {
                            Log.d(Tag, "Finished Encoding!");
                            //progressDialog.setIndeterminate(true);
                        }
                    });

                    //free Memory
            for (Bitmap bitm : src_list)
                bitm.recycle();

            //Java Garbage collector
            System.gc();

            //merging the split encoded image
            Bitmap srcEncoded = Utils.mergeImage(encoded_list, orgHeight, orgWidth);

            //Setting encoded image to result
            result.setEncodedImage(srcEncoded);
            result.setEncrypted(true);
        }
        Log.d(Tag, "Results: " + result.getKey());
        return result;
    }
}
