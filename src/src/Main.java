package src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Main {
	static HashMap<Integer, String> expDateLocMap = new HashMap<Integer, String>();
	static HashMap<Integer, String> realDateLocMap = new HashMap<Integer, String>();
	static HashMap<Integer, Double> dateScoreMap = new HashMap<Integer, Double>();
	static ArrayList<String> allLocations = new ArrayList<String>();
	
	static int[] month = {0,31,59,90,120,151,181,212,243,273,304,334};
	
	private static HashMap<Integer, String> statRecord (String fileName)
	//Request: File format: beijing 20141001 true
	{
		HashMap<Integer, String> dateLocMap = new HashMap<Integer, String>();
		
		File fileReal = new File(fileName);;
		BufferedReader realReader = null;
		String loc;
		int dateStr, date;
		String tempLocStr;
		
		try {
			realReader = new BufferedReader(new FileReader(fileReal));
			String tempString = null;
		
			while ((tempString = realReader.readLine()) != null) {
				
				String[] temps;
				temps = tempString.split(" ");//{"beijing", "20141001", "true"}
				loc = temps[0];
				dateStr = Integer.parseInt(temps[1]);//20141001
				date = dateStr % 100;//1
				dateStr /= 100;//201410
				date += month[(dateStr % 100) - 1];//1+273=274
				dateStr /= 100;//2014
				date += (dateStr-2014) * 365;//274+(2014-2014)*365=274
			
				if (dateLocMap.containsKey(date)) {
					tempLocStr = dateLocMap.get(date);
					tempLocStr = tempLocStr + "|" + loc;
					dateLocMap.put(date, tempLocStr);
				}
				else {
					dateLocMap.put(date, loc);
				}
			}
			realReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (realReader != null) {
				try {
					realReader.close();
				} catch (IOException e1) {
				}
			}
		}
		return dateLocMap;
	}

	private static HashMap<Integer, Double> statScore (String fileName)
	//Request: File format: 20141001 100.3333
	{
		HashMap<Integer, Double> dateScoreMap = new HashMap<Integer, Double>();
		
		File fileReal = new File(fileName);;
		BufferedReader realReader = null;
		double score;
		int dateStr, date;
		
		try {
			realReader = new BufferedReader(new FileReader(fileReal));
			String tempString = null;
		
			while ((tempString = realReader.readLine()) != null) {
				
				String[] temps;
				temps = tempString.split(" ");//{"20141001", "100.3333"}
				score = Double.parseDouble(temps[1]);//100.3333
				dateStr = Integer.parseInt(temps[0]);//20141001
				date = dateStr % 100;//1
				dateStr /= 100;//201410
				date += month[(dateStr % 100) - 1];//1+273=274
				dateStr /= 100;//2014
				date += (dateStr-2014) * 365;//274+(2014-2014)*365=274
				if (!dateScoreMap.containsKey(date))
					dateScoreMap.put(date, score);
			}
			realReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (realReader != null) {
				try {
					realReader.close();
				} catch (IOException e1) {
				}
			}
		}
		return dateScoreMap;
	}
	
	private static void initAllLocations() {
		allLocations.add("beijing");
		allLocations.add("tianjin");
		allLocations.add("hebei");
		allLocations.add("henan");
		allLocations.add("shan1xi");
		allLocations.add("shandong");
		allLocations.add("neimenggu");
		allLocations.add("liaoning");
		allLocations.add("jilin");
		allLocations.add("heilongjiang");
		allLocations.add("shan3xi");
		allLocations.add("hubei");
		allLocations.add("hunan");
		allLocations.add("anhui");
		allLocations.add("jiangsu");
		allLocations.add("gansu");
		allLocations.add("ningxia");
		allLocations.add("sichuan");
		allLocations.add("chongqing");
		allLocations.add("jiangxi");
		allLocations.add("zhejiang");
		allLocations.add("shanghai");
		allLocations.add("xinjiang");
		allLocations.add("xizang");
		allLocations.add("yunnan");
		allLocations.add("guangxi");
		allLocations.add("guangdong");
		allLocations.add("qinghai");
		allLocations.add("fujian");
		allLocations.add("guizhou");
		allLocations.add("hainan");
		allLocations.add("xianggang");
		allLocations.add("aomen");
		allLocations.add("taiwan");	
	}

	private static void calc() {
		//增加一个小程序，将真实事件的开始七天与最后七天的记录删掉
		
		Iterator iter = realDateLocMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Object realDate = entry.getValue();
			Object realLoc = entry.getValue();
			String[] realLocs;
			realLocs = realLoc.toString().split("\\|");
			ArrayList<String> allLocs = new ArrayList<String>();
			allLocs.addAll(allLocations);
			
			int foreTP = 0, foreFP = 0, foreTN = 0, foreFN = 0;
			int detcTP = 0, detcFP = 0, detcTN = 0, detcFN = 0;
			int leadTime = 0, lagTime = 0;
			int leadNum = 0, lagNum = 0;
			
			for (int i=0; i<realLocs.length; i++) {
				int flag = 0;
				for (int j=7; j>=1; j--) {//是否在当前省份该时间前1-7天产生事件预警，记录最早的那一天
					String[] expLocs;
					expLocs = expDateLocMap.get(Integer.parseInt(realDate.toString())-j).split("\\|");
					for (int k=0; k<expLocs.length; k++) {
						if (realLocs[i].equals(expLocs[k])) {//预测成功
							foreTP++;
							leadTime += j;
							leadNum++;
							lagTime += 0;//对于成功预测的事件取0天的延迟时间
							lagNum++;
							flag = 1;
							break;
						}
					}
					if (flag == 1)
						break;
				}
				if (flag == 0) {//预测失败
					leadTime += 0;//对于没有成功预测的事件定义提前天数为0
					leadNum++;
					for (int j=0; j<=7; j++) {//是否在当前省份该时间后0-7天产生事件检测警告，记录最早的那一天
						String[] expLocs;
						expLocs = expDateLocMap.get(Integer.parseInt(realDate.toString())+j).split("\\|");
						for (int k=0; k<expLocs.length; k++) {
							if (realLocs[i].equals(expLocs[k])) {//检测成功
								detcTP++;
								lagTime += j;
								lagNum++;
								flag = 1;
								break;
							}
						}
						if (flag == 1)
							break;
					}
				}
				if (flag == 0) {//预测检测均失败
					lagTime += 7;//对于没有检测出的事件取7天延迟时间
					lagNum++;
					//FN++;
				}
			}
			
		}
	}
	
	public static void main(String[] args) {

		expDateLocMap = statRecord("expRecord.txt");
		realDateLocMap = statRecord("realRecord.txt");
		dateScoreMap = statScore("score.txt");
		
		initAllLocations();
		
		
		
	}

}
