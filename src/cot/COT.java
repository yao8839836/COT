package cot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import org.apache.commons.math3.special.Beta;

import boc.Corpus;
import boc.Wikify;

import common.CommonMethod;

public class COT {

	int[][] documents;

	double[][] prob; // link probability via Wikipedia Miner

	double[][] views; // normalized number of article views

	double[] timestamp;

	int V;

	int K;

	double alpha;

	double beta;

	double[][] time_prior;

	int[][] z;

	double[][] nw;

	double[][] nd;

	double[] nwsum;

	int[] nwsum_int;

	double[] ndsum;

	int iterations;

	public COT(int[][] documents, int V, double[] timestamp, double[][] prob, double[][] views) {

		this.documents = documents;
		this.V = V;
		this.timestamp = timestamp;
		this.prob = prob;
		this.views = views;

	}

	public void initialState() {

		int D = documents.length;
		nw = new double[V][K];
		nd = new double[D][K];
		nwsum = new double[K];
		ndsum = new double[D];
		nwsum_int = new int[K];

		z = new int[D][];

		for (int d = 0; d < D; d++) {

			int Nd = documents[d].length;

			z[d] = new int[Nd];
			double new_doc_length = 0.0;
			for (int n = 0; n < Nd; n++) {
				int concept = (int) (Math.random() * K);

				z[d][n] = concept;

				nw[documents[d][n]][concept] += Math.exp(prob[d][n] * views[d][n]);

				nd[d][concept] += Math.exp(prob[d][n] * views[d][n]);

				nwsum[concept] += Math.exp(prob[d][n] * views[d][n]);

				new_doc_length += Math.exp(prob[d][n] * views[d][n]);

				nwsum_int[concept]++;

			}
			ndsum[d] = new_doc_length;
		}

		for (int k = 0; k < K; k++) {
			this.time_prior[k][0] = 2;
			this.time_prior[k][1] = 2;
		}
	}

	public void markovChain(int K, double alpha, double beta, int iterations) {

		this.K = K;
		this.alpha = alpha;
		this.beta = beta;
		this.iterations = iterations;
		this.time_prior = new double[K][2];

		initialState();

		for (int i = 0; i < this.iterations; i++) {
			System.out.println(i);
			gibbs();
			updateTimePrior();
		}
	}

	public void gibbs() {

		for (int d = 0; d < z.length; d++) {
			for (int n = 0; n < z[d].length; n++) {

				int topic = sampleFullConditional(d, n, timestamp);
				z[d][n] = topic;

			}
		}
	}

	public void updateTimePrior() {
		for (int k = 0; k < K; k++) {

			double[] time = new double[nwsum_int[k]];// only integer here
			int k_word_count = 0;

			for (int d = 0; d < z.length; d++) {
				for (int n = 0; n < z[d].length; n++) {

					if (z[d][n] == k) {
						time[k_word_count] = timestamp[d];
						// System.out.println(nwsum_int[k]+","+k_word_count+","+
						// d);
						k_word_count++;
					}
				}
			}

			double expect = 0;
			for (int i = 0; i < time.length; i++) {
				expect += time[i];
			}
			expect /= time.length;

			double variance = 0;
			for (int i = 0; i < time.length; i++) {
				variance = variance + (time[i] - expect) * (time[i] - expect);
			}
			variance /= time.length;
			// System.out.println(expect+" "+variance);
			time_prior[k][0] = expect * ((expect * (1 - expect) / variance) - 1);
			time_prior[k][1] = (1 - expect) * ((expect * (1 - expect) / variance) - 1);
		}
	}

	int sampleFullConditional(int d, int n, double[] timestamp) {

		int concept = z[d][n];
		nw[documents[d][n]][concept] -= Math.exp(prob[d][n] * views[d][n]);
		nd[d][concept] -= Math.exp(prob[d][n] * views[d][n]);
		nwsum[concept] -= Math.exp(prob[d][n] * views[d][n]);
		nwsum_int[concept]--;
		ndsum[d] -= Math.exp(prob[d][n] * views[d][n]);

		double[] p = new double[K];

		for (int k = 0; k < K; k++) {

			double beta_density = Math.pow(1 - timestamp[d], this.time_prior[k][0] - 1)
					* Math.pow(timestamp[d], this.time_prior[k][1] - 1)
					/ Math.exp(Beta.logBeta(this.time_prior[k][0], this.time_prior[k][1]));

			p[k] = (nd[d][k] + alpha) / (ndsum[d] + K * alpha) * (nw[documents[d][n]][k] + beta) / (nwsum[k] + V * beta)
					* beta_density;

		}
		for (int k = 1; k < K; k++) {
			p[k] += p[k - 1];
		}
		double u = Math.random() * p[K - 1];
		for (int t = 0; t < K; t++) {
			if (u < p[t]) {
				concept = t;
				break;
			}
		}
		nw[documents[d][n]][concept] += Math.exp(prob[d][n] * views[d][n]);
		nd[d][concept] += Math.exp(prob[d][n] * views[d][n]);
		nwsum[concept] += Math.exp(prob[d][n] * views[d][n]);
		nwsum_int[concept]++;
		ndsum[d] += Math.exp(prob[d][n] * views[d][n]);
		return concept;

	}

	public double[][] estimateTheta() {
		double[][] theta = new double[documents.length][K];
		for (int d = 0; d < documents.length; d++) {
			for (int k = 0; k < K; k++) {
				theta[d][k] = (nd[d][k] + alpha) / (ndsum[d] + K * alpha);
			}
		}
		return theta;
	}

	public double[][] estimatePhi() {
		double[][] phi = new double[K][V];
		for (int k = 0; k < K; k++) {
			for (int w = 0; w < V; w++) {
				phi[k][w] = (nw[w][k] + beta) / (nwsum[k] + V * beta);
			}
		}
		return phi;
	}

	public static void main(String args[]) throws IOException, InterruptedException {

		int docnum = 3158;
		String time_file = "file//boc//time.txt";
		String vocab_file = "file//boc//vocab(tech).txt";

		String[] time_string = Corpus.getDocumentTimeString(time_file, docnum);
		double[] time_stamp = Corpus.getDocumentTime(docnum);
		ArrayList<String> concept_set = Wikify.getConceptSet(vocab_file);

		int[][] documents = Wikify.getConceptDocuments(concept_set, docnum);
		int count_1 = 0;
		for (int i = 0; i < documents.length; i++) {
			for (int j = 0; j < documents[i].length; j++) {
				System.out.print(documents[i][j] + " ");
				count_1++;
			}
			System.out.println();
		}

		double[][] link_prob = Wikify.getDocumentsLinkProbability(concept_set, docnum);
		int count_2 = 0;
		for (int i = 0; i < link_prob.length; i++) {
			for (int j = 0; j < link_prob[i].length; j++) {
				System.out.print(link_prob[i][j] + " ");
				count_2++;
			}
			System.out.println();
		}
		System.out.println(concept_set.size());

		double[][] views = Wikify.getArtileView(documents, time_string, concept_set);
		int count_3 = 0;
		for (int i = 0; i < views.length; i++) {
			for (int j = 0; j < views[i].length; j++) {
				System.out.print(views[i][j] + " ");
				count_3++;
			}
			System.out.println();
		}

		System.out.println(count_1 + "\t" + count_2 + "\t" + count_3);

		int K = 50;

		double alpha = (double) 50 / K;
		double beta = 0.1;

		int iterations = 1000;
		System.out.println("Concept over Time using Gibbs Sampling.");
		System.out.println(documents.length + ":" + count_1 + " " + link_prob.length + ":" + count_2 + " "
				+ views.length + ":" + count_3);
		COT cot = new COT(documents, concept_set.size(), time_stamp, link_prob, views);
		cot.markovChain(K, alpha, beta, iterations);

		double[][] theta;
		double[][] phi;

		theta = cot.estimateTheta();
		phi = cot.estimatePhi();

		File result;
		OutputStream out;
		BufferedWriter bw;

		/*
		 * try TOT on articles
		 */

		// LDA tot = new LDA(documents, concept_set.size());
		// tot.markovChain(K, alpha, beta, iterations);
		// theta = tot.estimateTheta();
		// phi = tot.estimatePhi();

		result = new File("boc//concept assignment(TOT with view)tech 10.txt");
		out = new FileOutputStream(result, false);
		bw = new BufferedWriter(new OutputStreamWriter(out, "utf-8"));

		for (int d = 0; d < cot.z.length; d++) {
			for (int n = 0; n < cot.z[d].length; n++) {

				bw.write(cot.z[d][n] + " ");
			}
			bw.newLine();
		}
		bw.close();
		out.close();

		result = new File("file//boc//theta(TOT with view)tech 10.txt");
		out = new FileOutputStream(result, false);
		bw = new BufferedWriter(new OutputStreamWriter(out, "utf-8"));

		for (int m = 0; m < theta.length; m++) {

			for (int k = 0; k < theta[m].length; k++) {
				bw.write(theta[m][k] + " ");
			}
			bw.newLine();
		}
		bw.close();
		out.close();

		result = new File("file//boc//topic top 10 concepts(TOT with view)tech 10.txt");
		out = new FileOutputStream(result, false);
		bw = new BufferedWriter(new OutputStreamWriter(out, "utf-8"));

		for (int k = 0; k < phi.length; k++) {

			double[] array = phi[k];
			for (int i = 0; i < 10; i++) {
				int maxIndex = CommonMethod.maxIndex(array);
				// bw.write(vocab.get(maxIndex)+" "+array[maxIndex]+" ");
				bw.write(concept_set.get(maxIndex) + "   ");
				array[maxIndex] = 0;
			}
			bw.newLine();
		}
		bw.close();
		out.close();

		System.out.println(CommonMethod.AverageKLdivergence(phi));
		System.out.println(CommonMethod.perplexity(theta, phi, documents, link_prob));
	}

}
