package com.example.pankaj.mychatapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.pankaj.mychatapp.CustomUI.UserPicture;
import com.example.pankaj.mychatapp.Model.UserModel;
import com.example.pankaj.mychatapp.Utility.SqlLiteDb;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Edwin on 15/02/2015.
 */
public class Tab2 extends Fragment {
    View view;
    ImageView imgProfilePic;
    ToggleButton imgSavePic;
    ToggleButton imgSaveName;
    ToggleButton imgSaveUserStatus;
    EditText txtUserStatus;
    EditText txtUserName;
    Activity thisActivity;
    byte [] imageByteArray;
    /** Standard activity result: operation canceled. */
    public static final int RESULT_CANCELED    = 0;
    /** Standard activity result: operation succeeded. */
    public static final int RESULT_OK           = -1;
    /** Start of user-defined activity results. */
    public static final int RESULT_FIRST_USER   = 1;

    // this is the action code we use in our intent,
    // this way we know we're looking at the response from our own action
    private static final int SELECT_SINGLE_PICTURE = 101;

    private static final int SELECT_MULTIPLE_PICTURE = 201;

    public static final String IMAGE_TYPE = "image/*";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        thisActivity=getActivity();
        View view = inflater.inflate(R.layout.tab_2,container,false);
        imgProfilePic= (ImageView)view.findViewById(R.id.imgProfilePic);
        imgSavePic= (ToggleButton)view.findViewById(R.id.imgSavePic);
        imgSaveName= (ToggleButton)view.findViewById(R.id.imgSaveName);
        imgSaveUserStatus= (ToggleButton)view.findViewById(R.id.imgSaveUserStatus);
        txtUserStatus= (EditText)view.findViewById(R.id.txtUserStatus);
        txtUserName= (EditText)view.findViewById(R.id.txtUserName);

        SqlLiteDb entity=new SqlLiteDb(thisActivity);
        entity.open();
        UserModel user= entity.getUser();
        Bitmap bmp=null;
        if (user.PicData!=null && user.PicData.length >3) {
            try {
                bmp = BitmapFactory.decodeByteArray(user.PicData, 0, user.PicData.length);

            } catch (Exception e) {
                bmp=  ((BitmapDrawable) imgProfilePic.getDrawable()).getBitmap();
            }
        }
        else {
            bmp=  ((BitmapDrawable) imgProfilePic.getDrawable()).getBitmap();
        }
       /* Display display = thisActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        bmp=Bitmap.createScaledBitmap(bmp,width,250,false);*/
        imgProfilePic.setImageBitmap(bmp);

        txtUserName.setText(user.Name);
        txtUserStatus.setText(user.MyStatus);

        txtUserName.setEnabled(false);
        txtUserStatus.setEnabled(false);

        imgSavePic.setChecked(false);
        imgSaveName.setChecked(false);
        imgSaveUserStatus.setChecked(false);

        imgSavePic.setBackgroundResource(R.drawable.ic_edit);
        imgSaveName.setBackgroundResource(R.drawable.ic_edit);
        imgSaveUserStatus.setBackgroundResource(R.drawable.ic_edit);

        imgSavePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!imgSavePic.isChecked()) {
                    imgSavePic.setBackgroundResource(R.drawable.ic_save_check);
                    Intent intent = new Intent();
                    intent.setType(IMAGE_TYPE);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent,
                            getString(R.string.select_picture)), SELECT_SINGLE_PICTURE);
                } else {
                    //code to save data
                    imgSavePic.setBackgroundResource(R.drawable.ic_edit);
                }
            }
        });

        imgSaveName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!imgSaveName.isChecked()) {
                    txtUserName.setEnabled(true);
                    txtUserName.requestFocus();
                    imgSaveName.setBackgroundResource(R.drawable.ic_save_check);
                }
                else {
                    //code to save data
                    txtUserName.setEnabled(false);
                    imgSaveName.setBackgroundResource(R.drawable.ic_edit);
                }
            }
        });
        imgSaveUserStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!imgSaveUserStatus.isChecked()) {
                    txtUserStatus.setEnabled(true);
                    txtUserStatus.requestFocus();
                    imgSaveUserStatus.setBackgroundResource(R.drawable.ic_save_check);
                }
                else {
                    //code to save data
                    txtUserStatus.setEnabled(false);
                    imgSaveUserStatus.setBackgroundResource(R.drawable.ic_edit);
                }
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
                  /*  Display display = thisActivity.getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    int width = size.x;
                    int height = size.y;
                    bmp=Bitmap.createScaledBitmap(bmp,width,250,false);*/
                    imageByteArray = stream.toByteArray();
                    imgProfilePic.setImageBitmap(bmp);
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

}