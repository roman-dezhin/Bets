package biz.ddroid.bets.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class TournamentResult implements Parcelable{

    private int tourId;
    private String tourName;
    private List<TournamentResultRow> results;
    private int isFinished;

    public static final Creator<TournamentResult> CREATOR = new Creator<TournamentResult>() {
        @Override
        public TournamentResult createFromParcel(Parcel in) {
            return new TournamentResult(in);
        }

        @Override
        public TournamentResult[] newArray(int size) {
            return new TournamentResult[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(tourId);
        parcel.writeString(tourName);
        parcel.writeList(results);
        parcel.writeInt(isFinished);
    }

    public TournamentResult(int tourId, String tourName, ArrayList<TournamentResultRow> results, int isFinished) {
        this.tourId = tourId;
        this.tourName = tourName;
        this.results = results;
        this.isFinished = isFinished;
    }

    protected TournamentResult(Parcel in) {
        this.tourId = in.readInt();
        this.tourName = in.readString();
        this.results = in.readArrayList(TournamentResultRow.class.getClassLoader());
        this.isFinished = in.readInt();
    }

    public int getTourId() {
        return tourId;
    }

    public String getTourName() {
        return tourName;
    }

    public List<TournamentResultRow> getResults() {
        return results;
    }

    public int getIsFinished() {
        return isFinished;
    }
}
