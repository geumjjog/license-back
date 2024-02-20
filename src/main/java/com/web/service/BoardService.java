package com.web.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.web.domain.Board;
import com.web.domain.Member;

public interface BoardService {
   // 게시글 목록 
   //public void board(Model model, Board board, Pageable pageable, String searchCode, String searchKeyword, Long boardChoice);

   // 게시글 상세페이지 
    public Board getBoardBySeq(Long boardSeq);
   
   public List<Board> getBoardList();

   // 게시글 글 쓰기 
   public void board_write(String title,String content,MultipartFile file,String memberId,String comment);
   
   // 게시글 글 수정 시 정보데이터 & seq 같이 보내기 + 페이징처리 
   public void getBoard(Long boardSeq,Model model);
   
   
   
   // 게시글 글 수정 
   public void board_update(MultipartHttpServletRequest mul);
   
   // 게시글 글 삭제 
   public void board_delete(Board board);
   
}