package com.example.pankaj.mychatapp.Utility;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;

import com.example.pankaj.mychatapp.Model.TelephoneNumberModel;
import com.example.pankaj.mychatapp.Model.UserModel;
import com.example.pankaj.mychatapp.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by pankaj.dhami on 6/15/2015.
 */
public class Common {

    Context context;
    public Common(Context context) {
        this.context=context;
    }

    public  ArrayList<TelephoneNumberModel> fetchContacts() {

        String phoneNumber = null;
        String email = null;
        ArrayList<TelephoneNumberModel> friendList=new ArrayList<TelephoneNumberModel>();

        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

        Uri EmailCONTENT_URI =  ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
        String DATA = ContactsContract.CommonDataKinds.Email.DATA;

        StringBuffer output = new StringBuffer();

        ContentResolver contentResolver = context.getContentResolver();

        Cursor cursor = contentResolver.query(CONTENT_URI, null,null, null, null);

        // Loop for every contact in the phone
        if (cursor.getCount() > 0) {

            while (cursor.moveToNext()) {

                String contact_id = cursor.getString(cursor.getColumnIndex( _ID ));
                String name = cursor.getString(cursor.getColumnIndex( DISPLAY_NAME ));

                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex( HAS_PHONE_NUMBER )));

                if (hasPhoneNumber > 0) {

                    output.append("\n First Name:" + name);

                    // Query and loop for every phone number of the contact
                    Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[] { contact_id }, null);

                    while (phoneCursor.moveToNext()) {
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                        output.append("\n Phone number:" + phoneNumber);
                        TelephoneNumberModel userModel=new TelephoneNumberModel();
                        userModel.Name=name;
                        if(phoneNumber.startsWith("0"))
                        {
                            phoneNumber= GetCountryZipCode()+ phoneNumber.replace( phoneNumber.charAt(0) ,' ' ).trim();
                        }
                        else if (phoneNumber.startsWith("+"))
                        {
                            phoneNumber= phoneNumber.replace( phoneNumber.charAt(0) ,' ' ).trim();
                        }
                        userModel.MobileNo=phoneNumber;
                        friendList.add(userModel);
                    }

                    phoneCursor.close();

                    // Query and loop for every email of the contact
                      /*  Cursor emailCursor = contentResolver.query(EmailCONTENT_URI,	null, EmailCONTACT_ID+ " = ?", new String[] { contact_id }, null);

                        while (emailCursor.moveToNext()) {

                            email = emailCursor.getString(emailCursor.getColumnIndex(DATA));

                            output.append("\nEmail:" + email);

                        }

                        emailCursor.close();*/
                }

                output.append("\n");
            }
        }
        return  friendList;
    }

    public static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getTime(String dateInString)
    {
        String time="";
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date parsedDate = formatter.parse(dateInString);
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "hh:mm a", Locale.getDefault());

            time= DateFormat.getDateTimeInstance().format(parsedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  time;
    }

    public static String GetCountryZipCode() {
        String CountryID = "";
        String CountryZipCode = "";

        TelephonyManager manager = (TelephonyManager) HubNotificationService.thisServiceContext.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID = manager.getSimCountryIso().toUpperCase();
        String[] rl = HubNotificationService.thisServiceContext.getResources().getStringArray(R.array.CountryCodes);
        for (int i = 0; i < rl.length; i++) {
            String[] g = rl[i].split(",");
            if (g[1].trim().equals(CountryID.trim())) {
                CountryZipCode += g[0];
                break;
            }
        }
        return CountryZipCode;
    }
}
