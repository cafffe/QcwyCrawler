package com.kmgh;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BestMatchSpecFactory;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.impl.cookie.BrowserCompatSpecFactory;
import org.apache.http.protocol.HttpContext;

public class QcwyHttpUtil {
	public static CloseableHttpClient qcwyHttpClient = null;
	
	public static CloseableHttpClient getQcwyHttpClient() {  
        if (qcwyHttpClient == null) {    
        	try {
				qcwyHttpClient = getHttpclient();
			} catch (KeyManagementException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (KeyStoreException e) {
				e.printStackTrace();
			}  
        }    
       return qcwyHttpClient;  
   }  
	
	
	public static CloseableHttpClient getHttpclient() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException{
//		System.setProperty("javax.net.ssl.trustStore", "/usr/local/share/nfsfile/zhishengji/temp/jssecacerts_51");
		System.setProperty("javax.net.ssl.trustStore", "E:/jssecacerts_51");
		BasicCookieStore cookieStore = new BasicCookieStore();
    	CookieSpecProvider easySpecProvider = new CookieSpecProvider() {
    	public CookieSpec create(HttpContext context) {

    	return new BrowserCompatSpec() {
    	@Override
    	public void validate(Cookie cookie, CookieOrigin origin)
    	throws MalformedCookieException {
    	// Oh, I am easy
    	}
    	};
    	}

    	};
    	Registry<CookieSpecProvider> r = RegistryBuilder
    	.<CookieSpecProvider> create()
    	.register(CookieSpecs.BEST_MATCH, new BestMatchSpecFactory())
    	.register(CookieSpecs.BROWSER_COMPATIBILITY,
    	new BrowserCompatSpecFactory())
    	.register("easy", easySpecProvider).build();

    	RequestConfig requestConfig = RequestConfig.custom()
    	.setCookieSpec("easy").setSocketTimeout(10000)
    	.setConnectTimeout(10000).build();
        
        CloseableHttpClient httpclient = HttpClients.custom()
    	.setDefaultCookieSpecRegistry(r)
    	.setDefaultRequestConfig(requestConfig)
    	.setDefaultCookieStore(cookieStore)
    	.build();
//        httpclient.getParams().setParameter("User-Agent","Mozilla/5.0 (X11; U; Linux i686; zh-CN; rv:1.9.1.2) Gecko/20090803 Fedora/3.5.2-2.fc11 Firefox/3.5.2");
    	return httpclient;
    }
	
	
	public Map<String ,String> getYueXinMap(){
		Map yueximap= new HashMap();
		yueximap.put("0000001000", "01");
		yueximap.put("0100002000", "02");
		yueximap.put("0200104000", "04");
		yueximap.put("0400106000", "05");
		yueximap.put("0600108000", "06");
		yueximap.put("0800110000", "07");
		yueximap.put("1000115000", "08");
		yueximap.put("1500120000", "09");
		yueximap.put("2000130000", "13");
		yueximap.put("3000150000", "11");
		yueximap.put("5000199999", "12");
		return yueximap;
	}
	
	public Map<String ,String> getFuLiMap(){
		Map yueximap= new HashMap();
		yueximap.put("10000", "五险�?�?");
		yueximap.put("10001", "年底双薪");
		yueximap.put("10003", "年底分红");
		yueximap.put("10004", "股票期权");
		yueximap.put("10005", "加班补助");
		yueximap.put("10006", "全勤�?");
		yueximap.put("10007", "包吃");
		yueximap.put("10008", "包住");
		yueximap.put("10011", "房补");
		yueximap.put("10009", "交�?�补�?");
		yueximap.put("10010", "餐补");
		yueximap.put("10013", "采暖补贴");
		yueximap.put("10014", "带薪年假");
		yueximap.put("10015", "弹�?�工�?");
		yueximap.put("10016", "补充医疗保险");
		yueximap.put("10020", "高温补贴");
		yueximap.put("10017", "定期�?�?");
		yueximap.put("10018", "免费班车");
		yueximap.put("10019", "员工旅游");
		yueximap.put("10021", "节日福利");
		return yueximap;
	}
    
	
}
