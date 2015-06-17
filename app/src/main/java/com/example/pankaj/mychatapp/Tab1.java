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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.pankaj.mychatapp.Model.UserModel;
import com.example.pankaj.mychatapp.Utility.ApplicationConstants;
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
                        String fruitImg = "orange";
                        int fruitImgResId = getResources().getIdentifier(fruitImg, "drawable", "com.example.pankaj.mychatapp");
                        model.MyStatus = model.MobileNo +"/"+  model.UserID;
                        model.PicImg = fruitImgResId;
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


        //  View v =inflater.inflate(R.layout.tab_1,container,false);
        entity=new SqlLiteDb(thisActivity);
        entity.open();
        MyService.FriendsList= entity.getFriendsList();
        entity.close();
        userArrayAdapter.clear();
        for (UserModel model : MyService.FriendsList) {
            String fruitImg = "orange";
            int fruitImgResId = getResources().getIdentifier(fruitImg, "drawable", "com.example.pankaj.mychatapp");
            model.MyStatus = model.MobileNo +"/"+ model.UserID;
            model.PicImg = fruitImgResId;
            userArrayAdapter.add(model);
        }
       /* listView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(thisActivity, "Stop Clicking me", Toast.LENGTH_SHORT).show();

            }
        });*/
       /* listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                // showText();
              *//*  final UserModel item = (UserModel) parent.getItemAtPosition(position);
                view.animate().setDuration(2000).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                // textView.setText("Selected: "+ item);

                                //to remove item from list
                                //list.remove(item);
                                //adapter.notifyDataSetChanged();
                               // Toast.makeText(HomeActivity.this, "You Clicked at " + item.Name, Toast.LENGTH_SHORT).show();
                                startActivity(new Intent("com.example.pankaj.mychatapp.ChatBubbleActivity"));
                                view.setAlpha(1);
                            }
                        });*//*
                Toast.makeText(thisActivity, "Stop Clicking me", Toast.LENGTH_SHORT).show();
            }

        });*/

      /*  editText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    return addItem();
                }
                return false;
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addItem();
            }
        });*/
        //  Toast.makeText(this, "You Clicked at " + userModelList.get(position).Name, Toast.LENGTH_SHORT).show();
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

    public List<String[]> readData() {
        List<String[]> resultList = new ArrayList<String[]>();

        String[] fruit7 = new String[3];
        fruit7[0] = "orange";
        fruit7[1] = "Orange";
        fruit7[2] = "47 Calories";
        resultList.add(fruit7);

        String[] fruit1 = new String[3];
        fruit1[0] = "cherry";
        fruit1[1] = "Cherry";
        fruit1[2] = "50 Calories";
        resultList.add(fruit1);


        String[] fruit3 = new String[3];
        fruit3[0] = "banana";
        fruit3[1] = "Banana";
        fruit3[2] = "89 Calories";
        resultList.add(fruit3);

        String[] fruit4 = new String[3];
        fruit4[0] = "apple";
        fruit4[1] = "Apple";
        fruit4[2] = "52 Calories";
        resultList.add(fruit4);

        String[] fruit10 = new String[3];
        fruit10[0] = "kiwi";
        fruit10[1] = "Kiwi";
        fruit10[2] = "61 Calories";
        resultList.add(fruit10);

        String[] fruit5 = new String[3];
        fruit5[0] = "pear";
        fruit5[1] = "Pear";
        fruit5[2] = "57 Calories";
        resultList.add(fruit5);


        String[] fruit2 = new String[3];
        fruit2[0] = "strawberry";
        fruit2[1] = "Strawberry";
        fruit2[2] = "33 Calories";
        resultList.add(fruit2);

        String[] fruit6 = new String[3];
        fruit6[0] = "lemon";
        fruit6[1] = "Lemon";
        fruit6[2] = "29 Calories";
        resultList.add(fruit6);

        String[] fruit8 = new String[3];
        fruit8[0] = "peach";
        fruit8[1] = "Peach";
        fruit8[2] = "39 Calories";
        resultList.add(fruit8);

        String[] fruit9 = new String[3];
        fruit9[0] = "apricot";
        fruit9[1] = "Apricot";
        fruit9[2] = "48 Calories";
        resultList.add(fruit9);

        String[] fruit11 = new String[3];
        fruit11[0] = "mango";
        fruit11[1] = "Mango";
        fruit11[2] = "60 Calories";
        resultList.add(fruit11);

        return resultList;
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
        viewHolder.friendImage.setImageResource(user.PicImg);
        viewHolder.friendName.setText(user.Name);
        viewHolder.friendText.setText(user.MyStatus);
        return row;
    }

    public Bitmap decodeToBitmap(byte[] decodedByte) {
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}
