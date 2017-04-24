package com.summerlab.chords.main;

public class CategoryData {
	private int imageRes;
	private String title;
	private int count;

	public CategoryData(int imageRes, String title, int count) {
		this.imageRes = imageRes;
		this.title = title;
		this.count = count;
	}

	public void setImageRes(int imageRes) {
		this.imageRes = imageRes;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getImageRes() {
		return this.imageRes;
	}

	public String getTitle() {
		return this.title;
	}

	public int getCount() {
		return this.count;
	}
}
