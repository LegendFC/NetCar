package dataStructure;

import javax.swing.BoundedRangeModel;

public class LonLat {
	private double longtitude;
	private double latitude;
	private int weight;
	
	public LonLat(String line)
	{
		String[] segments=line.split(",");
		this.longtitude=Double.valueOf(segments[0]);
		this.latitude=Double.valueOf(segments[1]);
	}
	
	public LonLat(double longitude,double latitude)
	{
		this.longtitude=longitude;
		this.latitude=latitude;
	}
	
	public LonLat(double longitude,double latitude,int weight)
	{
		this.longtitude=longitude;
		this.latitude=latitude;
		this.weight = weight;
	}
	
	//计算点的外包正方形
	public Bound createBound(double distance)
	{
		LonLat center=this;
		double x1,x2,y1,y2;
		x1 = center.longtitude - distance;
		y1 = center.latitude + distance;
		x2 = center.longtitude + distance;
		y2 = center.latitude - distance;
		LonLat leftUp = new LonLat(x1,y1);
		LonLat rightDown = new LonLat(x2,y2);
		return new Bound(center, leftUp, rightDown);
	}
	
	public double getLongtitude(){
		return this.longtitude;
	}
	
	public double getLatitude() {
		return this.latitude;
	}
	
	public int getWeight() {
		return this.weight;		
	}
	
	 public double getDistance(LonLat point1,LonLat point2) 
	 {
	   double tmp = Math.pow(point1.getLongtitude()-point2.getLongtitude(), 2) + Math.pow(point1.getLatitude()-point2.getLatitude(), 2);
	   double d = Math.sqrt(tmp);
	   return d;
	}
}
