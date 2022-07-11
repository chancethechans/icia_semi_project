<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시글 상세보기</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<link rel="stylesheet" href="resources/css/style.css">
<script type="text/javascript">
$(function(){
	//메시지 출력 부분
	var msg = "${msg}";
	if(msg != ""){
		alert(msg);
	}
	
	//로그인한 회원 정보 및 로그아웃 출력
	var lname = "${mb.m_name}";
	$("#mname").html(lname + "님");
	$(".suc").css("display", "block");
	$(".bef").css("display", "none");
	
	//수정 / 삭제 버튼 처리(본인의 글이 아니면 수정/삭제 버튼 숨기기)
	$("#upbtn").hide();
	$("#delbtn").hide();
	
	var mid = "${mb.m_id}"
	var bid = "${board.b_id}"
	
	if(mid == bid) {
		$("#upbtn").show();
		$("#delbtn").show();
	}
	
});
</script>
</head>
<body>
<div class="wrap">
	<header>
	<jsp:include page="header.jsp"/>
	</header>
	
	<section>
	<div class="content">
		<div class="write-form">
			<div class="user-info">
				<div class="user-info-sub">
					<p class="grade">등급 [${mb.g_name}]</p>
					<p class="point">POINT [${mb.m_point}]</p>
				</div>
			</div>
			
			<h2 class="login-header">상세 보기</h2>
			<!-- 게시글 상세 내용 출력(div) -->
			<div>
				<div class="t_content p-15">NUM</div>
				<div class="d_content p-85">${board.b_num}</div>
			</div>
			<div>
				<div class="t_content p-15">WRITER</div>
				<div class="d_content dp-15">${board.m_name}</div>
				<div class="t_content p-15">DATE</div>
				<div class="d_content dp-30">
					<fmt:formatDate value="${board.b_date}"
							pattern ="yyyy-MM-dd HH:mm:ss"/></div>
				<div class="t_content p-15">VIEWS</div>
				<div class="d_content dp-10">${board.b_views}</div>
			</div>
			<div>
				<div class="t_content p-15">TITLE</div>
				<div class="d_content p-85">${board.b_title}</div>
			</div>
			<div>
				<div class="t_content p-15 content_h">CONTENTS</div>
				<div class="d_content p-85 content_h">
					<div class="content_in">
						${board.b_contents}
					<c:forEach var="f" items="${fList}">
					<c:choose>
						<c:when test="${fn:contains(f.bf_sysname,'.jpg')}">
							<img src="resources/upload/${f.bf_sysname}" width="100">
						</c:when>
						<c:when test="${fn:contains(f.bf_sysname,'.png')}">
							<img src="resources/upload/${f.bf_sysname}" width="100">
						</c:when>
						<c:when test="${fn:contains(f.bf_sysname,'.gif')}">
							<img src="resources/upload/${f.bf_sysname}" width="100">
						</c:when>
						<c:otherwise>사진 없음</c:otherwise>
					</c:choose>
					</c:forEach>
					</div>
					<div class="content_tag">
					<c:if test="${!empty tList}">
						<c:forEach var="t" items="${tList}">
							<a href="./list?colname=contents&keyword=${t.t_word}&pageNum=1">
							&#35;${t.t_word}
							</a>
						</c:forEach>
					</c:if>
					</div>
				</div>
			</div>
			<div>
				<div class="t_content p-15 file_h">FILES</div>
				<div class="d_content p-85 file_h" style="overflow: auto;">
					<c:if test="${empty fList}">
						첨부된 파일이 없습니다.
					</c:if>
					<c:if test="${!empty fList}">
					<c:forEach var="fitem" items="${fList}">
					<a href="./download?bf_sysname=${fitem.bf_sysname}&bf_oriname=${fitem.bf_oriname}">
						<span class="file-title">
							${fitem.bf_oriname}
						</span>
					</a>
					</c:forEach>
					</c:if>
				</div>
			</div>
			
			<table class="con_table">
				<!-- 이미지 파일 미리보기 -->
				<c:if test="${!empty fList}">
				<tr>
					<th>PREVIEW</th>
				</tr>
				<tr>
					<td>
					<c:forEach var="f" items="${fList}">
					<c:choose>
						<c:when test="${fn:contains(f.bf_sysname, '.jpg')}">
							 <!-- true/false를 내보냄 -->
						     <img src="resources/upload/${f.bf_sysname}" width="100">
						</c:when>
						<c:when test="${fn:contains(f.bf_sysname, '.png')}">
							<img src="resources/upload/${f.bf_sysname}" width="100">
						</c:when>
						<c:when test="${fn:contains(f.bf_sysname, '.gif')}">
							<img src="resources/upload/${f.bf_sysname}" width="100">
						</c:when>
						<c:otherwise>미리보기 없음</c:otherwise>
					</c:choose>
					</c:forEach>
					</td>
				</tr>
				</c:if>			
				<tr>
					<td colspan="6" align="center">
						<button class="btn-write" id="upbtn"
							onclick="location.href='./updateFrm?bnum=${board.b_num}'">U</button>
						<button class="btn-write" id="delbtn"
							onclick="delCheck('${board.b_num}')">D</button>
						<button class="btn-sub"
							onclick="backbtn();">B</button> <!-- 세션에 저장되는 pagenum -->
					</td>
				</tr>
			</table>
			<!-- 댓글 작성 양식 -->
			<form id="rFrm">
				<textarea rows="3" class="write-input ta"
					name="r_contents" id="comment"
					placeholder="댓글을 적어주세요~"></textarea>
				<input type="button" value="댓글 전송"
					class="btn-write" onclick="replyInsert(${board.b_num})"
					style="width: 100%; margin-bottom: 30px;">
			</form>
			<!-- 댓글 목록 보기 -->
			<table style="width: 100%"><!-- 제목 테이블 / 안바뀌는 부분-->
				<tr bgcolor="pink" align="center" height="30">
					<td width="20%">WRITER</td>
					<td width="50%">CONTENTS</td>
					<td width="30%">DATE</td>
				</tr>
			</table>
			<table id="rtable" style="width: 100%"><!-- 목록 테이블 / ajax로 데이터가 변경되면 바뀌는 부분 // 위와 크기는 동일하게 작성해야함 -->
				<c:forEach var="r" items="${rList}">
					<tr>
						<td width="20%">${r.r_id}</td>
						<td width="50%">${r.r_contents}</td>
						<td width="30%">
							<fmt:formatDate value="${r.r_date}"
								pattern="yyyy-MM-dd HH:mm:ss"/>
						</td>
					</tr>
				</c:forEach>
			</table>
		</div>
	</div>
	</section>
	
	<footer>
	<jsp:include page="footer.jsp"/>
	</footer>
</div>
</body>

<script src="resources/js/jquery.serializeObject.js"></script>
<!-- 댓글 전송 및 댓글 목록 새로고침용 스크립트 -->
<script type="text/javascript">
function replyInsert(bnum){
	console.log(bnum);
	
	var replyFrm = $("#rFrm").serializeObject();
	replyFrm.r_bnum = bnum; //게시글 번호 추가
	replyFrm.r_id = "${mb.m_id}";
	console.log(replyFrm);
	
	//controller에 ajax로 전송
	$.ajax({
		url: "./replyIns",
		type: "post",
		data: replyFrm,
		dataType: "json",
		success: function(result){
			console.log(result);
			var listStr = "";
			var rlist = result.rList; //json 객체에서 rList 꺼내기
			
			console.log(rlist);
			
			//목록 반복 처리
			for(var i = 0; i < rlist.length; i++) {
				listStr += "<tr>"
						+ "<td width='20%'>" + rlist[i].r_id + "</td>"
						+ "<td width='50%'>" + rlist[i].r_contents + "</td>"
						+ "<td width='30%'>" + rlist[i].r_date + "</td>"
						+ "</tr>";
			}
			$("#rtable").html(listStr);
			$("#comment").val("");
			
		},
		error: function(error){
			console.log(error);
			alert("댓글 입력 실패");
		}
	});

}

function delCheck(bnum){
	var conf = confirm("삭제하시겠습니까?");
	
	if(conf == true){
		location.href="./delete?bnum=" + bnum;
	}
}
//뒤로 가기 버튼 처리 함수

function backbtn() {
	var urlstr = "./list?";
	var col = "{list.colname}";
	var key = "{list.keyworkd}";
	
	if(col == null || col == "") { //검색을 안한경우
		urlstr += "pageNum=${pageNum}";
	}
	else { //검색을 한 경우 
		urlstr += "colname=${list.colname}&keyword=${list.keyword}" + "&pageNum=${pageNum}"; 
	}
	console.log(urlstr);
}
</script>
</html>




