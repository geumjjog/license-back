package com.web.data;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.web.domain.LicenseInfo;

public class TestInfo {
    public static void main(String[] args) {
    	TestInfo td = new TestInfo();
    	td.getInfoData();
    }
    
    public void getInfoData() {
	    HttpURLConnection conn = null;
	    BufferedReader rd = null;

	    try {
	        // seriesCd를 변경하면서 데이터를 요청
	    	for (int i = 1; i <= 4; i++) {
	    	    StringBuilder urlBuilder = new StringBuilder("http://openapi.q-net.or.kr/api/service/rest/InquiryQualInfo/getList"); /*URL*/
	    	    urlBuilder.append("?ServiceKey=8RQmmNMbqQKZO06m6d44ZNTJv55aWC7ld4cj5de9n14a6o3tbFOrn%2FF3Aa5cVQzRVlpUr2nt2J9sjnqrnD2KLA%3D%3D"); /*Service Key*/
	    	    urlBuilder.append("&seriesCd=" + String.format("%02d", i)); // seriesCd 값을 01부터 04까지 순차적으로 변경

	    	    URL url = new URL(urlBuilder.toString());
	    	    conn = (HttpURLConnection) url.openConnection();
	    	    conn.setRequestMethod("GET");
	    	    conn.setRequestProperty("Content-type", "application/xml");

	    	    if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
	    	        throw new RuntimeException("HTTP error code : " + conn.getResponseCode());
	    	    }

	    	    rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	    	    StringBuilder xmlResponse2 = new StringBuilder();
	    	    String line;

	    	    while ((line = rd.readLine()) != null) {
	    	        xmlResponse2.append(line);
	    	    }

	    	    // XML 파싱 및 DB에 저장
	    	    parseXmlResponse2(xmlResponse2.toString());
	    	}
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            if (rd != null) {
	                rd.close();
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        if (conn != null) {
	            conn.disconnect();
	        }
	    }
	}

	private void parseXmlResponse2(String xmlResponse2) {
		try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new org.xml.sax.InputSource(new java.io.StringReader(xmlResponse2)));

            
            
            int count =0; //
            NodeList itemList = document.getElementsByTagName("item");
            for (int i = 0; i < itemList.getLength(); i++) {
                Node itemNode = itemList.item(i);
                if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element itemElement = (Element) itemNode;

                    count++; //                    
                    LicenseInfo licenseInfo = new LicenseInfo();
                    licenseInfo.setJmnm(getTagValue("jmNm", itemElement));
                    licenseInfo.setCareer(getTagValue("career", itemElement));
                    licenseInfo.setImplnm(getTagValue("implNm", itemElement));
                    licenseInfo.setInstinm(getTagValue("instiNm", itemElement));
                    licenseInfo.setJob(getTagValue("job", itemElement));
                    licenseInfo.setSeriesnm(getTagValue("seriesNm", itemElement));
                    licenseInfo.setSummary(getTagValue("summary", itemElement));
                    licenseInfo.setTrend(getTagValue("trend", itemElement));
                    	    

                    System.out.println(licenseInfo);
                    System.out.println(count);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
		
	}
	
	private String getTagValue(String tagName, Element element) {
	    NodeList nodeList = element.getElementsByTagName(tagName);
	    if (nodeList.getLength() > 0) {
	        Node itemNode = nodeList.item(0);
	        if (itemNode != null && itemNode.getNodeType() == Node.ELEMENT_NODE) {
	            String textContent = itemNode.getTextContent().trim();
	            textContent = textContent.replaceAll("\\{.*?\\}", "");
	            // <> 안에 있는 내용 제거
	            textContent = textContent.replaceAll("\\<.*?\\>", "");
	            // BODY와 LI 사이의 내용 제거
	            textContent = textContent.replaceAll("BODY.*?LI", "");
	            textContent = textContent.replaceAll("NE.*?\\}", "");
	            return textContent.trim();
	        }
	    }
	    return ""; // XML 요소의 텍스트 값이 존재하지 않을 때 빈 문자열 반환
	}
	
}
