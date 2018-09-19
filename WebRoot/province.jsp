<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String thisPath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+"/";
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
     <title>全国首页</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    
	<script type="text/javascript" src="http://libs.baidu.com/jquery/1.9.1/jquery.js"></script>
    <!-- 上述3个meta标签*必须*放在最前面，任何其他内容都*必须*跟随其后！ -->
     <link href="http://libs.baidu.com/bootstrap/3.0.3/css/bootstrap.min.css" rel="stylesheet">
    <script src="http://libs.baidu.com/jquery/2.0.0/jquery.min.js"></script>
    <script src="http://libs.baidu.com/bootstrap/3.0.3/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="http://echarts.baidu.com/gallery/vendors/echarts/echarts-all-3.js"></script>
    <script type="text/javascript" src="http://echarts.baidu.com/gallery/vendors/echarts/extension/dataTool.min.js"></script>
    <script type="text/javascript" src="http://echarts.baidu.com/gallery/vendors/echarts/map/js/china.js"></script>
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
  </head>
  
  <body>
   <div class="col-md-4" id = "demo" style="margin-top:10px">
     <div class="span2">
            <ul class="nav nav-pills" role="tablist">
                <li class="active">
                <a href="province.jsp" style= "margin-right:10px">首页</a></li>   
                <li><a href="historyTrace.jsp" style= "margin-right:10px">历史轨迹</a></li>
                <li><a href="<%=thisPath+"grid/index.jsp"%>">订单预测</a></li> 
            </ul>
        </div>
 
</div>
   <div class="col-md-6" id="main" style="width:100%;height:100%;"></div>
    <script type="text/javascript">
        // 基于准备好的dom，初始化echarts实例
        var myChart = echarts.init(document.getElementById('main'));

        // 指定图表的配置项和数据

myChart.setOption({
    title: {
			text: '全国各省市运营车辆数',
			left: 'center'
			},
    tooltip: {
        trigger: 'item'
    },
    visualMap: {
				min: 0,
				max: 1500,
				left: 'left',
				top: 'bottom',
				text: ['高','低'],           // 文本，默认为数值文本
				show:false,calculable: true,
				color: ['blue','lightskyblue','white']
				},
    toolbox: {
			show: true,
			orient: 'vertical',
			left: 'right',
			top: 'center',
			feature: {
						dataView: {readOnly: false},
						restore: {},
						saveAsImage: {}
					}
			},
    series: [
        {
            name: '易到',
            type: 'map',
            mapType: 'china',
            roam: false,
            label: {
                normal: {
                    show: true
                },
                emphasis: {
                    show: true
                }
            },
            data:[]
        },
        {
            name: '首汽',
            type: 'map',
            mapType: 'china',
            label: {
                normal: {
                    show: true
                },
                emphasis: {
                    show: true
                }
            },
            data:[]
        },
        {
            name: '神州',
            type: 'map',
            mapType: 'china',
            label: {
                normal: {
                    show: true
                },
                emphasis: {
                    show: true
                }
            },
            data:[]
        }
    ]
}); // 使用刚指定的配置项和数据显示图表。
var area = "china";
var com="yd,sq,sz";
var status = "yuying";
var url="${pageContext.request.contextPath}/provinceDataServlet";
getJson();
setInterval("getJson()",1000);
function getJson(){
	$.getJSON(url,{area:"china",company:com, status:"yunying"}).done(function(data){
		var data1=JSON.parse(data.data1);
		var data2=JSON.parse(data.data2);
		var data3=JSON.parse(data.data3);

		myChart.setOption({
	        series: [
	        {
	            // 根据名字对应到相应的系列
	            name: '易到',
	            data: data1
	            },
	            {
	            name:'首汽',
	            data:data2
	            },
	            {
	            name:'神州',
	            data:data3 
	        }
	        ]
	    });
	});

}



/*  $.get(url,{area:"china",company1:"yd",company2:"sq",company3:"sz",status:"yunying"},function(data){
	console.log(data);
})  */

myChart.on('click', function (params) {
                     if (params.name=="北京")
                        window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+1));
                    else if (params.name=="上海")
                        window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+2));
                     else if (params.name=="天津")
                        window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+3));
                     else if (params.name=="海南")
                        window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+4));
                     else if (params.name=="宁夏")
                        window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+5));
                     else if (params.name=="重庆")
                        window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+6));
                     else if (params.name=="青海")
                        window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+7));
                     else if (params.name=="吉林")
                        window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+8));
                     else if (params.name=="西藏")
                        window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+9));
                     else if (params.name=="福建")
                        window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+10));
                     else if (params.name=="甘肃")
                        window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+11));
                     else if (params.name=="贵州")
                        window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+12));
                     else if (params.name=="浙江")
                        window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+13));
                     else if (params.name=="江苏")
                        window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+14));
                     else if (params.name=="江西")
                        window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+15));
                     else if (params.name=="辽宁")
                        window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+16));
                     else if (params.name=="内蒙古")
                        window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+17));
                     else if (params.name=="湖北")
                        window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+18));
                     else if (params.name=="新疆")
                        window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+19));
                     else if (params.name=="安徽")
                        window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+20));
                     else if (params.name=="陕西")
                        window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+21));
                     else if (params.name=="广西")
                        window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+22));
                     else if (params.name=="山西")
                        window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+23));
                     else if (params.name=="广东")
                        window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+24));
                     else if(params.name=="湖南")
                        window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+25));
                     else if (params.name=="黑龙江")
                        window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+26));
                     else if (params.name=="云南")
                        window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+27));
                     else if (params.name=="山东")
                        window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+28));
                     else if (params.name=="河南")
                        window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+29));
                     else if (params.name=="河北")
                        window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+30));
                     else if (params.name=="四川")
                        window.location.href = (encodeURI("BJ.jsp"+"?provincenum="+31));
                });
    </script>
    
  </body>
</html>
