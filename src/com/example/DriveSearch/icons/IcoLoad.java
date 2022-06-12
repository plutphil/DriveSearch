package com.example.DriveSearch.icons;

import java.awt.Image;

import javax.swing.ImageIcon;

public class IcoLoad {
static String[] extensions={
"_blank",
"QT",
"RAR",
"RB",
"RTF",
"SQL",
"TIFF",
"TXT",
"WAV",
"XLS",
"XML",
"YML",
"ZIP.",
"HTML",
"ICS",
"JAVA",
"JPG",
"KEY",
"MID",
"MP3",
"MP4",
"MPG",
"PDF",
"PHP",
"PNG",
"PPT",
"PSD",
"PY",
"AAC",
"AI",
"AIFF",
"AVI",
"C",
"CPP",
"CSS",
"CSV",
"DAT",
"DMG",
"DOC",
"EXE",
"FLV",
"GIF",
"H",
"HPP"};

	public static ImageIcon getIcon(String ext) {
		for(String s:extensions) {
			if(("."+s).equalsIgnoreCase(ext)) {
				ImageIcon ii= new ImageIcon(IcoLoad.class.getResource(s.toLowerCase()+".png"));
				ii=new ImageIcon(ii.getImage().getScaledInstance(24, 24, Image.SCALE_DEFAULT));
				return(ii);
			}
		}
		return null;
	}
}
