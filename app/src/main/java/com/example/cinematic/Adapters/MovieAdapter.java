package com.example.cinematic.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.cinematic.Async.DownloadImage;
import com.example.cinematic.Classes.Movies;
import com.example.cinematic.R;

import java.util.ArrayList;

public class MovieAdapter extends ArrayAdapter<Movies>
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