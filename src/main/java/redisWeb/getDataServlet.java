package redisWeb;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import com.sun.imageio.plugins.gif.GIFImageReaderSpi;
//import com.sun.istack.internal.Pool;

import java.util.concurrent.*;

import dataStructure.GeographicGrid;
import dataStructure.Grid;
import dataStructure.LonLat;
import dataStructure.Car;
import dataStructure.Cluster;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.Pipeline;
import scripts.Clustering;
import util.FileUtil;
import util.LonLatFileReader;
import util.LonLatTranformImpl;


public class getDataServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	JedisCluster cluster=null;
	Set<HostAndPort> clusterNodes=new HashSet<HostAndPort>();
//	private static HashMap<String, LonLat> grids=new HashMap<String, LonLat>();
	
	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();
		//ip地址
        //世纪互联：192.168.1.81
        //盈都：192.168.1.34
//        clusterNodes.add(new HostAndPort("10.2.15.107",7200));	
//        clusterNodes.add(new HostAndPort("10.2.15.107",7201));
//        clusterNodes.add(new HostAndPort("10.2.15.107",7202));
//        clusterNodes.add(new HostAndPort("10.2.15.107",7203));
//        clusterNodes.add(new HostAndPort("10.2.15.107",7204));
//        clusterNodes.add(new HostAndPort("10.2.15.107",7205));
//		cluster=new JedisCluster(clusterNodes);
		
		//readAllGrids("/Users/shushu/Documents/NRDC/网络约租车/网约车实验结果/有路网格");
	}
	
	@SuppressWarnings("resource")
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//String nowStr = sdf.format(new Date());		
		//以流的方式将结果响应到AJAX异步对象中
		
		String minLongitude=(String)req.getParameter("swlng");
		String maxLongitude=(String)req.getParameter("nelng");
		String minLatitude=(String)req.getParameter("swlat");
		String maxLatitude=(String)req.getParameter("nelat");
		String zoom=(String)req.getParameter("zoom");
		
		//113.93342  114.132311  22.507755  22.606799
//		minLongitude="113.93342";
//		maxLongitude="114.132311";
//		minLatitude="22.507755";
//		maxLatitude="22.606799";
//		zoom="16";
		
		Clustering.pointsList=new ArrayList<LonLat>();
		Clustering.clusters=new ArrayList<Cluster>();
		if(zoom!=null)
		{
			Date date;
			SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			
			
			resp.setContentType("text/html;charset=UTF-8");
			PrintWriter pw = resp.getWriter();
			resp.reset();
			
			double minLongtitude_double=Double.valueOf(minLongitude);
			double maxLongtitude_double=Double.valueOf(maxLongitude);
			double minLatitude_double=Double.valueOf(minLatitude);
			double maxLatitude_double=Double.valueOf(maxLatitude);		
			
			LonLat swPoint=LonLatTranformImpl.bd09_To_Gcj02(minLongtitude_double, minLatitude_double);
			LonLat nePoint=LonLatTranformImpl.bd09_To_Gcj02(maxLongtitude_double, maxLatitude_double);
			
			ArrayList<String> gridNos=getGridsIDInRangeBefore(minLongitude, minLatitude, maxLongitude, maxLatitude);
			//ArrayList<String> gridNos=getGridsIDInRange(swPoint, nePoint);
			ArrayList<Grid> grids=new ArrayList<Grid>();
			ArrayList<Grid> gridsToSearch=new ArrayList<Grid>();//切分出来的六位网格，待查询
			ArrayList<Grid> gridsWithCar=new ArrayList<Grid>();//存储最终查询出的所有八位网格（去除了没车的六位网格）
			
			System.out.println("grids:"+gridNos.size());
			
			//grids=getGridsIDInRangeBefore(minLongitude, minLatitude, maxLongitude, maxLatitude);
			
			//System.out.println(grids.size());
			
			Clustering.pointsList=new ArrayList<LonLat>();
			Clustering.initial(zoom);
			System.out.println("zoom"+zoom);
			
			if(Integer.valueOf(zoom)>15)//18级需要知道车的车牌号、经纬度、时间、方向、速度
			{
				List<LonLat> pointsList=new ArrayList<LonLat>();
				List<Car> carInfoList=new ArrayList<Car>();
				
				for(String gridID:gridNos)
				{
					List<String> carID=cluster.lrange(gridID,0,-1);
					
					int threadPoolSize=10;
					ExecutorService pool=Executors.newFixedThreadPool(threadPoolSize);
					try{
						List<Future> futures=new ArrayList<Future>();
						for(String car:carID){
							CarInfoCallable carInfoCallable=new CarInfoCallable(car);
							Future future=pool.submit(carInfoCallable);
							futures.add(future);
						}	
						
						for(Future future:futures)
						{
							Car car=(Car)future.get();
							carInfoList.add(car);
							pointsList.add(car.getPosition());
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				
				pw.write(Clustering.writePointsWithInfoJson(pointsList,carInfoList));
				System.out.println(Clustering.writePointsWithInfoJson(pointsList,carInfoList));
			}
			else//15级以下需要聚合
			{	
				System.out.println("startasd");
				long timeStart=System.currentTimeMillis();
				int threadPoolSize=10;
				ExecutorService pool=Executors.newFixedThreadPool(threadPoolSize);
				try 
				{				
					//第一步：查询六位网格及分散的八位网格内的车辆数
					List<Future> futures=new ArrayList<Future>();
					for(String gridID:gridNos)
					{
						GridCallable gridCallable=new GridCallable(gridID);
						Future future=pool.submit(gridCallable);
						futures.add(future);
					}				
					
					long timeMiddle=System.currentTimeMillis();
					
					System.out.print("timemiddle"+(timeMiddle-timeStart));
					
					for(Future future:futures)
					{
						grids.add((Grid)future.get());
					}
					pool.shutdown();
					
					//第二步：去除六位网格中没有车的，然后将有车的再次拆分为8位网格进行查询，同时只将有车的八位网格传进来，查询八位网格内的车辆数量
					futures=new ArrayList<Future>();
					gridWithCarFilter(grids,gridsWithCar,gridsToSearch);
					pool=Executors.newFixedThreadPool(threadPoolSize);
					gridsToSearch.addAll(gridsWithCar);
					int length=gridsToSearch.size();
					for(int i=0;i<length;i++)
					{
						Grid grid=gridsToSearch.get(i);
						CarCallable carCallable=new CarCallable(grid.GeographicCode);
						Future future=pool.submit(carCallable);
						futures.add(future);
					}
					System.out.println("  ");
					
					for(Future future:futures)
					{
						Clustering.pointsList.addAll((List<LonLat>)future.get());
					}
									

					long timeEnd=System.currentTimeMillis();
					
					System.out.print("time"+(timeEnd-timeStart));
					
					Clustering.clustering();
					
					pw.write(Clustering.writeClustersJson());
					
				} 
				catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				finally {
					pool.shutdown();
				}	
			}
		
			pw.flush();
			pw.close();		
		}
	}
	
	protected ArrayList<String> getGridsIDInRange(LonLat swPoint, LonLat nePoint)
	{
		ArrayList<String> gridIDs=new ArrayList<String>();
		
		String southWestGridID=(new GeographicGrid(swPoint)).geographicCode;
		String northEastGridID=(new GeographicGrid(nePoint)).geographicCode;
		
		//gridID(abcdefgh) can be divided into 6 parts, ab&e&g are related with latitude while cd&f&h are related with longitude
		int southWestGridID_ab=Integer.valueOf(southWestGridID.substring(0, 2));
		int southWestGridID_e=Integer.valueOf(southWestGridID.substring(4, 5));
		int southWestGridID_g=Integer.valueOf(southWestGridID.substring(6, 7));	
		int southWestGridID_cd=Integer.valueOf(southWestGridID.substring(2, 4));
		int southWestGridID_f=Integer.valueOf(southWestGridID.substring(5, 6));
		int southWestGridID_h=Integer.valueOf(southWestGridID.substring(7, 8));
		
		int northEastGridID_ab=Integer.valueOf(northEastGridID.substring(0, 2));
		int northEastGridID_e=Integer.valueOf(northEastGridID.substring(4, 5));
		int northEastGridID_g=Integer.valueOf(northEastGridID.substring(6, 7));	
		int northEastGridID_cd=Integer.valueOf(northEastGridID.substring(2, 4));
		int northEastGridID_f=Integer.valueOf(northEastGridID.substring(5, 6));
		int northEastGridID_h=Integer.valueOf(northEastGridID.substring(7, 8));
		
		List<String> level3LonRelated=new ArrayList<String>();
		List<String> level3LatRelated=new ArrayList<String>();
		List<String> level2LonRelated=new ArrayList<String>();
		List<String> level2LatRelated=new ArrayList<String>();
		
		if(southWestGridID.equals(northEastGridID))
		{
			gridIDs.add(southWestGridID);
		}
		else
		{
			//首先计算完整的2级网格，即范围内编号不以边角前六位开头的2级网格
			//获得四个角开头六位，分别获取经纬度相关的部分
			String northEastLevel2LonRelated=""+northEastGridID_cd+northEastGridID_f;
			String northEastLevel2LatRelated=""+northEastGridID_ab+northEastGridID_e;
			String southWestLevel2LonRelated=""+southWestGridID_cd+southWestGridID_f;
			String southWestLevel2LatRelated=""+southWestGridID_ab+southWestGridID_e;
			
			String level2LonRelatedCurrent=southWestLevel2LonRelated;
			if(!southWestLevel2LonRelated.equals(northEastLevel2LonRelated))
			{
				String level2LonRelatedMax=level2GridRightSubstract(northEastLevel2LonRelated);
				while(!level2LonRelatedCurrent.equals(level2LonRelatedMax))
				{
					level2LonRelatedCurrent=level2GridRightPlus(level2LonRelatedCurrent);
					level2LonRelated.add(level2LonRelatedCurrent);
				}
			}
			
			String level2LatRelatedCurrent=southWestLevel2LatRelated;
			if(!southWestLevel2LatRelated.equals(northEastLevel2LatRelated))
			{
				String level2LatRelatedMax=level2GridUpSubstract(northEastLevel2LatRelated);
				while(!level2LatRelatedCurrent.equals(level2LatRelatedMax))
				{
					level2LatRelatedCurrent=level2GridUpPlus(level2LatRelatedCurrent);
					level2LatRelated.add(level2LatRelatedCurrent);
				}
			}

			for(String lonNo:level2LonRelated)
			{
				for(String latNo:level2LatRelated)
				{
					String ab=latNo.substring(0, 2);
					String e=latNo.substring(2,3);
					String cd=lonNo.substring(0, 2);
					String f=lonNo.substring(2,3);
					
					gridIDs.add(ab+cd+e+f);
				}
			}
			
			//计算不完全的网格
			List<String> restLatRelated=new ArrayList<String>();
			List<String> restLonRelated=new ArrayList<String>();
			
			String restLatRelatedPrefix=""+northEastGridID_ab+northEastGridID_e;
			for(int i=0;i<=northEastGridID_g;i++)
			{
				restLatRelated.add(restLatRelatedPrefix+i);
			}
			restLatRelatedPrefix=""+southWestGridID_ab+southWestGridID_e;
			for(int i=southWestGridID_g;i<=9;i++)
			{
				restLatRelated.add(restLatRelatedPrefix+i);
			}
			
			String restLonRelatedPrefix=""+southWestGridID_cd+southWestGridID_f;
			for(int i=southWestGridID_h;i<=9;i++)
			{
				restLonRelated.add(restLonRelatedPrefix+i);
			}
			restLonRelatedPrefix=""+northEastGridID_cd+northEastGridID_f;
			for(int i=0;i<=northEastGridID_h;i++)
			{
				restLonRelated.add(restLonRelatedPrefix+i);
			}
			
			//获得allLonRelated和allLatRelated即level3LonRelated和level3LatRelated
			level3LonRelated.add(""+southWestGridID_cd+southWestGridID_f+southWestGridID_h);
			level3LatRelated.add(""+southWestGridID_ab+southWestGridID_e+southWestGridID_g);
			int latRelatedDifference=difference(northEastGridID_ab, northEastGridID_e, northEastGridID_g, southWestGridID_ab, southWestGridID_e, southWestGridID_g);
			
			String currentlatRelated=""+southWestGridID_ab+southWestGridID_e+southWestGridID_g;
			for(int i=0;i<latRelatedDifference;i++)
			{
				int ab=Integer.valueOf(currentlatRelated.substring(0, 2));
				int e=Integer.valueOf(currentlatRelated.substring(2, 3));
				int g=Integer.valueOf(currentlatRelated.substring(3, 4));
				currentlatRelated=upPlus(ab, e, g);
				level3LatRelated.add(currentlatRelated);
			}
			
			int lonRelatedDifference=difference(northEastGridID_cd, northEastGridID_f, northEastGridID_h, southWestGridID_cd, southWestGridID_f, southWestGridID_h);
			String currentlonRelated=""+southWestGridID_cd+southWestGridID_f+southWestGridID_h;
			for(int i=0;i<lonRelatedDifference;i++)
			{
				int cd=Integer.valueOf(currentlonRelated.substring(0, 2));
				int f=Integer.valueOf(currentlonRelated.substring(2, 3));
				int h=Integer.valueOf(currentlonRelated.substring(3, 4));
				currentlonRelated=rightPlus(cd, f, h);
				level3LonRelated.add(currentlonRelated);
			}
			
			HashSet<String> level3GridsID=new HashSet<String>();
			ConcurrentHashMap<String, Integer> level3GridsGroupByincompletableLevel2Grids=new ConcurrentHashMap<String, Integer>();
			
			//restLonRElated叉乘allLatRelated
			for(String restLon:restLonRelated)
			{
				for(String lat:level3LatRelated)
				{
					String ab=lat.substring(0, 2);
					String e=lat.substring(2,3);
					String g=lat.substring(3,4);
					String cd=restLon.substring(0, 2);
					String f=restLon.substring(2,3);
					String h=restLon.substring(3,4);
					
					level3GridsID.add(ab+cd+e+f+g+h);
				}
			}
			
			//restLatRelated叉乘allLonRelated
			for(String restLat:restLatRelated)
			{
				for(String lon:level3LonRelated)
				{
					String ab=restLat.substring(0, 2);
					String e=restLat.substring(2,3);
					String g=restLat.substring(3,4);
					String cd=lon.substring(0, 2);
					String f=lon.substring(2,3);
					String h=lon.substring(3,4);
					
					level3GridsID.add(ab+cd+e+f+g+h);
				}
			}
			
			//统计每一个不完整二级网格中的三级网格数
			for(Iterator<String> iterator=level3GridsID.iterator();iterator.hasNext();){
				String gridNo=iterator.next();
				String level2GridNo=gridNo.substring(0,6);
				
				Integer count;
				if((count=level3GridsGroupByincompletableLevel2Grids.get(level2GridNo))==null)
				{
					level3GridsGroupByincompletableLevel2Grids.put(level2GridNo, 1);
				}
				else
				{
					count++;
					level3GridsGroupByincompletableLevel2Grids.put(level2GridNo, count);
				}				
			}
			
			//将不完整二级网格中三级网格数大于等于70的取出放到GridIDs中
			synchronized(level3GridsGroupByincompletableLevel2Grids)
			{
				Iterator iter = level3GridsGroupByincompletableLevel2Grids.entrySet().iterator();
				List<String> gridIDToRemove=new ArrayList<String>();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					String gridID = (String)entry.getKey();
					int gridCount= (Integer)entry.getValue();

					if(gridCount>=70)
					{
						gridIDs.add(gridID);
						level3GridsGroupByincompletableLevel2Grids.remove(gridID);
					}
				}
			}			

			//将不完整二级网格中三级网格数小于70的所有三级网格放到gridIDs中
			for(Iterator<String> iterator=level3GridsID.iterator();iterator.hasNext();){
				String gridNo=iterator.next();
				String level2GridNo=gridNo.substring(0,6);
				
				if(level3GridsGroupByincompletableLevel2Grids.get(level2GridNo)!=null)
				{
					gridIDs.add(gridNo);
				}
			}
		}
		
		return gridIDs;
		
	}
	
	protected ArrayList<String> getGridsIDInRangeBefore(String minLongitude,String minLatitude,String maxLongitude, String maxLatitude)
	{
		ArrayList<String> gridIDs=new ArrayList<String>();
		
		String southWestGridID=(new GeographicGrid(new LonLat(minLongitude+","+minLatitude))).geographicCode;
		String northEastGridID=(new GeographicGrid(new LonLat(maxLongitude+","+maxLatitude))).geographicCode;
		
		//gridID(abcdefgh) can be divided into 6 parts, ab&e&g are related with latitude while cd&f&h are related with longitude
		int southWestGridID_ab=Integer.valueOf(southWestGridID.substring(0, 2));
		int southWestGridID_e=Integer.valueOf(southWestGridID.substring(4, 5));
		int southWestGridID_g=Integer.valueOf(southWestGridID.substring(6, 7));	
		int southWestGridID_cd=Integer.valueOf(southWestGridID.substring(2, 4));
		int southWestGridID_f=Integer.valueOf(southWestGridID.substring(5, 6));
		int southWestGridID_h=Integer.valueOf(southWestGridID.substring(7, 8));
		
		int northEastGridID_ab=Integer.valueOf(northEastGridID.substring(0, 2));
		int northEastGridID_e=Integer.valueOf(northEastGridID.substring(4, 5));
		int northEastGridID_g=Integer.valueOf(northEastGridID.substring(6, 7));	
		int northEastGridID_cd=Integer.valueOf(northEastGridID.substring(2, 4));
		int northEastGridID_f=Integer.valueOf(northEastGridID.substring(5, 6));
		int northEastGridID_h=Integer.valueOf(northEastGridID.substring(7, 8));
		
		List<String> lonRelated=new ArrayList<String>();
		List<String> latRelated=new ArrayList<String>();
		
		if(southWestGridID.equals(northEastGridID))
		{
			gridIDs.add(southWestGridID);
		}
		else
		{
			lonRelated.add(""+southWestGridID_cd+southWestGridID_f+southWestGridID_h);
			latRelated.add(""+southWestGridID_ab+southWestGridID_e+southWestGridID_g);
			int latRelatedDifference=difference(northEastGridID_ab, northEastGridID_e, northEastGridID_g, southWestGridID_ab, southWestGridID_e, southWestGridID_g);
			
			String currentlatRelated=""+southWestGridID_ab+southWestGridID_e+southWestGridID_g;
			for(int i=0;i<latRelatedDifference;i++)
			{
				int ab=Integer.valueOf(currentlatRelated.substring(0, 2));
				int e=Integer.valueOf(currentlatRelated.substring(2, 3));
				int g=Integer.valueOf(currentlatRelated.substring(3, 4));
				currentlatRelated=upPlus(ab, e, g);
				latRelated.add(currentlatRelated);
			}
			
			int lonRelatedDifference=difference(northEastGridID_cd, northEastGridID_f, northEastGridID_h, southWestGridID_cd, southWestGridID_f, southWestGridID_h);
			String currentlonRelated=""+southWestGridID_cd+southWestGridID_f+southWestGridID_h;
			for(int i=0;i<lonRelatedDifference;i++)
			{
				int cd=Integer.valueOf(currentlonRelated.substring(0, 2));
				int f=Integer.valueOf(currentlonRelated.substring(2, 3));
				int h=Integer.valueOf(currentlonRelated.substring(3, 4));
				currentlonRelated=rightPlus(cd, f, h);
				lonRelated.add(currentlonRelated);
			}
			
			for(String lonNo:lonRelated)
			{
				for(String latNo:latRelated)
				{
					String ab=latNo.substring(0, 2);
					String e=latNo.substring(2,3);
					String g=latNo.substring(3,4);
					String cd=lonNo.substring(0, 2);
					String f=lonNo.substring(2,3);
					String h=lonNo.substring(3,4);
					
					gridIDs.add(ab+cd+e+f+g+h);
				}
			}
		}
		
		return gridIDs;
		
	}
	
	protected String level2GridUpPlus(String GridLatRelated){
		int level1LatRelated=Integer.valueOf(GridLatRelated.substring(0,2));
		int level2LatRelated=Integer.valueOf(GridLatRelated.substring(2,3));
		
		if(level2LatRelated==7)
		{
			level2LatRelated=0;
			level1LatRelated++;
		}
		else
		{
			level2LatRelated++;
		}
		return ""+level1LatRelated+level2LatRelated;
	}
	
	protected String level2GridUpSubstract(String GridLatRelated){
		int level1LatRelated=Integer.valueOf(GridLatRelated.substring(0,2));
		int level2LatRelated=Integer.valueOf(GridLatRelated.substring(2,3));
		
		if(level2LatRelated==0)
		{
			level2LatRelated=7;
			level1LatRelated--;
		}
		else
		{
			level2LatRelated--;
		}
		return ""+level1LatRelated+level2LatRelated;
	}
	
	protected String level2GridRightSubstract(String GridLonRelated){
		int level1LonRelated=Integer.valueOf(GridLonRelated.substring(0,2));
		int level2LonRelated=Integer.valueOf(GridLonRelated.substring(2,3));
		
		if(level2LonRelated==0)
		{
			level2LonRelated=7;
			level1LonRelated--;
		}
		else
		{
			level2LonRelated--;
		}
		
		return ""+level1LonRelated+level2LonRelated;
	}
	
	protected String level2GridRightPlus(String GridLonRelated){
		int level1LonRelated=Integer.valueOf(GridLonRelated.substring(0,2));
		int level2LonRelated=Integer.valueOf(GridLonRelated.substring(2,3));
		
		if(level2LonRelated==7)
		{
			level2LonRelated=0;
			level1LonRelated++;
		}
		else
		{
			level2LonRelated++;
		}
		
		return ""+level1LonRelated+level2LonRelated;
	}
	
	protected int difference(int ab,int e, int g, int ab2, int e2, int g2)
	{
		int g_ans;
		int e_ans;
		int ab_ans;
				
		if(g>=g2)
		{
			g_ans=g-g2;
			if(e>=e2)
			{
				e_ans=e-e2;
				ab_ans=ab-ab2;
			}
			else
			{
				e_ans=e-e2+8;
				ab_ans=ab-ab2-1;
			}
		}
		else
		{
			g_ans=g-g2+10;
			if(e>e2)
			{
				e_ans=e-e2-1;
				ab_ans=ab-ab2;
			}
			else
			{
				e_ans=e-e2+7;
				ab_ans=ab-ab2-1;
			}
		}
		return ab_ans*100+e_ans*8+g_ans;
	}
	
	protected String upPlus(int ab, int e, int g)
	{
		if(g==9)
		{
			g=0;
			if(e==7)
			{
				e=0;
				ab++;
			}
			else
			{
				e++;
			}
		}
		else
		{
			g++;
		}
		
		return ""+ab+e+g;
	}
	
	protected String rightPlus(int cd, int f, int h)
	{
		if(h==9)
		{
			h=0;
			if(f==7)
			{
				f=0;
				cd++;
			}
			else
			{
				f++;
			}
		}
		else
		{
			h++;
		}
		
		return ""+cd+f+h;
	}
	
//	protected void readAllGrids(String folder)
//	{
//		try{
//			List<String> grid6_file_list=FileUtil.getAbsolutePathFromDIR(folder);
//			for(String path:grid6_file_list)
//			{
//				if(!path.contains("DS_Store")&&!path.contains("err"))
//				{
//					BufferedReader midFile=new BufferedReader(new FileReader(path));
//					String line;
//					while((line=midFile.readLine())!=null)
//					{
//						String[] attributes=line.split(",");
//						String gridNo=attributes[0];
//						String lonMax=attributes[3];
//						String latMax=attributes[4];
//						String lonMin=attributes[5];
//						String latMin=attributes[6];
//						
//						grids.put(gridNo, new LonLat((Double.valueOf(lonMax)+Double.valueOf(lonMin))/2,(Double.valueOf(latMax)+Double.valueOf(latMin))/2));
//					}
//					midFile.close();
//				}
//			}
//		}
//		catch(Exception e){
//			e.printStackTrace();
//		}
//	}
	
	protected void gridWithCarFilter(ArrayList<Grid> grids,ArrayList<Grid> gridsWithCar, ArrayList<Grid> gridsToSearch) 
	{
		int length=grids.size();
		for(int i=0;i<length;i++)
		{
			Grid grid=grids.get(i);
			if(grid.weight!=0)
			{
				if(grid.GeographicCode.length()==6)
				{
					//六位网格需要切分为8位网格后加入列表中
					for(int j=0;j<10;j++)
					{
						for(int k=0;k<10;k++)
						{
							gridsToSearch.add(new Grid(grid.GeographicCode+j+k, -1));
						}
					}					
				}
				else
				{
					//八位网格直接加入列表中
					gridsWithCar.add(new Grid(grid.GeographicCode, grid.weight));
				}
			}
		}
	}
	
	class GridCallable implements Callable{
		private String gridID;
		public GridCallable(String gridID) {
			// TODO Auto-generated constructor stub
			this.gridID=gridID;
		}
		
		public Grid call() throws Exception{
			List<LonLat> carIDsInGrid=new ArrayList<LonLat>();
			double gridStart = System.currentTimeMillis();
		
			List<String> carID=cluster.lrange(gridID,0,-1);
			
			double carForstart = System.currentTimeMillis();
			double gridReadTime=carForstart-gridStart;
			double readACarTime=0;
			//System.out.println(Thread.currentThread().getName() + "gridRead:"+gridReadTime);
//			for(String car:carID){
//				long readACarStart=System.currentTimeMillis();
//				List<String> info=cluster.lrange(car,4,5);
//				long readACarEnd=System.currentTimeMillis();
//				readACarTime=readACarEnd-readACarStart;
//				//System.out.println(Thread.currentThread().getName()+"carRead:"+readACarTime);
//				carIDsInGrid.add(LonLatFileReader.toPoint(Double.valueOf(info.get(0)), Double.valueOf(info.get(1))));
//			}
			double carForEnd=System.currentTimeMillis();
			double carForTime=carForEnd-carForstart;
			//System.out.println(Thread.currentThread().getName() + "carFor:"+carForTime);
			
			double allTime=carForEnd-gridStart;
			//System.out.println(Thread.currentThread().getName()+"allTime"+allTime);
			
			//System.out.println(Thread.currentThread().getName()+"gridReadPercent"+((gridReadTime)/allTime));
			//System.out.println(Thread.currentThread().getName()+"carReadPercent"+((readACarTime)/allTime));
			//System.out.println(Thread.currentThread().getName()+"carReadPercent"+((carForTime)/allTime));
			return new Grid(gridID, carID.size());
		}
	}
	
	class CarCallable implements Callable{
		private String gridID;
		public CarCallable(String gridID) {
			// TODO Auto-generated constructor stub
			this.gridID=gridID;
		}
		
		public List<LonLat> call() throws Exception{
			List<LonLat> carIDsInGrid=new ArrayList<LonLat>();
			double gridStart = System.currentTimeMillis();
		
			List<String> carID=cluster.lrange(gridID,0,-1);
			
			double carForstart = System.currentTimeMillis();
			double gridReadTime=carForstart-gridStart;
			double readACarTime=0;
			//System.out.println(Thread.currentThread().getName() + "gridRead:"+gridReadTime);
			for(String car:carID){
				long readACarStart=System.currentTimeMillis();
				List<String> line=cluster.lrange(car,1,1);

				if(line.size()==0)
				{
					//System.out.println("hhh");
				}
				else
				{
					String[] info=line.get(0).split(",");
					
					
					if(info.length==10)
					{
						long readACarEnd=System.currentTimeMillis();
						readACarTime=readACarEnd-readACarStart;
						//System.out.println(Thread.currentThread().getName()+"carRead:"+readACarTime);
						carIDsInGrid.add(LonLatFileReader.toPoint(Double.valueOf(info[4]), Double.valueOf(info[5])));
					}
					else if(info.length==12)
					{
						long readACarEnd=System.currentTimeMillis();
						readACarTime=readACarEnd-readACarStart;
						//System.out.println(Thread.currentThread().getName()+"carRead:"+readACarTime);
						carIDsInGrid.add(LonLatFileReader.toPoint(Double.valueOf(info[6]), Double.valueOf(info[7])));
					}
				}
					double carForEnd=System.currentTimeMillis();
					double carForTime=carForEnd-carForstart;
				//System.out.println(Thread.currentThread().getName() + "carFor:"+carForTime);
				
					double allTime=carForEnd-gridStart;
				}
				
				
			//System.out.println(Thread.currentThread().getName()+"allTime"+allTime);
			
			//System.out.println(Thread.currentThread().getName()+"gridReadPercent"+((gridReadTime)/allTime));
			//System.out.println(Thread.currentThread().getName()+"carReadPercent"+((readACarTime)/allTime));
			//System.out.println(Thread.currentThread().getName()+"carReadPercent"+((carForTime)/allTime));
			return carIDsInGrid;
		}
	}

	
	class CarInfoCallable implements Callable{
		private String carID;
		public CarInfoCallable(String carID){
			this.carID=carID;
		}
		
		public Car call() throws Exception{
			List<String> info=cluster.lrange(carID,1,1);
			String[] infos=info.get(0).split(",");
			System.out.println(info+"www");
			return new Car(carID, infos[0], infos[7], infos[6], LonLatFileReader.toPoint(Double.valueOf(infos[4]), Double.valueOf(infos[5])));
		}
	}
}