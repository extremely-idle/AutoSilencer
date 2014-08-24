package uk.co.rm.android.AutoSilencer.ui;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import uk.co.rm.android.AutoSilencer.R;
import uk.co.rm.android.AutoSilencer.R.drawable;
import uk.co.rm.android.AutoSilencer.R.id;
import uk.co.rm.android.AutoSilencer.obj.GCalendar;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @author Ross Moug
 * @param <T>
 */
public class CalendarAdapter<T> extends BaseAdapter {

	private static final String TAG = "CalendarAdapter";
	private Context context;
	private ArrayList<GCalendar> calendars;
	private ArrayList<CalendarHolder> holders;
	private int id;
	private LayoutInflater inflater;

	public CalendarAdapter(Context context, int id,
			ArrayList<GCalendar> calendars) {
		super();
		this.holders = new ArrayList<CalendarHolder>();
		this.context = context;
		this.calendars = calendars;
		this.id = id;
		this.inflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return calendars.size();
	}

	public GCalendar getItem(int pos) {
		return calendars.get(pos);
	}

	public long getItemId(int arg0) {
		return 10000000;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		//Log.i(TAG, "<*** Getting Friend View ***>");
		CalendarHolder ch;
		View v = null;
		if(convertView != null)
			v = convertView;
		else
			v = inflater.inflate(id, parent, false);
		
		ch = new CalendarHolder();

		ch.colour = (ImageView) v.findViewById(R.id.calendarColour);

		ch.caldenarName = (TextView) v.findViewById(R.id.calendarName);
		ch.caldenarName.setTextColor(Color.BLACK);

		ch.status = (ImageView) v.findViewById(R.id.calendarActive);

		v.setTag(ch);

		ch.caldenarName.setText((CharSequence) calendars.get(position).getName(), TextView.BufferType.SPANNABLE); 

		Drawable colourDraw = getCalColour(calendars.get(position).getColour());
		ch.colour.setImageDrawable(colourDraw);

		if(calendars.get(position).isSync()){
			//Log.i(TAG, "<*** "+friends.get(position).getName()+" is online ****>");
			Drawable statusDraw = context.getResources().getDrawable(R.drawable.check_pressed);
			//fh.status.invalidateDrawable(fh.status.getDrawable());
			ch.status.setImageDrawable(statusDraw);
		} else {
			//Log.i(TAG, "<*** "+friends.get(position).getName()+" is offline ****>");
			Drawable statusDraw = context.getResources().getDrawable(R.drawable.check_normal);
			//fh.status.invalidateDrawable(fh.status.getDrawable());
			ch.status.setImageDrawable(statusDraw);
		}

		Spannable span = (Spannable) ch.caldenarName.getText();

		span.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, ch.caldenarName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		holders.add(ch);
		
		return v;
	}

	private Drawable getCalColour(int colour) {
		switch(colour){
		case Color.RED: return context.getResources().getDrawable(R.drawable.cal_red);
		case Color.BLUE: return context.getResources().getDrawable(R.drawable.cal_blue);
		case Color.GREEN: return context.getResources().getDrawable(R.drawable.cal_green);
		case Color.YELLOW: return context.getResources().getDrawable(R.drawable.cal_yellow);
		case Color.CYAN: return context.getResources().getDrawable(R.drawable.cal_orange);
		case Color.MAGENTA: return context.getResources().getDrawable(R.drawable.cal_purple);
		case Color.LTGRAY: return context.getResources().getDrawable(R.drawable.cal_grey);
		case Color.WHITE: return context.getResources().getDrawable(R.drawable.cal_white);
		case Color.BLACK: return context.getResources().getDrawable(R.drawable.cal_black);
		default: return context.getResources().getDrawable(R.drawable.cal_white);
		}
	}

	public void clear(){
		calendars = null;
	}
}
