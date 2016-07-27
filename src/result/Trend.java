package result;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import common.CommonMethod;

import boc.Corpus;

public class Trend {

	/**
	 * Get topic distribution of each document
	 * 
	 * @param docnum
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public static double[][] getTheta(int docnum, String filename) throws IOException {
		double[][] theta = new double[docnum][];
		File f = new File(filename);
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
		String line = "";
		int index = 0;
		while ((line = reader.readLine()) != null) {
			String[] temp = line.split(" ");
			theta[index] = new double[temp.length];
			for (int k = 0; k < theta[index].length; k++) {
				theta[index][k] = Double.parseDouble(temp[k]);
			}
			index++;
		}
		reader.close();
		return theta;
	}

	public static void main(String args[]) throws IOException {
		int docnum = 3158;
		String filename = "src//file//theta (COT)tech.txt";

		String time_file = "src//file//boc//time.txt";
		String[] time_string = Corpus.getDocumentTimeString(time_file, docnum);

		List<String> day = new ArrayList<String>();

		for (int i = 0; i < time_string.length; i++) {
			String[] temp = time_string[i].split(",");
			String date = temp[0] + temp[1];
			if (!day.contains(date)) {
				day.add(date);
			}
		}
		/*
		 * COT
		 */
		double[][] theta = getTheta(docnum, filename);

		double[] cot = new double[docnum];
		double[] cot_month = new double[12];
		double[] cot_day = new double[day.size()];

		for (int i = 0; i < theta.length; i++) {
			System.out.println(theta[i][40]);
			cot[i] = theta[i][40];
			String[] temp = time_string[i].split(",");

			int month_no = CommonMethod.getMonthNo(temp[0]);
			cot_month[month_no - 1] += cot[i];

			String date = temp[0] + temp[1];
			cot_day[day.indexOf(date)] += cot[i];

		}
		/*
		 * TOT on words
		 */

		filename = "src//file//theta (TOT).txt";
		theta = getTheta(docnum, filename);

		double[] tot_words = new double[docnum];
		double[] tot_month_words = new double[12];
		double[] tot_day_words = new double[day.size()];
		for (int i = 0; i < docnum; i++) {
			System.out.println(theta[i][36] + " " + i);
			tot_words[i] = theta[i][36];
			String[] temp = time_string[i].split(",");
			int month_no = CommonMethod.getMonthNo(temp[0]);
			tot_month_words[month_no - 1] += tot_words[i];

			String date = temp[0] + temp[1];
			tot_day_words[day.indexOf(date)] += tot_words[i];
		}

		/*
		 * LDA on words
		 */

		filename = "src//file//theta(LDA) 50.txt";
		theta = getTheta(docnum, filename);

		double[] lda_words = new double[docnum];
		double[] lda_month_words = new double[12];
		double[] lda_day_words = new double[day.size()];
		for (int i = 0; i < docnum; i++) {
			System.out.println(theta[i][46] + " " + i);
			lda_words[i] = theta[i][46];
			String[] temp = time_string[i].split(",");
			int month_no = CommonMethod.getMonthNo(temp[0]);
			lda_month_words[month_no - 1] += lda_words[i];

			String date = temp[0] + temp[1];
			lda_day_words[day.indexOf(date)] += lda_words[i];
		}

		/*
		 * TOT
		 */

		filename = "src//file//boc//theta(TOT)tech 50.txt";
		theta = getTheta(docnum, filename);

		double[] tot = new double[docnum];
		double[] tot_month = new double[12];
		double[] tot_day = new double[day.size()];
		for (int i = 0; i < docnum; i++) {
			System.out.println(theta[i][41] + " " + i);
			tot[i] = theta[i][41];
			String[] temp = time_string[i].split(",");
			int month_no = CommonMethod.getMonthNo(temp[0]);
			tot_month[month_no - 1] += tot[i];

			String date = temp[0] + temp[1];
			tot_day[day.indexOf(date)] += tot[i];
		}

		/*
		 * TOT link
		 */

		filename = "src//file//boc//theta(TOT with link)tech 50.txt";
		theta = getTheta(docnum, filename);

		double[] tot_link = new double[docnum];
		double[] tot_link_month = new double[12];
		double[] tot_link_day = new double[day.size()];
		for (int i = 0; i < docnum; i++) {
			System.out.println(theta[i][35] + " " + i);
			tot_link[i] = theta[i][35];
			String[] temp = time_string[i].split(",");
			int month_no = CommonMethod.getMonthNo(temp[0]);
			tot_link_month[month_no - 1] += tot_link[i];

			String date = temp[0] + temp[1];
			tot_link_day[day.indexOf(date)] += tot_link[i];
		}

		/*
		 * LDA with link
		 */
		filename = "src//file//boc//theta(LDA with link prob)tech 50.txt";
		theta = getTheta(docnum, filename);

		double[] lda_link = new double[docnum];
		double[] lda_link_month = new double[12];
		double[] lda_link_day = new double[day.size()];
		for (int i = 0; i < docnum; i++) {
			System.out.println(theta[i][16]);
			lda_link[i] = theta[i][16];
			String[] temp = time_string[i].split(",");
			int month_no = CommonMethod.getMonthNo(temp[0]);
			lda_link_month[month_no - 1] += lda_link[i];

			String date = temp[0] + temp[1];
			lda_link_day[day.indexOf(date)] += lda_link[i];
		}
		/*
		 * LDA
		 */
		filename = "src//file//boc//theta(LDA)tech 50.txt";
		theta = getTheta(docnum, filename);

		double[] lda_only = new double[docnum];
		double[] lda_only_month = new double[12];
		double[] lda_only_day = new double[day.size()];
		for (int i = 0; i < docnum; i++) {
			System.out.println(theta[i][18]);
			lda_only[i] = theta[i][18];
			String[] temp = time_string[i].split(",");
			int month_no = CommonMethod.getMonthNo(temp[0]);
			lda_only_month[month_no - 1] += lda_only[i];

			String date = temp[0] + temp[1];
			lda_only_day[day.indexOf(date)] += lda_only[i];
		}

		/*
		 * LDA with view
		 */

		filename = "src//file//boc//theta(LDA with view)tech 50.txt";
		theta = getTheta(docnum, filename);

		double[] lda_view = new double[docnum];
		double[] lda_view_month = new double[12];
		double[] lda_view_day = new double[day.size()];
		for (int i = 0; i < docnum; i++) {
			System.out.println(theta[i][10]);
			lda_view[i] = theta[i][10];
			String[] temp = time_string[i].split(",");
			int month_no = CommonMethod.getMonthNo(temp[0]);
			lda_view_month[month_no - 1] += lda_view[i];

			String date = temp[0] + temp[1];
			lda_view_day[day.indexOf(date)] += lda_view[i];
		}

		/*
		 * TOT view
		 */

		filename = "src//file//boc//theta(TOT with view)tech 50.txt";
		theta = getTheta(docnum, filename);

		double[] tot_view = new double[docnum];
		double[] tot_view_month = new double[12];
		double[] tot_view_day = new double[day.size()];

		for (int i = 0; i < docnum; i++) {
			System.out.println(theta[i][35]);
			tot_view[i] = theta[i][35];
			String[] temp = time_string[i].split(",");
			int month_no = CommonMethod.getMonthNo(temp[0]);
			tot_view_month[month_no - 1] += tot_view[i];

			String date = temp[0] + temp[1];
			tot_view_day[day.indexOf(date)] += tot_view[i];
		}

		/*
		 * LDA view&link
		 */

		filename = "src//file//boc//theta(LDA with link&view)tech 50.txt";
		theta = getTheta(docnum, filename);

		double[] lda_view_link = new double[docnum];
		double[] lda_view_link_month = new double[12];
		double[] lda_view_link_day = new double[day.size()];

		for (int i = 0; i < docnum; i++) {
			System.out.println(theta[i][0]);
			lda_view_link[i] = theta[i][0];
			String[] temp = time_string[i].split(",");
			int month_no = CommonMethod.getMonthNo(temp[0]);
			lda_view_link_month[month_no - 1] += lda_view_link[i];

			String date = temp[0] + temp[1];
			lda_view_link_day[day.indexOf(date)] += lda_view_link[i];
		}

		File result;
		OutputStream out;
		BufferedWriter bw;

		result = new File("src//file//data_day(jobs)LDAw&TOTw&LDAv.tsv");
		out = new FileOutputStream(result, false);
		bw = new BufferedWriter(new OutputStreamWriter(out, "utf-8"));

		bw.write("date\tLDAw\tTOTw\tLDAv");
		bw.newLine();
		for (int i = day.size() - 1; i >= 0; i--) {

			bw.write(day.get(i) + "\t" + lda_day_words[i] + "\t" + tot_day_words[i] + "\t" + lda_view_day[i]);
			bw.newLine();

		}
		bw.close();
		out.close();
	}

}
