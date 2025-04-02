package org.toshiba;

import org.jupnp.DefaultUpnpServiceConfiguration;
import org.jupnp.UpnpService;
import org.jupnp.UpnpServiceImpl;
import org.jupnp.controlpoint.ControlPoint;
import org.jupnp.model.meta.LocalDevice;
import org.jupnp.model.meta.RemoteDevice;
import org.jupnp.registry.Registry;
import org.jupnp.registry.RegistryListener;
import org.jupnp.model.message.header.UpnpHeader;
import org.jupnp.model.message.header.STAllHeader;

public class Main {
	// exec:java -Dexec.mainClass="org.toshiba.Main"
	public static void main(String[] args) throws InterruptedException {
		 UpnpService upnpService = new UpnpServiceImpl(new DefaultUpnpServiceConfiguration());
		 
		 upnpService.startup();
		 
		 Registry registry = upnpService.getRegistry();
	        if (registry == null) {
	            System.err.println("Failed to initialize UPnP registry!");
	            return;
	        }
	        
		 RegistryListener listener = new RegistryListener() {
	            @Override
	            public void remoteDeviceDiscoveryStarted(Registry registry, RemoteDevice device) {
	                System.out.println("Discovery started: " + device.getDisplayString());
	            }

	            @Override
	            public void remoteDeviceDiscoveryFailed(Registry registry, RemoteDevice device, Exception ex) {
	                System.out.println("Discovery failed: " + device.getDisplayString() + " => " + ex);
	            }

	            @Override
	            public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
	                System.out.println("Remote device added: " + device.getDisplayString());
	            }

	            @Override
	            public void remoteDeviceUpdated(Registry registry, RemoteDevice device) {
	                System.out.println("Remote device updated: " + device.getDisplayString());
	            }

	            @Override
	            public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
	                System.out.println("Remote device removed: " + device.getDisplayString());
	            }

	            @Override
	            public void localDeviceAdded(Registry registry, LocalDevice device) {
	                System.out.println("Local device added: " + device.getDisplayString());
	            }

	            @Override
	            public void localDeviceRemoved(Registry registry, LocalDevice device) {
	                System.out.println("Local device removed: " + device.getDisplayString());
	            }

	            @Override
	            public void beforeShutdown(Registry registry) {
	                System.out.println("Before shutdown, the registry has devices: " + registry.getDevices().size());
	            }

	            @Override
	            public void afterShutdown() {
	                System.out.println("Shutdown of registry complete!");
	            }
	        };
	        
        upnpService.getRegistry().addListener(listener);

        ControlPoint controlPoint = upnpService.getControlPoint();
        controlPoint.search((UpnpHeader) new STAllHeader());

        System.out.println("Waiting 10 seconds before shutting down...");
        Thread.sleep(10000);

        System.out.println("Stopping JUPnP...");
        upnpService.shutdown();
    }
	
}
