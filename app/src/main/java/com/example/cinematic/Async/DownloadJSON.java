package com.example.cinematic.Async;

import android.os.AsyncTask;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class DownloadJSON extends AsyncTask<String, Void, String>
{
    protected String doInBackground(String... urls) {
        String jsonResult = "";
        URL url;

        try {
            BufferedReader inputStream = null;
            url = new URL(urls[0]);
            URLConnection dc = url.openConnection();
            inputStream = new BufferedReader(new InputStreamReader(dc.getInputStream()));
            jsonResult = inputStream.readLine();
            inputStream.close();

            return  jsonResult;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
