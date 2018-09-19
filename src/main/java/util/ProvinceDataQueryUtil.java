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
		//����ʡ���ص�mapӳ��
		private static HashMap<String,String> mapP = new HashMap<String,String>();
		//�����ж��ص�mapӳ��
		private static HashMap<String,String> mapC = new HashMap<String,String>();
		//����ʡ����list
		private static ArrayList<String> listCountryside = new ArrayList<String>();
		
		/*
		 *Ĭ�ϱ�ʾ���ؼ���Ϊ6λ����
		 *��ʾ���м���Ϊ4λ����
		 *��ʾʡ����Ϊ2λ���� 
		 */
		
		private static int len_countryside = 6;//�س���Ϊ6���ַ�
		private static int len_city = 4;//�г���Ϊ4���ַ�
		private static int len_province = 2;//ʡ����Ϊ2���ַ�
		
		/*
		 * �������� num_company �� num_status û���õ� 
		 * ����Ϊ��Ԥ��֮��˾�����ӻ��߼��� ����״̬�����ӻ��߼���
		 * ����֮���ѭ�����õ��������ȷ���
		 */
		public static int num_company = 3;//��˾����Ϊ3��
		public static int num_status = 3;//������Ӫ״̬Ϊ3��
		
		/*
		 *��˾��ſ�ʼ����˾��Ž�����Ϊ�˷����޸Ĺ�˾��Ŀ
		 *��Ϊ����ѭ�����õ���������    company_start <= company <= company_end
		 *�Է�֮�����֮����޸� 
		 */
		private static int company_start = 1;//��˾���ѭ����ʼ
		private static int company_end = 3;//��˾���ѭ������
		
		/*
		 *����״̬��ſ�ʼ������״̬��Ž�����Ϊ�˷����޸Ĺ�˾��Ŀ
		 *��Ϊ����ѭ�����õ���������    status_start <= status <= status_end
		 *�Է�֮�����֮����޸� 
		 */
		private static int status_start = 1;//����״̬���ѭ����ʼ
		private static int status_end = 3;//����״̬���ѭ������
		
		/*
		 * ����ܹؼ����ļ�·��
		 * �������ȫ����ʡ����
		 */
		private static String path = "/Users/shushu/desktop/all.csv";//��Ҫ���ص�ȫ�����ص�csv�ļ��ı���·��������
		public static void main(String[] args) throws IOException {
			Set<HostAndPort> clusterNodes=new HashSet<HostAndPort>();
			clusterNodes.add(new HostAndPort("192.168.1.37",16379));
			JedisCluster cluster=new JedisCluster(clusterNodes); 
			
			/*
			 *��ǰ����� mapP�� mapC ��listCountryside 
			 */
			if(mapC.size()==0)
				ProvinceDataQueryUtil.createMap(path);
			//***********************************************************************************************
			/*
			 * ����
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
			mystr[0]="��������-��˾-״̬:";
			mystr[1]="��������-��˾:";
			mystr[2]="��������-״̬:";
			mystr[3]="��������";
			
			mystr[4]="�������-��˾-״̬:";
			mystr[5]="�������-��˾:";
			mystr[6]="�������-״̬:";
			mystr[7]="�������:";
			
			mystr[8]="����ʡ-��˾-״̬:";
			mystr[9]="����ʡ-��˾:";
			mystr[10]="����ʡ-״̬:";
			mystr[11]="����ʡ:";
			
			mystr[12]="����ȫ��-��˾-״̬:";
			mystr[13]="����ȫ��-��˾:";
			mystr[14]="����ȫ��-״̬:";
			mystr[15]="����ȫ��:";
			int num = 0;
			for(int i = 0 ; i < 16 ; i ++){
				System.out.println(mystr[i]);
				num = calculate(cluster,s[i],path);
				System.out.println(num);
			}
		}
		
		//*********************************************************************************************
		
		/*
		 * �Դ�����Ԥ����ѡ�����Ӧ�ļ��㺯��
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
			int num = 0; //���ض�Ӧ�ļ�����
			String[] result = str.split("-");//����"-"���зָ�
			
			int len = result[0].length();	//����ȡ���Ĵ�����Ԥ����
			/*
			 * ����ж��ء��С�ʡ����
			 */
			if(len == len_countryside ){//�����ڲ��жϲ�ѯ����
				if(result[1] != "*" && result[2] != "*"){// ��������-��˾-״̬  110101-1-1
					num = calculate_countryside_company_status(cluster,str);
				}
				else if(result[1] != "*" && result[2] == "*"){// ��������-��˾  110101-1-*
					num = calculate_countryside_company(cluster,str);
				}
				else if(result[1] == "*" && result[2] != "*"){// ��������-״̬ 110101-*-1
					num = calculate_countryside_status(cluster,str);
				}
				else if(result[1] == "*" && result[2] == "*"){// �������� 110101-*-*
					num = calculate_countryside(cluster,str);
				}
			}
			else if(len == len_city){//�����ڲ��жϲ�ѯ����
				if(result[1] != "*" && result[2] != "*"){// �������-��˾-״̬  1101-1-1
					num = calculate_city_company_status(cluster,str);
				}
				else if(result[1] != "*" && result[2] == "*"){// �������-��˾  1101-1-*
					num = calculate_city_company(cluster,str);
				}
				else if(result[1] == "*" && result[2] != "*"){// �������-״̬ 1101-*-1
					num = calculate_city_status(cluster,str);
				}
				else if(result[1] == "*" && result[2] == "*"){// ������� 1101-*-*
					num = calculate_city(cluster,str);
				}
			}
			else if(len == len_province){//ʡ���ڲ��жϲ�ѯ����
				if(result[1] != "*" && result[2] != "*"){// ����ʡ-��˾-״̬  11-1-1
					num = calculate_province_company_status(cluster,str);
				}
				else if(result[1] != "*" && result[2] == "*"){// ����ʡ-��˾  11-1-*
					num = calculate_province_company(cluster,str);
				}
				else if(result[1] == "*" && result[2] != "*"){// ����ʡ-״̬ 11-*-1
					num = calculate_province_status(cluster,str);
				}
				else if(result[1] == "*" && result[2] == "*"){// ����ʡ 11-*-*
					num = calculate_province(cluster,str);
				}
			}
			else{//ȫ���ڲ��жϲ�ѯ����
				if(result[1] != "*" && result[2] != "*"){// ����ȫ��-��˾-״̬  *-1-1
					num = calculate_china_company_status(cluster,str);
				}
				else if(result[1] != "*" && result[2] == "*"){// ����ȫ��-��˾  *-1-*
					num = calculate_china_company(cluster,str);
				}
				else if(result[1] == "*" && result[2] != "*"){// ����ȫ��-״̬ *-*-1
					num = calculate_china_status(cluster,str);
				}
				else if(result[1] == "*" && result[2] == "*"){// ����ȫ��  *-*-*
					num = calculate_china(cluster,str);
				}
			}
			long end = System.currentTimeMillis();
			System.out.println("��������ʱ��:"+ (end - start)+"ms");
			return num;
		}
		
		//****************************************************************************************************
		/*
		 *��������-��˾-״̬ 
		 *�ַ�����ʽ110101-1-1
		 */
		public static int calculate_countryside_company_status(JedisCluster cluster,String str){
			
			int countryside_company_status =0;
			if(cluster.exists(str)){//�ж��Ƿ���ڴ�����¼
				countryside_company_status = Integer.parseInt(cluster.get(str));//����key-value �е�valueʹ��String,�����get
			}
			return countryside_company_status;
		}
		
		/*
		 * ��������-��˾
		 * �ַ�����ʽ110101-1-*
		 */
		public static int calculate_countryside_company(JedisCluster cluster,String str){
			int countryside_company = 0;
			String s;
			String[] result = str.split("-");
			/*
			 * ѭ������num_status
			 */
			for(int status = status_start ; status <= status_end ; status ++){
				s = "";//��ʼ��Ϊ��
				s = result[0]+"-"+result[1]+"-"+status+"";//����status ��status_start�� status_end
				if(cluster.exists(s)){
					countryside_company += Integer.parseInt(cluster.get(s));
				}
			}
			return countryside_company;
		}
		
		/*
		 * ��������-״̬
		 * �ַ�����ʽ110101-*-1
		 */
		public static int calculate_countryside_status(JedisCluster cluster,String str){
			int countryside_status = 0;
			String s;
			String[] result = str.split("-");
			/*
			 * ѭ������num_company
			 */
			for(int company = company_start; company <= company_end ; company++){
				s = "";
				s = result[0]+"-"+company+""+"-"+result[2];//����company ��company_start��company_end
				if(cluster.exists(s)){
					countryside_status += Integer.parseInt(cluster.get(s));
				}
			}
			return countryside_status;
		}
		
		/*
		 * ��������
		 * �ַ�����ʽ110101-*-*
		 */
		public static int calculate_countryside(JedisCluster cluster,String str){
			int countryside = 0;
			String s;
			String[] result = str.split("-");
			/*
			 * ѭ������num_company * num_status 
			 */
			for(int company = company_start ; company <= company_end ; company ++){
				for(int status = status_start ; status <= status_end ; status ++){
					s = "";
					s = result[0]+"-"+company+""+"-"+status+"";//Ƕ��ѭ������company��status 
					if(cluster.exists(s)){//����������жϣ�������
						countryside += Integer.parseInt(cluster.get(s));
					}
				}
			}
			return countryside;
		}
		
		//*****************************************************************************************************
		/*
		 * �������-��˾-״̬
		 * �ַ�����ʽ1101-1-1
		 */
		
		public static int calculate_city_company_status(JedisCluster cluster,String str){
			int city_company_status = 0;
			String s;
			String[] result = str.split("-");
			String value = mapC.get(result[0]);//�õ��ɸ�����������ɵ�һ���ַ���  ","Ϊÿ����֮��ķָ���
			String[] countrysides = value.split(",");//�õ����е���
			int num_countryside = countrysides.length;//�����صĸ��� ѭ�����������
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
		 * �������-��˾
		 * �ַ�����ʽ1101-1-*
		 */
		public static int calculate_city_company(JedisCluster cluster,String str){
			int city_company = 0;
			String s;
			String[] result = str.split("-");
			String value = mapC.get(result[0]);//�õ��ɸ�����������ɵ�һ���ַ���  ","Ϊÿ����֮��ķָ���
			String[] countrysides = value.split(",");//�õ����е���
			int num_countryside = countrysides.length;//�����صĸ��� ѭ�����������
			for(int countryside = 0 ; countryside < num_countryside ; countryside ++){
				for(int status = status_start ; status <= status_end ; status ++){
					s = "";
					s = countrysides[countryside]+"-"+result[1]+"-"+status+"";//ѭ������ÿ��״̬�ĸ���
					if(cluster.exists(s)){
						city_company += Integer.parseInt(cluster.get(s));
					}
				}
			}
			return city_company;
		}
		
		/*
		 * �������-״̬
		 * �ַ�����ʽ1101-*-1
		 */
		public static int calculate_city_status(JedisCluster cluster,String str){
			int city_status = 0;
			String s;
			String[] result = str.split("-");
			String value = mapC.get(result[0]);//�õ��ɸ�����������ɵ�һ���ַ���  ","Ϊÿ����֮��ķָ���
			String[] countrysides = value.split(",");//�õ����е���
			int num_countryside = countrysides.length;//�����صĸ��� ѭ�����������
			for(int countryside = 0 ; countryside < num_countryside ; countryside ++){
				for(int company = company_start ; company <= company_end ; company ++){
					s = "";
					s = countrysides[countryside]+"-"+company+""+"-"+result[2];//ѭ������ÿ��״̬�ĸ���
					if(cluster.exists(s)){
						city_status += Integer.parseInt(cluster.get(s));
					}
				}
			}
			return city_status;
		}
		
		/*
		 * �������
		 * �ַ�����ʽ1101-*-*
		 */
		public static int calculate_city(JedisCluster cluster,String str){
			int city = 0;
			String s;
			String[] result = str.split("-");
			String value = mapC.get(result[0]);//�õ��ɸ�����������ɵ�һ���ַ���  ","Ϊÿ����֮��ķָ���
			String[] countrysides = value.split(",");//�õ����е���
			int num_countryside = countrysides.length;//�����صĸ��� ѭ�����������
			/*
			 * ѭ������ num_countryside * num_company * num_status
			 */
			for(int countryside = 0 ; countryside < num_countryside ; countryside ++){
				for(int company = company_start ; company <= company_end ; company ++){
					for(int status = status_start ; status <= status_end ; status ++){
						s = "";
						s = countrysides[countryside]+"-"+countryside+""+"-"+status+"";//ѭ������ÿ��״̬�ĸ���
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
		 *����ʡ-��˾-״̬ 
		 * �ַ�����ʽ11-1-1
		 */
		public static int calculate_province_company_status(JedisCluster cluster,String str){
			int province_company_status = 0;
			String s;
			String[] result = str.split("-");
			String value = mapP.get(result[0]);//�õ����е�����ɵļ��� �ָ���Ϊ","
			String[] provinces = value.split(",");
			int num_province = provinces.length; //�õ���ʡ���е��صĸ���
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
		 *����ʡ-��˾ 
		 * �ַ�����ʽ11-1-*
		 */
		public static int calculate_province_company(JedisCluster cluster,String str){
			int province_company = 0;
			String s;
			String[] result = str.split("-");
			String value = mapP.get(result[0]);//�õ����е�����ɵļ��� �ָ���Ϊ","
			String[] provinces = value.split(",");
			int num_province = provinces.length; //�õ���ʡ���е��صĸ���
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
		 * ����ʡ-״̬
		 * �ַ�����ʽ11-*-1
		 */
		public static int calculate_province_status(JedisCluster cluster,String str){
			int province_status = 0;
			String s;
			String[] result = str.split("-");
			String value = mapP.get(result[0]);//�õ����е�����ɵļ��� �ָ���Ϊ","
			String[] provinces = value.split(",");
			int num_province = provinces.length; //�õ���ʡ���е��صĸ���
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
		 * ����ʡ
		 * �ַ�����ʽ11-*-*
		 */
		public static int calculate_province(JedisCluster cluster,String str){
			int province = 0;
			String s;
			String[] result = str.split("-");
			String value = mapP.get(result[0]);//�õ����е�����ɵļ��� �ָ���Ϊ","
			String[] provinces = value.split(",");
			int num_province = provinces.length; //�õ���ʡ���е��صĸ���
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
		 * ����ȫ��-��˾-״̬
		 * �ַ�����ʽ*-1-1
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
		 * ����ȫ��-��˾
		 * �ַ�������*-1-*
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
		 * ����ȫ��-״̬
		 * �ַ�������*-*-1
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
		 * ����ȫ��
		 * �ַ�������*-*-*
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
		 * ����ȫ����������listCountryside
		 * ����ʡ���ص�mapP 
		 * �����ж��ص�mapC
		 */
		public static void createMap(String path) throws IOException{
			
			BufferedReader reader = new BufferedReader(new FileReader(new File(path)));
			String line = "";
			while((line=reader.readLine())!=null){
				String[] str = line.split(",");
				String manageNo3 = str[0];//����
				String manageNo2 = manageNo3.substring(0,4);//����
				String manageNo1 = manageNo3.substring(0,2);//ʡ��
				
				//����listCountryside 
				/*
				 * ��ǰ����ȫ������
				 * ���ڼ���ȫ����һ�ȼ�
				 */
				if(!listCountryside.contains(manageNo3)){
					listCountryside.add(manageNo3);
				}
				
				//����mapP ����ʡ����
				/*
				 * ��ǰ����ʡ��Ӧ����
				 * ���ڼ���ʡ��һ�ȼ�
				 */
				if(!mapP.containsKey(manageNo1)){
					mapP.put(manageNo1, manageNo3);
				}else{
					String value = mapP.get(manageNo1);
					String[] str1 = value.split(",");
					//�ж��Ƿ���manageNo3
					//û�У����
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
				
				//����mapC
				/*
				 * ��ǰ�����ж�Ӧ����
				 * ���ڼ�������һ�ȼ�
				 */
				if(!mapC.containsKey(manageNo2)){
					mapC.put(manageNo2, manageNo3);
				}else{
					String value = mapC.get(manageNo2);
					String[] str1 = value.split(",");
					//�ж��Ƿ���manageNo3
					//û�У����
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
