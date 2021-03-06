package com.freakybyte.movies.control.adapter;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.imagepipeline.request.BasePostprocessor;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.imagepipeline.request.Postprocessor;
import com.freakybyte.movies.MoviesApplication;
import com.freakybyte.movies.R;
import com.freakybyte.movies.data.dao.FavoriteDao;
import com.freakybyte.movies.listener.RecyclerViewListener;
import com.freakybyte.movies.model.movie.MovieResponseModel;
import com.freakybyte.movies.util.DebugUtils;
import com.freakybyte.movies.util.ImageUtils;

import java.util.ArrayList;

/**
 * Created by Jose Torres on 20/10/2016.
 */

public class MoviesRecyclerViewAdapter extends RecyclerView.Adapter<MoviesItemHolder> {
    private ArrayList<MovieResponseModel> aGallery;
    private RecyclerViewListener mListener;
    private boolean sendLastItemVisible;
    private int iListIndex;

    private FavoriteDao mFavoriteDao;

    public MoviesRecyclerViewAdapter(RecyclerViewListener mListener) {
        this.aGallery = new ArrayList<>();
        this.mListener = mListener;
        iListIndex = 0;
        mFavoriteDao = FavoriteDao.getInstance();
    }

    @Override
    public MoviesItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, null);
        MoviesItemHolder rcv = new MoviesItemHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(final MoviesItemHolder viewHolder, int position) {
        final MovieResponseModel mImage = aGallery.get(position);

        viewHolder.getTvMovieTitle().setText(mImage.getOriginalTitle());

        viewHolder.getWrapperMovieResume().setBackgroundColor(MoviesApplication.getInstance().getResources().getColor(R.color.white));
        viewHolder.getTvMovieTitle().setTextColor(MoviesApplication.getInstance().getResources().getColor(R.color.primaryText));

        viewHolder.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(mImage);
            }
        });

        viewHolder.getIbMovieFavorite().setImageResource(mFavoriteDao.isMovieFavorite(mImage.getId()) ? R.drawable.ic_favorite_selected : R.drawable.ic_favorite_no_selected);

        viewHolder.getIbMovieFavorite().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFavoriteDao.isMovieFavorite(mImage.getId())) {
                    mFavoriteDao.deleteFavorite(mImage.getId());
                    viewHolder.getIbMovieFavorite().setImageResource(R.drawable.ic_favorite_no_selected);
                } else {
                    mFavoriteDao.insertFavoriteMovie(mImage);
                    viewHolder.getIbMovieFavorite().setImageResource(R.drawable.ic_favorite_selected);
                }
            }
        });

        final Postprocessor redMeshPostprocessor = new BasePostprocessor() {

            @Override
            public void process(Bitmap bitmap) {
                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                    @SuppressWarnings("ResourceType")
                    @Override
                    public void onGenerated(Palette palette) {
                        if (palette != null) {
                            Palette.Swatch dominantSwatch = palette.getDominantSwatch();
                            if (dominantSwatch != null) {
                                viewHolder.getWrapperMovieResume().setBackgroundColor(dominantSwatch.getRgb());
                                viewHolder.getTvMovieTitle().setTextColor(dominantSwatch.getTitleTextColor());
                            }
                        }
                    }
                });
            }
        };

        final ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(ImageUtils.getMovieUri(true, mImage.getPosterPath())))
                .setPostprocessor(redMeshPostprocessor)
                .build();

        final PipelineDraweeController controller = (PipelineDraweeController)
                Fresco.newDraweeControllerBuilder()
                        .setImageRequest(request)
                        .setOldController(viewHolder.getImagePoster().getController())
                        .build();

        viewHolder.getImagePoster().setController(controller);

        if (position == aGallery.size() - 1 && sendLastItemVisible) {
            sendLastItemVisible = false;
            mListener.onLastItemVisible();
        }

        if (mImage.getVideo())
            DebugUtils.logDebug("Has Vieos:: " + mImage.getId());

        iListIndex = position;
    }

    @Override
    public int getItemCount() {
        return this.aGallery.size();
    }

    public void clearItems(ArrayList<MovieResponseModel> aImages) {
        sendLastItemVisible = true;
        aGallery.clear();
        swapItems(aImages);
    }

    public ArrayList<MovieResponseModel> getMovieList() {
        return aGallery;
    }

    public int getListIndex() {
        return iListIndex;
    }

    public void swapItems(ArrayList<MovieResponseModel> aImages) {
        sendLastItemVisible = true;
        aGallery.addAll(aImages);
        DebugUtils.logDebug("TotalItems:: ", aGallery.size());
        notifyDataSetChanged();
    }

    public boolean isEmpty() {
        return aGallery.isEmpty();
    }
}