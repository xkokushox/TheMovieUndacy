package com.freakybyte.movies.control.movies.constructor;

import com.freakybyte.movies.util.ConstantUtils;

/**
 * Created by Jose Torres on 20/10/2016.
 */

public interface GridMoviesPresenter {

    void getMovies(int page);

    void setFilterType(ConstantUtils.movieFilter filter);

    ConstantUtils.movieFilter getFilterType();

    void getMovieDetail(int id);

    void onDestroy();

    void cancelDownload();
}
