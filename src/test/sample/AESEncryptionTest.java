package sample;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;


class AESEncryptionTest {

    private AESEncryption aesEncryption;
    private String key = "12345678asdfghjk";

    AESEncryptionTest() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        aesEncryption = new AESEncryption(key);
    }

    @Test
    void getKeyValue() {
        assertArrayEquals(key.getBytes(), aesEncryption.getKeyValue());
    }

    @Test
    void encryptAndEncodeBASE64() throws Exception {
        assertEquals(aesEncryption.encryptAndEncodeBASE64("Jakiś tekst"), "0ev7qw6kGNR2dRAHQY8Wmg==");
    }

    @Test
    void decodeBASE64AndDecrypt() throws Exception {
        assertEquals(aesEncryption.decodeBASE64AndDecrypt("0ev7qw6kGNR2dRAHQY8Wmg=="), "Jakiś tekst");
    }

    @Test
    void encryptText() throws Exception {
        assertArrayEquals(aesEncryption.encryptText("Jakiś tekst"), new byte[] {-47, -21, -5, -85, 14, -92, 24, -44, 118, 117, 16, 7, 65, -113, 22, -102});
        assertArrayEquals(aesEncryption.encryptText(""), new byte[] {94, -115, 24, -104, 110, -113, -56, -48, -40, -54, -16, 97, -23, -38, -58, -111});
    }

    @Test
    void decryptText() throws Exception {
        assertEquals(aesEncryption.decryptText(new byte[] {-47, -21, -5, -85, 14, -92, 24, -44, 118, 117, 16, 7, 65, -113, 22, -102}), "Jakiś tekst");
        assertEquals(aesEncryption.decryptText(new byte[] {94, -115, 24, -104, 110, -113, -56, -48, -40, -54, -16, 97, -23, -38, -58, -111}), "");
    }

    @Test
    void encodeBASE64() {
        assertEquals(aesEncryption.encodeBASE64(new byte[] {-47, -21, -5, -85, 14, -92, 24, -44, 118, 117, 16, 7, 65, -113, 22, -102}), "0ev7qw6kGNR2dRAHQY8Wmg==");
    }

    @Test
    void decodeBASE64() throws IOException {
        assertArrayEquals(aesEncryption.decodeBASE64("0ev7qw6kGNR2dRAHQY8Wmg=="), new byte[] {-47, -21, -5, -85, 14, -92, 24, -44, 118, 117, 16, 7, 65, -113, 22, -102});
    }

}