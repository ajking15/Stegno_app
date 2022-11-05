package com.example.stegnoapp;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.stegnoapp.stegno.Tools.CryptAlgo;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class CryptoUnitTest {
    CryptAlgo ca = new CryptAlgo();

    @Test
    public void messageEncryption() throws Exception {
        assertEquals("fv+hlhBwkkUXLb6N88QE5g==", ca.encryptMessage("test message", "key"));
        assertEquals("WmgeiFWWdd8RpKpbicT8zzG9w5s4ysRLcWmsK+IYHW8=", ca.encryptMessage("bababababbaabbaa", "bab"));
        assertEquals("BLMKU8SZdjT79ewC+x2lrQ==", ca.encryptMessage("hahahahahahhaha", "ha"));
        assertEquals("FRro6ehLm2HIvLdsS5UM0TKM/X1sllv9UZofUlXBJe4=", ca.encryptMessage("This is a test message", "A@$$"));
        assertEquals("ODgIuB+Q0IIsVVRXZAAtUkyfdqplQLv1PE77gAwkyj0=", ca.encryptMessage("I know im pretty awesome", "M3"));
    }

    @Test
    public void MessageDecryption() throws Exception{
        assertEquals("test message", ca.decryptMessage("fv+hlhBwkkUXLb6N88QE5g==", "key"));
        assertEquals("bababababbaabbaa", ca.decryptMessage("WmgeiFWWdd8RpKpbicT8zzG9w5s4ysRLcWmsK+IYHW8=", "bab"));
        assertEquals("hahahahahahhaha", ca.decryptMessage("BLMKU8SZdjT79ewC+x2lrQ==", "ha"));
        assertEquals("This is a test message", ca.decryptMessage("FRro6ehLm2HIvLdsS5UM0TKM/X1sllv9UZofUlXBJe4=", "A@$$"));
        assertEquals("I know im pretty awesome", ca.decryptMessage("ODgIuB+Q0IIsVVRXZAAtUkyfdqplQLv1PE77gAwkyj0=", "M3"));
    }

    @Test
    public void encryptToDecrypt() throws Exception{
        assertEquals("My hair is brown", ca.decryptMessage(ca.encryptMessage("My hair is brown", "L0V3U5"), "L0V3U5"));
        assertEquals("I love you", ca.decryptMessage(ca.encryptMessage("I love you", "Lalala123#35"), "Lalala123#35"));
        assertEquals("My earring is pretty", ca.decryptMessage(ca.encryptMessage("My earring is pretty", "YuSun123"), "YuSun123"));
        assertEquals("Why is life hard", ca.decryptMessage(ca.encryptMessage("Why is life hard", "IDK??"), "IDK??"));
    }
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
}