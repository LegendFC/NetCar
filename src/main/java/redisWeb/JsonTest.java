package redisWeb;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public class JsonTest {

	public static void main(String[] args) throws JSONException {
		// TODO Auto-generated method stub
	    JSONObject json=new JSONObject();
	    JSONArray jsonMembers = new JSONArray();  
	    JSONObject member1 = new JSONObject();  
	    member1.put("loginname", "zhangfan");  
	    member1.put("password", "userpass");  
	    member1.put("email","10371443@qq.com");  
	    member1.put("sign_date", "2007-06-12");  
	    jsonMembers.put(member1);  
	  
//	    JSONObject member2 = new JSONObject();  
//	    member2.put("loginname", "zf");  
//	    member2.put("password", "userpass");  
//	    member2.put("email","8223939@qq.com");  
//	    member2.put("sign_date", "2008-07-16");  
	    //jsonMembers.put(member2);  
	   
	    json.put("points", jsonMembers);  
	    System.out.println(json.toString());
	    //return json.toString();
	}

}
