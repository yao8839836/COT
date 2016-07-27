package cot;

import org.apache.commons.math3.special.Beta;

public class TOT {

	int[][] documents;

	double[][] prob;

	double[][] views;

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

	int[][] nd_int;

	double[] ndsum;

	int iterations;

	public TOT(int[][] documents, int V, double[] timestamp) {

		this.documents = documents;
		this.V = V;
		this.timestamp = timestamp;

	}

	public void initialState() {

		int D = documents.length;
		nw = new double[V][K];
		nd = new double[D][K];
		nwsum = new double[K];
		ndsum = new double[D];
		nwsum_int = new int[K];

		nd_int = new int[D][K];

		z = new int[D][];

		for (int d = 0; d < D; d++) {
			int Nd = documents[d].length;

			z[d] = new int[Nd];
			double new_doc_length = 0.0;
			for (int n = 0; n < Nd; n++) {
				int concept = (int) (Math.random() * K);

				z[d][n] = concept;

				nw[documents[d][n]][concept] += 1;

				nd[d][concept] += 1;

				nwsum[concept] += 1;

				new_doc_length += 1;

				nwsum_int[concept]++;

				nd_int[d][concept]++;

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

		double average_time = 0;

		for (int i = 0; i < this.iterations; i++) {
			System.out.println(i);

			long current_time = System.currentTimeMillis();

			gibbs();
			updateTimePrior();

			long cost_time = System.currentTimeMillis() - current_time;

			average_time += cost_time;

			System.out.println("平均耗时 ： " + (double) average_time / ((i + 1) * 1000));
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

			double[] time = new double[nwsum_int[k]];
			int k_word_count = 0;

			for (int d = 0; d < z.length; d++) {
				for (int n = 0; n < z[d].length; n++) {

					if (z[d][n] == k) {
						time[k_word_count] = timestamp[d];
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

	/**
	 * 快速更新Beta先验的参数
	 */
	public void updateTimePriorFast() {

		for (int k = 0; k < K; k++) {

			double[] time = new double[documents.length];

			for (int d = 0; d < documents.length; d++) {

				time[d] = nd_int[d][k] * timestamp[d];
			}

			double expect = 0;
			for (int i = 0; i < time.length; i++) {
				expect += time[i];
			}
			expect /= nwsum_int[k];

			double variance = 0;
			for (int i = 0; i < time.length; i++) {

				// variance = variance + (time[i] - expect) * (time[i] -
				// expect);

				for (int n = 0; n < nd[i][k]; n++) {

					variance = variance + (time[i] / nd[i][k] - expect) * (time[i] / nd[i][k] - expect);
				}

			}
			// variance /= time.length;

			variance /= nwsum_int[k];

			time_prior[k][0] = expect * ((expect * (1 - expect) / variance) - 1);
			time_prior[k][1] = (1 - expect) * ((expect * (1 - expect) / variance) - 1);

		}

	}

	int sampleFullConditional(int d, int n, double[] timestamp) {

		int topic = z[d][n];
		nw[documents[d][n]][topic] -= 1;
		nd[d][topic] -= 1;
		nwsum[topic] -= 1;
		ndsum[d] -= 1;
		nwsum_int[topic]--;

		nd_int[d][topic]--;

		double[] p = new double[K];

		for (int k = 0; k < K; k++) {

			// 错误，不是分布函数，应该是概率密度函数
			// double beta_function = Beta.regularizedBeta(timestamp[d],
			// this.time_prior[k][0], this.time_prior[k][1]);
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
				topic = t;
				break;
			}
		}
		nw[documents[d][n]][topic] += 1;
		nd[d][topic] += 1;
		nwsum[topic] += 1;
		ndsum[d] += 1;

		nwsum_int[topic]++;

		nd_int[d][topic]++;

		return topic;

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

}
