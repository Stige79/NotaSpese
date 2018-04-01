package com.example.stige.articoli;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by Stige on 24/03/2018.
 */

class FeedReaderDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "dbarticoli.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + dbArticoli.ReaderArticles.FeedEntry.TABLE_NAME + " (" +
                    BaseColumns._ID + " INTEGER PRIMARY KEY, " +
                    dbArticoli.ReaderArticles.FeedEntry.DESCRIZIONE + " TEXT, " +
                    dbArticoli.ReaderArticles.FeedEntry.PREZZO + " TEXT, " +
                    dbArticoli.ReaderArticles.FeedEntry.STAGIONE + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + dbArticoli.ReaderArticles.FeedEntry.TABLE_NAME;

    public FeedReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
    public void insertArticolo(Articolo articolo)
    {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put(dbArticoli.ReaderArticles.FeedEntry.DESCRIZIONE, articolo.getDescrizione());

        cv.put(dbArticoli.ReaderArticles.FeedEntry.PREZZO, articolo.getPrezzo());

        cv.put(dbArticoli.ReaderArticles.FeedEntry.STAGIONE, articolo.getStagione());

        // Inserting Row

        long u = db.insert(dbArticoli.ReaderArticles.FeedEntry.TABLE_NAME, null, cv);


    }

    public Cursor getAllArticles()
    {

               // Select All Query

        String selectQuery = "SELECT  * FROM " + dbArticoli.ReaderArticles.FeedEntry.TABLE_NAME +" ORDER BY "+ dbArticoli.ReaderArticles.FeedEntry._ID ;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null) {
                cursor.moveToFirst();
        }
        // looping through all rows and adding to list

                // return contact list
        return cursor;
    }

    public void updateArticolo(Articolo articolo)
    {

      SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(dbArticoli.ReaderArticles.FeedEntry.DESCRIZIONE,articolo.getDescrizione()); //These Fields should be your String values of actual column names
        cv.put(dbArticoli.ReaderArticles.FeedEntry.PREZZO, articolo.getPrezzo());
        cv.put(dbArticoli.ReaderArticles.FeedEntry.STAGIONE,articolo.getStagione());

        db.update(dbArticoli.ReaderArticles.FeedEntry.TABLE_NAME,cv,dbArticoli.ReaderArticles.FeedEntry._ID + "=?",new String[]{String.valueOf(articolo.getId())});

    }

    public Cursor getFilteredArticlesByDescr(String filtro)
    {


        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(dbArticoli.ReaderArticles.FeedEntry.TABLE_NAME,null,dbArticoli.ReaderArticles.FeedEntry.DESCRIZIONE +" like ?",new String[]{"%"+filtro+"%"},null,null,null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        // looping through all rows and adding to list

        // return contact list
        return cursor;
    }

    public Cursor getFilteredArticlesById(int filtro)
    {


        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(dbArticoli.ReaderArticles.FeedEntry.TABLE_NAME,null,dbArticoli.ReaderArticles.FeedEntry._ID +" like ?",new String[]{"%"+filtro+"%"},null,null,null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        // looping through all rows and adding to list

        // return contact list
        return cursor;
    }

    public Cursor getFilteredArticlesByEstivo()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String estivo = "estivo";
        Cursor cursor = db.query(dbArticoli.ReaderArticles.FeedEntry.TABLE_NAME,null,dbArticoli.ReaderArticles.FeedEntry.STAGIONE +" like ?",new String[]{estivo},null,null,null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        // looping through all rows and adding to list

        // return contact list
        return cursor;
    }

    public Cursor getFilteredArticlesByInvernale()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String invernale = "invernale";
        Cursor cursor = db.query(dbArticoli.ReaderArticles.FeedEntry.TABLE_NAME,null,dbArticoli.ReaderArticles.FeedEntry.STAGIONE +" like ?",new String[]{invernale},null,null,null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        // looping through all rows and adding to list

        // return contact list
        return cursor;
    }

    public Cursor getFilteredArticlesByCalze()
    {
        String calze = "calze";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(dbArticoli.ReaderArticles.FeedEntry.TABLE_NAME,null,dbArticoli.ReaderArticles.FeedEntry.STAGIONE +" like ?",new String[]{calze},null,null,null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        // looping through all rows and adding to list

        // return contact list
        return cursor;
    }


    public void deleteAllArticles() {
        SQLiteDatabase db = this.getWritableDatabase();
        int doneDelete = 0;
        doneDelete = db.delete(dbArticoli.ReaderArticles.FeedEntry.TABLE_NAME, null , null);
        //Log.w(TAG, Integer.toString(doneDelete));

    }

    public void deleteArticle(long id) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(dbArticoli.ReaderArticles.FeedEntry.TABLE_NAME, dbArticoli.ReaderArticles.FeedEntry._ID+ "=?",new String[]{Long.toString(id)}) ;

    }

}


