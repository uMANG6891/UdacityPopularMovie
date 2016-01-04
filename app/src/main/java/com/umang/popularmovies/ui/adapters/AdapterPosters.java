package com.umang.popularmovies.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.umang.popularmovies.R;
import com.umang.popularmovies.utility.Constants;
import com.umang.popularmovies.utility.Utility;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by umang on 20/11/15.
 */
public class AdapterPosters extends RecyclerView.Adapter<AdapterPosters.VH> {

    Context con;
    Cursor MOVIE_DATA;

    int baseColor;

    public AdapterPosters(Activity con, Cursor data) {
        this.con = con;
        this.MOVIE_DATA = data;
        baseColor = ContextCompat.getColor(con, R.color.colorAccent);
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(con).inflate(R.layout.item_main_grid, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(final VH holder, int position) {
        MOVIE_DATA.moveToPosition(position);
        holder.tvMovieName.setText(MOVIE_DATA.getString(Constants.RV_COL_MSB_TITLE));
        holder.tvMovieRating.setText(Utility.parseRating(MOVIE_DATA.getString(Constants.RV_COL_MSB_VOTE_AVERAGE)));
        Glide.with(con)
                .load(Constants.BASE_IMAGE_URL + Constants.BACKDROP_SIZE + MOVIE_DATA.getString(Constants.RV_COL_MSB_POSTER_PATH))
                .asBitmap()
                .into(new BitmapImageViewTarget(holder.ivPoster) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        super.onResourceReady(resource, glideAnimation);
                        holder.ivPoster.setImageBitmap(resource);
                        Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
                            public void onGenerated(Palette p) {
                                holder.llPaletteBackground.setBackgroundColor(p.getVibrantColor(baseColor));
                            }
                        });
                    }
                });
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
        return MOVIE_DATA.getInt(Constants.RV_COL_MSB_MOVIE_ID);
    }


    class VH extends RecyclerView.ViewHolder {

        @Bind(R.id.item_mg_cv_main)
        CardView cvMain;
        @Bind(R.id.item_mg_ll_palette_background)
        LinearLayout llPaletteBackground;

        @Bind(R.id.item_mg_iv_movie_poster)
        ImageView ivPoster;

        @Bind(R.id.item_mg_tv_movie_name)
        TextView tvMovieName;
        @Bind(R.id.item_mg_tv_movie_rating)
        TextView tvMovieRating;

        public VH(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
