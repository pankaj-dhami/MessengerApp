package com.example.pankaj.mychatapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.pankaj.mychatapp.CustomUI.UserPicture;
import com.example.pankaj.mychatapp.Model.AppResultModel;
import com.example.pankaj.mychatapp.Model.UserModel;
import com.example.pankaj.mychatapp.Utility.SqlLiteDb;
import com.example.pankaj.mychatapp.WebApiRequest.HttpManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Edwin on 15/02/2015.
 */
public class Tab2 extends Fragment {
    View view;
    ImageView imgProfilePic;
    ToggleButton btnSavePic;
    Button btnCancelPic;
    Button btnCancelUpdateText;
    ToggleButton btnSaveUpdateText;
    EditText txtUserStatus;
    EditText txtUserName;
    Activity thisActivity;
    Bitmap bitmapOriginal;
    byte[] imageByteArray;
    UserModel thisUser;
    /**
     * Standard activity result: operation canceled.
     */
    public static final int RESULT_CANCELED = 0;
    /**
     * Standard activity result: operation succeeded.
     */
    public static final int RESULT_OK = -1;
    /**
     * Start of user-defined activity results.
     */
    public static final int RESULT_FIRST_USER = 1;

    // this is the action code we use in our intent,
    // this way we know we're looking at the response from our own action
    private static final int SELECT_SINGLE_PICTURE = 101;

    private static final int SELECT_MULTIPLE_PICTURE = 201;

    public static final String IMAGE_TYPE = "image/*";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        thisActivity = getActivity();
        View view = inflater.inflate(R.layout.tab_2, container, false);
        imgProfilePic = (ImageView) view.findViewById(R.id.imgProfilePic);

        btnSavePic = (ToggleButton) view.findViewById(R.id.btnSavePic);
        btnCancelPic = (Button) view.findViewById(R.id.btnCancelPic);
        btnCancelUpdateText = (Button) view.findViewById(R.id.btnCancelUpdateText);
        btnSaveUpdateText = (ToggleButton) view.findViewById(R.id.btnSaveUpdateText);

        txtUserStatus = (EditText) view.findViewById(R.id.txtUserStatus);
        txtUserName = (EditText) view.findViewById(R.id.txtUserName);

        SqlLiteDb entity = new SqlLiteDb(thisActivity);
        entity.open();
        thisUser = entity.getUser();
        Bitmap bmp = null;
        if (thisUser.PicData != null && thisUser.PicData.length > 3) {
            try {
                bmp = BitmapFactory.decodeByteArray(thisUser.PicData, 0, thisUser.PicData.length);

            } catch (Exception e) {
                bmp = ((BitmapDrawable) imgProfilePic.getDrawable()).getBitmap();
            }
        } else {
            bmp = ((BitmapDrawable) imgProfilePic.getDrawable()).getBitmap();
        }
       /* Display display = thisActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        bmp=Bitmap.createScaledBitmap(bmp,width,300,false);*/
        //  bmp= getRoundedCornerBitmap(bmp,50);
        bitmapOriginal = bmp;
        imgProfilePic.setImageBitmap(bmp);
        // scaleImage();
        //imgProfilePic.setAdjustViewBounds(true);
        txtUserName.setText(thisUser.Name);
        txtUserStatus.setText(thisUser.MyStatus);

        txtUserName.setEnabled(false);
        txtUserStatus.setEnabled(false);

        btnSavePic.setChecked(false);
        btnSaveUpdateText.setChecked(false);
        btnCancelPic.setVisibility(View.INVISIBLE);
        btnCancelUpdateText.setVisibility(View.INVISIBLE);

        btnSavePic.setBackgroundResource(R.drawable.ic_edit);
        btnSaveUpdateText.setBackgroundResource(R.drawable.ic_edit);

        btnSavePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnSavePic.isChecked()) {
                    Intent intent = new Intent();
                    intent.setType(IMAGE_TYPE);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent,
                            getString(R.string.select_picture)), SELECT_SINGLE_PICTURE);
                    btnSavePic.setBackgroundResource(R.drawable.ic_tick_mark);
                    btnCancelPic.setVisibility(View.VISIBLE);
                } else {
                    //code to save data
                    UserUpdateTask  userRegisterTask = new UserUpdateTask(thisUser.UserID, "", "", imageByteArray);
                    userRegisterTask.execute((Void) null);
                    btnCancelPic.setVisibility(View.INVISIBLE);
                    btnSavePic.setBackgroundResource(R.drawable.ic_edit);
                }
            }
        });
        btnCancelPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSavePic.setBackgroundResource(R.drawable.ic_edit);
                btnCancelPic.setVisibility(View.INVISIBLE);
                btnSavePic.setChecked(false);
                imgProfilePic.setImageBitmap(bitmapOriginal);
            }
        });

        btnSaveUpdateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnSaveUpdateText.isChecked()) {
                    txtUserName.setEnabled(true);
                    txtUserStatus.setEnabled(true);
                    txtUserName.requestFocus();
                    btnCancelUpdateText.setVisibility(View.VISIBLE);
                    btnSaveUpdateText.setBackgroundResource(R.drawable.ic_tick_mark);
                } else {
                    //code to save data
                    UserUpdateTask  userRegisterTask = new UserUpdateTask(
                            thisUser.UserID, txtUserName.getText().toString() ,txtUserStatus.getText().toString(),null);
                    userRegisterTask.execute((Void) null);

                    txtUserName.setEnabled(false);
                    txtUserStatus.setEnabled(false);
                    btnCancelUpdateText.setVisibility(View.INVISIBLE);
                    btnSaveUpdateText.setBackgroundResource(R.drawable.ic_edit);

                }
            }
        });
        btnCancelUpdateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSaveUpdateText.setBackgroundResource(R.drawable.ic_edit);
                btnCancelUpdateText.setVisibility(View.INVISIBLE);
                txtUserName.setEnabled(false);
                txtUserStatus.setEnabled(false);
                btnSaveUpdateText.setChecked(false);
            }
        });
        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_SINGLE_PICTURE) {

                Uri selectedImageUri = data.getData();
                try {
                    Bitmap bmp = new UserPicture(selectedImageUri, thisActivity.getContentResolver()).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.JPEG, 20, stream);
                   /* Display display = thisActivity.getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    int width = size.x;
                    int height = size.y;
                    bmp=Bitmap.createScaledBitmap(bmp,width,300,false);*/
                    imageByteArray = stream.toByteArray();
                    // imgProfilePic.setAdjustViewBounds(true);
                    // bmp=  getRoundedCornerBitmap(bmp,150);
                    imgProfilePic.setImageBitmap(bmp);
                    // scaleImage();

                } catch (IOException e) {
                    Log.e(RegisterActivity.class.getSimpleName(), "Failed to load image", e);
                }
            }

        } else {
            // report failure
            Toast.makeText(thisActivity.getApplicationContext(), "No image selected", Toast.LENGTH_SHORT).show();
            Log.d(RegistrationFormActivity.class.getSimpleName(), "Failed to get intent data, result code is " + resultCode);
        }
    }

    public class UserUpdateTask extends AsyncTask<Void, Void, Boolean> {

        UserModel model;
        private String mobile;
        int userID;
        private String name;
        private String countryCode;
        private String stauts;

        AppResultModel result = null;

        UserUpdateTask(int userID, String name, String stauts, byte[] PicData) {
            model = new UserModel();
            model.UserID = userID;
            model.Name = name;
            model.MyStatus = stauts;
            model.Pic64Data = new ArrayList<String>();
            if (PicData!=null && PicData.length>0) {
                String sb = Base64.encodeToString(PicData, Base64.DEFAULT);
                int n = 0;
                for (String str : sb.split("/")) {
                    model.Pic64Data.add(str);
                }
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //this is where you should write your authentication code
            // or call external service
            // following try-catch just simulates network access
            try {

                result = HttpManager.registerUser(model,1);
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
           // load.showProgress(false);

            if (success) {
                Toast.makeText(thisActivity, "Profile successfully updated.", Toast.LENGTH_SHORT).show();
            } else {
                if (result != null) {
                    Toast.makeText(thisActivity, "Failed to update.", Toast.LENGTH_SHORT).show();
                }
            }

        }

        @Override
        protected void onCancelled() {
           // userRegisterTask = null;
           // load.showProgress(false);
        }
    }
}