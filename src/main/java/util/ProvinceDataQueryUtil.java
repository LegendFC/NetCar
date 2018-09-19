package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.print.DocFlavor.STRING;
import javax.swing.ProgressMonitorInputStream;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

public class ProvinceDataQueryUtil {
		//构造省对县的map映射
		private static HashMap<String,String> mapP = new HashMap<String,String>();
		//构造市对县的map映射
		private static HashMap<String,String> mapC = new HashMap<String,String>();
		//构造省级的list
		private static ArrayList<String> listCountryside = new ArrayList<String>();
		
		/*
		 *默认表示区县级别为6位数字
		 *表示地市级别为4位数字
		 *表示省级别为2位数字 
		 */
		
		private static int len_countryside = 6;//县长度为6个字符
		private static int len_city = 4;//市长度为4个字符
		private static int len_province = 2;//省长度为2个字符
		
		/*
		 * 下面两个 num_company 和 num_status 没有用到 
		 * 但是为了预防之后公司的增加或者减少 车辆状态的增加或者减少
		 * 由于之后的循环会用到，这里先放上
		 */
		public static int num_company = 3;//公司个数为3个
		public static int num_status = 3;//车辆运营状态为3个
		
		/*
		 *公司编号开始到公司编号结束是为了方便修改公司数目
		 *因为后面循环会用到，而且是    company_start <= company <= company_end
		 *以防之后程序之后的修改 
		 */
		private static int company_start = 1;//公司编号循环开始
		private static int company_end = 3;//公司编号循环结束
		
		/*
		 *车辆状态编号开始到车辆状态编号结束是为了方便修改公司数目
		 *因为后面循环会用到，而且是    status_start <= status <= status_end
		 *以防之后程序之后的修改 
		 */
		private static int status_start = 1;//车辆状态编号循环开始
		private static int status_end = 3;//车辆状态编号循环结束
		
		/*
		 * 这里很关键的文件路径
		 * 保存的是全国的省集合
		 */
		private static String path = "/Users/shushu/desktop/all.csv";//需要本地的全国各县的csv文件的保存路径！！！
		public static void main(String[] args) throws IOException {
			Set<HostAndPort> clusterNodes=new HashSet<HostAndPort>();
			clusterNodes.add(new HostAndPort("192.168.1.37",16379));
			JedisCluster cluster=new JedisCluster(clusterNodes); 
			
			/*
			 *提前构造好 mapP、 mapC 、listCountryside 
			 */
			if(mapC.size()==0)
				ProvinceDataQueryUtil.createMap(path);
			//***********************************************************************************************
			/*
			 * 测试
			 */
			String[] s = new String[16];
			s[0] = "110101-1-1";
			s[1] = "110101-1-*";
			s[2]= "110101-*-1";
			s[3] = "110101-*-*";
			
			s[4] = "1101-1-1";
			s[5] = "1101-1-*";
			s[6] = "1101-*-1";
			s[7] = "1101-*-*";
			
			s[8]  = "11-1-1";
			s[9]  = "11-1-*";
			s[10] = "11-*-1";
			s[11] = "11-*-*";
			
			s[12]  = "*-1-1";
			s[13] = "*-1-*";
			s[14] = "*-*-1";
			s[15] = "*-*-*";
			
			String[] mystr = new String[16];
			mystr[0]="计算区县-公司-状态:";
			mystr[1]="计算区县-公司:";
			mystr[2]="计算区县-状态:";
			mystr[3]="计算区县";
			
			mystr[4]="计算地市-公司-状态:";
			mystr[5]="计算地市-公司:";
			mystr[6]="计算地市-状态:";
			mystr[7]="计算地市:";
			
			mystr[8]="计算省-公司-状态:";
			mystr[9]="计算省-公司:";
			mystr[10]="计算省-状态:";
			mystr[11]="计算省:";
			
			mystr[12]="计算全国-公司-状态:";
			mystr[13]="计算全国-公司:";
			mystr[14]="计算全国-状态:";
			mystr[15]="计算全国:";
			int num = 0;
			for(int i = 0 ; i < 16 ; i ++){
				System.out.println(mystr[i]);
				num = calculate(cluster,s[i],path);
				System.out.println(num);
			}
		}
		
		//*********************************************************************************************
		
		/*
		 * 对串进行预处理，选择相对应的计算函数
		 */
		public static int calculate(JedisCluster cluster,String str, String path){
			try{
				if(mapC.size()==0)
					ProvinceDataQueryUtil.createMap(path);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			long start = System.currentTimeMillis();
			int num = 0; //返回对应的计算结果
			String[] result = str.split("-");//按照"-"进行分割
			
			int len = result[0].length();	//对提取到的串进行预处理
			/*
			 * 外层判断县、市、省级别
			 */
			if(len == len_countryside ){//区县内层判断查询类型
				if(result[1] != "*" && result[2] != "*"){// 计算区县-公司-状态  110101-1-1
					num = calculate_countryside_company_status(cluster,str);
				}
				else if(result[1] != "*" && result[2] == "*"){// 计算区县-公司  110101-1-*
					num = calculate_countryside_company(cluster,str);
				}
				else if(result[1] == "*" && result[2] != "*"){// 计算区县-状态 110101-*-1
					num = calculate_countryside_status(cluster,str);
				}
				else if(result[1] == "*" && result[2] == "*"){// 计算区县 110101-*-*
					num = calculate_countryside(cluster,str);
				}
			}
			else if(len == len_city){//地市内层判断查询类型
				if(result[1] != "*" && result[2] != "*"){// 计算地市-公司-状态  1101-1-1
					num = calculate_city_company_status(cluster,str);
				}
				else if(result[1] != "*" && result[2] == "*"){// 计算地市-公司  1101-1-*
					num = calculate_city_company(cluster,str);
				}
				else if(result[1] == "*" && result[2] != "*"){// 计算地市-状态 1101-*-1
					num = calculate_city_status(cluster,str);
				}
				else if(result[1] == "*" && result[2] == "*"){// 计算地市 1101-*-*
					num = calculate_city(cluster,str);
				}
			}
			else if(len == len_province){//省级内层判断查询类型
				if(result[1] != "*" && result[2] != "*"){// 计算省-公司-状态  11-1-1
					num = calculate_province_company_status(cluster,str);
				}
				else if(result[1] != "*" && result[2] == "*"){// 计算省-公司  11-1-*
					num = calculate_province_company(cluster,str);
				}
				else if(result[1] == "*" && result[2] != "*"){// 计算省-状态 11-*-1
					num = calculate_province_status(cluster,str);
				}
				else if(result[1] == "*" && result[2] == "*"){// 计算省 11-*-*
					num = calculate_province(cluster,str);
				}
			}
			else{//全国内层判断查询类型
				if(result[1] != "*" && result[2] != "*"){// 计算全国-公司-状态  *-1-1
					num = calculate_china_company_status(cluster,str);
				}
				else if(result[1] != "*" && result[2] == "*"){// 计算全国-公司  *-1-*
					num = calculate_china_company(cluster,str);
				}
				else if(result[1] == "*" && result[2] != "*"){// 计算全国-状态 *-*-1
					num = calculate_china_status(cluster,str);
				}
				else if(result[1] == "*" && result[2] == "*"){// 计算全国  *-*-*
					num = calculate_china(cluster,str);
				}
			}
			long end = System.currentTimeMillis();
			System.out.println("计算所用时间:"+ (end - start)+"ms");
			return num;
		}
		
		//****************************************************************************************************
		/*
		 *计算区县-公司-状态 
		 *字符串格式110101-1-1
		 */
		public static int calculate_countryside_company_status(JedisCluster cluster,String str){
			
			int countryside_company_status =0;
			if(cluster.exists(str)){//判断是否存在此条记录
				countryside_company_status = Integer.parseInt(cluster.get(str));//由于key-value 中的value使用String,因此用get
			}
			return countryside_company_status;
		}
		
		/*
		 * 计算区县-公司
		 * 字符串格式110101-1-*
		 */
		public static int calculate_countryside_company(JedisCluster cluster,String str){
			int countryside_company = 0;
			String s;
			String[] result = str.split("-");
			/*
			 * 循环次数num_status
			 */
			for(int status = status_start ; status <= status_end ; status ++){
				s = "";//初始化为空
				s = result[0]+"-"+result[1]+"-"+status+"";//遍历status 从status_start到 status_end
				if(cluster.exists(s)){
					countryside_company += Integer.parseInt(cluster.get(s));
				}
			}
			return countryside_company;
		}
		
		/*
		 * 计算区县-状态
		 * 字符串格式110101-*-1
		 */
		public static int calculate_countryside_status(JedisCluster cluster,String str){
			int countryside_status = 0;
			String s;
			String[] result = str.split("-");
			/*
			 * 循环次数num_company
			 */
			for(int company = company_start; company <= company_end ; company++){
				s = "";
				s = result[0]+"-"+company+""+"-"+result[2];//遍历company 从company_start到company_end
				if(cluster.exists(s)){
					countryside_status += Integer.parseInt(cluster.get(s));
				}
			}
			return countryside_status;
		}
		
		/*
		 * 计算区县
		 * 字符串格式110101-*-*
		 */
		public static int calculate_countryside(JedisCluster cluster,String str){
			int countryside = 0;
			String s;
			String[] result = str.split("-");
			/*
			 * 循环次数num_company * num_status 
			 */
			for(int company = company_start ; company <= company_end ; company ++){
				for(int status = status_start ; status <= status_end ; status ++){
					s = "";
					s = result[0]+"-"+company+""+"-"+status+"";//嵌套循环遍历company和status 
					if(cluster.exists(s)){//这里加上了判断！！！！
						countryside += Integer.parseInt(cluster.get(s));
					}
				}
			}
			return countryside;
		}
		
		//*****************************************************************************************************
		/*
		 * 计算地市-公司-状态
		 * 字符串格式1101-1-1
		 */
		
		public static int calculate_city_company_status(JedisCluster cluster,String str){
			int city_company_status = 0;
			String s;
			String[] result = str.split("-");
			String value = mapC.get(result[0]);//得到由该市所有县组成的一个字符串  ","为每个县之间的分隔符
			String[] countrysides = value.split(",");//得到所有的县
			int num_countryside = countrysides.length;//计算县的个数 循环体外面计算
			for(int countryside = 0 ; countryside < num_countryside ; countryside ++){
				s = "";
				s = countrysides[countryside]+"-"+result[1]+"-"+result[2];
				if(cluster.exists(s)){
					city_company_status += Integer.parseInt(cluster.get(s));
				}
			}
			return city_company_status;
		}
		
		/*
		 * 计算地市-公司
		 * 字符串格式1101-1-*
		 */
		public static int calculate_city_company(JedisCluster cluster,String str){
			int city_company = 0;
			String s;
			String[] result = str.split("-");
			String value = mapC.get(result[0]);//得到由该市所有县组成的一个字符串  ","为每个县之间的分隔符
			String[] countrysides = value.split(",");//得到所有的县
			int num_countryside = countrysides.length;//计算县的个数 循环体外面计算
			for(int countryside = 0 ; countryside < num_countryside ; countryside ++){
				for(int status = status_start ; status <= status_end ; status ++){
					s = "";
					s = countrysides[countryside]+"-"+result[1]+"-"+status+"";//循环计算每个状态的个数
					if(cluster.exists(s)){
						city_company += Integer.parseInt(cluster.get(s));
					}
				}
			}
			return city_company;
		}
		
		/*
		 * 计算地市-状态
		 * 字符串格式1101-*-1
		 */
		public static int calculate_city_status(JedisCluster cluster,String str){
			int city_status = 0;
			String s;
			String[] result = str.split("-");
			String value = mapC.get(result[0]);//得到由该市所有县组成的一个字符串  ","为每个县之间的分隔符
			String[] countrysides = value.split(",");//得到所有的县
			int num_countryside = countrysides.length;//计算县的个数 循环体外面计算
			for(int countryside = 0 ; countryside < num_countryside ; countryside ++){
				for(int company = company_start ; company <= company_end ; company ++){
					s = "";
					s = countrysides[countryside]+"-"+company+""+"-"+result[2];//循环计算每个状态的个数
					if(cluster.exists(s)){
						city_status += Integer.parseInt(cluster.get(s));
					}
				}
			}
			return city_status;
		}
		
		/*
		 * 计算地市
		 * 字符串格式1101-*-*
		 */
		public static int calculate_city(JedisCluster cluster,String str){
			int city = 0;
			String s;
			String[] result = str.split("-");
			String value = mapC.get(result[0]);//得到由该市所有县组成的一个字符串  ","为每个县之间的分隔符
			String[] countrysides = value.split(",");//得到所有的县
			int num_countryside = countrysides.length;//计算县的个数 循环体外面计算
			/*
			 * 循环次数 num_countryside * num_company * num_status
			 */
			for(int countryside = 0 ; countryside < num_countryside ; countryside ++){
				for(int company = company_start ; company <= company_end ; company ++){
					for(int status = status_start ; status <= status_end ; status ++){
						s = "";
						s = countrysides[countryside]+"-"+countryside+""+"-"+status+"";//循环计算每个状态的个数
						if(cluster.exists(s)){
							city += Integer.parseInt(cluster.get(s));
						}
					}
				}
			}
			return city;
		}
		
		//**************************************************************************************************
		/*
		 *计算省-公司-状态 
		 * 字符串格式11-1-1
		 */
		public static int calculate_province_company_status(JedisCluster cluster,String str){
			int province_company_status = 0;
			String s;
			String[] result = str.split("-");
			String value = mapP.get(result[0]);//得到所有的县组成的集合 分隔符为","
			String[] provinces = value.split(",");
			int num_province = provinces.length; //得到该省所有的县的个数
			for(int province = 0 ; province < num_province ; province ++){
				s = "";
				s = provinces[province] + "-"+result[1]+"-"+result[2];
				if(cluster.exists(s)){
					province_company_status += Integer.parseInt(cluster.get(s));
				}
			}		
			return province_company_status;
		}
		
		/*
		 *计算省-公司 
		 * 字符串格式11-1-*
		 */
		public static int calculate_province_company(JedisCluster cluster,String str){
			int province_company = 0;
			String s;
			String[] result = str.split("-");
			String value = mapP.get(result[0]);//得到所有的县组成的集合 分隔符为","
			String[] provinces = value.split(",");
			int num_province = provinces.length; //得到该省所有的县的个数
			for(int province = 0 ; province < num_province ; province ++){
				for(int status = status_start ; status <= status_end ; status ++){
					s = "";
					s = provinces[province] + "-"+result[1]+"-"+status+"";
					if(cluster.exists(s)){
						province_company += Integer.parseInt(cluster.get(s));
					}
				}
			}
			return province_company;
		}
		
		/*
		 * 计算省-状态
		 * 字符串格式11-*-1
		 */
		public static int calculate_province_status(JedisCluster cluster,String str){
			int province_status = 0;
			String s;
			String[] result = str.split("-");
			String value = mapP.get(result[0]);//得到所有的县组成的集合 分隔符为","
			String[] provinces = value.split(",");
			int num_province = provinces.length; //得到该省所有的县的个数
			for(int province = 0 ; province < num_province ; province ++){
				for(int company = company_start ; company <= status_end ; company ++){
					s = "";
					s = provinces[province] + "-"+company+""+"-"+result[2];
					if(cluster.exists(s)){
						province_status += Integer.parseInt(cluster.get(s));
					}
				}
			}
			return province_status;
		}
		
		/*
		 * 计算省
		 * 字符串格式11-*-*
		 */
		public static int calculate_province(JedisCluster cluster,String str){
			int province = 0;
			String s;
			String[] result = str.split("-");
			String value = mapP.get(result[0]);//得到所有的县组成的集合 分隔符为","
			String[] provinces = value.split(",");
			int num_province = provinces.length; //得到该省所有的县的个数
			for(int pro = 0 ; pro < num_province ; pro ++){
				for(int company = company_start ; company <= status_end ; company ++){
					for(int status = status_start ; status <= status_end ; status ++){
						s = "";
						s = provinces[pro]+"-"+company+""+"-"+status+"";
						if(cluster.exists(s)){
							province += Integer.parseInt(cluster.get(s));
						}
					}
				}
			}
			return province;
		}
		
		//******************************************************************************************************
		/*
		 * 计算全国-公司-状态
		 * 字符串格式*-1-1
		 */
		public static int calculate_china_company_status(JedisCluster cluster,String str){
			int china_company_status = 0;
			String s;
			String[] result = str.split("-");
			int num_province = listCountryside.size();
			for(int province = 0 ; province < num_province ; province++){
				s = "";
				s = listCountryside.get(province)+"-"+result[1]+"-"+result[2];
				if(cluster.exists(s)){
					china_company_status += Integer.parseInt(cluster.get(s));
				}
			}
			return china_company_status;
		}
		
		/*
		 * 计算全国-公司
		 * 字符串类型*-1-*
		 */
		public static int calculate_china_company(JedisCluster cluster,String str){
			int china_company = 0;
			String s;
			String[] result = str.split("-");
			int num_province = listCountryside.size();
			for(int province = 0 ; province < num_province ; province++){
				for(int status = status_start ; status <= status_end ; status ++){
					s = "";
					s = listCountryside.get(province)+"-"+result[1]+"-"+status+"";
					if(cluster.exists(s)){
						china_company += Integer.parseInt(cluster.get(s));
					}
				}
			}
			return china_company;
		}

		/*
		 * 计算全国-状态
		 * 字符串类型*-*-1
		 */
		public static int calculate_china_status(JedisCluster cluster,String str){
			int china_status = 0;
			String s;
			String[] result = str.split("-");
			int num_province = listCountryside.size();
			for(int province = 0 ; province < num_province ; province++){
				for(int company = company_start ; company <= company_end ; company ++){
					s = "";
					s = listCountryside.get(province)+"-"+company+""+"-"+result[2];
					if(cluster.exists(s)){
						china_status += Integer.parseInt(cluster.get(s));
					}
				}
			}
			return china_status;
		}
		
		/*
		 * 计算全国
		 * 字符串类型*-*-*
		 */
		public static int calculate_china(JedisCluster cluster,String str){
			int china = 0;
			String s;
			int num_province = listCountryside.size();
			for(int province = 0 ; province < num_province ; province++){
				for(int company = company_start ; company <= company_end ; company ++){
					for(int status = status_start ; status <= status_end ; status ++){
						s = "";
						s = listCountryside.get(province)+"-"+company+""+"-"+status+"";
						if(cluster.exists(s)){
							china += Integer.parseInt(cluster.get(s));
						}
					}
				}
			}
			return china;
		}
		
		//****************************************************************************************************
		/*
		 * 构造全国的所有县listCountryside
		 * 构造省对县的mapP 
		 * 构造市对县的mapC
		 */
		public static void createMap(String path) throws IOException{
			
			BufferedReader reader = new BufferedReader(new FileReader(new File(path)));
			String line = "";
			while((line=reader.readLine())!=null){
				String[] str = line.split(",");
				String manageNo3 = str[0];//区县
				String manageNo2 = manageNo3.substring(0,4);//地市
				String manageNo1 = manageNo3.substring(0,2);//省级
				
				//创建listCountryside 
				/*
				 * 提前保存全国的县
				 * 用于计算全国这一等级
				 */
				if(!listCountryside.contains(manageNo3)){
					listCountryside.add(manageNo3);
				}
				
				//创建mapP 构建省对县
				/*
				 * 提前计算省对应的县
				 * 用于计算省这一等级
				 */
				if(!mapP.containsKey(manageNo1)){
					mapP.put(manageNo1, manageNo3);
				}else{
					String value = mapP.get(manageNo1);
					String[] str1 = value.split(",");
					//判断是否含有manageNo3
					//没有，添加
					boolean flag = false;
					for(int i = 0;i<str1.length;i++){
						if(str1[i].equals(manageNo3)){
							flag = true;
						}
						if(flag == true){
							break;
						}
					}
					if(!flag){
						mapP.put(manageNo1,value+","+manageNo3);
					}
				}
				
				//创建mapC
				/*
				 * 提前计算市对应的县
				 * 用于计算市这一等级
				 */
				if(!mapC.containsKey(manageNo2)){
					mapC.put(manageNo2, manageNo3);
				}else{
					String value = mapC.get(manageNo2);
					String[] str1 = value.split(",");
					//判断是否含有manageNo3
					//没有，添加
					boolean flag = false;
					for(int i = 0;i<str1.length;i++){
						if(str1[i].equals(manageNo3)){
							flag = true;
						}
						if(flag ==true){
							break;
						}
					}
					if(!flag){
						mapC.put(manageNo2, value+","+manageNo3);
					}
				}
			}
			reader.close();
		}
}
