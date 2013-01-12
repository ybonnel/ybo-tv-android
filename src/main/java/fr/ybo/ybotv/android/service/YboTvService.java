package fr.ybo.ybotv.android.service;


import com.google.gson.reflect.TypeToken;
import fr.ybo.ybotv.android.exception.YboTvErreurReseau;
import fr.ybo.ybotv.android.modele.Channel;
import fr.ybo.ybotv.android.modele.Programme;

import java.util.List;

public class YboTvService extends HttpService {

    private static final YboTvService instance = new YboTvService();

    //public static final String SERVEUR = "http://192.168.1.85:8080/ybo-tv-server/";
    public static final String SERVEUR = "http://ybo-tv.ybonnel.fr/";

    private static final String CHANNEL_SERVICE_URL = "data/channel/";

    private static final String PROGRAMME_SERVICE_URL = "data/programme/";

    private static final String CHANNEL_PARAMETER = "channel/";

    private YboTvService() {
    }

    public static YboTvService getInstance() {
        return instance;
    }

    public List<Channel> getChannels() throws YboTvErreurReseau {
        return getObjects(SERVEUR + CHANNEL_SERVICE_URL, new TypeToken<List<Channel>>(){});
    }

    public List<Programme> getProgrammes(Channel channel) throws YboTvErreurReseau {
        return getObjects(SERVEUR + PROGRAMME_SERVICE_URL + CHANNEL_PARAMETER + channel.getId(), new TypeToken<List<Programme>>(){});
    }

    public List<Programme> getProgrammes(String channelId) throws YboTvErreurReseau {
        return getObjects(SERVEUR + PROGRAMME_SERVICE_URL + CHANNEL_PARAMETER + channelId, new TypeToken<List<Programme>>(){});
    }
}
