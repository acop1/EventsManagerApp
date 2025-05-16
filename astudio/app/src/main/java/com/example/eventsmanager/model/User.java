package com.example.eventsmanager.model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * A class to represent a user in the application.
 */
public class User implements Serializable {

    private static final String USERNAME = "username";
    private static final String AUTHKEY = "authkey";
    private String username; // the user's unique name
    private AuthKey authKey; // the authentication key associated with the user

    /**
     * Empty constructor.
     */
    public User() {}

    /**
     * Creates a user with the provided username and password.
     *
     * @param username the user's username.
     * @param password the user's password.
     */
    public User(@NonNull String username, @NonNull String password){
        this.username = username;
        this.authKey = new AuthKey(password);
    }

    /**
     * Tests whether the provided password matches the user's password.
     *
     * @param password the password to validate against the user's password.
     * @return true if the passwords match, false otherwise.
     */
    public boolean validatePassword(@NonNull String password){
        return this.authKey.validatePassword(password);
    }

    /**
     * Returns the user's username.
     *
     * @return the user's username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns a textual representation of the user.
     *
     * @return a textual representation of the user.
     */
    @Override
    @NonNull
    public String toString(){
        return String.format("User %s, authKey: %s", this.username, this.authKey.toString());
    }

    /**
     * Converts user into key-value map with string keys.
     *
     * @return a map representation of the user.
     */
    @NonNull
    public Map<String, Object> toMap() {
        Map<String,Object> map = new HashMap<>();

        map.put(USERNAME, this.username);
        map.put(AUTHKEY, this.authKey.toMap());

        return map;
    }

    /**
     * Builds and returns a user from a key-value map with string keys.
     *
     * @return a user containing the same information as the map.
     */
    @NonNull
    public static User fromMap(@NonNull Map<String,Object> map) {
        final User user = new User();

        user.username = (String)map.get(USERNAME);
        user.authKey = AuthKey.fromMap((Map<String, Object>) map.get(AUTHKEY));

        return user;
    }
}
