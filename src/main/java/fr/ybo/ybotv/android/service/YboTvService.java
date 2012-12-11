package fr.ybo.ybotv.android.service;


import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import fr.ybo.ybotv.android.exception.YboTvErreurReseau;
import fr.ybo.ybotv.android.exception.YboTvException;
import fr.ybo.ybotv.android.modele.Channel;
import fr.ybo.ybotv.android.modele.Programme;
import fr.ybo.ybotv.android.util.HttpUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.util.List;

public class YboTvService extends HttpService {

    private static final YboTvService instance = new YboTvService();

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
