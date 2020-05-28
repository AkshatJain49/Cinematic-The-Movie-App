package com.example.cinematic.Async;

import android.os.AsyncTask;


import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadJSON extends AsyncTask<String, Void, String>
{
    protected String doInBackground(String... urls) {
        String jsonResult = "";
        URL url;
        HttpURLConnection urlConnection = null;

        try {
            url = new URL(urls[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = urlConnection.getInputStream();
            InputStreamReader streamReader = new InputStreamReader(inputStream);
            int data = streamReader.read();

            while(data != -1)
            {
                char currentChar = (char) data;
                jsonResult += currentChar ;
                data = streamReader.read();
            }
            return  jsonResult;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
