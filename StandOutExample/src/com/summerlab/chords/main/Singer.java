package com.summerlab.chords.main;

public class Singer {
	private int singerID;
	private String _name;
	private String _asciiName;
	
	public Singer(int id, String name, String asciiName)
	{
		this.singerID = id;
		this._name = name;
		this._asciiName = asciiName;
	}
	
	public int getID()
	{
		return this.singerID;
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
