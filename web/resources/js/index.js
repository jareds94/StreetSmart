/*
 * Created by Jared Schwalbe on 2016.04.05  * 
 * Copyright © 2016 Jared Schwalbe. All rights reserved. * 
 */

/*
 * Fires when the DOM is finished loading.
 */
$(document).ready(function () {
    // Resize the UI to the window size
    resizeMapComponents($(window).width(), $(window).height() - 130, 0);
    
    // Hide home button
    $("#header-links-form\\:home-btn").hide();

    // Open the create pin dialog
    $(".add-pin-button").on("click", function () {
        // Cheap way to check if the user is signed in
        if ($("#header-links-form").text().indexOf("Sign In") >= 0) {
            // Don't allow pins to be created when not signed in
            showMapMessage("You must sign in to create a pin.", 5000);
        } else {
            $("#create-pin-dialog").dialog("open");
        }
    });
    
    // Open menu when clicking the hamburger icon
    $("#show-menu").bind("click", function (e) {
        $("#map-menu").addClass("open");

        // Show side menu
        $("#map-menu").animate({
            left: "0px"
        }, 200);

        // Shrink messages
        $("#map-message-wrapper").animate({
            left: "305px",
            width: ($("#map-message-wrapper").width() - 305) + "px"
        }, 200);
    });
    
    // Close menu when clicking the close button
    $("#map-menu-close").bind("click", function (e) {
        $("#map-menu").removeClass("open");
        
        // Hide side menu
        $("#map-menu").animate({
            left: "-305px"
        }, 200);

        // Expand messages
        $("#map-message-wrapper").animate({
            left: "0px",
            width: ($("#map-message-wrapper").width() + 305) + "px"
        }, 200);
    });
    
    // Close pin details menu
    $("#full-pin-close").bind("click", function (e) {
        // Hide title popup next to pin on map
        selectedPin.siblings().css('display', 'none');
        selectedPin = null;
        
        // Hide the pin details menu
        $("#map-menu-full-pin").removeClass("open");
        $("#map-menu-full-pin").hide();
    });
    
    // Change location button
    $("#map-menu-change-loc-btn").bind("click", function (e) {
        // Parse the address entered to get the geographic coordinates
        var address = $("#map-menu-change-loc").val();
        if (address !== null && address !== "") {
            var geocoder = new google.maps.Geocoder();
            geocoder.geocode({'address': address}, function (results, status) {
                if (status === google.maps.GeocoderStatus.OK) {
                    // Update new map center
                    setMapCenter(results[0].geometry.location, 0, 0, false);
                    // Clear style changes made from error
                    $("#map-menu-change-loc").css("background-color", "#ffffff");
                    $("#map-menu-change-loc").css("border-color", "#dcdcdc");
                } else {
                    // Change text box color indicating error
                    $("#map-menu-change-loc").css("background-color", "#fad8d8");
                    $("#map-menu-change-loc").css("border-color", "#de9191");
                }
            });
        } else {
            // If nothing is in the input field, center on current location
            setMapCenter(userLoc, 0, 0, false);
        }
    });
    
    // Trigger change location on enter key
    $("#map-menu-change-loc").keyup(function(event){
        if (event.keyCode === 13){
            $("#map-menu-change-loc-btn").click();
        }
    });

    // Add "..." to overflow on pins list descriptions
    addDotDotDot();
    
    // Fires when input text field is changed corresponding to the filter by keyword
    $(document.body).on('keyup paste','#map-menu-filter-keyword-form\\:map-menu-filter-keyword-input',function() {
        // Send inputted string to the backend
        $("#map-menu-filter-keyword-form\\:filter-keyword-btn").click();        
        $("#keyword-form\\:filterPinsByKeyword").click();
        // Add "..." to overflow on pins list descriptions
        addDotDotDot();
    });
    
    // Fires when input text field is changed corresponding to the filter by distance
    $(document.body).on('keyup paste','#filterForm\\:map-menu-distance-input',function() {       
        // Send the updated input text field's property to the backend
        $("#filterForm\\:filterBtn").click(); 
        // Click the hidden command button to populate menuPinsListHidden's value field
        $("#distance-form\\:filterPinsByDistance").click();
        // Add "..." to overflow on pins list descriptions
        addDotDotDot();
    });   
    
    // Clicks a hidden commmand button which pre populates the back end
    // with filtered pin data. Makes it so the menu displays filtered pins
    // by popularity by default.
    $("#filterForm\\:pre-populate-btn").click();
});

/*
 * Fires when window is resized by the user.
 */ 
$(window).resize(function () {
    // Resize the UI to the new window size
    resizeMapComponents($(window).width(), $(window).height() - 130, 100);
});

/*
 * Adjust the heights and widths of UI components to be window dependent.
 */ 
function resizeMapComponents(width, height, delay) {
    setTimeout(function () {
        // Resize height, width will always be 100%
        $("#content-container").css("height", height + "px");

        // Resize height, width will always be 100%
        // Adjust the background position for the loading animation (slightly above center)
        $("#map-loading").css("height", height + "px");
        $("#map-loading").css("background-position", ((width / 2) - 32) + "px " + (height / 3) + "px");

        // Height will always be fixed, resize width (gets changed to pixel dependent on menu pop out)
        if ($("#map-message-wrapper").css("display") !== "none") {
            $("#map-message-wrapper").css("width", width + "px");
        }
        
        // Resize height, width will always be fixed
        $("#map-menu").css("height", (height - 40) + "px");
        $("#map-menu-full-pin").css("height", (height - 40) + "px");

        // Resize height, width will always be 100%
        $("#map").css("height", height + "px");

        // Resize widths that are dependent on menu pop out
        if ($("#map-menu").hasClass("open")) {
            if ($("#map-message-wrapper").css("display") !== "none") {
                $("#map-message-wrapper").css("width", width + "px");
            }
        }
        
        // Resize pins list height
        var pinsListHeight = 237;
        if ($("#selectfilterForm\\:map-menu-sort-select").find(":selected").text() === "Keyword") {
            pinsListHeight = 285;
        } else if ($("#selectfilterForm\\:map-menu-sort-select").find(":selected").text() === "Distance") {
            pinsListHeight = 270;
        }
        $("#map-menu-pins-list").height(height - pinsListHeight);
        
        // Resize comments list height
        var commentsListHeight = $("#render-full-pin").height() + 10;
        if ($("#comment\\:full-pin-post-comment").css("display") !== "none") {
            commentsListHeight += 80;
        }
        $("#full-pin-comments-wrapper").height(height - commentsListHeight);
        
        
        // Reposition dialogs to center
        $("#enter-loc-dialog").dialog("option", "position", {my: "center", at: "center", of: window});
        $("#create-pin-dialog").dialog("option", "position", {my: "center", at: "center", of: window});
        $("#photo-dialog").dialog("option", "position", {my: "center", at: "center", of: window});
        $("#delete-dialog").dialog("option", "position", {my: "center", at: "center", of: window});
    }, delay);
}

/*
 * Adds "..." to overflow on pins list description.
 */
function addDotDotDot() {
    // Slight delay to allow the list to load
    setTimeout(function() {
        $(".pins-list-pin-desc").each(function (i, obj) {
            $(this).dotdotdot();
        });
    }, 600);
}