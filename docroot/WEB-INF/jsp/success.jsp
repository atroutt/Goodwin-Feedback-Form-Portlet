<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page contentType="text/html" isELIgnored="false" %>
<h1>Message Successfully Sent</h1>
<p>Thank you for your message.</p>
<p>Click <a href="<c:out value='${requestScope.homeUrl}'/>">here</a> to return to the form.</p>
