<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page contentType="text/html" isELIgnored="false" %>

<h1>${requestScope.title}</h1>
<p>${requestScope.message}</p>

<form  name="suggetionForm" action="<c:out value='${submitSuggestionActionUrl}'/>" method="post"/>
<p>
<textarea cols="80" rows="20" name="suggestion">
</textarea>
</p>
<p>
<input type="submit" name="submit" value="Send"/>       
&nbsp;<a href="<c:out value='${requestScope.resetRenderUrl}'/>"><Strong>Reset</Strong></a>
<br/>
<br/>
<label><input type="checkbox" name="anonymous" style="vertical-align:text-bottom"> Make Anonymous (Honestly!)</label>
</p>
</form>