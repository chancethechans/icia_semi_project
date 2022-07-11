<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시글 목록</title>
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
	
	//검색 기능 수행 코드
	$("#search").click(function(){
		var keyword = $("#keyword").val(); //id가 키워드인 값을 가져옴
		if(keyword == "") {
			alert("검색어를 입력하세요");
			return
		}
		
		var select = $("#sel").val();
		console.log(select, keyword);
		location.href="./list?colname=" + select + "&keyword=" + keyword + "&pageNum=1"; //from listDto 
	});
});
</script>
<style type="text/css">
.search-area {
	text-align: right;
	padding : 5px;
	hieght: 50px;
	line-height: 50px;
}
</style>
</head>
<body>
<div class="wrap">

	<header>
	<jsp:include page="header.jsp"/>
	</header>
	
	<section>
	<div class="content">
		<div class="board-form">
			<div class="user-info">
				<div class="user-info-sub">
					<p class="grade">등급 [${mb.g_name}]</p>
					<p class="point">POINT [${mb.m_point}]</p>
				</div>
			</div>
			
			<h2 class="login-header">게시글 목록</h2>
			
			<div class="search-area">
				<select id="sel">
					<option value="b_title" selected>제목</option>
					<option value="b_contents">내용</option>
				</select>
				<input type="text" id="keyword">
				<button id="search">검색</button>
			</div>
			
			<div class="data-area">
				<div class="title-row">
					<div class="t-no p-10">번호</div>
					<div class="t-title p-30">제목</div>
					<div class="t-name p-15">작성자</div>
					<div class="t-date p-30">작성일</div>
					<div class="t-view p-15">조회수</div>
				</div>
				
				<c:forEach var="bitem" items="${bList}"> <!-- blist로 보내주면 bitem으로 받음 -->
				<div class="data-row">
					<div class="t-no p-10">${bitem.b_num}</div>
					<div class="t-title p-30">
						<!-- 상세보기 화면 이동 url + 게시글번호 -->
						<a href="./contents?bnum=${bitem.b_num}">
							${bitem.b_title}
						</a>
					</div>
					<div class="t-name p-15">${bitem.m_name}</div>
					<div class="t-date p-30">
						<fmt:formatDate value="${bitem.b_date}"
							pattern="yyyy-MM-dd HH:mm:ss"/>
					</div>
					<div class="t-view p-15">${bitem.b_views}</div>
				</div>
				</c:forEach>
			</div>
			<div class="btn-area">
				<div class="paging">${paging}</div>
				<button class="wr-btn" onclick="location.href='./writeFrm'">글쓰기</button>
			</div>
		</div>
	</div>
	</section>
	
	<footer>
	<jsp:include page="footer.jsp"/>
	</footer>
	
</div>
</body>
</html>