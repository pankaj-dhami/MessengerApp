package com.example.pankaj.mychatapp.Utility;

import android.app.Activity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Created by pankaj on 6/6/2015.
 */
public class FileIO extends Activity {

    public void saveToFile(String fileName,String text)
    {
        try {
            FileOutputStream fos = openFileOutput(fileName, MODE_PRIVATE);
            BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(fos));
            bw.write(text);
        }
        catch (Exception ex)
        {

        }
    }

    public void appendToFile(String fileName,String text)
    {
        try {
         //   FileOutputStream fos = openFileOutput(fileName, MODE_PRIVATE);
          //  BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(fos));
            PrintWriter pw=new PrintWriter(new BufferedWriter(new FileWriter(fileName,true)));
            pw.println(text);
        }
        catch (Exception ex)
        {

        }
    }

    public String readFromfile(String fileName,int lineNo)
    {
        String line="";
        try {
            FileInputStream fileIn=openFileInput(fileName);
            BufferedReader br=new BufferedReader(new InputStreamReader(fileIn));

            int index=0;
            while (index!=lineNo) {
              if (index==lineNo-1)
              {
                  line=br.readLine();
              }
                index++;
            }
        }
        catch (Exception ex)
        {

        }
        return line;
    }

}
