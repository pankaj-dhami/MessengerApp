package com.example.pankaj.mychatapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.pankaj.mychatapp.Model.UserModel;
import com.example.pankaj.mychatapp.Utility.HubNotificationService;
import com.example.pankaj.mychatapp.Utility.MyService;
import com.example.pankaj.mychatapp.Utility.SqlLiteDb;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edwin on 15/02/2015.
 */
public class Tab1 extends Fragment {

    private UserModelAdapter userArrayAdapter;
    private ListView listView;
    private static int colorIndex;
    Activity thisActivity;
    SqlLiteDb entity;
    StringBuilder sb=new StringBuilder("iVBORw0KGgoAAAANSUhEUgAAAHgAAAB4CAMAAAAOusbgAAAAkFBMVEX///+kxjmkxjr8/febwSO+1ny61HKRugCcwiHT5KGbwSewzVnY5bG10V74+/Hc6beyzmCgxDH8/fWty0/q8tTu9NqSuwCVvRP0+Of3+u2GtADp8dTl7sq10GmbwC/x9uHL3ZTI3YyhxD/Z56zB14Tj7cCsy0zK3Je30WTG2o/F24TR4qXi7sd8rQCVvECry0SSP5/vAAAEVUlEQVRoge2b63aqOhRGIQShAYUAEkRRRKW29mzf/+12wk2wpiiJdYx9+P60CWkmK5fFyqWKMoqjeXBfOceTDM7+zO8p9vbfSTL4M9a1/lLzNVpJBi+y8I4qt/FWMldRdobfWyZYhzPp4MiKv3qKaHqcSufS8UryHnO+4lz2mGbSEqMesVo0X60Otm07h1WwWdQlgOE8gUt7MPY39If3ddq7GUZmIQT8JLWLSe7EyaKnioGaxicl2FprE2IqtRD9BUEIkp2n+U8ymLYwRlZoYBUAtSOahgb2jc9ncZ0cwW/Umk0N1zdP4c51gm9TKzRA2fsd3u1R2YBay8VWHU6sSDJWOxq4D8ushsSWyo1co9fcEo3Ju0TuxoI/9O41eS+N6+XwTiwjq0YqC2yWXMC8Bf8NmE8py0kiexNScaHlcskA5UlWjnugIiku7EQqVnjaREvzNhlAfb4IEli9BJDgSpaVkQBOWdIht9s5Z9+HAJQeHBBLmDuzEKgqe2PpBcI3DZ4UD93qIYiFYz47bOo+sjTPYp9ZvMxqMM5EXRhGTZ+S3UJbcUYXQPpGCybo0gSCfsQ2Gg4d1T5/VKsoc0FrumNfaHxpFmqB6KePQ60etl9KzGkfUNfAHx1Y5yFArkik+/6Ar7xuAFOgraPk1ty5k1xOv2EKVAEwFHAiX1AAjNFw8MkczKUKB/sQbT94bDGTw761FldegljwOFTG4Jk88wkUUDx4WHv7ZCIgd3g4oHmeNlj0bx9BOUcVnfWgBg/nUnLVbo6l4vPnz3s30SQkCCFCPtjbeh8TXUDJgVW5dEOIECTop9AgcgkbxnTyGx80OcvFBldK69jkpJwZZZWcdt6TKm4HgDhF2CMynUI2qv06VKTxNnfbaB42n3mA6fJrZiERB2JQ8CG+BBJ8570lLQ5ayQDrrSrBmrcteWx7SNORAT62agCQN7GnHbAtAay57RpeBuaubEbwCB7BI3gEj+ARPIJH8AgewSN4BDfiLtpetkx9+sKctxfxsq2IG5svouD7Nl++bTdJAF+2m0jK5dYbbJCYqSYJrMwTQno32GhrH45nnH8ui4QUsKI5UxWft3ddB6skBzxAI/jXwF4idBgy/ExC7PgHDz/+UYL1i07alG14HsrF4SMz91raJMT1XvlVxeBmdlMYcb+Bd5L3yESFcPt8GABUq3OEDnCVS/zBx2y1Vqmb+1R517Iij6nbF6Ao6k92Ei6CaLNCqwy3DDOr3NnMaY18AI9lvsxbkkvQBdf5dgc8lUisFHTBjU2/ClZfBX6Zxf8/8NjH/z547ON/H/yrfXz1WWzyr77H8sGRi1tr7LzJD3DrhUQuj3H11tpVCHdN9uIS9dOI6xm36KOsXtDQkLl1EfBA6jAQhDIvdV80j2EZu5Lu9YY0xtU9qkT+v+AU2liQxrommVw16C4jkK350+dgFXZf6F3X0++RerT70Pdvsv/F6orNiVwfukP1Sv0Fz/tveV13LiUAAAAASUVORK5CYII=");
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String code = bundle.getString("code");
                if (TextUtils.equals(code, "friendsList")) {
                    userArrayAdapter.clear();
                    entity.open();
                    MyService.FriendsList= entity.getFriendsList();
                    entity.close();
                    for (UserModel model : MyService.FriendsList) {

                        model.MyStatus = model.MobileNo +"/"+ model.UserID;
                        byte[] decodedString = Base64.decode(sb.toString(), Base64.DEFAULT);
                        model.PicData = decodedString;
                        userArrayAdapter.add(model);
                    }
                }
            }
        }
    };


    @Override
    public void onResume() {
        super.onResume();
        MyService.Tab1Activity_active = true;
        thisActivity.registerReceiver(receiver, new IntentFilter("com.example.pankaj.mychatapp"));
    }

    @Override
    public void onPause() {
        super.onPause();
        MyService.Tab1Activity_active = false;
        thisActivity.unregisterReceiver(receiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        colorIndex = 0;
        thisActivity = getActivity();
        View v = inflater.inflate(R.layout.tab_1, container, false);
        listView = (ListView) v.findViewById(R.id.listView);
        userArrayAdapter = new UserModelAdapter(getActivity(), R.layout.list_row);
        listView.setAdapter(userArrayAdapter);
        entity=new SqlLiteDb(thisActivity);
        entity.open();
        MyService.FriendsList= entity.getFriendsList();
        entity.close();
        userArrayAdapter.clear();
        for (UserModel model : MyService.FriendsList) {
          //  String fruitImg = "orange";
          //  int fruitImgResId = getResources().getIdentifier(fruitImg, "drawable", "com.example.pankaj.mychatapp");
            model.MyStatus = model.MobileNo +"/"+ model.UserID;
            byte[] decodedString = Base64.decode(sb.toString(), Base64.DEFAULT);
            model.PicData = decodedString;
            userArrayAdapter.add(model);
        }
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final UserModel item = (UserModel) parent.getItemAtPosition(position);
                //Toast.makeText(thisActivity, "Stop Clicking me", Toast.LENGTH_SHORT).show();
                showText(item);
            }
        });
    }

    public void showText(UserModel selectedModel) {
        Intent intent = new Intent(thisActivity, ChatBubbleActivity.class);

        //   intent.putExtra("userID",selectedModel.UserID);
        //  intent.putExtra("mobile",selectedModel.MobileNo);
        HubNotificationService.chatUser = selectedModel;
        startActivity(intent);
        //  startActivity(new Intent("com.example.pankaj.mychatapp.ChatBubbleActivity"));
    }

}


class UserModelAdapter extends ArrayAdapter<UserModel> {
    @Override
    public void clear() {
        userModelList.clear();
        super.clear();
    }

    private static final String TAG = "FruitArrayAdapter";
    private List<UserModel> userModelList = new ArrayList<UserModel>();

    static class UserViewHolder {
        ImageView friendImage;
        TextView friendName;
        TextView friendText;
    }

    public UserModelAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public void add(UserModel object) {
        userModelList.add(object);
        super.add(object);
    }

    @Override
    public int getCount() {
        return this.userModelList.size();
    }

    @Override
    public UserModel getItem(int index) {
        return this.userModelList.get(index);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        UserViewHolder viewHolder;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.list_row, parent, false);
            viewHolder = new UserViewHolder();
            viewHolder.friendImage = (ImageView) row.findViewById(R.id.friendImage);
            viewHolder.friendName = (TextView) row.findViewById(R.id.friendName);
            viewHolder.friendText = (TextView) row.findViewById(R.id.friendText);
            row.setTag(viewHolder);
        } else {
            viewHolder = (UserViewHolder) row.getTag();
        }
        UserModel user = getItem(position);
        if (user.PicData!=null) {
            viewHolder.friendImage.setImageBitmap(BitmapFactory.decodeByteArray(user.PicData, 0, user.PicData.length));
        }
        viewHolder.friendName.setText(user.Name);
        viewHolder.friendText.setText(user.MyStatus);
        return row;
    }

    public Bitmap decodeToBitmap(byte[] decodedByte) {
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}
