package com.example.pankaj.mychatapp.Utility;

import android.provider.ContactsContract;

/**
 * Created by pankaj.dhami on 6/2/2015.
 */
public interface ProfileQuery {
    String[] PROJECTION = {
            ContactsContract.CommonDataKinds.Email.ADDRESS,
            ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
    };

    int ADDRESS = 0;
    int IS_PRIMARY = 1;
}