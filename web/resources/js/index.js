// Fired when DOM is finished loading
$(document).ready(function () {
    resizeMapComponents($(window).width(), $(window).height() - 130, 0);
    
    // TESTING LOCAL ONLY - REMOVE WHEN DEPLOYING
    /*
    $("#hidden-pin-form\\:pin-id-hidden-1").val("23");
    $("#hidden-pin-form\\:pin-id-hidden-2").val("23");
    $("#hidden-pin-form\\:pin-id-submit").click();
    */
    
    // Hide home button
    $("#header-links-form\\:home-btn").hide();
    
    // Open menu
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
    
    // Close menu
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
        selectedPin.siblings().hide();
        selectedPin = null;
        
        $("#map-menu-full-pin").removeClass("open");
        $("#map-menu-full-pin").hide();
    });
    
    // Change location button
    $("#map-menu-change-loc-btn").bind("click", function (e) {
        var address = $("#map-menu-change-loc").val();
        if (address !== null && address !== "") {
            var geocoder = new google.maps.Geocoder();
            geocoder.geocode({'address': address}, function (results, status) {
                if (status === google.maps.GeocoderStatus.OK) {
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
            setMapCenter(userLoc, 0, 0, false);
        }
    });
    
    // Trigger change location on enter
    $("#map-menu-change-loc").keyup(function(event){
        if (event.keyCode === 13){
            $("#map-menu-change-loc-btn").click();
        }
    });

    $(".pins-list-pin-desc").each(function(i, obj) {
        $(this).dotdotdot();
    });
    
    /* Fired when input text field is changed corresponding to the filter by
     * keyword */
    $(document.body).on('change keyup paste','#map-menu-filter-keyword-form\\:map-menu-filter-keyword-input',function() {
        $("#map-menu-filter-keyword-form\\:filter-keyword-btn").click();        
        $("#map-menu-pins-list-form\\:filterPinsByKeyword").click();
        $(".pins-list-pin-desc").each(function (i, obj) {
            $(this).dotdotdot();
        });
    });
    
    /* Fired when input text field is changed corresponding to the filter
     * by distance */
    $(document.body).on('change keyup paste','#filterForm\\:map-menu-distance-input',function() {       
        /* Send the updated input text field's property to the backend. */
        $("#filterForm\\:filterBtn").click(); 
        /* Click the hidden command button to populate menuPinsListHidden's
         * value field. */  
        $("#map-menu-pins-list-form\\:filterPinsByDistance").click();
        $(".pins-list-pin-desc").each(function (i, obj) {
            $(this).dotdotdot();
        });
    });   
    // Clicks a hidden commmand button which pre populates the back end
    // with filtered pin data. Makes it so the menu displays filtered pins
    // by popularity by default.
    $("#filterForm\\:pre-populate-btn").click();
});

// Fired when window is resized by the user
$(window).resize(function () {
    resizeMapComponents($(window).width(), $(window).height() - 130, 100);
});

// Adjust the heights and width to be window dependent
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
        $("#map-menu-pins-list").height(height - 320);
        
        // Resize comments list height
        $("#full-pin-comments-wrapper").height(height - 340);
        
        // Reposition dialogs
        $("#enter-loc-dialog").dialog("option", "position", {my: "center", at: "center", of: window});
        $("#create-pin-dialog").dialog("option", "position", {my: "center", at: "center", of: window});
        $("#photo-dialog").dialog("option", "position", {my: "center", at: "center", of: window});
        $("#delete-dialog").dialog("option", "position", {my: "center", at: "center", of: window});
    }, delay);
}