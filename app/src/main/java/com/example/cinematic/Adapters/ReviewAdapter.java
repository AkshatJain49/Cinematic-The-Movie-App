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

import com.example.cinematic.Classes.Reviews;
import com.example.cinematic.R;

import java.util.ArrayList;

public class ReviewAdapter extends ArrayAdapter<Reviews>
{
    public ReviewAdapter(Context context, ArrayList<Reviews> reviewArrayList)
    {
        super(context, 0, reviewArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        @SuppressLint("ViewHolder") View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_review_adapter, parent, false);

         Reviews review = getItem(position);

        TextView textAuthor = view.findViewById(R.id.textAuthor);
        TextView textContent = view.findViewById(R.id.textContent);
        textAuthor.setText(review.getReviewAuthor());
        textContent.setText(review.getReviewContent());

        return view;
    }
}
