<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
String thisPath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+"/";
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'trace.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<script src="js/echarts.js"></script>
	<script src="js/china.js"></script>
	<link href="http://libs.baidu.com/bootstrap/3.0.3/css/bootstrap.min.css" rel="stylesheet">
    <script src="http://libs.baidu.com/jquery/2.0.0/jquery.min.js"></script>
    <script src="http://libs.baidu.com/bootstrap/3.0.3/js/bootstrap.min.js"></script>
	<script src="http://echarts.baidu.com/gallery/vendors/jquery/jquery.js"></script>
	<script type="text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=DGnY1nQZcu2e4nytZcpGYRXgWUV5Xqxp"></script>
  	<script type="text/javascript" src="http://api.map.baidu.com/library/TextIconOverlay/1.2/src/TextIconOverlay_min.js"></script>
  	<script type="text/javascript" src="http://api.map.baidu.com/library/MarkerClusterer/1.2/src/MarkerClusterer_min.js"></script>
	
  </head>
  
  <body>
   <div class="col-md-4" style="margin-top: 10px; margin-bottom:10px">
    <div class="row">
        <div class="span2">
            <ul class="nav nav-pills">
                <li class="active">
                <a href="province.jsp">首页</a></li>   
                <li><a href="historyTrace.jsp">历史轨迹查询</a></li>
                <li><a href="<%=thisPath+"grid/index.jsp"%>">订单预测</a></li> 
            </ul>
        </div>
    </div>
</div>
    <div class="col-md-6" id="l-map" style="height:100%;width:100%;"></div>
    <div>
    	<p id="testp"></p>
    	<p id="testp1"></p>
    </div>
    
	
  
	<script type="text/javascript">
   		
   		var opts = {
    		width : 300,   // 信息窗口宽度
    		height: 225,   // 信息窗口高度
    		enableMessage: false//设置不允许信息窗发送短息
  		};
  		var  clickedMarker = null;
  	   
		var map = new BMap.Map("l-map");
		
		
		
// 		接收上一层传来的数据：ID，lon，lat
		var urlinfo = decodeURI(window.location.href);
    	//alert(urlinfo);  
   		var urlinfo1 = urlinfo.split("?")[1];
    	//alert(urlinfo1);
  		var urlinfo2 =  urlinfo.split("?")[2];
  		//alert(urlinfo2);
  		var urlinfo3 = urlinfo.split("?")[3];
  		var id = urlinfo1.split("=")[1];
 		//alert(id);
  		var lon = urlinfo2.split("=")[1];
  		//alert(lon);
  		var lat = urlinfo3.split("=")[1];
  		//alert(lat);
		
		//code for test
/* 		carId="15897645000#967790129156";
   		lon=116.38166130880722;
   		lat=39.904110395857785; */
   		
		map.enableScrollWheelZoom();
		var point = new BMap.Point(lon,lat);//这个地方的中心点是上一个页面车辆的经纬度
		map.centerAndZoom(point, 18);
		var carIcon = new BMap.Icon('http://developer.baidu.com/map/jsdemo/img/car.png', new BMap.Size(52,26));
		var marker = new BMap.Marker(point, {icon: carIcon});
		map.addOverlay(marker);
		var infoWindow = new BMap.InfoWindow("", opts);  // 创建信息窗口对象
  		infoWindow.addEventListener("clickclose", function(){  //点击信息窗口的关闭按钮时触发此事件。
   			 clickedMarker = null;
  			});		
		if ( infoWindow.isOpen() ) {
	        show_info_window();
	      }
 		function show_info_window(){
        	var point = marker.getPosition();
	    	map.openInfoWindow(infoWindow, point); 
    		}
		
		var url = "${pageContext.request.contextPath}/getRouteServlet"; 

		var carpath1;
		var dir1;
		var time1;
		var speed1;
		var original=new BMap.Point(lon,lat);

		var i=1;
		var category="trace";
		
		refreshData();
		
		function refreshData(){
 			$.getJSON(url,{kind:category,id:id}).done(function(data){
						for(i in data){
							carpath1=new BMap.Point(data[i].lon,data[i].lat);
							dir1=data[i].direction;
							speed1=data[i].speed;	
						}  
						marker.setPosition(carpath1);
						marker.setRotation(dir1);
						var polyline = new BMap.Polyline([original,carpath1],{strokeColor:"blue",strokeWeight:6,strokeOpacity:0.5});
			
						infoWindow.setContent("车牌:"+id+"\n"+"方向:"+dir1+"\n");
							marker.addEventListener("click",function(e){
							show_info_window();
						});
						original=carpath1;
						map.addOverlay(polyline);
						map.closeInfoWindow();
							
						
						setTimeout(function(){				
								refreshData();
							},5000);
					}
		
						
						
										
				
			); 
		}
		
		//resetMKPoint();

		function resetMKPoint(){
			refreshData();只需确定carpath1可以不断更新即可
			carpath1=new BMap.Point(d2[i].lon,d2[i].lat);
			dir1=d2[i].direction;
						
			document.getElementById("testp").innerHTML=d2[i].lon;
			i++;
			if(i>=len)   i--;
			marker.setPosition(carpath1);
			marker.setRotation(dir1);
			var polyline = new BMap.Polyline([original,carpath1],{strokeColor:"blue",strokeWeight:6,strokeOpacity:0.5});
			
			infoWindow.setContent("车牌:"+carId+"\n"+"方向:"+dir1+"\n");
				marker.addEventListener("click",function(e){
				show_info_window();
			});
			original=carpath1;
			map.addOverlay(polyline);
			map.closeInfoWindow();
				
			
			setTimeout(function(){				
					resetMKPoint();
				},5000);
		}
		
	</script>
  </body>
</html>
