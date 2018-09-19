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
    
    <title>My JSP 'index.jsp' starting page</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	
	
	<link href="http://libs.baidu.com/bootstrap/3.0.3/css/bootstrap.min.css" rel="stylesheet">
    <script src="http://libs.baidu.com/jquery/2.0.0/jquery.min.js"></script>
    <script src="http://libs.baidu.com/bootstrap/3.0.3/js/bootstrap.min.js"></script>
	
	<script type="text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=DGnY1nQZcu2e4nytZcpGYRXgWUV5Xqxp"></script>
	<script type="text/javascript" src="http://libs.baidu.com/jquery/1.9.1/jquery.js"></script>
	<script type="text/javascript" src="http://api.map.baidu.com/library/TextIconOverlay/1.2/src/TextIconOverlay_min.js"></script>
  	<script type="text/javascript" src="http://api.map.baidu.com/library/MarkerClusterer/1.2/src/MarkerClusterer_min.js"></script>
	<title>zoom=12</title>
  </head>
 
  <body>

	
	<div id="allmap" style="width:100%;height:100%;"></div>
	

	
	<script type="text/javascript">
	var map = new BMap.Map("allmap");
	var geoc = new BMap.Geocoder();  
	//I add this block
	var urlinfo = decodeURI(window.location.href);
  //alert(urlinfo);  
  	var urlinfo1 = urlinfo.split("?")[1];
  //alert(urlinfo1);
  	var urlinfo2 =  urlinfo.split("?")[2];
  //alert(urlinfo2);
  	var lon = urlinfo1.split("=")[1];
  //alert(lon);
 	 var lat = urlinfo2.split("=")[1];
  //alert(lat);
  
  map.centerAndZoom(new BMap.Point(lon,lat), 10);//鍒濆�嬪寲鍦板浘
	//var point = new BMap.Point(116.342900000001,40.0019033333);
  //map.centerAndZoom(new BMap.Point(116.45502499999999,39.86971333333334), 16);
	var markerClusterer = new BMapLib.MarkerClusterer(map,{maxZoom:15});
	
	//beijing:116.41727,39.9390731
	//xizang:97.818145,31.304541
	//changsha:112.743826,28.177536
	//Redis has this point:115.9999999999, 39
	
	//map.centerAndZoom(point, 10);
	map.enableScrollWheelZoom();
	var clusterIcon0 = new BMap.Icon('images/m0.png',new BMap.Size(52,50));
    var clusterIcon1 = new BMap.Icon('images/m1.png',new BMap.Size(60,60));
    var clusterIcon2 = new BMap.Icon('images/m2.png',new BMap.Size(70,70));
    var clusterIcon3 = new BMap.Icon('images/m3.png',new BMap.Size(80,80));
    var clusterIcon4 = new BMap.Icon('images/m4.png',new BMap.Size(100,100));

    var cluster_label_style = {
	        fontSize: "12px",
	        //fontWeight: "bold",
	        border: "",
	        backgroundColor: ""
	      };
	
    var opts = {
    		  width : 400,   
    		  height: 325,  
    		  enableMessage: false
    		};//濞ｅ洠鍓濇导鍛�绮ｅΔ鈧�瑜版盯鏌婂�ュ洨鏋�

	var zoom;
	var oldZoom = 9;
	var swlng,swlat,nelng,nelat;
	
	
	
	var str;	
    var url = "${pageContext.request.contextPath}/getDataServlet";
    var caricon = new BMap.Icon('http://developer.baidu.com/map/jsdemo/img/car.png', new BMap.Size(52,26));
    var clickedMarker = null;
    var clusterMarker = null;
    var clickeddbMarker = null;
    var markerhash = {};
    var infoWindow = {};

		
	setInterval("refresh()",10000);
	
	function refresh()
	{
		if(zoom>=10&&zoom<=15)
		{
			show_cluster_markers(zoom);
		}
		else if(zoom>15)
		{
			show_car_markers();
		}
	}
		
    //閻忕偞娲滈妵姝漧usters
	function  show_cluster_markers(zoom){
		map.clearOverlays(); 
		$.getJSON(url,{zoom:zoom,swlng:swlng,swlat:swlat,nelng:nelng,nelat:nelat}).done(function(data){
			//alert("hhh")
			console.log(zoom);
			console.log(data);
			for (i in data){
				var point = new BMap.Point(data[i].lon,data[i].lat);
				var marker = new BMap.Marker(point, {icon: getIcon(data[i].size)});
				console.log(point);
				var label = new BMap.Label(data[i].size,{offset:getLabelOffset(data[i].size)});
				label.setStyle(cluster_label_style);
				marker.setLabel(label);
				map.addOverlay(marker);
				marker.addEventListener("click",function(e){
					clusterMarker = e.target;
					zoom_in();
				});
			}
		});
	}
		  
	//闁谎勫劤鐎规娊宕烽弶鎸庣�堥柤杈ㄨ壘閹�锟�
	function show_car_markers(){
		map.clearOverlays(); 
		$.getJSON(url,{zoom:zoom,swlng:swlng,swlat:swlat,nelng:nelng,nelat:nelat}).done(function (data){
			console.log(data);
			for (i in data){
				var point = new BMap.Point(data[i].lon,data[i].lat);
				var marker = markerhash[data[i].id];
				if(marker){
					 marker.setPosition(point);
				}
				else {
				marker = new BMap.Marker(point,{icon: caricon});
				marker.setRotation(data[i].direction);
				markerhash[data[i].id] = marker;
				map.addOverlay(marker);
				infoWindow[data[i].id] = new BMap.InfoWindow("", opts);
	  			infoWindow[data[i].id].setContent("车辆编号:"+data[i].id+"\n"+"经纬度"+data[i].lon+","+data[i].lat+"GPS时间:"+data[i].time+"\n"+"方向:"+data[i].direction+"速度:"+data[i].speed);
	  			
				marker.addEventListener("click",function(e){
						clickedMarker = e.target;

						for(j in markerhash){
							if(markerhash[j] == clickedMarker) break;
						}
							var ppoint = clickedMarker.getPosition();
      					 	map.openInfoWindow(infoWindow[j], ppoint); 
					});
					marker.addEventListener("ondblclick",function(e){
						clickeddbMarker = e.target;
						for(k in markerhash){
							if(markerhash[k] == clickeddbMarker) break;
						}
        			
        				clickeddbMarker = clickeddbMarker.getPosition();
        				var sendlon = Number(clickeddbMarker.lng);
       					var sendlat = Number(clickeddbMarker.lat);
        				window.open(encodeURI("trace.jsp"+"?id="+k+"?sendlon="+sendlon+"?sendlat="+sendlat));
					});
				}
	  		
				}
		});
		markerhash={};
		infoWindow={};
	}
	
/* 	function show_info_window(){
		 var point = clickedMarker.getPosition();
      	 map.openInfoWindow(infoWindow, point); 
	} */
	
	function zoom_in(){
		map.zoomIn();
	    map.setCenter(clusterMarker.getPosition());
	}
	
	function getAttribute()
	{
		zoom = map.getZoom();
		if (zoom < 10) {
			//window.history.back(-1);
			var rpoint = map.getCenter();
    		geoc.getLocation(rpoint, function(rs){
     			var addComp = rs.addressComponents;
      			//alert(addComp.province);
      			if (addComp.province=="北京市")
                        window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+1));
                else if (addComp.province=="上海市")
                        window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+2));
                else if (addComp.province=="天津市")
                        window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+3));
                else if (addComp.province=="海南省")
                        window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+4));
                else if (addComp.province=="宁夏回族自治区")
                        window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+5));
                else if (addComp.province=="重庆市")
                        window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+6));
                else if (addComp.province=="青海省")
            			window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+7));
          	 	else if (addComp.province=="吉林省")
                    	window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+8));
          	 	else if (addComp.province=="西藏自治区")
                  	  window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+9));
           	 	else if (addComp.province=="福建省")
            	        window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+10));
            	else if (addComp.province=="甘肃省")
                	    window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+11));
            	else if (addComp.province=="贵州省")
                  	  window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+12));
           		else if (addComp.province=="浙江省")
                    window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+13));
            	else if (addComp.province=="江苏省")
               		window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+14));
        		else if (addComp.province=="江西省")
                	window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+15));
        		else if (addComp.province=="辽宁省")
                	window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+16));
        		else if (addComp.province=="内蒙古自治区")
               		window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+17));
        		else if (addComp.province=="湖北省")
                	window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+18));
        		else if (addComp.province=="新疆维吾尔自治区")
                	window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+19));
        		else if (addComp.province=="安徽省")
              		window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+20));
        		else if (addComp.province=="陕西省")
                	window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+21));
        		else if (addComp.province=="广西省")
                	window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+22));
        		else if (addComp.province=="山西省")
                	window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+23));
        		else if (addComp.province=="广东省")
                	window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+24));
        		else if(addComp.province=="湖南省")
                	window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+25));
        		else if (addComp.province=="黑龙江省")
                	window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+26));
        		else if (addComp.province=="云南省")
                	window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+27));
        		else if (addComp.province=="山东省")
                	window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+28));
        		else if (addComp.province=="河南省")
                	window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+29));
        		else if (addComp.province=="河北省")
                	window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+30));
        		else if (addComp.province=="四川省")
                	window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+31));
			});
	}
		else if(zoom >=10 && zoom<=15) {
			swlng = map.getBounds().getSouthWest().lng;
		    swlat = map.getBounds().getSouthWest().lat;
		    nelng = map.getBounds().getNorthEast().lng;
		    nelat = map.getBounds().getNorthEast().lat;
		    oldZoom = zoom;
		    show_cluster_markers(zoom);
		  }
		  else {
				swlng = map.getBounds().getSouthWest().lng;
			    swlat = map.getBounds().getSouthWest().lat;
			    nelng = map.getBounds().getNorthEast().lng;
			    nelat = map.getBounds().getNorthEast().lat;
		  	show_car_markers();
		  }
	}
	
	map.addEventListener("zoomend", function(e){
		getAttribute();
    });
    
    map.addEventListener("dragend",function(e){
		getAttribute();
    });
	
	
	 function getIcon(sum) {
      if(sum > 0 && sum < 100) {
      	return clusterIcon0;
      }
      else if(sum >= 100 && sum < 500) {
      	return clusterIcon1;
      }
      else if(sum >= 500 && sum < 1000) {
      	return clusterIcon2;
      }
      else if(sum >= 1000 && sum < 3000) {
      	return clusterIcon3;
      }
      else if(sum >= 3000) {
      	return clusterIcon4;
      }
    }
	
    function getLabelOffset(sum) {
      if(sum > 0 && sum < 100) {
      	return new BMap.Size(19,17);
      }
      else if(sum >= 100 && sum < 500) {
      	return new BMap.Size(16.5,19);
      }
      else if(sum >= 500 && sum < 1000) {
      	return new BMap.Size(22,26.2);
      }
      else if(sum >= 1000 && sum < 3000) {
      	return new BMap.Size(23.5,32);
      }
      else if(sum >= 3000) {
      	return new BMap.Size(29,38);
      }
    }  
    
    getAttribute();
	
</script>
 <a href="trace.jsp?deviceID&sendpoint">濞磋偐濞€閳э拷閹烘垵妫橀柡渚婃嫹</a>  
	
	<!-- <script type="text/javascript">
	
		var str;	
	
		document.getElementById("buttonID").onclick = function(){
			//NO1)闁告帗绋戠紓鎻侸AX鐎碉拷閸屾冻鎷烽妷銉�鎷风涵鍛版澖
			var ajax = createAJAX();
			//NO2)闁告垵妫楅敓钘夋搐瑜板倿鏌呮担鏂ゆ嫹闁垮��婀�
			var method = "GET";
			var url = "${pageContext.request.contextPath}/getDataServlet";
			ajax.open(method,url);
			//NO3)闁伙拷閻旈潻鎷烽敐鍛�绲洪梺锟芥担鏂ゆ嫹闁垮��婀村ù锝嗘尵濞堟垿寮�閻楀牆绁﹂柛鎺斿�楀﹢鍥�宕濋垾铏�鐝ら柨娑樿嫰閿熻棄鍊归悘澶屾嫚闁垮��婀村ù锝嗘尫閼垫垿寮�閻樿櫕娈堕柟鐧告嫹闁汇劌瀚�閻︿粙鏁嶇仦鑺ョ殤闁烩懇鏆榰ll閻炴稏鍔庨妵锟�
			ajax.send(null);
			alert(url);
			//-------------------------------------------------------------缂佹稑锟藉﹦绐�
		
			//NO4)AJAX鐎碉拷閸屾冻鎷烽妷銉�鎷风涵鍛版澖濞戞挸绉甸弻鍥�鎯勯幋婵囧剶闁哄牆绉存慨鐔煎闯閵娿儲鎯欓幖瀛樻⒒濞堟垿鎮╅懜纰樺亾閿燂拷0-1-2-3-闁靛棴鎷�4闁靛棴鎷�
			//濞戞挴鍋撻悗瑙勪亢閿熸垝鑳舵慨鎼佸箑娴ｇ�跨秮闁告牗鐗曢幃妤呮晬鐏炴儳锟界娀宕ｉ敓鐣屾喆閿曗偓瑜颁咖unction(){}闁告垼濮ら弳锟�
			//濠碘€冲€归悘澶愭偐閼哥�樺亾娴ｈ�勶拷鍫熸交濠婂嫭笑4-4-4-4-4闁挎稑鏈�濡插憡绋夊�ュ嫮绐楅悷娆欑畱瑜颁咖unction(){}闁告垼濮ら弳鐔兼儍閿燂拷
			ajax.onreadystatechange = function(){
				//濠碘€冲€归悘澶愭偐閼哥�樺亾娴ｈ櫣鍨冲☉鎿勬嫹4闁汇劌瀚�閻︼拷
				if(ajax.readyState == 4){
					//濠碘€冲€归悘澶愬传瀹ュ懐瀹夐柣锟芥担鐤�绀�200闁汇劌瀚�閻︼拷
					if(ajax.status == 200){
						//NO5)濞寸姴鐩疛AX鐎碉拷閸屾冻鎷烽妷銉�鎷风涵鍛版澖濞戞搫鎷烽柤鎯у槻瑜板洭寮靛�ュ懎锟界喖宕抽妸銉︽儥閹煎瓨姊诲▓鎱擳ML闁轰胶澧楀畵锟�
						var nowStr = ajax.responseText;
						str=nowStr;
						//NO6)閻忓繐妫涚划銊╁几濠婂嫬鐦婚柣鎿勬嫹DOM閻熸瑥瀚�閸�锟介柨娑樿嫰婵�鈺呭箑娴ｇ懓娼戦柛鏃傚Т閸╁瘍eb濡炪倗鏁诲�间即骞愰崶褏鏆伴柣銊ュ��閻栵絿绮甸崣銉ㄥ幀
						var spanElement = document.getElementById("redis");
						spanElement.innerHTML = nowStr;
						var d1 =JSON.parse(nowStr);
					    alert(d1);
					    
						$(d1).each(function(i,item){
							var point = new BMap.Point(item.lon,item.lat);
							
							var marker = new BMap.Marker(point, {icon: getIcon(item.size)});
							var label = new BMap.Label(item.size,{offset:getLabelOffset(item.size)});
							label.setStyle(cluster_label_style);
							marker.setLabel(label);
							map.addOverlay(marker);
							//marker.setLabel(label);
							
						});
					}
				}
			} 
			
		}
	</script> -->
	
	<script type="text/javascript">
		
	</script>
	
	<script type="text/javascript">
		//闁告帗绋戠紓鎻侸AX鐎碉拷閸屾冻鎷烽妷銉�鎷风涵鍛版澖
		function createAJAX(){
			var ajax = null;
			try{
				//濠碘€冲€归悘濉圗5=IE12闁汇劌瀚�閻︼拷
				ajax = new ActiveXObject("microsoft.xmlhttp");
			}catch(e1){
				try{
					//濠碘€冲€归悘澶愬及閿熶粙妫冨┑鐩撻柣銊ュ��閻︼拷
					ajax = new XMLHttpRequest();
				}catch(e2){
					alert("濞达絿濮峰▓鎴澝硅箛姘炬嫹閸�婵囩彜濞戞搫鎷峰☉鎾崇У閺侊拷闁归晲绀佺槐鎾筹拷婵勫劚閿熺晫锟藉懓鏉介柨娑樼焷閿熶粙鏀卞畷鎻捗硅箛姘炬嫹閸�婵囩彜");
				}
			}
			return ajax;
		}
	</script>
	
	
  </body>
</html>
