package redisWeb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sound.sampled.LineListener;

import org.apache.commons.lang.ArrayUtils;

//import com.sun.imageio.plugins.gif.GIFImageReaderSpi;
//import com.sun.org.apache.bcel.internal.generic.RETURN;
//import com.sun.org.apache.bcel.internal.generic.StackInstruction;







import dataStructure.GeographicGrid;
import dataStructure.LonLat;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import scripts.Clustering;
import util.LonLatFileReader;
import util.LonLatTranformImpl;

public class getRouteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private JedisCluster cluster;
	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();
		Set<HostAndPort> clusterNodes=new HashSet<HostAndPort>();
		//ip地址
        clusterNodes.add(new HostAndPort("10.2.15.107",7200));	
        clusterNodes.add(new HostAndPort("10.2.15.107",7201));
        clusterNodes.add(new HostAndPort("10.2.15.107",7202));
        clusterNodes.add(new HostAndPort("10.2.15.107",7203));
        clusterNodes.add(new HostAndPort("10.2.15.107",7204));
        clusterNodes.add(new HostAndPort("10.2.15.107",7205));
		cluster=new JedisCluster(clusterNodes);
	}
	
	@SuppressWarnings("resource")
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		//System.out.println((String)req.getParameter("category"));
			PrintWriter pw = resp.getWriter();
			
			resp.setContentType("text/html;charset=UTF-8");
			String kind=(String)req.getParameter("kind");
			String carId=(String)req.getParameter("id");
			System.out.println(carId);
			String carTime=(String)req.getParameter("time");
			
			if(kind.equals("trace"))
			{
				List<String> infoList=new ArrayList<String>();
				infoList=cluster.lrange(carId, 0, -1);
				String json=null;
				if(infoList.size()>0)
				{
					json=toinfoJson(infoList);
					System.out.println("redis:"+json);
				}
					
				else
				{
					System.out.println("intime trace lost");
					json="[{\"id\":\"967790051476\",\"lon\":112.97478333333332,\"lat\":28.186913333333337,\"direction\":20}]";
				}
					
				pw.write(json);
				pw.flush();
				pw.close();
				return;
			}
			else if(kind.equals("history"))
			{

				String cartest=carId+"_slot";
				
				//List<String> traceKey=cluster.lrange(carId, 2, 3);
				
	
			
				List<String> traceList=cluster.lrange(cartest, 0, -1);

				System.out.println("length: "+traceList.size());
				
				String jsonData=null;
				
				if(traceList.size()>0)
					jsonData=toJson(traceList);
				else
					jsonData="wrong";
					
				System.out.println(jsonData);
				
				pw.write(jsonData);
				pw.flush();
				pw.close();		
			}
		}
	
		private String toinfoJson(List<String> info)
		{
			String[] trace=info.get(1).split(",");
			LonLat point=LonLatTranformImpl.gcj02_To_Bd09(Double.valueOf(trace[4]), Double.valueOf(trace[5]));
			String res="[{\"time\":"+trace[0]+",\"lon\":"+point.getLongtitude()+",\"lat\":"+point.getLatitude()+",\"speed\":"+trace[6]+",\"direction\":"+trace[7]+"}]";
			return res;
		}
	
		private String toJson(List<String> trace)
		{
			 Collections.sort(trace);
			 String res="[";
			 
			 int len=trace.size();

			 for(int cou=0; cou<len; cou++)
			 {
				 String[] list=trace.get(cou).split(",");
				 LonLat point=LonLatTranformImpl.gcj02_To_Bd09(Double.valueOf(list[1]), Double.valueOf(list[2]));
				 res=res+"{\"time\":"+list[0]+",\"lon\":"+point.getLongtitude()+",\"lat\":"+point.getLatitude()+",\"speed\":"+list[3]+"},\n";
				 
			 }
			 res=res.substring(0, res.length()-2);
			 res=res+"]";
//			 testdata=testdata.substring(0,testdata.length()-2);
//			 testdata=testdata+"]";
//			 return testdata;
			 return res;
		}
		
}

	
	