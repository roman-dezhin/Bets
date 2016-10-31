package biz.ddroid.bets.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class Friend implements Parcelable {
    private int uid;
    private String name;
    private String avatar;

    protected Friend(Parcel in) {
        this.uid = in.readInt();
        this.name = in.readString();
        this.avatar = in.readString();
    }

    public Friend(int uid, String name, String avatar) {
        this.uid = uid;
        this.name = name;
        this.avatar = avatar;
    }

    public static final Creator<Friend> CREATOR = new Creator<Friend>() {
        @Override
        public Friend createFromParcel(Parcel in) {
            return new Friend(in);
        }

        @Override
        public Friend[] newArray(int size) {
            return new Friend[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }
}
