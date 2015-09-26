package in.blogspot.upsolving.weatherman.data;


import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.graphics.Path;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.GregorianCalendar;
import java.util.TimeZone;

public class WeatherContract{
	public static final String CONTENT_AUTHORITY = "in.blogspot.upsolving.android.WeatherMan.app";
	public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

	public static final String PATH_WEATHER = "weather";
	public static final String PATH_LOCATION = "location";


	public static long normalizeDate(long startDate){
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeZone(TimeZone.getTimeZone("IST"));

		return calendar.getTimeInMillis();
	}

	public static final class LocationEntry implements BaseColumns{
		//location content uri
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();

		//content type
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

		//table name
		public static final String TABLE_NAME = "location";

		//columns
		public static final String COLUMN_LOCATION_SETTING = "location_setting";
		public static final String COLUMN_CITY_NAME = "city";
		public static final String COLUMN_COORD_LAT = "coord_lat";
		public static final String COLUMN_COORD_LONG = "coord_long";


		public static Uri buildLocationUri(long id){
			return ContentUris.withAppendedId(CONTENT_URI, id);
		}
	}

	public static final class WeatherEntry implements  BaseColumns{
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_WEATHER).build();

		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;

		public static final String TABLE_NAME = "weather";
		public static final String COLUMN_LOC_KEY = "location_id";
		// Date, stored as long in milliseconds since the epoch
		public static final String COLUMN_DATE = "date";
		// Weather id as returned by API, to identify the icon to be used
		public static final String COLUMN_WEATHER_ID = "weather_id";

		// Short description and long description of the weather, as provided by API.
		// e.g "clear" vs "sky is clear".
		public static final String COLUMN_SHORT_DESC = "short_desc";

		// Min and max temperatures for the day (stored as floats)
		public static final String COLUMN_MIN_TEMP = "min";
		public static final String COLUMN_MAX_TEMP = "max";

		// Humidity is stored as a float representing percentage
		public static final String COLUMN_HUMIDITY = "humidity";

		// Humidity is stored as a float representing percentage
		public static final String COLUMN_PRESSURE = "pressure";

		// Windspeed is stored as a float representing windspeed  mph
		public static final String COLUMN_WIND_SPEED = "wind";

		// Degrees are meteorological degrees (e.g, 0 is north, 180 is south).  Stored as floats.
		public static final String COLUMN_DEGREES = "degrees";

		public static Uri buildWeatherUri(long id){
			return ContentUris.withAppendedId(CONTENT_URI, id);
		}


		public static Uri buildWeatherLocation(String locationSetting){
			return CONTENT_URI.buildUpon().appendPath(locationSetting).build();
		}

		public static Uri buildWeatherLocationWithStartDate(String locationSetting, long startDate){
			long normalizedDate = normalizeDate(startDate);
			return CONTENT_URI.buildUpon().appendPath(locationSetting)
					.appendQueryParameter(COLUMN_DATE, Long.toString(normalizedDate)).build();
		}

		public static Uri buildWeatherLocationWithDate(String locationSetting, long date){
			return CONTENT_URI.buildUpon().appendPath(locationSetting).appendPath(Long.toString(date)).build();
		}

		public static String getLocationSettingFromUri(Uri uri){
			return uri.getPathSegments().get(1);
		}

		public static long getDateFromUri(Uri uri){
			return Long.parseLong(uri.getPathSegments().get(2));
		}

		public static long getStartDateFromUri(Uri uri){
			String startDate = uri.getQueryParameter(COLUMN_DATE);
			if(startDate != null && startDate.length() > 0){
				return Long.parseLong(startDate);
			}
			else
				return 0;
		}
	}
}
