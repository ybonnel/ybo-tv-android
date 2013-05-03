package fr.ybo.ybotv.android.lasylist;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import fr.ybo.ybotv.android.R;
import fr.ybo.ybotv.android.modele.Programme;
import fr.ybo.ybotv.android.service.MovieDbService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RatingLoader {


    private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    private Map<String, Integer> mapOfResources = Collections.synchronizedMap(new HashMap<String, Integer>());
    ExecutorService executorService;

    public RatingLoader(Context context) {
        executorService = Executors.newFixedThreadPool(5);
    }

    public void DisplayImage(Programme programme, ImageView imageView) {
        imageViews.put(imageView, programme.getId());
        Integer resource = mapOfResources.get(programme.getId());
        if (resource != null) {
            imageView.setImageResource(resource);
        } else {
            queueRating(programme, imageView);
        }
    }

    private void queueRating(Programme programme, ImageView imageView) {
        RatingToLoad p = new RatingToLoad(programme, imageView);
        executorService.submit(new RatingLoaderRunnable(p));
    }



    private final static Map<Integer, Integer> mapOfRatings = new HashMap<Integer, Integer>(){{
        put(1, R.drawable.rating_1star);
        put(2, R.drawable.rating_2star);
        put(3, R.drawable.rating_3star);
        put(4, R.drawable.rating_4star);
        put(5, R.drawable.rating_5star);
    }};

    private Integer getResource(Programme programme) {
        //from web
        try {
            Float result = MovieDbService.getInstance().getMovieRating(programme);
            if (result != null) {
                return mapOfRatings.get(Math.round(result));
            } else {
                return null;
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
            return null;
        }
    }

    //Task for the queue
    private class RatingToLoad {
        public Programme programme;
        public ImageView imageView;

        public RatingToLoad(Programme programme, ImageView i) {
            this.programme = programme;
            imageView = i;
        }
    }

    class RatingLoaderRunnable implements Runnable {
        RatingToLoad ratingToLoad;

        RatingLoaderRunnable(RatingToLoad ratingToLoad) {
            this.ratingToLoad = ratingToLoad;
        }

        @Override
        public void run() {
            try {
                if (imageViewReused(ratingToLoad))
                    return;
                Integer resource = getResource(ratingToLoad.programme);
                if (resource != null) {
                    mapOfResources.put(ratingToLoad.programme.getId(), resource);
                }
                if (imageViewReused(ratingToLoad))
                    return;
                BitmapDisplayer bd = new BitmapDisplayer(resource, ratingToLoad);
                Activity a = (Activity) ratingToLoad.imageView.getContext();
                a.runOnUiThread(bd);
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    boolean imageViewReused(RatingToLoad ratingToLoad) {
        String tag = imageViews.get(ratingToLoad.imageView);
        return tag == null || !tag.equals(ratingToLoad.programme.getId());
    }

    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable {
        Integer resource;
        RatingToLoad ratingToLoad;

        public BitmapDisplayer(Integer resource, RatingToLoad p) {
            this.resource = resource;
            ratingToLoad = p;
        }

        public void run() {
            if (imageViewReused(ratingToLoad))
                return;
            if (resource != null) {
                ratingToLoad.imageView.setImageResource(resource);
            } else {
                ratingToLoad.imageView.setVisibility(View.GONE);
            }
        }
    }

}
