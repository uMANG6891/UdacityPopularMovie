package com.umang.popularmovies.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.umang.popularmovies.R;
import com.umang.popularmovies.utility.Constants;
import com.umang.popularmovies.utility.Utility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by umang on 20/11/15.
 */
public class AdapterPosters extends BaseAdapter {

    Context con;
    ArrayList<HashMap<String, String>> MOVIE_DATA;
    private int SCREEN_WIDTH;
    private int width;
    private int height;

    public AdapterPosters(Activity con, ArrayList<HashMap<String, String>> data) {
        this.con = con;
        this.MOVIE_DATA = data;
        this.width = Utility.getScreenWidth(con) / con.getResources().getInteger(R.integer.main_grid_columns);
        height = (int) (width * Constants.MOVIE_POSTER_MULTIPLIER);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(con);
            imageView.setLayoutParams(new GridView.LayoutParams(width, height));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }
        HashMap<String, String> row = MOVIE_DATA.get(position);
        Picasso.with(con).load(row.get(Constants.MOVIE_JSON.POSTER)).resize(width, height).into(imageView);
        return imageView;
    }

    @Override
    public int getCount() {
        return MOVIE_DATA == null ? 0 : MOVIE_DATA.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void changeBase(ArrayList<HashMap<String, String>> data) {
        MOVIE_DATA = data;
    }

    public Serializable getOneMovieData(int position) {
        HashMap<String, String> row = MOVIE_DATA.get(position);
        return row;
    }
}
