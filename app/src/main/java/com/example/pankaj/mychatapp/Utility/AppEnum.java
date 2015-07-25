package com.example.pankaj.mychatapp.Utility;

import java.util.Stack;

/**
 * Created by pankaj on 6/16/2015.
 */
public class AppEnum {

    public final static int SEND_BY_OTHER = 0;
    public final static int SEND_BY_ME = 1;
    public final static int Trying_SEND = 0;
    public final static int SEND = 1;
    public final static int DELIVERED = 2;
    public final static int RECEIVED = 3;
    public final static int UNDELIVERED = 4;

    public static final String MsgReceivedNotify="msgReceivedNotify";
    public static final String MsgReceivedAttachment="msgReceivedAttachment";
    public static final String MsgSendNotify="msgSendNotify";
}
