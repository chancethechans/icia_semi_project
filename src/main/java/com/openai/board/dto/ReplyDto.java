package com.openai.board.dto;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class ReplyDto {
	private int r_num; // 댓글 번호(기본키)
	private int r_bnum; // 게시글 번호(검색)
	private String r_contents; //내용
	private String r_id; // 작성자 ID(로그인한 ID)
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	//제이슨으로 변환할때 날짜 형식 지정 가능, 입력도 ajax로 출력도 ajax로 하므로(비동기 처리) 
	private Timestamp r_date;
	
}//댓글 데이터는 ajax에서 json 객체로 처리되기 때문에 날짜 데이터의 출력 형식을 지정한다. 
