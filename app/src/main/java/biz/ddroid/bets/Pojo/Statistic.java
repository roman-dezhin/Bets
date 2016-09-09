package biz.ddroid.bets.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Statistic implements Parcelable{

    private String name;
    private int points;
    private int predictions;
    private int scores;
    private int results;
    private int percent;
    private int wins;

    protected Statistic(Parcel in) {
        name = in.readString();
        points = in.readInt();
        predictions = in.readInt();
        scores = in.readInt();
        results = in.readInt();
        percent = in.readInt();
        wins = in.readInt();
    }

    public static final Creator<Statistic> CREATOR = new Creator<Statistic>() {
        @Override
        public Statistic createFromParcel(Parcel in) {
            return new Statistic(in);
        }

        @Override
        public Statistic[] newArray(int size) {
            return new Statistic[size];
        }
    };

    public Statistic(String name, int points, int predictions, int scores, int results, int percent, int wins) {
        this.name = name;
        this.points = points;
        this.predictions = predictions;
        this.scores = scores;
        this.results = results;
        this.percent = percent;
        this.wins = wins;
    }

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
        parcel.writeInt(percent);
        parcel.writeInt(wins);
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

    public int getPercent() {
        return percent;
    }

    public int getWins() {
        return wins;
    }

    @Override
    public String toString() {
        return "Statistic{" +
                "name='" + name + '\'' +
                ", points=" + points +
                ", predictions=" + predictions +
                ", scores=" + scores +
                ", results=" + results +
                ", percent=" + percent +
                ", wins=" + wins +
                '}';
    }
}
