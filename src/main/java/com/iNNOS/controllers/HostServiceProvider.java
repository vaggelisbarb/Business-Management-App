package com.iNNOS.controllers;

import javafx.application.HostServices;

public class HostServiceProvider {
	
	private static HostServiceProvider hostService;
    private HostServices hostServices ;
 
    public void HostServiceProvider() {
    }
    
    public static HostServiceProvider getHostServiceProviderInstance() {
    	if (hostService == null)
    		return new HostServiceProvider();
    	return hostService;
    }
    
    public void init(HostServices hostServices) {
        if (this.hostServices != null) {
            throw new IllegalStateException("Host services already initialized");
        }
        this.hostServices = hostServices ;
    }
    
    public HostServices getHostServices() {
        if (hostServices == null) {
            throw new IllegalStateException("Host services not initialized");
        }
        return hostServices ;
    }
    
}
