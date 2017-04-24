package com.summerlab.chords.main;

public class Song {
	private int songID;
	private String songTitle;
	private String songTitleAscii;
	private String songLink;
	private String songContent;
	private String firstLyric;
	private String songDate;
	private boolean isFavorite;

	public Song(int songID, String songTitle, String songLink,
			String songContent, String firstLyric, String songDate,
			String songTitleAscii, boolean isfavorite) {
		this.songID = songID;
		this.songTitle = songTitle;
		this.songTitleAscii = songTitleAscii;
		this.songLink = songLink;
		this.songContent = songContent;
		this.firstLyric = firstLyric;
		this.songDate = songDate;
		this.isFavorite = isfavorite;
	}

	public int getID() {
		return this.songID;
	}

	public String getSongTitle() {
		return this.songTitle;
	}

	public String getSongTitleAscii() {
		return this.songTitleAscii;
	}

	public String getSongLink() {
		return this.songLink;
	}

	public String getSongContent() {
		return this.songContent;
	}

	public String getFirstLyric() {
		return this.firstLyric;
	}

	public String getSongDate() {
		return this.songDate;
	}
	
	public void setFavorite(boolean bol) {
		this.isFavorite = bol;
	}
	
	public boolean isFavorite() {
		return this.isFavorite;
	}
}
