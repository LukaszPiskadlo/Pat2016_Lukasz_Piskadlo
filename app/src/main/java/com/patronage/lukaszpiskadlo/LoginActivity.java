package com.patronage.lukaszpiskadlo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private EditText emailField;
    private EditText passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailField = (EditText) findViewById(R.id.input_email);
        passwordField = (EditText) findViewById(R.id.input_password);
        Button loginButton = (Button) findViewById(R.id.button_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        boolean isValid = true;

        // reset errors
        emailField.setError(null);
        passwordField.setError(null);

        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        // check email
        if(email.isEmpty()) {
            emailField.setError(getString(R.string.error_field_required));
            isValid = false;
        } else if(!isEmailValid(email)) {
            emailField.setError(getString(R.string.error_invalid_email));
            isValid = false;
        }

        // check password
        if(password.isEmpty()) {
            passwordField.setError(getString(R.string.error_field_required));
            isValid = false;
        } else if(!isPasswordValid(password)) {
            passwordField.setError(getString(R.string.error_invalid_password));
            isValid = false;
        }

        if(isValid) {
            // storing logged in status in SharedPreferences
            SharedPreferences settings = getSharedPreferences(getString(R.string.preferences), 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(getString(R.string.key_logged_in), true);
            editor.commit();

            startMainActivity();
        }
    }

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        /**
         * password pattern
         * (?=.*\d) - must have digit
         * (?=.*[a-z]) - must have lowercase letter
         * (?=.*[A-Z]) - must have uppercase letter
         * .{8,} - at least 8 characters
         */
        final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,})";

        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);

        return matcher.matches();
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
