package com.summerlab.chords.main;

public class Chord {
	private int chordID;
	private String _name;
	
	public Chord(int id, String name)
	{
		this.chordID = id;
		this._name = name;
	}
	
	public int getID()
	{
		return this.chordID;
	}
	
	public String getName()
	{
		return this._name;
	}
}
