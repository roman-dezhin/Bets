package biz.ddroid.bets.vos;

public class Match {
    private final int id;
    private final String dateTime;
    private final int tourId;
    private final String tourName;
    private final String stage;
    private final String team1;
    private final String team2;
    private final int scoreTeam1;
    private final int scoreTeam2;
    private final String betTeam1;
    private final String betTeam2;
    private final String imageTeam1;
    private final String imageTeam2;
    private final String city;
    private final int betsCount;

    private Match(MatchBuilder matchBuilder) {
        this.id = matchBuilder.id;
        this.dateTime = matchBuilder.dateTime;
        this.tourId = matchBuilder.tourId;
        this.tourName = matchBuilder.tourName;
        this.stage = matchBuilder.stage;
        this.team1 = matchBuilder.team1;
        this.team2 = matchBuilder.team2;
        this.scoreTeam1 = matchBuilder.scoreTeam1;
        this.scoreTeam2 = matchBuilder.scoreTeam2;
        this.betTeam1 = matchBuilder.betTeam1;
        this.betTeam2 = matchBuilder.betTeam2;
        this.imageTeam1 = matchBuilder.imageTeam1;
        this.imageTeam2 = matchBuilder.imageTeam2;
        this.city = matchBuilder.city;
        this.betsCount = matchBuilder.betsCount;
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

    public String getBetTeam1() {
        return betTeam1;
    }

    public String getBetTeam2() {
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

    public static class MatchBuilder {
        private int id;
        private String dateTime;
        private int tourId;
        private String tourName;
        private String stage;
        private String team1;
        private String team2;
        private int scoreTeam1;
        private int scoreTeam2;
        private String betTeam1;
        private String betTeam2;
        private String imageTeam1;
        private String imageTeam2;
        private String city;
        private int betsCount;

        public MatchBuilder() {
        }

        public MatchBuilder(Match originalMatch) {
            this.id = originalMatch.id;
            this.dateTime = originalMatch.dateTime;
            this.tourId = originalMatch.tourId;
            this.tourName = originalMatch.tourName;
            this.stage = originalMatch.stage;
            this.team1 = originalMatch.team1;
            this.team2 = originalMatch.team2;
            this.scoreTeam1 = originalMatch.scoreTeam1;
            this.scoreTeam2 = originalMatch.scoreTeam2;
            this.betTeam1 = originalMatch.betTeam1;
            this.betTeam2 = originalMatch.betTeam2;
            this.imageTeam1 = originalMatch.imageTeam1;
            this.imageTeam2 = originalMatch.imageTeam2;
            this.city = originalMatch.city;
            this.betsCount = originalMatch.betsCount;
        }

        public MatchBuilder id(int id) {
            this.id = id;
            return this;
        }

        public MatchBuilder dateTime(String dateTime) {
            this.dateTime = dateTime;
            return this;
        }

        public MatchBuilder tourId(int tourId) {
            this.tourId = tourId;
            return this;
        }

        public MatchBuilder tourName(String tourName) {
            this.tourName = tourName;
            return this;
        }

        public MatchBuilder stage(String stage) {
            this.stage = stage;
            return this;
        }

        public MatchBuilder team1(String team1) {
            this.team1 = team1;
            return this;
        }

        public MatchBuilder team2(String team2) {
            this.team2 = team2;
            return this;
        }

        public MatchBuilder scoreTeam1(int scoreTeam1) {
            this.scoreTeam1 = scoreTeam1;
            return this;
        }

        public MatchBuilder scoreTeam2(int scoreTeam2) {
            this.scoreTeam2 = scoreTeam2;
            return this;
        }

        public MatchBuilder betTeam1(String betTeam1) {
            this.betTeam1 = betTeam1;
            return this;
        }

        public MatchBuilder betTeam2(String betTeam2) {
            this.betTeam2 = betTeam2;
            return this;
        }

        public MatchBuilder imageTeam1(String imageTeam1) {
            this.imageTeam1 = imageTeam1;
            return this;
        }

        public MatchBuilder imageTeam2(String imageTeam2) {
            this.imageTeam2 = imageTeam2;
            return this;
        }

        public MatchBuilder city(String city) {
            this.city = city;
            return this;
        }

        public MatchBuilder betsCount(int betsCount) {
            this.betsCount = betsCount;
            return this;
        }

        public Match build() {
            return new Match(this);
        }

    }
}
