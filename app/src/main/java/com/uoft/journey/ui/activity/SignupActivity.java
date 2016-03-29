package com.uoft.journey.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baasbox.android.BaasException;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import com.baasbox.android.RequestToken;
import com.baasbox.android.json.JsonObject;
import com.uoft.journey.Journey;
import com.uoft.journey.R;
import com.uoft.journey.data.AddPatient;
import com.uoft.journey.data.LocalDatabaseAccess;
import com.uoft.journey.ui.adapter.PatientListAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by jenna on 16-01-28.
 */
public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";
    private final static String SIGNUP_TOKEN_KEY = "signup_token_key";
    private RequestToken mSignupOrLogin;


    @Bind(R.id.input_name)
    EditText _nameText;
    @Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.btn_signup)
    Button _signupButton;
    @Bind(R.id.link_login)
    TextView _loginLink;
    @Bind(R.id.clinician)
    CheckBox _clinician;
    Journey mApp;
    BaasUser currUser;
    Boolean exisitingUserAdd;
   // PatientListAdapter patientlist;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mApp = ((Journey)getApplicationContext());
        Intent intent = getIntent();
        String ans = intent.getStringExtra("clinician");

        ButterKnife.bind(this);

        if (ans.equals("yes")){
            exisitingUserAdd = true;
            _clinician.setVisibility(View.GONE);
            _loginLink.setVisibility(View.GONE);
        }
        else{
            exisitingUserAdd = false;
        }

        if (savedInstanceState!=null){
            mSignupOrLogin = savedInstanceState.getParcelable(SIGNUP_TOKEN_KEY);
        }

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        Boolean clinician = _clinician.isChecked();


        // TODO: Implement your own signup logic here.
        signupWithBaasBox(name, email, password, clinician);


    }

    private void signupWithBaasBox(String name, String email, String password, Boolean clinician) {

        BaasUser user = BaasUser.withUserName(email);
        user.setPassword(password);

        user.getScope(BaasUser.Scope.PUBLIC).put("actualname", name);

        if(clinician) {
            user.getScope(BaasUser.Scope.PUBLIC).put("clinician", "yes");
            //user.hasRole("clinician");
            if (!exisitingUserAdd)
                mApp.setType("clinician");
        }
        else {
           // user.hasRole("patient");
            user.getScope(BaasUser.Scope.PUBLIC).put("clinician", "no");
            if (!exisitingUserAdd)
                mApp.setType("patient");
        }
        mSignupOrLogin=user.signup(onComplete);

    }

    private final BaasHandler<BaasUser> onComplete =
            new BaasHandler<BaasUser>() {
                @Override
                public void handle(BaasResult<BaasUser> result) {

                    mSignupOrLogin = null;
                    String date = null;
                    if (result.isFailed()){
                        Log.d("ERROR","ERROR",result.error());
                    }

                    try {
                        currUser = result.get();
                        date = currUser.getSignupDate();
                    } catch (BaasException e) {
                        e.printStackTrace();
                    }
                    completeLogin(result.isSuccess(), date);
                }
            };

    private void completeLogin(boolean success, String date){
        mSignupOrLogin = null;
        if (success) {
           /* Intent intent = new Intent(this,MeasureActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();*/

            if(!exisitingUserAdd) {
                mApp.setUsername(_emailText.getText().toString());
                mApp.setPassword(_passwordText.getText().toString());
                if (mApp.getType().equals("clinician")) {
                    mApp.setType("clinician");
                } else {
                    mApp.setType("patient");
                }
                onSignupSuccess(date);

            }else{
                //BaasUser.current().logout(onCompleteLogout);
                loginWithBaasBox();
            }

        } else {
            onSignupFailed();

        }
    }

    private final BaasHandler<Void> onCompleteLogout =
            new BaasHandler<Void>() {
                @Override
                public void handle(BaasResult<Void> result) {

                    mSignupOrLogin = null;
                    if (result.isFailed()){
                        Log.d("ERROR","ERROR",result.error());
                    }
                }
            };

    private void loginWithBaasBox() {

        BaasUser user = BaasUser.withUserName(mApp.getUsername());
        user.setPassword(mApp.getPassword());

        mSignupOrLogin=user.login(onComplete2);
    }

    private final BaasHandler<BaasUser> onComplete2 =
            new BaasHandler<BaasUser>() {
                @Override
                public void handle(BaasResult<BaasUser> result) {
                    String date = null;
                    mSignupOrLogin = null;
                    if (result.isFailed()){
                        Log.d("ERROR","ERROR",result.error());
                    }

                    try {
                        currUser = result.get();
                        date = currUser.getSignupDate();
                    } catch (BaasException e) {
                        e.printStackTrace();
                    }
                    onSignupSuccess(date);

                }
            };

    public void onSignupSuccess(String date) {

        if(!exisitingUserAdd) {

            Toast.makeText(getBaseContext(), "Signup Successful ".concat(mApp.getType()), Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getBaseContext(), "Signup Successful Patient", Toast.LENGTH_LONG).show();
        }
        // Intent intent = new Intent(LoginActivity.this, PatientMainActivity.class);
        Intent intent;



        if (mApp.getType().equals("clinician") || exisitingUserAdd) {

          /* if(exisitingUserAdd){
                AddPatient task = new AddPatient(getApplicationContext(), null, _emailText.getText().toString());
                task.execute();
            }*/
            intent = new Intent(SignupActivity.this, PatientListActivity.class);
            intent.putExtra("newpatient",_emailText.getText().toString());

        }
        else{
            intent = new Intent(SignupActivity.this, PatientMainActivity.class);
            LocalDatabaseAccess.addUser(getApplicationContext(), mApp.getUsername(), _nameText.getText().toString(), date);
            intent.putExtra("patient", mApp.getUsername());
        }

        // Prevent navigation back to here
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);

    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

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
}
