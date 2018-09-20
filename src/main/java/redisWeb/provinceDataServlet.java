package redisWeb;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import jdk.nashorn.internal.objects.PrototypeObject;
import com.google.gson.Gson;
import dataStructure.JsonBean;
import dataStructure.Key_Order;
import org.json.JSONObject;
import org.json.JSONString;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.Pipeline;
import util.HttpUtil;
import util.ProvinceDataQueryUtil;

/**
 * Servlet implementation class provinceDataServlet
 */
@WebServlet("/provinceDataServlet")
//http://localhost:8080/REDIS/BJ.jsp?provincenum=1
public class provinceDataServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	//newly added
	private Gson gson=new Gson();
	public int[] provinceCarAmount=new int[16];
	//add end

	//new annotation
//	JedisCluster cluster;
//	Set<HostAndPort> clusterNodes=new HashSet<HostAndPort>();
	//annotation end

	public static String[] provinceNames=new String[]{"����","�Ϻ�","���","�㶫","����","����","�ຣ","����","����","����","����","����","�㽭","����","����","����","���ɹ�","����","�½�","����","����","����","ɽ��","����","����","������","����","ɽ��","����","�ӱ�","�Ĵ�"};
	public static String[] provinceCode=new String[]{"110000","310000","120000","440000","640000","500000","630000","220000","540000","350000","620000","520000","330000","320000","360000","210000","150000","420000","650000","340000","610000","450000","140000","460000","430000","230000","530000","370000","410000","130000","510000"};
	public static String[][] districtCodes=new String[][]{
		{"110101","110102","110105","110106","110107","110108","110109","110111","110112","110113","110114","110115","110116","110117","110118","110119"},
		{"310101","301104","310105","310106","310107","310108","310109","310110","310112","310113","310114","310115","310116","310117","310118","310120","310230"},
		{"120101","120102","120103","120104","120105","120106","120110","120111","120112","120113","120114","120115","120116","120117","120118","120225"},
		{"4401","4402","4403","4404","4405","4406","4407","4408","4409","4412","4413","4414","4415","4416","4417","4418","441900","442000","4451","4452","4453"},
		{"6401","6402","6403","6404","6405"},
		{"500101","500102","500103","500104","500105","500106","500107","500108","500109","500110","500111","500112","500113","500114","500115","500116","500117","500118","500119","500120","500151","500152","500153","500228","500229","500230","500231","500232","500233","500234","500235","500236","500237","500238","500240","500241","500242","500243"},
		{"6301","6302","6322","6323","6325","6326","6327","6328"},
		{"2201","2202","2203","2204","2205","2206","2207","2208","2224"}		
		};
	public static String[][] districtNames=new String[][]{
		{"������","������","������","��̨��","ʯ��ɽ��","������","��ͷ����","��ɽ��","ͨ����","˳����","��ƽ��","������","������","ƽ����","������","������"},
		{"������","�����","������","������","������","բ����","�����","������","������","��ɽ��","�ζ���","�ֶ�����","��ɽ��","�ɽ���","������","������","������"},
		{"��ƽ��","�Ӷ���","������","�Ͽ���","�ӱ���","������","������","������","������","������","������","������","��������","������","������","����"},
		{"������","�ع���","������","�麣��","��ͷ��","��ɽ��","������","տ����","ï����","������","������","÷����","��β��","��Դ��","������","��Զ��","��ݸ��","��ɽ��","������","������","�Ƹ���"},
		{"������","ʯ��ɽ��","������","��ԭ��","������"},
		{"������","������","������","��ɿ���","������","ɳƺ����","��������","�ϰ���","������","�뽭��","������","�山��","������","ǭ����","������","������","�ϴ���","������","�ϴ���","�ɽ��","ͭ����","������","�ٲ���","��ƽ��","�ǿ���","�ᶼ��","�潭��","��¡��","����","����","������","�����","��ɽ��","��Ϫ��","ʯ��������������","��ɽ����������������","��������������������","��ˮ����������������"},
		{"������","������","��������������","���ϲ���������","���ϲ���������","�������������","��������������","�����ɹ������������"},
		{"������","������","��ƽ��","��Դ��","ͨ����","��ɽ��","��ԭ��","�׳���","�ӱ߳�����������"}	
		};
		
		
	public static final String SHENZHOU="1";
	public static final String YIDAO="2";
	public static final String SHOUQI="3";
	public static final String ALLCOMPANY="*";
	
	public static final String YUNYING="1";
	public static final String TINGYUN="2";
	
	
	
	public static int listLength=31;
	public static String root="/Users/shushu/Coding/eclipse/REDIS";
    /**
     * @see HttpServlet#HttpServlet()
     */
    public provinceDataServlet() {
        super();
        // TODO Auto-generated constructor stub
        //ip��ַ
        //���ͻ�����192.168.1.81
        //ӯ����192.168.1.34

		//new annotation
//        clusterNodes.add(new HostAndPort("10.2.15.107",7200));
//        clusterNodes.add(new HostAndPort("10.2.15.107",7201));
//        clusterNodes.add(new HostAndPort("10.2.15.107",7202));
//        clusterNodes.add(new HostAndPort("10.2.15.107",7203));
//        clusterNodes.add(new HostAndPort("10.2.15.107",7204));
//        clusterNodes.add(new HostAndPort("10.2.15.107",7205));
//		  cluster=new JedisCluster(clusterNodes);
		//annotation end

		//root=request.getSession().getServletContext().getRealPath("");
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

//		String district=request.getParameter("area");
//		String company=request.getParameter("company");
//		String com[] = company.split(",");
//		String status=request.getParameter("status");

		
		String filePath=this.getServletContext().getRealPath(request.getRequestURI().substring(request.getContextPath().length()));
		String dirpath= new File(filePath).getParent()+"/files/all.csv";
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter pw = response.getWriter();
		response.reset();

	//new annotation
//		if(district.equals("china"))
//		{
//			provinceCarAmount=new int[31][3];
//			for(int i=0;i<listLength;i++)
//			{
//				for(int j=0;j<com.length;j++)
//				{
//					String temp_amount=cluster.get(provinceCode[i].substring(0,2)+"-"+(j+1)+"-1");
//					if(temp_amount==null)
//					{
//						provinceCarAmount[i][j]=0;
//					}
//					else{
//						provinceCarAmount[i][j]=Integer.valueOf(temp_amount);
//					}
//					//provinceCarAmount[i][j]=ProvinceDataQueryUtil.calculate(cluster, provinceCode[i].substring(0, 2)+"-"+(j+1)+"-1",dirpath);
//				}
//			}
//			pw.write(writeJsonChina(com.length));
//		}
//		else
//		{
//			int index=0;
//			for(;index<listLength;index++)
//			{
//				if(district.equals(provinceCode[index]))1+
//				{
//					break;
//				}
//			}
//
//			String[] aDistrictCodes=districtCodes[index];
//			String[] aDistrictNames=districtNames[index];
//			int districtLength=aDistrictCodes.length;
//
//			provinceCarAmount=new int[districtLength][3];
//
//			for(int j=0;j<districtLength-1;j++)
//			{
//				for(int k=0;k<com.length;k++)
//				{
//					provinceCarAmount[j][k]=ProvinceDataQueryUtil.calculate(cluster,aDistrictCodes[j]+"-"+(k+1)+"-1",dirpath);
//				}
//			}
//
//			pw.write(writeJsonProvince(com.length,aDistrictNames));
//		}
		//annotation end

		//newly added
		String temp,jsonString;
		HttpUtil httpUtil=new HttpUtil();
//		temp=httpUtil.doGet("http://139.199.32.80:3000/getResult?serviceRequestID=59");
		temp=httpUtil.doGet("http://192.168.3.16:3000/getResult?serviceRequestID=57");

//
//		a="[\r\n {\r\n\"time\": \"2018-09-15T06:50:22.000Z\",\r\n\"result\": \"[{\\\"key\\\":\\\"10-order-all-110111-*\\\",\\\"orderId\\\":[\\\"000003\\\"]},{\\\"key\\\":\\\"10-order-all-110115-*\\\",\\\"orderId\\\":[\\\"000003\\\"]}]\"\r\n },\r\n{\r\n\"time\": \"2018-09-15T06:50:23.000Z\",\r\n\"result\": \"[{\\\"key\\\":\\\"10-order-all-110111-*\\\",\\\"orderId\\\":[\\\"000003\\\"]},{\\\"key\\\":\\\"10-order-all-110115-*\\\",\\\"orderId\\\":[\\\"000003\\\"]}]\"\r\n },\r\n {\r\n\"time\": \"2018-09-15T06:50:24.000Z\",\r\n\"result\": \"[{\\\"key\\\":\\\"10-order-all-110111-*\\\",\\\"orderId\\\":[\\\"000003\\\"]},{\\\"key\\\":\\\"10-order-all-110115-*\\\",\\\"orderId\\\":[\\\"000003\\\"]}]\"\r\n },\r\n {\r\n\"time\": \"2018-09-15T06:50:25.000Z\",\r\n\"result\": \"[{\\\"key\\\":\\\"10-order-all-110111-*\\\",\\\"orderId\\\":[\\\"000003\\\"]},{\\\"key\\\":\\\"10-order-all-110115-*\\\",\\\"orderId\\\":[\\\"000003\\\"]}]\"\r\n },\r\n {\r\n\"time\": \"2018-09-15T06:50:26.000Z\",\r\n\"result\": \"[{\\\"key\\\":\\\"10-order-all-110111-*\\\",\\\"orderId\\\":[\\\"000003\\\"]},{\\\"key\\\":\\\"10-order-all-110115-*\\\",\\\"orderId\\\":[\\\"000003\\\"]}]\"\r\n },\r\n {\r\n\"time\": \"2018-09-15T06:50:27.000Z\",\r\n\"result\": \"[{\\\"key\\\":\\\"10-order-all-110111-*\\\",\\\"orderId\\\":[\\\"000003\\\"]},{\\\"key\\\":\\\"10-order-all-110115-*\\\",\\\"orderId\\\":[\\\"000003\\\"]}]\"\r\n },\r\n {\r\n\"time\": \"2018-09-15T06:50:28.000Z\",\r\n\"result\": \"[{\\\"key\\\":\\\"10-order-all-110111-*\\\",\\\"orderId\\\":[\\\"000003\\\"]},{\\\"key\\\":\\\"10-order-all-110115-*\\\",\\\"orderId\\\":[\\\"000003\\\"]}]\"\r\n },\r\n {\r\n\"time\": \"2018-09-15T06:50:29.000Z\",\r\n\"result\": \"[{\\\"key\\\":\\\"10-order-all-110111-*\\\",\\\"orderId\\\":[\\\"000003\\\"]},{\\\"key\\\":\\\"10-order-all-110115-*\\\",\\\"orderId\\\":[\\\"000003\\\"]}]\"\r\n }\r\n]\r\n";
//
		jsonString=resolveJson(temp);
		String s="[{\"name\":\"��ɽ��\",\"value\":3},{\"name\":\"������\",\"value\":3}]";
		pw.write(jsonString);
		//add end

		pw.flush();
		pw.close();		
	}

	public String resolveJson(String str){
		JsonBean[] jsonBeans=gson.fromJson(str, JsonBean[].class);
		JsonBean temp_bean=jsonBeans[jsonBeans.length-1];
		Key_Order[] key_orders=gson.fromJson(temp_bean.result,Key_Order[].class);

		for (Key_Order k:key_orders) {
			char c1=k.key.charAt(16);
			char c2=k.key.charAt(17);
//			int i=Integer.valueOf(k.orderId[0]);
			int i=k.orderId.length;
			switch (c1){
				case '0':
					switch(c2){
						case '1':
							provinceCarAmount[0]=i;
							break;
						case '2':
							provinceCarAmount[1]=i;
							break;
						case '5':
							provinceCarAmount[2]=i;
							break;
						case '6':
							provinceCarAmount[3]=i;
							break;
						case '7':
							provinceCarAmount[4]=i;
							break;
						case '8':
							provinceCarAmount[5]=i;
							break;
						case '9':
							provinceCarAmount[6]=i;
							break;
					}
					break;
				case '1':
					switch(c2){
						case '1':
							provinceCarAmount[7]=i;
							break;
						case '2':
							provinceCarAmount[8]=i;
							break;
						case '3':
							provinceCarAmount[9]=i;
							break;
						case '4':
							provinceCarAmount[10]=i;
							break;
						case '5':
							provinceCarAmount[11]=i;
							break;
						case '6':
							provinceCarAmount[12]=i;
							break;
						case '7':
							provinceCarAmount[13]=i;
							break;
						case '8':
							provinceCarAmount[14]=i;
							break;
						case '9':
							provinceCarAmount[15]=i;
							break;
					}
					break;
			}
		}

		String data_lines;
		int districtLength=16;

		data_lines="[";

		for(int i=0;i<districtLength-1;i++)
		{
			data_lines+="{\"name\":\""+districtNames[0][i]+"\", \"value\":"+provinceCarAmount[i]+"},";
		}

		data_lines+="{\"name\":\""+districtNames[0][districtLength-1]+"\", \"value\":"+provinceCarAmount[districtLength-1]+"}]";

		return data_lines;
	}

	//new annotation
//	protected String writeJsonProvince(int length,String[] aDistrictNames){
//		String all="{";
//
//		String[] data_lines=new String[length];
//		int districtLength=aDistrictNames.length;
//
//		for(int index=0;index<length;index++)
//		{
//			data_lines[index]="[";
//
//			for(int i=0;i<districtLength-1;i++)
//			{
//				data_lines[index]+="{\\\"name\\\":\\\""+aDistrictNames[i]+"\\\", \\\"value\\\":"+provinceCarAmount[i][index]+"},";
//			}
//
//			data_lines[index]+="{\\\"name\\\":\\\""+aDistrictNames[districtLength-1]+"\\\", \\\"value\\\":"+provinceCarAmount[districtLength-1][index]+"}]";
//		}
//
//		for(int index=0;index<length-1;index++)
//		{
//			all+="\"data"+(index+1)+"\":\""+data_lines[index]+"\",\r\n";
//		}
//
//		if(length!=0)
//		{
//			all+="\"data"+length+"\":\""+data_lines[length-1]+"\"";
//		}
//		all+="}";
//		return all;
//	}
//
//	protected String writeJsonChina(int length) {
//		String all="{";
//
//		String[] data_lines=new String[length];
//
//		for(int index=0;index<length;index++)
//		{
//			data_lines[index]="[";
//
//			for(int i=0;i<listLength-1;i++)
//			{
//				data_lines[index]+="{\\\"name\\\":\\\""+provinceNames[i]+"\\\", \\\"value\\\":"+provinceCarAmount[i][index]+"},";
//			}
//
//			data_lines[index]+="{\\\"name\\\":\\\""+provinceNames[listLength-1]+"\\\", \\\"value\\\":"+provinceCarAmount[listLength-1][index]+"}]";
//		}
//
//		for(int index=0;index<length-1;index++)
//		{
//			all+="\"data"+(index+1)+"\":\""+data_lines[index]+"\",\r\n";
//		}
//
//		if(length!=0)
//		{
//			all+="\"data"+length+"\":\""+data_lines[length-1]+"\"";
//		}
//		all+="}";
//		return all;
//	}
	//annotation end
}
