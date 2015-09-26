package in.blogspot.upsolving.weatherman;

import android.app.Notification;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ActionProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.internal.view.menu.MenuItemImpl;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.ShareActionProvider;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {
	private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
	private static final String HASH_TAG = "#WeatherMan APP";

	private String mForecastStr;

	public DetailActivityFragment() {
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		Intent intent = getActivity().getIntent();
		View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
		mForecastStr = intent.getStringExtra(Intent.EXTRA_TEXT);
		TextView textview = (TextView) rootView.findViewById(R.id.detail_text);
		textview.setText(mForecastStr);

		return rootView;
	}

	public Intent createShareForecastIntent(){
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(Intent.EXTRA_TEXT, mForecastStr + " " + HASH_TAG);

		return shareIntent;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		inflater.inflate(R.menu.detail_fragment, menu);
		MenuItem menuItem = menu.findItem(R.id.action_share);

		ShareActionProvider shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

		if(shareActionProvider != null){
			shareActionProvider.setShareIntent( createShareForecastIntent() );
		}
		else{
			Log.e(LOG_TAG, "shareAP FAIL");
		}
	}
}
