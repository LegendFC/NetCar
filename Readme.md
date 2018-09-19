# 边缘节点计算平台 计算样例展示程序

##### 概述：迭代自Haiquan Wang实验室原网约车展示项目，用于实时展示北京市各区县网约车数量。

##### 改动提示：

	所有代码注释部分格式如下：

```
//new annotation
	xxx
	xxx
//annotation end
```

	所有代码新增部分格式如下：

```
//newly added
	xxx
	xxx
//add end
```

##### 配置说明：

	目前取数据是从http://139.199.32.80:3000的/getResult服务中获取，传递参数serviceRequestID。如需更改取数据节点，只需在REDIS\src\main\java\redisWeb\provinceDataServlet.java中更改以下代码。

```
HttpUtil httpUtil=new HttpUtil();
temp=httpUtil.doGet("http://139.199.32.80:3000/getResult?serviceRequestID=59");
```

	将其中的url更换即可。