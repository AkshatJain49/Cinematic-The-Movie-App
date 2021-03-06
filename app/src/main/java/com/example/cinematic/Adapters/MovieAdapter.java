package com.example.cinematic.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.cinematic.Classes.Movies;
import com.example.cinematic.R;
import com.squareup.picasso.Picasso;

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

        //DOWNLOADING IMAGE
        Picasso.get().load(posterURL).error(R.drawable.no_image).into(imageMovie);

        return view;
    }
}