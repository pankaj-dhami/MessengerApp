package com.example.pankaj.mychatapp.Utility;

/**
 * Created by pankaj on 6/16/2015.
 */
public class AppEnum {

    public enum SendDeliver {
        SEND(1), DELIVERED(2), UNDELIVERED(0), RECEIVED(3);
        private int value;

        private SendDeliver(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    };

    public enum Message
    {
        SEND_BY_ME(1),
        SEND_BY_OTHER(0);
        private int value;

        Message(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
    }
}
