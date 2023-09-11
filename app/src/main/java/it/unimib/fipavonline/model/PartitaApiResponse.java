package it.unimib.fipavonline.model;

import android.os.Parcel;

import java.util.List;

/**
 * Class to represent the API response
 */
public class PartitaApiResponse extends PartitaResponse {
    private int status;
    private String info;
    private int results;

    public PartitaApiResponse() {
        super();
    }

    public PartitaApiResponse(int status, String info, int results, List<Partita> partitaList) {
        super(partitaList);
        this.status = status;
        this.info = info;
        this.results = results;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getResults() {
        return results;
    }

    public void setResults(int results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "PartitaApiResponse {" +
                "status=" + status +
                ", info='" + info + '\'' +
                ", results=" + results +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.status);
        dest.writeString(this.info);
        dest.writeInt(this.results);
    }

    public void readFromParcel(Parcel source) {
        super.readFromParcel(source);
        this.status = source.readInt();
        this.info = source.readString();
        this.results = source.readInt();
    }

    protected PartitaApiResponse(Parcel in) {
        super(in);
        this.status = in.readInt();
        this.info = in.readString();
        this.results = in.readInt();
    }

    public static final Creator<PartitaApiResponse> CREATOR = new Creator<PartitaApiResponse>() {
        @Override
        public PartitaApiResponse createFromParcel(Parcel source) {
            return new PartitaApiResponse(source);
        }

        @Override
        public PartitaApiResponse[] newArray(int size) {
            return new PartitaApiResponse[size];
        }
    };
}
