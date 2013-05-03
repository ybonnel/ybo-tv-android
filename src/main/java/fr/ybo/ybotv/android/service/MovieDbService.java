package fr.ybo.ybotv.android.service;


import android.util.Log;
import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;
import com.omertron.themoviedbapi.model.MovieDb;
import fr.ybo.ybotv.android.YboTvApplication;
import fr.ybo.ybotv.android.exception.YboTvErreurReseau;
import fr.ybo.ybotv.android.modele.Programme;
import fr.ybo.ybotv.android.util.AsciiUtils;

import java.util.List;

public class MovieDbService {

    private static final MovieDbService instance = new MovieDbService();

    public static MovieDbService getInstance() {
        return instance;
    }

    public MovieDb getMovie(Programme programme, TheMovieDbApi api) throws MovieDbException {
        List<MovieDb> movies = api.searchMovie(AsciiUtils.convertNonAscii(programme.getTitle()), -1, null, true, -1);
        return getCurrentMovie(programme, movies);
    }

    public TheMovieDbApi getTheMovieDbApi() throws MovieDbException {
        return new TheMovieDbApi("7adbba8f19ab75647fe8ec087f3ba37b");
    }

    public Float getMovieRating(Programme programme) throws YboTvErreurReseau {
        if (programme == null || programme.getTitle() == null || programme.getDate() == null) {
            return null;
        }

        try {
            return getRatingOfMovie(getMovie(programme, getTheMovieDbApi()));
        } catch (MovieDbException e) {
            throw new YboTvErreurReseau(e);
        }
    }

    private MovieDb getCurrentMovie(Programme programme, List<MovieDb> movies) {
        int programmeDate = Integer.parseInt(programme.getDate());
        Log.d(YboTvApplication.TAG, "Movies : " + movies);
        int currentDate = 0;
        MovieDb currentMovie = null;
        for (MovieDb movie : movies) {
            if (movie.getReleaseDate() != null && movie.getReleaseDate().length() >= 4) {
                int movieDate = Integer.parseInt(movie.getReleaseDate().substring(0, 4));
                if (Math.abs(movieDate - programmeDate) < Math.abs(currentDate - programmeDate)) {
                    currentDate = movieDate;
                    currentMovie = movie;
                }
            }
        }
        Log.d(YboTvApplication.TAG, "Selected movie : " + currentMovie);
        return currentMovie;
    }

    private Float getRatingOfMovie(MovieDb currentMovie) {
        if (currentMovie == null) {
            return null;
        }
        Float rating = currentMovie.getVoteAverage() / 2;
        Log.d(YboTvApplication.TAG, "Rating of " + currentMovie.getTitle() + " : " + rating);
        return rating;
    }
}
