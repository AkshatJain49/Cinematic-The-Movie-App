package com.example.cinematic;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cinematic.Adapters.CastAdapter;
import com.example.cinematic.Adapters.MovieAdapter;
import com.example.cinematic.Adapters.ReviewAdapter;
import com.example.cinematic.Adapters.TrailerAdapter;
import com.example.cinematic.Async.DownloadJSON;
import com.example.cinematic.Classes.Cast;
import com.example.cinematic.Classes.Movies;
import com.example.cinematic.Classes.Reviews;
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
    ListView trailerListView, reviewsListView;
    GridView similarGridView, castGridView;
    ImageView imageMovie;
    String ID, Type, castData = "";
    TrailerAdapter adapter;
    MovieAdapter movieAdapter;
    CastAdapter castAdapter;
    ReviewAdapter reviewAdapter;
    ArrayList<Movies> moviesArrayList;
    ArrayList<Cast> castArrayList;
    ArrayList<Trailers> trailerArrayList;
    ArrayList<Reviews> reviewsArrayList;
    boolean imageLoad = true;





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
        reviewsArrayList = new ArrayList<>();

        // VIEWS
        similarGridView = findViewById(R.id.similarGridView);
        trailerListView = findViewById(R.id.trailerListView);
        castGridView = findViewById(R.id.castGridView);
        reviewsListView = findViewById(R.id.reviewsListView);

        // ADAPTERS
        adapter = new TrailerAdapter(DetailActivity.this, trailerArrayList);
        movieAdapter = new MovieAdapter(DetailActivity.this, moviesArrayList);
        castAdapter = new CastAdapter(DetailActivity.this, castArrayList);
        reviewAdapter = new ReviewAdapter(DetailActivity.this, reviewsArrayList);

        textMovieName = findViewById(R.id.textMovieName);
        textDate = findViewById(R.id.textDate);
        textRuntime = findViewById(R.id.textRuntime);
        textRating = findViewById(R.id.textRating);
        textStatus = findViewById(R.id.textStatus);
        textOverview = findViewById(R.id.textOverview);
        imageMovie = findViewById(R.id.imageMovie);

        textOverview.setMovementMethod(new ScrollingMovementMethod());

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

                    DownloadJSON downloadCastJSON = new DownloadJSON();
                    DownloadJSON downloadReviewJSON = new DownloadJSON();
                    DownloadJSON downloadSimilarJSON = new DownloadJSON();
                    String dataCast, dataReview, dataSimilar;
                    if (Type.equals("MOVIE")) {
                        dataCast = downloadCastJSON.execute("https://api.themoviedb.org/3/movie/" + ID + "/credits?api_key=83b2f8791807db4f499f4633fca4af79").get();
                        dataReview = downloadReviewJSON.execute("https://api.themoviedb.org/3/movie/"+ ID +"/reviews?api_key=83b2f8791807db4f499f4633fca4af79&language=en-US&page=1").get();
                        dataSimilar = downloadSimilarJSON.execute("https://api.themoviedb.org/3/movie/"+ ID +"/similar?api_key=83b2f8791807db4f499f4633fca4af79&language=en-US&page=1").get();
                    }

                    else {
                        dataCast = downloadCastJSON.execute("https://api.themoviedb.org/3/tv/" + ID + "/credits?api_key=83b2f8791807db4f499f4633fca4af79").get();
                        dataReview = downloadReviewJSON.execute("https://api.themoviedb.org/3/tv/"+ ID +"/reviews?api_key=83b2f8791807db4f499f4633fca4af79&language=en-US&page=1").get();
                        dataSimilar = downloadSimilarJSON.execute("https://api.themoviedb.org/3/tv/"+ ID +"/similar?api_key=83b2f8791807db4f499f4633fca4af79&language=en-US&page=1").get();
                    }
                    getCastData(dataCast);
                    getReviewData(dataReview);
                    getSimilarData(dataSimilar);

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
                    reviewsListView.setVisibility(View.GONE);
                }

                else if (tab.getText().equals("TRAILERS"))
                {
                    textOverview.setVisibility(View.GONE);
                    trailerListView.setVisibility(View.VISIBLE);
                    castGridView.setVisibility(View.GONE);
                    similarGridView.setVisibility(View.GONE);
                    reviewsListView.setVisibility(View.GONE);
                }

                else if(tab.getText().equals("CAST"))
                {
                    textOverview.setVisibility(View.GONE);
                    trailerListView.setVisibility(View.GONE);
                    castGridView.setVisibility(View.VISIBLE);
                    similarGridView.setVisibility(View.GONE);
                    reviewsListView.setVisibility(View.GONE);
                }

                else if(tab.getText().equals("REVIEWS"))
                {
                    textOverview.setVisibility(View.GONE);
                    trailerListView.setVisibility(View.GONE);
                    castGridView.setVisibility(View.GONE);
                    similarGridView.setVisibility(View.GONE);
                    reviewsListView.setVisibility(View.VISIBLE);
                }

                else
                {
                    textOverview.setVisibility(View.GONE);
                    trailerListView.setVisibility(View.GONE);
                    castGridView.setVisibility(View.GONE);
                    similarGridView.setVisibility(View.VISIBLE);
                    reviewsListView.setVisibility(View.GONE);
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
        final String name;
        String releaseDate, runTime, userRating, releaseStatus, overview;

        try {

            JSONObject jsonObject = new JSONObject(s);
            final String posterURL = "https://image.tmdb.org/t/p/w500/" + jsonObject.getString("poster_path");
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
            picasso.load(posterURL).error(R.drawable.no_image).into(imageMovie);
            //Picasso.get().load(posterURL).error(R.drawable.no_image).into(imageMovie);

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


            imageMovie.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // STARTS ACTIVITY ONLY IF IMAGE IS LOADED
                    if (imageLoad) {
                        Intent intent = new Intent(DetailActivity.this, SaveImageActivity.class);
                        intent.putExtra("URL", posterURL);
                        intent.putExtra("NAME", name);
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





    protected void getReviewData(String s)
    {
        try {
            JSONObject jsonObject = new JSONObject(s);

            String info = jsonObject.getString("results");

            JSONArray jsonArray = new JSONArray(info);

            reviewsArrayList.clear();

            for(int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject object = jsonArray.getJSONObject(i);

                String author = object.getString("author");
                String content = object.getString("content");

                reviewsArrayList.add(new Reviews(author, content));
            }

            reviewsListView.setAdapter(reviewAdapter);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}