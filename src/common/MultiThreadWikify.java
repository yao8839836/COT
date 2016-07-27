package common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MultiThreadWikify implements Runnable {
	private int start;
	private int end;
	private String[] texts;

	public MultiThreadWikify(int start, int end, String[] texts) {
		this.start = start;
		this.end = end;
		this.texts = texts;
		//
	}

	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		// TODO Auto-generated method stub
		org.jsoup.nodes.Document doc = null;

		Elements links = null;

		File result = null;
		OutputStream out = null;
		BufferedWriter bw = null;
		int i = 0;
		try {
			for (i = start; i < end; i++) {

				String option = "&sourceMode=AUTO&repeatMode=FIRST_IN_REGION&minProbability=0";

				result = new File("file//boc//wikified(NYT)//" + (i + 1) + ".txt");
				out = new FileOutputStream(result, false);
				bw = new BufferedWriter(new OutputStreamWriter(out, "utf-8"));
				String[] passage = null;
				if (texts[i].contains("\n"))
					passage = texts[i].split("\n");
				for (int j = 0; j < passage.length; j++) {
					// Thread.sleep(2000);
					if (passage[j].equals(""))
						continue;

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
					// Connection conn =
					// Jsoup.connect("http://wikipedia-miner.cms.waikato.ac.nz/services/wikify?source="+URLEncoder.encode(passage[j]).replaceAll("\\+",
					// "%20")+option);
					System.out.println(j);
				}

				bw.close();
				out.close();

			}

		} catch (Exception e) {
			System.out.println("Error:" + i + " " + e.toString() + "\n" + e.fillInStackTrace());
		} finally {

		}
	}
}
