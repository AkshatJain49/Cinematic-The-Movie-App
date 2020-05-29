package com.example.cinematic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.cinematic.Adapters.CastAdapter;
import com.example.cinematic.Async.DownloadJSON;
import com.example.cinematic.Classes.Cast;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class CastActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ActionBarDrawerToggle actionBarDrawerToggle;
    ProgressDialog progressDialog;
    GridView castGridView;
    ArrayList<Cast> castArrayList;
    CastAdapter castAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cast);

        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("POPULAR PEOPLE");

        setSupportActionBar(toolbar);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("LOADING");
        progressDialog.show();

        castGridView = findViewById(R.id.castGridView);
        castArrayList = new ArrayList<>();
        castAdapter = new CastAdapter(CastActivity.this, castArrayList);


        // HANDLER IS USED TO MAKE TRANSITION SMOOTH
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                DownloadJSON downloadJSON = new DownloadJSON();
                try {
                    String data = downloadJSON.execute("https://api.themoviedb.org/3/person/popular?api_key=83b2f8791807db4f499f4633fca4af79&language=en-US&page=1").get();
                    getData(data);
                    progressDialog.dismiss();
                }
                catch (Exception e)
                {
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
                    case "MOVIES":
                        intent = new Intent(CastActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        return true;

                    case "TV SHOWS":
                        intent = new Intent(CastActivity.this, ShowsActivity.class);
                        startActivity(intent);
                        finish();
                        return true;

                    case "ABOUT THE DEVELOPER":
                        intent = new Intent(CastActivity.this, AboutActivity.class);
                        startActivity(intent);
                        return true;

                }
                return false;
            }
        });
    }





    protected void getData(String s)
    {
        try {
            JSONObject jsonObject = new JSONObject(s);

            String info = jsonObject.getString("results");

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
                    Intent intent = new Intent(CastActivity.this, CastDetailActivity.class);
                    intent.putExtra("ID", ID);
                    startActivity(intent);

                }
            });

        }
        catch (Exception e) {
            e.printStackTrace();
            progressDialog.hide();
            Snackbar.make(drawerLayout, "UNABLE TO FETCH INFORMATION", BaseTransientBottomBar.LENGTH_SHORT);
        }
    }
}
