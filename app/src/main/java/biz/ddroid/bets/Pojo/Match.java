package biz.ddroid.bets.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class Match implements Parcelable{

    public static final Creator<Match> CREATOR = new Creator<Match>() {
        @Override
        public Match createFromParcel(Parcel in) {
            return new Match(in);
        }

        @Override
        public Match[] newArray(int size) {
            return new Match[size];
        }
    };

    private int id;
    private String dateTime;
    private int tourId;
    private String tourName;
    private String stage;
    private String team1;
    private String team2;
    private int scoreTeam1;
    private int scoreTeam2;
    private int betTeam1;
    private int betTeam2;
    private String imageTeam1;
    private String imageTeam2;
    private String city;
    private int betsCount;
    private int points;

    public Match(int id, String dateTime, int tourId, String tourName, String stage, String team1, String team2, int scoreTeam1, int scoreTeam2, int betTeam1, int betTeam2, String imageTeam1, String imageTeam2, String city, int betsCount, int points) {
        this.id = id;
        this.dateTime = dateTime;
        this.tourId = tourId;
        this.tourName = tourName;
        this.stage = stage;
        this.team1 = team1;
        this.team2 = team2;
        this.scoreTeam1 = scoreTeam1;
        this.scoreTeam2 = scoreTeam2;
        this.betTeam1 = betTeam1;
        this.betTeam2 = betTeam2;
        this.imageTeam1 = imageTeam1;
        this.imageTeam2 = imageTeam2;
        this.city = city;
        this.betsCount = betsCount;
        this.points = points;
    }

    protected Match(Parcel in) {
        id = in.readInt();
        dateTime = in.readString();
        tourId = in.readInt();
        tourName = in.readString();
        stage = in.readString();
        team1 = in.readString();
        team2 = in.readString();
        scoreTeam1 = in.readInt();
        scoreTeam2 = in.readInt();
        betTeam1 = in.readInt();
        betTeam2 = in.readInt();
        imageTeam1 = in.readString();
        imageTeam2 = in.readString();
        city = in.readString();
        betsCount = in.readInt();
        points = in.readInt();
    }

    public int getId() {
        return id;
    }

    public String getDateTime() {
        return dateTime;
    }

    public int getTourId() {
        return tourId;
    }

    public String getTourName() {
        return tourName;
    }

    public String getStage() {
        return stage;
    }

    public String getTeam1() {
        return team1;
    }

    public String getTeam2() {
        return team2;
    }

    public int getScoreTeam1() {
        return scoreTeam1;
    }

    public int getScoreTeam2() {
        return scoreTeam2;
    }

    public int getBetTeam1() {
        return betTeam1;
    }

    public int getBetTeam2() {
        return betTeam2;
    }

    public String getImageTeam1() {
        return imageTeam1;
    }

    public String getImageTeam2() {
        return imageTeam2;
    }

    public String getCity() {
        return city;
    }

    public int getBetsCount() {
        return betsCount;
    }

    public int getPoints() {
        return points;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public void setTourId(int tourId) {
        this.tourId = tourId;
    }

    public void setTourName(String tourName) {
        this.tourName = tourName;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public void setTeam1(String team1) {
        this.team1 = team1;
    }

    public void setTeam2(String team2) {
        this.team2 = team2;
    }

    public void setScoreTeam1(int scoreTeam1) {
        this.scoreTeam1 = scoreTeam1;
    }

    public void setScoreTeam2(int scoreTeam2) {
        this.scoreTeam2 = scoreTeam2;
    }

    public void setBetTeam1(int betTeam1) {
        this.betTeam1 = betTeam1;
    }

    public void setBetTeam2(int betTeam2) {
        this.betTeam2 = betTeam2;
    }

    public void setImageTeam1(String imageTeam1) {
        this.imageTeam1 = imageTeam1;
    }

    public void setImageTeam2(String imageTeam2) {
        this.imageTeam2 = imageTeam2;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setBetsCount(int betsCount) {
        this.betsCount = betsCount;
    }

    public void setPoints(int points) {
        this.points = points;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(dateTime);
        parcel.writeInt(tourId);
        parcel.writeString(tourName);
        parcel.writeString(stage);
        parcel.writeString(team1);
        parcel.writeString(team2);
        parcel.writeInt(scoreTeam1);
        parcel.writeInt(scoreTeam2);
        parcel.writeInt(betTeam1);
        parcel.writeInt(betTeam2);
        parcel.writeString(imageTeam1);
        parcel.writeString(imageTeam2);
        parcel.writeString(city);
        parcel.writeInt(betsCount);
        parcel.writeInt(points);
    }

    @Override
    public String toString() {
        return "Match{" +
                "id=" + id +
                ", dateTime='" + dateTime + '\'' +
                ", tourId=" + tourId +
                ", tourName='" + tourName + '\'' +
                ", stage='" + stage + '\'' +
                ", team1='" + team1 + '\'' +
                ", team2='" + team2 + '\'' +
                ", scoreTeam1=" + scoreTeam1 +
                ", scoreTeam2=" + scoreTeam2 +
                ", betTeam1='" + betTeam1 + '\'' +
                ", betTeam2='" + betTeam2 + '\'' +
                ", imageTeam1='" + imageTeam1 + '\'' +
                ", imageTeam2='" + imageTeam2 + '\'' +
                ", city='" + city + '\'' +
                ", betsCount=" + betsCount +
                ", betsCount=" + points +
                '}';
    }
}
