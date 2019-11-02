package com.example.myfirstwhatsapp.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfirstwhatsapp.R;
import com.example.myfirstwhatsapp.semister.FifthSemActivity;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter
  <ImageAdapter.MyViewHolder>
{

    int image [];
    List<String> myList;


    public ImageAdapter(int[] image, List<String> myList ) {
        this.image = image;
        this.myList = myList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_displaying_layout2 , parent ,false);

        return new MyViewHolder(view );
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        holder.imageView.setImageResource(image[position]);
        holder.textView.setText(myList.get(position));

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String string = myList.get(position);
                if(string.equals("Semister 5 ")){
                    Intent intent = new Intent(holder.imageView.getContext() , FifthSemActivity.class);
                    intent.putExtra("semName" , "Semister 5");
                    holder.imageView.getContext().startActivity(intent);
                }
                else {
                    Toast.makeText(holder.imageView.getContext(),
                            "u presses :" + myList.get(position), Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    @Override
    public int getItemCount() {
        return myList.size();
    }

    public class MyViewHolder  extends RecyclerView.ViewHolder {
        View view;
        ImageView imageView;
        TextView textView;

        public MyViewHolder(@NonNull View itemView ) {
            super(itemView);

            view =itemView;
            imageView = view.findViewById(R.id.id_img);
            textView = view.findViewById(R.id.id_text);

        }

    }
}
