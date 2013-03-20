package edu.neu.madcourse.zhongjiemao.persistent_boggle.arrayadapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import edu.neu.madcourse.zhongjiemao.R;
import edu.neu.madcourse.zhongjiemao.gsonhelper.entities.RoomStatus;

public class RoomStatusAdapter extends ArrayAdapter<RoomStatus> {

	int resource;

	public RoomStatusAdapter(Context _context, int _resource,
			List<RoomStatus> _items) {
		super(_context, _resource, _items);
		this.resource = _resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout roomStatusView;

		RoomStatus rs = getItem(position);
		String roomID = rs.getRoomID();
		int numberOfPlayer = rs.getNumberOfPlayers();

		if (convertView == null) {
			roomStatusView = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
					inflater);
			vi.inflate(resource, roomStatusView, true);
		} else {
			roomStatusView = (LinearLayout) convertView;
		}
		TextView roomID_tv = (TextView) roomStatusView
				.findViewById(R.id.txt_roomID);
		TextView numberOfPlayer_tv = (TextView) roomStatusView
				.findViewById(R.id.txt_numerOfPlayers);

		roomID_tv.setText(roomID);
		numberOfPlayer_tv.setText(String.valueOf(numberOfPlayer));

		return roomStatusView;
	}
}
