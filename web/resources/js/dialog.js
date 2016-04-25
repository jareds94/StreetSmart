/*
 * Created by Jared Schwalbe on 2016.04.10  * 
 * Copyright © 2016 Jared Schwalbe. All rights reserved. * 
 */

/*
 * Holds the dialog properties for all dialogs used.
 */
$(function () {
    // Disable focus
    $.ui.dialog.prototype._focusTabbable = function () {};

    // Dialog properties
    $("#create-pin-dialog").dialog({
        autoOpen: false,
        width: 300,
        resizable: false,
        draggable: false,
        closeOnEscape: false,
        modal: true
    });
    
    // Dialog properties
    $("#enter-loc-dialog").dialog({
        autoOpen: false,
        width: 400,
        resizable: false,
        draggable: false,
        closeOnEscape: false,
        modal: true
    });
    
    // Dialog properties
    $("#delete-dialog").dialog({
        autoOpen: false,
        height: 225,
        width: 400,
        resizable: false,
        draggable: false,
        modal: true
    });
    
    // Dialog properties
    $("#photo-dialog").dialog({
        autoOpen: false,
        height: 286,
        width: 420,
        resizable: false,
        draggable: false,
        modal: true
    });
});

/*
 * Closes all dialogs and applies necessary styles on close.
 */ 
function closeDialogs() {
    // Close all dialogs
    $("#create-pin-dialog").dialog("close");
    $("#enter-loc-dialog").dialog("close");
    $("#delete-dialog").dialog("close");
    $("#photo-dialog").dialog("close");
    
    // Give a half second delay and then reset field styles
    setTimeout(function() {
        // Reset fields
        $("#create-pin-form\\:title").val("");
        $("#create-pin-form\\:title").css("background-color", "#ffffff");
        $("#create-pin-form\\:description").val("");
        $("#create-pin-form\\:description").css("background-color", "#ffffff");
        $("#create-pin-form\\:browse-btn").attr("value", "");
        $("#create-pin-form\\:anonymous").attr('checked', false);
        $("#enter-loc-form\\:loc").val("");
        $("#enter-loc-form\\:loc").css("background-color", "#ffffff");
    }, 500);
}

/*
 * Fired when the user submits their location as an address.
 */
function enterLocSubmit() {
    // Parse the address and return the geographic coordinates
    var geocoder = new google.maps.Geocoder();
    geocoder.geocode({'address': $("#enter-loc-form\\:loc").val()}, function (results, status) {
        if (status == google.maps.GeocoderStatus.OK) {
            userLoc = results[0].geometry.location;
            closeDialogs();
            // Submit their location to the backend
            $("#hidden-loc-form\\:user-loc-hidden").val(userLoc.toString());
            $("#hidden-loc-form\\:user-loc-submit").click();
            // Update the map center and draw the location marker
            map.setCenter(userLoc);
            drawUserLocMarker();
            // Temporary fix for distances in sidebar not working until location is set
            $("#selectfilterForm\\:map-menu-sort-select").val("pop").change();
        } else {
            // Address can't be parsed, make field red to indicate error
            $("#enter-loc-form\\:loc").css("background-color", "#f7e2e2");
        }
    });
}