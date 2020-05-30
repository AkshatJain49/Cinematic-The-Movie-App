package com.example.cinematic;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cinematic.Adapters.CastAdapter;
import com.example.cinematic.Adapters.MovieAdapter;
import com.example.cinematic.Adapters.TrailerAdapter;
import com.example.cinematic.Async.DownloadJSON;
import com.example.cinematic.Classes.Cast;
import com.example.cinematic.Classes.Movies;
import com.example.cinematic.Classes.Trailers;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    TextView textMovieName, textDate, textRuntime, textRating, textStatus, textOverview;
    ListView trailerListView;
    GridView similarGridView, castGridView;
    ImageView imageMovie;
    String ID, Type, castData = "";
    TrailerAdapter adapter;
    MovieAdapter movieAdapter;
    CastAdapter castAdapter;
    ArrayList<Movies> moviesArrayList;
    ArrayList<Cast> castArrayList;
    ArrayList<Trailers> trailerArrayList;
    boolean requestCast = true, requestSimilar = true;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        ID = intent.getStringExtra("ID");
        Type = intent.getStringExtra("TYPE");

        final String fetchURL;

        if(Type.equals("MOVIE"))
            fetchURL = "https://api.themoviedb.org/3/movie/" + ID + "?api_key=83b2f8791807db4f499f4633fca4af79&language=en-US";
        else
            fetchURL = "https://api.themoviedb.org/3/tv/"+ ID +"?api_key=83b2f8791807db4f499f4633fca4af79&language=en-US";


        // ARRAY LISTS
        moviesArrayList = new ArrayList<>();
        castArrayList = new ArrayList<>();
        trailerArrayList = new ArrayList<>();

        // VIEWS
        similarGridView = findViewById(R.id.similarGridView);
        trailerListView = findViewById(R.id.trailerListView);
        castGridView = findViewById(R.id.castGridView);

        // ADAPTERS
        adapter = new TrailerAdapter(DetailActivity.this, trailerArrayList);
        movieAdapter = new MovieAdapter(DetailActivity.this, moviesArrayList);
        castAdapter = new CastAdapter(DetailActivity.this, castArrayList);

        textMovieName = findViewById(R.id.textMovieName);
        textDate = findViewById(R.id.textDate);
        textRuntime = findViewById(R.id.textRuntime);
        textRating = findViewById(R.id.textRating);
        textStatus = findViewById(R.id.textStatus);
        textOverview = findViewById(R.id.textOverview);
        imageMovie = findViewById(R.id.imageMovie);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("LOADING");
        progressDialog.show();


        // HANDLER IS USED TO MAKE TRANSITION SMOOTH
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                try {
                    DownloadJSON downloadJSON = new DownloadJSON();
                    String data = downloadJSON.execute(fetchURL).get();
                    getData(data);
                    progressDialog.dismiss();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }, 500);


        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getText().equals("OVERVIEW"))
                {
                    textOverview.setVisibility(View.VISIBLE);
                    trailerListView.setVisibility(View.GONE);
                    castGridView.setVisibility(View.GONE);
                    similarGridView.setVisibility(View.GONE);
                }

                else if (tab.getText().equals("TRAILERS"))
                {
                    textOverview.setVisibility(View.GONE);
                    trailerListView.setVisibility(View.VISIBLE);
                    castGridView.setVisibility(View.GONE);
                    similarGridView.setVisibility(View.GONE);
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

                    castGridView.setVisibility(View.VISIBLE);
                    similarGridView.setVisibility(View.GONE);
                }

                else
                {
                    textOverview.setVisibility(View.GONE);
                    trailerListView.setVisibility(View.GONE);

                    if(requestSimilar == true) {

                        progressDialog.show();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {

                                try {

                                    DownloadJSON downloadSimilarJSON = new DownloadJSON();
                                    String data;

                                    if (Type.equals("MOVIE")) {

                                        data = downloadSimilarJSON.execute("https://api.themoviedb.org/3/movie/"+ ID +"/similar?api_key=83b2f8791807db4f499f4633fca4af79&language=en-US&page=1").get();
                                        getSimilarData(data);
                                    }

                                    else {

                                        data = downloadSimilarJSON.execute("https://api.themoviedb.org/3/tv/"+ ID +"/similar?api_key=83b2f8791807db4f499f4633fca4af79&language=en-US&page=1").get();
                                        getSimilarData(data);
                                    }
                                    requestSimilar = false;

                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }, 500);
                    }

                    castGridView.setVisibility(View.GONE);
                    similarGridView.setVisibility(View.VISIBLE);
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

            Picasso.get().load(posterURL).error(R.drawable.no_image).into(imageMovie);

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
                castArrayList.add(new Cast(object.getString("profile_path"), object.getString("name"), object.getString("id")));
            }

            castGridView.setAdapter(castAdapter);
            progressDialog.hide();

            castGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String ID = castArrayList.get(position).getId();
                    Intent intent = new Intent(DetailActivity.this, CastDetailActivity.class);
                    intent.putExtra("ID", ID);
                    startActivity(intent);

                }
            });

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }





    protected void getSimilarData(String s)
    {
        try {
            JSONObject jsonObject = new JSONObject(s);

            String info = jsonObject.getString("results");

            JSONArray jsonArray = new JSONArray(info);

            moviesArrayList.clear();

            for(int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject object = jsonArray.getJSONObject(i);

                if (Type.equals("MOVIE"))
                    moviesArrayList.add(new Movies(
                            object.getString("poster_path"),
                            object.getString("title"),
                            object.getString("id")
                    ));

                else
                    moviesArrayList.add(new Movies(
                            object.getString("poster_path"),
                            object.getString("name"),
                            object.getString("id")
                    ));
            }

            similarGridView.setAdapter(movieAdapter);

            progressDialog.hide();

            similarGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String ID = moviesArrayList.get(position).getId();
                    Intent intent = new Intent(DetailActivity.this, DetailActivity.class);

                    if(Type.equals("MOVIE"))
                        intent.putExtra("TYPE", "MOVIE");

                    else
                        intent.putExtra("TYPE", "TV");

                    intent.putExtra("ID", ID);
                    startActivity(intent);
                    finish();

                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
            progressDialog.hide();
            Snackbar.make(findViewById(R.id.drawerLayout), "UNABLE TO FETCH INFORMATION", BaseTransientBottomBar.LENGTH_SHORT).show();
        }
    }
}