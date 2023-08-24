package com.sanhua;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ufida.eip.core.MessageContext;
import com.ufida.eip.exception.EIPException;
import com.ufida.eip.java.IContextProcessor;

import commonj.sdo.DataObject;

public class personAPI implements IContextProcessor {
	//E学平台删除接口
	//地址不能存在数据库里
//	public JSONObject deletePerson(ArrayList<String> mdm_code){
//		
//		JSONObject results = new JSONObject();
//		JSONObject data = new JSONObject();
//		JSONArray mdMappings = new JSONArray();
//		results.put("status", true);
//		String domain = "openapi-adel.yunxuetang.cn";
//		boolean success = true;
//		boolean EStudyStatus = true;
//		String message;
//        Iterator<String> iterator = mdm_code.iterator();
//        //TODO 换成数据库
//        String entityCode = "g_pers_test";
//        int numFail = 0;
//        while(iterator.hasNext()){
//        	String uid = iterator.next();
//    		String url = "https://"+domain+"/v1/udp/public/users/"+uid+"/del";
//    		JSONObject oneResult = new JSONObject();
//    		try{
//    			
//            	URL uri = new URL(url);
//    	        HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
//    	        connection.setRequestMethod("POST");
//    	        connection.setReadTimeout(50000);
//    	        connection.setConnectTimeout(100000);
//    	        connection.setDoInput(true); 
//    			connection.setDoOutput(true);
//            	String Authorization = getToken.getTokenFromDb("person");
//    			connection.setRequestProperty("Authorization", Authorization);
//    			connection.setRequestProperty("Content-type", "application/json; charset=utf-8");
//    	        connection.connect(); 
//    			InputStream is = connection.getInputStream();
//    	        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8")); 
//    			String strRead;
//    			StringBuffer sbf = new StringBuffer(); 
//    			while ((strRead = reader.readLine()) != null) {
//    				System.out.println(strRead);
//    				sbf.append(strRead); 
//    				sbf.append("\n");
//    			}
//    	        //用完token恢复状态
//    	        AsyncAccessToken.personUsing = false;
//    			reader.close();
//    			JSONObject result = new JSONObject(sbf.toString());
//    			if(!result.getString("msg").equals("success")){
//    				success = false;
//    				oneResult.put("mdmCode", uid);
//    				oneResult.put("entityCode", entityCode);
//    				oneResult.put("success", false);
//    				oneResult.put("message", "数据删除时出错，错误信息为："+result.getString("subMsg"));
//    				mdMappings.put(oneResult);
//    				numFail++;
//    			}
//    			else{
//    				oneResult.put("mdmCode", uid);
//    				oneResult.put("entityCode", entityCode);
//    				oneResult.put("success", true);
//    				oneResult.put("message", "数据删除成功！");
//    				mdMappings.put(oneResult);
//    			}
//    		}
//    		catch(Exception e){
//    			if(results.getBoolean("status")){
//    				EStudyStatus = false;
//	    			results.put("status", false);
//	    			results.put("location", "调用E学平台删除人员接口时出现错误");
//	    			results.put("description", e.getMessage());
//	    			results.put("detail", e.getStackTrace());
//    			}
//    		}
//        }
//        data.put("success", success);
//        data.put("mdMappings", mdMappings);
//        if(numFail != 0){
//        	message = "删除人员时出错，共"+Integer.toString(numFail)+"条数据删除失败";
//        }
//        else{
//        	message = "消费系统消费这批数据成功！";
//        }
//        data.put("message", message);
//        results.put("data", data);
//        results.put("status", EStudyStatus);
//		return results;
//	}
	//向E学平台同步信息，不论成功与否都有status字段，true为成功，false为失败，失败时会有错误原因和详细描述，成功时返回数据
	public JSONObject EStudySync(JSONArray data){
		//TODO 这里将域名拼接，正式平台记得看是域名还是url
		JSONObject results = new JSONObject();
		try{
			String domain = "openapi-adel.yunxuetang.cn";
			String url = "https://"+ domain +"/v1/udp/public/users/sync/batch";
	    	URL uri = new URL(url);


	    	//TODO 这里用E学查岗位的token
	        //组装查询条件
	        JSONObject queryCondition = new JSONObject();
	        queryCondition.put("datas", data);
	        //建立连接，设置请求头
	        HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
	        connection.setRequestMethod("POST");
	        connection.setReadTimeout(50000);
	        connection.setConnectTimeout(100000);
	        connection.setDoInput(true); 
			connection.setDoOutput(true);
			//使用token前将状态置为1
	    	String Authorization = getToken.getTokenFromDb();
			connection.setRequestProperty("Authorization", Authorization);
			connection.setRequestProperty("Content-type", "application/json; charset=utf-8");
	        connection.connect(); 
	        //TODO 
	        System.out.println(queryCondition.toString());
	        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(),"UTF-8"); 
	        writer.write(queryCondition.toString());
			writer.flush();
	        
			InputStream is = connection.getInputStream();
	        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8")); 
			String strRead;
			StringBuffer sbf = new StringBuffer(); 
			while ((strRead = reader.readLine()) != null) {
				System.out.println(strRead);
				sbf.append(strRead);
				sbf.append("\n"); 
			}
	        //用完token恢复状态
	        AsyncAccessToken.personUsing = false;
			reader.close();
			results.put("data", new JSONObject(sbf.toString()));
			results.put("status", true);
			
		}
		catch(Exception e){
			results.put("status", false);
			results.put("location", "调用E学平台接口时出现错误");
			results.put("description", e.getMessage());
			results.put("detail", e.getStackTrace());
		}
		System.out.println(results.toString());
		return results;
	}
	//这个函数用于获取员工直属经理的主数据编码
    //TODO 是否存在无上级岗位的人员，需要询问 2023-8-5
	public JSONObject getManagerIds(ArrayList<String> pkPostCodes){
        JSONObject results = new JSONObject();
		ArrayList<String> resultArray = new ArrayList<String>();
		try{
			String url = "http://10.10.201.40/iuapmdm/cxf/mdmrs/newcenter/newCenterService/queryListMdByMdmCodes";
	    	URL uri = new URL(url);
	    	
	    	//TODO 这里用E学查岗位的token
	        String mdmtoken = "9d2eeb88-c162-4214-9e86-e9c0fea9c6b2";
	        String tenantid = "tenant";
	        String systemCode = "EStudy-position";
	        String gdCode = "g_post_test";
	        //组装查询条件
	        JSONObject queryCondition = new JSONObject();
	        queryCondition.put("systemCode", systemCode);
	        queryCondition.put("gdCode", gdCode);
	        queryCondition.put("pageable", false);
	        queryCondition.put("pageIndex", 1);
	        queryCondition.put("pageSize", 30000);
	        queryCondition.put("codes", pkPostCodes);
	        //建立连接，设置请求头
	        HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
	        connection.setRequestMethod("POST");
	        connection.setReadTimeout(50000);
	        connection.setConnectTimeout(100000);
	        connection.setDoInput(true); 
			connection.setDoOutput(true); 
			connection.setRequestProperty("mdmtoken", mdmtoken);
			connection.setRequestProperty("tenantid", tenantid);
			connection.setRequestProperty("Content-type", "application/json");
	        connection.connect(); 
	        //TODO 
	        System.out.println(queryCondition.toString());
	        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(),"UTF-8"); 
	        writer.write(queryCondition.toString()); 
			writer.flush();
	        
			InputStream is = connection.getInputStream();
	        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8")); 
			String strRead;
			StringBuffer sbf = new StringBuffer(); 
			while ((strRead = reader.readLine()) != null) {
				System.out.println(strRead);
				sbf.append(strRead); 
				sbf.append("\n"); 
			}
			reader.close();
			JSONObject result = new JSONObject(sbf.toString());
			//这里与下面区分，E学有所有的岗位分类！ 2023-8-3 如果后续有扩充规则记得添加异常处理
            //默认每个人都能查询到上级岗位的岗位编号，如果有错误将在下面处理
			JSONArray resultData = result.getJSONArray("data");
			JSONObject pkMapping = new JSONObject();
			ArrayList<String> personCondition = new ArrayList<String>();
			personCondition.add("and");
			personCondition.add("in");
			for(int i = 0; i < resultData.length(); i++){
				JSONObject oneResultData = resultData.getJSONObject(i);
				String pk_mdm_code = oneResultData.getString("mdm_code");
				String pk_post_name = oneResultData.getString("code");
				pkMapping.put(pk_mdm_code, pk_post_name);
				personCondition.add("'" + pk_post_name + "'");
			}
			//到这里查询到了所有的上级岗位的岗位编码，下面用岗位编码查询该批岗位的主数据编码

			//TODO 这一次查询用E学人员的信息
			try{
				url = "http://10.10.201.40/iuapmdm/cxf/mdmrs/newcenter/newCenterService/queryListMdByConditions";
		    	uri = new URL(url);
		    	
		    	//TODO 这里用E学查岗位的token
		        mdmtoken = "272f9d08-5b39-40d1-a53f-8b45a8b7c89a";
		        tenantid = "tenant";
		        systemCode = "EStudy-person";
		        gdCode = "g_pers_test";
		        //组装查询条件
		        queryCondition = new JSONObject();
		        JSONObject conditions = new JSONObject();
		        conditions.put("pk_post#code", personCondition);
		        queryCondition.put("systemCode", systemCode);
		        queryCondition.put("gdCode", gdCode);
		        queryCondition.put("pageable", false);
		        queryCondition.put("pageIndex", 1);
		        queryCondition.put("pageSize", 30000);
		        queryCondition.put("conditions", conditions);
		        //建立连接，设置请求头
		        connection = (HttpURLConnection) uri.openConnection();
		        connection.setRequestMethod("POST");
		        connection.setReadTimeout(50000);
		        connection.setConnectTimeout(100000);
		        connection.setDoInput(true); 
				connection.setDoOutput(true); 
				connection.setRequestProperty("mdmtoken", mdmtoken);
				connection.setRequestProperty("tenantid", tenantid);
				connection.setRequestProperty("Content-type", "application/json");
		        connection.connect(); 
		        System.out.println(queryCondition.toString());
		        writer = new OutputStreamWriter(connection.getOutputStream(),"UTF-8"); 
		        writer.write(queryCondition.toString()); 
				writer.flush();
		        
				is = connection.getInputStream();
		        reader = new BufferedReader(new InputStreamReader(is, "UTF-8")); 
				sbf = new StringBuffer(); 
				while ((strRead = reader.readLine()) != null) {
					System.out.println(strRead);
					sbf.append(strRead); 
					sbf.append("\n"); 
				}
				reader.close();
                //这里的result即为调用人员查询接口后返回的所有数据，需要注意有些人员没有上级，所以返回的数据长度可能比送入的少
				result = new JSONObject(sbf.toString());
			}
			catch(Exception e){
                results.put("status", false);
                results.put("location", "调用主数据人员接口时出现错误");
                results.put("description", e.getMessage());
                results.put("detail", e.getStackTrace());
                return results;
			}
			//此处try用于处理零数据异常，即所有人员的上级都不需要同步
            //当查询到的人员都无上级时不影响同步，只需要将其对应的上级主数据置为null即可
			JSONArray personResult = new JSONArray();
			try{
				personResult = results.getJSONArray("data");
			}
			catch (Exception e){
				System.out.println("此批人员都无上级！");
			}
            //这里获得岗位编码和主数据编码的映射
            //至此有从人员主数据编码到上级岗位编码，岗位编码到主数据编码的映射，对应查找即可
			JSONObject codeMdmCodeMapping = new JSONObject();
			for(int i = 0; i < personResult.length(); i++){
				JSONObject oneData = personResult.getJSONObject(i);
				String onePostCode = oneData.getString("pk_post_name");
				String oneMdmCode = oneData.getString("mdm_code");
				codeMdmCodeMapping.put(onePostCode, oneMdmCode);
			}
			
			Iterator<String> iterator = pkPostCodes.iterator();
			while(iterator.hasNext()){
				//此处try用于处理人员上级无需同步的异常
				try{
					String pkPostCode = iterator.next();
					String managerMdmCode = codeMdmCodeMapping.getString(pkMapping.getString(pkPostCode));
					resultArray.add(managerMdmCode);
				}
				catch (Exception e){
					resultArray.add(null);
				}
			}
			results.put("status", true);
			results.put("data", resultArray);
		}
		catch (Exception e){
			results.put("status", false);
			results.put("location", "调用主数据岗位查询接口时出现错误");
			results.put("description", e.getMessage());
			results.put("detail", e.getStackTrace());
		}
		return results;
	}
	//用于从身份证号转生日
	public String getBirthDay(String id){
		String birthdayDate = id.substring(6, 14);
		String birthdayYear = birthdayDate.substring(0, 4);
		String birthdayMonth = birthdayDate.substring(4, 6);
		String birthdayDay = birthdayDate.substring(6, 8);
		String birthday = birthdayYear + "-" + birthdayMonth + "-" + birthdayDay;
		return birthday;
	}
	@Override
	public String handleMessageContext(MessageContext arg0) throws EIPException {
		// TODO Auto-generated method stub
        // TODO
		JSONObject personData = new JSONObject();
		JSONObject personMdmData = new JSONObject(arg0.getBodyData().toString());
		String entityCode = personMdmData.getString("mdType");
		JSONArray personMasterData = new JSONArray(personMdmData.getString("masterData"));
		//同步名单
		System.out.println(personMasterData.toString());
		JSONArray dataToEStudy = new JSONArray();
		//离职名单
		ArrayList<String> dataDelEstudy = new ArrayList<String>();
		//这里区分离职与否，并存放在职人员的上级岗位主数据编码
		ArrayList<String> syncMdmCodes = new ArrayList<String>();
		ArrayList<String> pkPostCodes = new ArrayList<String>();
		ArrayList<String> mdmCodes = new ArrayList<String>();
		
		Iterator<Object> iterator = personMasterData.iterator();
		//TODO 人员一小时只允许同步一次，需要改为递归式同步 2023-8-4
//		//第一次同步
//		while(iterator.hasNext()){
//			JSONObject onePersonMasterData = (JSONObject) iterator.next();
//			String peopleStatus = onePersonMasterData.getJSONObject("peop_status_entity").getString("code");
//			String mdm_code = onePersonMasterData.getString("mdm_code");
//			mdmCodes.add(mdm_code);
//			if(!peopleStatus.equals("01")){
//				dataDelEstudy.add(mdm_code);
//				iterator.remove();;//这里将不在职的员工移除
//				continue;
//			}
//			String pk_post_code = onePersonMasterData.getJSONObject("pk_post_entity").getString("pk_post");
//			syncMdmCodes.add(mdm_code);
//			pkPostCodes.add(pk_post_code);
//		}
        //如果获取结果不对就直接返回

//		
//		for(int i = 0; i < personMasterData.length(); i++){
//			JSONObject oneData = new JSONObject();
//			JSONObject onePersonMasterData = personMasterData.getJSONObject(i);
//			
//			String thirdUserId = syncMdmCodes.get(i);
//			String username = onePersonMasterData.getString("last_code");
//			String fullname = onePersonMasterData.getString("name");
//			
//			oneData.put("thirdUserId",thirdUserId);
//			oneData.put("username",username);
//			oneData.put("fullname",fullname);
//			dataToEStudy.put(oneData);
//		}
//		JSONObject result = EStudySync(dataToEStudy);
//		System.out.println(result.toString());
//		//String successOrNot = result.getString("msg");

		//第二次同步
		//不需要调用删除接口 2023.8.24
		iterator = personMasterData.iterator();
		while(iterator.hasNext()){
			JSONObject onePersonMasterData = (JSONObject) iterator.next();
//			String peopleStatus = onePersonMasterData.getJSONObject("peop_status_entity").getString("code");
			String mdm_code = onePersonMasterData.getString("mdm_code");
			mdmCodes.add("mdm_code");
//			if(!peopleStatus.equals("01")){
//				dataDelEstudy.add(mdm_code);
//				iterator.remove();;//这里将不在职的员工移除
//				continue;
//			}
			String pk_post_code = onePersonMasterData.getJSONObject("pk_post_entity").getString("pk_post");
			syncMdmCodes.add(mdm_code);
			pkPostCodes.add(pk_post_code);
		}
//		//调用删除接口
        boolean success;
        String message = "";
//        boolean delStatus = true;
//        boolean delSuccess = true;
        JSONArray mdMappings = new JSONArray();
//		if(dataDelEstudy.size()>0){
//			JSONObject delResults = deletePerson(dataDelEstudy);
//	        delStatus = delResults.getBoolean("status");
//	        
//	        JSONObject delData = delResults.getJSONObject("data");
//	        delSuccess = delData.getBoolean("success");
//	        JSONArray delMdMappings = delData.getJSONArray("mdMappings");
//	        String delMessage = delData.getString("message");
//	
//
//	        
//	        
//			if(!delStatus){
//				message += delMessage + "\n";
//				message += "错误位置：" + delResults.getString("location") + "\n";
//				message += "错误原因：" + delResults.getString("description") + "\n";
//				message += "详细信息：" + delResults.getString("detail") + "\n";
//			}
//			else{
//				message += "人员删除成功！\n";
//			}
//			for(int i = 0; i < delMdMappings.length(); i++){
//				mdMappings.put(delMdMappings.get(i));
//			}
//		}
		//删除到此结束
		
		
		//JSONObject managerCodesResult = getManagerIds(pkPostCodes);

        
        
		//if(managerCodesResult.getBoolean("status")){
            
        //}
		dataToEStudy = new JSONArray();
		for(int i = 0; i < personMasterData.length(); i++){
			JSONObject oneData = new JSONObject();
			JSONObject onePersonMasterData = personMasterData.getJSONObject(i);
			String peopleStatus = onePersonMasterData.getJSONObject("peop_status_entity").getString("code");

			String thirdUserId = syncMdmCodes.get(i);
			String username = onePersonMasterData.getString("last_code");
			String fullname = onePersonMasterData.getString("name");
			String userNo = onePersonMasterData.getString("last_code");
			String hireDate = onePersonMasterData.getString("entry_time");
			String gender = onePersonMasterData.getString("sex");
			int genderInd = 0;
			if(gender.equals("01")){
				genderInd = 1;
			}
			else if(gender.equals("02")){
				genderInd = 2;
			}
			String birthday = getBirthDay(onePersonMasterData.getString("id_munb"));
			String email = onePersonMasterData.getString("mail_addr");
			String positionThirdId = onePersonMasterData.getJSONObject("pk_post_entity").getString("mdm_code");
			
			String deptThirdId = onePersonMasterData.getJSONObject("pk_post_entity").getString("pk_org");
			//这两条需要测试数据，暂时没有
			//String parttimeDeptThirdIds = "";
			//String parttimePositionThirdIds = "";
			//String managerThirdId = managerCodes.get(i);
			oneData.put("thirdUserId",thirdUserId);
			oneData.put("username",username);
			oneData.put("fullname",fullname);
			oneData.put("userNo",userNo);
			oneData.put("hireDate",hireDate);
			oneData.put("gender",genderInd);
			oneData.put("birthday",birthday);
			oneData.put("email",email);
			oneData.put("positionThirdId",positionThirdId);
			oneData.put("deptThirdId",deptThirdId);
			//oneData.put("parttimePositionThirdIds",parttimePositionThirdIds);
			//oneData.put("parttimeDeptThirdIds",parttimeDeptThirdIds);
			//oneData.put("managerThirdId",managerThirdId);
			if(!peopleStatus.equals("01")){
				oneData.put("status", 0);
			}
			dataToEStudy.put(oneData);
		}
		System.out.println(dataToEStudy.toString());
		JSONObject syncResults = EStudySync(dataToEStudy);
		boolean syncStatus = syncResults.getBoolean("status");
		boolean syncSuccess = true;
		if(!syncStatus){
			message += "错误位置：" + syncResults.getString("location") + "\n";
			message += "错误原因：" + syncResults.getString("description") + "\n";
			message += "详细信息：" + syncResults.getString("detail") + "\n";
			for(int i = 0; i < syncMdmCodes.size(); i++){
				JSONObject oneSyncResult = new JSONObject();
				oneSyncResult.put("mdmCode", syncMdmCodes.get(i));
				oneSyncResult.put("entityCode", entityCode);
				oneSyncResult.put("message", "该条主数据消费失败");
				oneSyncResult.put("success", false);
			}
		}
		
		else{
			JSONObject syncData = syncResults.getJSONObject("data");
			System.out.println(syncData.toString());
			if(!syncData.getString("msg").equals("success")){
				syncSuccess = false;
			}
			JSONObject syncDetail = syncData.getJSONObject("data");
			JSONArray syncFails = syncDetail.getJSONArray("failThirdIds");
			ArrayList<String> syncFailIds = new ArrayList<String>();
			if(syncFails.length() == 0){
				message += "同步数据成功";
			}
			else{
				message += "同步数据不成功";
				
				for(int i = 0; i < syncFails.length(); i++){
					syncFailIds.add(syncFails.getString(i));
				}
			}
			for(int i = 0; i < syncMdmCodes.size(); i++){
				JSONObject oneSyncResult = new JSONObject();
				oneSyncResult.put("mdmCode", syncMdmCodes.get(i));
				oneSyncResult.put("entityCode", entityCode);
				if(syncFailIds.contains(syncMdmCodes.get(i))){
					oneSyncResult.put("message", "该条主数据消费失败，错误信息为："+syncData.get("subMsg"));
					oneSyncResult.put("success", false);
				}
				else{
					oneSyncResult.put("message", "该条主数据消费成功");
					oneSyncResult.put("success", true);
				}
				mdMappings.put(oneSyncResult);
			}
			
		}
		//同步到此结束
		success = syncStatus&syncSuccess;
		personData.put("success", success);
		personData.put("message", message);
		personData.put("mdMappings", mdMappings);
		DataObject body = arg0.getBody();
		
		body.set("personData",personData.toString());
		return null;
	}

}
