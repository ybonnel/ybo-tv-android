package fr.ybo.ybotv.android.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import fr.ybo.database.DataBaseException;
import fr.ybo.database.DataBaseHelper;
import fr.ybo.ybotv.android.modele.Channel;
import fr.ybo.ybotv.android.modele.FavoriteChannel;
import fr.ybo.ybotv.android.modele.LastUpdate;
import fr.ybo.ybotv.android.modele.Programme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YboTvDatabase extends DataBaseHelper {

    private static final List<Class<?>> databaseClasses = new ArrayList<Class<?>>() {{
        add(Channel.class);
        add(Programme.class);
        add(LastUpdate.class);
        add(FavoriteChannel.class);
    }};

    private static final String DB_NAME = "YBO_TV";
    private static final int DB_VERSION = 7;


    public YboTvDatabase(Context context) throws DataBaseException {
        super(context, databaseClasses, DB_NAME, DB_VERSION);
    }

    private final Map<Integer, UpgradeDatabase> mapUpgrades = new HashMap<Integer, UpgradeDatabase>() {{
        put(2, new UpgradeDatabase() {
            @Override
            public void upgrade(SQLiteDatabase sqLiteDatabase) {
                getBase().dropDataBase(sqLiteDatabase);
                getBase().createDataBase(sqLiteDatabase);
            }
        });
        put(3, new UpgradeDatabase() {
            @Override
            public void upgrade(SQLiteDatabase sqLiteDatabase) {
                getBase().dropDataBase(sqLiteDatabase);
                getBase().createDataBase(sqLiteDatabase);
            }
        });
        put(4, new UpgradeDatabase() {
            @Override
            public void upgrade(SQLiteDatabase sqLiteDatabase) {
                deleteAll(LastUpdate.class);
                deleteAll(FavoriteChannel.class);
                getBase().getTable(Channel.class).dropTable(sqLiteDatabase);
                getBase().getTable(Channel.class).createTable(sqLiteDatabase);
            }
        });
        put(5, new UpgradeDatabase() {
            @Override
            public void upgrade(SQLiteDatabase sqLiteDatabase) {
                deleteAll(LastUpdate.class);
                deleteAll(FavoriteChannel.class);
            }
        });
        put(6, new UpgradeDatabase() {
            @Override
            public void upgrade(SQLiteDatabase sqLiteDatabase) {
                Channel syfy = new Channel();
                syfy.setId("SCI1");
                syfy.setDisplayName("SYFY UNIVERSAL");
                syfy.setNumero(999);
                syfy.setIcon("syfy.png");
                getBase().insert(sqLiteDatabase, syfy);
            }
        });
        put(7, new UpgradeDatabase() {
            @Override
            public void upgrade(SQLiteDatabase sqLiteDatabase) {
                Channel eurosport = new Channel();
                eurosport.setId("EUR3");
                eurosport.setDisplayName("EUROSPORT");
                eurosport.setNumero(999);
                eurosport.setIcon("eurosport.png");
                getBase().insert(sqLiteDatabase, eurosport);
            }
        });

    }};

    @Override
    protected Map<Integer, UpgradeDatabase> getUpgrades() {
        return mapUpgrades;
    }
}
