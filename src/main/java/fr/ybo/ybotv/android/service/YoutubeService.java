package fr.ybo.ybotv.android.service;


import com.google.gson.reflect.TypeToken;
import fr.ybo.ybotv.android.exception.YboTvErreurReseau;
import fr.ybo.ybotv.android.service.youtube.model.YouTubeAnswer;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class YouTubeService extends HttpService {

    private static final YouTubeService instance = new YouTubeService();

    private static String WEB_API_KEY = "AIzaSyBcWoSLALvBbxha0SKSzKtU7aeydVy9ua8";

    private static String YOUTUBE_BASE_URL = "https://www.googleapis.com/youtube/v3/search?key="
            + WEB_API_KEY + "&part=id&q=";

    private YouTubeService() {
    }

    public static YouTubeService getInstance() {
        return instance;
    }

    public String getFirstResult(String query) throws YboTvErreurReseau {

        try {
            YouTubeAnswer answer = getObjects(YOUTUBE_BASE_URL + URLEncoder.encode(query, "UTF-8"),
                    new TypeToken<YouTubeAnswer>() {});
            if (answer != null && !answer.getItems().isEmpty() && answer.getItems().get(0).getId() != null) {
                return answer.getItems().get(0).getId().getVideoId();
            }
            return null;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

    }


}
