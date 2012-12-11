package fr.ybo.ybotv.android.service.allocine.modele;


public class RootSearch {

    private SearchFeed feed;

    public SearchFeed getFeed() {
        return feed;
    }

    @Override
    public String toString() {
        return "RootSearch{" +
                "feed=" + feed +
                '}';
    }
}
