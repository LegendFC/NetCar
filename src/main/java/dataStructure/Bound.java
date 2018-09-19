package dataStructure;

public class Bound {
	private LonLat center;
	private LonLat leftUp;
	private LonLat rightDown;
	
	public Bound(LonLat center,LonLat leftUp,LonLat rightDown)
	{
		this.center=center;
		this.leftUp=leftUp;
		this.rightDown=rightDown;
	}
	
	public LonLat getCenter()
	{
		return center;
	}
	
	public boolean inBound(LonLat point)
	{
		double lon=point.getLongtitude();
		double lat=point.getLatitude();
		
		if(lon<leftUp.getLongtitude()||lon>rightDown.getLongtitude()||lat<rightDown.getLatitude()||lat>leftUp.getLatitude())
		{
			return false;
		}
		
		return true;
	}
}
