package util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import dataStructure.LonLat;


public class LonLatFileReader {
	
	public static double worldWidth;
	public String inputPath;
	
	public LonLatFileReader(String inputPath)
	{
		this.inputPath=inputPath;
	}
	
	public ArrayList<LonLat> readLonLatFromFile() throws IOException
	{
		ArrayList<LonLat> pointsList=new ArrayList<LonLat>();
		
		//从文件inputPath读经纬度，并把每个经纬度经过toPoint函数计算后生成的LonLat对象放入pointsList中
		BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(inputPath)));
		String data = null;
		while((data = br.readLine())!=null){
			double lon,lat;
			String[] seg = data.split(",");
			lon = Double.valueOf(seg[7]);
			lat = Double.valueOf(seg[8]);
			int weight=(int)(double)(Double.valueOf(seg[9]));
			pointsList.add(toPoint(lon, lat, weight));
		}
		return pointsList;
		
	}
	
	//将经纬度转换成二维平面坐标
	public static LonLat toPoint(double longitude, double latitude)
	{  
       double x = longitude / 360 +0.5;  
       double siny = Math.sin(Math.toRadians(latitude));  
       double y = 0.5 * Math.log((1 + siny) / (1 - siny)) / -(2 * Math.PI) + .5;  
       return new LonLat(x * worldWidth, y * worldWidth);  
	}  
	
	//将经纬度转换成二维平面坐标
	public LonLat toPoint(double longitude, double latitude,int weight )
	{  
       double x = longitude / 360 +0.5;  
       double siny = Math.sin(Math.toRadians(latitude));  
       double y = 0.5 * Math.log((1 + siny) / (1 - siny)) / -(2 * Math.PI) + .5;  
       return new LonLat(x * worldWidth, y * worldWidth,weight);  
	}  
}
