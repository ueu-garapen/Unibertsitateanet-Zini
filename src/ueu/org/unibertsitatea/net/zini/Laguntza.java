package ueu.org.unibertsitatea.net.zini;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewParent;
import android.webkit.WebView;

public class Laguntza extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Ezarri leihoaren izenburuaren kolorea eta fondoa
		View titleView = getWindow().findViewById(android.R.id.title);
		if (titleView != null) {
			ViewParent parent = titleView.getParent();
			if (parent != null && (parent instanceof View)) {
				View parentView = (View) parent;
				parentView.setBackgroundColor(Color.rgb(111, 201, 0));
			}
		}		
		setContentView(R.layout.laguntza);
		WebView webview_testua = (WebView) findViewById(R.id.laguntzatestua);
		webview_testua.loadData(getString(R.string.laguntza), "text/html", "utf-8");
	}
}
