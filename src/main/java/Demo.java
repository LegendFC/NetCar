import com.google.gson.Gson;
import dataStructure.JsonBean;
import dataStructure.Key_Order;
import redisWeb.provinceDataServlet;


public class Demo {
    public static String[][] districtCodes=new String[][]{
            {"110101","110102","110105","110106","110107","110108","110109","110111","110112","110113","110114","110115","110116","110117","110118","110119"},
            {"310101","301104","310105","310106","310107","310108","310109","310110","310112","310113","310114","310115","310116","310117","310118","310120","310230"},
            {"120101","120102","120103","120104","120105","120106","120110","120111","120112","120113","120114","120115","120116","120117","120118","120225"},
            {"4401","4402","4403","4404","4405","4406","4407","4408","4409","4412","4413","4414","4415","4416","4417","4418","441900","442000","4451","4452","4453"},
            {"6401","6402","6403","6404","6405"},
            {"500101","500102","500103","500104","500105","500106","500107","500108","500109","500110","500111","500112","500113","500114","500115","500116","500117","500118","500119","500120","500151","500152","500153","500228","500229","500230","500231","500232","500233","500234","500235","500236","500237","500238","500240","500241","500242","500243"},
            {"6301","6302","6322","6323","6325","6326","6327","6328"},
            {"2201","2202","2203","2204","2205","2206","2207","2208","2224"}
    };
    public static String[][] districtNames=new String[][]{
            {"东城区","西城区","朝阳区","丰台区","石景山区","海淀区","门头沟区","房山区","通州区","顺义区","昌平区","大兴区","怀柔区","平谷区","密云区","延庆区"},
            {"黄浦区","徐汇区","长宁区","静安区","普陀区","闸北区","虹口区","杨浦区","闵行区","宝山区","嘉定区","浦东新区","金山区","松江区","青浦区","奉贤区","崇明县"},
            {"和平区","河东区","河西区","南开区","河北区","红桥区","东丽区","西青区","津南区","北辰区","武清区","宝坻区","滨海新区","宁河区","静海区","蓟县"},
            {"广州市","韶关市","深圳市","珠海市","汕头市","佛山市","江门市","湛江市","茂名市","肇庆市","惠州市","梅州市","汕尾市","河源市","阳江市","清远市","东莞市","中山市","潮州市","揭阳市","云浮市"},
            {"银川市","石嘴山市","吴忠市","固原市","中卫市"},
            {"万州区","涪陵区","渝中区","大渡口区","江北区","沙坪坝区","九龙坡区","南岸区","北碚区","綦江区","大足区","渝北区","巴南区","黔江区","长寿区","江津区","合川区","永川区","南川区","璧山区","铜梁区","潼南区","荣昌区","梁平县","城口县","丰都县","垫江县","武隆县","忠县","开县","云阳县","奉节县","巫山县","巫溪县","石柱土家族自治县","秀山土家族苗族自治县","酉阳土家族苗族自治县","彭水苗族土家族自治县"},
            {"西宁市","海东市","海北藏族自治州","黄南藏族自治州","海南藏族自治州","果洛藏族自治州","玉树藏族自治州","海西蒙古族藏族自治州"},
            {"长春市","吉林市","四平市","辽源市","通化市","白山市","松原市","白城市","延边朝鲜族自治州"}
    };
    public Gson gson=new Gson();
    public int[] provinceCarAmount=new int[16];

    public String resolveJson(String str){
        JsonBean[] jsonBeans=gson.fromJson(str, JsonBean[].class);
        JsonBean temp_bean=jsonBeans[jsonBeans.length-1];
        Key_Order[] key_orders=gson.fromJson(temp_bean.result,Key_Order[].class);

        for (Key_Order k:key_orders) {
            char c1=k.key.charAt(17);
            char c2=k.key.charAt(18);
            int i=Integer.valueOf(k.orderId[0]);
            switch (c1){
                case '0':
                    switch(c2){
                        case '1':
                            provinceCarAmount[0]=i;
                            break;
                        case '2':
                            provinceCarAmount[1]=i;
                            break;
                        case '5':
                            provinceCarAmount[2]=i;
                            break;
                        case '6':
                            provinceCarAmount[3]=i;
                            break;
                        case '7':
                            provinceCarAmount[4]=i;
                            break;
                        case '8':
                            provinceCarAmount[5]=i;
                            break;
                        case '9':
                            provinceCarAmount[6]=i;
                            break;
                    }
                    break;
                case '1':
                    switch(c2){
                        case '1':
                            provinceCarAmount[7]=i;
                            break;
                        case '2':
                            provinceCarAmount[8]=i;
                            break;
                        case '3':
                            provinceCarAmount[9]=i;
                            break;
                        case '4':
                            provinceCarAmount[10]=i;
                            break;
                        case '5':
                            provinceCarAmount[11]=i;
                            break;
                        case '6':
                            provinceCarAmount[12]=i;
                            break;
                        case '7':
                            provinceCarAmount[13]=i;
                            break;
                        case '8':
                            provinceCarAmount[14]=i;
                            break;
                        case '9':
                            provinceCarAmount[15]=i;
                            break;
                    }
                    break;
            }
        }

        String all="{";

        String data_lines;
        int districtLength=16;

        data_lines="[";

        for(int i=0;i<districtLength-1;i++)
        {
            data_lines+="{\\\"name\\\":\\\""+districtNames[0][i]+"\\\", \\\"value\\\":"+provinceCarAmount[i]+"},";
        }

        data_lines+="{\\\"name\\\":\\\""+districtNames[0][districtLength-1]+"\\\", \\\"value\\\":"+provinceCarAmount[districtLength-1]+"}]";

        all+="\"data"+"\":\""+data_lines+"\",\r\n";

        all+="}";
        return all;
    }

//    public static void main(String[] args){
//        String a,b;
//        a="[\r\n {\r\n\"time\": \"2018-09-15T06:50:22.000Z\",\r\n\"result\": \"[{\\\"key\\\":\\\"10-order-all-110111-*\\\",\\\"orderId\\\":[\\\"000003\\\"]},{\\\"key\\\":\\\"10-order-all-110115-*\\\",\\\"orderId\\\":[\\\"000003\\\"]}]\"\r\n },\r\n{\r\n\"time\": \"2018-09-15T06:50:23.000Z\",\r\n\"result\": \"[{\\\"key\\\":\\\"10-order-all-110111-*\\\",\\\"orderId\\\":[\\\"000003\\\"]},{\\\"key\\\":\\\"10-order-all-110115-*\\\",\\\"orderId\\\":[\\\"000003\\\"]}]\"\r\n },\r\n {\r\n\"time\": \"2018-09-15T06:50:24.000Z\",\r\n\"result\": \"[{\\\"key\\\":\\\"10-order-all-110111-*\\\",\\\"orderId\\\":[\\\"000003\\\"]},{\\\"key\\\":\\\"10-order-all-110115-*\\\",\\\"orderId\\\":[\\\"000003\\\"]}]\"\r\n },\r\n {\r\n\"time\": \"2018-09-15T06:50:25.000Z\",\r\n\"result\": \"[{\\\"key\\\":\\\"10-order-all-110111-*\\\",\\\"orderId\\\":[\\\"000003\\\"]},{\\\"key\\\":\\\"10-order-all-110115-*\\\",\\\"orderId\\\":[\\\"000003\\\"]}]\"\r\n },\r\n {\r\n\"time\": \"2018-09-15T06:50:26.000Z\",\r\n\"result\": \"[{\\\"key\\\":\\\"10-order-all-110111-*\\\",\\\"orderId\\\":[\\\"000003\\\"]},{\\\"key\\\":\\\"10-order-all-110115-*\\\",\\\"orderId\\\":[\\\"000003\\\"]}]\"\r\n },\r\n {\r\n\"time\": \"2018-09-15T06:50:27.000Z\",\r\n\"result\": \"[{\\\"key\\\":\\\"10-order-all-110111-*\\\",\\\"orderId\\\":[\\\"000003\\\"]},{\\\"key\\\":\\\"10-order-all-110115-*\\\",\\\"orderId\\\":[\\\"000003\\\"]}]\"\r\n },\r\n {\r\n\"time\": \"2018-09-15T06:50:28.000Z\",\r\n\"result\": \"[{\\\"key\\\":\\\"10-order-all-110111-*\\\",\\\"orderId\\\":[\\\"000003\\\"]},{\\\"key\\\":\\\"10-order-all-110115-*\\\",\\\"orderId\\\":[\\\"000003\\\"]}]\"\r\n },\r\n {\r\n\"time\": \"2018-09-15T06:50:29.000Z\",\r\n\"result\": \"[{\\\"key\\\":\\\"10-order-all-110111-*\\\",\\\"orderId\\\":[\\\"000003\\\"]},{\\\"key\\\":\\\"10-order-all-110115-*\\\",\\\"orderId\\\":[\\\"000003\\\"]}]\"\r\n }\r\n]\r\n";
//        Demo d=new Demo();
//        b=d.resolveJson(a);
//        System.out.println(b);
//    }
}
