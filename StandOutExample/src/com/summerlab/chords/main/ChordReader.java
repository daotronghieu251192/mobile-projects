package com.summerlab.chords.main;

import java.util.ArrayList;

public class ChordReader {

	String chord;
	public ArrayList<ChrodProperties> listHopAm = null;
	public ArrayList<ChrodProperties> listHopAm_old = null;
	public ArrayList<ChrodProperties> listHopAmTotal = null;

	public ChordReader(String chord) {
		this.chord = chord;
	}

	public String getChord() {
		String[] listofLine = chord.split("\n");
		String total = "";
		listHopAmTotal = new ArrayList<ChrodProperties>();
		for (int i = 0; i < listofLine.length; i++) {
			listHopAm = new ArrayList<ChrodProperties>();
			listHopAm_old = new ArrayList<ChrodProperties>();
			String resString = proccessChrod(listofLine[i]);
			total = total + resString;
		}
		return total;
	}

	public String proccessChrod(String eachOfline) {
		int indexOpenBracket = eachOfline.indexOf("[");
		int indexCloseBracket = eachOfline.indexOf("]");
		while (indexOpenBracket >= 0) {

			ChrodProperties myChrod = new ChrodProperties();
			ChrodProperties oldChrod = new ChrodProperties();
			myChrod.sign = false;
			System.out.println(indexOpenBracket + "---" + indexCloseBracket);

			myChrod.index = indexOpenBracket;
			oldChrod.index = myChrod.index;
			myChrod.length = indexCloseBracket - indexOpenBracket + 1;
			oldChrod.length = myChrod.length;
			myChrod.content = eachOfline.substring(indexOpenBracket,
					indexCloseBracket + 1);
			oldChrod.content = myChrod.content;
			

			int distance = eachOfline.indexOf("[", indexOpenBracket + 1)
					- indexCloseBracket - 1;
			if (eachOfline.indexOf("[", indexOpenBracket + 1) != -1
					&& distance <= myChrod.length) {
				StringBuilder str = new StringBuilder(eachOfline);
				str.insert(eachOfline.indexOf("[", indexOpenBracket + 1),
						new String(new char[myChrod.length - distance + 1])
								.replace('\0', ' '));
				eachOfline = str.toString();
			}

			listHopAm_old.add(oldChrod);
			
			if (myChrod.content.contains("/")) {
				String[] str = myChrod.content.split("/");
				myChrod.content = str[0] + "]";
				myChrod.length = myChrod.content.length();
			} 
			listHopAm.add(myChrod);
			listHopAmTotal.add(myChrod);
			
			indexOpenBracket = eachOfline.indexOf("[", indexOpenBracket + 1);
			indexCloseBracket = eachOfline.indexOf("]", indexCloseBracket + 1);

		}
		boolean isSpace = true;
		for (int i = 0; i < listHopAm.size(); i++) {
			isSpace = true;
			String toBeReplaced = listHopAm.get(i).content;
			if (i < listHopAm.size() - 2) {
				int closeFirst = listHopAm.get(i).index
						+ listHopAm.get(i).length - 1;
				int openSecond = listHopAm.get(i + 1).index;
				String subStr = eachOfline
						.substring(closeFirst + 1, openSecond);
				for (int k = 0; k < subStr.length(); k++) {
					if ((subStr.charAt(k)) != ' ') {
						isSpace = false;
						break;
					}

				}
				if (isSpace) {
					listHopAm.get(i + 1).sign = true;
					String newStr = new String(new char[toBeReplaced.length()])
							.replace('\0', ' ');

					StringBuilder newLine = new StringBuilder(eachOfline);
					newLine.replace(listHopAm.get(i).index, closeFirst + 1,
							newStr);
					eachOfline = newLine.toString();
					int m = 0;
					m++;
				}
			}

		}
		for (int i = 0; i < listHopAm_old.size(); i++) {
			String toBeReplaced = listHopAm_old.get(i).content;
			eachOfline = eachOfline.replace(toBeReplaced, "");
		}

		int chrodTotal = 0;

		// update new index
		for (int i = 1; i < listHopAm.size(); i++) {

			if (listHopAm.get(i).sign == false)
				chrodTotal += listHopAm.get(i - 1).length;

			listHopAm.get(i).index = listHopAm.get(i).index - chrodTotal;
		}

		String newLine = "";
		if (listHopAm.size() > 0) {
			if (listHopAm.get(0).index > 0) {
				String space = new String(new char[listHopAm.get(0).index])
						.replace('\0', ' ');
				newLine = newLine + space;
			}
			for (int i = 0; i < listHopAm.size() - 1; i++) {
				int lengthSpace = listHopAm.get(i + 1).index
						- listHopAm.get(i).index - listHopAm.get(i).length;
				if (lengthSpace < 0) {
					for (int j = i + 1; j < listHopAm.size(); j++) {
						listHopAm.get(j).index -= lengthSpace;

					}
					newLine = newLine + listHopAm.get(i).content;
					StringBuilder str = new StringBuilder(eachOfline);
					if (listHopAm.get(i).index + listHopAm.get(i).length <= str
							.length())
						str.insert(listHopAm.get(i).index
								+ listHopAm.get(i).length, new String(
								new char[-lengthSpace]).replace('\0', ' '));
					else
						str.append(new String(new char[-lengthSpace]).replace(
								'\0', ' '));
					eachOfline = str.toString();
				} else {

					String space = new String(new char[lengthSpace]).replace(
							'\0', ' ');
					newLine = newLine + listHopAm.get(i).content + space;
				}

			}
			newLine = newLine + listHopAm.get(listHopAm.size() - 1).content;
		}

		newLine = newLine + "\n";

		eachOfline = eachOfline + "\n";
		String a = newLine + eachOfline;

		return a;
	}

	public static class ChrodProperties {
		int index;
		int length;
		String content;
		boolean sign;
	}

}
