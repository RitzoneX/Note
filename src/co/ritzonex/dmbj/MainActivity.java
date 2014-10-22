package co.ritzonex.dmbj;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import co.ritzonex.b.BannerView;
import co.ritzonex.dmbj.BookFragment.OnFragmentInteractionListener;

import com.umeng.analytics.MobclickAgent;

public class MainActivity extends ActionBarActivity implements
		OnFragmentInteractionListener {
	private MyApp app;
	private BannerView bannerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new BookFragment()).commit();
		}
		app = (MyApp) getApplication();
		
		MobclickAgent.updateOnlineConfig(this);
		bannerView =  (BannerView) findViewById(R.id.banner);
		bannerView.showBanner("bb8b3d6aac9440e2a75858a52502f4ee", "all");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		// Set up ShareActionProvider's default share intent
		MenuItem shareItem = menu.findItem(R.id.action_share);
		ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat
				.getActionProvider(shareItem);
		mShareActionProvider.setShareIntent(app.getDefaultIntent());

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_bookmark) {
			Intent intent = new Intent(this, BookmarkActivity.class);
			startActivity(intent);
			return true;
		}
		if (id == R.id.action_comment) {
			Intent intent = new Intent("android.intent.action.VIEW");
			Uri data = Uri.parse("market://details?id=co.ritzonex.dmbj");
			intent.setData(data);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onFragmentInteraction(CharSequence bookName) {
		Intent intent = new Intent(this, IndexActivity.class);
		intent.putExtra(IndexActivity.BOOK_NAME, bookName);
		startActivity(intent);
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (bannerView !=null) {
			bannerView.finishBanner();
		}
	
	}

}
