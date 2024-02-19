package com.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.web.domain.AladinBookCrawl;
import com.web.domain.KyoboBookCrawl;
import com.web.domain.TotalBookCrawl;
import com.web.domain.Yes24BookCrawl;
import com.web.service.AladinCrawlService;
import com.web.service.KyoboCrawlService;
import com.web.service.Yes24CrawlService;


@RestController
@RequestMapping("/detail")
public class CrawlController {

	@Autowired
	private Yes24CrawlService bookCrawlService;
	
	@Autowired
	private KyoboCrawlService kyoboCrawlService;
	
	@Autowired
	private AladinCrawlService aladinCrawlService;
	
	
	@GetMapping("/books/{jmfldnm}")
    public ResponseEntity<List<TotalBookCrawl>> getBookDetails(@PathVariable String jmfldnm) {
        try {
            // 각 사이트에서 도서 정보를 가져오는 로직
            List<Yes24BookCrawl> yes24Books = bookCrawlService.getBooksFromCrawl(jmfldnm);
            List<KyoboBookCrawl> kyoboBooks = kyoboCrawlService.getBooksFromCrawl(jmfldnm);
            List<AladinBookCrawl> aladinBooks = aladinCrawlService.getBooksFromCrawl(jmfldnm);
            
            
            // 리스트를 하나로 합치기
            List<TotalBookCrawl> books = new ArrayList<>();
            books.addAll(yes24Books);
            books.addAll(kyoboBooks);
            books.addAll(aladinBooks);

            // 리스트를 클라이언트에 반환
            return new ResponseEntity<>(books,HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
	 @GetMapping("/books")
	    public ResponseEntity<List<TotalBookCrawl>> searchBooks(
	            @RequestParam(required = false) String search
	    ) {
		System.out.println("검색어 받았낭 "+search);
	        try {
	            // 각 사이트에서 도서 정보를 가져오는 로직
	            List<Yes24BookCrawl> yes24Books = bookCrawlService.getSearchBooksFromCrawl(search);
	            List<KyoboBookCrawl> kyoboBooks = kyoboCrawlService.getSearchBooksFromCrawl(search);
	            List<AladinBookCrawl> aladinBooks = aladinCrawlService.getSearchBooksFromCrawl(search);

	            // 리스트를 하나로 합치기
	            List<TotalBookCrawl> books = new ArrayList<>();
	            books.addAll(yes24Books);
	            books.addAll(kyoboBooks);
	            books.addAll(aladinBooks);

	            // 리스트를 클라이언트에 반환
	            return new ResponseEntity<>(books, HttpStatus.OK);
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	        }
	    }


}
