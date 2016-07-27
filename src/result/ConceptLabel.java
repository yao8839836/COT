package result;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ConceptLabel {
	@SuppressWarnings("deprecation")
	public static ArrayList<String> getMapCategory(String article) throws IOException {

		ArrayList<String> catelist = new ArrayList<String>();

		String url = "http://en.wikipedia.org/w/api.php?action=query&prop=categories&titles=";

		org.jsoup.nodes.Document doc = null;

		Elements links = null;

		doc = Jsoup.connect(url + URLEncoder.encode(article)).timeout(500000).get();

		links = doc.getElementsByTag("span");

		String str = "";
		for (Element e : links) {

			String title = e.ownText();
			str += title + "\n";

		}

		doc = Jsoup.parseBodyFragment(str);

		links = doc.getElementsByTag("cl");

		for (Element e : links) {
			String title = e.attr("title");
			title = title.substring(9, title.length());
			// System.out.println(title);
			if (!title.contains("articles") && !title.contains("Articles") && !title.contains("pages")
					&& !title.contains("Pages") && !title.contains("category") && !title.contains("categories")
					&& !title.contains("Pages") && !title.contains("errors") && !title.contains("Redirects")
					&& !title.contains("redirects") && !title.contains("disputes") && !title.contains("dmy")
					&& !title.contains("Wiki"))
				catelist.add(title);

		}
		return catelist;
	}

	@SuppressWarnings("deprecation")
	public static ArrayList<String> getRelatedCategory(String[] articles) throws IOException {

		ArrayList<String> list = new ArrayList<String>();
		String url = "http://wikipedia-miner.cms.waikato.ac.nz/services/suggest?queryTopics=";
		String str = "";
		for (int i = 0; i < articles.length && i < 10; i++) {
			if (i < articles.length - 1)
				str += articles[i] + ",";
			else
				str += articles[i];
		}
		org.jsoup.nodes.Document doc = null;

		Elements links = null;

		doc = Jsoup.connect(url + URLEncoder.encode(str)).timeout(500000).get();

		links = doc.getElementsByTag("suggestionCategory");

		for (Element e : links) {
			String t = e.attr("title");
			// System.out.println(t);
			list.add(t);

		}
		return list;
	}

	@SuppressWarnings("deprecation")
	public static ArrayList<String> getCategoryArticles(String category) throws IOException {
		ArrayList<String> articles = new ArrayList<String>();
		// String url =
		// "http://en.wikipedia.org/w/api.php?action=query&list=categorymembers&cmlimit=500&cmtitle=Category:";
		String url = "http://en.wikipedia.org/w/api.php?action=query&list=categorymembers&cmtitle=Category:";
		org.jsoup.nodes.Document doc = null;

		Elements links = null;

		doc = Jsoup.connect(url + URLEncoder.encode(category)).timeout(500000).get();

		links = doc.getElementsByTag("span");

		String str = "";
		for (Element e : links) {

			String title = e.ownText();
			str += title + "\n";

		}

		doc = Jsoup.parseBodyFragment(str);

		// System.out.println(doc);
		links = doc.getElementsByTag("cm");

		for (Element e : links) {
			String t = e.attr("title");

			if (t.startsWith("Category:"))
				t = t.substring(9, t.length());
			// System.out.println(t);

			articles.add(t);
		}

		return articles;

	}

	@SuppressWarnings("deprecation")
	public static double getArticlesRelatedness(String article_1, String article_2) throws IOException {
		double relatedness = 0.0;
		String url = "http://wikipedia-miner.cms.waikato.ac.nz/services/compare?";
		org.jsoup.nodes.Document doc = null;
		Elements links = null;
		String str = "term1=" + article_1 + "&" + "term2=" + article_2;
		// System.out.println(url+URLEncoder.encode(str).replaceAll("\\+",
		// "%20").replaceAll("%26", "&").replaceAll("%3D", "="));
		doc = Jsoup
				.connect(url
						+ URLEncoder.encode(str).replaceAll("\\+", "%20").replaceAll("%26", "&").replaceAll("%3D", "="))
				.timeout(500000).get();
		// System.out.println(doc);
		links = doc.getElementsByTag("message");
		for (Element e : links) {
			String t = e.attr("relatedness");
			relatedness = Double.parseDouble(t);

		}
		return relatedness;
	}

	@SuppressWarnings("deprecation")
	public static String getArticleId(String article) throws IOException {

		String id = "";
		String title = "";
		if (article.contains("("))
			title = article.substring(0, article.indexOf('(') - 1);
		else
			title = article;

		String url = "http://wikipedia-miner.cms.waikato.ac.nz/services/search?query=";

		org.jsoup.nodes.Document doc = null;

		Elements links = null;

		doc = Jsoup.connect(url + URLEncoder.encode(title).replaceAll("\\+", "%20")).timeout(50000).get();
		// Connection conn =
		// Jsoup.connect(url+URLEncoder.encode(title).replaceAll("\\+", "%20"));
		// conn.header("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X
		// 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
		// doc = conn.timeout(50000).get();
		links = doc.getElementsByTag("sense");

		for (Element e : links) {
			String t = e.attr("title");
			if (t.equals(article)) {
				id = e.attr("id");
				break;
			}

		}

		return id;

	}

	public static void main(String args[]) throws IOException, InterruptedException {
		String article = "Android (operating system)";

		String articleId = getArticleId(article);
		System.out.println(articleId);
		ArrayList<String> map = getMapCategory(article);
		System.out.println(map);
		System.out.println(getArticlesRelatedness("Steve Jobs", "Apple Inc."));
		ArrayList<String> cat_articles = getCategoryArticles("Physics");
		System.out.println(cat_articles);

		File f;
		BufferedReader reader;
		String line = "";
		f = new File("file//concept top 30 words (COT )NYT.txt");
		reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));

		// get articles and probabilities

		int line_no = 0;
		ArrayList<String[]> concepts = new ArrayList<String[]>();
		ArrayList<double[]> prob = new ArrayList<double[]>();
		while ((line = reader.readLine()) != null) {
			String[] terms = line.split("  ");

			if (line_no % 2 == 0) {
				concepts.add(terms);
			} else {
				double[] p = new double[30];
				for (int i = 0; i < 30; i++) {
					p[i] = Double.parseDouble(terms[i]);
					System.out.print(p[i] + "  ");
				}
				System.out.println();
				prob.add(p);
			}
			line_no++;

		}
		reader.close();

		// // get concepts top articles id
		//
		// ArrayList <String []> concepts_articles_id = new ArrayList <String
		// []> ();
		// for (int i = 0; i < concepts.size(); i++){
		// String [] article_id = new String [30];
		// String [] concept = concepts.get(i);
		// for(int j = 0; j < 30; j++){
		// System.out.print(concept[j]+" ");
		// String id = getArticleId(concept[j]);
		// Thread.sleep(1);
		// article_id [j] = id;
		// System.out.print(id+" ");
		// }
		// System.out.println();
		// concepts_articles_id.add(article_id);
		// }
		//
		// //get mapped categories
		//
		// for(int i = 0; i < concepts.size(); i++){
		// ArrayList <String> map_categories = new ArrayList <String> ();
		// String [] articles = concepts.get(i);
		// for(int j = 0; j < concepts.get(i).length; j++){
		//
		// ArrayList <String> categories = getMapCategory(articles [j]);
		//
		// for(int k = 0; k < categories.size(); k++)
		// if(!map_categories.contains(categories.get(k)))
		// map_categories.add(categories.get(k));
		//
		// }
		//
		//
		// int [] map_count = new int [map_categories.size()];
		//
		// for(int j = 0; j < concepts.get(i).length; j++){
		//
		// ArrayList <String> categories = getMapCategory(articles [j]);
		//
		// for(int k = 0; k < categories.size(); k++){
		// map_count[map_categories.indexOf(categories.get(k))]++;
		// }
		//
		//
		// }
		// int index = CommonMethod.maxIndex(map_count);
		//
		// String label = map_categories.get(index);
		//
		// System.out.println(i+" Concept Label: "+ label);
		// }
		// //get related categories
		// for(int i = 0; i < concepts.size(); i++){
		// ArrayList <String> raleted_categories = new ArrayList <String> ();
		// String [] articles = concepts_articles_id.get(i);
		// raleted_categories = getRelatedCategory(articles);
		// for(int j = 0; j < raleted_categories.size(); j++)
		// System.out.print(raleted_categories.get(j)+" ");
		// System.out.println();
		// }
		// System.out.println(getArticlesRelatedness("Steve Jobs","Apple
		// Inc."));

		String[] concept = concepts.get(7);
		double[] p = prob.get(7);
		/*
		 * get id
		 */
		String[] concept_article_id = new String[10];
		for (int i = 0; i < 10; i++) {
			concept_article_id[i] = getArticleId(concept[i]);
			System.out.println(concept_article_id[i]);
			System.out.println(concept[i]);
		}
		/*
		 * get mapped
		 */
		ArrayList<String> map_categories = new ArrayList<String>();
		for (int i = 0; i < concept.length && i < 10; i++) {
			ArrayList<String> categories = getMapCategory(concept[i]);
			for (int k = 0; k < categories.size(); k++)
				if (!map_categories.contains(categories.get(k)))
					map_categories.add(categories.get(k));
		}
		int[] map_count = new int[map_categories.size()];

		for (int i = 0; i < concept.length && i < 10; i++) {
			ArrayList<String> categories = getMapCategory(concept[i]);
			for (int k = 0; k < categories.size(); k++)
				map_count[map_categories.indexOf(categories.get(k))]++;
		}
		System.out.println(map_categories);
		for (int i = 0; i < map_count.length; i++)
			System.out.println(map_count[i]);

		// int index = CommonMethod.maxIndex(map_count);
		//
		// String label = map_categories.get(index);
		//
		// System.out.println("Concept Label: "+ label);

		/*
		 * get related
		 */

		/*
		 * Wikipedia Miner Suggest
		 */
		ArrayList<String> raleted_categories = new ArrayList<String>();

		raleted_categories = getRelatedCategory(concept_article_id);

		for (int j = 0; j < raleted_categories.size(); j++)
			System.out.print(raleted_categories.get(j) + "  ");
		System.out.println();

		/*
		 * Compute score using Concept Labeling
		 */
		double[] relate_score = new double[map_categories.size()];
		for (int i = 0; i < map_categories.size(); i++) {
			String category = map_categories.get(i);
			cat_articles = getCategoryArticles(category);
			double relatedness = 0.0;
			for (int j = 0; j < cat_articles.size(); j++) {
				article = cat_articles.get(j);
				for (int k = 0; k < concept.length && k < 10; k++) {
					relatedness += p[k] * getArticlesRelatedness(article, concept[k]);
				}

			}
			relate_score[i] = relatedness;
			System.out.println(relatedness);
		}
		double[] final_score = new double[map_categories.size()];

		double alpha = 1, beta = 1;

		int sum_cat = 0;

		for (int i = 0; i < map_count.length; i++) {
			sum_cat += map_count[i];
		}

		double[] map_prob = new double[map_categories.size()];
		for (int i = 0; i < map_count.length; i++) {
			map_prob[i] = (double) map_count[i] / sum_cat;
			final_score[i] = alpha * map_prob[i] + beta * relate_score[i];

			System.out.println(map_categories.get(i) + " " + final_score[i]);
		}

		/*
		 * select best in top articles
		 */
		// double [] article_score = new double [30];
		//
		//
		// for(int i = 0; i < 30; i++){
		// double score = 0.0;
		// for(int j = 0; j < 30; j++){
		// if(i != j)
		// score += getArticlesRelatedness(concept [i], concept[j]);
		// }
		// article_score [i] = score;
		//
		// System.out.println(concept[i]+" "+score);
		// }
		//

	}

}
