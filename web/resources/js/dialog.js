/* 
 * Created by Jared Schwalbe on 2016.03.09  * 
 * Copyright © 2016 Osman Balci. All rights reserved. * 
 */

$(function () {
    // Disable focus
    $.ui.dialog.prototype._focusTabbable = function () {};

    // Open the dialog when the "choose photo" button is clicked
    $(".add-pin-button").on("click", function () {
        $("#create-pin-dialog").dialog("open");
    });

    // Disable dragging
    $(".ui-widget").draggable({disabled: true});

    // Dialog properties
    $("#create-pin-dialog").dialog({
        autoOpen: false,
        width: 300,
        resizable: false,
        modal: true
    });
});

function closeDialog() {
    $("#create-pin-dialog").dialog("close");
}