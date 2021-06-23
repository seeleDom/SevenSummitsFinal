package com.md.sevensummitsfinal;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {
    private ArrayList<exampleActivity> mActivities;
    public static class CardViewHolder extends RecyclerView.ViewHolder{
        public ImageView mImage;
        public TextView text1;
        public TextView text2;

        public CardViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mImage = itemView.findViewById(R.id.CardImageView);
            text1 = itemView.findViewById(R.id.CardTextView);
            text2 = itemView.findViewById(R.id.CardTextView2);

        }
    }

    public CardAdapter(ArrayList<exampleActivity> myActivities){
        mActivities = myActivities;
    }

    @NonNull
    @NotNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_card, parent, false);
        CardViewHolder cvh = new CardViewHolder(v);
        return cvh;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CardViewHolder holder, int position) {
        exampleActivity currentItem = mActivities.get(position);
        Log.d(TAG, "ImageUrl: " + currentItem.getImage());
        Picasso.get().load(currentItem.getImage()).into(holder.mImage);
        holder.text1.setText(currentItem.getTitel());
        holder.text2.setText(currentItem.getBeschreibung());
    }

    @Override
    public int getItemCount() {
        return mActivities.size();
    }
}
