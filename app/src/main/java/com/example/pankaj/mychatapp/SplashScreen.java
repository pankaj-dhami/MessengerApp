package com.example.pankaj.mychatapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.pankaj.mychatapp.Utility.MyService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;


public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Button btn1=(Button)findViewById(R.id.button);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStartClick(view);
            }
        });
        Button btn2=(Button)findViewById(R.id.buttonstop);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStopService(view);
            }
        });

        Thread thread=new Thread(){
            public  void  run()
            {
                try{
                    sleep(2000);
                }
                catch(Exception ex){}
                finally {
                   // Intent intent = new Intent("com.example.pankaj.mychatapp.LoginActivity");

                //   saveToFile("pankaj.txt","pankaj dhami");
                //    String str= readFromfile("pankaj.txt",1);
               //     if (TextUtils.isEmpty(str))
                //    {
                       startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                //   }
                 //   else
                  //  {
                 //       Toast.makeText(SplashScreen.this,str,Toast.LENGTH_LONG);
                  //  }
                }
            }
        };
        thread.start();
    }
    public void saveToFile(String fileName,String text)
    {
        try {
            FileOutputStream fos = openFileOutput(fileName, MODE_PRIVATE);
            BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(fos));
            bw.write(text);
            bw.close();
            fos.close();
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
            fileIn.close();
        }
        catch (Exception ex)
        {

        }
        return line;
    }

    public void onStartClick(View v)
    {
        Intent intent = new Intent(this, MyService.class);
        intent.putExtra("message","pankaj");
        this.startService(intent);
        Toast.makeText(this, "service destroyed ", Toast.LENGTH_LONG);
    }
    public void onStopService(View v) {
        Intent intent = new Intent(this, MyService.class);
        this.stopService(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash_screen, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
