package com.example.eventsmanager.model;


import android.util.Log;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * A class to represent an authentication key.
 */
public class AuthKey implements Serializable {

    private static final String SALT = "salt";
    private static final String KEY = "key";
    private String salt; // the random salt added to the password
    private String key; // the cryptographic hash of the password

    /**
     * Creates an empty authentication key.
     */
    private AuthKey() {}

    /**
     * Creates an authentication key from the provided password, using a randomly-generated salt.
     *
     * @param password the password to create the key from.
     */
    public AuthKey(@NonNull String password) {
        this(AuthKey.generateSalt(), password);
    }

    /**
     * Creates an authentication key from the provided salt and password.
     *
     * @param salt the salt to create the key from.
     * @param password the password to create the key from.
     */
    private AuthKey(@NonNull String salt, @NonNull String password){
        this.salt = salt;
        this.key = AuthKey.generateKey(salt, password);
    }

    /**
     * Tests whether the provided password matches the password used to generate the key
     * the method is being called on.
     *
     * @param password the password to validate against the key's password.
     * @return true if the passwords match, false otherwise.
     */
    public boolean validatePassword(@NonNull String password){
        AuthKey oauthKey = new AuthKey(this.salt, password);
        return this.key.equals(oauthKey.key);
    }

    private static final int SALT_LEN = 20;
    private static final int KEY_LEN = 40;
    private static final int NITERS = 64000;

    /**
     * Generates a random salt string.
     *
     * @return a random salt string.
     */
    @NonNull
    private static String generateSalt() {
        byte[] salt = new byte[SALT_LEN];
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            sr.nextBytes(salt);
        } catch (NoSuchAlgorithmException e) {
            Log.e("NextGenPos", "Error generating authentication salt", e);
        }
        return Base64.getEncoder().encodeToString(salt); // salt string
    }

    /**
     * Generate a cryptographic key from a salt, plaintext combination.
     *
     * @param salt the salt to the added to the password.
     * @param password the plaintext password.
     * @return the resulting cryptographic key.
     */
    private static String generateKey(@NonNull String salt, @NonNull String password) {
        String hashStr = null;
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

            byte[] saltBytes = Base64.getDecoder().decode(salt);
            char[] chars = password.toCharArray();
            PBEKeySpec spec = new PBEKeySpec(chars, saltBytes, NITERS, KEY_LEN * Byte.SIZE);

            byte[] hashBytes = skf.generateSecret(spec).getEncoded();
            hashStr = Base64.getEncoder().encodeToString(hashBytes);

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            Log.e("NextGenPos", "Error generating authentication key", e);
        }
        return hashStr;
    }

    /**
     * Converts AuthKey into key-value map with string keys.
     *
     * @return a map representation of the AuthKey.
     */
    @NonNull
    public Map<String, Object> toMap() {
        Map<String,Object> map = new HashMap<>();

        map.put(SALT, this.salt);
        map.put(KEY, this.key);

        return map;
    }

    /**
     * Builds and returns an AuthKey from a key-value map with string keys.
     *
     * @return an AuthKey containing the same information as the map.
     */
    @NonNull
    public static AuthKey fromMap(@NonNull Map<String,Object> map) {
        final AuthKey authKey = new AuthKey();

        authKey.salt = (String)map.get(SALT);
        authKey.key = (String)map.get(KEY);

        return authKey;
    }
}
