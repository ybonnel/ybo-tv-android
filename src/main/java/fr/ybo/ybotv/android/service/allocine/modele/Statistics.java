package fr.ybo.ybotv.android.service.allocine.modele;


public class Statistics {

    private Float pressRating;
    private Float userRating;

    public Float getPressRating() {
        return pressRating;
    }

    public Float getUserRating() {
        return userRating;
    }

    @Override
    public String toString() {
        return "Statistics{" +
                "pressRating=" + pressRating +
                ", userRating=" + userRating +
                '}';
    }
}
