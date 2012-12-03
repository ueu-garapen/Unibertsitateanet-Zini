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


import ueu.org.unibertsitatea.net.zini.data.Titulazioa;
import ueu.org.unibertsitatea.net.zini.data.htmlJsoupHelper;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TitulazioInfoFragment extends Fragment {
	
	private ProgressDialog progressDialog;
	TextView izenburua;
	TextView ikasketa_info;
	TextView ezaugarriak_info;
	TextView kredituak_info;
	TextView unibertsitatea_info;
	TextView unibertsitatea_mota_info;
	TextView fakultatea_info;
	TextView herria_info;
	TextView lurraldea_info;
	TextView info_gehigarria;
	
	Titulazioa titulazioa;
	
	
	private final Handler handler = new Handler() {
		@Override
		public void handleMessage(final Message msg) {
			
			if (msg.what == 0){
				if(!msg.getData().getBoolean("rounded")){
					kenduRounded();
				}
				System.out.println("dena ondo joan da");
				/*izenburua.setText(titulazioa.izenburua);
				ikasketa_info.setText(titulazioa.ikasketa);
				ezaugarriak_info.setText(titulazioa.ezaugarriak);
				kredituak_info.setText(titulazioa.kredituak);
				unibertsitatea_info.setText(titulazioa.unibertsitatea);
				unibertsitatea_mota_info.setText(titulazioa.unibertsitate_mota);
				fakultatea_info.setText(titulazioa.fakultatea);
				herria_info.setText(titulazioa.herria);
				lurraldea_info.setText(titulazioa.lurraldea);
				info_gehigarria.setMovementMethod(LinkMovementMethod
						.getInstance());
				info_gehigarria.setText((Html.fromHtml("<a href=\""
						+ titulazioa.info_url + "\">Informazio gehiago</a>")));
				*/
				setEdukia(titulazioa);
			}else{
				System.out.println("Errorea");
			}
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e("Test", "hello");
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		izenburua = (TextView) getView().findViewById(R.id.izenburua_info);
		ikasketa_info = (TextView) getView().findViewById(R.id.ikasketa_info);
		ezaugarriak_info = (TextView) getView().findViewById(R.id.ezaugarriak_info);
		kredituak_info = (TextView) getView().findViewById(R.id.kredituak_info);
		unibertsitatea_info = (TextView) getView().findViewById(R.id.unibertsitatea_info);
		unibertsitatea_mota_info = (TextView) getView().findViewById(R.id.unibertsitatea_mota_info);
		fakultatea_info = (TextView) getView().findViewById(R.id.fakultatea_info);
		herria_info = (TextView) getView().findViewById(R.id.herria_info);
		lurraldea_info = (TextView) getView().findViewById(R.id.lurraldea_info);
		info_gehigarria = (TextView) getView().findViewById(R.id.info_gehiago);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		System.out.println("info");
		View view = inflater.inflate(R.layout.titulazioa_info, container, false);
		
		return view;
	}

	public void setText(String item) {
		TextView view = (TextView) getView().findViewById(R.id.detailsText);
		view.setText(item);
	}
	
	public void kenduRounded() {
		System.out.println("markoa kendu");
		RelativeLayout rounded = (RelativeLayout) getView()
				.findViewById(R.id.layout_rounded);
		rounded.setBackgroundDrawable(null);
	}
	
	public void setEdukia(final Titulazioa titulazioa) {
		// Eskuratu pantailako elementuak
		

		izenburua.setText(titulazioa.izenburua);
		ikasketa_info.setText(titulazioa.ikasketa);
		ezaugarriak_info.setText(titulazioa.ezaugarriak);
		kredituak_info.setText(titulazioa.kredituak);
		unibertsitatea_info.setText(titulazioa.unibertsitatea);
		unibertsitatea_mota_info.setText(titulazioa.unibertsitate_mota);
		fakultatea_info.setText(titulazioa.fakultatea);
		herria_info.setText(titulazioa.herria);
		lurraldea_info.setText(titulazioa.lurraldea);
		if (titulazioa.info_url.trim() != ""){
			System.out.println("infor_url luzera: " + titulazioa.info_url.length());
			info_gehigarria.setMovementMethod(LinkMovementMethod
				.getInstance());
			info_gehigarria.setText((Html.fromHtml("<a href=\""
				+ titulazioa.info_url + "\">Informazio gehiago</a>")));
			if(!info_gehigarria.isShown()){
				info_gehigarria.setVisibility(View.VISIBLE);
			}
		}else{
			info_gehigarria.setVisibility(View.GONE);
		}
	}
	
	public void setEdukia(final String titulazioUrl, final String titulazioIzenburua, final Boolean rounded) {
		// Eskuratu pantailako elementuak
		
		progressDialog = ProgressDialog.show(getActivity(), " Lanean...",
				" Titulazioa eskuratzen", true, false);

		new Thread() {
			@Override
			public void run() {
				try {
					htmlJsoupHelper hh = new htmlJsoupHelper(titulazioUrl,
							getActivity().getApplicationContext());
					titulazioa = hh.titulazioaEskuratu(titulazioIzenburua);
					
					//fragment.setEdukia(emaitza);
					Message msg = new Message();
					msg.what = 0;
					Bundle bundle = new Bundle();
					bundle.putBoolean("rounded", rounded);
					msg.setData(bundle);
					handler.sendMessage(msg);
				} catch (Exception e) {
					handler.sendEmptyMessage(1);
					Log.d("titulzioa eskuratzen errorea", e.getMessage());
				} finally{
					if (progressDialog != null && progressDialog.isShowing())
						System.out.println("progressDialog ixteragoaz");
						progressDialog.dismiss();
				}
			}
		}.start();

	}
}
