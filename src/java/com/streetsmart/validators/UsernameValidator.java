/*
 * Created by Hung Vu on 2016.04.07  * 
 * Copyright Â© 2016 Hung Vu. All rights reserved. * 
 */
package com.streetsmart.validators;


import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("usernameValidator")
/**
 *
 * @author Kevin
 */
public class UsernameValidator implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        
        // Typecast the password "value" entered by the user to String.
        String username = (String) value;

        if (username == null || username.isEmpty()) {
            // Do not take any action. 
            // The required="true" in the XHTML file will catch this and produce an error message.
            return;
        }
       
        // Make sure the length isn't less than 4 characters
        if (username.length() < 4) {
            throw new ValidatorException(new FacesMessage("Username must be at least 4 characters."));
        }        
    } 
    
}