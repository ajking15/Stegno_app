package com.example.stegnoapp.ui.decrypt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.example.stegnoapp.R;
import com.example.stegnoapp.databinding.DecryptBinding;
import com.example.stegnoapp.stegno.CallBacks.CryptCallback;
import com.example.stegnoapp.stegno.CryptoTask.Decoding;
import com.example.stegnoapp.stegno.CryptoTask.Encoding;
import com.example.stegnoapp.stegno.CryptoTask.ImageStegno;
import com.example.stegnoapp.ui.decrypt.DecryptModel;
import com.example.stegnoapp.ui.encrypt.Encrypt;

import java.io.IOException;

public class Decrypt extends Fragment implements CryptCallback {

    private DecryptModel slideshowViewModel;
    private DecryptBinding binding;
    private static final String Tag = "Decoding fragment";
    ImageView imageView;
    TextView textView;
    int SELECT_IMAGE_CODE = 100;
    EditText key;
    Decoding decoding;
    ImageStegno imageStegno;
    Uri filepath;
    //
    Bitmap encodedImage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(Tag,"Starting Decode View");
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                new ViewModelProvider(this).get(DecryptModel.class);

    binding = DecryptBinding.inflate(inflater, container, false);
    View root = binding.getRoot();

    textView = binding.textGallery;
    imageView = binding.imageSelect;
    key = binding.key;
    final Button selectButton = binding.selectButton;
    final Button decryptButton = binding.decryptButton;

    //Button to pick the image
    selectButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            imageSelection();
        }
    });
    //Decrypt Image
        decryptButton.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageDecrypt();
            }
        }));
        slideshowViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
    //Opens dialogue to select Image
    private void imageSelection(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Title"), SELECT_IMAGE_CODE);
    }
    private void imageDecrypt(){
        if(filepath != null){
            //New Image Stegnography Obj
            imageStegno = new ImageStegno(key.getText().toString(), encodedImage);
            Log.d(Tag, "UI Key: " + key.getText());
            //Decode Text to image
            decoding = new Decoding(this.getActivity(), Decrypt.this);
            decoding.execute(imageStegno);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SELECT_IMAGE_CODE){
            filepath = data.getData();
            //imgSelect.setImageURI(filepath);
            try {
                encodedImage = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), filepath);
                imageView.setImageURI(filepath);
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
        if(result != null) {
            if (!result.getDecrypted()) {
                textView.setText(result.getKey().toString());
            } else {
                Log.d(Tag,"Message: " + result.getMessage().toString());
                if (!result.getWrongKey()) {
                    textView.setText("Decoded Message: " + result.getMessage().toString());
                } else {
                    textView.setText("Wrong Secret Key");
                }
            }
        }else{
                textView.setText("Select Image First");
            }
        }
}