package boc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GetArticleTraffic {
	// private String baseURL = "http://stats.grok.se/json";
	// private ObjectMapper objectMapper = new ObjectMapper();

	private List<String[]> list = new ArrayList<String[]>();

	private ArrayList<String[]> vocab_view = new ArrayList<String[]>();

	private ArrayList<String> vocab = new ArrayList<String>();

	// @SuppressWarnings("unchecked")
	// public int getArticleTraffic(String language, String time, String word,
	// String day) throws IOException {
	// String fullURL = baseURL + "/" + language + "/" + time + "/" + word;
	// URL url = new URL(fullURL);
	// Map maps = objectMapper.readValue(url, Map.class);
	// System.out.println(maps.isEmpty());
	// while(maps.isEmpty()){
	// System.out.println(maps.isEmpty());
	// maps = objectMapper.readValue(url, Map.class);
	//
	// }
	// String realday = time.substring(0, 4) + "-" + time.substring(4) + "-" +
	// day;
	// Map datas = (Map) maps.get("daily_views");
	// if (datas.containsKey(realday) == false) {
	// return -1;
	// } else {
	// return (Integer) datas.get(realday);
	// }
	// }
	//
	// public GetArticleTraffic(String baseURL) {
	// this.baseURL = baseURL;
	// }

	public GetArticleTraffic(ArrayList<String> vocab) throws IOException {
		this.list = readHistory("file//boc//views(tech).txt");
		this.vocab = vocab;
		for (int i = 0; i < vocab.size(); i++) {
			for (int j = 0; j < list.size(); j++) {
				String[] record = list.get(j);
				if (vocab.get(i).equals(record[0])) {
					;
					vocab_view.add(record);
					break;
				}
			}
		}

	}

	public double getNormalizedView(String article, int month) {
		double view = 0.0;
		System.out.println(month);
		int index = vocab.indexOf(article);
		String[] views = vocab_view.get(index);
		int sum = 0;
		for (int i = 1; i < 13; i++)
			sum += Integer.parseInt(views[i]);
		System.out.println(views[month]);
		view = (double) Integer.parseInt(views[month]) * 12 / sum;
		return view;

	}

	public List<String[]> readHistory(String file) throws IOException {
		List<String[]> list = new ArrayList<String[]>();
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);

		String line;
		int count = 0;

		String[] his = new String[13];
		while ((line = br.readLine()) != null) {
			if (line.trim().equals(""))
				continue;
			if ((count) % 13 == 0) {
				System.out.print(line + "@");

			}

			his[count % 13] = line;
			if (count % 13 == 12) {
				list.add(his);
				his = new String[13];
			}
			count++;
		}
		br.close();

		return list;
	}
}
