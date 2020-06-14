package com.example.cinematic;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.example.cinematic.Adapters.MovieAdapter;
import com.example.cinematic.Async.DownloadJSON;
import com.example.cinematic.Classes.Movies;
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

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextToSpeech textToSpeech;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ActionBarDrawerToggle actionBarDrawerToggle;
    ProgressDialog progressDialog;
    ArrayList<Movies> moviesArrayList;
    MovieAdapter adapter;
    GridView moviesListView;
    EditText inputName;





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

        // HANDLER IS USED TO MAKE TRANSITION SMOOTH
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                try {
                    DownloadJSON downloadJSON = new DownloadJSON();
                    String data = downloadJSON.execute("https://api.themoviedb.org/3/movie/popular?api_key=83b2f8791807db4f499f4633fca4af79&language=en-US&page=1").get();
                    getData(data);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 500);


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
                        return true;

                    case "PEOPLE":
                        intent = new Intent(MainActivity.this, CastActivity.class);
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

                inputName.setText("");

                if (tab.getText().equals("POPULAR")) {

                    progressDialog.show();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                DownloadJSON downloadPopularJSON = new DownloadJSON();
                                String data = downloadPopularJSON.execute("https://api.themoviedb.org/3/movie/popular?api_key=83b2f8791807db4f499f4633fca4af79&language=en-US&page=1").get();
                                getData(data);
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, 500);
                }

                else if (tab.getText().equals("NOW PLAYING")) {

                    progressDialog.show();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                DownloadJSON downloadNowPlayingJSON = new DownloadJSON();
                                String data = downloadNowPlayingJSON.execute("https://api.themoviedb.org/3/movie/now_playing?api_key=83b2f8791807db4f499f4633fca4af79&language=en-US&page=1").get();
                                getData(data);
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, 500);
                }

                else if (tab.getText().equals("UPCOMING")) {

                    progressDialog.show();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                DownloadJSON downloadUpcomingJSON = new DownloadJSON();
                                String data = downloadUpcomingJSON.execute("https://api.themoviedb.org/3/movie/upcoming?api_key=83b2f8791807db4f499f4633fca4af79&language=en-US&page=1").get();
                                getData(data);
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, 500);
                }

                else {

                    progressDialog.show();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                DownloadJSON downloadTopRatedJSON = new DownloadJSON();
                                String data = downloadTopRatedJSON.execute("https://api.themoviedb.org/3/movie/top_rated?api_key=83b2f8791807db4f499f4633fca4af79&language=en-US&page=1").get();
                                getData(data);
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, 500);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        inputName = findViewById(R.id.inputName);
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
                    getSearchData(query);
                }
            }
        });


        Button btnVoice = findViewById(R.id.btnVoice);
        btnVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

                try{
                    startActivityForResult(intent, 101);
                }
                catch(Exception e) {
                    Snackbar.make(drawerLayout, "SPEECH INPUT NOT SUPPORTED", BaseTransientBottomBar.LENGTH_SHORT).show();
                }
            }
        });


        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    textToSpeech.setLanguage(Locale.getDefault());
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                final String query = result.get(0);
                inputName.setText(query);
                textToSpeech.speak("Searching" + query, TextToSpeech.QUEUE_FLUSH, null);

                getSearchData(query);
            }
        }
    }





    protected void getData(String s)
    {
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





    protected void getSearchData(final String query)
    {
        progressDialog.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    DownloadJSON downloadSearchJSON = new DownloadJSON();
                    String data = downloadSearchJSON.execute("https://api.themoviedb.org/3/search/movie?api_key=83b2f8791807db4f499f4633fca4af79&language=en-US&query=" + query +"&page=1").get();
                    getData(data);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 500);
    }
}