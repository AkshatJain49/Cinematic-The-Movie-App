package com.example.cinematic;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class DetailActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    TextView textMovieName, textDate, textRuntime, textRating, textStatus, textOverview, textCast;
    ListView trailerListView;
    ImageView imageMovie;
    String ID, Type, castData = "";
    TrailerAdapter adapter;
    ArrayList<Trailers> trailerArrayList;
    boolean requestCast = true;


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






    public class DownloadImage extends AsyncTask<String, Void, Bitmap>
    {

        @Override
        protected Bitmap doInBackground(String... urls) {
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            }
            catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }






    class TrailerAdapter extends ArrayAdapter<Trailers>
    {
        public TrailerAdapter(Context context, ArrayList<Trailers> trailerArrayList)
        {
            super(context, 0, trailerArrayList);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            @SuppressLint("ViewHolder") View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_trailer_adapter, parent, false);

            Trailers trailer = getItem(position);

            TextView textLink = view.findViewById(R.id.textLink);
            textLink.setText(trailer.getTrailerTitle());

            return view;
        }
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        ID = intent.getStringExtra("ID");
        Type = intent.getStringExtra("TYPE");

        String fetchURL;

        if(Type.equals("MOVIE"))
            fetchURL = "https://api.themoviedb.org/3/movie/" + ID + "?api_key=83b2f8791807db4f499f4633fca4af79&language=en-US";
        else
            fetchURL = "https://api.themoviedb.org/3/tv/"+ ID +"?api_key=83b2f8791807db4f499f4633fca4af79&language=en-US";


        trailerListView = findViewById(R.id.trailerListView);
        trailerArrayList = new ArrayList<>();
        adapter = new TrailerAdapter(DetailActivity.this, trailerArrayList);

        textMovieName = findViewById(R.id.textMovieName);
        textDate = findViewById(R.id.textDate);
        textRuntime = findViewById(R.id.textRuntime);
        textRating = findViewById(R.id.textRating);
        textStatus = findViewById(R.id.textStatus);
        textOverview = findViewById(R.id.textOverview);
        textCast = findViewById(R.id.textCast);
        imageMovie = findViewById(R.id.imageMovie);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("LOADING");
        progressDialog.show();

        DownloadJSON downloadJSON = new DownloadJSON();
        try {
            String data = downloadJSON.execute(fetchURL).get();
            getData(data);
            progressDialog.dismiss();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getText().equals("OVERVIEW"))
                {
                    textOverview.setVisibility(View.VISIBLE);
                    trailerListView.setVisibility(View.GONE);
                    textCast.setVisibility(View.GONE);
                }

                else if (tab.getText().equals("TRAILERS"))
                {
                    textOverview.setVisibility(View.GONE);
                    trailerListView.setVisibility(View.VISIBLE);
                    textCast.setVisibility(View.GONE);
                }

                else if(tab.getText().equals("CAST"))
                {
                    textOverview.setVisibility(View.GONE);
                    trailerListView.setVisibility(View.GONE);

                    if(requestCast == true) {

                        progressDialog.show();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {

                                try {

                                    DownloadJSON downloadCastJSON = new DownloadJSON();
                                    String data;

                                    if (Type.equals("MOVIE")) {

                                        data = downloadCastJSON.execute("https://api.themoviedb.org/3/movie/" + ID + "/credits?api_key=83b2f8791807db4f499f4633fca4af79").get();
                                        getCastData(data);
                                    }

                                    else {

                                        data = downloadCastJSON.execute("https://api.themoviedb.org/3/tv/" + ID + "/credits?api_key=83b2f8791807db4f499f4633fca4af79").get();
                                        getCastData(data);
                                    }
                                    requestCast = false;

                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }, 500);
                    }

                    textCast.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        trailerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String videoID = trailerArrayList.get(position).getTrailerLink();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoID));
                intent.putExtra("Video_id",videoID);
                startActivity(intent);
            }

        });
    }





    protected void getData(String s)
    {
        String name, releaseDate, runTime, userRating, releaseStatus, overview;
        try {

            JSONObject jsonObject = new JSONObject(s);
            String posterURL = "https://image.tmdb.org/t/p/w500/" + jsonObject.getString("poster_path");
            DownloadJSON downloadTrailer = new DownloadJSON();

            if(Type.equals("MOVIE"))
            {
                name = jsonObject.getString("title");
                releaseDate = jsonObject.getString("release_date");
                runTime = jsonObject.getString("runtime");
                userRating = jsonObject.getString("vote_average");
                releaseStatus = jsonObject.getString("status");
                overview = jsonObject.getString("overview");
                String data = downloadTrailer.execute("https://api.themoviedb.org/3/movie/"+ ID +"/videos?api_key=83b2f8791807db4f499f4633fca4af79&language=en-US").get();
                getTrailerData(data);
            }

            else
            {
                name = jsonObject.getString("name");
                releaseDate = jsonObject.getString("first_air_date");
                runTime = "44";
                userRating = jsonObject.getString("vote_average");
                releaseStatus = jsonObject.getString("status");
                overview = jsonObject.getString("overview");

                String data = downloadTrailer.execute("https://api.themoviedb.org/3/tv/"+ ID +"/videos?api_key=83b2f8791807db4f499f4633fca4af79&language=en-US").get();
                getTrailerData(data);
            }

            DownloadImage imagePoster = new DownloadImage();
            try {
                Bitmap bitmapPoster = imagePoster.execute(posterURL).get();
                imageMovie.setImageBitmap(bitmapPoster);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            int time = Integer.parseInt(runTime);
            int hours = time / 60;
            int min = time % 60;
            if (hours == 0)
                runTime = min + " min";
            else if(hours !=0)
                runTime = hours + " hours " + min + " minutes";

            textMovieName.setText(name);
            textDate.setText(releaseDate);
            textRuntime.setText(runTime);
            textRating.setText(userRating);
            textStatus.setText(releaseStatus);
            textOverview.setText(overview);

        }
        catch (Exception e) {
            e.printStackTrace();
            progressDialog.hide();
            Snackbar.make(findViewById(R.id.constraintLayout), "UNABLE TO FETCH INFORMATION", BaseTransientBottomBar.LENGTH_SHORT).show();
        }
    }





    protected void getTrailerData(String s)
    {
        try {
            JSONObject jsonObject = new JSONObject(s);

            String info = jsonObject.getString("results");

            JSONArray jsonArray = new JSONArray(info);

            trailerArrayList.clear();

            for(int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject object = jsonArray.getJSONObject(i);

                String title = object.getString("name");
                String link = "https://www.youtube.com/watch?v=" + object.getString("key");

                trailerArrayList.add(new Trailers(title, link));
            }

            trailerListView.setAdapter(adapter);

            // SET LIST VIEW HEIGHT DEPENDING ON TOTAL NO. OF ITEMS
            ListAdapter listAdapter = trailerListView.getAdapter();
            if (listAdapter == null) {
                return;
            }

            int totalHeight = 0;
            for (int i = 0; i < listAdapter.getCount(); i++) {
                View listItem = listAdapter.getView(i, null, trailerListView);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }

            ViewGroup.LayoutParams params = trailerListView.getLayoutParams();
            params.height = totalHeight + (trailerListView.getDividerHeight() * (listAdapter.getCount() - 1));
            trailerListView.setLayoutParams(params);
            trailerListView.requestLayout();

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }





    protected void getCastData(String s)
    {
        try {
            JSONObject jsonObject = new JSONObject(s);

            String info = jsonObject.getString("cast");
            JSONArray jsonArray = new JSONArray(info);


            for(int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject object = jsonArray.getJSONObject(i);
                String name = object.getString("name");
                String character = object.getString("character");
                castData += "\'" + name +"\'" + " as " + character + "\n";

            }

            textCast.setText(castData);
            progressDialog.hide();

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}