package it.unimib.fipavonline.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.database.Exclude;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * Classe che rappresenta una Partita
 */
@Entity
public class Partita implements Parcelable {

    // Used for Room
    @PrimaryKey
    private long id;
    private String campionato;
    private String sesso;
    private String data;
    private String locali;
    private String ospiti;
    @SerializedName("set_locali")
    @ColumnInfo(name = "set_locali")
    private int setLocali;
    @SerializedName("set_ospiti")
    @ColumnInfo(name = "set_ospiti")
    private int setOspiti;
    @SerializedName("parziali")
    private String set;

    @ColumnInfo(name = "is_synchronized")
    private boolean isSynchronized;

    public Partita() {}

    public Partita(long id, String campionato, String sesso, String data, String locali, String ospiti, int setLocali, int setOspiti, String set, boolean isSynchronized) {
        this.id = id;
        this.campionato = campionato;
        this.sesso = sesso;
        this.data = data;
        this.locali = locali;
        this.ospiti = ospiti;
        this.setLocali = setLocali;
        this.setOspiti = setOspiti;
        this.set = set;
        this.isSynchronized = isSynchronized;
    }

    public Partita(long id, String campionato, String sesso, String data, String locali, String ospiti, int setLocali, int setOspiti, String set) {
        this(id, campionato, sesso, data, locali, ospiti, setLocali, setOspiti, set, false);
    }

    public long getId() { return id; }

    public void setId(long id) {
        this.id = id;
    }

    public String getCampionato() {
        return campionato;
    }

    public void setCampionato(String campionato) {
        this.campionato = campionato;
    }

    public String getSesso() {
        return sesso;
    }

    public void setSesso(String sesso) {
        this.sesso = sesso;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getLocali() {
        return locali;
    }

    public void setLocali(String locali) {
        this.locali = locali;
    }

    public String getOspiti() {
        return ospiti;
    }

    public void setOspiti(String ospiti) {
        this.ospiti = ospiti;
    }

    public int getSetLocali() {
        return setLocali;
    }

    public void setSetLocali(int setLocali) {
        this.setLocali = setLocali;
    }

    public int getSetOspiti() {
        return setOspiti;
    }

    public void setSetOspiti(int setOspiti) {
        this.setOspiti = setOspiti;
    }

    public String getSet() {
        return set;
    }

    public void setSet(String set) {
        this.set = set;
    }

    @Exclude
    public boolean isSynchronized() {
        return isSynchronized;
    }

    public void setSynchronized(boolean aSynchronized) {
        isSynchronized = aSynchronized;
    }

    @Override
    public String toString() {
        return "Partita{" +
                "id=" + id +
                ", campionato='" + campionato + '\'' +
                ", sesso='" + sesso + '\'' +
                ", data='" + data + '\'' +
                ", locali='" + locali + '\'' +
                ", ospiti='" + ospiti + '\'' +
                ", setLocali=" + setLocali +
                ", setOspiti=" + setOspiti +
                ", set='" + set + '\'' +
                ", isSynchronized=" + isSynchronized +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Partita partita = (Partita) o;
        return Objects.equals(data, partita.data) && Objects.equals(campionato, partita.campionato) &&
                Objects.equals(locali, partita.locali) && Objects.equals(ospiti, partita.ospiti) &&
                Objects.equals(setLocali, partita.setLocali) && Objects.equals(setOspiti, partita.setOspiti) &&
                Objects.equals(set, partita.set) && Objects.equals(sesso, partita.sesso);
    }

    @Override
    public int hashCode() {
        return Objects.hash(campionato, sesso, data, locali, ospiti, setLocali, setOspiti, set);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.campionato);
        dest.writeString(this.sesso);
        dest.writeString(this.data);
        dest.writeString(this.locali);
        dest.writeString(this.ospiti);
        dest.writeString(String.valueOf(this.setLocali));
        dest.writeString(String.valueOf(this.setOspiti));
        dest.writeString(this.set);
        dest.writeByte(this.isSynchronized ? (byte) 1 : (byte) 0);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readLong();
        this.campionato = source.readString();
        this.sesso = source.readString();
        this.data = source.readString();
        this.locali = source.readString();
        this.ospiti = source.readString();
        this.setLocali = source.readInt();
        this.setOspiti = source.readInt();
        this.set = source.readString();
        this.isSynchronized = source.readByte() != 0;
    }

    protected Partita(Parcel in) {
        this.id = in.readLong();
        this.campionato = in.readString();
        this.sesso = in.readString();
        this.data = in.readString();
        this.locali = in.readString();
        this.ospiti = in.readString();
        this.setLocali = in.readInt();
        this.setOspiti = in.readInt();
        this.set = in.readString();
        this.isSynchronized = in.readByte() != 0;
    }

    public static final Creator<Partita> CREATOR = new Creator<Partita>() {
        @Override
        public Partita createFromParcel(Parcel source) {
            return new Partita(source);
        }

        @Override
        public Partita[] newArray(int size) {
            return new Partita[size];
        }
    };
}
