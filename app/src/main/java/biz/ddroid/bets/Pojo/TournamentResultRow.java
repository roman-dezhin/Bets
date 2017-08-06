package biz.ddroid.bets.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class TournamentResultRow implements Parcelable {

    private String name;
    private int points;
    private int predictions;
    private int scores;
    private int results;
    private int winner;

    public static final Creator<TournamentResultRow> CREATOR = new Creator<TournamentResultRow>() {
        @Override
        public TournamentResultRow createFromParcel(Parcel in) {
            return new TournamentResultRow(in);
        }

        @Override
        public TournamentResultRow[] newArray(int size) {
            return new TournamentResultRow[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeInt(points);
        parcel.writeInt(predictions);
        parcel.writeInt(scores);
        parcel.writeInt(results);
        parcel.writeInt(winner);
    }

    public TournamentResultRow(String name, int points, int predictions, int scores, int results, int winner) {
        this.name = name;
        this.points = points;
        this.predictions = predictions;
        this.scores = scores;
        this.results = results;
        this.winner = winner;
    }

    protected TournamentResultRow(Parcel in) {
        this.name = in.readString();
        this.points = in.readInt();
        this.predictions = in.readInt();
        this.scores = in.readInt();
        this.results = in.readInt();
        this.winner = in.readInt();
    }

    public String getName() {
        return name;
    }

    public int getPoints() {
        return points;
    }

    public int getPredictions() {
        return predictions;
    }

    public int getScores() {
        return scores;
    }

    public int getResults() {
        return results;
    }

    public int getWinner() {
        return winner;
    }

    public void setWinner(int winner) {
        this.winner = winner;
    }

    @Override
    public String toString() {
        return "TournamentResultRow{" +
                "name='" + name + '\'' +
                ", points=" + points +
                ", predictions=" + predictions +
                ", scores=" + scores +
                ", results=" + results +
                ", winner=" + winner +
                '}';
    }
}
