package com.summerlab.chords.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import com.summerlab.chords.R;

import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper {

	private Context mycontext;
	private static String DB_NAME = "hopam.db";// the extension may be
												// .sqlite or .db
	private static String DB_PATH = "";
	private static String myDBNameString = "hopam.db";
	private final String ArtistTbl = "ArtistTbl";
	private final String SongTbl = "SongTbl";
	private final String ChordTbl = "ChordTbl";
	private final String FavoriteTbl = "FavoriteTbl";
	private final String Song_Author_Tbl = "Song_Author_Tbl";
	private final String Song_Singer_Tbl = "Song_Singer_Tbl";
	private final String Song_Chord_Tbl = "Song_Chord_Tbl";
	public static SQLiteDatabase myDataBase;
	private SQLiteDatabase appDataBase;
	private String TableName = "SongTbl";
	private final String signTextSong = "[S]";
	private final String signTextArtist = "[A]";
	private final String dataPath = "/data/data/";

	public DataBaseHelper(Context context) throws IOException {
		super(context, myDBNameString, null, 1);
		this.mycontext = context;
		if (android.os.Build.VERSION.SDK_INT >= 4.2) {
			DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
			
		} else {
			DB_PATH = dataPath + context.getPackageName() + "/databases/";
			
		}
		DB_NAME = "hopam.db";
		DB_NAME = DB_PATH + DB_NAME;
		// DB_NAME = "/data/data/hopamchuan.db";
		boolean dbexist = checkdatabase();
		if (dbexist) {
			opendatabase();
		} else {
			System.out.println("Database doesn't exist");
			copyDataBaseFromAsset();
		}
	}

	public void copyDataBaseFromAsset() throws IOException {
		InputStream in = mycontext.getAssets().open("hopam.db");
		
		String outputFileName = DB_NAME;
		File databaseFile = new File(DB_PATH);
		// check if databases folder exists, if not create one and its
		// subfolders
		if (!databaseFile.exists()) {
			databaseFile.mkdir();
		}

		OutputStream out = new FileOutputStream(outputFileName);

		byte[] buffer = new byte[1024];
		int length;

		while ((length = in.read(buffer)) > 0) {
			out.write(buffer, 0, length);
		}
		
		out.flush();
		out.close();
		in.close();
		opendatabase();
	}

	private boolean checkdatabase() {
		// SQLiteDatabase checkdb = null;
		boolean checkdb = false;
		try {
			String myPath = DB_NAME;
			File dbfile = new File(myPath);
			// checkdb =
			// SQLiteDatabase.openDatabase(myPath,null,SQLiteDatabase.OPEN_READWRITE);
			checkdb = dbfile.exists();
			
		} catch (SQLiteException e) {
			
		}
		return checkdb;
	}

	private void copydatabase() throws IOException {
		// Open your local db as the input stream
		InputStream myinput = mycontext.getAssets().open(DB_NAME);

		// Path to the just created empty db
		String outfilename = DB_NAME;

		// Open the empty db as the output stream
		OutputStream myoutput = new FileOutputStream(
				"/data/data/(packagename)/databases   /(datbasename).sqlite");

		// transfer byte to inputfile to outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myinput.read(buffer)) > 0) {
			myoutput.write(buffer, 0, length);
		}
		// Close the streams
		myoutput.flush();
		myoutput.close();
		myinput.close();
	}

	public void opendatabase() {
		// Open the database
		String mypath = DB_NAME;
		try {
			myDataBase = SQLiteDatabase.openDatabase(mypath, null,
					SQLiteDatabase.OPEN_READONLY);
		} catch (Exception e) {
			// TODO: handle exception
			
		}
	}

	@Override
	public synchronized void close() {
		if (myDataBase != null) {
			myDataBase.close();
		}
		super.close();
	}

	public void getTableName() {
		Cursor c = myDataBase.rawQuery(
				"SELECT name FROM sqlite_master WHERE type='table'", null);

		c.moveToFirst();
		do {
			
		} while (c.moveToNext());
		c.close();
	}

	/*
	 * public void getColumns() { myColumnList = new
	 * android.util.ArrayMap<String, String>(); Cursor ti =
	 * myDataBase.rawQuery("PRAGMA table_info(" + TableName + ")", null); if
	 * (ti.moveToFirst()) { do { System.out.println("column: " +
	 * ti.getString(1)); System.out.println("type: " + ti.getString(2));
	 * myColumnList.put(ti.getString(1), ti.getString(2));
	 * 
	 * } while (ti.moveToNext()); } ti.close(); }
	 */

	public String getData(String columnName, int index) {
		Cursor cur = myDataBase.rawQuery("SELECT " + columnName + " FROM "
				+ TableName, null);
		String data = "";
		int i = 0;
		try {
			while (cur.moveToNext()) {
				System.out.println(cur.getString(0));
				if (i == index) {
					data = cur.getString(0);
					break;
				}
				i++;
			}
		} catch (Exception e) {
			System.out.println("error in getLabelID in DB() :" + e);
		} finally {
			cur.close();
		}
		return data;
	}

	public void createNewDatabase() {

		try {
			ArrayList<String> select = new ArrayList<String>();
			select.add("_id");
			select.add("song_id");
			select.add("chord_id");
			// select.add("song_link");
			// select.add("song_content");
			// select.add("song_first_lyric");
			// select.add("song_date");
			// select.add("song_title_ascii");
			appDataBase = getWritableDatabase();
			Cursor ti = myDataBase.rawQuery("PRAGMA table_info(" + TableName
					+ ")", null);
			String createTableLineString = "CREATE TABLE IF NOT EXISTS "
					+ TableName + " (";
			if (ti.moveToFirst()) {
				createTableLineString += "" + ti.getString(1) + " "
						+ ti.getString(2);
				System.out.println("column: " + ti.getString(1));
				System.out.println("type: " + ti.getString(2));
				while (ti.moveToNext()) {
					System.out.println("column: " + ti.getString(1));
					System.out.println("type: " + ti.getString(2));
					String name = ti.getString(1);
					String type = ti.getString(2);
					if (select.contains(name)) {
						createTableLineString += ", " + name + " " + type;
					}
				}
			}
			ti.close();
			createTableLineString += ");";
			
			appDataBase.execSQL("DROP TABLE IF EXISTS " + TableName);
			// Create a Table in the Database.
			appDataBase.execSQL(createTableLineString);
			// Insert data to a Table
			Cursor cur = myDataBase
					.rawQuery("SELECT * FROM " + TableName, null);
			ContentValues values = new ContentValues();
			if (cur.moveToFirst()) {
				do {
					for (int i = 0; i < select.size(); i++) {
						values.put(select.get(i), cur.getString(cur
								.getColumnIndex(select.get(i))));
					}
					appDataBase.insert(TableName, null, values);
					values.clear();
					/*
					 * appDataBase .execSQL("INSERT INTO " + TableName +
					 * " (_id, song_id, song_title, song_link, song_content, song_first_lyric, song_date, song_title_ascii) VALUES ('"
					 * +
					 * cur.getInt(0)+"','"+cur.getInt(1)+"','"+cur.getString(2)+
					 * "','"
					 * +cur.getString(3)+"','"+cur.getString(4)+"','"+cur.getString
					 * (5)+"','"+cur.getString(6)+"','"+cur.getString(7)+"');");
					 */
				} while (cur.moveToNext());
			}
			cur.close();
		} catch (Exception e) {
			// TODO: handle exception
			
		} finally {
			appDataBase.close();
		}

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	public ArrayList<MakeRelation> searchBySongID(String input) {
		ArrayList<MakeRelation> songs_data = new ArrayList<MakeRelation>();
		if (input.isEmpty())
			return songs_data;
		Cursor cur_song_tbl = myDataBase.rawQuery("SELECT *FROM " + SongTbl
				+ " WHERE song_id=" + input, null);
		cur_song_tbl.moveToFirst();
		if (cur_song_tbl.getCount() == 0) {
			cur_song_tbl.close();
			return songs_data;
		}

		do {
			MakeRelation data = new MakeRelation();
			int songID = cur_song_tbl.getInt(cur_song_tbl
					.getColumnIndex("song_id"));
			String songTitle = cur_song_tbl.getString(cur_song_tbl
					.getColumnIndex("song_title"));
			String songLink = cur_song_tbl.getString(cur_song_tbl
					.getColumnIndex("song_link"));
			String songContent = cur_song_tbl.getString(cur_song_tbl
					.getColumnIndex("song_content"));
			String firstLyric = cur_song_tbl.getString(cur_song_tbl
					.getColumnIndex("song_first_lyric"));
			String songDate = cur_song_tbl.getString(cur_song_tbl
					.getColumnIndex("song_date"));
			String songTitleAscii = cur_song_tbl.getString(cur_song_tbl
					.getColumnIndex("song_title_ascii"));
			int isFavorite = cur_song_tbl.getInt(cur_song_tbl
					.getColumnIndex("song_isfavorite"));
			data.addSong(new Song(songID, songTitle, songLink, songContent,
					firstLyric, songDate, songTitleAscii, 1 == isFavorite));
			// get info of author
			Cursor cur_song_author_tbl = myDataBase.rawQuery("SELECT *FROM "
					+ Song_Author_Tbl + " WHERE song_id=" + songID, null);
			cur_song_author_tbl.moveToFirst();
			if (cur_song_author_tbl.getCount() != 0) {
				do {
					int artist_id = cur_song_author_tbl
							.getInt(cur_song_author_tbl
									.getColumnIndex("artist_id"));
					Cursor cur_artist_tbl = myDataBase
							.rawQuery("SELECT *FROM " + ArtistTbl
									+ " WHERE artist_id=" + artist_id, null);
					cur_artist_tbl.moveToFirst();
					String artist_name = cur_artist_tbl
							.getString(cur_artist_tbl
									.getColumnIndex("artist_name"));
					String artist_ascii = cur_artist_tbl
							.getString(cur_artist_tbl
									.getColumnIndex("artist_ascii"));
					data.addArtist(new Artist(artist_id, artist_name,
							artist_ascii));
					cur_artist_tbl.close();
				} while (cur_song_author_tbl.moveToNext());
			}
			cur_song_author_tbl.close();
			// get info of singer
			Cursor cur_song_singer_tbl = myDataBase.rawQuery("SELECT *FROM "
					+ Song_Singer_Tbl + " WHERE song_id=" + songID, null);
			cur_song_singer_tbl.moveToFirst();
			if (cur_song_singer_tbl.getCount() != 0) {
				do {
					int singer_id = cur_song_singer_tbl
							.getInt(cur_song_singer_tbl
									.getColumnIndex("artist_id"));
					Cursor cur_singer_tbl = myDataBase
							.rawQuery("SELECT *FROM " + ArtistTbl
									+ " WHERE artist_id=" + singer_id, null);
					cur_singer_tbl.moveToFirst();
					String singer_name = cur_singer_tbl
							.getString(cur_singer_tbl
									.getColumnIndex("artist_name"));
					String singer_ascii = cur_singer_tbl
							.getString(cur_singer_tbl
									.getColumnIndex("artist_ascii"));
					data.addSinger(new Singer(singer_id, singer_name,
							singer_ascii));
					cur_singer_tbl.close();
				} while (cur_song_singer_tbl.moveToNext());
			}
			cur_song_singer_tbl.close();
			// get chords of song
			Cursor cur_song_chord_tbl = myDataBase.rawQuery("SELECT *FROM "
					+ Song_Chord_Tbl + " WHERE song_id=" + songID, null);
			cur_song_chord_tbl.moveToFirst();
			if (cur_song_chord_tbl.getCount() != 0) {
				do {
					int chord_id = cur_song_chord_tbl.getInt(cur_song_chord_tbl
							.getColumnIndex("chord_id"));
					Cursor cur_chord_tbl = myDataBase.rawQuery("SELECT *FROM "
							+ ChordTbl + " WHERE chord_id=" + chord_id, null);
					cur_chord_tbl.moveToFirst();
					String chord_name = cur_chord_tbl.getString(cur_chord_tbl
							.getColumnIndex("chord_name"));
					data.addChord(new Chord(chord_id, chord_name));
					cur_chord_tbl.close();
				} while (cur_song_chord_tbl.moveToNext());
			}
			cur_song_chord_tbl.close();
			//
			songs_data.add(data);

		} while (cur_song_tbl.moveToNext());

		cur_song_tbl.close();
		return songs_data;
	}

	public ArrayList<MakeRelation> searchByArtistName(String artistName) {
		ArrayList<MakeRelation> result_data = new ArrayList<MakeRelation>();
		if (artistName.isEmpty())
			return result_data;
		Cursor cur_artist_tbl = myDataBase.rawQuery("SELECT *FROM " + ArtistTbl
				+ " WHERE artist_name like '" + artistName + "%'", null);
		cur_artist_tbl.moveToFirst();
		if (cur_artist_tbl.getCount() == 0) {
			cur_artist_tbl = myDataBase.rawQuery("SELECT *FROM " + ArtistTbl
					+ " WHERE artist_ascii like '" + artistName + "%'", null);
		}
		cur_artist_tbl.moveToFirst();
		if (cur_artist_tbl.getCount() == 0) {
			cur_artist_tbl.close();
			return result_data;
		}
		do {
			int artist_id = cur_artist_tbl.getInt(cur_artist_tbl
					.getColumnIndex("artist_id"));
			String artist_name = cur_artist_tbl.getString(cur_artist_tbl
					.getColumnIndex("artist_name"));
			String artist_ascii = cur_artist_tbl.getString(cur_artist_tbl
					.getColumnIndex("artist_ascii"));
			// ----------------Author
			Cursor cur_song_author_tbl = myDataBase.rawQuery("SELECT *FROM "
					+ Song_Author_Tbl + " WHERE artist_id=" + artist_id + "",
					null);
			cur_song_author_tbl.moveToFirst();
			if (cur_song_author_tbl.getCount() != 0) {
				MakeRelation data = new MakeRelation();
				data.addArtist(new Artist(artist_id, artist_name, artist_ascii));
				do {
					int song_id = cur_song_author_tbl
							.getInt(cur_song_author_tbl
									.getColumnIndex("song_id"));
					Cursor cur_song_tbl = myDataBase.rawQuery("SELECT *FROM "
							+ SongTbl + " WHERE song_id=" + song_id + "", null);
					cur_song_tbl.moveToFirst();
					if (cur_song_tbl.getCount() != 0) {
						String songTitle = cur_song_tbl.getString(cur_song_tbl
								.getColumnIndex("song_title"));
						String songLink = cur_song_tbl.getString(cur_song_tbl
								.getColumnIndex("song_link"));
						String songContent = cur_song_tbl
								.getString(cur_song_tbl
										.getColumnIndex("song_content"));
						String firstLyric = cur_song_tbl.getString(cur_song_tbl
								.getColumnIndex("song_first_lyric"));
						String songDate = cur_song_tbl.getString(cur_song_tbl
								.getColumnIndex("song_date"));
						String songTitleAscii = cur_song_tbl
								.getString(cur_song_tbl
										.getColumnIndex("song_title_ascii"));
						int isFavorite = cur_song_tbl.getInt(cur_song_tbl
								.getColumnIndex("song_isfavorite"));
						data.addSong(new Song(song_id, songTitle, songLink,
								songContent, firstLyric, songDate,
								songTitleAscii, 1 == isFavorite));
					}
					cur_song_tbl.close();
				} while (cur_song_author_tbl.moveToNext());
				result_data.add(data);
			}
			cur_song_author_tbl.close();
			// ---------------------Singer
			Cursor cur_song_singer_tbl = myDataBase.rawQuery("SELECT *FROM "
					+ Song_Singer_Tbl + " WHERE artist_id=" + artist_id + "",
					null);
			cur_song_singer_tbl.moveToFirst();
			if (cur_song_singer_tbl.getCount() != 0) {
				MakeRelation data = new MakeRelation();
				data.addSinger(new Singer(artist_id, artist_name, artist_ascii));
				do {
					int song_id = cur_song_singer_tbl
							.getInt(cur_song_singer_tbl
									.getColumnIndex("song_id"));
					Cursor cur_song_tbl = myDataBase.rawQuery("SELECT *FROM "
							+ SongTbl + " WHERE song_id=" + song_id + "", null);
					cur_song_tbl.moveToFirst();
					if (cur_song_tbl.getCount() != 0) {
						String songTitle = cur_song_tbl.getString(cur_song_tbl
								.getColumnIndex("song_title"));
						String songLink = cur_song_tbl.getString(cur_song_tbl
								.getColumnIndex("song_link"));
						String songContent = cur_song_tbl
								.getString(cur_song_tbl
										.getColumnIndex("song_content"));
						String firstLyric = cur_song_tbl.getString(cur_song_tbl
								.getColumnIndex("song_first_lyric"));
						String songDate = cur_song_tbl.getString(cur_song_tbl
								.getColumnIndex("song_date"));
						String songTitleAscii = cur_song_tbl
								.getString(cur_song_tbl
										.getColumnIndex("song_title_ascii"));
						int isFavorite = cur_song_tbl.getInt(cur_song_tbl
								.getColumnIndex("song_isfavorite"));
						data.addSong(new Song(song_id, songTitle, songLink,
								songContent, firstLyric, songDate,
								songTitleAscii, 1 == isFavorite));
					}
					cur_song_tbl.close();
				} while (cur_song_singer_tbl.moveToNext());
				result_data.add(data);
			}
			cur_song_singer_tbl.close();
			// ----------------------
		} while (cur_artist_tbl.moveToNext());
		cur_artist_tbl.close();

		return result_data;
	}

	// ------- Search Function I : make result list for auto completeTextView
	public ArrayList<String> searchFunctionI(String searchInput) {
		if (MainActivity.idList_completeTextview != null) {
			MainActivity.idList_completeTextview.clear();
		}
		ArrayList<String> searchOutput = new ArrayList<String>();
		// ------------ search in song database --------------
		ArrayList<String> idSongOutput = new ArrayList<String>();
		Cursor cur_song_tbl = myDataBase.rawQuery(
				"SELECT song_title, song_id FROM " + SongTbl
						+ " WHERE song_title like '" + searchInput
						+ "%' OR song_title_ascii like '" + searchInput + "%'",
				null);
		cur_song_tbl.moveToFirst();
		if (cur_song_tbl.getCount() != 0) {
			do {
				String songTitle = signTextSong
						+ cur_song_tbl.getString(cur_song_tbl
								.getColumnIndex("song_title"));
				searchOutput.add(songTitle);
				idSongOutput.add(""
						+ cur_song_tbl.getInt(cur_song_tbl
								.getColumnIndex("song_id")));
			} while (cur_song_tbl.moveToNext());
		}
		cur_song_tbl.close();
		MainActivity.idList_completeTextview.addAll(idSongOutput);
		// ------------- search in artist database -------------------
		ArrayList<String> idArtistOutput = new ArrayList<String>();
		Cursor cur_artist_tbl = myDataBase.rawQuery(
				"SELECT artist_name, artist_id FROM " + ArtistTbl
						+ " WHERE artist_name like '" + searchInput
						+ "%' OR artist_ascii like '" + searchInput + "%'",
				null);
		cur_artist_tbl.moveToFirst();
		if (cur_artist_tbl.getCount() != 0) {
			do {
				String artistName = signTextArtist
						+ cur_artist_tbl.getString(cur_artist_tbl
								.getColumnIndex("artist_name"));
				searchOutput.add(artistName);
				idArtistOutput.add(""
						+ cur_artist_tbl.getInt(cur_artist_tbl
								.getColumnIndex("artist_id")));
			} while (cur_artist_tbl.moveToNext());
		}
		cur_artist_tbl.close();
		MainActivity.idList_completeTextview.addAll(idArtistOutput);
		// -------------- return result ------------------
		return searchOutput;
	}

	// -------- Search Function II : make list of song - shortcut search
	public ArrayList<Song> searchFunctionII(String searchInput) {
		ArrayList<Song> searchOutput = new ArrayList<Song>();
		// ------------ search in song database --------------
		Cursor cur_song_tbl = myDataBase.rawQuery(
				"SELECT song_id, song_title, song_first_lyric, song_isfavorite FROM "
						+ SongTbl + " WHERE song_title like '" + searchInput
						+ "%' OR song_title_ascii like '" + searchInput + "%' ORDER BY song_title;",
				null);
		cur_song_tbl.moveToFirst();
		if (cur_song_tbl.getCount() != 0) {
			do {
				String songTitle = cur_song_tbl.getString(cur_song_tbl
						.getColumnIndex("song_title"));
				int songID = cur_song_tbl.getInt(cur_song_tbl
						.getColumnIndex("song_id"));
				String firstLyric = cur_song_tbl.getString(cur_song_tbl
						.getColumnIndex("song_first_lyric"));
				int isFavorite = cur_song_tbl.getInt(cur_song_tbl
						.getColumnIndex("song_isfavorite"));
				Song song = new Song(songID, songTitle, null, null, firstLyric,
						null, null, 1 == isFavorite);
				searchOutput.add(song);
			} while (cur_song_tbl.moveToNext());
		}
		cur_song_tbl.close();
		return searchOutput;
	}

	// -------- Search Function III : make list of author - shortcut search
	public ArrayList<Artist> searchFunctionIII(String searchInput) {
		ArrayList<Artist> searchOutput = new ArrayList<Artist>();
		// ------------- search in artist database -------------------
		Cursor cur_artist_tbl = myDataBase
				.rawQuery(
						"SELECT DISTINCT ArtistTbl.artist_name, ArtistTbl.artist_id as artistID FROM "
								+ ArtistTbl
								+ ", "
								+ Song_Author_Tbl
								+ " WHERE (ArtistTbl.artist_name like '"
								+ searchInput
								+ "%' OR ArtistTbl.artist_ascii like '"
								+ searchInput
								+ "%') AND ArtistTbl.artist_id=Song_Author_Tbl.artist_id  ORDER BY artist_name",
						null);
		cur_artist_tbl.moveToFirst();
		if (cur_artist_tbl.getCount() != 0) {
			do {
				int artist_id = cur_artist_tbl.getInt(cur_artist_tbl
						.getColumnIndex("artistID"));
				String artistName = cur_artist_tbl.getString(cur_artist_tbl
						.getColumnIndex("artist_name"));
				searchOutput.add(new Artist(artist_id, artistName, null));
			} while (cur_artist_tbl.moveToNext());
		}
		cur_artist_tbl.close();

		return searchOutput;
	}

	// -------- Search Function IV : make list of singer - shortcut search
	public ArrayList<Artist> searchFunctionIV(String searchInput) {
		ArrayList<Artist> searchOutput = new ArrayList<Artist>();
		// ------------- search in artist database -------------------
		Cursor cur_artist_tbl = myDataBase
				.rawQuery(
						"SELECT DISTINCT ArtistTbl.artist_name, ArtistTbl.artist_id as artistID FROM "
								+ ArtistTbl
								+ ", "
								+ Song_Singer_Tbl
								+ " WHERE (ArtistTbl.artist_name like '"
								+ searchInput
								+ "%' OR ArtistTbl.artist_ascii like '"
								+ searchInput
								+ "%') AND ArtistTbl.artist_id=Song_Singer_Tbl.artist_id ORDER BY artist_name;",
						null);
		cur_artist_tbl.moveToFirst();
		if (cur_artist_tbl.getCount() != 0) {
			do {
				int artist_id = cur_artist_tbl.getInt(cur_artist_tbl
						.getColumnIndex("artistID"));
				String artistName = cur_artist_tbl.getString(cur_artist_tbl
						.getColumnIndex("artist_name"));
				searchOutput.add(new Artist(artist_id, artistName, null));
			} while (cur_artist_tbl.moveToNext());
		}
		cur_artist_tbl.close();

		return searchOutput;
	}

	// ----------- make list song of a author - full search
	public ArrayList<Song> ListSong_Author(String searchInput) {
		ArrayList<Song> searchOutput = new ArrayList<Song>();
		// ------------- search in artist database -------------------
		Cursor cursor = myDataBase
				.rawQuery(
						"SELECT SongTbl.song_id as songID, song_title, song_first_lyric, song_isfavorite FROM "
								+ SongTbl
								+ ", "
								+ Song_Author_Tbl
								+ " WHERE Song_Author_Tbl.artist_id="
								+ searchInput
								+ " AND Song_Author_Tbl.song_id=SongTbl.song_id;",
						null);
		cursor.moveToFirst();
		if (cursor.getCount() != 0) {
			do {
				int songID = cursor.getInt(cursor.getColumnIndex("songID"));
				String songName = cursor.getString(cursor
						.getColumnIndex("song_title"));
				String first_lyric = cursor.getString(cursor
						.getColumnIndex("song_first_lyric"));
				int isFavorite = cursor.getInt(cursor
						.getColumnIndex("song_isfavorite"));
				searchOutput.add(new Song(songID, songName, null, null,
						first_lyric, null, null, 1 == isFavorite));
			} while (cursor.moveToNext());
		}
		cursor.close();
		return searchOutput;
	}

	// ----------- make list song of a singer - full search
	public ArrayList<Song> ListSong_Singer(String searchInput) {
		ArrayList<Song> searchOutput = new ArrayList<Song>();
		// ------------- search in artist database -------------------
		Cursor cursor = myDataBase
				.rawQuery(
						"SELECT SongTbl.song_id as songID, song_title, song_first_lyric, song_isfavorite FROM "
								+ SongTbl
								+ ", "
								+ Song_Singer_Tbl
								+ " WHERE Song_Singer_Tbl.artist_id="
								+ searchInput
								+ " AND Song_Singer_Tbl.song_id=SongTbl.song_id;",
						null);
		cursor.moveToFirst();
		if (cursor.getCount() != 0) {
			do {
				int songID = cursor.getInt(cursor.getColumnIndex("songID"));
				String songName = cursor.getString(cursor
						.getColumnIndex("song_title"));
				String first_lyric = cursor.getString(cursor
						.getColumnIndex("song_first_lyric"));
				int isFavorite = cursor.getInt(cursor
						.getColumnIndex("song_isfavorite"));
				searchOutput.add(new Song(songID, songName, null, null,
						first_lyric, null, null, 1 == isFavorite));
			} while (cursor.moveToNext());
		}
		cursor.close();
		return searchOutput;
	}

	// ----------- make list song of a singer - full search
	public ArrayList<Song> ListSong_Artist(String searchInput) {
		ArrayList<Song> searchOutput = new ArrayList<Song>();
		// ------------- search in artist database ---------------
		searchOutput.addAll(ListSong_Author(searchInput));
		searchOutput.addAll(ListSong_Singer(searchInput));
		return searchOutput;
	}

	// --------------
	public ArrayList<CategoryData> getCategoryDatas() {
		ArrayList<CategoryData> categoryDatas = new ArrayList<CategoryData>();
		// ---song
		Cursor cur_song_tbl = myDataBase.rawQuery("SELECT song_id FROM "
				+ SongTbl, null);
		CategoryData songData = new CategoryData(R.drawable.song, "Songs",
				cur_song_tbl.getCount());
		categoryDatas.add(songData);
		cur_song_tbl.close();
		// ------artist
		Cursor cur_song_author_tbl = myDataBase.rawQuery(
				"SELECT DISTINCT artist_id FROM " + Song_Author_Tbl, null);
		CategoryData authorData = new CategoryData(R.drawable.artist,
				"Authors", cur_song_author_tbl.getCount());
		categoryDatas.add(authorData);
		cur_song_author_tbl.close();
		// -------singer
		Cursor cur_song_singer_tbl = myDataBase.rawQuery(
				"SELECT DISTINCT artist_id FROM " + Song_Singer_Tbl, null);
		CategoryData singerData = new CategoryData(R.drawable.singer,
				"Singers", cur_song_singer_tbl.getCount());
		categoryDatas.add(singerData);
		cur_song_singer_tbl.close();
		// ------chord
		Cursor cur_chord_tbl = myDataBase.rawQuery("SELECT chord_id FROM "
				+ ChordTbl, null);
		CategoryData chordData = new CategoryData(R.drawable.chord, "Chords",
				cur_chord_tbl.getCount());
		categoryDatas.add(chordData);
		cur_chord_tbl.close();
		// ----------favorite
		Cursor cur_favorite_tbl = myDataBase.rawQuery("SELECT song_id FROM "
				+ SongTbl + " WHERE song_isfavorite=1;", null);
		CategoryData favoriteData = new CategoryData(R.drawable.heartfull,
				"Favorites", cur_favorite_tbl.getCount());
		categoryDatas.add(favoriteData);
		cur_favorite_tbl.close();

		return categoryDatas;
	}

	// ----- isFavorite song
	public boolean isFavorite(int song_id) {
		Cursor cur_favorite_tbl = myDataBase.rawQuery(
				"SELECT song_isfavorite FROM " + SongTbl + " WHERE song_id="
						+ song_id, null);
		cur_favorite_tbl.moveToFirst();
		int isFavorite = cur_favorite_tbl.getInt(cur_favorite_tbl
				.getColumnIndex("song_isfavorite"));
		cur_favorite_tbl.close();
		return (1 == isFavorite);

	}

	// --------- add Favorite song
	public void addFavorite(int song_id) {
		appDataBase = getWritableDatabase();
		appDataBase
				.execSQL("UPDATE SongTbl SET song_isfavorite=1 WHERE song_id="
						+ song_id + ";");
	}

	// --------- delete Favorite song
	public void deleteFavorite(int song_id) {
		appDataBase = getWritableDatabase();
		appDataBase
				.execSQL("UPDATE SongTbl SET song_isfavorite=0 WHERE song_id="
						+ song_id + ";");
	}

	// --------- get Favorite song
	public ArrayList<Song> getFavoriteSong(String searchInput) {
		ArrayList<Song> searchOutput = new ArrayList<Song>();
		Cursor cursor = myDataBase.rawQuery(
				"SELECT song_id, song_title, song_isfavorite, song_first_lyric FROM " + SongTbl
						+ " WHERE (song_title like '" + searchInput
						+ "%' OR song_title_ascii like '" + searchInput
						+ "%') AND song_isfavorite=1 ORDER BY song_title;", null);
		cursor.moveToFirst();
		if (cursor.getCount() != 0) {
			do {
				String songName = cursor.getString(cursor
						.getColumnIndex("song_title"));
				int songID = cursor.getInt(cursor.getColumnIndex("song_id"));
				String first_lyric = cursor.getString(cursor
						.getColumnIndex("song_first_lyric"));
				int isFavorite = cursor.getInt(cursor
						.getColumnIndex("song_isfavorite"));
				searchOutput.add(new Song(songID, songName, null, null,
						first_lyric, null, null, 1 == isFavorite));
			} while (cursor.moveToNext());
		}
		cursor.close();
		return searchOutput;
	}

}
