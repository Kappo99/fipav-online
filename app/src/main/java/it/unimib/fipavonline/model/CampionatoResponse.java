package it.unimib.fipavonline.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CampionatoResponse implements Parcelable {

    private boolean isLoading;

    @SerializedName("articles")
    private List<Campionato> campionatoList;

    public CampionatoResponse() {}

    public CampionatoResponse(List<Campionato> campionatoList) {
        this.campionatoList = campionatoList;
    }

    public List<Campionato> getNewsList() {
        return campionatoList;
    }

    public void setNewsList(List<Campionato> campionatoList) {
        this.campionatoList = campionatoList;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    @Override
    public String toString() {
        return "CampionatoResponse{" +
                "campionatoList=" + campionatoList +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.isLoading ? (byte) 1 : (byte) 0);
        dest.writeTypedList(this.campionatoList);
    }

    public void readFromParcel(Parcel source) {
        this.isLoading = source.readByte() != 0;
        this.campionatoList = source.createTypedArrayList(Campionato.CREATOR);
    }

    protected CampionatoResponse(Parcel in) {
        this.isLoading = in.readByte() != 0;
        this.campionatoList = in.createTypedArrayList(Campionato.CREATOR);
    }

    public static final Parcelable.Creator<CampionatoResponse> CREATOR = new Parcelable.Creator<CampionatoResponse>() {
        @Override
        public CampionatoResponse createFromParcel(Parcel source) {
            return new CampionatoResponse(source);
        }

        @Override
        public CampionatoResponse[] newArray(int size) {
            return new CampionatoResponse[size];
        }
    };
}
