package it.unimib.fipavonline.model;

import android.os.Parcel;

import java.util.List;

/**
 * Class to represent the API response of NewsAPI.org (https://newsapi.org)
 * associated with the endpoint "Top headlines" - /v2/top-headlines.
 */
public class CampionatoApiResponse extends CampionatoResponse {
    private String status;
    private int totalResults;

    public CampionatoApiResponse() {
        super();
    }

    public CampionatoApiResponse(String status, int totalResults, List<Campionato> articles) {
        super(articles);
        this.status = status;
        this.totalResults = totalResults;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    @Override
    public String toString() {
        return "CampionatoApiResponse{" +
                "status='" + status + '\'' +
                ", totalResults=" + totalResults +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.status);
        dest.writeInt(this.totalResults);
    }

    public void readFromParcel(Parcel source) {
        super.readFromParcel(source);
        this.status = source.readString();
        this.totalResults = source.readInt();
    }

    protected CampionatoApiResponse(Parcel in) {
        super(in);
        this.status = in.readString();
        this.totalResults = in.readInt();
    }

    public static final Creator<CampionatoApiResponse> CREATOR = new Creator<CampionatoApiResponse>() {
        @Override
        public CampionatoApiResponse createFromParcel(Parcel source) {
            return new CampionatoApiResponse(source);
        }

        @Override
        public CampionatoApiResponse[] newArray(int size) {
            return new CampionatoApiResponse[size];
        }
    };
}
