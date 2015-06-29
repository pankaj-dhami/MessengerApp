package com.example.pankaj.mychatapp;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pankaj.mychatapp.Model.AppResultModel;
import com.example.pankaj.mychatapp.Utility.ApplicationConstants;
import com.example.pankaj.mychatapp.Utility.HubNotificationService;
import com.example.pankaj.mychatapp.Utility.LoadingControl;
import com.example.pankaj.mychatapp.Utility.MyService;
import com.example.pankaj.mychatapp.Utility.ProfileQuery;
import com.example.pankaj.mychatapp.Utility.SqlLiteDb;
import com.example.pankaj.mychatapp.WebApiRequest.HttpManager;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import static com.example.pankaj.mychatapp.Utility.Validations.isMobileValid;
import static com.example.pankaj.mychatapp.Utility.Validations.isPasswordValid;


/**
 * Created by pankaj.dhami on 6/2/2015.
 */


public class LoginActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String DUMMY_CREDENTIALS = "user@test.com:hello";
    private UserLoginTask userLoginTask = null;
    private View loginFormView;
    private View progressView;
    private AutoCompleteTextView mobileTextView;
    private EditText passwordTextView;
    private TextView signUpTextView;
    LoadingControl load;
    private TextView textView;
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String str = bundle.getString("text");
                showMsg(str);

            }
        }
    };

    public void showMsg(String str) {
        mobileTextView.setError(null);
        mobileTextView.setError(str);
        Toast.makeText(LoginActivity.this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter("com.example.pankaj.mychatapp"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mobileTextView = (AutoCompleteTextView) findViewById(R.id.loginMobileNo);
        getLoaderManager().initLoader(0, null, LoginActivity.this);

        passwordTextView = (EditText) findViewById(R.id.password);
        passwordTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_NULL) {
                    initLogin();
                    return true;
                }
                return false;
            }
        });

        Button loginButton = (Button) findViewById(R.id.email_sign_in_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initLogin();
            }
        });

        loginFormView = findViewById(R.id.login_form);
        progressView = findViewById(R.id.login_progress);
        load = new LoadingControl(loginFormView, progressView);
        //adding underline and link to signup textview
        signUpTextView = (TextView) findViewById(R.id.signUpTextView);
        signUpTextView.setPaintFlags(signUpTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        Linkify.addLinks(signUpTextView, Linkify.ALL);

        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("LoginActivity", "Sign Up Activity activated.");
                // this is where you should start the signup Activity
                // LoginActivity.this.startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                  LoginActivity.this.startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                 Intent intent = new Intent(LoginActivity.this, MyService.class);
                //intent.putExtra("message","pankaj");
                 LoginActivity.this.stopService(intent);
             //   startActivity(new Intent(LoginActivity.this,ChatBubbleActivity.class));
             //   startActivity(new Intent(LoginActivity.this,HomeActivity.class));
             //   startActivity(new Intent("com.example.pankaj.mychatapp.ChatBubbleActivity"));
            }
        });
    }

    public void initLogin() {
        if (userLoginTask != null) {
            return;
        }

        mobileTextView.setError(null);
        passwordTextView.setError(null);

        String mobile = mobileTextView.getText().toString();
        String password = passwordTextView.getText().toString();

        boolean cancelLogin = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            passwordTextView.setError(getString(R.string.invalid_password));
            focusView = passwordTextView;
            cancelLogin = true;
        }

        if (TextUtils.isEmpty(mobile)) {
            mobileTextView.setError(getString(R.string.field_required));
            focusView = mobileTextView;
            cancelLogin = true;
        } else if (!isMobileValid(mobile)) {
            mobileTextView.setError(getString(R.string.invalid_mobile));
            focusView = mobileTextView;
            cancelLogin = true;
        }

        if (cancelLogin) {
            // error in login
            focusView.requestFocus();
        } else {
            // show progress spinner, and start background task to login
            load.showProgress(true);
            userLoginTask = new UserLoginTask(mobile, password);
            userLoginTask.execute((Void) null);
        }
    }


    /**
     * Shows the progress UI and hides the login form.
     */


    /*interface LoaderCallbacks implementation*/
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mobileTextView.setAdapter(adapter);
    }


    /**
     * Async Login Task to authenticate
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mobileStr;
        private final String passwordStr;
        AppResultModel result = null;

        UserLoginTask(String mobile, String password) {
            mobileStr = mobile;
            passwordStr = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //this is where you should write your authentication code
            // or call external service
            // following try-catch just simulates network access
            try {
                HttpManager manager=new HttpManager(LoginActivity.this);
                result = manager.getToken(mobileStr, passwordStr);
                return result.IsValid;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            //stop the progress spinner
            load.showProgress(false);

            if (success) {
              //  saveToFile(ApplicationConstants.token_fileName, ApplicationConstants.getAccess_token());
                //  login success and move to main Activity here.

                SqlLiteDb entity=new SqlLiteDb(LoginActivity.this);
                entity.open();
                ApplicationConstants.thisUser.Name=ApplicationConstants.thisUser.MobileNo;
                entity.updateUser(ApplicationConstants.thisUser);
                entity.close();
                startActivity(new Intent(LoginActivity.this, HomeDash.class));
                Intent intent = new Intent(LoginActivity.this, MyService.class);
               // intent.putExtra("msg","start");
                LoginActivity.this.startService(intent);
                LoginActivity.this.startService(new Intent(LoginActivity.this, HubNotificationService.class));

            } else {
                if (result != null) {
                    // login failure
                    Toast.makeText(LoginActivity.this, result.RawResponse, Toast.LENGTH_SHORT).show();
                    // passwordTextView.setError(getString(R.string.incorrect_password));
                    // passwordTextView.requestFocus();
                }
            }
            userLoginTask = null;
        }

        public void saveToFile(String fileName, String text) {
            try {
                FileOutputStream fos = openFileOutput(fileName, MODE_PRIVATE);
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
                bw.write(text);
            } catch (Exception ex) {

            }
        }
        //sfsdfsdf
        //

        @Override
        protected void onCancelled() {
            userLoginTask = null;
            load.showProgress(false);
        }
    }
}
