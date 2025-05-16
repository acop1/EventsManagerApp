package com.example.eventsmanager.view;

public interface LoginUI {

    interface Listener{
        /**
         * Called to indicate the user's desire to register an account.
         *
         * @param username the user-provided username.
         * @param password the user-provided password.
         * @param ui the generator of the event.
         */
        void onRegister(String username, String password, LoginUI ui);

        /**
         * Called to indicate the user's desire to sign onto the system.
         *
         * @param username the user-provided username.
         * @param password the user-provided password.
         * @param ui the generator of the event.
         */
        void onSigninAttempt(String username, String password, LoginUI ui);
    }

    /**
     * Called to convey to the user that account registration was successful.
     */
    void onRegisterSuccess();

    /**
     * Called to convey to the user that the provided credentials are invalid.
     */
    void onInvalidCredentials();

    /**
     * Called to convey to the user that an account with the specified username already exists.
     */
    void onUserAlreadyExists();

    void setListener(final Listener listener);
}
