package com.example.helsanf.catalogemovie.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.helsanf.catalogemovie.R;
import com.example.helsanf.catalogemovie.model.MovieItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdapterCardMovie extends RecyclerView.Adapter<AdapterCardMovie.CardViewHolder> {
    List<MovieItem> listMovie;
    Context context;
    private String fix;

    public AdapterCardMovie(Context context) {
        this.context = context;
    }

    public AdapterCardMovie(List<MovieItem> listMovie) {
        this.listMovie = listMovie;
    }

    public List<MovieItem> getListMovie() {
        return listMovie;
    }

    public void setListMovie(List<MovieItem> listMovie) {
        this.listMovie = listMovie;
    }

    @NonNull
    @Override
    public AdapterCardMovie.CardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_card_view, viewGroup, false);
        return new CardViewHolder(view);
//        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterCardMovie.CardViewHolder cardViewHolder, int i) {
        cardViewHolder.mTitle.setText(getListMovie().get(i).getTitle());

        String test = getListMovie().get(i).getDeskripsi();

        if(TextUtils.isEmpty(test)){
            fix = "Data tidak ada";
        }else{
            fix = test.substring(0,15)+"....";
        }
        cardViewHolder.mDesc.setText(fix);

        String getDate = getListMovie().get(i).getReleaseDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = dateFormat.parse(getDate);
            SimpleDateFormat newFormat = new SimpleDateFormat("EEEE, MMM dd, yyyy");
            String dateFix = newFormat.format(date);
            cardViewHolder.mTanggal.setText(dateFix);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.icon_launcher);
        requestOptions.error(R.drawable.icon_launcher);
        Glide.with(context.getApplicationContext())
                .setDefaultRequestOptions(requestOptions)
                .load("http://image.tmdb.org/t/p/w500"+getListMovie().get(i).getPosterPath())
                .into(cardViewHolder.imgMovie);
    }

    @Override
    public int getItemCount() {
        return listMovie.size();
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_title)
        TextView mTitle;
        @BindView(R.id.tv_desc)
        TextView mDesc;
        @BindView(R.id.tv_tanggal)
        TextView mTanggal;
        @BindView(R.id.image_view)
        ImageView imgMovie;
        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
