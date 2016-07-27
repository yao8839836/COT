package common;

public class CommonMethod {

	public static String getDate(String line) {

		String str = "";

		String[] temp = line.split(",");

		String year = temp[2].trim();

		String[] month_day = temp[1].trim().split(" ");

		String month = month_day[0];

		String day = month_day[1];

		if (Character.isDigit(day.charAt(1)))
			day = day.substring(0, 2);
		else
			day = "0" + day.substring(0, 1);

		month = getMonthNum(month);

		str = year + month + "," + day;

		return str;

	}

	public static String getDateNYT(String line) {
		String str = "";
		String[] temp = line.split(" ");
		String[] date = temp[1].split("\\\\");
		str = date[0] + date[1] + "," + date[2];
		return str;
	}

	public static String getMonthNum(String month) {

		String str = "";
		if (month.equals("January"))
			str = "01";
		else if (month.equals("February"))
			str = "02";
		else if (month.equals("March"))
			str = "03";
		else if (month.equals("April"))
			str = "04";
		else if (month.equals("May"))
			str = "05";
		else if (month.equals("June"))
			str = "06";
		else if (month.equals("July"))
			str = "07";
		else if (month.equals("August"))
			str = "08";
		else if (month.equals("September"))
			str = "09";
		else if (month.equals("October"))
			str = "10";
		else if (month.equals("November"))
			str = "11";
		else if (month.equals("December"))
			str = "12";
		return str;

	}

	public static int getMonthNo(String month_day) {

		int month = 0;
		if (month_day.equals("201108"))
			month = 1;
		else if (month_day.equals("201109"))
			month = 2;
		else if (month_day.equals("201110"))
			month = 3;
		else if (month_day.equals("201111"))
			month = 4;
		else if (month_day.equals("201112"))
			month = 5;
		else if (month_day.equals("201201"))
			month = 6;
		else if (month_day.equals("201202"))
			month = 7;
		else if (month_day.equals("201203"))
			month = 8;
		else if (month_day.equals("201204"))
			month = 9;
		else if (month_day.equals("201205"))
			month = 10;
		else if (month_day.equals("201206"))
			month = 11;
		else if (month_day.equals("201207"))
			month = 12;
		else if (month_day.equals("201208"))
			month = 12;
		return month;

	}

	public static int getMonthNoNYT(String month_day) {

		int month = 0;
		if (month_day.equals("201101"))
			month = 1;
		else if (month_day.equals("201102"))
			month = 2;
		else if (month_day.equals("201103"))
			month = 3;
		else if (month_day.equals("201104"))
			month = 4;
		else if (month_day.equals("201105"))
			month = 5;
		else if (month_day.equals("201106"))
			month = 6;
		else if (month_day.equals("201107"))
			month = 7;
		else if (month_day.equals("201108"))
			month = 8;
		else if (month_day.equals("201109"))
			month = 9;
		else if (month_day.equals("201110"))
			month = 10;
		else if (month_day.equals("201111"))
			month = 11;
		else if (month_day.equals("201112"))
			month = 12;
		return month;

	}

	public static int maxIndex(double[] array) {
		double max = array[0];
		int maxIndex = 0;
		for (int i = 1; i < array.length; i++) {
			if (array[i] > max) {
				max = array[i];
				maxIndex = i;
			}

		}
		return maxIndex;

	}

	public static int maxIndex(int[] array) {
		int max = array[0];
		int maxIndex = 0;
		for (int i = 1; i < array.length; i++) {
			if (array[i] > max) {
				max = array[i];
				maxIndex = i;
			}

		}
		return maxIndex;

	}

	public static double AverageKLdivergence(double[][] phi) {
		double average_divergence = 0.0;

		for (int i = 0; i < phi.length; i++) {
			double d1 = 0.0;
			for (int j = 0; j < phi.length; j++) {

				for (int k = 0; k < phi[i].length; k++) {

					if (phi[i][k] == 0 || phi[j][k] == 0)
						d1 += phi[i][k] * Math.log((phi[i][k] + 0.0001) / (phi[j][k] + 0.0001));
					else
						d1 += phi[i][k] * Math.log(phi[i][k] / phi[j][k]);

				}

				for (int k = 0; k < phi[i].length; k++) {
					if (phi[i][k] == 0 || phi[j][k] == 0)
						d1 += phi[j][k] * Math.log((phi[j][k] + 0.0001) / (phi[i][k] + 0.0001));
					else
						d1 += phi[j][k] * Math.log(phi[j][k] / phi[i][k]);

				}
				d1 /= 2;

			}
			average_divergence += d1;

		}
		average_divergence /= phi.length;

		return average_divergence;
	}

	public static double perplexity(double[][] theta, double[][] phi, int[][] docs) {
		double perplexity = 0.0;

		int total_length = 0;
		for (int i = 0; i < docs.length; i++) {
			for (int j = 0; j < docs[i].length; j++)
				total_length++;
		}

		for (int i = 0; i < docs.length; i++) {

			for (int j = 0; j < docs[i].length; j++) {
				double prob = 0.0;
				for (int k = 0; k < phi.length; k++) {
					prob += theta[i][k] * phi[k][docs[i][j]];
				}
				if (prob == 0.0)
					prob = 0.1;
				perplexity += Math.log(prob);

			}
		}
		System.out.println(perplexity);
		perplexity = Math.exp(-1 * perplexity / total_length);
		System.out.println(total_length);
		return perplexity;
	}

	public static double perplexity(double[][] theta, double[][] phi, int[][] docs, double[][] link_prob) {
		double perplexity = 0.0;

		int total_length = 0;
		for (int i = 0; i < docs.length; i++) {
			for (int j = 0; j < docs[i].length; j++)
				total_length += Math.exp(link_prob[i][j]);
		}

		for (int i = 0; i < docs.length; i++) {

			for (int j = 0; j < docs[i].length; j++) {
				double prob = 0.0;
				for (int k = 0; k < phi.length; k++) {
					prob += theta[i][k] * phi[k][docs[i][j]];
				}
				if (prob == 0.0)
					prob = 0.1;
				perplexity += Math.log(prob * Math.exp(link_prob[i][j]));

			}
		}
		System.out.println(perplexity);
		perplexity = Math.exp(-1 * perplexity / total_length);
		System.out.println(total_length);
		return perplexity;
	}

	public static double perplexity(double[][] theta, double[][] phi, int[][] docs, double[][] link_prob,
			double[][] view) {
		double perplexity = 0.0;

		int total_length = 0;
		for (int i = 0; i < docs.length; i++) {
			for (int j = 0; j < docs[i].length; j++)
				total_length += Math.exp(link_prob[i][j] * view[i][j]);
		}

		for (int i = 0; i < docs.length; i++) {

			for (int j = 0; j < docs[i].length; j++) {
				double prob = 0.0;
				for (int k = 0; k < phi.length; k++) {
					prob += theta[i][k] * phi[k][docs[i][j]];
				}
				if (prob == 0.0)
					prob = 0.1;
				perplexity += Math.log(prob * Math.exp(link_prob[i][j] * view[i][j]));

			}
		}
		System.out.println(perplexity);
		perplexity = Math.exp(-1 * perplexity / total_length);
		System.out.println(total_length);
		return perplexity;
	}
}
