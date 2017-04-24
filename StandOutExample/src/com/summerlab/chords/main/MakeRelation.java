package com.summerlab.chords.main;

import java.util.ArrayList;

public class MakeRelation {
	private ArrayList<Artist> artists;
	private ArrayList<Singer> singers;
	private ArrayList<Chord> chords;
	private ArrayList<Song> songs;
	
	public MakeRelation() {
		artists = new ArrayList<Artist>();
		singers = new ArrayList<Singer>();
		chords = new ArrayList<Chord>();
		songs = new ArrayList<Song>();
	}
	
	public void addArtist(Artist artist)
	{
		artists.add(artist);
	}
	
	public void addSinger(Singer singer)
	{
		singers.add(singer);
	}
	
	public void addChord(Chord chord)
	{
		chords.add(chord);
	}
	
	public void addSong(Song song)
	{
		songs.add(song);
	}
	
	public ArrayList<Artist> getArtists()
	{
		return artists;
	}
	
	public ArrayList<Singer> getSingers()
	{
		return singers;
	}
	
	public ArrayList<Song> getSongs()
	{
		return songs;
	}
	
	public ArrayList<Chord> getChords()
	{
		return chords;
	}
}
