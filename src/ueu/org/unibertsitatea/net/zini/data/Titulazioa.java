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

package ueu.org.unibertsitatea.net.zini.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Titulazioa implements Parcelable {
	public String izenburua;
	public String ikasketa;
	public String ezaugarriak;
	public String kredituak;
	public String unibertsitatea;
	public String unibertsitate_mota;
	public String fakultatea;
	public String herria;
	public String lurraldea;
	public String url;
	public String info_url;

	public Titulazioa(String izenburua, String ikasketa, String unibertsitatea,
			String fakultatea, String lurraldea, String herria) {
		this.izenburua = izenburua;
		this.ikasketa = ikasketa;
		this.unibertsitatea = unibertsitatea;
		this.fakultatea = fakultatea;
		this.lurraldea = lurraldea;
		this.herria = herria;
	}

	public Titulazioa() {
		this.izenburua = "";
		this.ikasketa = "";
		this.ezaugarriak = "";
		this.kredituak = "";
		this.unibertsitatea = "";
		this.unibertsitate_mota = "";
		this.fakultatea = "";
		this.herria = "";
		this.lurraldea = "";
		this.info_url = "";
	}

	public void setizenburua(String izenburua) {
		this.izenburua = izenburua;
	}

	public void setfakultatea(String fakultatea) {
		this.fakultatea = fakultatea;
	}

	public void setherria(String herria) {
		this.herria = herria;
	}

	public void setlurraldea(String lurraldea) {
		this.lurraldea = lurraldea;
	}

	public void setezaugarriak(String ezaugarriak) {
		this.ezaugarriak = ezaugarriak;
	}

	public void setkredituak(String kredituak) {
		this.kredituak = kredituak;
	}

	public void setunibertsitatea(String unibertsitatea) {
		this.unibertsitatea = unibertsitatea;
	}

	public void setunibertsitate_mota(String unibertsitate_mota) {
		this.unibertsitate_mota = unibertsitate_mota;
	}

	public void setikasketa(String ikasketa) {
		this.ikasketa = ikasketa;
	}

	public boolean isEmpty() {
		return this.izenburua == "" && this.ikasketa == ""
				&& this.ezaugarriak == "" && this.kredituak == ""
				&& this.unibertsitatea == "" && this.unibertsitate_mota == ""
				&& this.fakultatea == "" && this.herria == ""
				&& this.lurraldea == "";
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("*Titulazioa*\n");
		sb.append("izenburua:" + this.izenburua + "\n");
		sb.append("ikasketa:" + this.ikasketa + "\n");
		sb.append("ezaugarriak:" + this.ezaugarriak + "\n");
		sb.append("kredituak:" + this.kredituak + "\n");
		sb.append("unibertsitatea:" + this.unibertsitatea + "\n");
		sb.append("unibertsitate_mota:" + this.unibertsitate_mota + "\n");
		sb.append("fakultatea:" + this.fakultatea + "\n");
		sb.append("herria:" + this.herria + "\n");
		sb.append("lurraldea:" + this.lurraldea + "\n");
		sb.append("url:" + this.url + "\n");
		sb.append("info_url:" + this.info_url + "\n");
		return sb.toString();
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeString(izenburua);
		dest.writeString(ikasketa);
		dest.writeString(ezaugarriak);
		dest.writeString(kredituak);
		dest.writeString(unibertsitatea);
		dest.writeString(unibertsitate_mota);
		dest.writeString(fakultatea);
		dest.writeString(herria);
		dest.writeString(lurraldea);
		dest.writeString(info_url);

	}

	public static final Parcelable.Creator<Titulazioa> CREATOR = new Parcelable.Creator<Titulazioa>() {
		public Titulazioa createFromParcel(Parcel in) {
			return new Titulazioa(in);
		}

		public Titulazioa[] newArray(int size) {
			return new Titulazioa[size];
		}
	};

	private Titulazioa(Parcel in) {
		System.out.println("Parcelabea eskuratzen: " + in.toString());
		this.izenburua = in.readString();
		this.ikasketa = in.readString();
		this.ezaugarriak = in.readString();
		this.kredituak = in.readString();
		this.unibertsitatea = in.readString();
		this.unibertsitate_mota = in.readString();
		this.fakultatea = in.readString();
		this.herria = in.readString();
		this.lurraldea = in.readString();
		this.info_url = in.readString();
	}
}