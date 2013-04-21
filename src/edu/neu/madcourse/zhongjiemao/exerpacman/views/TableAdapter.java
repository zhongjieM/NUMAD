package edu.neu.madcourse.zhongjiemao.exerpacman.views;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TableAdapter extends BaseAdapter {

	private Context context;

	private List<TableRow> table;

	public TableAdapter(Context context, List<TableRow> table) {
		this.context = context;
		this.table = table;
	}

	@Override
	public int getCount() {
		return table.size();
	}

	@Override
	public TableRow getItem(int position) {
		return table.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TableRow tableRow = table.get(position);
		return new TableRowView(this.context, tableRow);
	}

	class TableRowView extends LinearLayout {
		public TableRowView(Context context, TableRow tableRow) {
			super(context);

			this.setOrientation(LinearLayout.HORIZONTAL);
			for (int i = 0; i < tableRow.getSize(); i++) {// 			
				TableCell tableCell = tableRow.getCellValue(i);
				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(tableCell.width, tableCell.height);
				layoutParams.setMargins(1, 1, 1, 1);// 
				if (tableCell.type == TableCell.STRING) {// 
					TextView textCell = new TextView(context);
					textCell.setLines(1);
					textCell.setGravity(Gravity.CENTER);
					textCell.setBackgroundColor(Color.TRANSPARENT);
					textCell.setTextSize(tableCell.textSize);
					textCell.setGravity(Gravity.CENTER);
					textCell.setTypeface(Typeface.SERIF);
					textCell.setTextColor(Color.WHITE);
					textCell.setText(String.valueOf(tableCell.value));
					addView(textCell, layoutParams);
				} else if (tableCell.type == TableCell.IMAGE) {// 
					ImageView imgCell = new ImageView(context);
					imgCell.setBackgroundColor(Color.TRANSPARENT);
					imgCell.setImageResource((Integer) tableCell.value);
					addView(imgCell, layoutParams);
				}
			}
		}
	}

	static public class TableRow {
		private TableCell[] cells;

		public TableRow(TableCell[] cells) {
			this.cells = cells;
		}

		public int getSize() {
			return cells.length;
		}

		public TableCell getCellValue(int index) {
			if (index >= cells.length)
				return null;
			return cells[index];
		}
	}

	static public class TableCell {
		static public final int STRING = 0;
		static public final int IMAGE = 1;
		public Object value;
		public int width;
		public int height;
		private int type;
		private int textSize;

		public TableCell(Object value, int width, int height, int type) {
			this.value = value;
			this.width = width;
			this.height = height;
			this.type = type;
		}
		
		public TableCell(Object value, int width, int height, int type, int textSize) {
			this.value = value;
			this.width = width;
			this.height = height;
			this.type = type;
			this.textSize = textSize;
		}
	}
}
