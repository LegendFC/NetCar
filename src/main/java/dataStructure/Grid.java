package dataStructure;

import java.io.ObjectInputStream.GetField;

import util.LonLatFileReader;

public class Grid {
	public String GeographicCode;
	public int weight;
	
	public Grid(String GeographicCode, int weight)
	{
		this.GeographicCode=GeographicCode;
		this.weight=weight;
	}
	
	public LonLat gridToLonLatGrid()
	{
		String ab=GeographicCode.substring(0, 2);
		String cd=GeographicCode.substring(2,4);
		String e=GeographicCode.substring(4,5);
		String f=GeographicCode.substring(5,6);
		String g=GeographicCode.substring(6,7);
		String h=GeographicCode.substring(7,8);
		
		double lat=Double.valueOf(ab)*2/3+Double.valueOf(e)/12+Double.valueOf(g)/120+0.00625;
		double lon=Double.valueOf(cd)+Double.valueOf(f)/12+Double.valueOf(h)/120+60+0.00416666667;
		LonLat currentPoint=LonLatFileReader.toPoint(lon, lat);
		LonLat point=new LonLat(currentPoint.getLongtitude(), currentPoint.getLatitude(), weight);
		return point;
	}
}
