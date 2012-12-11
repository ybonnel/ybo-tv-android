package fr.ybo.ybotv.android.modele;

import android.os.Parcel;
import android.os.Parcelable;
import fr.ybo.database.annotation.Column;
import fr.ybo.database.annotation.Entity;
import fr.ybo.database.annotation.PrimaryKey;
import fr.ybo.ybotv.android.R;
import fr.ybo.ybotv.android.service.YboTvService;

import java.io.Serializable;

@Entity
public class Channel implements Serializable, Comparable<Channel>, Parcelable {

    @Column
    @PrimaryKey
    private String id;
    @Column
    private String displayName;
    @Column
    private String icon;

    private transient boolean favorite;

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public int getIconResource() {
        if (icon != null) {
            try {
                String resourceName = icon.split("\\.")[0];
                if (Character.isDigit(icon.charAt(0))) {
                    resourceName = "_" + resourceName;
                }
                return (Integer)R.drawable.class.getDeclaredField(resourceName).get(null);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    public int getNotifIconResource() {
        if (icon != null) {
            try {
                String resourceName = icon.split("\\.")[0];
                if (Character.isDigit(icon.charAt(0))) {
                    resourceName = "_" + resourceName;
                }
                resourceName = "notif_" + resourceName;
                return (Integer)R.drawable.class.getDeclaredField(resourceName).get(null);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id='" + id + '\'' +
                ", displayName='" + displayName + '\'' +
                ", icon='" + icon + '\'' +
                '}';
    }


    @Override
    public int compareTo(Channel other) {
        int id1 = Integer.parseInt(getId());
        int id2 = Integer.parseInt(other.getId());
        return (id1 == id2) ? 0 : (id1 < id2) ? -1 : 1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(displayName);
        parcel.writeString(icon);
    }

    public Channel() {
    }

    public Channel(Parcel in) {
        id = in.readString();
        displayName = in.readString();
        icon = in.readString();
    }

    public static final Creator<Channel> CREATOR = new Creator<Channel>() {
        @Override
        public Channel createFromParcel(Parcel parcel) {
            return new Channel(parcel);
        }

        @Override
        public Channel[] newArray(int size) {
            return new Channel[size];
        }
    };
}
