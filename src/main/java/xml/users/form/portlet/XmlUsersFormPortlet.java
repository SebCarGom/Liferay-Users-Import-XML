package xml.users.form.portlet;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import java.io.File;
import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import xml.users.form.constants.XmlUsersFormPortletKeys;

/**
 * @author scarnero
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.instanceable=true",
		"javax.portlet.display-name=XmlUsersForm",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=" + XmlUsersFormPortletKeys.XMLUSERSFORM,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user"
	},
	service = Portlet.class
)
public class XmlUsersFormPortlet extends MVCPortlet {
	
	@Reference
	private MessageBus							_messageBus;

	private static Log _log = LogFactoryUtil.getLog(XmlUsersFormPortlet.class);

	public void doView(RenderRequest renderRequest, RenderResponse renderResponse)
			throws IOException, PortletException {

		User user = (User) renderRequest.getAttribute(WebKeys.USER);

		renderRequest.setAttribute("user", user.getFullName());

		super.doView(renderRequest, renderResponse);

	}

	@Override
	public void processAction(ActionRequest actionRequest, ActionResponse actionResponse)
			throws IOException, PortletException {

		try {
			importUsers(actionRequest, actionResponse);
		} catch (PortalException e) {
			e.printStackTrace();
		}


		super.processAction(actionRequest, actionResponse);
	}

	public void importUsers(ActionRequest actionRequest, ActionResponse actionResponse)
			throws IOException, PortletException, PortalException {
		
		UploadPortletRequest uploadRequest = PortalUtil.getUploadPortletRequest(actionRequest);
		File file = uploadRequest.getFile("fileToUpload");
		
		Message message = new Message();
		message.put("fileSent", file);
		message.setDestinationName(XmlUsersFormPortletKeys.XMLUSERSFORM_MESSAGE_DESTINATION);
		
		try {
		_messageBus.sendMessage(message.getDestinationName(), message);
		} catch (Exception e){
			_log.error("An exception occurred processing the file", e);
		}
		
	}	
	
}