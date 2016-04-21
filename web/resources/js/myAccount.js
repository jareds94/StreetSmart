$(document).ready(function () {
    // Hide last two rows
    $("#account-details-table tr:last td").hide();
    $("#account-details-table tr:last").prev().find("td").hide();
    // Hide save and cancel buttons
    $("#account-form\\:edit-account-save-btn").hide();
    $("#account-form\\:edit-account-cancel-btn").hide();
    // Disable fields
    $("#account-form\\:first-name").prop("disabled", true);
    $("#account-form\\:last-name").prop("disabled", true);
    $("#account-form\\:email").prop("disabled", true);
    
    // Fix time display for each pin
    $(".pin-time-posted").each(function(i, obj) {
        var timestamp = $(this).text();
        var d = new Date(0);
        d.setUTCSeconds(timestamp);
        
        var monthNames = ["January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        ];
        var formatted = monthNames[d.getMonth()] + " " + d.getDate() + ", " + d.getFullYear();
        $(this).text(formatted);
    });
    
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
});