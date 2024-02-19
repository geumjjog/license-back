package com.web.controller;

import java.security.Principal;
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
import com.web.service.BoardService;
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
	


	//----------------------------------------------------------
	// 게시글 등록

	@PostMapping("/boardWrite")
	  public String BoardWrite(
	    @RequestParam("title") String title,
	    @RequestParam("content") String content,
	    @RequestParam(value = "file", required = false) MultipartFile file,
	    @RequestParam("memberId") String memberId
	  ) {
		boardService.board_write(title,content,file,memberId);

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
	@GetMapping("boardView/{boardSeq}")
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
	@GetMapping("/board_update")
	public String board_update_form(Model model,Long boardSeq, String username, ModelMap modelMap){
		Member member = memberService.getMemberInfo(username);
		modelMap.addAttribute("member", member);
		boardService.getBoard(boardSeq,model);
		return "board/board_update";
	}
	// **게시글 수정
	@PostMapping("/boardUpdate")
	public String boardUpdate(MultipartHttpServletRequest mul,RedirectAttributes ra) {
		boardService.board_update(mul);
		ra.addAttribute("boardChoice",mul.getParameter("boardChoice"));
		return "redirect:board";
	}
	
	//----------------------------------------------------------

	// 게시글 삭제 
	@GetMapping("/board_delete")
	public String board_delete(Board board,Long boardChoice, RedirectAttributes ra) {
		boardService.board_delete(board);
		ra.addAttribute("boardChoice",boardChoice);
		return "redirect:board";
	}
	
	/***
	 * 	댓글 Controller
	 ***/
	//----------------------------------------------------------

	
	
	
}
