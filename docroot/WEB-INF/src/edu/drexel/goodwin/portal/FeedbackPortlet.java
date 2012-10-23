package edu.drexel.goodwin.portal;

import java.io.IOException;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletURL;
import javax.portlet.ProcessAction;
import javax.portlet.RenderMode;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.log4j.Logger;

import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.util.mail.MailEngine;
import com.liferay.util.mail.MailEngineException;

public class FeedbackPortlet extends GenericPortlet {
	private final Logger logger = Logger.getLogger(FeedbackPortlet.class);
	
	private User getCurrentUser(String userId) {
		User user = null;
		try {
			user = UserLocalServiceUtil.getUserById(Long.parseLong(userId));
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return user;
	}

	/**
	 * Renders the preferences page for the portlet.
	 * 
	 * @param request
	 * @param response
	 * @throws PortletException
	 * @throws IOException
	 */
	@RenderMode(name = "EDIT")
	public void renderPrefs(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		String subject = request.getPreferences().getValue("subject", "New suggestion");
		String toemail = request.getPreferences().getValue("to", "admin@example.com");
		String title = request.getPreferences().getValue("title", "Feedback Form");
		String message = request.getPreferences().getValue("message", "Please send us your comments.");
		String anonymous = request.getPreferences().getValue("anonymous", "noreply@example.com");
		request.setAttribute("anonymous", anonymous);
		request.setAttribute("toemail", toemail);
		request.setAttribute("subject", subject);
		request.setAttribute("title", title);
		request.setAttribute("message", message);
		getPortletContext().getRequestDispatcher("/WEB-INF/jsp/preferencesForm.jsp").include(request, response);
	}
	
	/**
	 * Renders the suggestion form or success JSP based on the value of request attribute actionStatus.
	 * 
	 * @param request
	 * @param response
	 * @throws PortletException
	 * @throws IOException
	 */
	@RenderMode(name = "VIEW")
	public void renderForm(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		// -- dispatch request to success.jsp if actionStatus is success
		if ("success".equalsIgnoreCase((String) request.getAttribute("actionStatus"))) {
			PortletURL homeUrl = response.createRenderURL();
			request.setAttribute("homeUrl", homeUrl);
			getPortletContext().getRequestDispatcher("/WEB-INF/jsp/success.jsp").include(request, response);
			return;
		}
		// -- create action and render URLs for the registration form.
		// -- Reset hyperlink fires a render request and Submit button
		// -- results in an action request
		PortletURL submitSuggestionActionUrl = response.createActionURL();
		submitSuggestionActionUrl.setParameter(ActionRequest.ACTION_NAME, "submitSuggestionAction");
		PortletURL resetRenderUrl = response.createRenderURL();
		request.setAttribute("submitSuggestionActionUrl", submitSuggestionActionUrl);
		request.setAttribute("resetRenderUrl", resetRenderUrl);
		
		String title = request.getPreferences().getValue("title", "Feedback");
		String message = request.getPreferences().getValue("message", "Please send us your comments.");
		request.setAttribute("title", title);
		request.setAttribute("message", message);
		
		getPortletContext().getRequestDispatcher("/WEB-INF/jsp/suggestionForm.jsp").include(request, response);
	}
	
	/**
	 * Saves preferences in the persistent store.
	 * 
	 * @param request
	 * @param response
	 * @throws PortletException
	 * @throws IOException
	 */
	@ProcessAction(name = "savePreferences")
	public void savePreferences(ActionRequest request, ActionResponse response) throws PortletException, IOException {
		logger.info("Inside savePreferences action method");

		PortletPreferences prefs = request.getPreferences();
		prefs.setValue("anonymous", request.getParameter("anonymous"));
		prefs.setValue("to", request.getParameter("recip"));
		prefs.setValue("subject", request.getParameter("subject"));
		prefs.setValue("title", request.getParameter("title"));
		prefs.setValue("message", request.getParameter("message"));
		
		try {
			prefs.store();
		} catch(Exception e) {
			logger.error("Unexpected exception ocurred while editing preferences: Cause : " + e.getCause() + ". Message : " + e.getMessage());
		}
	}
	
	/**
	 * Processes a suggestion submission, which causes an email to be sent to the recipients specified in the portal preferences.
	 * 
	 * @param request
	 * @param response
	 * @throws PortletException
	 * @throws IOException
	 */
	@ProcessAction(name = "submitSuggestionAction")
	public void submitSuggestion(ActionRequest request, ActionResponse response) throws PortletException, IOException {
		String subject = request.getPreferences().getValue("subject", "New Suggestion");
		String to = request.getPreferences().getValue("to", "admin@example.com");
		String from = null;
		String suggestion = request.getParameter("suggestion");
		String anonymous = request.getParameter("anonymous");
		if ("on".equals(anonymous)) {
			from = request.getPreferences().getValue("anonymous", "noreply@example.com");
		} else {
			from = getCurrentUser(request.getRemoteUser()).getEmailAddress();
		}

		try {
			sendSuggestionEmail(from, to, subject, suggestion);
			request.setAttribute("actionStatus", "success");
		} catch (Exception e) {
			request.setAttribute("errorMsg", "There was an error submitting your suggestion. Please try again.");
			request.setAttribute("actionStatus", "error");
			logger.error("Unexpected exception ocurred while sending a suggestion: Cause : " + e.getCause() + ". Message : " + e.getMessage());
		}
	}
	
	protected void sendSuggestionEmail(String from, String to, String subject, String message) throws AddressException, MailEngineException {
		System.out.println("Sending suggestion from " + from);
		InternetAddress cc[] = null;
		String recipients[] = to.split(";");
		InternetAddress fromAddress = new InternetAddress(from);
		InternetAddress toAddresses[] = new InternetAddress[recipients.length];
		
		for (int i = 0; i < toAddresses.length; i++) {
			toAddresses[i] = new InternetAddress(recipients[i]);
		}
		
		MailEngine.send(fromAddress, toAddresses, cc, subject, message);
	}
}
