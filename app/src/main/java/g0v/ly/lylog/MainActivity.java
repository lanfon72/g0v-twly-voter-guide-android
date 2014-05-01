package g0v.ly.lylog;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import g0v.ly.lylog.data.list.NavigationDrawerList;
import g0v.ly.lylog.legislator.Profile;

@SuppressWarnings("ALL")
public class MainActivity extends FragmentActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

	private NavigationDrawerFragment 	mNavigationDrawerFragment;
	private CharSequence 				mTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mNavigationDrawerFragment = (NavigationDrawerFragment)getFragmentManager().findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,(DrawerLayout) findViewById(R.id.drawer_layout));
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		FragmentManager fragmentManager;
		fragmentManager = getSupportFragmentManager();

		switch (position){
/*
			case 0:
				Log.d("MainActivity", "[" + (position+1) + "] row clicked");
				mTitle = getString(R.string.title_section1);
				break;
			case 1:
				Log.d("MainActivity", "[" + (position+1) + "] row clicked");
				mTitle = getString(R.string.title_section2);
				break;
			case 2:
				Log.d("MainActivity", "[" + (position+1) + "] row clicked");
				mTitle = getString(R.string.title_section3);
				break;
*/

			case 0:
			case 4:
				//Log.d("MainActivity", "Title row clicked");
				break;
			case 1:
				//Log.d("MainActivity", "[" + (position+1) + "] row clicked");
				Profile fragment = new Profile();
				fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
				//fragmentManager.beginTransaction().replace(R.id.container, PlaceholderFragment.newInstance(position + 1)).commit();
				break;
			case 2:
				//Log.d("MainActivity", "[" + (position+1) + "] row clicked");
				fragmentManager.beginTransaction().replace(R.id.container, PlaceholderFragment.newInstance(position + 1)).commit();
				break;
			case 3:
				//Log.d("MainActivity", "[" + (position+1) + "] row clicked");
				fragmentManager.beginTransaction().replace(R.id.container, PlaceholderFragment.newInstance(position + 1)).commit();
				break;
			case 5:
				//Log.d("MainActivity", "[" + (position+1) + "] row clicked");
				fragmentManager.beginTransaction().replace(R.id.container, PlaceholderFragment.newInstance(position + 1)).commit();
				break;
			case 6:
				//Log.d("MainActivity", "[" + (position+1) + "] row clicked");
				fragmentManager.beginTransaction().replace(R.id.container, PlaceholderFragment.newInstance(position + 1)).commit();
				break;
			case 7:
				//Log.d("MainActivity", "[" + (position+1) + "] row clicked");
				fragmentManager.beginTransaction().replace(R.id.container, PlaceholderFragment.newInstance(position + 1)).commit();
				break;

		}
	}

	public void onSectionAttached(int number) {
		NavigationDrawerList 	navigationDrawerList 	= new NavigationDrawerList();
		String[] 				titleStrArray 			= navigationDrawerList.getDrawerList();
		switch (number) {
			case 1:
			case 5:
				//mTitle = titleStrArray[number-1];
				break;
			case 2:
			case 3:
			case 4:
				//mTitle = getString(R.string.title_section1);
				mTitle = titleStrArray[0] + " : " + titleStrArray[number-1];
				break;

			case 6:
			case 7:
				//mTitle = getString(R.string.title_section1);
				mTitle = titleStrArray[4] + " : " + titleStrArray[number-1];
				break;
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		assert actionBar != null;
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		return id == R.id.action_settings || super.onOptionsItemSelected(item);
	}

	public static class PlaceholderFragment extends Fragment {
		private static final String ARG_SECTION_NUMBER = "section_number";

		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment 	= new PlaceholderFragment();
			Bundle 				args 		= new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
			View 		rootView 	= inflater.inflate(R.layout.fragment_main, container, false);
			assert rootView != null;
			TextView 	textView 	= (TextView) rootView.findViewById(R.id.section_label);
			textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
		}
	}
}
