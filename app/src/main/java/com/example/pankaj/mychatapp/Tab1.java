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
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pankaj.mychatapp.Model.UserModel;
import com.example.pankaj.mychatapp.Utility.HubNotificationService;
import com.example.pankaj.mychatapp.Utility.MyService;
import com.example.pankaj.mychatapp.Utility.SqlLiteDb;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edwin on 15/02/2015.
 */
public class Tab1 extends Fragment implements ObservableScrollViewCallbacks {

    private static final String TAG = Tab1.class.getSimpleName();
    private UserModelAdapter userArrayAdapter;
    private ObservableListView listView;
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

                        model.MyStatus = model.MobileNo +"/"+ model.UserID;
                        userArrayAdapter.add(model);
                    }
                }
            }
        }
    };


    @Override
    public void onResume() {
        super.onResume();
        HubNotificationService.Tab1Activity_active = true;
        thisActivity.registerReceiver(receiver, new IntentFilter("com.example.pankaj.mychatapp"));
    }

    @Override
    public void onPause() {
        super.onPause();
        HubNotificationService.Tab1Activity_active = false;
        thisActivity.unregisterReceiver(receiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        colorIndex = 0;
        thisActivity = getActivity();

        View v = inflater.inflate(R.layout.tab_1, container, false);
        listView = (ObservableListView) v.findViewById(R.id.listView);

        listView.setScrollViewCallbacks(this);
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
            userArrayAdapter.add(model);
        }

        // ObservableListView uses setOnScrollListener, but it still works.
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                Log.v(TAG, "onScrollStateChanged: " + scrollState);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                Log.v(TAG, "onScroll: firstVisibleItem: " + firstVisibleItem + " visibleItemCount: " + visibleItemCount + " totalItemCount: " + totalItemCount);
            }
        });
        return v;
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        ActionBar ab = HomeDash.homeDashActivity.getSupportActionBar();
        if (ab == null) {
            return;
        }
        if (scrollState == ScrollState.UP) {
            if (ab.isShowing()) {
                ab.hide();
            }
        } else if (scrollState == ScrollState.DOWN) {
            if (!ab.isShowing()) {
                ab.show();
            }
        }
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
        if (user.PicData!=null && user.PicData.length >3) {
            try {
                viewHolder.friendImage.setImageBitmap(BitmapFactory.decodeByteArray(user.PicData, 0, user.PicData.length));
            } catch (Exception e) {
                viewHolder.friendImage.setImageResource(R.drawable.nouser);
            }
        }
        else {

            viewHolder.friendImage.setImageResource(R.drawable.nouser);
        }
        viewHolder.friendName.setText(user.Name);
        viewHolder.friendText.setText(user.MyStatus);
        return row;
    }

    public Bitmap decodeToBitmap(byte[] decodedByte) {
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}
