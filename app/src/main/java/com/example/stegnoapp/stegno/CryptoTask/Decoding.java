package com.example.stegnoapp.stegno.CryptoTask;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.example.stegnoapp.stegno.CallBacks.CryptCallback;
import com.example.stegnoapp.stegno.Tools.CryptAlgo;
import com.example.stegnoapp.stegno.Tools.Utils;

import java.util.List;

/**
 * In this class all those method in EnDeCode class are used to decode secret message in image.
 * All the tasks will run in background.
 */
public class Decoding extends AsyncTask<ImageStegno, Void, ImageStegno> {

    //Tag for Log
    private final static String Tag = Decoding.class.getName();

    private final ImageStegno result;
    //Callback interface for AsyncTask
    private final CryptCallback textDecodingCallback;
    //private ProgressDialog progressDialog;

    public Decoding(Activity activity, CryptCallback textDecodingCallback) {
        super();
        //this.progressDialog = new ProgressDialog(activity);
        this.textDecodingCallback = textDecodingCallback;
        //making result object
        this.result = new ImageStegno();
    }

    //pre execution of method
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(ImageStegno ImageStegno) {
        super.onPostExecute(ImageStegno);
        //sending result to callback
        textDecodingCallback.onEncoded(result);
    }

    @Override
    protected ImageStegno doInBackground(ImageStegno... imageSteganographies) {

        //If it is not already decoded
        if (imageSteganographies.length > 0) {

            ImageStegno ImageStegno = imageSteganographies[0];

            //getting bitmap image from file
            Bitmap bitmap = ImageStegno.getImage();

            //splitting images
            List<Bitmap> srcEncodedList = Utils.splitImage(bitmap);

            //decoding encrypted zipped message
            String decoded_message = EnDeCode.decodeMessage(srcEncodedList);

            Log.d(Tag, "Decoded_Message : " + decoded_message);

            //text decoded = true
            if (!Utils.stringEmpty(decoded_message)) {
                result.setDecrypted(true);
            }

            //decrypting the encoded message
            String decrypted_message = ImageStegno.decryptMessage(decoded_message, ImageStegno.getKey());
            Log.d(Tag, "Decrypted message : " + decrypted_message);

            //If decrypted_message is null it means that the secret key is wrong otherwise secret key is right.
            if (!Utils.stringEmpty(decrypted_message)) {

                //secret key provided is right
                result.setWrongKey(false);

                // Set Results

                result.setMessage(decrypted_message);


                //free memory
                for (Bitmap bitm : srcEncodedList)
                    bitm.recycle();

                //Java Garbage Collector
                System.gc();
            }
        }

        return result;
    }
}