package it.unimib.fipavonline.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.database.Exclude;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * Classe che rappresenta un Campionato
 */
@Entity
public class Campionato implements Parcelable {

    // Used for Room
    @PrimaryKey
    private long id;
    private String nome;
    private String sesso;

    @ColumnInfo(name = "is_favorite")
    private boolean isFavorite;

    @ColumnInfo(name = "is_synchronized")
    private boolean isSynchronized;

    public Campionato() {
    }

    public Campionato(long id, String nome, String sesso, boolean isFavorite, boolean isSynchronized) {
        this.id = id;
        this.nome = nome;
        this.sesso = sesso;
        this.isFavorite = isFavorite;
        this.isSynchronized = isSynchronized;
    }

    public Campionato(long id, String nome, String sesso) {
        this(id, nome, sesso, false, false);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSesso() {
        return sesso;
    }

    public void setSesso(String sesso) {
        this.sesso = sesso;
    }

    @Exclude
    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
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
        return "Campionato{" +
                "id=" + id +
                ", title='" + nome + '\'' +
                ", sesso='" + sesso + '\'' +
                ", isFavorite=" + isFavorite +
                ", isSynchronized=" + isSynchronized +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Campionato campionato = (Campionato) o;
        return Objects.equals(nome, campionato.nome) && Objects.equals(sesso, campionato.sesso);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, sesso);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.nome);
        dest.writeString(String.valueOf(this.sesso));
        dest.writeByte(this.isFavorite ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isSynchronized ? (byte) 1 : (byte) 0);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readLong();
        this.nome = source.readString();
        this.sesso = source.readString();
        this.isFavorite = source.readByte() != 0;
        this.isSynchronized = source.readByte() != 0;
    }

    protected Campionato(Parcel in) {
        this.id = in.readLong();
        this.nome = in.readString();
        this.sesso = in.readString();
        this.isFavorite = in.readByte() != 0;
        this.isSynchronized = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Campionato> CREATOR = new Parcelable.Creator<Campionato>() {
        @Override
        public Campionato createFromParcel(Parcel source) {
            return new Campionato(source);
        }

        @Override
        public Campionato[] newArray(int size) {
            return new Campionato[size];
        }
    };
}