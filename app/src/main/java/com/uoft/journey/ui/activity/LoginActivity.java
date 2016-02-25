package com.uoft.journey.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import com.baasbox.android.RequestToken;
import com.uoft.journey.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by jenna on 16-01-28.
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private final static String SIGNUP_TOKEN_KEY = "signup_token_key";
    public static final String EXTRA_USERNAME = "com.baasbox.deardiary.username.EXTRA";
    private RequestToken mSignupOrLogin;


    @Bind(R.id.input_email)
    EditText _emailText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.btn_login)
    Button _loginButton;
    @Bind(R.id.link_signup)
    Button _signupLink;
    ProgressDialog _auth;
    boolean _newUser;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (savedInstanceState!=null){
            mSignupOrLogin = savedInstanceState.getParcelable(SIGNUP_TOKEN_KEY);
        }

        ButterKnife.bind(this);
        _auth = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme);;
        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login(false);
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
               /* Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);*/
                login(true);

            }
        });
    }

    public void login(boolean newUser) {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed(newUser);
            return;
        }

        _loginButton.setEnabled(false);


        _auth.setIndeterminate(true);
        _auth.setMessage("Authenticating...");
        _auth.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.

        signupWithBaasBox(newUser, email, password);


        /*new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        //onLoginSuccess();
                        // onLoginFailed();
                        _auth.dismiss();
                    }
                }, 3000);*/
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mSignupOrLogin!=null){
            outState.putParcelable(SIGNUP_TOKEN_KEY,mSignupOrLogin);
        }
    }

    private void signupWithBaasBox(boolean newUser, String email, String password) {

        BaasUser user = BaasUser.withUserName(email);
        user.setPassword(password);
        _newUser = newUser;

        if (newUser) {
            mSignupOrLogin=user.signup(onComplete);
        } else {
            mSignupOrLogin=user.login(onComplete);
        }
    }

    private final BaasHandler<BaasUser> onComplete =
            new BaasHandler<BaasUser>() {
                @Override
                public void handle(BaasResult<BaasUser> result) {

                    mSignupOrLogin = null;
                    if (result.isFailed()){
                        Log.d("ERROR","ERROR",result.error());
                    }
                    completeLogin(result.isSuccess());
                }
            };

    private void completeLogin(boolean success){
        mSignupOrLogin = null;
        if (success) {
           /* Intent intent = new Intent(this,MeasureActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();*/
            onLoginSuccess(_newUser);

        } else {
            onLoginFailed(_newUser);

        }
    }


    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }*/

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess(boolean newUser) {
        _loginButton.setEnabled(true);
        if (newUser){
            Toast.makeText(getBaseContext(), "Signup Successful", Toast.LENGTH_LONG).show();

        }
        else {
            Toast.makeText(getBaseContext(), "Login Successful", Toast.LENGTH_LONG).show();
        }
        Intent intent = new Intent(LoginActivity.this, PatientMainActivity.class);
        _auth.dismiss();

        startActivity(intent);
    }

    public void onLoginFailed(boolean newUser) {
        if (newUser){
            Toast.makeText(getBaseContext(), "Signup Failed", Toast.LENGTH_LONG).show();

        }
        else {
            Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        }
        _auth.dismiss();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mSignupOrLogin!=null){
            mSignupOrLogin.suspend();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSignupOrLogin!=null){
            mSignupOrLogin.resume(onComplete);
        }
    }




}
