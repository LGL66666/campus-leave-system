<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="com.campus.leave.model.User" %>
<%@ page import="com.campus.leave.util.ParamUtil" %>
<%
    String ctx = request.getContextPath();
    User currentUser = (User) session.getAttribute("currentUser");
    String role = currentUser == null ? "" : currentUser.getRole();
    String uri = request.getRequestURI();
    String title = (String) request.getAttribute("pageTitle");
    if (title == null) {
        title = "高校在线请假管理系统";
    }
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title><%= title %></title>
    <link rel="stylesheet" href="<%= ctx %>/assets/style.css">
</head>
<body>
<header class="topbar">
    <div class="brand">高校在线请假管理系统</div>
    <div>
        <%= currentUser == null ? "" : currentUser.getRealName() %>
        <span class="help"><%= currentUser == null ? "" : ParamUtil.roleText(currentUser.getRole()) %></span>
        &nbsp;|&nbsp;<a style="color:#fff;" href="<%= ctx %>/logout">退出</a>
    </div>
</header>
<div class="layout">
    <aside class="sidebar">
        <a class="<%= uri.endsWith("/dashboard") ? "active" : "" %>" href="<%= ctx %>/dashboard">系统首页</a>
        <% if ("ADMIN".equals(role)) { %>
        <a class="<%= uri.contains("/admin/colleges") ? "active" : "" %>" href="<%= ctx %>/admin/colleges">学院管理</a>
        <a class="<%= uri.contains("/admin/classes") ? "active" : "" %>" href="<%= ctx %>/admin/classes">班级管理</a>
        <a class="<%= uri.contains("/admin/users") ? "active" : "" %>" href="<%= ctx %>/admin/users">用户管理</a>
        <a class="<%= uri.endsWith("/leaves") ? "active" : "" %>" href="<%= ctx %>/leaves">请假记录</a>
        <a class="<%= uri.endsWith("/stats") ? "active" : "" %>" href="<%= ctx %>/stats">请假统计</a>
        <% } else if ("TEACHER".equals(role)) { %>
        <a class="<%= uri.contains("/teacher/approvals") ? "active" : "" %>" href="<%= ctx %>/teacher/approvals">请假审批</a>
        <a class="<%= uri.endsWith("/leaves") ? "active" : "" %>" href="<%= ctx %>/leaves">班级记录</a>
        <a class="<%= uri.endsWith("/stats") ? "active" : "" %>" href="<%= ctx %>/stats">请假统计</a>
        <% } else if ("STUDENT".equals(role)) { %>
        <a href="<%= ctx %>/leaves?action=new">提交请假</a>
        <a class="<%= uri.endsWith("/leaves") ? "active" : "" %>" href="<%= ctx %>/leaves">我的记录</a>
        <% } %>
    </aside>
    <main class="content">
        <h1 class="page-title"><%= title %></h1>
        <% if (request.getParameter("msg") != null) { %>
        <div class="message"><%= request.getParameter("msg") %></div>
        <% } %>
        <% if (request.getAttribute("error") != null) { %>
        <div class="message error"><%= request.getAttribute("error") %></div>
        <% } %>
