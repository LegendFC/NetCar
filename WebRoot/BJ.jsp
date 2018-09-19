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
    
    <title>运营车辆数</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	 <link href="http://libs.baidu.com/bootstrap/3.0.3/css/bootstrap.min.css" rel="stylesheet">
    <script src="http://libs.baidu.com/jquery/2.0.0/jquery.min.js"></script>
    <script src="http://libs.baidu.com/bootstrap/3.0.3/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="http://libs.baidu.com/jquery/1.9.1/jquery.js"></script>
    <!-- 上述3个meta标签*必须*放在最前面，任何其他内容都*必须*跟随其后！ -->
    <script type="text/javascript" src="http://echarts.baidu.com/gallery/vendors/echarts/echarts-all-3.js"></script>
    <script type="text/javascript" src="http://echarts.baidu.com/gallery/vendors/echarts/extension/dataTool.min.js"></script>
    <script type="text/javascript" src="http://echarts.baidu.com/gallery/vendors/echarts/map/js/china.js"></script>   
    <script type="text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=DGnY1nQZcu2e4nytZcpGYRXgWUV5Xqxp"></script>
    <script type="text/javascript" src="http://echarts.baidu.com/gallery/vendors/echarts/extension/bmap.min.js"></script>
    <script type="text/javascript" src="js/beijing.js"></script>
    <script type="text/javascript" src="js/shanghai.js"></script>
    <script type="text/javascript" src="js/anhui.js"></script>
    <script type="text/javascript" src="js/chongqing.js"></script>
    <script type="text/javascript" src="js/fujian.js"></script>
	<script type="text/javascript" src="js/gansu.js"></script>
	<script type="text/javascript" src="js/guangdong.js"></script>
	<script type="text/javascript" src="js/guangxi.js"></script>
	<script type="text/javascript" src="js/guizhou.js"></script>
	<script type="text/javascript" src="js/hainan.js"></script>
	<script type="text/javascript" src="js/hebei.js"></script>
	<script type="text/javascript" src="js/heilongjiang.js"></script>
	<script type="text/javascript" src="js/henan.js"></script>
	<script type="text/javascript" src="js/hubei.js"></script>
	<script type="text/javascript" src="js/hunan.js"></script>
	<script type="text/javascript" src="js/jiangsu.js"></script>
	<script type="text/javascript" src="js/jiangxi.js"></script>
	<script type="text/javascript" src="js/jilin.js"></script>
	<script type="text/javascript" src="js/liaoning.js"></script>
	<script type="text/javascript" src="js/neimenggu.js"></script>
	<script type="text/javascript" src="js/ningxia.js"></script>
	<script type="text/javascript" src="js/qinghai.js"></script>
	<script type="text/javascript" src="js/shandong.js"></script>
	<script type="text/javascript" src="js/shanxi.js"></script>
	<script type="text/javascript" src="js/shanxi1.js"></script>
	<script type="text/javascript" src="js/sichuan.js"></script>
	<script type="text/javascript" src="js/tianjin.js"></script>
	<script type="text/javascript" src="js/xinjiang.js"></script>
	<script type="text/javascript" src="js/xizang.js"></script>
	<script type="text/javascript" src="js/yunnan.js"></script>
	<script type="text/javascript" src="js/zhejiang.js"></script>
  </head>
  
  <body>
    <div class="col-md-4" id = "demo"style="margin-top: 10px">
     <div class="span2">
            <ul class="nav nav-pills" role="tablist">
                <li class="active">
                <a href="province.jsp" style= "margin-right:10px">首页</a></li>   
                <li><a href="historyTrace.jsp" style= "margin-right:10px">历史轨迹</a></li>
                <li><a href=<%=thisPath+"grid/index.jsp"%>>订单预测</a></li> 
            </ul>
        </div>
	</div>
   <div class="col-md-6" id="main" style="width:100%;height:100%;"></div>
    <script type="text/javascript">
        // 基于准备好的dom，初始化echarts实例
        var myChart = echarts.init(document.getElementById('main'));
        
        var urlinfo = window.location.href;  
        var provincenum = urlinfo.split("?")[1].split("=")[1];
        var province = [];
        province.push('北京','上海','天津','海南','宁夏','重庆','青海','吉林','西藏','福建','甘肃','贵州','浙江','江苏','江西','辽宁','内蒙古','湖北','新疆','安徽','陕西','广西','山西','广东','湖南','黑龙江','云南','山东','河南','河北','四川');
        var provincegrid = [];
        provincegrid.push("110000","310000","120000","460000","640000","500000","630000","220000","540000","350000","620000","520000","330000","320000","360000","210000","150000","420000","650000","340000","610000","450000","450000","440000","430000","230000","530000","370000","410000","130000","510000");

        //new annotation
// myChart.setOption({
//     title: {
// 			text: '运营车辆数',
// 			left: 'center'
// 			},
//     tooltip: {
//         trigger: 'item'
//     },
//     visualMap: {
// 				min: 0,
// 				max: 500,
// 				left: 'left',
// 				top: 'bottom',
// 				text: ['高','低'],           // 文本，默认为数值文本
// 				show:false,
// 				calculable: true,
// 				color: ['blue','lightskyblue','white']
// 				},
//     toolbox: {
// 			show: true,
// 			orient: 'vertical',
// 			left: 'right',
// 			top: 'center',
// 			feature: {
// 						dataView: {readOnly: false},
// 						restore: {},
// 						saveAsImage: {}
// 					}
// 			},
//     series: [
//         {
//             name: '易到',
//             type: 'map',
//             showLegendSymbol:false,
// 			mapType: province[provincenum-1],
//             roam: false,
//             label: {
//                 normal: {
//                     show: true
//                 },
//                 emphasis: {
//                     show: true
//                 }
//             },
//             data:[]
//         },
//         {
//             name: '首汽',
//             type: 'map',
//             mapType: province[provincenum-1],
//             label: {
//                 normal: {
//                     show: true
//                 },
//                 emphasis: {
//                     show: true
//                 }
//             },
//             data:[]
//         },
//         {
//             name: '神州',
//             type: 'map',
//             mapType: province[provincenum-1],
//             label: {
//                 normal: {
//                     show: true
//                 },
//                 emphasis: {
//                     show: true
//                 }
//             },
//             data:[]
//         }
//     ]
// }); // 使用刚指定的配置项和数据显示图表。
        //annotation end

        //newly added
        myChart.setOption({
            title: {
                text: '运营车辆数',
                left: 'center'
            },
            tooltip: {
                trigger: 'item'
            },
            visualMap: {
                min: 0,
                max: 500,
                left: 'left',
                top: 'bottom',
                text: ['高','低'],           // 文本，默认为数值文本
                show:false,
                calculable: true,
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
			//new annotation
            // series: [
             //    {
             //        name: '易到',
             //        type: 'map',
             //        showLegendSymbol:false,
             //        mapType: province[provincenum-1],
             //        roam: false,
             //        label: {
             //            normal: {
             //                show: true
             //            },
             //            emphasis: {
             //                show: true
             //            }
             //        },
             //        data:[]
             //    },
             //    {
             //        name: '首汽',
             //        type: 'map',
             //        mapType: province[provincenum-1],
             //        label: {
             //            normal: {
             //                show: true
             //            },
             //            emphasis: {
             //                show: true
             //            }
             //        },
             //        data:[]
             //    },
             //    {
             //        name: '神州',
             //        type: 'map',
             //        mapType: province[provincenum-1],
             //        label: {
             //            normal: {
             //                show: true
             //            },
             //            emphasis: {
             //                show: true
             //            }
             //        },
             //        data:[]
             //    }
            // ]
			//annotation end
            series: [
                {
                    name: '实时车辆数',
                    type: 'map',
                    showLegendSymbol:false,
                    mapType: province[provincenum-1],
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
                }
            ]
        }); // 使用刚指定的配置项和数据显示图表。
        //add end

var area = provincegrid[provincenum-1];
var url="${pageContext.request.contextPath}/provinceDataServlet";


refresh();
setInterval("refresh()",1000);

    //new annotation
// function refresh()
// {
//     $.getJSON(url,{area:area,company:"yd,sz,sq",status:"yunying"}).done(function(data){
//         var data1=JSON.parse(data.data1);
// 		var data2=JSON.parse(data.data2);
// 		var data3=JSON.parse(data.data3);
//
// 		myChart.setOption({
// 	        series: [
// 	        {
// 	            // 根据名字对应到相应的系列
// 	            name: '易到',
// 	            data: data1
// 	            },
// 	            {
// 	            name:'首汽',
// 	            data:data2
// 	            },
// 	            {
// 	            name:'神州',
// 	            data:data3
// 	        }
// 	        ]
// 	    });
//     });
// }
    //annotation end

    //newly add
function refresh() {
    $.getJSON(url).done(function (data) {
        myChart.setOption({
            series: [
                {
                    // 根据名字对应到相应的系列
                    name: '实时车辆数',
                    data: data
                }
            ]
        });
    });
}
    //add end


myChart.on('click', function (params) {
                   var myGeo = new BMap.Geocoder();
    // 将地址解析结果显示在地图上,并调整地图视野
            myGeo.getPoint(params.name, function(point){
            if (point) {
            //alert(params.name);
            //alert(point);
            //alert(point.lng);
            //alert(point.lat);
            lon = Number(point.lng);
            lat = Number(point.lat);
            window.location.href = (encodeURI("index.jsp"+"?lon="+lon+"?lat="+lat));
        }else{
            alert("您选择地址没有解析到结果!");
        }
    }, "北京市");
                });
                
function jump(){
    window.location.href = "province.jsp";
}
    </script>
  </body>
</html>
