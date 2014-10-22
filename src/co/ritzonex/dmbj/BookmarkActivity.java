package co.ritzonex.dmbj;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;
import co.ritzonex.b.BannerView;
import co.ritzonex.dmbj.BookmarkFragment.OnFragmentInteractionListener;

public class BookmarkActivity extends ActionBarActivity implements
		OnFragmentInteractionListener {

	private BannerView bannerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bookmark);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new BookmarkFragment()).commit();
		}

		Toast.makeText(this, "长按列表删除书签", Toast.LENGTH_SHORT).show();
		bannerView = (BannerView) findViewById(R.id.banner);
		bannerView.showBanner("bb8b3d6aac9440e2a75858a52502f4ee", "all");
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.bookmark, menu);
	// return true;
	// }

	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// // Handle action bar item clicks here. The action bar will
	// // automatically handle clicks on the Home/Up button, so long
	// // as you specify a parent activity in AndroidManifest.xml.
	// int id = item.getItemId();
	// if (id == R.id.action_settings) {
	// return true;
	// }
	// return super.onOptionsItemSelected(item);
	// }

	@Override
	public void onFragmentInteraction(String bookName, int index) {
		Intent intent = new Intent(this, IndexActivity.class);
		intent.putExtra(IndexActivity.BOOK_NAME, bookName);
		intent.putExtra(IndexActivity.INDEX, index);
		startActivity(intent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (bannerView != null) {
			bannerView.finishBanner();
		}

	}
}
