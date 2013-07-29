package com.yao_guet;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by lixiang on 13-7-8.
 */
public class AllAppProvider extends ContentProvider {
    static final Uri CONTENT_URI = Uri.parse("content://com.yao_guet/allapp?notify=true");
    private static final String DATABASE_NAME = "launcher.db";
    private static final String TABLE_NAME = "allapp";
    private static final int DATABASE_VERSION = 12;
    private static final  String TAG = "AllAppProvider";
    public static DatabaseHelper mOpenHelper;



    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        Log.e("lixiang","mOpenHelper= "+mOpenHelper);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings2, String s2) {
        SqlArguments sqlArguments = new SqlArguments(uri,s,strings2);
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(sqlArguments.table);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Cursor result = qb.query(db,strings,sqlArguments.where,sqlArguments.args,null,null,s2);
        result.setNotificationUri(getContext().getContentResolver(),uri);
        return result;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final long rowId = db.insert("allapp",null,contentValues);
        if(rowId <= 0)
            return null;
        uri = ContentUris.withAppendedId(uri, rowId);
        sendNotify(uri);
        return uri;
    }

    private void sendNotify(Uri uri) {
        String notify = uri.getQueryParameter("notify");
        Log.e(TAG,"Context = "+getContext());
        if(notify == null || "true".equals(notify))
            try {
                getContext().getContentResolver().notifyChange(uri, null);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }


    }

    public void deletTable(AllAppProvider provider){
        Log.e(TAG, "mOpenHelper = " + mOpenHelper);
        SQLiteDatabase db = provider.mOpenHelper.getWritableDatabase();
        String sql = "DELETE FROM "+TABLE_NAME+";";
        db.execSQL(sql);
//        revertSql(db);
    }



    private void revertSql(SQLiteDatabase db) {
        String sql = "update allapp set seq=0 where name='"+TABLE_NAME+"'";
        db.execSQL(sql);
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        SqlArguments args = new SqlArguments(uri,s,strings);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count = db.delete(args.table,args.where,args.args);
        if(count>0)
            sendNotify(uri);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
    static class DatabaseHelper extends SQLiteOpenHelper{

        private final Context mContext;

        DatabaseHelper(Context context){
           super(context,DATABASE_NAME, null, DATABASE_VERSION);
            mContext = context;
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.e("lixiang ", "db execSQL");
            db.execSQL("CREATE TABLE allapp (" +
                    "_id INTEGER PRIMARY KEY," +
                    "package TEXT," +
                    "activity TEXT," +
                    "location INTEGER," +
                    "icon BLOB," +
                    "title TEXT" +
                    ");");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i2) {

        }
    }

    static class SqlArguments {
        public final String table;
        public final String where;
        public final String[] args;

        SqlArguments(Uri url, String where, String[] args) {
            if (url.getPathSegments().size() == 1) {
                this.table = url.getPathSegments().get(0);
                this.where = where;
                this.args = args;
            } else if (url.getPathSegments().size() != 2) {
                throw new IllegalArgumentException("Invalid URI: " + url);
            } else if (!TextUtils.isEmpty(where)) {
                throw new UnsupportedOperationException("WHERE clause not supported: " + url);
            } else {
                this.table = url.getPathSegments().get(0);
                this.where = "_id=" + ContentUris.parseId(url);
                this.args = null;
            }
        }

        SqlArguments(Uri url) {
            if (url.getPathSegments().size() == 1) {
                table = url.getPathSegments().get(0);
                where = null;
                args = null;
            } else {
                throw new IllegalArgumentException("Invalid URI: " + url);
            }
        }
    }
}
