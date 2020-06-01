package com.example.cinematic;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cinematic.Async.DownloadJSON;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

public class CastDetailActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    TextView textBirthDate, textBiography, textName, textPlace, textPopularity;
    ImageView imageCast;
    String ID;
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
                    progressDialog.dismiss();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }, 500);
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
}