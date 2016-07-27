package cot;

/*
 * LDA GibbsSampling
 * @author: Liang Yao
 */
public class LDA {

	int[][] documents;

	double[][] prob;

	int V;

	int K;

	double alpha;

	double beta;

	int[][] z;

	double[][] nw;

	double[][] nd;

	double[] nwsum;

	double[] ndsum;

	double[][] views;

	int iterations;

	public LDA(int[][] documents, int V) {

		this.documents = documents;
		this.V = V;

	}

	public void initialState() {

		int D = documents.length;
		nw = new double[V][K];
		nd = new double[D][K];
		nwsum = new double[K];
		ndsum = new double[D];

		z = new int[D][];
		for (int d = 0; d < D; d++) {

			int Nd = documents[d].length;

			z[d] = new int[Nd];

			double new_doc_length = 0.0;
			for (int n = 0; n < Nd; n++) {
				int topic = (int) (Math.random() * K);

				z[d][n] = topic;

				nw[documents[d][n]][topic] += 1;

				nd[d][topic] += 1;

				nwsum[topic] += 1;

				new_doc_length += 1;
			}
			ndsum[d] = new_doc_length;

			// ndsum[d] = Nd;
		}

	}

	public void markovChain(int K, double alpha, double beta, int iterations) {

		this.K = K;
		this.alpha = alpha;
		this.beta = beta;
		this.iterations = iterations;

		initialState();

		double average_time = 0;

		for (int i = 0; i < this.iterations; i++) {
			System.out.println(i + " " + V);

			long current_time = System.currentTimeMillis();

			gibbs();

			long cost_time = System.currentTimeMillis() - current_time;

			average_time += cost_time;

			System.out.println("平均耗时 ： " + (double) average_time / ((i + 1) * 1000));
		}
	}

	public void gibbs() {

		for (int d = 0; d < z.length; d++) {
			for (int n = 0; n < z[d].length; n++) {

				int topic = sampleFullConditional(d, n);
				z[d][n] = topic;

			}
		}
	}

	int sampleFullConditional(int d, int n) {

		int topic = z[d][n];
		nw[documents[d][n]][topic] -= 1;
		nd[d][topic] -= 1;
		nwsum[topic] -= 1;
		ndsum[d] -= 1;

		double[] p = new double[K];

		for (int k = 0; k < K; k++) {

			p[k] = (nd[d][k] + alpha) / (ndsum[d] + K * alpha) * (nw[documents[d][n]][k] + beta)
					/ (nwsum[k] + V * beta);
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
