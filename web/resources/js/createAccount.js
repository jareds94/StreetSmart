/*
 * Created by Jared Schwalbe on 2016.04.12  * 
 * Copyright © 2016 Jared Schwalbe. All rights reserved. * 
 */

/*
 * Fires when the DOM is finished loading. All of this fixes the styles for the 
 * password field because of the feedback on password strength uses stupid,
 * ugly PrimeFaces.
 */
$(document).ready(function () {
    // Give this section a slight delay
    setTimeout(function () {
        // Check if validation failed on the password input
        var valFailed = false;
        if ($("#form\\:password").hasClass("validation-failed")) {
            valFailed = true;
        }
        
        // Remove all classes added by PrimeFaces except ui-password-panel
        $("#form\\:password_panel").removeClass();
        $("#form\\:password_panel").addClass("ui-password-panel");
        // Remove all classes added by PrimeFaces
        $("#form\\:password").removeClass();
        $("#form\\:password").addClass("input-control");
        
        // Add back the validation failed class if necessary
        if (valFailed) {
            $("#form\\:password").addClass("validation-failed");
        }
        
        // Move the password_panel to just below the password input field
        $("#form\\:password_panel").detach().appendTo("#password_panel-wrapper");
    }, 200);

    // Hide password_panel when the password field is not in focus
    $("#form\\:password").bind("blur", function () {
        $("#password_panel-wrapper").css("display", "none");
    });
    $("#form\\:password").bind("focus", function () {
        $("#password_panel-wrapper").css("display", "inline-block");
    });

    // Fix the css for hover because of PrimeFaces
    $("#form\\:password").mouseover(function () {
        $("#form\\:password").css("background-color", "#ffffff");
        if ($("#form\\:password").hasClass("validation-failed")) {
            $("#form\\:password").css("background-color", "#f7e2e2");
        }
    });
});