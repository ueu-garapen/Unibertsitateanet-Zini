package ueu.org.unibertsitatea.net.zini;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewParent;
import android.widget.TextView;

public class Honiburuz extends Activity {
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
		//Gordetako iragarkiak ezabatu, sareko iragarki berriekin eguneratzeko.
		setContentView(R.layout.honiburuz);
		TextView bertsioa = (TextView) findViewById(R.id.bertsioa);
		bertsioa.setText("Bertsioa " + bertsioa.getText());
	}

}
