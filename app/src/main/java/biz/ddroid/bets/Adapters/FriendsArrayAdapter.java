package biz.ddroid.bets.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import biz.ddroid.bets.R;
import biz.ddroid.bets.pojo.Friend;

public class FriendsArrayAdapter extends ArrayAdapter<Friend> {
    private final List<Friend> friends;
    private final Context context;

    public FriendsArrayAdapter(Context context, List<Friend> objects) {
        super(context, R.layout.profile_friendlist_row, objects);
        this.friends = objects;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.profile_friendlist_row, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.name);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.avatar);
        textView.setText(friends.get(position).getName());
        if (!friends.get(position).getAvatar().equals("")) Picasso.with(context).load(friends.get(position).getAvatar()).resize(32, 32).into(imageView);

        return rowView;
    }
}
