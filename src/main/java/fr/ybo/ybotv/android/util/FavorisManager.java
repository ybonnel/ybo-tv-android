package fr.ybo.ybotv.android.util;


import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import fr.ybo.ybotv.android.R;
import fr.ybo.ybotv.android.YboTvApplication;
import fr.ybo.ybotv.android.database.YboTvDatabase;
import fr.ybo.ybotv.android.modele.Channel;
import fr.ybo.ybotv.android.modele.FavoriteChannel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavorisManager {

    private static final String FILE_NAME = "ybo-tv-favorites.txt";

    private Context context;
    private YboTvDatabase database;

    public FavorisManager(Context context, YboTvDatabase database) {
        this.context = context;
        this.database = database;
    }

    public String export() {
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Toast.makeText(context, R.string.exportErreurSd, Toast.LENGTH_LONG).show();
            return context.getString(R.string.exportErreurSd);
        }

        File outputFile = openCsvFile();
        if (outputFile.exists()) {
            outputFile.delete();
        }

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
            try {
                for (FavoriteChannel channel : database.selectAll(FavoriteChannel.class)) {
                    writer.write(channel.getChannel());
                    writer.write("\n");
                }
            } finally {
                writer.close();
            }
        } catch (Exception erreurEcriture) {
            Log.e(YboTvApplication.TAG, "Erreur à l'écriture du fichier", erreurEcriture);
            Toast.makeText(context, R.string.exportErreurSd, Toast.LENGTH_LONG).show();
            return context.getString(R.string.exportErreurSd);
        }
        return context.getString(R.string.exportResult, currentFileName);
    }

    public String load() {
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Toast.makeText(context, R.string.importErreurSd, Toast.LENGTH_LONG).show();
            return context.getString(R.string.importErreurSd);
        }

        BufferedReader bufReader = openCsvFileAsBufReader();
        if (bufReader == null) {
            return context.getString(R.string.importErreurFichierNonPresent, currentFileName);
        }
        try {
            Set<String> idInDb = new HashSet<String>();
            for (Channel channel : database.selectAll(Channel.class)) {
                idInDb.add(channel.getId());
            }
            List<FavoriteChannel> newChannels = new ArrayList<FavoriteChannel>();
            String ligne = bufReader.readLine();
            while (ligne != null) {
                if (idInDb.contains(ligne)) {
                    newChannels.add(new FavoriteChannel(ligne));
                }
                ligne = bufReader.readLine();
            }

            if (newChannels.isEmpty()) {
                Toast.makeText(context, R.string.noChannelsToImport, Toast.LENGTH_SHORT).show();
                return context.getString(R.string.noChannelsToImport);
            } else {
                database.deleteAll(FavoriteChannel.class);
                for (FavoriteChannel channel : newChannels) {
                    database.insert(channel);
                }
                return context.getString(R.string.importResult);
            }
        } catch (Exception exception) {
            Log.e(YboTvApplication.TAG, "Une erreur pendant l'import : ", exception);
            Toast.makeText(context, context.getString(R.string.importErreurGenerique, exception.getMessage()),
                    Toast.LENGTH_LONG).show();
            return context.getString(R.string.importErreurGenerique, exception.getMessage());
        } finally {
            closeBufReader(bufReader);
        }
    }

    private void closeBufReader(BufferedReader bufReader) {
        try {
            bufReader.close();
        } catch (IOException ignore) {
        }
    }

    private BufferedReader openCsvFileAsBufReader() {
        try {
            return new BufferedReader(new FileReader(openCsvFile()));
        } catch (FileNotFoundException e) {
            Toast.makeText(
                    context,
                    context.getString(R.string.importErreurFichierNonPresent, currentFileName), Toast.LENGTH_LONG).show();
            return null;
        }
    }

    private String currentFileName;

    private File openCsvFile() {

        File repertoire;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            repertoire = context.getExternalFilesDir(null);
            Log.d(YboTvApplication.TAG, "Répertoire : " + repertoire);
        } else {
            File root = Environment.getExternalStorageDirectory();
            repertoire = new File(root, "ybo-tv");
            if (!repertoire.exists()) {
                boolean result = repertoire.mkdir();
                Log.d(YboTvApplication.TAG, "Result of mkdir : " + result);
            }
        }
        Log.d(YboTvApplication.TAG, "Repertoire  : " + repertoire.getAbsolutePath());
        File file = new File(repertoire, FILE_NAME);
        currentFileName = file.getAbsolutePath();
        return file;
    }

}

