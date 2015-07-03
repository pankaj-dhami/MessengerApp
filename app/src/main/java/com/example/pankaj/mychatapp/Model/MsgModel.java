package com.example.pankaj.mychatapp.Model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by pankaj.dhami on 6/10/2015.
 */
public  class MsgModel implements Serializable
{
    public UserModel UserModel ;
    public String AttachmentUrl ;
    public String AttachmentName ;
    public byte[] AttachmentData ;
    public String TextMessage ;
    public ArrayList<String> Pic64Data;
    public boolean IsAttchment;;

}

