package fr.ybo.ybotv.android.service.allocine.modele;

import java.util.ArrayList;
import java.util.List;

public class SearchFeed {

    private List<Movie> movie;

    public List<Movie> getMovie() {
        if (movie == null) {
            movie = new ArrayList<Movie>();
        }
        return movie;
    }

    @Override
    public String toString() {
        return "SearchFeed{" +
                "movie=" + movie +
                '}';
    }
}
