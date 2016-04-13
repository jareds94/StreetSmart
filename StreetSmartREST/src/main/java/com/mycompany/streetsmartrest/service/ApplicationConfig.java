/*
 * Created by Mykhaylo Bulgakov on 2016.04.12  * 
 * Copyright © 2016 Mykhaylo Bulgakov. All rights reserved. * 
 */
package com.mycompany.streetsmartrest.service;

import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author mybu
 */
@javax.ws.rs.ApplicationPath("webresources")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(com.mycompany.streetsmartrest.service.PinFacadeREST.class);
        resources.add(com.mycompany.streetsmartrest.service.UserFacadeREST.class);
    }
    
}
