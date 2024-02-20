package com.web.controller;

import java.io.File;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.web.domain.Board;
import com.web.domain.Member;
import com.web.persistence.BoardRepository;
import com.web.service.BoardService;
import com.web.service.FolderPathREPO;
import com.web.service.MemberService;
import com.web.service.ReplyService;

@RequestMapping("/board")
@RestController
public class BoardController {
   
   @Autowired
   private BoardService boardService;
   
   @Autowired
   private ReplyService replyService;

   @Autowired
   private MemberService memberService;
   
   @Autowired
   private BoardRepository BoardRepo;

   //----------------------------------------------------------
   // 게시글 등록

   @PostMapping("/boardWrite")
     public String BoardWrite(
       @RequestParam("title") String title,
       @RequestParam("content") String content,
       @RequestParam(value = "file", required = false) MultipartFile file,
       @RequestParam("memberId") String memberId,
       @RequestParam("comment") String comment
     ) {
      boardService.board_write(title,content,file,memberId,comment);

       return "Success"; // 또는 다른 응답 메시지
     }
   
   //----------------------------------------------------------
      // 게시글 리스트
   
   @GetMapping("/boardList")
   public List<Board> getBoardList() {
      System.out.println("보드리스트====================");
       return boardService.getBoardList();
   }
   //----------------------------------------------------------
   // 게시글 상세페이지
   @GetMapping("/boardView/{boardSeq}")
    public ResponseEntity<Board> getBoardBySeq(@PathVariable Long boardSeq) {
        // 게시글 번호로 게시글 정보를 조회
        Board board = boardService.getBoardBySeq(boardSeq);

        if (board != null) {
            return new ResponseEntity<>(board, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
   }
   
   
   //----------------------------------------------------------
   // **게시글 수정 시 정보 데아터 & seq 저장 + 페이징처리 
   @PostMapping("/update")
    public ResponseEntity<String> updateBoard(
            @RequestParam("boardSeq") Long boardSeq,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        try {
            // 기존 게시글 정보 가져오기
            Board board = boardService.getBoardBySeq(boardSeq);

            // 수정된 정보 업데이트
            board.setBoardTitle(title);
            board.setBoardContents(content);

            // 파일이 존재하는 경우 파일 업데이트
            if (file != null && !file.isEmpty()) {
                String fileName = file.getOriginalFilename();
                // 파일 업로드 및 저장 등의 로직 수행
                // 예: 파일 업로드 서비스를 사용하여 파일 업로드
                // fileService.uploadFile(file);
                board.setFile(fileName); // 파일 이름 저장
            }

            // 게시글 업데이트
            BoardRepo.save(board);

            return new ResponseEntity<>("게시글이 성공적으로 수정되었습니다.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("게시글 수정 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

   // **게시글 수정
   @PostMapping("/boardUpdate")
   public String boardUpdate(MultipartHttpServletRequest mul) {
      boardService.board_update(mul);
      return "redirect:community";
   }
   
   //----------------------------------------------------------

   // 게시글 삭제 
   @GetMapping("/board_delete/{boardSeq}")
   public String board_delete(@PathVariable Long boardSeq) {
      Board board = boardService.getBoardBySeq(boardSeq);
      boardService.board_delete(board);
      return "redirect:community";
   }
   
   /***
    *    댓글 Controller
    ***/
   //----------------------------------------------------------

   
   
   
}