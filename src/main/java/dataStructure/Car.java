package dataStructure;

public class Car {
	//18级需要知道车的车牌号、经纬度、时间、方向、速度
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
