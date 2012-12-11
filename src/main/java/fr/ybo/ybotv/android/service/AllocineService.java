package fr.ybo.ybotv.android.service;


import android.util.Log;
import com.google.gson.reflect.TypeToken;
import fr.ybo.ybotv.android.YboTvApplication;
import fr.ybo.ybotv.android.exception.YboTvErreurReseau;
import fr.ybo.ybotv.android.modele.Programme;
import fr.ybo.ybotv.android.service.allocine.modele.Movie;
import fr.ybo.ybotv.android.service.allocine.modele.RootSearch;

import java.util.List;

public class AllocineService extends HttpService {

    private final static AllocineService instance = new AllocineService();

    public static AllocineService getInstance() {
        return instance;
    }

    private final static String BASE_URL = "http://api.allocine.fr/rest/v3/search?partner=YW5kcm9pZC12M3M";
    private final static String BEGIN_MOVIE_QUERY_URL = BASE_URL + "&filter=movie&count=50&page=1&format=json&q=";

    public Float getMovieRating(Programme programme) throws YboTvErreurReseau {
        if (programme == null || programme.getTitle() == null || programme.getDate() == null) {
            return null;
        }
        String url = BEGIN_MOVIE_QUERY_URL + programme.getTitle();

        RootSearch result = getObjects(url, new TypeToken<RootSearch>(){});

        Log.d(YboTvApplication.TAG, "Result : " + result.toString());

        Movie currentMovie = getCurrentMovie(programme, result);

        return getRatingOfMovie(currentMovie);
    }

    private Movie getCurrentMovie(Programme programme, RootSearch result) {
        Movie currentMovie = null;
        int currentDate = 0;
        int programmeDate = Integer.parseInt(programme.getDate());
        if (result.getFeed() != null) {
            for (Movie movie : result.getFeed().getMovie()) {
                int movieDate = movie.getProductionYear();
                if (Math.abs(movieDate - programmeDate) < Math.abs(currentDate - programmeDate)) {
                    currentDate = movieDate;
                    currentMovie = movie;
                }
            }
        }
        Log.d(YboTvApplication.TAG, "Selected movie : " + currentMovie);
        return currentMovie;
    }

    private Float getRatingOfMovie(Movie currentMovie) {
        Float movieRating = null;
        if (currentMovie != null && currentMovie.getStatistics() != null) {
            Log.d(YboTvApplication.TAG, "PressRating : " + currentMovie.getStatistics().getPressRating());
            float sommeRating = currentMovie.getStatistics().getPressRating() == null ? 0.0f : currentMovie.getStatistics().getPressRating();
            int nbRating = currentMovie.getStatistics().getPressRating() == null ? 0 : 1;
            Log.d(YboTvApplication.TAG, "SommeRating=" + sommeRating +", nbRating=" + nbRating);

            Log.d(YboTvApplication.TAG, "UserRating : " + currentMovie.getStatistics().getUserRating());
            sommeRating += currentMovie.getStatistics().getUserRating() == null ? 0.0f : currentMovie.getStatistics().getUserRating();
            nbRating += currentMovie.getStatistics().getUserRating() == null ? 0 : 1;
            Log.d(YboTvApplication.TAG, "SommeRating=" + sommeRating +", nbRating=" + nbRating);

            if (nbRating != 0) {
                movieRating = sommeRating / ((float) nbRating);
                Log.d(YboTvApplication.TAG, "MovieRating : " + movieRating);
            }
        }
        return movieRating;
    }


}
