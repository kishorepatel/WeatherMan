package in.blogspot.upsolving.weatherman;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

	public ForecastFragment() {
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

		new FetchWeatherTask().execute("http://api.openweathermap.org/data/2.5/forecast/daily?q=chennai,in&mode=json&cnt=7");

		return v;
	}//onCreateView




	public class FetchWeatherTask extends AsyncTask<String, Void, Void>{
		private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

		@Override
		protected Void doInBackground(String... params) {
			HttpURLConnection urlConnection = null;
			BufferedReader reader = null;
			String forecastJsonString = null;

			try{
				//NOTE: when using execute() method => please pass url as first param
				URL url = new URL(params[0]);
				urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setRequestMethod("GET");
				urlConnection.connect(); //this line creates lot of trouble

				InputStream inputStream = urlConnection.getInputStream();
				if(inputStream == null){
					return null;
				}

				reader = new BufferedReader(new InputStreamReader(inputStream));
				StringBuffer buffer = new StringBuffer();
				String line = "";

				while((line = reader.readLine()) != null){
					buffer.append(line + "\n");
				}

				if(buffer.length() == 0){
					return null;
				}

				forecastJsonString = buffer.toString();
			}
			catch(IOException e){
				Log.e(LOG_TAG , "Error: ", e);
			}
			finally{
				if(urlConnection != null){
					urlConnection.disconnect();
				}
				if(reader != null){
					try{
						reader.close();
					}
					catch(IOException e){
						Log.e(LOG_TAG , "Error closing stream: " , e);
					}
				}
			}

			return null;
		}//doInBackGround

	}//FetchWeatherTask
}
