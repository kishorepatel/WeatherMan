package in.blogspot.upsolving.weatherman;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

	public MainActivityFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_main, container, false);

		String[] forecastArray = new String[]{
				"Today Stormy 28/16",
				"Tomorrow Sunny 32/12",
				"Wed Snow 12/23",
				"Thurs Hailstrom 33/23",
				"Fri go to hell 120/00",
				"Sat Rainy ",
				"Sun Sunny"
		};

		//creating an ARRAY ADAPTER
		ArrayList<String> weekForecast = new ArrayList<>(Arrays.asList(forecastArray));
		ArrayAdapter mForeCastAdapter = new ArrayAdapter(getContext(), R.layout.list_item_forecast, R.id.list_item_forecast_textview,  weekForecast);

		//now we have to link the adapter to list_view
		ListView listItems = (ListView) v.findViewById(R.id.listview_forecast);
		listItems.setAdapter(mForeCastAdapter);

		return v;
	}
}
