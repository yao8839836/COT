package bow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Corpus {

	public static List<String> getVocab(String directory) throws IOException {

		List<String> vocab = new ArrayList<String>();
		File f = new File(directory + "vocab(NYT).txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
		String line = "";
		while ((line = reader.readLine()) != null) {
			vocab.add(line);
			System.out.println(line);
		}
		reader.close();
		return vocab;
	}

	public static int[][] getDocuments(String directory, int docnum) throws IOException {

		int[][] documents = new int[docnum][];

		File f = new File(directory + "NYT.docs");
		InputStream in = new FileInputStream(f);
		Scanner scanner = new Scanner(in);

		int[] count = new int[docnum];
		while (scanner.hasNext()) {

			int s = scanner.nextInt();
			count[s]++;
		}

		scanner.close();
		in.close();

		f = new File(directory + "NYT.words");
		in = new FileInputStream(f);
		scanner = new Scanner(in);
		for (int i = 0; i < documents.length; i++) {
			documents[i] = new int[count[i]];
			for (int j = 0; j < documents[i].length; j++) {
				if (scanner.hasNext()) {
					documents[i][j] = scanner.nextInt();
				}
			}
		}

		scanner.close();
		in.close();

		return documents;
	}

	public static double[] getDocumentTime(int docnum) {

		double[] time = new double[docnum];

		for (int i = 0; i < docnum; i++) {
			time[i] = (double) (docnum - i) / docnum;
		}

		return time;
	}

}
