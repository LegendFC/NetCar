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
		
		//���ļ�inputPath����γ�ȣ�����ÿ����γ�Ⱦ���toPoint������������ɵ�LonLat�������pointsList��
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
	
	//����γ��ת���ɶ�άƽ������
	public static LonLat toPoint(double longitude, double latitude)
	{  
       double x = longitude / 360 +0.5;  
       double siny = Math.sin(Math.toRadians(latitude));  
       double y = 0.5 * Math.log((1 + siny) / (1 - siny)) / -(2 * Math.PI) + .5;  
       return new LonLat(x * worldWidth, y * worldWidth);  
	}  
	
	//����γ��ת���ɶ�άƽ������
	public LonLat toPoint(double longitude, double latitude,int weight )
	{  
       double x = longitude / 360 +0.5;  
       double siny = Math.sin(Math.toRadians(latitude));  
       double y = 0.5 * Math.log((1 + siny) / (1 - siny)) / -(2 * Math.PI) + .5;  
       return new LonLat(x * worldWidth, y * worldWidth,weight);  
	}  
}
