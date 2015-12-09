package com.umang.popularmovies.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.umang.popularmovies.R;
import com.umang.popularmovies.ui.activity.DetailActivity;
import com.umang.popularmovies.utility.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by umang on 20/11/15.
 */
public class AdapterPosters extends RecyclerView.Adapter<AdapterPosters.VH> {


    Context con;
    Cursor MOVIE_DATA;

    public AdapterPosters(Activity con, Cursor data) {
        this.con = con;
        this.MOVIE_DATA = data;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(con).inflate(R.layout.item_main_grid, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        MOVIE_DATA.moveToPosition(position);
        holder.tvMovieName.setText(MOVIE_DATA.getString(Constants.RV_COL_TITLE));
        holder.tvMovieRating.setText(MOVIE_DATA.getString(Constants.RV_COL_VOTE_AVERAGE));
        Picasso.with(con)
                .load(Constants.BASE_IMAGE_URL + Constants.BACKDROP_SIZE + MOVIE_DATA.getString(Constants.RV_COL_POSTER_PATH))
                .into(holder.ivPoster);
    }

    @Override
    public int getItemCount() {
        return MOVIE_DATA == null ? 0 : MOVIE_DATA.getCount();
    }


    public void changeBase(Cursor data) {
        MOVIE_DATA = data;
        notifyDataSetChanged();
    }

    public int getMovieId(int position) {
        MOVIE_DATA.moveToPosition(position);
        return MOVIE_DATA.getInt(Constants.RV_COL_MOVIE_ID);
    }


    class VH extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.item_mg_cv_main)
        CardView cvMain;

        @Bind(R.id.item_mg_iv_movie_poster)
        ImageView ivPoster;

        @Bind(R.id.item_mg_tv_movie_name)
        TextView tvMovieName;
        @Bind(R.id.item_mg_tv_movie_rating)
        TextView tvMovieRating;

        public VH(View view) {
            super(view);
            ButterKnife.bind(this, view);
            cvMain.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(con, DetailActivity.class);
            i.putExtra(DetailActivity.EXTRA_MOVIE_ID, getMovieId(getAdapterPosition()));
            con.startActivity(i);
        }
    }
}
