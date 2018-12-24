<%@ page pageEncoding="UTF-8" contentType="text/html;charset=utf-8" isELIgnored="false" %>
<%@include file="util.jsp" %>

<html>
<head>
    <title>ToSearch</title>
</head>
<body>
<h2>诗词查询</h2>
<form action="${app}/poetry/search" method="post">
    <input type="text" name="search">
    <br>
    <br>
    按内容查询：<input type="checkbox" name="indexs" value="content" checked="checked">
    <br>
    按诗人查询：<input type="checkbox" name="indexs" value="poet">
    <br>
    按标题查询：<input type="checkbox" name="indexs" value="title">
    <br>
    <br>
    <input type="submit" value="Search">
</form>
</body>
</html>