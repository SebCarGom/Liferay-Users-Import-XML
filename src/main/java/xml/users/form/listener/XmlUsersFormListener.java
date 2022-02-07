package xml.users.form.listener;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.messaging.MessageListenerException;

import org.osgi.service.component.annotations.Component;

import xml.users.form.constants.XmlUsersFormPortletKeys;
import xml.users.form.constants.users.UsersImporter;
import xml.users.form.portlet.XmlUsersFormPortlet;

@Component(immediate = true, property = { "destination.name="
		+ XmlUsersFormPortletKeys.XMLUSERSFORM_MESSAGE_DESTINATION }, service = MessageListener.class)
public class XmlUsersFormListener implements MessageListener {

	private static Log _log = LogFactoryUtil.getLog(XmlUsersFormPortlet.class);

	@Override
	public void receive(Message message) throws MessageListenerException {
		try {
			doReceive(message);
		} catch (Exception e) {
			_log.error(e);
		}

	}

	public void doReceive(Message message) throws Exception {
		UsersImporter importer = new UsersImporter();

		importer.importXML(message);
	}

}
