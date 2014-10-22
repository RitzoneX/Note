package co.ritzonex.dmbj;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class MyApp extends Application {
	private File path; // 主路径
	private File bookDir;// 书本目录
	private String[] titles;
	private SharedPreferences preferences;
	private Set<String> bookmarks; // 书签集合
	private ArrayList<String> bookmarkTitles = new ArrayList<String>(); // 书签标题

	public static String SPLIT = "\n";

	public SharedPreferences getPreferences() {
		return preferences;
	}

	public String[] getTitles() {
		return titles;
	}

	public File getBookDir() {
		return bookDir;
	}

	public Set<String> getBookmarks() {
		return bookmarks;
	}

	public void setBookDir(File bookDir) {
		this.bookDir = bookDir;
		int length = bookDir.list().length;
		titles = new String[length];
		for (int i = 0; i < length; i++) {
			String name = bookDir.list()[i];
			titles[i] = name.substring(3, name.length() - 4);
		}
	}

	public File getPath() {
		return path;
	}

	/*
	 * 删除书签
	 */
	public void removeBookmark(String bookmark) {
		editBookmark(bookmark, false);
	}

	/*
	 * 添加书签
	 */
	public void addBookmark(String bookmark) {
		editBookmark(bookmark, true);
	}

	private void editBookmark(String bookmark, boolean isAdd) {
		if (isAdd) {
			bookmarkTitles.add(bookmark.split(SPLIT)[2]);
			getBookmarks().add(bookmark);
		} else {
			bookmarkTitles.remove(bookmark.split(SPLIT)[2]);
			getBookmarks().remove(bookmark);
		}
		String str = getBookmarks().toString();
		preferences
				.edit()
				.putString(IndexActivity.PREF_BOOKMARK,
						str.substring(1, str.length() - 1)).commit();
		String tostr = isAdd ? "已添加书签" : "已删除书签";
		Toast.makeText(this, tostr, Toast.LENGTH_SHORT).show();
	}

	public ArrayList<String> getBookmarkTitles() {
		return bookmarkTitles;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		path = new File(getFilesDir() + "/book");
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String temp = preferences.getString(IndexActivity.PREF_BOOKMARK, null);
		bookmarks = temp == null ? new LinkedHashSet<String>()
				: new LinkedHashSet<String>(Arrays.asList(temp.split(", ")));
		for (String mark : bookmarks)
			bookmarkTitles.add(mark.split(SPLIT)[2]);
	}

	/**
	 * Defines a default (dummy) share intent to initialize the action provider.
	 * However, as soon as the actual content to be used in the intent is known
	 * or changes, you must update the share intent by again calling
	 * mShareActionProvider.setShareIntent()
	 */
	public Intent getDefaultIntent() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
		intent.putExtra(Intent.EXTRA_TEXT, "我正在阅读《" + getApplicationName()
				+ "》，非常精彩\n下载地址: http://fir.im/q74k");
		intent.putExtra(Intent.EXTRA_TITLE, getApplicationName());
		return intent;
	}

	public String getApplicationName() {
		PackageManager packageManager = null;
		ApplicationInfo applicationInfo = null;
		try {
			packageManager = getApplicationContext().getPackageManager();
			applicationInfo = packageManager.getApplicationInfo(
					getPackageName(), 0);
		} catch (PackageManager.NameNotFoundException e) {
			applicationInfo = null;
		}
		String applicationName = (String) packageManager
				.getApplicationLabel(applicationInfo);
		return applicationName;
	}
}