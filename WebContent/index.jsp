<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="email.validate_code_source"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Check email is valid</title>
</head>
<body>
<center>
	<h2>Check if email id exists using JAVA</h2>
	<form action="index.jsp">
		Email : <input type="text" name="email"> <input type="submit" value="Check">
	</form>
	<br>
	<%
	String email=(String)request.getParameter("email");
	if(email!=null){
		validate_code_source obj_validate_code_source=new validate_code_source();
		boolean exists=obj_validate_code_source.isAddressValid(email);
		if(exists==true){
	%>
			<h3><%=email %> : Exist </h3>	
	<%		
		}else{
			%>
			<h3><%=email %> : Does not Exist </h3>	
	<%		
		}
	}
	%>
</center>
</body>
</html>