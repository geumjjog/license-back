package com.web.Crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/support")
public class SupportCrawler {

    @GetMapping
    public Map<String, Object> crawl() {
        Map<String, Object> response = new HashMap<>();

        try {
            // 크롤링 결과를 담을 Map
            Map<String, String> resultMap1 = crawlUrl("https://www.youthcenter.go.kr/youngPlcyUnif/youngPlcyUnifDtl.do?pageIndex=1&frameYn=&bizId=R2024010218403&dtlOpenYn=&plcyTpOpenTy=&plcyCmprInfo=&srchWord=&chargerOrgCdAll=&srchAge=&trgtJynEmp=&trgtJynEmp=&srchSortOrder=1&pageUnit=12");
            Map<String, String> resultMap2 = crawlUrl("https://www.youthcenter.go.kr/youngPlcyUnif/youngPlcyUnifDtl.do?pageIndex=1&frameYn=&bizId=R2024010818564&dtlOpenYn=&plcyTpOpenTy=&plcyCmprInfo=&srchWord=%EC%9D%91%EC%8B%9C%EB%A3%8C&chargerOrgCdAll=&srchAge=&trgtJynEmp=&trgtJynEmp=&srchSortOrder=2&pageUnit=12");
            Map<String, String> resultMap3 = crawlUrl("https://www.youthcenter.go.kr/youngPlcyUnif/youngPlcyUnifDtl.do?pageIndex=1&frameYn=&bizId=R2024010218423&dtlOpenYn=&plcyTpOpenTy=&plcyCmprInfo=&srchWord=&chargerOrgCdAll=&srchAge=&trgtJynEmp=&trgtJynEmp=&srchSortOrder=1&pageUnit=12");
            Map<String, String> resultMap4 = crawlUrl("https://www.youthcenter.go.kr/youngPlcyUnif/youngPlcyUnifDtl.do?pageIndex=1&frameYn=&bizId=R2024011018628&dtlOpenYn=&plcyTpOpenTy=&plcyCmprInfo=&srchWord=&chargerOrgCdAll=&srchAge=&trgtJynEmp=&trgtJynEmp=&srchSortOrder=1&pageUnit=12");
            Map<String, String> resultMap5 = crawlUrl("https://www.youthcenter.go.kr/youngPlcyUnif/youngPlcyUnifDtl.do?pageIndex=1&frameYn=&bizId=R2024010518524&dtlOpenYn=&plcyTpOpenTy=&plcyCmprInfo=&srchWord=&chargerOrgCdAll=&srchAge=&trgtJynEmp=&trgtJynEmp=&srchSortOrder=1&pageUnit=12");

            // 이미지 크롤링 결과를 추가
            Map<String, String> imageResult = crawlImages("https://www.kua.go.kr/uapda020/selectKuaStoryDtal.do?pageIndex=2&pageUnit=10&srchLinkTycd=A&linkTycd=2&srchTy=&srchKwrd=&ntceStno=84");

            // 최종 결과를 응답에 담음
            response.put("section1", resultMap1);
            response.put("section2", resultMap2);
            response.put("section3", resultMap3);
            response.put("section4", resultMap4);
            response.put("section5", resultMap5);
            response.put("images", imageResult);

        } catch (IOException e) {
            e.printStackTrace();
            response.put("error", "An error occurred while crawling the website.");
        }

        return response;
    }

    private Map<String, String> crawlUrl(String url) throws IOException {
        Document document = Jsoup.connect(url).get();
        Elements titleElements = document.select(".common_table01:first-child .list_tit");
        Elements contentElements = document.select(".common_table01:first-child .list_cont");

        Map<String, String> resultMap = new HashMap<>();

        for (int i = 0; i < titleElements.size(); i++) {
            String title = titleElements.get(i).text();
            String content = contentElements.get(i).text();
            resultMap.put("li_title" + i, title);
            resultMap.put("li_cont" + i, content);
        }

        return resultMap;
    }

    private Map<String, String> crawlImages(String url) throws IOException {
        Map<String, String> imageResult = new HashMap<>();
        Document document = Jsoup.connect(url).get();
        Elements imgElements = document.select(".slider li:nth-child(2) img");

        for (Element imgElement : imgElements) {
            String imgUrl = imgElement.absUrl("src");
            imageResult.put("image_url", imgUrl);
        }

        return imageResult;
    }
}
