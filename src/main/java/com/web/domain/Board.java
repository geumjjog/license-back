package com.web.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;



@Getter
@Setter
@ToString
@Entity
@Table(name = "BOARD")
public class Board {
   
   @Id
   @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "board_seq_generator")
   @SequenceGenerator(name = "board_seq_generator", sequenceName = "board_seq_generator", allocationSize = 1)
   @Column(name = "BOARD_SEQ")
   private Long boardSeq; // 글번호
   
   @Column(updatable = false, name="BoardUsername")
   private String boardUsername; // 작성자(사용자)
   
   @Column(name = "BOARD_TITLE")
   private String boardTitle; // 제목
   
   @Column(name = "BOARD_CONTENTS")
   private String boardContents; // 내용
   
   @Column(name = "BOARD_DATE",insertable = false, updatable = false, columnDefinition = "date default sysdate")
   private Date boardDate; // 작성날짜(SYSDATE)
   
   @Column(name = "BOARD_COMMENT")
   private String boardComment; // 댓글
   
   @Column(name = "BOARD_FILE")
   private String file; 
   
   @Column(name = "DEFAULT_VALUE")
   private String defaultValue;
   
   
}