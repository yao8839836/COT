package boc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import common.CommonMethod;
import common.MultiThreadWikify;

public class Wikify {

	@SuppressWarnings("deprecation")
	public static void wikify(String directory, int docnum) throws IOException {

		String[] texts = new String[docnum];
		texts = Corpus.getDocuments(directory, docnum);

		org.jsoup.nodes.Document doc = null;

		Elements links = null;

		File result = null;
		OutputStream out = null;
		BufferedWriter bw = null;

		for (int i = 0; i < docnum; i++) {

			System.out.println(texts[i]);
			String option = "&sourceMode=AUTO&repeatMode=FIRST_IN_REGION&minProbability=0";

			result = new File("file//boc//wikified(dense)//" + (i + 1) + ".txt");
			out = new FileOutputStream(result, false);
			bw = new BufferedWriter(new OutputStreamWriter(out, "utf-8"));

			String[] passage = texts[i].split("\\.");
			for (int j = 0; j < passage.length; j++) {
				doc = Jsoup
						.connect("http://wikipedia-miner.cms.waikato.ac.nz/services/wikify?source="
								+ URLEncoder.encode(passage[j]).replaceAll("\\+", "%20") + option)
						.timeout(500000).get();

				links = doc.getElementsByTag("detectedtopic");
				for (Element e : links) {
					String title = e.attr("title");
					String weight = e.attr("weight");
					System.out.println(title + " " + weight);
					bw.write(title + " " + weight);
					bw.newLine();
				}
			}
			bw.close();
			out.close();
		}
	}

	public static ArrayList<String> getConceptSet(int docnum) throws IOException {
		ArrayList<String> vocab = new ArrayList<String>();
		for (int i = 1; i <= docnum; i++) {
			File f = new File("file//boc//wikified(dense)//" + i + ".txt");
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
			String line = "";
			while ((line = reader.readLine()) != null) {
				String[] wiki = line.split(" ");
				String concept = "";
				for (int j = 0; j < wiki.length - 1; j++) {
					concept += wiki[j] + " ";
				}
				concept = concept.trim();
				if (!vocab.contains(concept)) {
					vocab.add(concept);
					System.out.println(concept);
				}

			}
			reader.close();

		}
		return vocab;
	}

	public static ArrayList<String> getConceptSet(String filename) throws IOException {
		ArrayList<String> vocab = new ArrayList<String>();
		File f = new File(filename);
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
		String line = "";
		while ((line = reader.readLine()) != null) {
			if (!vocab.contains(line))
				vocab.add(line);
		}
		reader.close();
		return vocab;

	}

	public static int[][] getConceptDocuments(ArrayList<String> vocab, int docnum) throws IOException {

		int[][] documents = new int[docnum][];

		for (int i = 1; i <= docnum; i++) {

			File f = new File("file//boc//wikified(dense)//" + i + ".txt");
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
			String line = "";
			int count = 0;// 文件行数（维基概念数）
			while ((line = reader.readLine()) != null) {

				String[] wiki = line.split(" ");
				String concept = "";
				for (int j = 0; j < wiki.length - 1; j++) {
					concept += wiki[j] + " ";
				}
				concept = concept.trim();
				if (vocab.contains(concept))
					count++;
			}
			reader.close();
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
			documents[i - 1] = new int[count];
			int line_index = 0;
			while ((line = reader.readLine()) != null) {

				String[] wiki = line.split(" ");

				String concept = "";
				for (int j = 0; j < wiki.length - 1; j++) {
					concept += wiki[j] + " ";
				}

				concept = concept.trim();
				if (vocab.contains(concept)) {
					documents[i - 1][line_index] = vocab.indexOf(concept);

					line_index++;
				}

			}
			reader.close();
		}
		return documents;
	}

	public static double[][] getDocumentsLinkProbability(ArrayList<String> vocab, int docnum) throws IOException {

		double[][] prob = new double[docnum][];

		for (int i = 1; i <= docnum; i++) {

			File f = new File("file//boc//wikified(dense)//" + i + ".txt");
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
			String line = "";
			int count = 0;// 文件行数（维基概念数）
			while ((line = reader.readLine()) != null) {
				String[] wiki = line.split(" ");
				String concept = "";
				for (int j = 0; j < wiki.length - 1; j++) {
					concept += wiki[j] + " ";
				}
				concept = concept.trim();
				if (vocab.contains(concept))
					count++;
			}
			reader.close();
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
			prob[i - 1] = new double[count];
			int line_index = 0;
			while ((line = reader.readLine()) != null) {

				String[] wiki = line.split(" ");
				String concept = "";
				for (int j = 0; j < wiki.length - 1; j++) {
					concept += wiki[j] + " ";
				}
				concept = concept.trim();
				if (vocab.contains(concept)) {

					double probablity = Double.parseDouble(wiki[wiki.length - 1]);
					prob[i - 1][line_index] = probablity;

					line_index++;
				}

			}
			reader.close();

		}
		return prob;
	}

	public static double[][] getArtileView(int[][] documents, String[] time, ArrayList<String> vocab)
			throws IOException, InterruptedException {
		double[][] views = new double[documents.length][];

		GetArticleTraffic traffic = new GetArticleTraffic(vocab);
		for (int i = 0; i < documents.length; i++) {
			views[i] = new double[documents[i].length];
			String[] month_day = time[i].split(",");
			for (int j = 0; j < documents[i].length; j++) {
				String article = vocab.get(documents[i][j]);
				System.out.print(article + " ");

				double view = traffic.getNormalizedView(article, CommonMethod.getMonthNo(month_day[0]));
				// double view = traffic.getNormalizedView(article,
				// CommonMethod.getMonthNoNYT(month_day[0]));
				views[i][j] = view;
			}
			System.out.println();
		}
		return views;
	}

	public static void main(String args[]) throws IOException, InterruptedException {

		// String directory = "E:\\datablog\\";

		int docnum = 6778;

		// wikify(directory, docnum);

		String[] texts = new String[docnum];
		String[] filename = new String[docnum];

		File f = new File("src//file//doclist(NYT)part.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
		String line = "";
		int line_index = 0;
		while ((line = reader.readLine()) != null) {
			filename[line_index] = line;
			System.out.println(line);
			line_index++;
		}

		reader.close();

		texts = Corpus.getDocuments(filename, docnum);
		for (int i = 0; i < docnum; i++) {
			System.out.println(texts[i]);
		}

		ExecutorService pool = Executors.newFixedThreadPool(1);
		// for(int i=0;i<5;i++)
		// pool.submit(new Thread(new MultiThreadWikify(i*1200 ,(i+1)*1200,
		// texts)));
		pool.submit(new Thread(new MultiThreadWikify(854, 1200, texts)));
		pool.submit(new Thread(new MultiThreadWikify(1338, 2400, texts)));
		pool.submit(new Thread(new MultiThreadWikify(2547, 3600, texts)));
		pool.submit(new Thread(new MultiThreadWikify(3751, 4800, texts)));
		pool.submit(new Thread(new MultiThreadWikify(4967, 6000, texts)));
		pool.submit(new Thread(new MultiThreadWikify(6143, 6778, texts)));
		pool.shutdown();
		while (!pool.isTerminated())
			Thread.sleep(1000);

		// String time_file_name = "src//file//boc//time.txt";
		//
		// String [] time = Corpus.getDocumentTimeString(time_file_name,
		// docnum);
		//
		// for(int i = 0; i < docnum; i++){
		// System.out.println(time[i]);
		// }
		//

	}

}
