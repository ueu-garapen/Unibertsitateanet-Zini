/**
 * Copyright (C) 2012  Udako Euskal Unibertsitatea informatikaria@ueu.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package ueu.org.unibertsitatea.net.zini;


import ueu.org.unibertsitatea.net.zini.TitulazioZerrendaFragment.OnArticleSelectedListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewParent;

public class TitulazioZerrendaActivity extends FragmentActivity implements
OnArticleSelectedListener {
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

		this.setTitleColor(Color.WHITE);
		
		setContentView(R.layout.titulazio_zerrenda_fragment);
	}

	@Override
	public void onArticleSelected(String item) {
		// TODO Auto-generated method stub
		TitulazioInfoFragment viewer = (TitulazioInfoFragment) getSupportFragmentManager()
                .findFragmentById(R.id.detailFragment);

        if (viewer == null || !viewer.isInLayout()) {
			Intent intent = new Intent(getApplicationContext(),
					TitulazioInfoActivity.class);
			intent.putExtra("value", item);
			startActivity(intent);
        }else{
        	viewer.setText(item);
        }
		
	}
}
