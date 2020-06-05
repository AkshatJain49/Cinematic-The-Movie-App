package com.example.cinematic;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cinematic.Adapters.MovieAdapter;
import com.example.cinematic.Async.DownloadJSON;
import com.example.cinematic.Classes.Movies;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class CastDetailActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    TextView textBirthDate, textBiography, textName, textPlace, textPopularity;
    ImageView imageCast;
    GridView moviesGridView, showsGridView;
    String ID;
    ArrayList<Movies> moviesArrayList, showsArrayList;
    MovieAdapter movieAdapter, showsAdapter;
    boolean imageLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cast_detail);

        Intent intent = getIntent();
        ID = intent.getStringExtra("ID");

        textName = findViewById(R.id.textCastName);
        textBiography = findViewById(R.id.textBiography);
        textBirthDate = findViewById(R.id.textBirthDate);
        textPopularity = findViewById(R.id.textPopularity);
        textPlace = findViewById(R.id.textPlace);

        imageCast = findViewById(R.id.imageCast);

        moviesArrayList = new ArrayList<>();
        showsArrayList = new ArrayList<>();

        moviesGridView = findViewById(R.id.moviesGridView);
        showsGridView = findViewById(R.id.showsGridView);

        movieAdapter = new MovieAdapter(CastDetailActivity.this, moviesArrayList);
        showsAdapter = new MovieAdapter(CastDetailActivity.this, showsArrayList);

        //ALLOWS TEXT VIEW SCROLLING
        textBiography.setMovementMethod(new ScrollingMovementMethod());

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("LOADING");
        progressDialog.show();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                try {
                    DownloadJSON downloadJSON = new DownloadJSON();
                    String data = downloadJSON.execute("https://api.themoviedb.org/3/person/"+ ID +"?api_key=83b2f8791807db4f499f4633fca4af79&language=en-US").get();
                    getData(data);

                    DownloadJSON downloadMoviesJSON = new DownloadJSON();
                    String dataMovies = data = downloadMoviesJSON.execute("https://api.themoviedb.org/3/person/"+ ID +"/movie_credits?api_key=83b2f8791807db4f499f4633fca4af79&language=en-US").get();
                    getMoviesData(dataMovies);

                    DownloadJSON downloadShowsJSON = new DownloadJSON();
                    String dataShows = downloadShowsJSON.execute("https://api.themoviedb.org/3/person/"+ ID +"/tv_credits?api_key=83b2f8791807db4f499f4633fca4af79&language=en-US").get();
                    getShowsData(dataShows);


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
                    textBiography.setVisibility(View.VISIBLE);
                    moviesGridView.setVisibility(View.GONE);
                    showsGridView.setVisibility(View.GONE);
                }

                else if(tab.getText().equals("MOVIES"))
                {
                    textBiography.setVisibility(View.GONE);
                    moviesGridView.setVisibility(View.VISIBLE);
                    showsGridView.setVisibility(View.GONE);
                }

                else
                {
                    textBiography.setVisibility(View.GONE);
                    moviesGridView.setVisibility(View.GONE);
                    showsGridView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    protected void getData(String s)
    {
        final String castName, dob, biography, place, popularity;
        try {
            JSONObject jsonObject = new JSONObject(s);

            final String posterURL = "https://image.tmdb.org/t/p/w500/" + jsonObject.getString("profile_path");

            castName = jsonObject.getString("name");
            biography = jsonObject.getString("biography");
            dob = jsonObject.getString("birthday");
            place = jsonObject.getString("place_of_birth");
            popularity = jsonObject.getString("popularity");

            Picasso.Builder picassoBuilder = new Picasso.Builder(this);
            picassoBuilder.listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    imageLoad = false;
                }
            });

            Picasso picasso = picassoBuilder.build();
            picasso.setLoggingEnabled(true);
            picasso.setLoggingEnabled(true);
            picasso.load(posterURL).error(R.drawable.no_image).into(imageCast);

            textName.setText(castName);
            textBirthDate.setText(dob);
            textBiography.setText(biography);
            textPlace.setText(place);
            textPopularity.setText(popularity);


            // STARTS ACTIVITY ONLY IF IMAGE IS LOADED
            imageCast.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (imageLoad) {
                        Intent intent = new Intent(CastDetailActivity.this, SaveImageActivity.class);
                        intent.putExtra("URL", posterURL);
                        intent.putExtra("NAME", castName);
                        startActivity(intent);
                    }

                }
            });

        }
        catch (Exception e) {
            e.printStackTrace();
            progressDialog.hide();
            Snackbar.make(findViewById(R.id.constraintLayout), "UNABLE TO FETCH INFORMATION", BaseTransientBottomBar.LENGTH_SHORT).show();
        }
    }





    protected void getMoviesData(String s)
    {
        try {
            Log.i("DATA", s);
            JSONObject jsonObject = new JSONObject(s);

            String info = jsonObject.getString("cast");

            JSONArray jsonArray = new JSONArray(info);

            moviesArrayList.clear();

            for(int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject object = jsonArray.getJSONObject(i);
                moviesArrayList.add(new Movies(
                            object.getString("poster_path"),
                            object.getString("title"),
                            object.getString("id")
                ));
            }

            moviesGridView.setAdapter(movieAdapter);

            progressDialog.hide();

            moviesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String ID = moviesArrayList.get(position).getId();
                    Intent intent = new Intent(CastDetailActivity.this, DetailActivity.class);
                    intent.putExtra("TYPE", "MOVIE");
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





    protected void getShowsData(String s)
    {
        try {
            JSONObject jsonObject = new JSONObject(s);

            String info = jsonObject.getString("cast");

            JSONArray jsonArray = new JSONArray(info);

            showsArrayList.clear();

            for(int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject object = jsonArray.getJSONObject(i);
                showsArrayList.add(new Movies(
                            object.getString("poster_path"),
                            object.getString("name"),
                            object.getString("id")
                ));
            }

            showsGridView.setAdapter(showsAdapter);

            progressDialog.hide();

            showsGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String ID = showsArrayList.get(position).getId();
                    Intent intent = new Intent(CastDetailActivity.this, DetailActivity.class);
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