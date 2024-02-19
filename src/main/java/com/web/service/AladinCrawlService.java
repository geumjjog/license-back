package com.web.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.web.domain.AladinBookCrawl;
import com.web.domain.Yes24BookCrawl;
import com.web.persistence.AladinCrawlRepository;

@Service
public class AladinCrawlService {

	@Autowired
	private AladinCrawlRepository aladinCrwalRepo;

	public AladinCrawlService(AladinCrawlRepository aladinCrwalRepo) {
		this.aladinCrwalRepo = aladinCrwalRepo;
	}

	public void saveAladinBookCrawl(AladinBookCrawl bookCrawl) {
		aladinCrwalRepo.save(bookCrawl);
	}

	public void saveAladinBookCrawls(List<AladinBookCrawl> bookCrawls) {
		aladinCrwalRepo.saveAll(bookCrawls);
	}

	// 페이지 번호에 따라 검색 URL을 생성하는 메소드 수정
	public String generateSearchUrl(int page, String jmfldnm) {
		try {
			String searchQuery = jmfldnm;
			String encodedQuery = URLEncoder.encode(searchQuery, StandardCharsets.UTF_8.toString());
			return "https://www.aladin.co.kr/search/wsearchresult.aspx?SearchTarget=All&SearchWord=" + encodedQuery
					+ "&page=" + page;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	
	public List<AladinBookCrawl> getBooksFromCrawl(String jmfldnm) throws IOException {
		List<AladinBookCrawl> bookCrawls = new ArrayList<>();
		int maxPages = 4;

		for (int page = 1; page <= maxPages; page++) {
			String bookDataUrl = generateSearchUrl(page, jmfldnm);
			Document doc = Jsoup.connect(bookDataUrl).get();

			String cssSelector = "div#Search3_Result div.ss_book_box";

			if (doc.select(cssSelector).isEmpty()) {
				System.out.println("========데이터 없음 ======");
			}

			Elements bookElements = doc.select(cssSelector);

			for (Element bookElement : bookElements) {
				// 책 정보
				String bookName = bookElement
						.select("table tbody tr td table tbody tr td div.ss_book_list ul li a.bo3 b").text();
				System.out.println("알라딘 : " + bookName);

				// 가격 정보
				String bookPrice = bookElement
						.select("table tbody tr td table tbody tr td div.ss_book_list ul li span.ss_p2 b span").text();

				// 이미지 정보
				Elements imageElementsFirst = bookElement.select(
						"table tbody tr td table tbody tr td div.cover_area a div.flipcover_out div.flipcover_in.lcover_none img[src$=.jpg]");
				String imageNameFirst = imageElementsFirst.attr("src");

				Elements imageElementsSecond = bookElement.select(
						"table tbody tr td table tbody tr td div.cover_area a div.flipcover_out div.flipcover_in:not(.lcover_none) img.front_cover[src$=.jpg]");
				String imageNameSecond = imageElementsSecond.attr("src");
				
				Elements imageElementsThird = bookElement.select(
						"table tbody tr td table tbody tr td div.cover_area_other a img[src$=.jpg]");
				String imageNameThird = imageElementsThird.attr("src");
				//바로가기링크
				String viewDetail = bookElements.select("table tbody tr td table tbody tr td div.cover_area a").attr("href");
				System.out.println("아아아아ㅏ아아ㅏ아아아"+viewDetail);
				
				String imageName = "";
				// 이미지가 있는 경우에만 값을 할당
				if (!imageElementsFirst.isEmpty()) {
					imageName = imageNameFirst;
				} else if (!imageElementsSecond.isEmpty()) {
					imageName = imageNameSecond;
				} else if (!imageNameThird.isEmpty()) {
					imageName = imageNameThird;
				}

				AladinBookCrawl bookCrawl = AladinBookCrawl.builder()
						.bookName(bookName)
						.bookPrice(bookPrice)
						.imageName(imageName)
						.viewDetail(viewDetail)
						.build();

				bookCrawls.add(bookCrawl);
			}
		}
		return bookCrawls;
	}
	//========================================================
	public String SearchUrl(int page, String search) {
		try {
			String searchQuery = search;
			String encodedQuery = URLEncoder.encode(searchQuery, StandardCharsets.UTF_8.toString());
			return "https://www.aladin.co.kr/search/wsearchresult.aspx?SearchTarget=All&SearchWord=" + encodedQuery
					+ "&page=" + page;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	public List<AladinBookCrawl> getSearchBooksFromCrawl(String search) throws IOException {
		List<AladinBookCrawl> bookCrawls = new ArrayList<>();
		int maxPages = 4;

		for (int page = 1; page <= maxPages; page++) {
			String bookDataUrl = SearchUrl(page, search);
			Document doc = Jsoup.connect(bookDataUrl).get();

			String cssSelector = "div#Search3_Result div.ss_book_box";

			if (doc.select(cssSelector).isEmpty()) {
				System.out.println("========데이터 없음 ======");
			}

			Elements bookElements = doc.select(cssSelector);

			for (Element bookElement : bookElements) {
				// 책 정보
				String bookName = bookElement
						.select("table tbody tr td table tbody tr td div.ss_book_list ul li a.bo3 b").text();
				System.out.println("알라딘 : " + bookName);

				// 가격 정보
				String bookPrice = bookElement
						.select("table tbody tr td table tbody tr td div.ss_book_list ul li span.ss_p2 b span").text();

				// 이미지 정보
				Elements imageElementsFirst = bookElement.select(
						"table tbody tr td table tbody tr td div.cover_area a div.flipcover_out div.flipcover_in.lcover_none img[src$=.jpg]");
				String imageNameFirst = imageElementsFirst.attr("src");

				Elements imageElementsSecond = bookElement.select(
						"table tbody tr td table tbody tr td div.cover_area a div.flipcover_out div.flipcover_in:not(.lcover_none) img.front_cover[src$=.jpg]");
				String imageNameSecond = imageElementsSecond.attr("src");
				
				Elements imageElementsThird = bookElement.select(
						"table tbody tr td table tbody tr td div.cover_area_other a img[src$=.jpg]");
				String imageNameThird = imageElementsThird.attr("src");
				
				//바로가기링크
				String viewDetail = bookElements.select("table tbody tr td table tbody tr td div.cover_area a").attr("href");
				System.out.println("아아아아ㅏ아아ㅏ아아아"+viewDetail);
				String imageName = "";
				// 이미지가 있는 경우에만 값을 할당
				if (!imageElementsFirst.isEmpty()) {
					imageName = imageNameFirst;
				} else if (!imageElementsSecond.isEmpty()) {
					imageName = imageNameSecond;
				} else if (!imageNameThird.isEmpty()) {
					imageName = imageNameThird;
				}

				AladinBookCrawl bookCrawl = AladinBookCrawl.builder()
						.bookName(bookName)
						.bookPrice(bookPrice)
						.imageName(imageName)
						.viewDetail(viewDetail)
						.build();

				bookCrawls.add(bookCrawl);
			}
		}
		return bookCrawls;
	}

	// 전체 출력해줄 메서드
	public List<AladinBookCrawl> getAllBooks() {
		Sort sortByDatabaseOrder = Sort.by(Sort.Direction.ASC, "id");
		return aladinCrwalRepo.findAll(sortByDatabaseOrder);
	}

}
