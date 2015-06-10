package com.example.pankaj.mychatapp.Model;

import java.io.Serializable;

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
}
