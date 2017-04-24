package com.summerlab.chords.main;

public class Artist {
	private int artistID;
	private String _name;
	private String _asciiName;
	
	public Artist(int id, String name, String asciiName)
	{
		this.artistID = id;
		this._name = name;
		this._asciiName = asciiName;
	}
	
	public int getID()
	{
		return this.artistID;
	}
	
	public String getName()
	{
		return this._name;
	}
	
	public String getAsciiName()
	{
		return this._asciiName;
	}
	
}
