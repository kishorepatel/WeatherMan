package in.blogspot.upsolving.weatherman;


import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {
	private static ArrayAdapter mForecastAdapter;

	public ForecastFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.forecastfragment, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemID = (int) item.getItemId();
		if(itemID == R.id.action_refresh){
			updateWeather();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public void updateWeather(){
		FetchWeatherTask fetchWeatherTask = new FetchWeatherTask(getActivity(), mForecastAdapter);
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		String location = preferences.getString(getString(R.string.pref_location_key), getString(R.string.pref_default_location));
		fetchWeatherTask.execute(location);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {


		//creating an ARRAY ADAPTER
		mForecastAdapter = new ArrayAdapter(
				getContext(),
				R.layout.list_item_forecast,
				R.id.list_item_forecast_textview,
				new ArrayList<>());


		View v = inflater.inflate(R.layout.fragment_main, container, false);


		//now we have to link the adapter to list_view
		ListView listItems = (ListView) v.findViewById(R.id.listview_forecast);
		listItems.setAdapter(mForecastAdapter);

		listItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String content = mForecastAdapter.getItem(position).toString();
				Intent intent = new Intent(getActivity(), DetailActivity.class)
						.putExtra(Intent.EXTRA_TEXT, content);
				startActivity(intent);
			}
		});

		return v;
	}//onCreateView


	@Override
	public void onStart() {
		super.onStart();
		updateWeather();
	}

}
