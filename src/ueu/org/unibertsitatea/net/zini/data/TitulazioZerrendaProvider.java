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

import ueu.org.unibertsitatea.net.zini.data.DbUtil.DatabaseHelper;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
//import android.database.sqlite.SQLiteConstraintException;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteQueryBuilder;
import info.guardianproject.database.sqlcipher.SQLiteConstraintException;
import info.guardianproject.database.sqlcipher.SQLiteDatabase;
import info.guardianproject.database.sqlcipher.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;


public class TitulazioZerrendaProvider extends ContentProvider {

    private DbUtil dbUtil;
    private DatabaseHelper mDB;
    
    private static final String AUTHORITY = "ueu.org.unibertsitatea.net.zini.data.TitulazioZerrendaProvider";
    public static final int TITULAZIOAK = 100;
    public static final int TITULAZIOA_ID = 110;

    private static final String TITULAZIOAK_BASE_PATH = "titulazioak";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + TITULAZIOAK_BASE_PATH);

    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/uninet-zini";
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/uninet-zini";

    private static final UriMatcher sURIMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);

    private static final String DEBUG_TAG = "UninetZerrendaProvider";
    static {
        sURIMatcher.addURI(AUTHORITY, TITULAZIOAK_BASE_PATH, TITULAZIOAK);
        sURIMatcher.addURI(AUTHORITY, TITULAZIOAK_BASE_PATH + "/#", TITULAZIOA_ID);
    }

    @Override
    public boolean onCreate() {
        dbUtil = new DbUtil(getContext());
		dbUtil.open();
		mDB = dbUtil.dbHelper;
        return true;
    }
    

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(DbUtil.TABLE_NAME);
        
        //Cursor cursor;

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
        case TITULAZIOA_ID:
            queryBuilder.appendWhere(DbUtil.KEY_ID + "="
                    + uri.getLastPathSegment());
        	//cursor = dbUtil.getTitulazioa(uri.getLastPathSegment());
        	//cursor = queryBuilder.query(mDB.getReadableDatabase(),
        	//                projection, selection, selectionArgs, null, null, sortOrder);
            break;
        case TITULAZIOAK:
            //TODO Iragazi aurretik zerbait badator (izenburua, ikasketa edo lurraldea)
            break;
        default:
            throw new IllegalArgumentException("Unknown URI");
        }
        String query = queryBuilder.buildQuery(projection, selection, selectionArgs, null, null, sortOrder, null);
        //TODO begiratu era kurtsore hutsa bidaltzen duen.
        Cursor cursor = queryBuilder.query(mDB.getReadableDatabase(query),
                projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        
        return cursor;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = mDB.getWritableDatabase(selection);
        int rowsAffected = 0;
        switch (uriType) {
        case TITULAZIOAK:
            rowsAffected = sqlDB.delete(DbUtil.TABLE_NAME,
                    selection, selectionArgs);
            break;
        case TITULAZIOA_ID:
            String id = uri.getLastPathSegment();
            if (TextUtils.isEmpty(selection)) {
                rowsAffected = sqlDB.delete(DbUtil.TABLE_NAME,
                		DbUtil.KEY_ID + "=" + id, null);
            } else {
                rowsAffected = sqlDB.delete(DbUtil.TABLE_NAME,
                        selection + " and " + DbUtil.KEY_ID + "=" + id,
                        selectionArgs);
            }
            break;
        default:
            throw new IllegalArgumentException("Unknown or Invalid URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsAffected;
    }

    @Override
    public String getType(Uri uri) {
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
        case TITULAZIOAK:
            return CONTENT_TYPE;
        case TITULAZIOA_ID:
            return CONTENT_ITEM_TYPE;
        default:
            return null;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        if (uriType != TITULAZIOAK) {
            throw new IllegalArgumentException("Invalid URI for insert");
        }
        SQLiteDatabase sqlDB = mDB.getWritableDatabase(null);
        try {
            long newID = sqlDB.insertOrThrow(DbUtil.TABLE_NAME,
                    null, values);
            if (newID > 0) {
                Uri newUri = ContentUris.withAppendedId(uri, newID);
                getContext().getContentResolver().notifyChange(uri, null);
                return newUri;
            } else {
                throw new SQLException("Failed to insert row into " + uri);
            }
        } catch (SQLiteConstraintException e) {
            Log.i(DEBUG_TAG, "Ignoring constraint failure.");
        }
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = mDB.getWritableDatabase(selection);

        int rowsAffected;

        switch (uriType) {
        case TITULAZIOA_ID:
            String id = uri.getLastPathSegment();
            StringBuilder modSelection = new StringBuilder(DbUtil.KEY_ID
                    + "=" + id);

            if (!TextUtils.isEmpty(selection)) {
                modSelection.append(" AND " + selection);
            }

            rowsAffected = sqlDB.update(DbUtil.TABLE_NAME,
                    values, modSelection.toString(), null);
            break;
        case TITULAZIOAK:
            rowsAffected = sqlDB.update(DbUtil.TABLE_NAME,
                    values, selection, selectionArgs);
            break;
        default:
            throw new IllegalArgumentException("Unknown URI");
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsAffected;
    }
}