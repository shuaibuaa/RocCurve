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
	
	
	
	private static void init() {
		File file_test = new File("data//2,0.145,0.015.txt");
		File file_real = new File("data//real_data.txt");
		BufferedReader reader_test = null;
		BufferedReader reader_real = null;
		test = new int[200][34];
		real = new int[200][34];
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
	
	//FPR vs TPR(Forecasting and Detection)
	private static void calcForeDete() {
		ArrayList<FPR_X> result = new ArrayList<FPR_X>();
		System.out.println("##########FPR vs TPR(Forecasting and Detection)##########");
		for (int i=7; i<testi-7; i++) {
			int win[] = new int[34];
			for (int j=-7; j<=7; j++) {
				for (int k=0; k<34; k++) {
					win[k] += real[i+j][k];
				}
			}
			printVector("test",test[i]);
			printVector("real",win);
			int[] temp = compare(test[i], win);//TP,TN,FP,FN
			int TP = temp[0];
			int TN = temp[1];
			int FP = temp[2];
			int FN = temp[3];
			System.out.print("["+i+"] "+"TP: "+TP+"   TN: "+TN+"   FP: "+FP+"   FN: "+FN);
			result.add(new FPR_X((double)FP/(FP+TN), (double)TP/(TP+FN)));
			DecimalFormat df = new DecimalFormat("0.00");
			String FPR, TPR, precision, recall, score;
			if(FP+TN == 0)
				FPR = df.format(0);
			else
				FPR = df.format(1.0*FP/(FP+TN));
			if(TP+FN == 0)
				TPR = df.format(0);
			else
				TPR = df.format(1.0*TP/(TP+FN));
			if(TP+FP == 0)
				precision = df.format(0);
			else
				precision = df.format(1.0*TP/(TP+FP));
			if(FP+TN == 0)
				recall = df.format(0);
			else
				recall = df.format(1.0*FP/(FP+TN));
			if(Double.parseDouble(precision)+Double.parseDouble(recall)==0)
				score = df.format(0);
			else
				score = df.format(2.0*Double.parseDouble(precision)*Double.parseDouble(recall)/(Double.parseDouble(precision)+Double.parseDouble(recall)));
			System.out.print("      FPR: " + FPR + "   TPR: " + TPR + "   precision: "+precision 
					+ "   recall: "+recall + "   score: " + score + "\n");
			System.out.println();
		}
		Collections.sort(result, new myComparator());
		DrawPlot.draw("FPR vs TPR(Forecasting and Detection)", "False Positive Rate(From 0-1 FP Per-day)", 
				"True Positive Rate(Forecasting and Detection)", result);
	}
	
	//FPR vs TPR(Forecasting)
	private static void calcFore() {
		ArrayList<FPR_X> result = new ArrayList<FPR_X>();
		System.out.println("##########FPR vs TPR(Forecasting)##########");
		for (int i=7; i<testi-7; i++) {
			int win[] = new int[34];
			for (int j=-7; j<0; j++) {
				for (int k=0; k<34; k++) {
					win[k] += real[i+j][k];
				}
			}
			printVector("test",test[i]);
			printVector("real",win);
			int[] temp = compare(test[i], win);//TP,TN,FP,FN
			int TP = temp[0];
			int TN = temp[1];
			int FP = temp[2];
			int FN = temp[3];
			System.out.print("["+i+"] "+"TP: "+TP+"   TN: "+TN+"   FP: "+FP+"   FN: "+FN);
			result.add(new FPR_X((double)temp[2]/(temp[2]+temp[1]), (double)temp[0]/(temp[0]+temp[3])));
			DecimalFormat df = new DecimalFormat("0.00");
			String FPR, TPR, precision, recall, score;
			if(FP+TN == 0)
				FPR = df.format(0);
			else
				FPR = df.format(1.0*FP/(FP+TN));
			if(TP+FN == 0)
				TPR = df.format(0);
			else
				TPR = df.format(1.0*TP/(TP+FN));
			if(TP+FP == 0)
				precision = df.format(0);
			else
				precision = df.format(1.0*TP/(TP+FP));
			if(FP+TN == 0)
				recall = df.format(0);
			else
				recall = df.format(1.0*FP/(FP+TN));
			if(Double.parseDouble(precision)+Double.parseDouble(recall)==0)
				score = df.format(0);
			else
				score = df.format(2.0*Double.parseDouble(precision)*Double.parseDouble(recall)/(Double.parseDouble(precision)+Double.parseDouble(recall)));
			System.out.print("      FPR: " + FPR + "   TPR: " + TPR + "   precision: "+precision 
					+ "   recall: "+recall + "   score: " + score + "\n");
			System.out.println();
		}
		Collections.sort(result, new myComparator());
		DrawPlot.draw("FPR vs TPR(Forecasting)", "False Positive Rate(From 0-1 FP Per-day)", 
				"True Positive Rate(Forecasting)", result);
	}
	
	//FPR vs Lead Time(Forecasting)
	private static void calcLead() {
		int lead = 0;
		int leadcnt = 0;
		ArrayList<FPR_X> result = new ArrayList<FPR_X>();
		for (int i=7; i<reali-7; i++) {
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
			/*
			 * 
			 */
		}
	}
	
	//FPR vs Lag Time(Detection)
	private static void calcLag() {
		int lag = 0;
		int lagcnt = 0;
		ArrayList<FPR_X> result = new ArrayList<FPR_X>();
		for (int i=7; i<reali-7; i++) {
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
			/*
			 * 
			 */
		}
	}
	
	private static int[] compare(int[] test, int[] real) {
		int result[] = new int[4];
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
		calcFore();
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

class myComparator implements Comparator<FPR_X> { 
    public int compare(FPR_X one, FPR_X another) {
         double i = 0;
         i = one.FPR - another.FPR;
         if(i == 0) {
        	 double j = one.X - another.X;
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
 