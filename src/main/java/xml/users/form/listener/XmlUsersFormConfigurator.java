package xml.users.form.listener;

import com.liferay.portal.kernel.concurrent.CallerRunsPolicy;
import com.liferay.portal.kernel.concurrent.RejectedExecutionHandler;
import com.liferay.portal.kernel.concurrent.ThreadPoolExecutor;
import com.liferay.portal.kernel.messaging.Destination;
import com.liferay.portal.kernel.messaging.DestinationConfiguration;
import com.liferay.portal.kernel.messaging.DestinationFactory;
import com.liferay.portal.kernel.util.HashMapDictionary;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import xml.users.form.constants.XmlUsersFormPortletKeys;

@Component(immediate = true, service = XmlUsersFormConfigurator.class)
public class XmlUsersFormConfigurator {
	@Activate
	protected void activate(BundleContext bundleContext) {
		
		_bundleContext = bundleContext;
		
		// Create a DestinationConfiguration for parallel destinations.
		
		DestinationConfiguration destinationConfiguration = new DestinationConfiguration(
				DestinationConfiguration.DESTINATION_TYPE_PARALLEL,
				XmlUsersFormPortletKeys.XMLUSERSFORM_MESSAGE_DESTINATION);
		
		// Set the DestinationConfiguration's max queue size and
		// rejected execution handler.
		destinationConfiguration.setMaximumQueueSize(10);
		
		RejectedExecutionHandler rejectedExecutionHandler = new CallerRunsPolicy() {
			
			@Override
			public void rejectedExecution(Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {
				
				// if (_log.isWarnEnabled()) {
				// _log.warn(
				// "The current thread will handle the request " +
				// "because the graph walker's task queue is at " +
				// "its maximum capacity");
				// }
				super.rejectedExecution(runnable, threadPoolExecutor);
			}
			
		};
		
		destinationConfiguration.setRejectedExecutionHandler(rejectedExecutionHandler);
		
		// Create the destination
		
		Destination destination = _destinationFactory.createDestination(destinationConfiguration);
		
		// Add the destination to the OSGi service registry
		
		Dictionary<String, Object> properties = new HashMapDictionary<>();
		
		properties.put("destination.name", destination.getName());
		
		ServiceRegistration<Destination> serviceRegistration = _bundleContext.registerService(Destination.class,
				destination, properties);
		
		// Track references to the destination service registrations
		
		_serviceRegistrations.put(destination.getName(), serviceRegistration);
	}
	
	@Deactivate
	protected void deactivate() {
		
		// Unregister and destroy destinations this component unregistered
		
		for (ServiceRegistration<Destination> serviceRegistration : _serviceRegistrations.values()) {
			
			Destination destination = _bundleContext.getService(serviceRegistration.getReference());
			
			serviceRegistration.unregister();
			
			destination.destroy();
			
		}
		
		_serviceRegistrations.clear();
		
	}
	
	private BundleContext										_bundleContext;
	
	@Reference
	private DestinationFactory									_destinationFactory;
	
	private final Map<String, ServiceRegistration<Destination>>	_serviceRegistrations	= new HashMap<>();
}
