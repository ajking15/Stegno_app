package com.example.stegnoapp.ui.encrypt;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.stegnoapp.databinding.EncryptBinding;
import com.example.stegnoapp.stegno.CallBacks.CryptCallback;
import com.example.stegnoapp.stegno.CryptoTask.Encoding;
import com.example.stegnoapp.stegno.CryptoTask.ImageStegno;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Encrypt extends Fragment implements CryptCallback {

    private EncryptModel galleryViewModel;
    private EncryptBinding binding;
    private static final String Tag = "Encoding fragment";
    ImageView imgSelect;
    TextView textView;
    int SELECT_IMAGE_CODE = 100;
    EditText message;
    EditText key;
    //
    Encoding encoding;
    ImageStegno imageStegno;
    Uri filepath;
    ProgressBar save;
    //
    Bitmap originalImage;
    Bitmap encodedImage;
    //
    Context myApp = getContext();
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(Tag, "Starting To encode view");
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                new ViewModelProvider(this).get(EncryptModel.class);


        binding = EncryptBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        textView = binding.textGallery;
        final Button selectButton = binding.selectButton;
        final Button encryptButton = binding.encryptButton;
        final Button saveButton = binding.saveButton;
        message = binding.message;
        key  = binding.key;
        imgSelect = binding.imageSelect;
        save = binding.progressBar;
        // Button to pick th image
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageSelection();
            }
        });
        //Encrypt Image
        encryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageEncrypt();
            }
        });
        //Saving Image
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Bitmap saveImage = encodedImage;

                    Thread toEncode = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            saveImageToStorage(saveImage);
                        }
                    });
                    save.setVisibility(View.VISIBLE);
                    toEncode.start();

            }
        });
        galleryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });


        return root;
    }
    //Encrypts the image
    private void imageEncrypt(){
        if(filepath != null && message.getText() != null){
            //New Image Stegnography Obj
            imageStegno = new ImageStegno(key.getText().toString(),message.getText().toString(), originalImage);
            //Encoding Text to image
            encoding = new Encoding(this.getActivity(), Encrypt.this);
            encoding.execute(imageStegno);
        }
    }
    //Opens dialogue to select Image
    private void imageSelection(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Title"), SELECT_IMAGE_CODE);
    }
    //Saves Image to internal Storage
    private void saveImageToStorage(Bitmap image){
        if(ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            Log.d(Tag,"Permission Granted");
        }
        else{
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 112);
        }
        OutputStream outStream;
        // File that saves
        File myFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "_encoded" + ".PNG");
        try{
            outStream = new FileOutputStream(myFile);
            //Compress the image to PNG file
            image.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close(); // CLosing the stream
            textView.setText("Image Saved");

        } catch(FileNotFoundException excep){
            excep.printStackTrace();
        } catch(IOException excep){
            excep.printStackTrace();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SELECT_IMAGE_CODE){
            filepath = data.getData();
            //imgSelect.setImageURI(filepath);
            try {
                originalImage = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), filepath);
                imgSelect.setImageURI(filepath);
            } catch (IOException excep){
                Log.d(Tag, "Error: " + excep);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    @Override
    public void onEncode() {

    }

    @Override
    public void onEncoded(ImageStegno result) {
        if(result != null && result.getEncrypted()){
            encodedImage = result.getEncodedImage();
            textView.setText("Image Encoded!");
            imgSelect.setImageBitmap(encodedImage);
            Log.d(Tag, "Message: " + result.getEncryptedMessage().toString());
            Log.d(Tag, "Key: " + result.getKey().toString());
        }
    }
}
