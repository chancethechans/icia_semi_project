<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>오픈인공지는 게시판 홈</title>
<!-- 반응형 웹에 필요한~ -->
<meta name="viewport" content="width=device-width, initial-scale=1">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<link rel="stylesheet" href="resources/css/style.css">
<script type="text/javascript">

$(function(){
	var msg = "${msg}"
		if(msg != "") {
			alert(msg);
		}	
});

</script>

</head>
<body>
<div class ="wrap">

	<header>
	<jsp:include page="header.jsp"/>
	</header>
	
	<section>
	<div class="content">
		<form class="login-form" action="./loginProc" method="post">
			<h2 class="login-header">로그인</h2>
			<input type="text" class="login-input" name="m_id" autofocus required placeholder="아이디">
			<input type="password" class="login-input"
				name="m_pwd" placeholder="비밀번호" required>
			<input type="submit" class="login-btn" value="로그인">
		</form>
	</div>
	</section>
	
	<footer>
	<jsp:include page="footer.jsp"/>
	</footer>
	
</div>
</body>
</html>