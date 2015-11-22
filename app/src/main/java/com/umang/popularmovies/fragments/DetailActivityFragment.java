package com.umang.popularmovies.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.umang.popularmovies.DetailActivity;
import com.umang.popularmovies.R;
import com.umang.popularmovies.Utility;
import com.umang.popularmovies.data.Constants;
import com.umang.popularmovies.data.Constants.MOVIE_JSON;

import java.util.HashMap;

/**
 * Created by umang on 21/11/15.
 */
public class DetailActivityFragment extends Fragment {

    FragmentActivity con;

    ImageView ivBackdrop, ivPoster;
    TextView tvMovieName, tvReleaseDate, tvVotes, tvRating, tvOverview;
    RelativeLayout rlPoster;
    LinearLayout firstViewInScrollBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        con = getActivity();

        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        ivBackdrop = (ImageView) view.findViewById(R.id.frag_d_iv_backdrop);
        ivPoster = (ImageView) view.findViewById(R.id.frag_d_iv_poster);

        tvMovieName = (TextView) view.findViewById(R.id.frag_d_tv_movie_name);
        tvReleaseDate = (TextView) view.findViewById(R.id.frag_d_tv_release_date);
        tvVotes = (TextView) view.findViewById(R.id.frag_d_tv_votes);
        tvRating = (TextView) view.findViewById(R.id.frag_d_tv_rating);
        tvOverview = (TextView) view.findViewById(R.id.frag_d_tv_overview);

        int width = Utility.getScreenWidth(con);
        int height = (int) (width * Constants.MOVIE_BACKDROP_MULTIPLIER);
        rlPoster = (RelativeLayout) view.findViewById(R.id.frag_d_rl_poster_layout);
        rlPoster.setLayoutParams(new LinearLayout.LayoutParams(width, height));

        firstViewInScrollBar = (LinearLayout) view.findViewById(R.id.frag_d_first_element);

        Intent intent = con.getIntent();
        if (intent != null && intent.hasExtra(DetailActivity.EXTRA_MOVIE_DATA)) {
            HashMap<String, String> row = (HashMap<String, String>) intent.getSerializableExtra(DetailActivity.EXTRA_MOVIE_DATA);
            Picasso.with(con).load(row.get(MOVIE_JSON.BACKDROP)).resize(width, height).into(ivBackdrop);
            Picasso.with(con).load(row.get(MOVIE_JSON.POSTER)).into(ivPoster);
            tvMovieName.setText(row.get(MOVIE_JSON.TITLE));
            tvReleaseDate.setText(Utility.getYear(row.get(MOVIE_JSON.RELEASE_DATE)));
            tvVotes.setText(row.get(MOVIE_JSON.VOTE_COUNT) + " ");
            tvRating.setText(row.get(MOVIE_JSON.VOTE_AVERAGE));
            tvOverview.setText(row.get(MOVIE_JSON.OVERVIEW));
            ((DetailActivity) con).setActionBarTitle(row.get(MOVIE_JSON.TITLE)); // setting the title in the toolbar
        }

        return view;
    }
}
