package co.ritzonex.dmbj;

import java.io.File;
import java.io.FileInputStream;

import org.apache.http.util.EncodingUtils;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

public class IndexActivity extends ActionBarActivity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks {
	public static final String BOOK_NAME = "bookname";
	public static final String INDEX = "index";
	private static final String PREF_TEXT_SIZE = "text_size";
	private static final String PREF_MODE_NIGHT = "mode_night";
	private static final String PREF_SCREEN = "screen";
	public static final String PREF_BOOKMARK = "bookmark";

	private MyApp app;
	private boolean modeNight;
	private boolean screen;
	private int currentSelectedPosition;

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;
	private PlaceholderFragment mPlaceholderFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_index);

		init();

		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
	}

	private void init() {
		app = (MyApp) getApplication();
		modeNight = app.getPreferences().getBoolean(PREF_MODE_NIGHT, false);
		screen = app.getPreferences().getBoolean(PREF_SCREEN, false);
		if (screen) {
			getActionBar().hide();
			screen();
		}

	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getSupportFragmentManager();
		mPlaceholderFragment = PlaceholderFragment.newInstance(position);
		fragmentManager.beginTransaction()
				.replace(R.id.container, mPlaceholderFragment).commit();
	}

	public void onSectionAttached(int number) {
		mTitle = app.getTitles()[number];
		currentSelectedPosition = number;
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	private void screen() {
		WindowManager.LayoutParams attrs = getWindow().getAttributes();
		attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
		getWindow().setAttributes(attrs);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
	}

	private void quitScreen() {
		WindowManager.LayoutParams attrs = getWindow().getAttributes();
		attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setAttributes(attrs);
		getWindow()
				.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.index, menu);
			restoreActionBar();
			MenuItem item = menu.findItem(R.id.action_mode);
			item.setTitle(modeNight ? R.string.action_mode_day
					: R.string.action_mode_night);
			item = menu.findItem(R.id.action_screen);
			item.setTitle(screen ? R.string.action_quit_screen
					: R.string.action_screen);
			item = menu.findItem(R.id.action_bookmark);
			item.setTitle(app.getBookmarks().contains(bookmark()) ? R.string.action_remove_bookmark
					: R.string.action_add_bookmark);
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
		// if (id == R.id.action_settings) {
		// return true;
		// }
		switch (id) {
		case R.id.action_mode: // 夜间模式
			modeNight = !modeNight;
			item.setTitle(modeNight ? R.string.action_mode_day
					: R.string.action_mode_night);
			View view = mPlaceholderFragment.getRootView();
			if (modeNight) {
				view.setBackgroundResource(android.R.color.black);
			} else {
				view.setBackgroundResource(R.color.brown);
			}
			app.getPreferences().edit().putBoolean(PREF_MODE_NIGHT, modeNight)
					.commit();
			return true;
		case R.id.action_font_bigger: // 增大字体
			setFontSize(1);
			return true;
		case R.id.action_font_smaller: // 减小字体
			setFontSize(-1);
			return true;
		case R.id.action_screen: // 全屏
			screen = !screen;
			item.setTitle(screen ? R.string.action_quit_screen
					: R.string.action_screen);
			if (screen) {
				getActionBar().hide();
				screen();
			} else {
				getActionBar().show();
				quitScreen();
			}
			app.getPreferences().edit().putBoolean(PREF_SCREEN, screen)
					.commit();
			return true;
		case R.id.action_bookmark: // 书签
			if (app.getBookmarks().contains(bookmark())) {
				app.removeBookmark(bookmark());
				item.setTitle(R.string.action_add_bookmark);
			} else {
				app.addBookmark(bookmark());
				item.setTitle(R.string.action_remove_bookmark);
			}
			return true;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private String bookmark() {
		return app.getBookDir().getName() + MyApp.SPLIT
				+ currentSelectedPosition + MyApp.SPLIT + mTitle;
	}

	// 设置字体大小
	private void setFontSize(int s) {
		View view = mPlaceholderFragment.getView();
		TextView textView = (TextView) view.findViewById(R.id.section_label);
		float size = px2sp(textView.getTextSize() + s);
		textView.setTextSize(size);
		app.getPreferences().edit().putFloat(PREF_TEXT_SIZE, size).commit();
	}

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 * 
	 * @param pxValue
	 * @param fontScale
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public float px2sp(float pxValue) {
		float fontScale = getResources().getDisplayMetrics().scaledDensity;
		return pxValue / fontScale;
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		private MyApp app;
		private int sectionNumber;
		private boolean modeNight;
		private View rootView;
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			app = (MyApp) getActivity().getApplication();
			modeNight = app.getPreferences().getBoolean(PREF_MODE_NIGHT, false);
			sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			rootView = inflater.inflate(R.layout.fragment_index, container,
					false);
			TextView textView = (TextView) rootView
					.findViewById(R.id.section_label);
			File file = app.getBookDir().listFiles()[sectionNumber];
			textView.setText(readFile(file));
			float size = app.getPreferences().getFloat(PREF_TEXT_SIZE, 0);
			if (size != 0)
				textView.setTextSize(size);
			if (modeNight)
				rootView.setBackgroundResource(android.R.color.black);
			return rootView;
		}

		public View getRootView() {
			return rootView;
		}

		// 读数据
		public String readFile(File file) {
			String res = "";
			try {
				FileInputStream fin = new FileInputStream(file);
				int length = fin.available();
				byte[] buffer = new byte[length];
				fin.read(buffer);
				res = EncodingUtils.getString(buffer, "UTF-8");
				fin.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return res;

		}

		// @Override
		// public void onAttach(Activity activity) {
		// super.onAttach(activity);
		// ((IndexActivity) activity).onSectionAttached(getArguments().getInt(
		// ARG_SECTION_NUMBER));
		// }

		@Override
		public void onActivityCreated(@Nullable Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			((IndexActivity) getActivity()).onSectionAttached(getArguments()
					.getInt(ARG_SECTION_NUMBER));
		}

	}

}
