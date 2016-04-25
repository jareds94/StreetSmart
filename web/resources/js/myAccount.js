/*
 * Created by Jared Schwalbe on 2016.04.11  * 
 * Copyright © 2016 Jared Schwalbe. All rights reserved. * 
 */

/*
 * Fires when the DOM is finished loading.
 */
$(document).ready(function () {
    // Hide last two rows in table (password and confirm password)
    $("#account-details-table tr:last td").hide();
    $("#account-details-table tr:last").prev().find("td").hide();
    // Hide save and cancel buttons
    $("#account-form\\:edit-account-save-btn").hide();
    $("#account-form\\:edit-account-cancel-btn").hide();
    // Disable fields
    $("#account-form\\:first-name").prop("disabled", true);
    $("#account-form\\:last-name").prop("disabled", true);
    $("#account-form\\:email").prop("disabled", true);
    
    // Display a message if there are no pins
    if ($('.pin-wrapper').length === 0) {
        var div = document.createElement("div");
        div.textContent = "No pins to display.";
        $("#pins-list-wrapper").append(div);
    }
    
    // Edit account button click
    $("#account-form\\:edit-account-btn").bind("click", function(e) {
        // Enable fields
        $("#account-form\\:first-name").prop("disabled", false);
        $("#account-form\\:last-name").prop("disabled", false);
        $("#account-form\\:email").prop("disabled", false);
        // Show password fields
        $("#account-details-table tr:last td").show();
        $("#account-details-table tr:last").prev().find("td").show();
        // Show/hide appropriate buttons
        $("#account-form\\:edit-account-btn").hide();
        $("#account-form\\:delete-account-btn").hide();
        $("#account-form\\:edit-account-save-btn").show();
        $("#account-form\\:edit-account-cancel-btn").show();
    });
    
    // Open photo edit dialog
    $("#account-photo-edit-btn").bind("click", function(e) {
        $("#photo-dialog").dialog("open");
    });
    
    // Open delete dialog
    $("#account-form\\:delete-account-btn").on("click", function () {
        $("#delete-dialog").dialog("open");
    });
});