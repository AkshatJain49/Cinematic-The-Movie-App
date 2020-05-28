package com.example.cinematic;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ActionBarDrawerToggle actionBarDrawerToggle;
    ProgressDialog progressDialog;
    ArrayList<Movies> moviesArrayList;
    MovieAdapter adapter;
    GridView moviesListView;





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

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);

                String info = jsonObject.getString("results");

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

                moviesListView.setAdapter(adapter);

                progressDialog.hide();

                moviesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        String ID = moviesArrayList.get(position).getId();

                        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                        intent.putExtra("TYPE", "MOVIE");
                        intent.putExtra("ID", ID);
                        startActivity(intent);
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





    //CUSTOM ADAPTER
    class MovieAdapter extends ArrayAdapter<Movies>
    {
        public MovieAdapter(Context context, ArrayList<Movies> moviesArrayList)
        {
            super(context, 0, moviesArrayList);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_adapter, parent, false);

            ImageView imageMovie = view.findViewById(R.id.imageMovie);
            TextView textName = view.findViewById(R.id.textName);

            Movies movie = getItem(position);

            textName.setText(movie.getMovieName());

            String posterURL = "https://image.tmdb.org/t/p/w500/" + movie.getPosterURL();

            DownloadImage image = new DownloadImage();

            try {
                Bitmap bitmap = image.execute(posterURL).get();
                imageMovie.setImageBitmap(bitmap);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return view;
        }
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("MOVIES");

        setSupportActionBar(toolbar);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("LOADING");
        progressDialog.show();

        moviesArrayList = new ArrayList<>();
        adapter = new MovieAdapter(MainActivity.this, moviesArrayList);
        moviesListView = findViewById(R.id.moviesGridView);

        DownloadJSON downloadJSON = new DownloadJSON();
        downloadJSON.execute("https://api.themoviedb.org/3/movie/popular?api_key=83b2f8791807db4f499f4633fca4af79&language=en-US&page=1");


        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Intent intent;
                switch (item.getTitle().toString())
                {
                    case "TV SHOWS":
                        intent = new Intent(MainActivity.this, ShowsActivity.class);
                        startActivity(intent);
                        finish();
                        return true;

                    case "ABOUT THE DEVELOPER":
                        intent = new Intent(MainActivity.this, AboutActivity.class);
                        startActivity(intent);
                        finish();
                        return true;

                }
                return false;
            }
        });


        TabLayout tabCategories = findViewById(R.id.tabCategories);
        tabCategories.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getText().equals("POPULAR")) {

                    progressDialog.show();
                    DownloadJSON downloadPopularJSON = new DownloadJSON();
                    downloadPopularJSON.execute("https://api.themoviedb.org/3/movie/popular?api_key=83b2f8791807db4f499f4633fca4af79&language=en-US&page=1");
                }

                else if (tab.getText().equals("NOW PLAYING")) {

                    progressDialog.show();
                    DownloadJSON downloadNowPlayingJSON = new DownloadJSON();
                    downloadNowPlayingJSON.execute("https://api.themoviedb.org/3/movie/now_playing?api_key=83b2f8791807db4f499f4633fca4af79&language=en-US&page=1");
                }

                else if (tab.getText().equals("UPCOMING")) {

                    progressDialog.show();
                    DownloadJSON downloadUpcomingJSON = new DownloadJSON();
                    downloadUpcomingJSON.execute("https://api.themoviedb.org/3/movie/upcoming?api_key=83b2f8791807db4f499f4633fca4af79&language=en-US&page=1");
                }

                else {

                    progressDialog.show();
                    DownloadJSON downloadTopRatedJSON = new DownloadJSON();
                    downloadTopRatedJSON.execute("https://api.themoviedb.org/3/movie/top_rated?api_key=83b2f8791807db4f499f4633fca4af79&language=en-US&page=1");

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        final EditText inputName = findViewById(R.id.inputName);
        Button btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(inputName.getWindowToken(), 0);

                final String query = inputName.getText().toString();

                if(query.isEmpty())
                    Snackbar.make(findViewById(R.id.drawerLayout), "ENTER MOVIE/SHOW NAME!", BaseTransientBottomBar.LENGTH_SHORT).show();

                else {
                    progressDialog.show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            DownloadJSON downloadNowPlayingJSON = new DownloadJSON();
                            downloadNowPlayingJSON.execute("https://api.themoviedb.org/3/search/movie?api_key=83b2f8791807db4f499f4633fca4af79&language=en-US&query=" + query +"&page=1");
                        }
                    }, 500);
                }
            }
        });
    }
}