<%@ page pageEncoding="UTF-8" contentType="text/html;charset=utf-8" isELIgnored="false" %>
<%@include file="util.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>Search</title>
    <script type="text/javascript">
        function t(value) {
            window.location.href = "${app}/poetry/showSearch?pageSize=" + value;
        }
    </script>
</head>

<body>
<h2>查询结果</h2>
<p><a href="${app}/poetry/outSearch">返回搜索</a></p>
<p>总信息数：${total}</p>
每页展示条数
<select id="t_size" onchange="t(this.value)">
    <c:if test="${pageSize==1}">
        <option value="1" selected>1</option>
        <option value="5">5</option>
        <option value="10">10</option>
        <option value="15">15</option>
        <option value="20">20</option>
    </c:if>
    <c:if test="${pageSize==5}">
        <option value="1">1</option>
        <option value="5" selected>5</option>
        <option value="10">10</option>
        <option value="15">15</option>
        <option value="20">20</option>
    </c:if>
    <c:if test="${pageSize==10}">
        <option value="1">1</option>
        <option value="5">5</option>
        <option value="10" selected>10</option>
        <option value="15">15</option>
        <option value="20">20</option>
    </c:if>
    <c:if test="${pageSize==15}">
        <option value="1">1</option>
        <option value="5">5</option>
        <option value="10">10</option>
        <option value="15" selected>15</option>
        <option value="20">20</option>
    </c:if>
    <c:if test="${pageSize==20}">
        <option value="1">1</option>
        <option value="5">5</option>
        <option value="10">10</option>
        <option value="15">15</option>
        <option value="20" selected>20</option>
    </c:if>
</select>
<br>
<br>
<c:if test="${nowPage==1}">
    <input type="button" class="button" value="Previous Page" disabled="disabled"/>
</c:if>
<c:if test="${nowPage>1}">
    <input type="button" class="button" value="Previous Page"
           onclick="location='${app}/poetry/showSearch?nowPage=${nowPage-1}&pageSize=${pageSize}'"/>
</c:if>
<font>Page Now : ${nowPage}</font>
<c:if test="${nowPage==maxPage||maxPage==0}">
    <input type="button" class="button" value="Next Page" disabled="disabled"/>
</c:if>
<c:if test="${nowPage<maxPage}">
    <input type="button" class="button" value="Next Page"
           onclick="location='${app}/poetry/showSearch?nowPage=${nowPage+1}&pageSize=${pageSize}'"/>
</c:if>

<p>+----------------------------------------+</p>
<c:forEach items="${poetries}" var="poetry">
    <p>标题：${poetry.title}</p>
    <p>诗人：${poetry.poet.name}</p>
    <p>${poetry.content}</p>
    <p>+----------------------------------------+</p>
</c:forEach>
</body>
</html>