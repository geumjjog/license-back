package com.web.service;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.web.domain.Board;
import com.web.domain.Member;
import com.web.domain.Reply;
import com.web.persistence.BoardRepository;
import com.web.persistence.ReplyRepository;

@Service
public class BoardServiceImpl implements BoardService, FolderPathREPO {
   
   @Autowired
   private BoardRepository boardRepo;

   @Autowired 
   private ReplyRepository replyRepository;
   
   
   @Override
   public List<Board> getBoardList() {
       return boardRepo.findAll();
   }
   

   // 게시글 글 쓰기 
   @Override
   public void board_write(String title,String content,MultipartFile file,String memberId,String comment) {
      Board board = new Board();
      System.out.println("=====게시글 제목========"+title);
      board.setBoardTitle(title);
      board.setBoardContents(content);
      board.setBoardUsername(memberId);
      board.setBoardComment(comment);
      if(memberId.equals("admin123")) {
         board.setDefaultValue("notice");
      } else {
         board.setDefaultValue("freeboard");
      }
      System.out.println(memberId);
      MultipartFile files = file;
      if(files != null && files.getSize() !=0) {
         SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss-");
         Calendar calendar = Calendar.getInstance();
         String sysFileName = sdf.format(calendar.getTime());
         sysFileName += file.getOriginalFilename();
         // 첨부파일이 들어오면 저장되는 위치
         File saveFile = new File(BOARD_IMAGE_REPO + "/" + sysFileName);
         board.setFile(sysFileName);
         try {
            file.transferTo(saveFile);
         } catch (Exception e) {
            e.printStackTrace();
         }
      } else {
         // 파일이 존재하지 않을 시 DB에 nan으로 저장
         board.setFile("nan");
      }
      // 데이터베이스에 저장
       boardRepo.save(board);
      
   }
   // 게시글 상세페이지 : 

    public Board getBoardBySeq(Long boardSeq) {
           Optional<Board> optionalBoard = boardRepo.findById(boardSeq);
           return optionalBoard.orElse(null);
       }
   
   // 게시글 글 수정 시 정보데이터 & seq 같이 보내기 + 페이징처리 
   @Override
   public void getBoard(Long boardSeq,Model model) {
      Board board = boardRepo.findById(boardSeq).get();
      model.addAttribute("board", board);
   }

   // 게시글 글 수정 
   @Override
   public void board_update(MultipartHttpServletRequest mul) {
      Long a  = Long.parseLong(mul.getParameter("boardSeq"));
      Board board = boardRepo.findById(a).get();
      board.setBoardTitle(mul.getParameter("boardTitle"));
      board.setBoardContents(mul.getParameter("boardContents"));
      MultipartFile file = mul.getFile("file");
      
      if(file.getSize() !=0) {
         SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss-");
         Calendar calendar = Calendar.getInstance();
         String sysFileName = sdf.format(calendar.getTime());
         sysFileName += file.getOriginalFilename();
         File saveFile = new File(BOARD_IMAGE_REPO + "/" + sysFileName); 
         board.setFile(sysFileName);
         try {
            file.transferTo(saveFile);
         } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
         }
      } else {
         board.setFile("nan");
      }
      boardRepo.save(board);
   }
   
   // 게시글 글 삭제 
   @Override
   public void board_delete(Board board) {
      List<Reply> replyList = replyRepository.findByBoardSeqOrderByReplySeqDesc(board.getBoardSeq());
      for(Reply reply : replyList) {
         replyRepository.delete(reply);
      }
      boardRepo.deleteById(board.getBoardSeq());
   }

//   // 게시글 공지사항
//   @Override
//   public void boardNotice(Model model) {
//      List<Board> noticeList = boardRepo.findByBoardChoice(3L);
//      model.addAttribute("noticeList",noticeList);
//   }

//   // 내가 쓴 글
//   @Override
//   public void myBoardList(Model model, Board board, Pageable pageable, Member member) {
//      String username = member.getUsername();
//      int nowPage = boardList.getPageable().getPageNumber() + 1;
//
//      int startPage = Math.max(nowPage - 4, 1);
//      int endPage = Math.min(nowPage + 5, boardList.getTotalPages());
//
//      model.addAttribute("boardList", boardList);
//      model.addAttribute("nowPage", nowPage);
//      model.addAttribute("startPage", startPage);
//      model.addAttribute("endPage", endPage);
//   }
//   
   
   
}
















