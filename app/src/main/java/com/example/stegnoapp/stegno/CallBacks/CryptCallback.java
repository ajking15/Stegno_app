package com.example.stegnoapp.stegno.CallBacks;

import com.example.stegnoapp.stegno.CryptoTask.ImageStegno;

public interface CryptCallback {

    void onEncode();

    void onEncoded(ImageStegno result);

}