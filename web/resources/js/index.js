// Fired when DOM is finished loading
$(document).ready(function () {
    resizeMapComponents($(window).width(), $(window).height() - 130, 0);
    
    // Hide home button
    $("#header-links-form\\:home-btn").hide();
    
    // Add pin hover mobile fix
    $(".add-pin-button").bind("touchstart touchend mouseover mouseout", function (e) {
        e.preventDefault();
        $(this).toggleClass("add-pin-button-hover");
    });
    
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
    $("#map-menu-pin-close").bind("click", function (e) {
        $("#map-menu-pin").removeClass("open");
        $("#map-menu-pin").hide();
    });
    
    // Change location button
    $("#map-menu-change-loc-btn").bind("click", function (e) {
        var address = $("#map-menu-change-loc").val();
        if (address !== null && address !== "") {
            setMapCenterFromAddress(address);
        } else {
            setMapCenterCurrLoc();
        }
    });
    
    // Trigger change location on enter
    $("#map-menu-change-loc").keyup(function(event){
        if (event.keyCode == 13){
            $("#map-menu-change-loc-btn").click();
        }
    });
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
        $("#map-menu-pin").css("height", (height - 40) + "px");

        // Resize height, width will always be 100%
        $("#map").css("height", height + "px");

        // Resize widths that are dependent on menu pop out
        if ($("#map-menu").hasClass("open")) {
            if ($("#map-message-wrapper").css("display") !== "none") {
                $("#map-message-wrapper").css("width", width + "px");
            }
        }
    }, delay);
}