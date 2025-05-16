package com.example.eventsmanager.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventsmanager.R;
import com.example.eventsmanager.databinding.FragmentLoginBinding;
import com.google.android.material.snackbar.Snackbar;

//import edu.vassar.cmpu203.nextgenpos.R;
//import edu.vassar.cmpu203.nextgenpos.databinding.FragmentAuthBinding;

/**
 * Implements AuthUI interface using an Android fragment.
 */
public class Login extends Fragment implements LoginUI {

    private static final String IS_REGISTERED = "isRegistered";

    private Listener listener;
    private FragmentLoginBinding binding;
    private boolean isRegistered = false;

    /**
     * OnCreateView() overrides method of the same name from superclass. It's purpose is to
     * inflate the xml layout associated with the fragment.
     * @param inflater object to use to inflate the xml layout (create actual graphical widgets out of the xml declarations)
     * @param container where the graphical widgets will be placed
     * @param savedInstanceState any saved state information to be restored (null if none exists)
     * @return the root of the layout that has just been inflated
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.binding = FragmentLoginBinding.inflate(inflater);
        return this.binding.getRoot();
    }


    /**
     * OnViewCreated() overrides method of the same name from superclass. It is called by the
     * android platform after the layout has been inflated, and before the view transitions to the
     * created state.
     *
     * @param view the layout's root view
     * @param savedInstanceState any saved state information to be restored (null if none exists)
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.getBoolean(IS_REGISTERED))
            activateRegisteredConfig();

        this.binding.registerButton.setOnClickListener( (clickedView) ->{
            String username = this.binding.usernameEditText.getText().toString();
            String password = this.binding.passwordEditText.getText().toString();
            if (Login.this.listener != null)
                Login.this.listener.onRegister(username, password,Login.this);
        });

        this.binding.signinButton.setOnClickListener( (clickedView) ->{
            String username = this.binding.usernameEditText.getText().toString();
            String password = this.binding.passwordEditText.getText().toString();
            if (Login.this.listener != null)
                Login.this.listener.onSigninAttempt(username, password, Login.this);
        });
    }

    /**
     * Saves state information between fragment reconstructions.
     *
     * @param outState Bundle in which to place your saved state.
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_REGISTERED, this.isRegistered);
    }

    /**
     * Called to convey to the user that account registration was successful.
     */
    @Override
    public void onRegisterSuccess() {
        activateRegisteredConfig();
        this.displayMessage(R.string.registration_success_msg);
    }

    /**
     * Helper method to disable registration button.
     */
    private void activateRegisteredConfig(){
        this.isRegistered = true;
        this.binding.registerButton.setEnabled(false);
    }

    /**
     * Called to convey to the user that the provided credentials are invalid.
     */
    @Override
    public void onInvalidCredentials() {
        displayMessage(R.string.invalid_credentials_msg);
    }

    /**
     * Called to convey to the user that an account with the specified username already exists.
     */
    @Override
    public void onUserAlreadyExists() {
        displayMessage(R.string.user_already_exists_msg);
    }

    /**
     * Helper method that displays a snackbar message.
     *
     * @param msgRid message resource it to be displayed.
     */
    private void displayMessage(int msgRid){
        Snackbar.make(this.binding.getRoot(),
                        msgRid,
                        Snackbar.LENGTH_LONG)
                .show();
    }

    /**
     * Sets the listener object to be notified of events of interest originating in the user interface.
     *
     * @param listener the listener object.
     */
    @Override
    public void setListener(final Listener listener) {
        this.listener = listener;
    }
}