/*
 * Created by Jared Schwalbe on 2016.04.20  * 
 * Copyright © 2016 Jared Schwalbe. All rights reserved. * 
 */

/*
 * Fires when the DOM is finished loading.
 */
$(document).ready(function () {
    // Hide home button
    $("#header-links-form\\:home-btn").hide();
    // Resize the UI
    resizeWelcomeMap($(window).width(), $(window).height() - 130, 0);
});

/*
 * Fires when the window is resized by the user.
 */
$(window).resize(function () {
    // Resize the UI
    resizeWelcomeMap($(window).width(), $(window).height() - 130, 100);
});

/*
 * Adjust the heights and widths of the UI components to be window dependent.
 */ 
function resizeWelcomeMap(width, height, delay) {
    setTimeout(function () {
        // Calculate new heights based on window height
        $("#content-container").css("height", height + "px");
        $("#welcome-wrapper").css("height", height + "px");
        $("#welcome-title").css("margin-top", height/4 + "px");
        $("#welcome-map").css("height", height + "px");
    }, delay);
}