package com.openai.board;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.openai.board.dto.BfileDto;
import com.openai.board.dto.ListDto;
import com.openai.board.dto.ReplyDto;
import com.openai.board.service.BoardSevice;

@Controller
public class BoardController {
	private static final Logger logger = LoggerFactory.getLogger(BoardController.class);
	
	@Autowired
	private BoardSevice bServ;
	
	private ModelAndView mv;
	
	@GetMapping("/list")
	public ModelAndView boardList(ListDto list, HttpSession session) {
		logger.info("boardList()");
		
		//db에서 게시글의 목록을 가져와서 페이지로 전달.
		mv = bServ.getBoardList(list, session);
		
		return mv;
	}
	@GetMapping("/writeFrm")
	public String writeFrm() {
		logger.info("writeFrm()");
		
		return "writeFrm";
	}
	//멀티파트 데이터를 처리할 경우 첫번째 매개변수는 MultipartHttpServletRequest여야 한다(must)
	@PostMapping("/boardWrite")
	public String boardWrite(MultipartHttpServletRequest multi, RedirectAttributes rttr) {
		logger.info("boardWrite()");
		
		String view = bServ.boardWrite(multi, rttr);
		
		return view;
	}
	// <!-- 상세보기 화면 이동 url + 게시글번호 -->
	// <a href="./contents?bnum=${bitem.b_num}">
	
	@GetMapping("/contents")
	public ModelAndView boardContents(Integer bnum) {
		logger.info("boardContents()  : bnum - " + bnum);
		
		mv = bServ.getContent(bnum);
		
		
		return mv;
	}
	
	@PostMapping(value = "/replyIns", produces = "application/json; charset=UTF-8")
	@ResponseBody
	public Map<String, List<ReplyDto>> replyInsert(ReplyDto reply) {
		logger.info("replyInsert()");
		
		Map<String, List<ReplyDto>> rmap = bServ.replayInsert(reply);
		
		return rmap; // map -> json 변환은 jackson 라이브러리 객체가 처리
	}
	
	@GetMapping("/download") //string이나 모델엔드뷰는 화면을 전환할때 사용하는데 다운로드는 화면전환이 아니므로 void사용
	public void fileDownload(BfileDto bfile, HttpServletResponse response, HttpSession session ) {
		logger.info("fileDownload() - oriname : " + bfile.getBf_oriname());
		
		bServ.fileDowload(bfile, response, session);
	}
	
	@GetMapping("/updateFrm") //내용도 같이 이동해야함.
	public ModelAndView updateFrm(int bnum) {
		logger.info("updateFrm() : b num - " + bnum);
		
		mv = bServ.updateFrm(bnum);
		
		return mv;
	}
	
	@PostMapping(value = "/delFile", produces = "application/json; charset=UTF-8")
	@ResponseBody //일반 컨트롤러 이므로
	public Map<String, List<BfileDto>> delFile (String sysname, int bnum, HttpSession session) { //from function del
		logger.info("delFile() - sysname" + sysname);
		
		Map<String, List<BfileDto>> fMap = bServ.fileDelete(sysname, bnum, session); //파일목록이 들어간 맵
		
		return fMap; // jackson 라이브러리의 객체가 json object로 변환. 
	}
	
	@PostMapping("/boardUpdate")
	public String boardUpdate(MultipartHttpServletRequest multi, RedirectAttributes rttr) {
		logger.info("boardUpdate()");
		
		String view = bServ.boardUpdate(multi, rttr);
		
		return view;
	}
	
	@GetMapping("/delete")
	public String boardDelete(int bnum, RedirectAttributes rttr) {
		logger.info("boardDelete() - bnum : " + bnum);
		
		String view = bServ.boardDelete(bnum, rttr);
		
		return view;
	}
	
}//class end
