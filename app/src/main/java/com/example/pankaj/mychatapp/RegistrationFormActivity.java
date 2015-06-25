package com.example.pankaj.mychatapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pankaj.mychatapp.CustomUI.UserPicture;
import com.example.pankaj.mychatapp.Model.AppResultModel;
import com.example.pankaj.mychatapp.Model.UserModel;
import com.example.pankaj.mychatapp.Utility.Common;
import com.example.pankaj.mychatapp.Utility.HubNotificationService;
import com.example.pankaj.mychatapp.Utility.LoadingControl;
import com.example.pankaj.mychatapp.WebApiRequest.HttpManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static com.example.pankaj.mychatapp.Utility.Validations.isMobileValid;


public class RegistrationFormActivity extends ActionBarActivity {

    // this is the action code we use in our intent,
    // this way we know we're looking at the response from our own action
    private static final int SELECT_SINGLE_PICTURE = 101;

    private static final int SELECT_MULTIPLE_PICTURE = 201;

    public static final String IMAGE_TYPE = "image/*";

    private ImageView selectedImagePreview;
    private UserRegisterTask userRegisterTask = null;
    private AutoCompleteTextView mobileTextView;
    private EditText txtCountryCode;
    private EditText txbName;
    private EditText txbStatus;
    private View registerFormView;
    private View progressView;
    private TextView signInTextView;
    LoadingControl load;
    byte[] imageByteArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_form);

        findViewById(R.id.image_preview).setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                // in onCreate or any event where your want the user to
                // select a file
                Intent intent = new Intent();
                intent.setType(IMAGE_TYPE);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        getString(R.string.select_picture)), SELECT_SINGLE_PICTURE);
            }
        });

        //<editor-fold desc="Description">
        // multiple image selection
      /*  findViewById(R.id.btn_pick_multiple_images).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType(IMAGE_TYPE);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // this line is different here !!
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(intent,
                        getString(R.string.select_picture)), SELECT_MULTIPLE_PICTURE);
            }
        });
*/
        //</editor-fold>

        selectedImagePreview = (ImageView) findViewById(R.id.image_preview);
        Button registterButton = (Button) findViewById(R.id.register_form_button);
        registterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
        mobileTextView = (AutoCompleteTextView) findViewById(R.id.txtmobileNo);
        txtCountryCode = (EditText) findViewById(R.id.txtCountryCode);
        txtCountryCode.setText( "+" + GetCountryZipCode());
        txbName = (EditText) findViewById(R.id.txbName);
        txbStatus = (EditText) findViewById(R.id.txbStatus);

        registerFormView = findViewById(R.id.register_form_scroll);
        progressView = findViewById(R.id.register_progress);
        load = new LoadingControl(registerFormView, progressView);
    }
    public  String GetCountryZipCode() {
        String CountryID = "";
        String CountryZipCode = "";

        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID = manager.getSimCountryIso().toUpperCase();
        String[] rl = this.getResources().getStringArray(R.array.CountryCodes);
        for (int i = 0; i < rl.length; i++) {
            String[] g = rl[i].split(",");
            if (g[1].trim().equals(CountryID.trim())) {
                CountryZipCode += g[0];
                break;
            }
        }
        return CountryZipCode;
    }
    private void registerUser() {
        if (userRegisterTask != null) {
            return;
        }

        mobileTextView.setError(null);

        String mobile = mobileTextView.getText().toString();
        String name = txbName.getText().toString();
        String countryCode = txtCountryCode.getText().toString();
        String stauts = txbStatus.getText().toString();


    /*    Bitmap bitmap = ((BitmapDrawable) selectedImagePreview.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        imageByteArray = stream.toByteArray();*/

        boolean cancelLogin = false;
        View focusView = null;

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
            userRegisterTask = new UserRegisterTask(mobile, name, countryCode, stauts, imageByteArray);
            userRegisterTask.execute((Void) null);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_SINGLE_PICTURE) {

                Uri selectedImageUri = data.getData();
                try {
                    Bitmap bmp = new UserPicture(selectedImageUri, getContentResolver()).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 5, stream);
                   // bmp=Bitmap.createScaledBitmap(bmp,50,50,false);
                    imageByteArray = stream.toByteArray();
                    selectedImagePreview.setImageBitmap(bmp);
                } catch (IOException e) {
                    Log.e(RegisterActivity.class.getSimpleName(), "Failed to load image", e);
                }
                // original code
//                String selectedImagePath = getPath(selectedImageUri);
//                selectedImagePreview.setImageURI(selectedImageUri);
            } else if (requestCode == SELECT_MULTIPLE_PICTURE) {
                //And in the Result handling check for that parameter:
                if (Intent.ACTION_SEND_MULTIPLE.equals(data.getAction())
                        && data.hasExtra(Intent.EXTRA_STREAM)) {
                    // retrieve a collection of selected images
                    ArrayList<Parcelable> list = data.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
                    // iterate over these images
                    if (list != null) {
                        for (Parcelable parcel : list) {
                            Uri uri = (Uri) parcel;
                            // handle the images one by one here
                        }
                    }

                    // for now just show the last picture
                    if (!list.isEmpty()) {
                        Uri imageUri = (Uri) list.get(list.size() - 1);

                        try {
                            selectedImagePreview.setImageBitmap(new UserPicture(imageUri, getContentResolver()).getBitmap());
                        } catch (IOException e) {
                            Log.e(RegisterActivity.class.getSimpleName(), "Failed to load image", e);
                        }
                        // original code
//                        String selectedImagePath = getPath(imageUri);
//                        selectedImagePreview.setImageURI(imageUri);
//                        displayPicture(selectedImagePath, selectedImagePreview);
                    }
                }
            }
        } else {
            // report failure
            Toast.makeText(getApplicationContext(), "No image selected", Toast.LENGTH_LONG).show();
            Log.d(RegistrationFormActivity.class.getSimpleName(), "Failed to get intent data, result code is " + resultCode);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_registration_form, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        UserModel model;
        private String mobile;
        private String name;
        private String countryCode;
        private String stauts;

        AppResultModel result = null;

        UserRegisterTask(String mobile, String name, String countryCode, String stauts, byte[] PicData) {
            model = new UserModel();

            model.MobileNo = countryCode.replace('+',' ').trim() + mobile;
            model.Name = name;
           model.Password="pankaj";
            model.MyStatus = stauts;
            model.Pic64Data = new ArrayList<String>();
            if (PicData!=null && PicData.length>0) {
                String sb = Base64.encodeToString(PicData, Base64.DEFAULT);
                int n = 0;
                for (String str : sb.split("/")) {
                    model.Pic64Data.add(str);
                }
            }
           /* while (n != sb.length()) {
                if (100 < sb.length()-n) {
                    model.Pic64Data.add(sb.substring(n, n + 99));
                    n = n + 100;
                } else {
                    int count = sb.length()-n;
                    model.Pic64Data.add(sb.substring(n, n + count - 1));
                    n=n+count;
                }
            }*/
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //this is where you should write your authentication code
            // or call external service
            // following try-catch just simulates network access
            try {

                result = HttpManager.registerUser(model);
                return result.IsValid;
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
                Toast.makeText(RegistrationFormActivity.this, result.RawResponse, Toast.LENGTH_SHORT).show();
                // startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                //  login success and move to main Activity here.
            } else {
                if (result != null) {
                    Toast.makeText(RegistrationFormActivity.this, result.RawResponse, Toast.LENGTH_SHORT).show();
                    // login failure
                    mobileTextView.setError(result.RawResponse);
                    mobileTextView.requestFocus();
                }
            }
            userRegisterTask = null;
        }

        @Override
        protected void onCancelled() {
            userRegisterTask = null;
            load.showProgress(false);
        }
    }

}
