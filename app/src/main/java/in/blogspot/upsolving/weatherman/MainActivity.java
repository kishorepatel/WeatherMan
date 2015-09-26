package in.blogspot.upsolving.weatherman;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		}
		if(id == R.id.action_map){
			openPreferedLocationInMap();
		}

		return super.onOptionsItemSelected(item);
	}

	public void openPreferedLocationInMap(){
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String location = preferences.getString(getString(R.string.pref_location_key), getString(R.string.pref_default_location));

		Uri uri = Uri.parse("geo:0,0?").buildUpon()
				.appendQueryParameter("q", location)
				.build();

		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(uri);

		if(intent.resolveActivity(getPackageManager()) != null){
			startActivity(intent);
		}
		else{
			Log.e("MAP INTENT FAIL: loc = ", location);
		}
	}
}
