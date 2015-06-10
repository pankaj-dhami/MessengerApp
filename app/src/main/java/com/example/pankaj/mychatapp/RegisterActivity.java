package com.example.pankaj.mychatapp;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
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
import com.example.pankaj.mychatapp.Utility.LoadingControl;
import com.example.pankaj.mychatapp.Utility.ProfileQuery;
import com.example.pankaj.mychatapp.WebApiRequest.HttpManager;

import java.util.ArrayList;
import java.util.List;

import static com.example.pankaj.mychatapp.Utility.Validations.isConformPassword;
import static com.example.pankaj.mychatapp.Utility.Validations.isMobileValid;

/**
 * Created by pankaj.dhami on 6/2/2015.
 */
public class RegisterActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>{

    private UserRegisterTask userRegisterTask = null;
    private AutoCompleteTextView mobileTextView;
    private EditText passwordTextView;
    private EditText conformPasswordTextView;
    private View registerFormView;
    private View progressView;
    private TextView signInTextView;
    LoadingControl load;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mobileTextView = (AutoCompleteTextView) findViewById(R.id.mobileNo);
         getLoaderManager().initLoader(0, null, RegisterActivity.this);

        passwordTextView = (EditText) findViewById(R.id.password);
        conformPasswordTextView =(EditText)findViewById(R.id.conformPassword);
        conformPasswordTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_NULL) {
                    registerUser();
                    return true;
                }
                return false;
            }
        });

        Button registterButton = (Button) findViewById(R.id.register_button);
        registterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        registerFormView = findViewById(R.id.register_form);
        progressView = findViewById(R.id.login_progress);
        load=new LoadingControl(registerFormView,progressView);
        //adding underline and link to signup textview
        signInTextView = (TextView) findViewById(R.id.signInTextView);
        signInTextView.setPaintFlags(signInTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        Linkify.addLinks(signInTextView, Linkify.ALL);

        signInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("LoginActivity", "Sign Up Activity activated.");
                // this is where you should start the signin Activity
                Intent intent=new Intent("com.example.pankajdhami.loginactivity.LoginActivity");
                startActivity(intent);
            }
        });
    }

    private void registerUser()
    {
        if (userRegisterTask != null) {
            return;
        }

        mobileTextView.setError(null);
        passwordTextView.setError(null);
       conformPasswordTextView.setError(null);

        String mobile = mobileTextView.getText().toString();
        String password = passwordTextView.getText().toString();
        String conformPassword = conformPasswordTextView.getText().toString();

        boolean cancelLogin = false;
        View focusView = null;

        if (TextUtils.isEmpty(password) ) {
            passwordTextView.setError(getString(R.string.field_required));
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
        if (!isConformPassword(password,conformPassword))
        {
            conformPasswordTextView.setError(getString(R.string.incorrect_conform_password));
            focusView = conformPasswordTextView;
            cancelLogin = true;
        }

        if (cancelLogin) {
            // error in login
            focusView.requestFocus();
        } else {
            // show progress spinner, and start background task to login
            load.showProgress(true);
            userRegisterTask = new UserRegisterTask (mobile, password);
            userRegisterTask.execute((Void) null);
        }
    }

    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final String mobileStr;
        private final String passwordStr;
        AppResultModel result=null;

        UserRegisterTask(String mobile, String password) {
            mobileStr = mobile;
            passwordStr = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //this is where you should write your authentication code
            // or call external service
            // following try-catch just simulates network access
            try {

                result=  HttpManager.registerUser(mobileStr,passwordStr);
                return   result.IsValid;
            } catch (Exception e) {
                return false;
            }

            //using a local dummy credentials store to authenticate
        //    String[] pieces = DUMMY_CREDENTIALS.split(":");
         //   if (pieces[0].equals(emailStr) && pieces[1].equals(passwordStr)) {
          //      return true;
        //    } else {
           //     return false;
         //   }
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            //stop the progress spinner
            load.showProgress(false);

            if (success) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                //  login success and move to main Activity here.
            } else {
                if(result!=null)
                {
               Toast.makeText(RegisterActivity.this, result.RawResponse, Toast.LENGTH_SHORT).show();
                // login failure
                mobileTextView.setError(result.RawResponse);
                mobileTextView.requestFocus();
            }}
            userRegisterTask = null;
        }

        @Override
        protected void onCancelled() {
            userRegisterTask = null;
            load.showProgress(false);
        }
    }


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
                new ArrayAdapter<String>(RegisterActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mobileTextView.setAdapter(adapter);
    }
}
