package scripts;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import dataStructure.Bound;
import dataStructure.Car;
import dataStructure.Cluster;
import dataStructure.LonLat;
import util.LonLatFileReader;
import util.LonLatTranformImpl;

public class Clustering {
	
	public static ArrayList<LonLat> pointsList;
	public static List<Cluster> clusters=new ArrayList<Cluster>();
	public static HashMap<LonLat, Double> minDistance=new HashMap<LonLat,Double>();
	public static HashMap<LonLat, Integer> clusterIndex=new HashMap<LonLat,Integer>();
	public static int zoom;
	public static double distance;

	static double R = 6371.0;
	static double PI = 3.14159;
	
	public static void main(String args[])
	{
		try{
			LonLatFileReader.worldWidth=(double)zoom*zoom*256;
			LonLatFileReader reader=new LonLatFileReader("/Users/shushu/desktop/北京市.csv");
			pointsList=reader.readLonLatFromFile();
			
			
			
			clustering();
			
			String json=writeClustersJson();
			BufferedWriter out = null;
			String outPath ="/Users/shushu/desktop/clusters"+zoom+".json";
			File file = new File(outPath);
			file.createNewFile();
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			out.write(json);
			out.close();
			//System.out.println(getDistance(new LonLat(116.30483666666666,39.97834666666667),new LonLat(116.335515,39.985035)));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void initial(String _zoom)
	{
		zoom=Integer.valueOf(_zoom);
		distance=25600/Math.pow(2, zoom);
		LonLatFileReader.worldWidth=(double)zoom*zoom*256;
		System.out.println(LonLatFileReader.worldWidth);
	}
	
	public static void clustering()
	{
		for(LonLat point:pointsList)
		{
			if(minDistance.get(point)==null)
			{
				Cluster cluster=new Cluster(point.createBound(distance));
				clusters.add(cluster);
				minDistance.put(point, Double.valueOf("0"));
				
				getAllPointInClusterBound(cluster);
			}
		}
		
		//加入婧玥的代码
//		while(true){
//			boolean flag = false;
//			
//			ListIterator<Cluster> listIterator1=clusters.listIterator();
//			while(listIterator1.hasNext())
//			{
//				Cluster cluster1=listIterator1.next();
//				
//				ListIterator<Cluster> listIterator2=clusters.listIterator();
//				while(listIterator2.hasNext())
//				{
//					Cluster cluster2=listIterator2.next();
//					
//					if (cluster1.equals(cluster2)) {
//						continue;
//					}
//					else{
//						if(combineCluster(cluster1, cluster2))
//						{
//							flag = true;
//							listIterator2.remove();//remove cluster2
//						}
//					}
//				}
//			}
//////			for (Cluster i:clusters){
//////				for(Cluster j:clusters){
//////					if (i.equals(j)) {
//////						continue;
//////					}
//////					else{
//////						flag = combineCluster(i, j);
//////					}
//////				}
//////			}
////			
//			if (flag == false) break;
//		}
	}
	
	public static void getAllPointInClusterBound(Cluster cluster)
	{
		Bound bound=cluster.getBound();
		for(LonLat point:pointsList)
		{
			if(bound.inBound(point))
			{
				double distance=cluster.getDistaneToClusterCenter(point);
				//double distance=getDistance(cluster.getCenterPoint(), point);
				if(minDistance.get(point)==null)
				{
					minDistance.put(point, distance);
					clusterIndex.put(point, clusters.size());
					cluster.addPoint(point);
				}
				else if(minDistance.get(point)>distance)
				{
					int clusterNo=clusterIndex.get(point);
					Cluster oriCluster=clusters.get(clusterNo);
					oriCluster.removePoint(point);
					
					minDistance.put(point, distance);
					clusterIndex.put(point, clusters.size());
					cluster.addPoint(point);
				}
			}
		}
		cluster.setClusterCenter();
	}
	

	
	//结果输出
	public static String writeClustersJson(){
			String line =  "[";
			
			int length=clusters.size();
			
			for(int i=0;i<length-1;i++){
				//写数据  getlon和getlat就是获得clusters的中心点坐标
				Cluster cluster=clusters.get(i);
				LonLat center=toPosition(cluster.getCenterPoint());
				center=LonLatTranformImpl.gcj02_To_Bd09(center.getLongtitude(), center.getLatitude());
				//LonLat center=cluster.getCenterPoint();
				line += "{\"lon\":" + center.getLongtitude() + ",\"lat\":" + center.getLatitude() + ",\"size\":" + cluster.getSize()+"},\n";

			}
			
			if(clusters.size()!=0)
			{
				Cluster cluster=clusters.get(length-1);
				LonLat center=toPosition(cluster.getCenterPoint());

				center=LonLatTranformImpl.gcj02_To_Bd09(center.getLongtitude(), center.getLatitude());
				//LonLat center=cluster.getCenterPoint();
				line += "{\"lon\":" + center.getLongtitude() + ",\"lat\":" + center.getLatitude() + ",\"size\":" + cluster.getSize()+"}";
			}
			
			
			
			line+="]";
			return line;
	}
	
	public static String writePointsJson(List<LonLat> points, List<String> carIDs)
	{
		String line="[";
		
		int length=pointsList.size();
		
		for(int i=0;i<length-1;i++)
		{
			LonLat point=points.get(i);

			point=LonLatTranformImpl.gcj02_To_Bd09(point.getLongtitude(), point.getLatitude());
			
			line += "{\"id\":" + carIDs.get(i)+",\"lon\":" + point.getLongtitude() + ",\"lat\":" + point.getLatitude() + "},\n";
		}
		
		if(points.size()!=0)
		{
			LonLat point=points.get(length-1);
			point=LonLatTranformImpl.gcj02_To_Bd09(point.getLongtitude(), point.getLatitude());
			//LonLat center=cluster.getCenterPoint();
			line += "{\"id\":" + carIDs.get(length-1)+",\"lon\":" + point.getLongtitude() + ",\"lat\":" + point.getLatitude() + "}";
		}
		
		line+="]";
		
		return line;
	}
	
	public static boolean combineCluster(Cluster cluster1,Cluster cluster2){
		LonLat point1 = cluster1.getCenterPoint();
		LonLat point2 = cluster2.getCenterPoint();
		double d = point1.getDistance(point1, point2);
		if(d<=distance){
			cluster1.addSize(cluster2);
			return true;
		}
		else {
			return false;
		}
	}
	
	public static String writePointsWithInfoJson(List<LonLat> points, List<Car> carInfos)
	{
		String line="[";
		
		int length=points.size();
		
		for(int i=0;i<length-1;i++)
		{
			LonLat point=points.get(i);
			point=toPosition(point);
			Car car=carInfos.get(i);
			point=LonLatTranformImpl.gcj02_To_Bd09(point.getLongtitude(), point.getLatitude());
			line += "{\"id\":\"" + car.getCarID()+"\",\"lon\":" + point.getLongtitude() + ",\"lat\":" + point.getLatitude() + ",\"direction\":" + car.getDirection() +",\"time\":" + car.getTime() +",\"speed\":" + car.getSpeed() +"},\n";
		}
		
		if(points.size()!=0)
		{
			LonLat point=points.get(length-1);
			point=toPosition(point);
			Car car=carInfos.get(length-1);
			point=LonLatTranformImpl.gcj02_To_Bd09(point.getLongtitude(), point.getLatitude());
			line+="{\"id\":\"" + car.getCarID()+"\",\"lon\":" + point.getLongtitude() + ",\"lat\":" + point.getLatitude() + ",\"direction\":" + car.getDirection() +",\"time\":" + car.getTime() +",\"speed\":" + car.getSpeed() +"}";
		}
		
		line+="]";
		
		return line;
	}
	
	public static LonLat toPosition(LonLat point)
	{
		double x=point.getLongtitude()/LonLatFileReader.worldWidth;
		double y=point.getLatitude()/LonLatFileReader.worldWidth;
		
		double lon=(x-0.5)*360;
		double t=4*Math.PI*(0.5-y);
		double lat=Math.toDegrees(Math.asin((Math.pow(Math.E, t)-1)/(Math.pow(Math.E, t)+1)));
		LonLat position=new LonLat(lon,lat);
		return position;
	}
	
	
	
//	public static double getDistance(LonLat point1,LonLat point2)
//	{
//		double tmp = Math.cos(point1.getLatitude())*Math.cos(point2.getLatitude())*Math.cos(point1.getLongtitude()-point2.getLongtitude())+(Math.sin(point1.getLatitude())*Math.sin(point2.getLatitude()));
//		
//		double d = R * Math.acos(tmp) * PI/180;
//		return d;
//	}
}
