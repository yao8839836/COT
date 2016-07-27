package result;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Coherence {

	/**
	 * 一个概念的top维基词条的平均相关度
	 * 
	 * @param top_articles
	 * @return
	 * @throws IOException
	 */
	public static double coherence(List<String> top_articles) throws IOException {

		double score = 0;

		for (String article_1 : top_articles) {

			for (String article_2 : top_articles) {

				if (!article_1.equals(article_2)) {
					score += getArticlesRelatedness(article_1, article_2);
				}

			}
		}

		return score / top_articles.size();
	}

	/**
	 * 获得两篇维基文章的相关度
	 * 
	 * @param article_1
	 * @param article_2
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("deprecation")
	public static double getArticlesRelatedness(String article_1, String article_2) throws IOException {
		double relatedness = 0.0;
		String url = "http://wikipedia-miner.cms.waikato.ac.nz/services/compare?";
		org.jsoup.nodes.Document doc = null;
		Elements links = null;
		String str = "term1=" + article_1 + "&" + "term2=" + article_2;

		doc = Jsoup
				.connect(url
						+ URLEncoder.encode(str).replaceAll("\\+", "%20").replaceAll("%26", "&").replaceAll("%3D", "="))
				.timeout(5000000).get();

		links = doc.getElementsByTag("message");
		for (Element e : links) {
			String t = e.attr("relatedness");
			relatedness = Double.parseDouble(t);

		}

		return relatedness;
	}

	public static void main(String[] args) throws IOException {

		File f = new File("file//boc//topic top 10 concepts(TOT with view)tech 10.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
		String line = "";

		double average_coherence = 0;

		while ((line = reader.readLine()) != null) {

			String[] terms = line.split("  ");

			List<String> concept = new ArrayList<String>();

			if (true) {
				for (int i = 0; i < 10; i++) {
					concept.add(terms[i]);
				}

				double score = coherence(concept);
				System.out.println(score);
				average_coherence += score;
			}

		}

		reader.close();

		System.out.println(average_coherence / 10);

	}

}
