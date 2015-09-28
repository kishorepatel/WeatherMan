package in.blogspot.upsolving.weatherman;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ArrayAdapter;

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
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;

import in.blogspot.upsolving.weatherman.data.WeatherContract;

public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {
	private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
	private ArrayAdapter mForecastAdapter;
	private Context mContext;

	public FetchWeatherTask(Context context, ArrayAdapter forecastAdapter){
		mContext = context;
		mForecastAdapter = forecastAdapter;
	}

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

			//Log.v("CHECK URIBuilder: " , buildUri.toString());

			//NOTE: when using execute() method => please pass url as first param
			URL url = new URL(buildUri.toString());
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.connect(); //this line creates lot of trouble

			//Log.v(LOG_TAG, "Connected HTTP");

			InputStream inputStream = urlConnection.getInputStream();
			if(inputStream == null){
				//	Log.v(LOG_TAG, "inputstrams is null");
				return null;
			}

			reader = new BufferedReader(new InputStreamReader(inputStream));
			StringBuffer buffer = new StringBuffer();
			String line = "";

			while((line = reader.readLine()) != null){
				buffer.append(line + "\n");
			}

			if(buffer.length() == 0){
				//	Log.v(LOG_TAG, "buffer is null");
				return null;
			}

			forecastJsonString = buffer.toString();
			Log.v("JSON: ", forecastJsonString);

			try{
				return getForecastDataFromJson(forecastJsonString, numDays);
			}
			catch(JSONException e){
				Log.e(LOG_TAG, "calling getForecstDataFromJson" + e);
			}

		}
		catch(IOException e){
			Log.e(LOG_TAG , "Error: "+ e);
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
					Log.e(LOG_TAG , "Error closing stream: " + e);
				}
			}
		}

		return null;
	}//doInBackGround


	@Override
	protected void onPostExecute(String[] strings) {
		super.onPostExecute(strings);
		if(strings == null){
			//Log.v(LOG_TAG,"strings is null");
		}
		mForecastAdapter.clear();
		mForecastAdapter.addAll(Arrays.asList(strings));
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
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		String unitType = preferences.getString(mContext.getString(R.string.pref_unit_key), mContext.getString(R.string.pref_default_unit));

		double dmax = Double.parseDouble(max);
		double dmin = Double.parseDouble(min);
		if(!unitType.equals(mContext.getString(R.string.pref_default_unit))){
			dmax = (dmax * 1.8) + 32;
			dmin = (dmin * 1.8) + 32;
		}

		int imax = (int) Math.round(dmax);
		int imin = (int) Math.round(dmin);
		return imax + "/" + imin;
	}

	long addLocation(String locationSetting, String cityName, double lat, double lon){
		long locationId;

		Cursor locationCursor = mContext.getApplicationContext().getContentResolver().query(
				WeatherContract.LocationEntry.CONTENT_URI,
				new String[]{WeatherContract.LocationEntry._ID},
				WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? ",
				new String[]{locationSetting},
				null
		);

		if(locationCursor.moveToFirst()){
			int locationIndex = locationCursor.getColumnIndex(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING);
			locationId = locationCursor.getLong(locationIndex);
		}else{
			ContentValues locationValues = new ContentValues();

			locationValues.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, cityName);
			locationValues.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING, locationSetting);
			locationValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT, lat);
			locationValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG, lon);

			locationId = 1;
		}

		return locationId;
	}

}//FetchWeatherTask