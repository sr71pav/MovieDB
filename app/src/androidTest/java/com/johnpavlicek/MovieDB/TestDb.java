package com.johnpavlicek.MovieDB;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

/**
 * Created by johnpavlicek on 10/21/15.
 */
public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(MovieDBHelper.DATABASE_NAME);
    }

    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(MovieContract.MovieEntry.TABLE_NAME);

        mContext.deleteDatabase(MovieDBHelper.DATABASE_NAME);
        SQLiteDatabase db = new MovieDBHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created without both the location entry and weather entry tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + MovieContract.MovieEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> locationColumnHashSet = new HashSet<String>();
        locationColumnHashSet.add(MovieContract.MovieEntry._ID);
        locationColumnHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_NAME);
        locationColumnHashSet.add(MovieContract.MovieEntry.COLUMN_DESC);
        locationColumnHashSet.add(MovieContract.MovieEntry.COLUMN_IMAGE_LOCATION);
        locationColumnHashSet.add(MovieContract.MovieEntry.COLUMN_RATING);
        locationColumnHashSet.add(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            locationColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                locationColumnHashSet.isEmpty());
        db.close();

    }

    public void testMovieTable() {
        insertMovie();
    }

    public long insertMovie() {
        // First step: Get reference to writable database
        MovieDBHelper dbHelper = new MovieDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create ContentValues of what you want to insert
        ContentValues contentValues = TestUtilities.createStarWarsEntryValues();


        // Insert ContentValues into database and get a row ID back
        long movieID = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, contentValues);

        assertTrue("Movie row not created", movieID != -1);

        // Query the database and receive a Cursor back
        Cursor locationCursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                null,null,null,null,null,null);

        // Move the cursor to a valid database row
        assertTrue("Movie Query failed", locationCursor.moveToFirst());

        TestUtilities.validateCurrentRecord("Movie Table Failed", locationCursor, contentValues);

        // Finally, close the cursor and database
        locationCursor.close();
        db.close();

        return movieID;
    }
}
