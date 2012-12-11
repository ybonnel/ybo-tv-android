package fr.ybo.ybotv.android.service.allocine.modele;

public class Movie {

    private String originalTitle;
    private int productionYear;
    private Statistics statistics;

    public String getOriginalTitle() {
        return originalTitle;
    }

    public int getProductionYear() {
        return productionYear;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "originalTitle='" + originalTitle + '\'' +
                ", productionYear='" + productionYear + '\'' +
                ", statistics=" + statistics +
                '}';
    }
}
