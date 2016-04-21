/* 
 * Created by Jared Schwalbe on 2016.03.09  * 
 * Copyright © 2016 Osman Balci. All rights reserved. * 
 */

$(function () {
    // Disable focus
    $.ui.dialog.prototype._focusTabbable = function () {};

    // Open the create pin dialog
    $(".add-pin-button").on("click", function () {
        if ($("#header-links-form").text().indexOf("Sign In") >= 0) {
            showMapMessage("You must sign in to create a pin.", 5000);
        } else {
            $("#create-pin-dialog").dialog("open");
        }
    });
    
    // Open delete dialog
    $("#account-form\\:delete-account-btn").on("click", function () {
        $("#delete-dialog").dialog("open");
    });

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
});

function closeDialogs() {
    $("#create-pin-dialog").dialog("close");
    $("#enter-loc-dialog").dialog("close");
    $("#delete-dialog").dialog("close");
    
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

function enterLocSubmit() {
    var geocoder = new google.maps.Geocoder();
    geocoder.geocode({'address': $("#enter-loc-form\\:loc").val()}, function (results, status) {
        if (status == google.maps.GeocoderStatus.OK) {
            userLoc = results[0].geometry.location;
            closeDialogs();
            $("#hidden\\:userLocationHidden").val(userLoc.toString());
            $("#hidden\\:hiddenSubmit").click();
            map.setCenter(userLoc);
            drawUserLocMarker();
        } else {
            $("#enter-loc-form\\:loc").css("background-color", "#f7e2e2");
        }
    });
}