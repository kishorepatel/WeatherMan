package in.blogspot.upsolving.weatherman;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {
	ArrayAdapter mForeCastAdapter;

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
			FetchWeatherTask fetchWeatherTask = new FetchWeatherTask();
			fetchWeatherTask.execute("94043");
			return true;
		}

		return super.onOptionsItemSelected(item);
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
		mForeCastAdapter = new ArrayAdapter(getContext(), R.layout.list_item_forecast, R.id.list_item_forecast_textview,  weekForecast);

		//now we have to link the adapter to list_view
		ListView listItems = (ListView) v.findViewById(R.id.listview_forecast);
		listItems.setAdapter(mForeCastAdapter);

		return v;
	}//onCreateView






	//-----------------------------------------------------------------------------------------------------------------------------
	public class FetchWeatherTask extends AsyncTask<String, Void, String[]>{
		private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

		@Override
		protected String[] doInBackground(String... params) {
			HttpURLConnection urlConnection = null;
			BufferedReader reader = null;
			String forecastJsonString = null;

			if(params.length == 0){
				return null;
			}

			String units = "metric";
			String format = "json";
			int numDays = 7;
			try{

				final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
				final String QUERY_PARAM = "q";
				final String FORMAT_PARAM = "mode";
				final String UNITS_PARAM = "units";
				final String DAYS_PARAM = "cnt";


				Uri buildUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
						.appendQueryParameter(QUERY_PARAM, params[0])
						.appendQueryParameter(FORMAT_PARAM, format)
						.appendQueryParameter(UNITS_PARAM, units)
						.appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
						.build();

				Log.v("CHECK URIBuilder: " , buildUri.toString());

				//NOTE: when using execute() method => please pass url as first param
				URL url = new URL(buildUri.toString());
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
				//Log.v("JSON: ", forecastJsonString);
				try{
					return getForecastDataFromJson(forecastJsonString, numDays);
				}
				catch(JSONException e){
					Log.e(LOG_TAG, "calling getForecstDataFromJson", e);
				}

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


		@Override
		protected void onPostExecute(String[] strings) {
			super.onPostExecute(strings);
			mForeCastAdapter.clear();
			mForeCastAdapter.addAll(Arrays.asList(strings));
		}

		//------parsing JSON-------
		private String[] getForecastDataFromJson(String forecastJsonString, int numDays) throws JSONException{
			final String OWM_LIST = "list";
			final String OWM_WEATHER = "weather";
			final String OWM_TEMPERATURE = "temp";
			final String OWM_MAX = "max";
			final String OWM_MIN = "min";
			final String OWM_DESCRIPTION = "main";
			String[] resultStr = new String[numDays];


				JSONObject forecastJson = new JSONObject(forecastJsonString);
				JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

				for (int i = 0; i < numDays; i++) {
					String day;
					String description;
					String highandlow;


					JSONObject dayForecast = weatherArray.getJSONObject(i);

					//day
					GregorianCalendar calendar = new GregorianCalendar();
					calendar.add(GregorianCalendar.DATE, i);
					Date time = calendar.getTime();
					SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
					day = shortenedDateFormat.format(time);

					//weather description
					JSONObject weatherForecast = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
					description = weatherForecast.getString(OWM_DESCRIPTION);

					//temperature
					JSONObject temperatureForecast = dayForecast.getJSONObject(OWM_TEMPERATURE);
					String max = temperatureForecast.getString(OWM_MAX);
					String min = temperatureForecast.getString(OWM_MIN);
					highandlow = formatHighLows(max, min);

					resultStr[i] = day + " - " + description + " - " + highandlow;
				}

			return resultStr;
		}//getForecastDataFromJson

		private String formatHighLows(String max, String min){
			int imax = (int) Math.round(Double.parseDouble(max));
			int imin = (int) Math.round(Double.parseDouble(min));
			return imax + "/" + imin;
		}



	}//FetchWeatherTask
	//----------------------------------------------------------------------------------------------------------------------------
}
