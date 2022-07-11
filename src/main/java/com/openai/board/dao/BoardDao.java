package com.openai.board.dao;

import java.util.List;
import java.util.Map;

import com.openai.board.dto.BfileDto;
import com.openai.board.dto.BoardDto;
import com.openai.board.dto.ListDto;
import com.openai.board.dto.ReplyDto;
import com.openai.board.dto.TagDto;

public interface BoardDao {
	
	//게시글 목록 가져오기
	public List<BoardDto> boardListSelect(ListDto list);
	
	//전체 글 개수 구하기
	public int bcntSelect(ListDto list);
	
	//게시글 저장하기
	public void boardInsert(BoardDto board);
	
	//파일 정보 저장하기
	public void fileInsert(Map<String, String> fmap);
	
	//게시글 내용 가져오기
	public BoardDto boardSelect(Integer bnum);
	
	//파일 목록 가져오기
	public List<BfileDto> fileSelect(Integer bnum);
	
	//댓글 목록 가져오기 
	public List<ReplyDto> replySelect(Integer bnum);
	
	//댓글 저장하기
	public void replyInsert(ReplyDto reply);
	
	//파일 삭제(개별 - 게시글 수정)
	public void fileDelete(String sysname);
	
	//게시글 수정 
	public void boardUpdate(BoardDto board);
	
	//댓글 삭제
	public void replyDelete(Integer bnum);
	//파일목록 삭제
	public void fListDelete(Integer bnum);
	//게시글 삭제
	public void boardDelete(Integer bnum);
	
	//태그입력
	public void tagInsert(TagDto td);
	//태그 목록 불러오기
	public List<TagDto> tagSelect(Integer bnum);
	
}
