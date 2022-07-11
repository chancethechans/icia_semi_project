package com.openai.board.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.openai.board.dao.BoardDao;
import com.openai.board.dao.MemberDao;
import com.openai.board.dto.BfileDto;
import com.openai.board.dto.BoardDto;
import com.openai.board.dto.ListDto;
import com.openai.board.dto.ReplyDto;
import com.openai.board.dto.TagDto;
import com.openai.board.util.HashTagProcess;
import com.openai.board.util.PagingUtil;

import lombok.extern.java.Log;

@Service
@Log
public class BoardSevice {
	@Autowired
	private BoardDao bDao;
	
	@Autowired
	private MemberDao mDao;
	//게시글 작성한 사용자의 point 증가 및 등급 변경 내용 출력 시 활용.
	//(세션에 다시 저장)
	
	private ModelAndView mv;
	
	private int listCnt = 5; //페이지 당 출력할 게시글 개수
	
	public ModelAndView getBoardList(ListDto list, HttpSession session) {
		
		mv = new ModelAndView();
		
		////Dao에 보내는 데이터를 만들자.(검색 기능 추가로 코드 사용 안함)
		//Map<String, Integer> pmap = new HashMap<String, Integer>();
		//pmap.put("pnum", (list - 1) * listCnt); // ***select * from blist limit 30(pnum), 10(lcnt);
		//pmap.put("lcnt", listCnt);
		int num = list.getPageNum();
		list.setPageNum((num - 1) * listCnt); //데이터 베이스는 0부터 시작하고 여기는 1부터 시작하므로 -1 해줘야 함 
		list.setListCnt(listCnt);
		
		List<BoardDto> bList = bDao.boardListSelect(list); //원래 map이 들어갔는데 list dto로 변경
			
		mv.addObject("bList", bList);
		
		//페이징 처리
		list.setPageNum(num);
		String pageHtml = getPaging(list);
		mv.addObject("paging", pageHtml);
		
		//세션에 페이지번호 저장(글쓰기 또는 글 상세보기 화면에서 목록 화면으로 돌아갈 때 사용할 페이지 번호를 저장)
		session.setAttribute("pageNum", list.getPageNum());
		if(list.getColname() != null) {
			session.setAttribute("list", list);
		}
		else { //검색이 아닐경우는 세션의 ListDto를 제거.
			session.removeAttribute("list");
		}
		
		
		mv.setViewName("boardList");
		
		return mv;
	}

	private String getPaging(ListDto list) {
		String pageHtml = null;
		
		//전체 글 개수 구하기
		int maxNum = bDao.bcntSelect(list);
		//한 페이지 당 보여질 페이지 번호의 개수
		int pageCnt = 5;
		String listName = "list?";
		
		//검색 용 컬럼명과 검색어를 추가 //컬럼이 null이 아닐때만 사용
		if(list.getColname() != null) {
			listName += "colname=" + list.getColname() + "&keyword=" + list.getKeyword() + "&";
		}
		
		PagingUtil paging = new PagingUtil(maxNum, list.getPageNum(), listCnt, pageCnt, listName);
		
		pageHtml = paging.makePaging();
		
		return pageHtml;
	}
	
	@Transactional
	public String boardWrite(MultipartHttpServletRequest multi, RedirectAttributes rttr) {
		String view = null;
		String msg = null;
		
		//세션에 들어가 있는 접속자의 정보를 갱신해야 함. 
		HttpSession session = multi.getSession();
		
		//multi에서 데이터를 추출(게시글의 텍스트 부분) from writeFrm input태그의 name 속성
		String id = multi.getParameter("bid");
		String title = multi.getParameter("btitle");
		String content = multi.getParameter("bcontents");
		String fcheck = multi.getParameter("fileCheck");
		
		//textarea는 실제 데이터의 압뒤에 공백(노이즈)이 발생하는 경우가 종종 있음 -> trim() 활용
		content = content.trim();
				
		//추출한 데이터를 dto에 삽입. 
		BoardDto board = new BoardDto();
		board.setB_id(id);
		board.setB_title(title);
		board.setB_contents(content);
		
		try {
			//1. 게시글을 db에 저장
			bDao.boardInsert(board); // 이 문장 실행 후 b_num 필드에 입력한 게시글의 번호가 저장.
			
			log.info("b_num " + board.getB_num());
			
			//2. 업로드 파일이 있을 경우 파일 저장 및 db에 정보 저장
			if(fcheck.equals("1")) {
				fileUpload(multi, board.getB_num());
			}
			
			//3. 회원의 포인트 정보 변경 및 세션 데이터 변경
			// update member set m_point = m_point + 5 where m_id=#{b_id};
			
			//해시태그 등록
			HashTagProcess htp = new HashTagProcess();
			List<TagDto> tList = htp.addTagWord(content, board.getB_num());
			
			int cnt = 0;
			for(int i= 0; i < tList.size(); i++) {
				TagDto td = tList.get(i);
				String type = td.getT_type();
				if(type.equals("NNG") || type.equals("NNP") || type.equals("NNB")) {
					bDao.tagInsert(td);
					cnt++;
				}
				if(cnt > 5) {
					break;
				}
			}
			
			view = "redirect:/list?pageNum=1";//목록의 첫페이지로 이동
			msg = "글 작성 성공";
			
		} catch (Exception e) {
			e.printStackTrace();
			
			view = "redirect:/wirteFrm";
			msg = "글 작성 실패";
		}
		
		rttr.addFlashAttribute("msg", msg);
		
		return view;
	}

	private void fileUpload(MultipartHttpServletRequest multi, int b_num) throws Exception {
		//request의 서버 정보에서 프로젝트의 폴더의 절대위치 정보 구하기. 
		String realPath = multi.getServletContext().getRealPath("/");
		
		//파일을 저장할 경로를 절대 경로에 추가
		realPath += "resources/upload/";
		log.info(realPath);
		
		//upload 폴더가 없을 경우 새로 생성.
		File folder = new File(realPath);
		if(folder.isDirectory() == false) {
			//isDirectory() : 폴더의 존재 유무 및 폴더인지 파일인지 여부 확인 메소드
			folder.mkdir(); // 폴더 생성 메소드
		}
		
		//1. 파일 정보를 db(boardFile) 테이블에 저장(글 번호, 원래 이름, 변경이름)
		//파일 정보는 hashMap을 사용하여 저장.
		Map<String, String> fmap = new HashMap<String, String>();
		fmap.put("bnum", b_num + ""); //==String.valueOf(b_num);
		
		//multi에서 file 태그의 name 값 꺼내기
		Iterator<String> files =  multi.getFileNames();
		
		while(files.hasNext()) {
			String fn = files.next();
			
			//multilple 선택 파일 처리 -> 파일 목록 가져오기
			List<MultipartFile> fList = multi.getFiles(fn);
			
			//각각의 파일을 처리
			for(int i = 0; i < fList.size(); i++) {
				MultipartFile mf = fList.get(i);
				
				//파일명 추출
				String oriname = mf.getOriginalFilename();
				
				//변경할 이름 생성
				String sysname = System.currentTimeMillis()
						+ oriname.substring(oriname.lastIndexOf("."));
				
				fmap.put("oriname", oriname);
				fmap.put("sysname", sysname);
				
				//upload 폴더 파일 저장
				File ff = new File(realPath + sysname);
				mf.transferTo(ff);
				
				//DB에 파일정보 저장
				bDao.fileInsert(fmap);
			}
		}
	}
	public ModelAndView getContent(Integer bnum) {
		mv = new ModelAndView();
		
		//글 내용 가져오기
		BoardDto board = bDao.boardSelect(bnum);
		
		//파일 목록 가져오기
		List<BfileDto> fList = bDao.fileSelect(bnum);
		
		//댓글 목록 가져오기
		List<ReplyDto> rList = bDao.replySelect(bnum);
		//조회수 수정(1씩 증가)
		
		//해시태그 목록 가져오기
		List<TagDto> tList = bDao.tagSelect(bnum);
		
		//가져온 데이터를 mv에 추가
		mv.addObject("board", board);
		mv.addObject("fList", fList);
		mv.addObject("rList", rList);
		mv.addObject("tList", tList);
		//보여질 페이지(jsp) 이름 지정
		mv.setViewName("boardContents");
		
		return mv;
	}
	
	@Transactional
	public Map<String, List<ReplyDto>> replayInsert(ReplyDto reply) {
		Map<String, List<ReplyDto>> rmap = null;
		
		try {
			//댓글 삽입
			bDao.replyInsert(reply);
			//댓글 목록 불러오기
			List<ReplyDto> rList = bDao.replySelect(reply.getR_bnum());
			
			rmap = new HashMap<String, List<ReplyDto>>();
			rmap.put("rList", rList);
		} catch (Exception e) {
			e.printStackTrace();
			rmap = null;
		}
		
		return rmap;
	}

	public void fileDowload(BfileDto bfile, HttpServletResponse response, HttpSession session) {
		//파일 저장 폴더까지의 실제 경로를 구하기
		String realPath = session.getServletContext().getRealPath("");
		realPath += "resources/upload/";
		
		//경로 + 파일 이름(sysname)
		realPath += bfile.getBf_sysname();
		
		//서버 파일 폴더에서 파일을 가지고 오는 통로(InPutStream)
		InputStream is = null;
		
		//사용자 컴퓨터로 파일을 보내는 통로(OutPutStream)
		OutputStream os = null;
		
		try {
			//파일명 인코딩 (한글 꺠짐 방지)
			String dfName = URLEncoder.encode(bfile.getBf_oriname(), "UTF-8");
			
			File file = new File(realPath);
			is = new FileInputStream(file);
			
			//인터넷을 통해 전달하기 위한 설정
			response.setContentType("application/octet-stream");
			response.setHeader("content-Disposition", "attachment; filename=\"" + dfName + "\""); 
				//(설정하는 항목 , )
				//attachment; filename="그림1.jpg"
			
				//보내는 통로 생성
				os = response.getOutputStream();
				
				//파일 전송 (byte 단위로 전송)
				byte[] buffer = new byte[1024]; //1kb
				int length;
				while ((length = is.read(buffer)) != -1) {
					//inputstream의 read 메소드는 버퍼의 크기만큼 데이터를 읽어와서 읽어온 크기를 알려주는 메소드
					//파일에서 읽어올 데이터가 없을 경우 -1을 알려줌.
					
					//읽어온 데이터를 바로 사용자 컴퓨터로 보내기.
					os.write(buffer);
				}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				os.flush();
				os.close();
				is.close();
			} catch (IOException ie) {
				ie.printStackTrace();
			}
		}
		
	}
	
	public ModelAndView updateFrm(int bnum) {
		mv = new ModelAndView();
		
		//게시글 내용 가져오기(DB)
		BoardDto board = bDao.boardSelect(bnum);
		
		//파일 목록 가져오기(DB)
		List<BfileDto> fList = bDao.fileSelect(bnum);
		
		//mv에 위 내용 추가
		mv.addObject("board", board);
		mv.addObject("fList", fList);
		
		//화면(jsp) 이름 지정.
		mv.setViewName("updateFrm");
		
		
		return mv;
	}

	@Transactional
	public Map<String, List<BfileDto>> fileDelete (String sysname, int bnum, HttpSession session) {
			Map<String, List<BfileDto>> fMap = null;
			
			//파일 삭제 (파일 정보 DB 삭제 + 실제 파일)
			//실제 파일 경로 
			String realPath = session.getServletContext().getRealPath("/");
			realPath += "resources/upload/" + sysname;
			log.info(realPath);
			
			try {
				bDao.fileDelete(sysname);
				
				File file = new File(realPath);
				
				if(file.exists()) { //파일이 있을 경우
					if(file.delete()) {//파일 삭제에 성공한 경우 
						//파일 목록 다시 가져오기
						List<BfileDto> fList = bDao.fileSelect(bnum);
						fMap = new HashMap<String, List<BfileDto>>(); 
						
						//파일 목록 맵에 삽입
						fMap.put("fList", fList);
							
					}
					else {
						fMap = null;
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				fMap = null;
			}
			
			
		return fMap;
	}
	
	@Transactional
	public String boardUpdate(MultipartHttpServletRequest multi, RedirectAttributes rttr) {
		String view = null;
		String msg = null;
		
		int bnum = Integer.parseInt(multi.getParameter("bnum"));
		
		//multi에서 데이터 추출 및 Dto에 삽입 
		BoardDto board = new BoardDto();
		board.setB_num(bnum);
		board.setB_title(multi.getParameter("btitle"));
		board.setB_contents(multi.getParameter("bcontents"));
		String check = multi.getParameter("fileCheck");
		
		try {
			bDao.boardUpdate(board);
			
			if(check.equals("1")) {
				fileUpload(multi, bnum);
			}
			
			view= "redirect:/contents?bnum=" + bnum;
			msg = "수정 성공";
			
		} catch (Exception e) {
			e.printStackTrace();
			view = "redirect:/updateFrm?bnum=" + bnum;
			msg = "수정 실패";
		}
		
		rttr.addFlashAttribute("msg", msg);
		
		return view;
	}
	
	@Transactional
	public String boardDelete(int bnum, RedirectAttributes rttr) {
		String view = null;
		String msg = null;
		
		try {
			//댓글 삭제
			bDao.replyDelete(bnum);
			//파일 목록 삭제 - db만 삭제
			bDao.fListDelete(bnum);
			//파일(실제)들 삭제(file.delete) - 
			
			//해시 태그 목록 삭제
			
			//게시글 삭제
			bDao.boardDelete(bnum);
			
			view = "redirect:/list?pageNum=1";
			msg = "삭제 성공";
			
		} catch (Exception e) {
			e.printStackTrace();
			
			view = "redirect:/contents?bnum=" + bnum;
			msg = "삭제 실패";
		}
		
		rttr.addFlashAttribute("msg", msg);
		
		return view;
	}
}//class end















