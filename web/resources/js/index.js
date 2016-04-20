// Fired when DOM is finished loading
$(document).ready(function () {
    resizeMapComponents($(window).width(), $(window).height() - 130, 0);
    
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
    $("#map-menu-pin-close").bind("click", function (e) {
        selectedPin.siblings().hide();
        selectedPin = null;
        
        $("#map-menu-pin").removeClass("open");
        $("#map-menu-pin").hide();
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
    
    //$(".pins-list-pin-desc").dotdotdot();
    
    $( "#map-menu-distance-input" ).change(function() {
        
        /* TODO: Handle removal of previously inserted child elements to
         * properly update list */
        if(document.getElementById("pins-list-pin-wrapper")) {
            $('pins-list-pin-wrapper').remove();
        }
        
        $('#hidden\\:menuPinsListHidden').val(" ");
        $("#hidden\\:hiddenUpdatePins").click();
        var pinMenuList = $('#hidden\\:menuPinsListHidden').val();
        
        for (i = 0; i < pinMenuList.length; i++) { 
            
            // Create pin list div
            var pinListDiv = document.getElementById("map-menu-pins-list");

            // Create pin list wrapper div
            var pinListWrapper = document.createElement("div");
            pinListWrapper.className = "pins-list-pin-wrapper";

            // Append pin list wrapper inside div 
            pinListDiv.appendChild(pinListWrapper);

            // Create pin list pin text wrapper
            var pinTextWrapper = document.createElement("div");
            pinTextWrapper.className = "pins-list-pin-text-wrapper";

            // Append it to div
            pinListWrapper.appendChild(pinTextWrapper);

            var pinTitle = document.createElement("div");
            pinTitle.className = "pins-list-pin-title";

            var pinDescription = document.createElement("div");
            pinDescription.className = "pins-list-pin-desc";

            pinTitle.innerHTML = "Pin title here";
            pinDescription.innerHTML = "Pin description here";

            pinTextWrapper.appendChild(pinTitle);
            pinTextWrapper.appendChild(pinDescription);
        }

        // Pin picture
        // var pic = document.createElement("img");
        //pic.className = "pin-pic";
        //pic.src = this.imgsrc;
        //div.appendChild(pic);
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
        
        // Resize pins list height
        $("#map-menu-pins-list").height(height - 320);
        
        // Reposition dialogs
        $("#enter-loc-dialog").dialog("option", "position", {my: "center", at: "center", of: window});
        $("#create-pin-dialog").dialog("option", "position", {my: "center", at: "center", of: window});
    }, delay);
}

/* Updates menu pin listing. */
function updateMapMenuPinsList() {
    
    
}