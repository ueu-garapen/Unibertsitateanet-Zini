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

import java.util.ArrayList;
import java.util.List;

import ueu.org.unibertsitatea.net.zini.data.DbUtil;
import ueu.org.unibertsitatea.net.zini.data.TitulazioZerrendaProvider;
import ueu.org.unibertsitatea.net.zini.data.Titulazioa;
import ueu.org.unibertsitatea.net.zini.data.htmlJsoupHelper;
import android.app.Activity;
import android.app.ProgressDialog;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;

public class TitulazioZerrendaFragment extends ListFragment implements
		LoaderManager.LoaderCallbacks<Cursor> {

	private int unekoIndex = 0;

	private SimpleCursorAdapter adapter;

	TextView hutsik;

	private static final int TITULAZIOAK_LIST_LOADER = 0x01;

	public String titulazioMota = "";
	public String titulazioLurraldea = "";
	public String titulazioBilaketaIrizpidea = "";

	OnArticleSelectedListener mListener;

	private View zerrendaView;

	private ProgressDialog progressDialog2;

	private TitulazioInfoFragment titulazioInfoFragment;

	private final Handler handler = new Handler() {
		@Override
		public void handleMessage(final Message msg) {
			if (progressDialog2 != null && progressDialog2.isShowing())
				try {
					progressDialog2.dismiss();
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}

			switch (msg.what) {
			case 0:// Errorea
				Toast.makeText(getActivity().getApplicationContext(),
						"Titulazioa hutsik dago", Toast.LENGTH_LONG).show();
				break;
			case 1:
				// Dena ondo joan da, alda dezagun edukia
				// TitulazioInfoFragment fragment = (TitulazioInfoFragment)
				// getFragmentManager()
				// .findFragmentById(R.id.titulazio_info_fragment);
				if (titulazioInfoFragment != null
						&& titulazioInfoFragment.isInLayout()) {
					Titulazioa tit = (Titulazioa) msg.getData().getParcelable(
							"titulazioa");
					int index = msg.getData().getInt("unekoIndex", 0);
					if (tit != null) {
						tit.toString();
						System.out.println("handler -- index: " + index);
						System.out.println("handler -- unekoIndex: "
								+ unekoIndex);
						titulazioInfoFragment.setEdukia((Titulazioa) msg
								.getData().getParcelable("titulazioa"));
						getListView()
								.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
						getListView().setItemChecked(index, true);
						getListView().setSelection(index);
					} else {
						System.out.println("parcelable hutsik");
					}

				}
				break;
			case 2: // Zerrenda hutsik
				if (hutsik != null) {
					System.out.println("Hutsik -- handlerren.");
					if (zerrendaView != null) {
						zerrendaView.findViewById(android.R.id.list)
								.setVisibility(View.GONE);
						// getListView().setEmptyView(hutsik);
						hutsik.setVisibility(View.VISIBLE);

						if (titulazioInfoFragment != null
								&& titulazioInfoFragment.isInLayout()) {
							FragmentTransaction ft = getFragmentManager()
									.beginTransaction();
							ft.hide(titulazioInfoFragment);
							ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
							ft.commit();
							System.out.println("TitulazioInfoFragment ezabatzen");
							// titulazioInfoFragment.
						}
					}
				} else {
					System.out.println("Hutsik bista null da");
				}
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("TitulazioZerrendaFragment -- onCreate");
		// jaso haiserako pantailatik hartutako

		// Eskuratu bilaketarako irizpideak: lurraldea, ikasketa, gakoa
		Intent launchingIntent = getActivity().getIntent();
		this.titulazioLurraldea = launchingIntent.getStringExtra("lurraldea");
		this.titulazioMota = launchingIntent.getStringExtra("ikasketa");
		this.titulazioBilaketaIrizpidea = launchingIntent
				.getStringExtra("irizpidea");

		// Zerrenda adaptadorea prestatu
		// dbUtil = new DbUtil(getActivity());
		// dbUtil.open();

		String[] uiBindFrom = { DbUtil.KEY_IZENBURUA,
				DbUtil.KEY_UNIBERTSITATEAMOTA, DbUtil.KEY_HELBIDEA };
		int[] uiBindTo = { R.id.izenburua, R.id.fakultatea, R.id.helbidea };
		if (adapter == null) {
			adapter = new SimpleCursorAdapter(getActivity()
					.getApplicationContext(), R.layout.list_item, null,
					uiBindFrom, uiBindTo,
					CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		}
		// setListShown(true);

		setListAdapter(adapter);

		// dbUtil.close();

		LoaderManager.enableDebugLogging(true);
		/*
		 * LoaderManager lm = getLoaderManager(); if
		 * (lm.getLoader(TITULAZIOAK_LIST_LOADER) != null) {
		 * lm.initLoader(TITULAZIOAK_LIST_LOADER, null, this); }
		 */
		getLoaderManager().initLoader(TITULAZIOAK_LIST_LOADER, null, this);
		// setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		System.out.println("TitulazioZerrendaFragment -- OnCreateView");
		// Zerrenda pertsonalizatua eskuratu, eremu aukeratuak dituena
		try {
			zerrendaView = inflater.inflate(R.layout.zerrenda, null);
			hutsik = (TextView) zerrendaView.findViewById(R.id.hutsik);
			System.out.println(zerrendaView.toString());
		} catch (Exception e) {
			Log.i(Konstanteak.LOGTAG + " JSOUP", "Exception!" + e.getMessage(),
					e);
			// tv = (TextView) zerrendaView.findViewById(R.id.zerrenda_hutsa);
		}
		return zerrendaView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// setRetainInstance(true);

		View TitulazioInfoFrame = getActivity().findViewById(
				R.id.titulazio_info_fragment);
		TitulazioInfoFragment viewer = (TitulazioInfoFragment) getFragmentManager()
				.findFragmentById(R.id.titulazio_info_fragment);
		titulazioInfoFragment = viewer;

		if (savedInstanceState != null) {
			// Restore last state for checked position.
			unekoIndex = savedInstanceState.getInt("unekoAukera", 0);
		}

		ListView zerrenda = getListView();

		if (TitulazioInfoFrame != null
				&& TitulazioInfoFrame.getVisibility() == View.VISIBLE) {
			System.out.println("TitulazioZerrendaFragment --  -- Apaisatuan");
			try {
				progressDialog2 = ProgressDialog.show(getActivity(),
						" Lanean...", " Titulazioa eskuratzen", true, false);
			} catch (Exception e) {
				Log.e("progressDialog errorea: ", e.getMessage());
			}
			zerrenda.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			// String item = (String) getListAdapter().getItem(unekoIndex);
			// mListener.onArticleSelected(item);
			// viewer.setText(item);
			viewer.kenduRounded();
			// viewer.setEdukia(titulazioa);
			zerrenda.setItemChecked(unekoIndex, true);
		}

		zerrenda.setSelection(unekoIndex);

	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		unekoIndex = position;
		// Cursor item = (Cursor) getListAdapter().getItem(position);
		// final String titulazioUrl = "";
		// final String izenburua = "";

		Cursor kurtsorea = (Cursor) l.getItemAtPosition(position);
		final String titulazioUrl = kurtsorea.getString(kurtsorea
				.getColumnIndex(DbUtil.KEY_URL));
		final String izenburua = kurtsorea.getString(kurtsorea
				.getColumnIndex(DbUtil.KEY_IZENBURUA));

		final TitulazioInfoFragment fragment = (TitulazioInfoFragment) getFragmentManager()
				.findFragmentById(R.id.titulazio_info_fragment);
		if (fragment != null && fragment.isInLayout()) {

			l.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			l.setItemChecked(position, true);
			l.setSelection(position);
			fragment.setEdukia(titulazioUrl, izenburua, false);
		} else {
			Intent intent = new Intent();
			intent.setClass(getActivity().getApplicationContext(),
					TitulazioInfoActivity.class);
			intent.putExtra("titulazioUrl", titulazioUrl);
			intent.putExtra("izenburua", izenburua);
			startActivity(intent);
		}

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("unekoAukera", unekoIndex);
		if (progressDialog2 != null && progressDialog2.isShowing()) {
			progressDialog2.dismiss();
			System.out.println("onSaveInstanceState -- dismiss dialg");
		}

	}

	public interface OnArticleSelectedListener {
		public void onArticleSelected(String item);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnArticleSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnArticleSelectedListener");
		}
	}

	// LoaderManager.LoaderCallbacks<Cursor> methods

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// Provider-en bidez zerrenda kargatu
		// iragazteko paramentroak kontuan izan behar dira: titulazio-mota,
		// lurraldea eta bilaketa irizpidea
		// Erabiliko diren eremuen definizioa
		String[] projection = {
				DbUtil.KEY_ID,
				DbUtil.KEY_IZENBURUA,
				DbUtil.KEY_FAKULTATEA + " || ' - '  || "
						+ DbUtil.KEY_UNIBERTSITATEA + " as "
						+ DbUtil.KEY_UNIBERTSITATEAMOTA,
				DbUtil.KEY_HERRIA + " || ' - '  || " + DbUtil.KEY_LURRALDEA
						+ " as " + DbUtil.KEY_HELBIDEA, DbUtil.KEY_URL };
		List<String> param_values = new ArrayList<String>();

		// Bilaketarako irizpideak zehaztu
		String params = "(1=1)";

		String[] param_values_string;
		if (this.titulazioMota.indexOf("Guztiak") < 0) {
			params = params + " and lower(" + DbUtil.KEY_IKASKETA + ")=?";
			param_values.add(this.titulazioMota.trim().toLowerCase());
		}
		if (this.titulazioLurraldea.indexOf("Guztiak") < 0) {
			params = params + " and lower(" + DbUtil.KEY_LURRALDEA + ")=?";
			param_values.add(this.titulazioLurraldea.trim().toLowerCase());
		}
		if (this.titulazioBilaketaIrizpidea.length() > 0) {
			params = params + " and " + DbUtil.KEY_IZENBURUA + " like ?";
			param_values.add("%"
					+ this.titulazioBilaketaIrizpidea.trim().toLowerCase()
					+ "%");
		}
		param_values_string = new String[param_values.size()];
		for (int i = 0; i < param_values.size(); i++) {
			param_values_string[i] = param_values.get(i);
		}

		// Kurtsorearen kargatzailea lortua Providerren bitartez
		CursorLoader cursorLoader = new CursorLoader(getActivity(),
				TitulazioZerrendaProvider.CONTENT_URI, projection, params,
				param_values_string, null);

		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		System.out.println("TitulazioZerrendaFrgament -- onLoadFinished");
		if (cursor.moveToFirst()) {
			// final TitulazioInfoFragment fragment = (TitulazioInfoFragment)
			// getFragmentManager()
			// .findFragmentById(R.id.titulazio_info_fragment);
			final View TitulazioInfoFrame = getActivity().findViewById(
					R.id.titulazio_info_fragment);
			if (TitulazioInfoFrame != null
					&& TitulazioInfoFrame.getVisibility() == View.VISIBLE) {
				System.out.println("UnekoIndex: " + unekoIndex);
				cursor.moveToPosition(unekoIndex);

				final String titulazioUrl = cursor.getString(cursor
						.getColumnIndex(DbUtil.KEY_URL));
				final String izenburua = cursor.getString(cursor
						.getColumnIndex(DbUtil.KEY_IZENBURUA));
				System.out.println("Datuak: " + izenburua + " , "
						+ titulazioUrl);

				// l.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
				// l.setItemChecked(position, true);
				// l.setSelection(position);

				new Thread() {
					@Override
					public void run() {
						try {
							htmlJsoupHelper hh = new htmlJsoupHelper(
									titulazioUrl, getActivity()
											.getApplicationContext());
							Titulazioa emaitza = hh
									.titulazioaEskuratu(izenburua);
							// fragment.kenduRounded();
							System.out.println("onLoadFinished -- \n"
									+ emaitza.toString() + "  "
									+ emaitza.isEmpty());
							if (!emaitza.isEmpty()) {
								// fragment.setEdukia(emaitza);
								// getListView().setItemChecked(unekoIndex,
								// true);
								Message msg = new Message();
								msg.what = 1;
								Bundle bundle = new Bundle();
								bundle.putParcelable("titulazioa", emaitza);
								bundle.putInt("unekoIndex", unekoIndex);
								msg.setData(bundle);
								handler.sendMessage(msg);
							} else {
								handler.sendEmptyMessage(0);
							}
						} catch (Exception e) {
							Log.v("kk", e.getMessage());
							handler.sendEmptyMessage(0);
						} /*
						 * finally { if (progressDialog2 != null &&
						 * progressDialog2.isShowing()){
						 * progressDialog2.dismiss(); System.out.println(
						 * "zerrenda karga bukatuta. dialogoa kendu"); } }
						 */
					}

				}.start();
			}
		} else {
			// Kurtsorea hutsik -- Bilaketak ez du emaitzarik
			System.out
					.println("Ez da ezer topatu zure bilaketa irizpidearekin.");
			handler.sendEmptyMessage(2);
		}

		adapter.swapCursor(cursor);
		adapter.notifyDataSetChanged();
		// setListAdapter(adapter);
		System.out.println("Zerrendaren karga bukatuta");

	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}

}
