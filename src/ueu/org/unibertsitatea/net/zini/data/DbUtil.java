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

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
import info.guardianproject.database.sqlcipher.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
import info.guardianproject.database.sqlcipher.SQLiteOpenHelper;
import android.util.Log;

public class DbUtil {

	// Taulako eremuak
	public static final String KEY_ID = "_id";
	public static final String KEY_IZENBURUA = "izenburua";
	public static final String KEY_IKASKETA = "ikasketa";
	public static final String KEY_EZAUGARRIA = "ezaugarriak";
	public static final String KEY_KREDITUAK = "kredituak";
	public static final String KEY_UNIBERTSITATEA = "unibertsitatea";
	public static final String KEY_UNIBERTSITATEAMOTA = "unibertsitate_mota";
	public static final String KEY_FAKULTATEA = "fakultatea";
	public static final String KEY_HERRIA = "herria";
	public static final String KEY_LURRALDEA = "lurraldea";
	public static final String KEY_URL = "url";
	public static final String KEY_URL_INFO = "url_info";
	public static final String KEY_OSORIK = "osorik";

	// AS balioetarako
	public static final String KEY_HELBIDEA = "helbidea";
	public static final String KEY_UNIDATUAK = "uni";

	// DB izena
	private static final String DB_NAME = "uninet_zini";

	// Taula izena
	public static final String TABLE_NAME = "titulazioak";
	public static final String TABLE_NAME2 = "iraungitze";
	private static final int DATABASE_VERSION = 1;

	// Taula sortzeko
	private static final String DATABASE_CREATE = "create table if not exists "
			+ TABLE_NAME + " (" + KEY_ID
			+ " integer primary key autoincrement, " + KEY_IZENBURUA
			+ " varchar not null, " + KEY_IKASKETA + " varchar, "
			+ KEY_EZAUGARRIA + " varchar, " + KEY_KREDITUAK + " varchar, "
			+ KEY_UNIBERTSITATEA + " varchar, " + KEY_UNIBERTSITATEAMOTA
			+ " varchar, " + KEY_FAKULTATEA + " varchar, " + KEY_HERRIA
			+ " varchar, " + KEY_LURRALDEA + " varchar, " + KEY_URL
			+ " varchar, " + KEY_URL_INFO
			+ " varchar, " + KEY_OSORIK + " integer DEFAULT 0" + " );";

	private static final String DB_SCHEMA = DATABASE_CREATE;

	// Iraungitze data kontrolatzeko taula sortu
	private static final String DB_SCHEMA2 = "create table if not exists " + TABLE_NAME2 +" ( _id integer primary key autoincrement, sortze_data varchar DEFAULT CURRENT_TIMESTAMP, sortze_data_int integer DEFAULT 0);";
	private Context context;
	public DatabaseHelper dbHelper;
	private SQLiteDatabase db;

	public DbUtil(Context context) {
		this.context = context;
		SQLiteDatabase.loadLibs(this.context);
		this.dbHelper = new DatabaseHelper(this.context);
	}

	public static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DB_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DB_SCHEMA);
			db.execSQL(DB_SCHEMA2);

			ContentValues values = new ContentValues();
			values.clear();
			values.put("sortze_data_int", new Date().getTime());
			db.insert("iraungitze", null, values);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion,

		int newVersion)

		{

			Log.v("DBUTIL", "Upgrading database from version " + oldVersion

			+ " to "

			+ newVersion + ", which will destroy all old data");

			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

			onCreate(db);

		}

	}

	public void open() {
		// Ezarri Datu-baseko informazioa zifratzeko gakoa
		String pass = "";
		if(db == null || !db.isOpen()){
			db = dbHelper.getWritableDatabase(pass);
		}
	}

	public void close() {

		dbHelper.close();

	}

	public void ezbatuDB(){
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME2);
		dbHelper.onCreate(db);
	}
	
	public Cursor getTitulazioa(String url) {

		return db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_URL
				+ " = '" + url + "'", null);
	}

	public String getIranugitzeData() {
		String emaitza = "";
		Cursor cur = db.rawQuery("SELECT sortze_data FROM iraungitze", null);
		if (cur.moveToFirst()) {
			emaitza = cur.getString(cur.getColumnIndex("sortze_data"));
		}
		cur.close();
		return emaitza;
	}

	public Cursor getTitulazioak(String izenburua, String ikasketa,
			String lurraldea) {
		System.out.println(izenburua + " " + ikasketa + " " + lurraldea);
		String sql = "Select *" + "," + KEY_FAKULTATEA + " || ' - '  || "
				+ KEY_UNIBERTSITATEA + " as uni " + "," + KEY_HERRIA
				+ " || ' - '  || " + KEY_LURRALDEA + " as helbidea " + "from "
				+ TABLE_NAME + " where (1=1)";

		if (ikasketa.indexOf("Guztiak") < 0) {
			sql = sql + " AND ikasketa like '%" + ikasketa.trim() + "%'";
		}
		if (lurraldea.indexOf("Guztiak") < 0) {
			sql = sql + " AND lurraldea like '%" + lurraldea.trim() + "%'";
		}
		if (izenburua.length() > 0) {
			sql = sql + " AND izenburua like '%" + izenburua.trim() + "%'";
		}

		sql = sql + " ORDER BY izenburua";

		return db.rawQuery(sql, null);
	}

	public Boolean titulazioaOsorik(String url) {
		Cursor cur;
		Boolean emaitza;
		cur = db.rawQuery("SELECT osorik FROM " + TABLE_NAME + " WHERE "
				+ KEY_URL + " = " + url, null);
		if (cur.getCount() == 1) {
			cur.moveToFirst();
			int osorik = cur.getInt(cur.getColumnIndex("osorik"));
			emaitza = (osorik == 1) ? true : false;
		} else {
			emaitza = false;
		}
		cur.close();
		return emaitza;
	}

	public Boolean titulazioaTaulaHutsik() {
		Cursor cur;
		cur = db.rawQuery(
				"SELECT count(*) FROM sqlite_master WHERE type='table' AND name='"
						+ TABLE_NAME + "'", null);
		cur.moveToFirst();
		Boolean emaitza = (cur.getInt(0) == 0) ? true : false;
		cur.close();
		return emaitza;
	}

	public void insertTitulazioa(Titulazioa titulazioa, Boolean osorik) {

		// db.execSQL("DELETE FROM " + TABLE_NAME);
		ContentValues values = new ContentValues();
		values.clear();

		if (titulazioa.izenburua.length() > 0) {
			values.put(KEY_IZENBURUA, titulazioa.izenburua);
		}
		if (titulazioa.ikasketa.length() > 0) {
			values.put(KEY_IKASKETA, titulazioa.ikasketa);
		}
		if (titulazioa.ezaugarriak.length() > 0) {
			values.put(KEY_EZAUGARRIA, titulazioa.ezaugarriak);
		}
		if (titulazioa.kredituak.length() > 0) {
			values.put(KEY_KREDITUAK, titulazioa.kredituak);
		}
		if (titulazioa.unibertsitatea.length() > 0) {
			values.put(KEY_UNIBERTSITATEA, titulazioa.unibertsitatea);
		}
		if (titulazioa.unibertsitate_mota.length() > 0) {
			values.put(KEY_UNIBERTSITATEAMOTA, titulazioa.unibertsitate_mota);
		}
		if (titulazioa.fakultatea.length() > 0) {
			values.put(KEY_FAKULTATEA, titulazioa.fakultatea);
		}
		if (titulazioa.herria.length() > 0) {
			values.put(KEY_HERRIA, titulazioa.herria);
		}
		if (titulazioa.lurraldea.length() > 0) {
			values.put(KEY_LURRALDEA, titulazioa.lurraldea);
		}
		if (titulazioa.url.length() > 0) {
			values.put(KEY_URL, titulazioa.url);
		}
		if (titulazioa.info_url.length() > 0) {
			values.put(KEY_URL_INFO, titulazioa.info_url);
		}
		if(osorik){
			values.put(KEY_OSORIK, 1);
		}

		db.insert(TABLE_NAME, null, values);

	}

	public void eguneratuTitulazioa(Titulazioa titulazioa) { 
		ContentValues values = new ContentValues();

		Log.v("Uninet dbUtil", "Titulazioa:" + titulazioa.toString());

		if (titulazioa.izenburua.length() > 0) {
			values.put(KEY_IZENBURUA, titulazioa.izenburua);
		}
		if (titulazioa.ikasketa.length() > 0) {
			values.put(KEY_IKASKETA, titulazioa.ikasketa);
		}
		if (titulazioa.ezaugarriak.length() > 0) {
			values.put(KEY_EZAUGARRIA, titulazioa.ezaugarriak);
		}
		if (titulazioa.kredituak.length() > 0) {
			values.put(KEY_KREDITUAK, titulazioa.kredituak);
		}
		if (titulazioa.unibertsitatea.length() > 0) {
			values.put(KEY_UNIBERTSITATEA, titulazioa.unibertsitatea);
		}
		if (titulazioa.unibertsitate_mota.length() > 0) {
			values.put(KEY_UNIBERTSITATEAMOTA, titulazioa.unibertsitate_mota);
		}
		if (titulazioa.fakultatea.length() > 0) {
			values.put(KEY_FAKULTATEA, titulazioa.fakultatea);
		}
		if (titulazioa.herria.length() > 0) {
			values.put(KEY_HERRIA, titulazioa.herria);
		}
		if (titulazioa.lurraldea.length() > 0) {
			values.put(KEY_LURRALDEA, titulazioa.lurraldea);
		}
		if (titulazioa.info_url.length() > 0) {
			values.put(KEY_URL_INFO, titulazioa.info_url);
		}

		values.put(KEY_OSORIK, 1); // zehaztu orain titulazioaren informazioa
									// osorik dagoela

		db.update(TABLE_NAME, values, "url=?", new String[] { titulazioa.url });

	}

	public Cursor fetchAllData() {

		return db.rawQuery("SELECT * " + "," + KEY_FAKULTATEA
				+ " || ' - '  || " + KEY_UNIBERTSITATEA + " as uni " + ","
				+ KEY_HERRIA + " || ' - '  || " + KEY_LURRALDEA
				+ " as helbidea " + "FROM " + TABLE_NAME + " ORDER BY "
				+ KEY_IZENBURUA + " ASC", null);

	}

}