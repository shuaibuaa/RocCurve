package src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class FPR_Based_Curves {
	
	static int test[][]; // from d0 to dn
	static int real[][]; // from d0 to dn
	static int testi = 0;
	static int reali = 0;
	
	//[0]TP,[1]TN,[2]FP,[3]FN,[4]FPR,[5]TPR,[6]precision,[7]recall,[8]score,[9]lead,[10]leadcnt,[11]lag,[12]lagcnt
	static ArrayList<double[]> result = new ArrayList<double[]>();
	
	private static void init() {
		File file_test = new File("data//result.txt");
		File file_real = new File("data//true_labels_255Days.txt");
		BufferedReader reader_test = null;
		BufferedReader reader_real = null;
		test = new int[300][34];
		real = new int[300][34];
		try {
			reader_test = new BufferedReader(new FileReader(file_test));
			reader_real = new BufferedReader(new FileReader(file_real));
			String str_test = null;
			String str_real = null;
			while ((str_test = reader_test.readLine()) != null && 
					(str_real = reader_real.readLine()) != null) {
				String[] str_tests;
				String[] str_reals;
				str_tests = str_test.split("\\ ");
				str_reals = str_real.split("\\ ");
				for (int i=0; i<34; i++) {
					test[testi][i] = Integer.parseInt(str_tests[i+2]);
					real[reali][i] = Integer.parseInt(str_reals[i+2]);
				}
				testi++;
				reali++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void printVector(String vname, int[] v) {
		System.out.print(vname+": [ ");
		for (int i=0; i<34; i++) {
			System.out.print(v[i]+" ");
		}
		System.out.print("]\n");
	}
	
	private static void calcForeDete() {
		for (int i=7; i<testi-7; i++) {
			int win[] = new int[34];
			for (int j=-7; j<0; j++) {//forecasting
			//for (int j=-7; j<=7; j++) {//forecasting and detection
				for (int k=0; k<34; k++) {
					win[k] += real[i+j][k];
				}
			}
			double[] temp = compare(test[i], win);//TP,TN,FP,FN
			double TP = temp[0];
			double TN = temp[1];
			double FP = temp[2];
			double FN = temp[3];
			double FPR, TPR, precision, recall, score;
			FPR = (FP+TN==0) ? 0 : 1.0*FP/(FP+TN);
			TPR = (TP+FN==0) ? 0 : 1.0*TP/(TP+FN);
			precision = (TP+FP==0) ? 0 : 1.0*TP/(TP+FP);
			recall = (FP+TN==0) ? 0 : 1.0*FP/(FP+TN);
			score = (precision+recall==0) ? 0 : 2.0*precision*recall/(precision+recall);
			temp[4] = FPR;
			temp[5] = TPR;
			temp[6] = precision;
			temp[7] = recall;
			temp[8] = score;
			
			double lead=0, lag=0;
			int leadcnt=0, lagcnt=0;
			for (int k=0; k<34; k++) {
				if (real[i][k]==1) {
					int sign = 0;
					for (int j=-7; j<0; j++) {
						if (test[i+j][k]==1) {
							lead += -j;
							leadcnt++;
							sign = 1;
							break;
						}
					}
					if (sign == 0){
						lead += 0;
						leadcnt++;
					}
				}
			}
			temp[9] = lead;
			temp[10] = leadcnt;
			for (int k=0; k<34; k++) {
				if (real[i][k]==1) {
					int sign = 0;
					for (int j=0; j<=7; j++) {
						if (test[i+j][k]==1) {
							lag += j;
							lagcnt++;
							sign = 1;
							break;
						}
					}
					if (sign == 0){
						lag += 7;
						lagcnt++;
					}
				}
			}
			temp[11] = lag;
			temp[12] = lagcnt;
			result.add(temp);
		}
	}
	
	private static void rocCurve() {
		ArrayList<FPR_X> plot = new ArrayList<FPR_X>(); 
		Collections.sort(result, new myComparator());
		double TP=0,TN=0,FP=0,FN=0;
		double FPR, TPR;
		double T=0, F=0;
		for (int i=0; i<result.size(); i++) {
			T = T+result.get(i)[0]+result.get(i)[1];
			F = F+result.get(i)[2]+result.get(i)[3];
		}
		for (int i=0; i<result.size(); i++) {
			TP+=result.get(i)[0];
			TN+=result.get(i)[1];
			FP+=result.get(i)[2];
			FN+=result.get(i)[3];
			double x,y;
			x = (TP+TN)/T;
			y = (FP+FN)/F;
//			FPR = (FP+TN==0) ? 0 : FP/(FP+TN);
//			TPR = (TP+FN==0) ? 0 : TP/(TP+FN);
//			System.out.println("["+(i+1)+"]"+" FPR: "+FPR+"\tTPR: "+TPR);
			plot.add(new FPR_X(x,y));
		}
		DrawPlot.draw("FPR vs TPR(Forecasting and Detection)", "False Positive Rate(From 0-1 FP Per-day)", 
				"True Positive Rate(Forecasting and Detection)", plot);
	}
	
	private static void curve() {
		ArrayList<FPR_X> plot = new ArrayList<FPR_X>(); 
		Collections.sort(result, new myComparator());
		double TP=0,TN=0,FP=0,FN=0;
		double FPR, TPR;
		double T=0, F=0;
		for (int i=0; i<result.size(); i++) {
			T = T+result.get(i)[0]+result.get(i)[1];
			F = F+result.get(i)[2]+result.get(i)[3];
		}
		for (int i=0; i<result.size(); i++) {
			TP+=result.get(i)[0];
			TN+=result.get(i)[1];
			FP+=result.get(i)[2];
			FN+=result.get(i)[3];
			FPR = (FP+TN==0) ? 0 : FP/(FP+TN);
			TPR = (TP+FN==0) ? 0 : TP/(TP+FN);
			System.out.println("["+(i+1)+"]"+" FPR: "+FPR+"\tTPR: "+TPR);
			plot.add(new FPR_X(FPR,TPR));
		}
		DrawPlot.draw("FPR vs TPR(Forecasting and Detection)", "False Positive Rate(From 0-1 FP Per-day)", 
				"True Positive Rate(Forecasting and Detection)", plot);
	}
	
	private static double[] compare(int[] test, int[] real) {
		double result[] = new double[20];
		int TP=0, TN=0, FP=0, FN=0;
		for (int i=0; i<34; i++) {
			if (test[i]!=0 && real[i]!=0)
				TP++;
			else if (test[i]==0 && real[i]==0)
				TN++;
			else if (test[i]!=0 && real[i]==0)
				FP++;
			else if (test[i]==0 && real[i]!=0)
				FN++;
		}
		result[0]=TP;
		result[1]=TN;
		result[2]=FP;
		result[3]=FN;
		return result;
	}	
	
	public static void main(String[] args) {
		init();
		calcForeDete();
		curve();
		rocCurve();
	}
	
}

class FPR_X{
	double FPR;
	double X;
	
	public FPR_X(double a, double b){
		FPR = a;
		X = b;
	}
}

class myComparator implements Comparator<double[]> { 
    public int compare(double[] one, double[] another) {
         double i = 0;
         i = one[8] - another[8];
         if(i == 0) {
        	 double j = one[5] - another[5];
        	 if (j == 0)
        		 return 0;
        	 else if (j < 0)
        		 return 1;
        	 else
        		 return -1;
         } 
         else if (i < 0)
        	 return 1;
         else
             return -1;
    }
}
 