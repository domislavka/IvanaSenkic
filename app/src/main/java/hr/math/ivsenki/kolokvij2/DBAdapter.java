package hr.math.ivsenki.kolokvij2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by ivsenki on 1/26/18.
 */

public class DBAdapter {
    static final String KEY_ROWID = "ID";
    static final String KEY_ROWID_2 = "ID_FILMA";
    static final String KEY_IME = "IME";
    static final String KEY_AUTOR = "AUTOR";
    static final String TAG = "DBAdapter";

    static final String DATABASE_NAME = "animfilm";
    static final String DATABASE_TABLE = "LIK";
    static final String DATABASE_TABLE_2 = "FILM";

    static final int DATABASE_VERSION = 1;

    static final String DATABASE_CREATE =
            "create table LIK (ID integer primary key autoincrement, "
                    + "IME text not null, AUTOR text not null);";

    static final String DATABASE_CREATE_2 =
            "create table FILM (ID_FILMA text primary key not null, "
                    + "ID integer not null);";

    final Context context;

    DatabaseHelper DBHelper;
    SQLiteDatabase db;

    public DBAdapter(Context ctx)
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            try {
                db.execSQL(DATABASE_CREATE);
                db.execSQL(DATABASE_CREATE_2);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            Log.w(TAG, "Upgrading db from" + oldVersion + "to"
                    + newVersion );
            db.execSQL("DROP TABLE IF EXISTS LIK");
            db.execSQL("DROP TABLE IF EXISTS FILM");
            onCreate(db);
        }
    }

    //---opens the database---
    public DBAdapter open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //---closes the database---
    public void close()
    {
        DBHelper.close();
    }

    //---insert a contact into the database---
    public long insertLik(String ime, String autor)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_IME, ime);
        initialValues.put(KEY_AUTOR, autor);
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    public long insertFilm(String nazivFilma, int id){

        //provjeravamo postoji li takav id u tablici LIK
        if(getLik(id) != null){

            ContentValues initialValues = new ContentValues();
            initialValues.put(KEY_ROWID_2, nazivFilma);
            initialValues.put(KEY_ROWID, id);
            return db.insert(DATABASE_TABLE_2, null, initialValues);
        }

        return -1;
    }

    //---brise lika iz tablice LIK i pripadni redak iz tablice FILM---
    public boolean deleteRedak(long rowId)
    {
        return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0
                && db.delete(DATABASE_TABLE_2, KEY_ROWID + "=" + rowId, null) > 0;
    }

    //---vrati sve Likove---
    public Cursor getAllLikovi() throws SQLException
    {
        return db.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_IME,
                KEY_AUTOR}, null, null, null, null, null);
    }

    //---vrati sve filmove---
    public Cursor getAllFilmovi() throws SQLException {
        return db.query(DATABASE_TABLE_2, new String[] {KEY_ROWID_2, KEY_ROWID},
                null, null, null, null, null);
    }

    //---vrati jednog LIKa---
    public Cursor getLik(long rowId) throws SQLException
    {
        Cursor mCursor =
                db.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                                KEY_IME, KEY_AUTOR}, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor getMickeyAuthor(String name) throws SQLException{
        Cursor mCursor =
                db.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_IME, KEY_AUTOR},
                        KEY_IME + " LIKE " + "\'" + name + "\'", null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public boolean deleteAll() {
        return db.delete(DATABASE_TABLE, null, null) > 0 && db.delete(DATABASE_TABLE_2, null, null) > 0;
    }


}
