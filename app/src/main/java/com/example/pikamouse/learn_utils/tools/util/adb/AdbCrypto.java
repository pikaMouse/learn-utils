package com.example.pikamouse.learn_utils.tools.util.adb;

import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * @author: jiangfeng
 * @date: 2019/1/22
 */
public class AdbCrypto {
    /** An RSA keypair encapsulated by the AdbCrypto object */
    private KeyPair mKeyPair;
    /** The ADB RSA key length in bits */
    private final static int KEY_LENGTH_BITS = 2048;
    /** The ADB RSA key length in bytes */
    public static final int KEY_LENGTH_BYTES = KEY_LENGTH_BITS / 8;
    /** The ADB RSA key length in words */
    public static final int KEY_LENGTH_WORDS = KEY_LENGTH_BYTES / 4;
    /** The RSA signature padding as an int array */
    public static final int[] SIGNATURE_PADDING_AS_INT = new int[]
            {
                    0x00,0x01,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,
                    0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,
                    0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,
                    0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,
                    0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,
                    0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,
                    0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,
                    0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,
                    0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,
                    0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,
                    0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,
                    0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,
                    0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,
                    0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,
                    0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,
                    0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,
                    0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0xff,0x00,
                    0x30,0x21,0x30,0x09,0x06,0x05,0x2b,0x0e,0x03,0x02,0x1a,0x05,0x00,
                    0x04,0x14
            };

    /** The RSA signature padding as a byte array */
    public static byte[] SIGNATURE_PADDING;
    static {
        SIGNATURE_PADDING = new byte[SIGNATURE_PADDING_AS_INT.length];

        for (int i = 0; i < SIGNATURE_PADDING.length; i++)
            SIGNATURE_PADDING[i] = (byte)SIGNATURE_PADDING_AS_INT[i];
    }

    /**
     * ADB literally just saves the RSAPublicKey struct to a file.
     *
     * 		 typedef struct RSAPublicKey {
     *          uint32_t modulus_size_words;             // Length of n[] in number of uint32_t
     *          uint32_t n0inv;                         // Precomputed montgomery parameter: -1 / n[0] mod 2^32
     *          uint32_t n[RSANUMWORDS];                // modulus as a little-endian array
     *          uint32_t rr[RSANUMWORDS];               // R^2 as little-endian array  of little-endian words
     *          int exponent;                           // 3 or 65537
     *        } RSAPublicKey;
     *
     */
    private static byte[] convertRsaPublicKeyToAdbFormat(RSAPublicKey pubKey) {
        BigInteger r32, r, rr, rem, n, n0inv;

        r32 = BigInteger.ZERO.setBit(32);
        n = pubKey.getModulus();
        r = BigInteger.ZERO.setBit(KEY_LENGTH_WORDS * 32);
        rr = r.modPow(BigInteger.valueOf(2), n);
        rem = n.remainder(r32);
        n0inv = rem.modInverse(r32);

        int myN[] = new int[KEY_LENGTH_WORDS];
        int myRr[] = new int[KEY_LENGTH_WORDS];
        BigInteger res[];
        for (int i = 0; i < KEY_LENGTH_WORDS; i++)
        {
            res = rr.divideAndRemainder(r32);
            rr = res[0];
            rem = res[1];
            myRr[i] = rem.intValue();

            res = n.divideAndRemainder(r32);
            n = res[0];
            rem = res[1];
            myN[i] = rem.intValue();
        }
        ByteBuffer bbuf = ByteBuffer.allocate(524).order(ByteOrder.LITTLE_ENDIAN);


        bbuf.putInt(KEY_LENGTH_WORDS);
        bbuf.putInt(n0inv.negate().intValue());
        for (int i : myN)
            bbuf.putInt(i);
        for (int i : myRr)
            bbuf.putInt(i);

        bbuf.putInt(pubKey.getPublicExponent().intValue());
        return bbuf.array();
    }

    /**
     * Gets the RSA public key in ADB format.
     */
    public byte[] getAdbPublicKeyPayload() throws IOException {
        byte[] convertedKey = convertRsaPublicKeyToAdbFormat((RSAPublicKey)mKeyPair.getPublic());
        StringBuilder keyString = new StringBuilder(720);
        /* The key is base64 encoded with a user@host suffix and terminated with a NUL */
        keyString.append(Base64.encodeToString(convertedKey, Base64.DEFAULT));
        keyString.append(" unknown@unknown");
        keyString.append('\0');

        return keyString.toString().getBytes("UTF-8");
    }

    /**
     * 用私钥对随机token进行加密
     */
    public byte[] signAdbTokenWithPrivateKey(byte[] payload) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher c = Cipher.getInstance("RSA/ECB/NoPadding");
        c.init(Cipher.ENCRYPT_MODE, mKeyPair.getPrivate());
        return c.doFinal(payload);
    }

    /**
     * 随机生成RSA密钥对
     * 密钥长度，范围：512～2048
     */
    public static AdbCrypto generateAdbKeyPair() throws NoSuchAlgorithmException {
        AdbCrypto adbCrypto = new AdbCrypto();
        KeyPairGenerator rsaKeyPair = KeyPairGenerator.getInstance("RSA");
        rsaKeyPair.initialize(KEY_LENGTH_BITS);
        adbCrypto.mKeyPair = rsaKeyPair.genKeyPair();
        return adbCrypto;
    }

    /**
     * Saves the AdbCrypto's key pair to the specified files.
     * @param privateKey The file to store the encoded private key
     * @param publicKey The file to store the encoded public key
     * @throws IOException If the files cannot be written
     */
    public void saveAdbKeyPair(File privateKey, File publicKey) throws IOException
    {
        FileOutputStream privOut = new FileOutputStream(privateKey);
        FileOutputStream pubOut = new FileOutputStream(publicKey);

        privOut.write(mKeyPair.getPrivate().getEncoded());
        pubOut.write(mKeyPair.getPublic().getEncoded());

        privOut.close();
        pubOut.close();
    }

    /**
     * Creates a new AdbCrypto object by generating a new key pair.
     * @return A new AdbCrypto object
     * @throws NoSuchAlgorithmException If an RSA key factory cannot be found
     */
    public static AdbCrypto loadAdbKeyPair(File privateKey, File publicKey) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException
    {
        AdbCrypto crypto = new AdbCrypto();

        int privKeyLength = (int)privateKey.length();
        int pubKeyLength = (int)publicKey.length();
        byte[] privKeyBytes = new byte[privKeyLength];
        byte[] pubKeyBytes = new byte[pubKeyLength];

        FileInputStream privIn = new FileInputStream(privateKey);
        FileInputStream pubIn = new FileInputStream(publicKey);

        privIn.read(privKeyBytes);
        pubIn.read(pubKeyBytes);

        privIn.close();
        pubIn.close();

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privKeyBytes);
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(pubKeyBytes);

        crypto.mKeyPair = new KeyPair(keyFactory.generatePublic(publicKeySpec),
                keyFactory.generatePrivate(privateKeySpec));
        return crypto;
    }

}
