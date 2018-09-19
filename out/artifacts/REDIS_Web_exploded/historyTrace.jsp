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
    
    <title>历史轨迹</title>
    
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
	<script src="http://echarts.baidu.com/gallery/vendors/jquery/jquery.js"></script>
	<script type="text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=DGnY1nQZcu2e4nytZcpGYRXgWUV5Xqxp"></script>
  	<script type="text/javascript" src="http://api.map.baidu.com/library/TextIconOverlay/1.2/src/TextIconOverlay_min.js"></script>
  	<script type="text/javascript" src="http://api.map.baidu.com/library/MarkerClusterer/1.2/src/MarkerClusterer_min.js"></script>
	<script type="text/javascript" src="http://api.map.baidu.com/library/LuShu/1.2/src/LuShu_min.js"></script>
	<link rel="stylesheet" href="http://cdn.bootcss.com/bootstrap/3.3.0/css/bootstrap.min.css">
  </head>
  
  <body>
  	<div class="col-md-4" id = "demo"style="margin-top: 10px; margin-bottom:10px">
     <div class="span2">
            <ul class="nav nav-pills" role="tablist">
                <li>
                <a href="province.jsp" style= "margin-right:10px">首页</a></li>   
                <li><a href="historyTrace.jsp" style= "margin-right:10px">历史轨迹</a></li>
                <li><a href=<%=thisPath+"grid/index.jsp"%>>订单预测</a></li> 
            </ul>
        </div>
	</div>
  	<div style="position:fixed;width:100%;height:80px;text-align:center;top:80px;margin-left:15px;z-index:5;">
  	    <div class="row">
	  <div class="col-lg-4">
	    <div class="input-group">
	      <input type="text" class="form-control" id="input" placeholder="请输入车牌号">
	      <span class="input-group-btn">
	        <button class="btn btn-default" type="button" onclick="search()" >查询历史轨迹</button>
	      </span>
	    </div><!-- /input-group -->
	  </div><!-- /.col-lg-6 -->
	  <div class="col-lg-8">
	    
	  </div><!-- /.col-lg-6 -->
	</div><!-- /.row -->
	</div>
	
 
    <div class = "col-md-6" id="l-map" style="height:100%;width:100%;"></div>
    
    
	
  
	<script type="text/javascript">
/* 		var urlinfo = window.location.href;//获取url
		document.getElementById("testp").innerHTML=urlinfo;
   		var carId = urlinfo.split("?")[1].split("=")[1];  		 
   		var carTime=urlinfo.split("?")[2].split("=")[1];*/
   		
   		
   		/* var carlon= urlinfo.split("?")[2].split("=")[1];  
   		var carlat= urlinfo.split("?")[3].split("=")[1];  */

   		
   		   
		var map = new BMap.Map("l-map");
		map.enableScrollWheelZoom();
		var point = new BMap.Point(116.399, 39.910);//这个地方的中心点是上一个页面车辆的经纬度
		
		map.centerAndZoom(point, 15);
		/* var carIcon = new BMap.Icon('http://developer.baidu.com/map/jsdemo/img/car.png', new BMap.Size(52,26));
		var marker = new BMap.Marker(point, {icon: carIcon});
		map.addOverlay(marker); */
		var url = "${pageContext.request.contextPath}/getRouteServlet"; 

		var carpath1=[];
	
		var cou=0;
		var situation="true";
		
		//window.setInterval(refreshData, 10000);
		
		function search(){
			alert("wang!");
			var temp=document.getElementById("input").value;
			carpath1=[];
			var category="history";
			$.getJSON(url,{kind:category,id:temp}).done(function(data){
				
					var len=data.length;
					
					
					
						for (i in data){
							
							carpath1.push(new BMap.Point(data[i].lon,data[i].lat));
							//console.log(carpath1);
		
							/* carpath1[cou]=new BMap.Point(data[i].lon,data[i].lat);
							cou++; */
						}	
				
			}); 
			
			putTrace();
		}
		
		function putTrace()
		{
			 if(carpath1.length!=0){
				//document.getElementById("testp").innerHTML=carpath1.length;
				map.clearOverlays();  
				
				
				polyline = new BMap.Polyline(carpath1, {strokeColor:'#0A0A0A', strokeWeight:5, strokeOpacity:0.5});   //创建折线
				map.addOverlay(polyline);
				map.setViewport(carpath1);
				
				lushu = new BMapLib.LuShu(map,carpath1,{
                defaultContent:"",
                autoView:true,//是否开启自动视野调整，如果开启那么路书在运动过程中会根据视野自动调整
                icon  : new BMap.Icon('http://developer.baidu.com/map/jsdemo/img/car.png', new BMap.Size(52,26),{anchor : new BMap.Size(27, 13)}),
                speed: 800,
                enableRotation:true,//是否设置marker随着道路的走向进行旋转
                }); 
                lushu.start();
			}else{
				setTimeout(function(){				
					putTrace();
				},20);
			}
		}
	
		//alert(carpath1.length);
		
/* 		polyline = new BMap.Polyline([
		new BMap.Point(116.399, 39.910),
		new BMap.Point(116.405, 39.920),
		new BMap.Point(116.425, 39.900)
	], {strokeColor:"blue", strokeWeight:2, strokeOpacity:0.5});   //创建折线 */ 
		
		
	 	
		
	</script>
  </body>
</html>
