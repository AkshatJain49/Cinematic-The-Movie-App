package com.example.cinematic.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.cinematic.R;
import com.example.cinematic.Classes.Trailers;

import java.util.ArrayList;

public class TrailerAdapter extends ArrayAdapter<Trailers>
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