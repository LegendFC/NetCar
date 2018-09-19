package dataStructure;

import java.util.ArrayList;
import java.util.List;

public class Cluster{
    private Bound bound;
    private List<LonLat> pointsInBound;
    private int size;//聚类中车辆的个数
    private LonLat center;
    
    public Cluster(Bound bound)
    {
    	this.bound=bound;
    	pointsInBound=new ArrayList<LonLat>();
    	size=1;
    	center=bound.getCenter();
    }
    
    public void addSize(Cluster cluster) 
    {
 	   size = this.getSize()+cluster.getSize();
    }
    
    public int getSize()
    {
 		 return size;
    }
   
   public void addPoint(LonLat point)
   {
	   pointsInBound.add(point);
	   size++;
   }
   
   public void removePoint(LonLat point)
   {
	   pointsInBound.remove(point);
   }
   
   public double getDistaneToClusterCenter(LonLat point)
   {
	   double sum;
	   LonLat center=this.getCenterPoint();
	   double tmp = Math.pow(center.getLongtitude()-point.getLongtitude(), 2) + Math.pow(center.getLatitude()-point.getLatitude(), 2);
	   double d = Math.sqrt(tmp);
	   //sum = d*0.8+0.2*this.getSize();
	   return d;
   }
   
   public void setClusterCenter()
   {
	   //the point with max weight is the center of cluster
	   int maxWeight=0;
	   for(LonLat point:pointsInBound)
	   {
		   int weight=point.getWeight();
		   if(weight>maxWeight)
		   {
			   center=point;
			   maxWeight=weight;
		   }
	   }
//	   center=bound.getCenter();
   }
   
   public LonLat getCenterPoint()
   {
	   return bound.getCenter();
   }
   
   public Bound getBound()
   {
	   return bound;
   }
}
