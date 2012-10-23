<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page contentType="text/html" isELIgnored="false" %>
<portlet:defineObjects/>
<h1>Feedback Form Preferences</h1>
<portlet:actionURL var="savePrefUrl" name="savePreferences"/>

<form  name="<portlet:namespace/>preferencesForm" action="${savePrefUrl}" method="post"/>
<p>
	<label>Title of form<input size="80" name="title" type="text" value="<c:out value='${requestScope.title}' />"/></label>
</p>
<p>
	<label>Message on form:<input size="80" name="message" type="text" value="<c:out value='${requestScope.message}' />"/></label>
</p>
<p>
	<label>Subject for email:<input size="80" name="subject" type="text" value="<c:out value='${requestScope.subject}' />"/></label>
</p>
<p>
	<label>Email for anonymous senders:<input size="80" name="anonymous" type="text" value="<c:out value='${requestScope.anonymous}' />"/></label>
</p>
<p>
	<label>Send email to: <input size="80" type="text" value="<c:out value='${requestScope.toemail}'/>" name="recip"></label><br/>Separate multiple recipients with a semicolon.
</p>
<input  type="submit" name="submit" value="Save"/>
</form>