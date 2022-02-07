
<%@ include file="/init.jsp" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="aui" uri="http://liferay.com/tld/aui" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %> 

<portlet:defineObjects />
<liferay-ui:success key="success" message="Usuario Registrado Correctamente"/>

<liferay-ui:error key="error" message="Error al Registrar Usuario" />

<%
	String userFullName = (String) renderRequest.getAttribute("user");
%>

<portlet:actionURL var="importUsersActionURL" name="importUsers"></portlet:actionURL>

<p>
	<b><liferay-ui:message key="Hello"/><%=userFullName%></b>
</p>


<aui:form action="<%=importUsersActionURL%>" name="importUsers" method="POST">

		<aui:fieldset label="New Users Registration">
			<aui:row>
				<aui:col width="50">
					<aui:input type="file" name="fileToUpload"/>
				</aui:col>
			</aui:row>	
		</aui:fieldset>
	<aui:button-row>
		<aui:button name="submitButton" type="submit" value="import" />
	</aui:button-row>
</aui:form>