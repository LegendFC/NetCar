package dataStructure;

public class Car {
	//18����Ҫ֪�����ĳ��ƺš���γ�ȡ�ʱ�䡢�����ٶ�
	String carID;
	String time;
	String direction;
	String speed;
	LonLat position;
	
	public Car(String carID,String time, String direction, String speed, LonLat position)
	{
		this.carID=carID;
		this.time=time;
		this.direction=direction;
		this.speed=speed;
		this.position=position;
	}
	
	public String getCarID()
	{
		return carID;
	}
	
	public String getTime()
	{
		return time;
	}
	
	public String getDirection()
	{
		return direction;
	}
	
	public String getSpeed()
	{
		return speed;
	}
	
	public LonLat getPosition()
	{
		return position;
	}
}
