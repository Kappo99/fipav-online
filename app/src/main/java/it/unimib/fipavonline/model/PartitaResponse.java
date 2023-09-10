package it.unimib.fipavonline.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PartitaResponse implements Parcelable {

    private boolean isLoading;

    @SerializedName("body")
    private List<Partita> partitaList;

    public PartitaResponse() {}

    public PartitaResponse(List<Partita> partitaList) {
        this.partitaList = partitaList;
    }

    public List<Partita> getPartitaList() {
        return partitaList;
    }

    public void setPartitaList(List<Partita> partitaList) {
        this.partitaList = partitaList;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    @Override
    public String toString() {
        return "PartitaResponse{" +
                "partitaList=" + partitaList +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.isLoading ? (byte) 1 : (byte) 0);
        dest.writeTypedList(this.partitaList);
    }

    public void readFromParcel(Parcel source) {
        this.isLoading = source.readByte() != 0;
        this.partitaList = source.createTypedArrayList(Partita.CREATOR);
    }

    protected PartitaResponse(Parcel in) {
        this.isLoading = in.readByte() != 0;
        this.partitaList = in.createTypedArrayList(Partita.CREATOR);
    }

    public static final Creator<PartitaResponse> CREATOR = new Creator<PartitaResponse>() {
        @Override
        public PartitaResponse createFromParcel(Parcel source) {
            return new PartitaResponse(source);
        }

        @Override
        public PartitaResponse[] newArray(int size) {
            return new PartitaResponse[size];
        }
    };
}
