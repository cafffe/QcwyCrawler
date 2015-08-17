package com.kmgh;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class MyCrawler {
		public static void main(String[] args) throws Exception {  
			final SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("yyyyMMddHHmmss");
			ValuesObj valuesObj = new ValuesObj();
			/*getZlzpResumeId(valuesObj, simpleDateFormat);//use to utilize the valuesObj
			Map<String, List<RenCaiObj>> map=getRenCaiList("1", "java", "", "", "", "", valuesObj);
			for(int i=0;i<map.get("renCaiObjList").size();i++){
				System.out.println(map.get("renCaiObjList").get(i).getAge());
			}*/
			/*
			 *获取前程无忧登陆结果
			 */
			LoginResultObj loginResultObj=LoginQcwy("快马过河", "kmgh137", "km,2015");
			System.out.println(loginResultObj.getJobsnum());
		}
		public static void getZlzpResumeId(ValuesObj valuesObj,SimpleDateFormat simpleDateFormat){
			LoginResultObj loginResultObj = new LoginResultObj();
			ZhaoPinHttpUtil ahaoPinHttpUtil = new ZhaoPinHttpUtil();
			CloseableHttpClient httpClient = ahaoPinHttpUtil.getZhaoPinHttpClient();
			loginResultObj.setResult(false);
			HttpPost post = null;
			HttpEntity he = null;
			HttpEntity entity = null;
			HttpResponse httpresponse = null;
			List<NameValuePair> loginParames = null;
			String postresult = "";
			try {
				//if ("".equals(valuesObj.loginPointId)) {
					loginParames = new ArrayList<NameValuePair>();
					loginParames
							.add(new BasicNameValuePair("LoginName", "ep26339738qi"));
					loginParames.add(new BasicNameValuePair("Password", URLEncoder.encode("kuaimaguohe,2015")));
					loginParames.add(new BasicNameValuePair("CheckCode",
							valuesObj.checkcodestr));
					post = new HttpPost("https://passport.zhaopin.com/org/login");
					post.setHeader("Accept",
									"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
					post.setHeader("Accept-Encoding", "gzip, deflate, plain");
					post.setHeader("Accept-Language", "en-US,en;q=0.5");
					post.setHeader("Cache-Control", "max-age=0");
					post.setHeader("Connection", "keep-alive");
					post.setHeader("Content-Type",
							"application/x-www-form-urlencoded");

					post.setHeader("User-Agent",
									"Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:28.0) Gecko/20100101 Firefox/28.0");
					he = new UrlEncodedFormEntity(loginParames,
							Consts.UTF_8);// new
					// UrlEncodedFormEntity(loginParames);
					post.setEntity(he);
					httpresponse = httpClient.execute(post);
					entity = httpresponse.getEntity();
					postresult = EntityUtils.toString(entity, "gb2312");
					//System.out.println(postresult);
					int timenum = 0;
					if ("".equals(valuesObj.loginPointId)) {
						while ((postresult
								.indexOf("%e9%aa%8c%e8%af%81%e7%a0%81%e9%94%99%e8%af%af%ef%bc%81") > 0 || postresult
								.indexOf("验证码错误") > 0||postresult
								.indexOf("请输入验证码") > 0)
								&& timenum < 20) {
							System.out.println("findind..."+timenum);
							getImgCheckNum(valuesObj,simpleDateFormat);
							System.out.println(valuesObj.checkcodestr);
							loginParames = new ArrayList<NameValuePair>();
							loginParames.add(new BasicNameValuePair("LoginName", "ep26339738qi"));
							loginParames.add(new BasicNameValuePair("Password", "kuaimaguohe,2015"));
							loginParames.add(new BasicNameValuePair("CheckCode",valuesObj.checkcodestr));
							post = new HttpPost("https://passport.zhaopin.com/org/login");
							post.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
							post.setHeader("Accept-Encoding", "gzip, deflate, plain");
							post.setHeader("Accept-Language", "en-US,en;q=0.5");
							post.setHeader("Cache-Control", "max-age=0");
							post.setHeader("Connection", "keep-alive");
							post.setHeader("Content-Type","application/x-www-form-urlencoded");
							post.setHeader("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:28.0) Gecko/20100101 Firefox/28.0");
							he=new UrlEncodedFormEntity(loginParames, Consts.UTF_8);// new
							// UrlEncodedFormEntity(loginParames);
							post.setEntity(he);
							httpresponse = httpClient.execute(post);
							entity = httpresponse.getEntity();
							postresult = EntityUtils.toString(entity, "gb2312");
							//System.out.println(postresult);
							timenum++;
						}
					}
					if (((postresult
							.indexOf("%e9%aa%8c%e8%af%81%e7%a0%81%e9%94%99%e8%af%af%ef%bc%81") > 0 || postresult
							.indexOf("验证码错误") > 0||postresult
							.indexOf("请输入验证码") > 0)
							&& timenum == 20)&&"".equals(valuesObj.loginPointId)) {
						System.out.println(1111);
						loginResultObj.setResult(false);
						loginResultObj.setPostresult("登录失败，请重新登录！");
					} else {
						// 用户名密码错误
						if ((postresult
								.indexOf("%e9%aa%8c%e8%af%81%e7%a0%81%e9%94%99%e8%af%af%ef%bc%81") > 0||postresult
								.indexOf("用户名或密码错误") > 0)&&"".equals(valuesObj.loginPointId)) {
							System.out.println(2222);
							// 用户名密码错误
							loginResultObj.setResult(false);
							loginResultObj.setPostresult("用户名密码错误！");
						} else {
							if ("".equals(valuesObj.loginPointId)) {
								System.out.println(3333);
								post = new HttpPost(
										"http://rd2.zhaopin.com/s/loginmgr/loginproc_new.asp");

								HttpResponse gethttpresponse1 = httpClient
										.execute(post);
								HttpEntity getentity1 = gethttpresponse1.getEntity();

								postresult = EntityUtils.toString(getentity1, "gb2312");
								Header[] headers = gethttpresponse1
										.getHeaders("set-cookie");
								String RDsUserInfo = "";
								for (int i = 0; i < headers.length; i++) {
									String cookie = headers[i].toString();
									if (cookie.indexOf("RDsUserInfo=") > 0) {
										int start = cookie.indexOf("RDsUserInfo=") + 12;
										int end = cookie.indexOf(";", start);
										RDsUserInfo = RDsUserInfo + "RDsUserInfo="
												+ cookie.substring(start, end);
									}
								}
								post = new HttpPost(
										"http://rd2.zhaopin.com/s/loginmgr/choose.asp");
								RDsUserInfo = RDsUserInfo
										+ ";strloginusertype=4;PositionPub_PriorityRule=1;urlfrom2=121123540;adfcid2=topbanner;adfbid2=0;welfaretab=10000";
								post.setHeader("Cookie", RDsUserInfo);
								HttpResponse gethttpresponse = httpClient.execute(post);
								HttpEntity getentity = gethttpresponse.getEntity();
								postresult = EntityUtils.toString(getentity, "gb2312");
								System.out.println(postresult);
								// 从result中获取跳转URL
								//
								int idstart = postresult.indexOf("id=") + 3;
								int idend = postresult.indexOf("&", idstart);
								valuesObj.loginPointId = postresult.substring(idstart,
										idend);
								post = new HttpPost(
										"http://rd2.zhaopin.com/s/loginmgr/loginpoint.asp?id="
												+ valuesObj.loginPointId + "&BkUrl=");
								post.setHeader("Cookie", RDsUserInfo);
								HttpResponse gethttpresponse2 = httpClient
										.execute(post);
								HttpEntity getentity2 = gethttpresponse2.getEntity();
								postresult = EntityUtils.toString(getentity2, "gb2312");

								Header[] headers2 = gethttpresponse2
										.getHeaders("set-cookie");
								for (int i = 0; i < headers2.length; i++) {
									String cookie = headers2[i].toString();
									if (cookie.indexOf("RDsUserInfo=") > 0) {
										int start = cookie.indexOf("RDsUserInfo=") + 12;
										int end = cookie.indexOf(";", start);
										RDsUserInfo = "RDsUserInfo="
												+ cookie.substring(start, end);
									}

								}
								RDsUserInfo = RDsUserInfo
										+ ";strloginusertype=4;PositionPub_PriorityRule=1;urlfrom2=121123540;adfcid2=topbanner;adfbid2=0;welfaretab=10000";
								valuesObj.rd2cookiestr = RDsUserInfo;
								RDsUserInfo = RDsUserInfo + ";isNewUser=1";
								RDsUserInfo = RDsUserInfo + ";cgmark=2";
								valuesObj.rencaicookiestr=RDsUserInfo;
								valuesObj.rencaicookiestr = valuesObj.rencaicookiestr+";Home_ResultForCustom_searchFrom=custom;Home_ResultForCustom_isOpen=true;Home_ResultForCustom_orderBy=DATE_MODIFIED%2C1;SearchHead_Erd=rd;Home_ResultForCustom_displayMode_JR152722905R90000000000_1=1"
								+";pageReferrInSession=http%3A//jobads.zhaopin.com/Position/PositionManage;dywem=95841923.y;" +
								 		"__xsptplus30=30.17.1431316853.1431316868.2%234%7C%7C%7C%7C%7C%23%236FxFrrcZFXFF2LRJ4Yr9jydJnSejF3Q9%23;" +
								 		"dywez=95841923.1435043849.108.28.dywecsr=rdsearch.zhaopin.com|dyweccn=(referral)|dywecmd=referral|dywectr=undefined|dywecct=/home/resultforcustom;" +
								 		"__zpWAM=1434011024152.400411.1434873614.1435043861.8;" +
								 		"lastchannelurl=http%3A//rd2.zhaopin.com/portal/myrd/regnew.asp%3Fza%3D2;" +
								 		"SearchHistory_StrategyId_1=%2fHome%2fResultForCustom%3fSF_1_1_1%3d%25e9%2594%2580%25e5%2594%25ae%26SF_1_1_27%3d0%26orderBy%3dDATE_MODIFIED%252c1%26exclude%3d1";
								RDsUserInfo = RDsUserInfo + ";fktADClickFlag-success=Y";
								RDsUserInfo = RDsUserInfo
										+ ";pageReferrInSession=http%3A//rd2.zhaopin.com/consume/rewardview";
								valuesObj.jobcookiestr = RDsUserInfo;
							}
							post = new HttpPost("http://rd2.zhaopin.com/s/homepage.asp");
							post.setHeader("Cookie", valuesObj.rencaicookiestr);
							HttpResponse gethttpresponse3 = httpClient.execute(post);
							HttpEntity getentity3 = gethttpresponse3.getEntity();
							postresult = EntityUtils.toString(getentity3, "gb2312");
							
							System.out.println(postresult);
							
							if(postresult.indexOf("/s/loginmgr/expire.asp")>0){
								valuesObj.loginPointId="";
								loginResultObj.setResult(false);
							}else{
								// 获取剩余简历数 resumesnum
								int resumesnumstart = postresult
										.indexOf("<li>剩余下载数：<span class=\"number\"><b>") + 34;
								int resumesnumend = postresult.indexOf("</",
										resumesnumstart);
								String resumesnum = postresult.substring(resumesnumstart,
										resumesnumend);
								loginResultObj.setResumesnum(resumesnum);

								// 获取剩余职位数jobsnum

								int jobsnumstart = postresult
										.indexOf("<li>还可发布职位：<span class=\"number\">") + 32;
								int jobsnumend = postresult.indexOf("</", jobsnumstart);
								String jobsnum = postresult.substring(jobsnumstart,
										jobsnumend);
								loginResultObj.setJobsnum(jobsnum);
								loginResultObj.setResult(true);
							}
						}
					}
				//}
			}catch (Exception e) {
				if (post != null) {
					post.abort();
				}
				e.printStackTrace();
			} finally {
				if (post != null) {
					post.abort();
				}
			}
		}
		public static void getImgCheckNum(ValuesObj valuesObj,SimpleDateFormat simpleDateFormat) throws ClientProtocolException, IOException {
			
			ZhaoPinHttpUtil ahaoPinHttpUtil = new ZhaoPinHttpUtil();
			CloseableHttpClient httpClient = ahaoPinHttpUtil.getZhaoPinHttpClient();
			byte[] data = new byte[1024];
			int len = 0;
			
			 HttpGet get = new HttpGet("https://passport.zhaopin.com/checkcode/imgrd?r="+simpleDateFormat.format(new Date()));
	         HttpResponse httpResponse = httpClient.execute(get);
	         //String temp_path = FileUtil.getFilePath("config.properties", "temp_path");
	         File imgfile = new File("E:/1.jpg");
			OutputStream out = new FileOutputStream(imgfile);
			try {
//				out = response.getOutputStream();
				 if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
						InputStream is = httpResponse.getEntity().getContent();
						while ((len = is.read(data)) != -1) {
							out.write(data, 0, len);
			            }
						
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				if(out!=null){
					try {
						out.flush();
					} catch (IOException e) {
//						 TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			ImagePreProcess2 imgutile = new ImagePreProcess2();
			try {
				valuesObj.checkcodestr = imgutile.getAllOcr(imgfile);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//页面，关键词，公司名称，更新日期，工作年限，学历
		public static Map getRenCaiList(String pageIndex, String gjc,
				String gsmc, String gxrq, String gznx, String xl,ValuesObj valuesObj) throws UnsupportedEncodingException {
			StringBuffer url = new StringBuffer();
			url.append("http://rdsearch.zhaopin.com/Home/ResultForCustom?orderBy=DATE_MODIFIED%2C1&SF_1_1_27=0&exclude=1");
			if(null!= pageIndex && !"".equals(pageIndex)){
				url.append("&pageIndex=").append(pageIndex);
			}
			if(null!=gjc && !"".equals(gjc) ){
				url.append("&SF_1_1_1=").append(URLEncoder.encode(gjc, "UTF-8"));
			}
			if(null!=gsmc && !"".equals(gsmc) ){
				url.append("&SF_1_1_25=COMPANY_NAME_ALL:").append(URLEncoder.encode(gsmc, "UTF-8"));
			}
			if(null!=gxrq && !"".equals(gxrq) ){
				url.append("&SF_1_1_7=").append(URLEncoder.encode(gxrq, "UTF-8"));
			}
			if(null!=gznx && !"".equals(gznx) ){
				url.append("&SF_1_1_4=").append(URLEncoder.encode(gznx, "UTF-8"));
			}
			if(null!=xl && !"".equals(xl) ){
				url.append("&SF_1_1_5=").append(URLEncoder.encode(xl, "UTF-8"));
			}
			Map resultMap = new HashMap();
			List<RenCaiObj> renCaiObjList = new ArrayList<RenCaiObj>();
			
			ZhaoPinHttpUtil ahaoPinHttpUtil = new ZhaoPinHttpUtil();
			CloseableHttpClient httpClient = ahaoPinHttpUtil.getZhaoPinHttpClient();
			 HttpPost post = new HttpPost(
					 url.toString());
			 String renCaiObjListpageNum ="";
			 post.setHeader("Referer",url.toString());
			 post.setHeader("Cookie",valuesObj.rencaicookiestr);
			HttpResponse gethttpresponse3;
			try {
				
				gethttpresponse3 = httpClient.execute(post);
				HttpEntity getentity3 = gethttpresponse3.getEntity();
				String postresult = EntityUtils.toString(getentity3, "gb2312");
				
				if(postresult.indexOf("rd-resumelist-pageNum\">")< 0){
					resultMap.put("renCaiObjList", renCaiObjList);
					resultMap.put("renCaiObjListpageNum", "1");
					return resultMap;
				}
				
				int start = postresult.indexOf("rd-resumelist-pageNum\">");
				start = postresult.indexOf("/",start)+1;
				int end = postresult.indexOf("</span>",start);
				renCaiObjListpageNum=postresult.substring(start, end); 
				
				while(postresult.indexOf("data-smpcvid")!=-1){  
					RenCaiObj renCaiObj = new RenCaiObj();
					renCaiObj.setWebsite("智联招聘");
					
					int infoUrlstart = postresult.indexOf("viewOneResume('")+15;
					int infoUrlend = postresult.indexOf("'",infoUrlstart);
					String infoUrl = postresult.substring(infoUrlstart,infoUrlend);
					renCaiObj.setInfo_url(infoUrl);
					//简历编号
					int rIdstart = postresult.indexOf("data-smpcvid=\"")+14;
					int rIdend = postresult.indexOf("\"",rIdstart);
					String rId = postresult.substring(rIdstart,rIdend);
					renCaiObj.setRId(rId);
					//简历名称
					int namestart = postresult.indexOf("resumename=\"")+12;
					int nameend = postresult.indexOf("\"",namestart);
					String name = postresult.substring(namestart,nameend);
					renCaiObj.setName(name);
					//求职意向
					int qzyxstart = postresult.indexOf("class=\"rd-show-btn\"")+19;
					qzyxstart =  postresult.indexOf("<td>" ,qzyxstart)+4;
					int qzyxend =  postresult.indexOf("</td>" ,qzyxstart);
					String qzyx = postresult.substring(qzyxstart,qzyxend);
					renCaiObj.setQzyx(qzyx);
					//学历
					int xlstart = postresult.indexOf("<td>",qzyxend)+4;
					int xlend = postresult.indexOf("</td>",xlstart);
					String xlstr = postresult.substring(xlstart,xlend);
					renCaiObj.setXl(xlstr);
					//性别
					int sexstart = postresult.indexOf("<td>",xlend)+4;
					int sexend = postresult.indexOf("</td>",sexstart);
					String sex = postresult.substring(sexstart,sexend);
					renCaiObj.setSex(sex);
					//年龄
					int agestart = postresult.indexOf("<td>",sexend)+4;
					int ageend = postresult.indexOf("</td>",agestart);
					String age = postresult.substring(agestart,ageend);
					renCaiObj.setAge(age);
					//现居住地
					int xjzdstart = postresult.indexOf("<td>",ageend)+4;
					int xjzdend = postresult.indexOf("</td>",xjzdstart);
					String xjzd = postresult.substring(xjzdstart,xjzdend);
					renCaiObj.setXjzd(xjzd);
					//更新时间
					int gxsjstart = postresult.indexOf("<td>",xjzdend)+4;
					int gxsjend = postresult.indexOf("</td>",gxsjstart);
					String gxsj = postresult.substring(gxsjstart,gxsjend);
					renCaiObj.setGxsj(gxsj);
					//希望月薪
					int qwyxstart = postresult.indexOf("期望月薪：")+5;
					int qwyxend = postresult.indexOf("<",qwyxstart);
					String qwyx = postresult.substring(qwyxstart,qwyxend);
					renCaiObj.setQwyx(qwyx);
					//当前状态
					int dqztstart = postresult.indexOf("当前状态：")+5;
					int dqztend = postresult.indexOf("<",dqztstart);
					String dqzt = postresult.substring(dqztstart,dqztend);
					renCaiObj.setDqzt(dqzt);
					//最近工作
					if (postresult.indexOf("最近工作") > 0) {
						int zjgzstart = postresult.indexOf("最近工作") + 4;
						int zjgzend = postresult.indexOf(
								"<div class=\"resumes-content\">", zjgzstart);
						String zjgz = postresult.substring(zjgzstart, zjgzend);
						while (zjgz.indexOf("<") != -1) {
							int substart = zjgz.indexOf("<", 0);
							int subend = zjgz.indexOf(">", substart);
							zjgz = zjgz.replace(zjgz
									.substring(substart, subend + 1), "");
						}
						renCaiObj.setZjgz(zjgz);
					}
					//学历详情
					int xlxqstart = postresult.indexOf("最高学历")+4;
					int xlxqend = postresult.indexOf("</div>",xlxqstart);
					String xlxq = postresult.substring(xlxqstart,xlxqend);
					while(xlxq.indexOf("<")!=-1){
						int substart = xlxq.indexOf("<",0);
						int subend=xlxq.indexOf(">",substart);
						xlxq = xlxq.replace(xlxq.substring(substart,subend+1),"");
					}
					renCaiObj.setXlxq(xlxq);
					
					postresult = postresult.substring(xlxqend);
					renCaiObjList.add(renCaiObj);
	            }  
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			resultMap.put("renCaiObjList", renCaiObjList);
			resultMap.put("renCaiObjListpageNum", renCaiObjListpageNum);
			return resultMap;
		}
		@SuppressWarnings("finally")
		public static LoginResultObj LoginQcwy(String ctmName ,String userName, String password){
				QcwyHttpUtil qcwyHttpUtil = new QcwyHttpUtil();
				LoginResultObj loginResultObj = new LoginResultObj();
				CloseableHttpClient httpClient = qcwyHttpUtil.getQcwyHttpClient();
				String result="";
				HttpPost post = null;
				try{
				 HttpGet get = new HttpGet("http://ehire.51job.com/MainLogin.aspx");
		         HttpResponse response = httpClient.execute(get);
		         HttpEntity entity = response.getEntity();
		         result =  EntityUtils.toString(entity, "gb2312");
		         String langtype = "";
		         String oldAccessKey = "";
		         
		         if(result.indexOf("hidLangType")>0){
		        	 int start = result.indexOf("name=\"hidLangType\"");
		        	 start = result.indexOf("value=\"",start)+7;
		        	 int end = result.indexOf("\"", start);
		        	 langtype = result.substring(start, end);
		         }
		         
		         if(result.indexOf("hidAccessKey")>0){
		        	 int start = result.indexOf("name=\"hidAccessKey\"");
		        	 start = result.indexOf("value=\"",start)+7;
		        	 int end = result.indexOf("\"", start);
		        	 oldAccessKey = result.substring(start, end);
		         }
		         List<NameValuePair> testloginParames = new ArrayList<NameValuePair>();
		         testloginParames.add(new BasicNameValuePair("ctmName", ctmName));
		         testloginParames.add(new BasicNameValuePair("userName", userName));
		         testloginParames.add(new BasicNameValuePair("password", password));
		         testloginParames.add(new BasicNameValuePair("checkCode", ""));
		         testloginParames.add(new BasicNameValuePair("langtype", langtype));
		         testloginParames.add(new BasicNameValuePair("oldAccessKey",
		                 oldAccessKey));
		         testloginParames.add(new BasicNameValuePair("isRememberMe", "false"));

		    	post = new HttpPost("https://ehirelogin.51job.com/Member/UserLogin.aspx");
		        post.setHeader("Accept",
		    	"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		        post.setHeader("Accept-Encoding", "gzip, deflate, plain");
		        post.setHeader("Accept-Language", "en-US,en;q=0.5");
		        post.setHeader("Cache-Control", "max-age=0");
		        post.setHeader("Connection", "keep-alive");
		        post.setHeader("Content-Type", "application/x-www-form-urlencoded");
		        
		        post.setHeader(
		    	"User-Agent",
		    	"Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:28.0) Gecko/20100101 Firefox/28.0");
		        post.setHeader("Host", "ehirelogin.51job.com");
		        post.setHeader("Origin", "http://ehire.51job.com/");
		        post.setHeader("Referer", "http://ehire.51job.com/");
		        HttpEntity he = new UrlEncodedFormEntity(testloginParames, Consts.UTF_8);//new UrlEncodedFormEntity(loginParames);
		        post.setEntity(he);
		         
		        HttpResponse response2 = httpClient.execute(post);
		        HttpEntity entity2 = response2.getEntity();
		        result = EntityUtils.toString(entity2, "gb2312");
		        Header[] headers = response2.getHeaders("set-cookie");
				String NET_SessionId="";
				for (int i = 0; i < headers.length; i++) {
					String cookie = headers[i].toString();
					if (cookie.indexOf("ASP.NET_SessionId=") > 0) {
						int start = cookie.indexOf("ASP.NET_SessionId=") + 18;
						int end = cookie.indexOf(";", start);
						NET_SessionId = cookie.substring(start, end);
					}
				}
		        
		        String UserLoginJumpUrl =response2.getLastHeader("Location").getValue();
		        HttpGet UserLoginJumpUrlGet = new HttpGet(UserLoginJumpUrl);
		        UserLoginJumpUrlGet.setHeader("Host", "ehire.51job.com");
		        UserLoginJumpUrlGet.setHeader("Referer", "http://ehire.51job.com/MainLogin.aspx");
		        UserLoginJumpUrlGet.setHeader("Accept",
		    	"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		        UserLoginJumpUrlGet.setHeader("User-Agent",
		    	"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 BIDUBrowser/6.x Safari/537.36");
		        
		        UserLoginJumpUrlGet.setHeader("Cache-Control", "max-age=0");
		        UserLoginJumpUrlGet.setHeader("Accept-Encoding", "gzip,deflate");
		        UserLoginJumpUrlGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
		        HttpResponse UserLoginJumpResponse = httpClient.execute(UserLoginJumpUrlGet);
		        HttpEntity UserLoginJumpEntity = UserLoginJumpResponse.getEntity();
		        result =  EntityUtils.toString(UserLoginJumpEntity, "UTF-8");
		        //System.out.println("====>>"+result);
		        if (result.indexOf("对不起，您已通过其他浏览器登录到本系统") != -1) {

		            String url = "", viewstate = "", gvOnLineUser = "", kickOut = "";

		            Document document = Jsoup.parse(result);
		            url = document.select("#form1").attr("action");
		            viewstate = document.select("#form1").select("#__VIEWSTATE")
		                    .attr("value");
		            String reg = "<a href=\"javascript:__doPostBack(.+?)\">强制下线</a>";

		            if (null!=url && !"".equals(url)) {
		                url = "https://ehirelogin.51job.com/Member/" + url;
		                List<NameValuePair> parames = new ArrayList<NameValuePair>();
		                parames.add(new BasicNameValuePair("__VIEWSTATE", viewstate));
		                parames.add(new BasicNameValuePair("__EVENTTARGET",
		                        "gvOnLineUser"));
		                parames.add(new BasicNameValuePair("__EVENTARGUMENT",
		                        "KickOut$0"));

		                post = new HttpPost(url);
		                post.setHeader("Host","ehire.51job.com");
		                post.setHeader("Origin","http://ehire.51job.com");
		                post.setHeader("Referer",url);
		                
		                HttpEntity kickOuthe = new UrlEncodedFormEntity(parames, Consts.UTF_8);//new UrlEncodedFormEntity(loginParames);
		                post.setEntity(kickOuthe);
		                 
		                HttpResponse kickOutresponse = httpClient.execute(post);
		                HttpEntity kickOutentity = kickOutresponse.getEntity();
		                String tempResult = EntityUtils.toString(kickOutentity, "gb2312");
		                System.out.println(tempResult);
		                if (tempResult.indexOf("UserLogin.aspx?ctmName") != -1) {
		                    return LoginQcwy(ctmName,userName, password);
		                }
		            }
		        }
		        //获取剩余简历数 剩余职位数
		        HttpGet navigateGet = new HttpGet("http://ehire.51job.com/Navigate.aspx");
		        navigateGet.setHeader("Host", "ehire.51job.com");
		        navigateGet.setHeader("Referer", "http://ehire.51job.com/MainLogin.aspx");
		        navigateGet.setHeader("Cache-Control", "max-age=0");
		        navigateGet.setHeader("Accept-Encoding", "gzip,deflate");
		        navigateGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
		        HttpResponse navigateResponse = httpClient.execute(navigateGet);
		        HttpEntity navigateEntity = navigateResponse.getEntity();
		        result =  EntityUtils.toString(navigateEntity, "gb2312");
		        
		        HttpGet navigateGet_1 = new HttpGet("http://ehire.51job.com/Candidate/ResumeView.aspx?hidUserID=100000");
		        
		        
		        HttpPost resume_post=new HttpPost("http://ehire.51job.com/Candidate/SearchResume.aspx");
		        List<NameValuePair> namePairs = new ArrayList<NameValuePair>();
		        namePairs.add(new BasicNameValuePair("ctrlSerach$txtUserID", "100000"));
		        HttpEntity newenEntity=new UrlEncodedFormEntity(namePairs);
		        resume_post.setEntity(newenEntity);
		        
		        HttpResponse httpResponse_1=httpClient.execute(navigateGet_1);
		        HttpResponse httpResponse=httpClient.execute(resume_post);
		        HttpEntity httpEntity=httpResponse_1.getEntity();
		        System.out.println("=======================================");
		        System.out.println(EntityUtils.toString(httpEntity, "gb2312"));
		        //System.out.println("================================>>"+result);
		        String zhiwei="";
		        String jianli="";
		        if(result.indexOf("您还可发布职位：")!=-1){
		        	int zwstart = result.indexOf("您还可发布职位：");
		        	zwstart = result.indexOf("<b class='info_att'>",zwstart)+20;
		        	int zwend = result.indexOf("</b>", zwstart);
		        	zhiwei = result.substring(zwstart, zwend);
		        	loginResultObj.setResult(true);
		        }
		        
		        if(result.indexOf("您还可下载简历：")!=-1){
		        	int jlstart = result.indexOf("您还可下载简历：");
		        	jlstart = result.indexOf("<b class='info_att'>",jlstart)+20;
		        	int jlend = result.indexOf("</b>", jlstart);
		        	jianli = result.substring(jlstart, jlend);
		        }
		        loginResultObj.setResumesnum(jianli);
		        loginResultObj.setJobsnum(zhiwei);
		        
				} catch (Exception e) {
					if (post != null) {
						post.abort();
					}
					e.printStackTrace();
				} finally {
					if (post != null) {
						post.abort();
					}
					return loginResultObj;
				}
			}
}
