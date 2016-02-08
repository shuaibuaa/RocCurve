package src;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class NPHGS_Curves {

	static HashMap<String, String> userLocMap = null;
	static Connection conn = null;
	static Statement stmt = null;
	static String[] prov_vector={"beijing","tianjin","hebei","henan","shan1xi","shandong","neimenggu","liaoning","jilin","heilongjiang","shan3xi","hubei","hunan","anhui","jiangsu","gansu","ningxia","sichuan","chongqing","jiangxi","zhejiang","shanghai","xinjiang","xizang","yunnan","guangxi","guangdong","qinghai","fujian","guizhou","hainan","xianggang","aomen","taiwan"};
	
	public static int[] genIndicatorVector(Map<String, Integer> S_star){
		
		int[] indicatorVector= new int[prov_vector.length];
		Set<Entry<String, Integer>> entries = S_star.entrySet();
		Iterator<Entry<String, Integer>> iter = entries.iterator();
		Map<String, Integer> provIndicatorMap=new HashMap<String, Integer>();
		
		for(int i=0;i<prov_vector.length;i++){
			provIndicatorMap.put(prov_vector[i], (Integer)i);
		}
		while(iter.hasNext()) {
			Entry entry = iter.next();
			if(provIndicatorMap.containsKey(entry.getKey()))
				indicatorVector[provIndicatorMap.get(entry.getKey())]=1;
		}
		return indicatorVector;
	}
	
	private static void initDB(){
		String url = "jdbc:mysql://localhost:3306/gasdm?user=root&password=&useUnicode=true&characterEncoding=UTF8";
		try {  
			Class.forName("com.mysql.jdbc.Driver");  
			conn = DriverManager.getConnection(url);
		} catch (Exception e) {  
			e.printStackTrace();  
		}
	}
	
	private static void init() {
		userLocMap = new HashMap<String, String>();
		try {
			stmt = conn.createStatement();
			String sql = "select distinct u_id, location from pdven";
			ResultSet rs;
			rs = stmt.executeQuery(sql);
			while(rs.next()){
				String u_id = rs.getString("u_id");
				String location = rs.getString("location");
				userLocMap.put(u_id, location);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static String arrayToString(int[] int_vector){
		String int_String="[ ";
		for(int e:int_vector)
			int_String+=e+" ";
		int_String+="]";
		return int_String;
	}
	
	private static void readfile(String filepath) {
		BufferedWriter outputWriter = null;
		try {
			outputWriter = new BufferedWriter(new FileWriter("data/NPHGS_result.txt"));
            File file = new File(filepath);
            String[] filelist = file.list();
            for (int i = 0; i < filelist.length; i++) {
                File readfile = new File(filepath + "\\" + filelist[i]);
                BufferedReader reader = new BufferedReader(new FileReader(readfile));
                String templine;
                if ((templine = reader.readLine())!=null) {
                	String[] temp = templine.split("\\ ");
                	String day = temp[0].split("\\-")[0];
                	String date = "";
                	date+=day.charAt(0);
                	date+=day.charAt(1);
                	date+=day.charAt(2);
                	date+=day.charAt(3);
                	date+="-";
                	date+=day.charAt(4);
                	date+=day.charAt(5);
                	date+="-";
                	date+=day.charAt(6);
                	date+=day.charAt(7);
                	int length = Integer.parseInt(temp[2]);
                	Map<String, Integer> province = new HashMap<String, Integer>();
                    ArrayList<String> users = new ArrayList<String>();
                    
                	for (int k=3; k<length+3; k++) {
                		users.add(temp[k]);
                	}
            		for (int j=0; j<users.size(); j++) {
            			String prov=userLocMap.get(users.get(j));
            			if(province.containsKey(prov)){
            				Integer times=province.get(prov);
            				province.put(prov,times+1);
            			}else{
            				province.put(prov,1);
            			}
            		}
            		
            		int[] indicatorVector= new int[prov_vector.length];
                    indicatorVector=genIndicatorVector(province);
            		outputWriter.write(date+" "+arrayToString(indicatorVector)+"\n");
            		System.out.println(date+" "+arrayToString(indicatorVector));
            		reader.close();
                }
            }
            outputWriter.close();
	    } catch (IOException e) {
	    }

		
	}
	
	public static void main(String[] args) {
		initDB();
		init();
		readfile("data/nphgs_result");
	}

}
