package com.summerlab.wishcards.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.summerlab.wishcards.Utils.DanhMuc;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class DataBaseHelper extends SQLiteOpenHelper {

    //The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/YOUR_PACKAGE/databases/";

    private static String DB_NAME = "dbChucTet2016.s3db";

    private SQLiteDatabase myDataBase;

    private final Context myContext;

    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     *
     * @param context
     */

    public DataBaseHelper(Context context) {

        super(context, DB_NAME, null, 1);
        this.myContext = context;

        DB_PATH = "/data/data/" + myContext.getPackageName() + "/databases/";
    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     */
    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();

        if (dbExist) {
            //do nothing - database already exist
        } else {

            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();

            try {

                copyDataBase();

            } catch (IOException e) {

                throw new Error("Error copying database");

            }
        }



    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     *
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase() {

//        SQLiteDatabase checkDB = null;
//
//        try {
//            String myPath = DB_PATH + DB_NAME;
//            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
//
//        } catch (Exception e) {
//
//            //database does't exist yet.
//
//        }
//
//        if (checkDB != null) {
//
//            checkDB.close();
//
//        }

        return false;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     */
    private void copyDataBase() throws IOException {

        //Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

        

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDataBase() throws SQLException {

        //Open the database
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }

    @Override
    public synchronized void close() {

        if (myDataBase != null)
            myDataBase.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // Add your public helper methods to access and get content from the database.
    // You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
    // to you to create adapters for your views.

    public ArrayList<String> getAllTable(){


        ArrayList<String> tblNames = new ArrayList<String>();
        Cursor c = myDataBase.rawQuery("select name from sqlite_sequence", null);
        c.moveToFirst();

        while(!c.isAfterLast()){
            
            c.moveToNext();
        }
        c.close();
        return  tblNames;
    }
    public ArrayList<DanhMuc> getAllDM()
    {
        ArrayList<DanhMuc> allDM = new ArrayList<DanhMuc>();
        Cursor c = myDataBase.rawQuery("select * from DanhMuc", null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            DanhMuc dm= new DanhMuc();
            dm.setId(c.getInt(c.getColumnIndex("id")));
            dm.setName(c.getString(c.getColumnIndex("Name")));
            allDM.add(dm);
            c.moveToNext();

        }
        c.close();
        return  allDM;
    }
    public ArrayList<String> getContent(int id)
    {


        String query= "select * from listData where types = "+id;
        ArrayList<String> allContent= new ArrayList<String>();
        Cursor c = myDataBase.rawQuery(query, null);

        while(c.moveToNext())
        {
                allContent.add(c.getString(c.getColumnIndex("content")));

        }
        c.close();
        return allContent;
    }
}