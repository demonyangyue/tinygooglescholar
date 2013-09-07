	<%@ page language="java" contentType="text/html; charset=utf-8"
	    pageEncoding="utf-8"%>
	<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
	<%@ page import="java.util.List" %>
	<%@ page import="com.nju.app.search.*" %>
	<HTML><HEAD><TITLE>Lucene Search Engine Demo Client</TITLE>
	<META http-equiv=Content-Type content="text/html; charset=utf-8">
	<META content="MSHTML 6.00.2800.1543" name=GENERATOR>
	<style type="text/css">
	<!--
	body{margin:0;
	font-family: Arial, Helvetica, sans-serif;
		font-size: 0.8em;
	}
	td,th,div{
			font-size: 0.8em;
	}
	a{
		color: #0066FF;
	}
	a:hover{
		color: #666;
		text-decoration: none;
	}
	
	h1{
	margin:0px;
	height:70px;
	line-height:70px;
	background: #6699CC;
	color: #fff;
	border-bottom:solid 1px #006699;
	}
	
	.search{
	background:#ddeeff;
	padding-top:5px;
	padding-bottom:5px;
	border-bottom:solid 1px #9ABBCB;
	}
	form{margin:0;
	}
	.result{
	width:70%;
		clear:both;
	margin-bottom:20px;
	margin-top:20px;}
	.result h3{
			margin:0px;
			line-height: 25px;
			font-size: 1.3em;
	}
	.linked {
		padding-top: 5px;
		padding-bottom: 5px;
	}
	.linked a{
	
	margin-right: 10px;
	border:solid 1px #CCCCCC;
	padding:3px 10px 3px 10px;
	text-decoration: none;
	}
	.linked a:hover{
	
	margin-right: 10px;
	border:solid 1px #0066FF;
		background: #0066FF;
		color: #fff;
	
	}
	hr{
	
		border: dashed 1px #ddd;
			display: block;
			background:#fff;
			height: 1px;
	}
	.footer{
		font-size:0.8em;
		border-top:solid 1px #ddd;
		padding-top:10px;
	}
	.footer a{color:#666;
	text-decoration: none;}
	.footer a:hover{
		text-decoration: underline;
	}
	-->
	</style>
	</HEAD>
	<BODY>
	
	<CENTER><h1>Lucene Search Engine Demo Client</h1>
	<div class="search">
	  <FORM id=searchForm action=SearchController>
	  <TABLE>
	    <TBODY>
	      <TR>
	        <TD colspan="3">
	          <INPUT name=searchWord id=searchWord type=text size="40"> 
	          <INPUT id=doSearch type=submit value=search> 
	        </TD>
	      </TR>
	    </TBODY>
	  </TABLE>
	  </FORM>
	</div>
 <TABLE class="result">
	  <TBODY>
	  <%
	    List searchResult = (List)request.getAttribute("searchResult");
	    int resultCount = 0;
	    if(null != searchResult){
	    	resultCount = searchResult.size();
	    }
	    for(int i = 0; i < resultCount; i++){
	    	SearchResultBean resultBean = (SearchResultBean)searchResult.get(i);
	    	String title = resultBean.getPDFTitle();
	    	String path  = resultBean.getPDFPath();
	  %>
	  <TR>
	    <TD class="title"><h3><A href="<%=path %>"><%=title %></A></h3></TD>
	  </TR>
	  <tr><td><hr /></td></tr>
	  <%
	    }
	  %>
	
	</TBODY>
 </TABLE>
	
	</CENTER>
	</BODY></HTML>
