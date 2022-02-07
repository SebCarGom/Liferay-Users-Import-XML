package xml.users.form.constants.users;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.DocumentException;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import xml.users.form.portlet.XmlUsersFormPortlet;

public class UsersImporter {

	private static Log _log = LogFactoryUtil.getLog(XmlUsersFormPortlet.class);

	public void createUser(String firstName, String middleName, String lastName, String screenName, String birthday,
			String gender, String email, String password1, String password2, String jobTitle) throws PortalException {

		boolean male;

		if (gender.equals("male")) {
			male = true;
		} else if (gender.equals("female")) {
			male = false;
		} else {
			male = true;
		}

		long[] groupIds = null;
		long[] organizationIds = {};
		long[] roleIds = {};
		long[] userGroupIds = null;

		String[] birthdayParts = birthday.split("-");
		int birthdayMonth = Integer.parseInt(birthdayParts[1]);
		int birthdayDay = Integer.parseInt(birthdayParts[0]);
		int birthdayYear = Integer.parseInt(birthdayParts[2]);

		ServiceContext serviceContext = new ServiceContext();
		Date date = new Date();
		serviceContext.setUuid(UUID.randomUUID().toString());
		serviceContext.setCreateDate(date);
		serviceContext.setModifiedDate(date);

		UserLocalServiceUtil.addUser(0L, PortalUtil.getDefaultCompanyId(), false, password1, password2, true,
				screenName, email, 0L, "", LocaleUtil.getDefault(), firstName, middleName, lastName, 0L, 1L, male,
				birthdayMonth, birthdayDay, birthdayYear, jobTitle, groupIds, organizationIds, roleIds, userGroupIds,
				false, serviceContext);
	}

	public boolean isXMLValid(File file, URL xsdFilePath) {
		boolean isValid = false;
		try {
			Source xmlFile = new StreamSource(file);
			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = schemaFactory.newSchema(xsdFilePath);
			javax.xml.validation.Validator validator = schema.newValidator();
			validator.validate(xmlFile);
			isValid = true;
		} catch (IOException e) {
			_log.error("IO Exception", e);
		} catch (SAXException e) {
			_log.error("Incorrect XML or XSD file", e);

		}
		return isValid;
	}

	public void importXML(Message message) throws PortalException {
		File file = (File) message.get("fileSent");

		URL url = getClass().getClassLoader().getResource("/xsd/user.xsd");
		_log.info(url.getPath());
		if (!isXMLValid(file, url)) {
			_log.info("error");
			return;
		}

		Document document = null;

		try {
			document = SAXReaderUtil.read(file);
		} catch (DocumentException e) {
			_log.error("Cannot read file: " + file.getName(), e);
		}

		Element root = document.getRootElement();

		List<Element> userList = root.elements("user");

		for (Element users : userList) {

			createUser(users.element("firstName").getData().toString(),
					users.element("middleName").getData().toString(), users.element("lastName").getData().toString(),
					users.element("screenName").getData().toString(), users.element("birthday").getData().toString(),
					users.element("sex").getData().toString(), users.element("email").getData().toString(),
					users.element("password").getData().toString(),
					users.element("confirmPassword").getData().toString(),
					users.element("jobTitle").getData().toString());

		}
	}

}
