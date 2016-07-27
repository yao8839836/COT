package boc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import common.CommonMethod;

public class Corpus {

	public static String[] getDocuments(String directory, int docnum) throws IOException {

		File f;
		BufferedReader reader;
		String line = "";

		String[] texts = new String[docnum];
		for (int j = 0; j < texts.length; j++) {
			f = new File(directory + (j + 1) + ".txt");
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
			line = "";
			String s = "";
			while ((line = reader.readLine()) != null) {
				s += line + " ";
			}
			reader.close();
			texts[j] = s;
		}

		return texts;
	}

	public static String[] getDocuments(String[] filename, int docnum) throws IOException {
		String[] texts = new String[docnum];

		File f;
		BufferedReader reader;
		String line = "";

		for (int i = 0; i < docnum; i++) {
			f = new File(filename[i]);
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
			line = "";
			String s = "";
			while ((line = reader.readLine()) != null) {
				s += line + "\n";
			}
			texts[i] = s;
			reader.close();
		}
		return texts;

	}

	public static double[] getDocumentTime(int docnum) {

		double[] time = new double[docnum];

		for (int i = 0; i < docnum; i++) {
			time[i] = (double) (docnum - i) / docnum;
		}

		return time;
	}

	public static String[] getDocumentTimeString(String filename, int docnum) throws IOException {
		String[] time = new String[docnum];

		File f = new File(filename);
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
		String line = "";

		int i = 0;
		while ((line = reader.readLine()) != null) {
			String str = CommonMethod.getDate(line);
			// String str = CommonMethod.getDateNYT(line);
			time[i] = str;
			i++;
		}
		reader.close();
		return time;
	}

}
