/*
 * Created by Jared Schwalbe on 2016.04.05  * 
 * Copyright © 2016 Jared Schwalbe. All rights reserved. * 
 */

// Global variables
var map;
var userLoc;
var selectedPin;
var parent;

/*
 * Fires when the Google Maps Map is initialized.
 */
function initialize() {
    // Function level variables
    map = null;
    userLoc = null;
    var locationError = false;
    var locationDialogOpen = false;

    // Turn off points of interest
    var myStyles = [
        {
            featureType: "poi",
            elementType: "labels",
            stylers: [{visibility: "off"}]
        }
    ];

    // Create map (center on drillfield until we get the user's location)
    map = new google.maps.Map(document.getElementById('map'), {
        center: {lat: 37.2277411, lng: -80.422268},
        zoom: 15,
        disableDefaultUI: true,
        styles: myStyles
    });

    // Fires when map is finished loading
    google.maps.event.addListenerOnce(map, 'tilesloaded', function () {
        // Hide loading overlay
        $("#map-loading").hide();
        
        // Open the enter location dialog if we couldn't get their location
        if (locationError && !locationDialogOpen) {
            $("#enter-loc-dialog").dialog("open");
        }
    });
    
    // Initialize map by centering on current location and showing location dot
    // First check here is to see if we've already saved their location
    if ($("#hidden-loc-form\\:user-loc-hidden").val() !== "") {
        // Parse stored user location
        var value = $("#hidden-loc-form\\:user-loc-hidden").val();
        value = value.replace("(", "");
        value = value.replace(")", "");
        var split = value.split(", ");
        // Set user location, center on it, and display the location marker
        userLoc = new google.maps.LatLng(split[0], split[1]);
        map.setCenter(userLoc);
        drawUserLocMarker();
        // Temporary fix for distances in sidebar not working until location is set
        $("#selectfilterForm\\:map-menu-sort-select").val("pop").change();
    } else {
        // Try HTML5 geolocation
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(function (position) {
                // Get user's location in lat and long
                userLoc = {
                    lat: position.coords.latitude,
                    lng: position.coords.longitude
                };
                // Send the location to the backend
                $("#hidden-loc-form\\:user-loc-hidden").val("(" + userLoc.lat + ", " + userLoc.lng + ")");
                $("#hidden-loc-form\\:user-loc-submit").click();
                // Center map on location and show the location marker
                map.setCenter(userLoc);
                drawUserLocMarker();
                // Temporary fix for distances in sidebar not working until location is set
                $("#selectfilterForm\\:map-menu-sort-select").val("pop").change();
            }, function() {
                // HTML5 geolocation failed - show dialog to enter location manually
                locationError = true;
                if ($("#map-loading").css("display") === "none") {
                    $("#enter-loc-dialog").dialog("open");
                    locationDialogOpen = true;
                }
            });
        }
    }
    
    
    // ---------------------------
    // #region CustomMarker Object
    // ---------------------------

    // CustomMarker constructor
    // NOTE: Modified from a stack overflow answer.
    // 
    // @param latlng google.maps.LatLng object
    // @param map google.maps.Map object
    // @param args other arguments (just using this for marker_id)
    // @param imgsrc path to image for pin
    // @param text string to be display next to pin
    // @param expand boolean if the pin should be clicked immediately
    function CustomMarker(latlng, map, args, imgsrc, text, expand) {
        this.latlng = latlng;
        this.args = args;
        this.setMap(map);
        this.imgsrc = imgsrc;
        this.text = text;
        this.expand = expand;
    }

    // Extend OverlayView class
    CustomMarker.prototype = new google.maps.OverlayView();

    // Draw pin on map
    CustomMarker.prototype.draw = function () {
        var self = this;
        var div = this.div;

        if (!div) {
            // Create pin div
            div = this.div = document.createElement("div");
            div.className = "pin-wrapper";

            // Pin picture
            var picWrapper = document.createElement("div");
            picWrapper.className = "pin-pic-wrapper";
            var pic = document.createElement("img");
            pic.className = "pin-pic";
            pic.src = this.imgsrc;
            picWrapper.appendChild(pic);
            div.appendChild(picWrapper);

            // Pin text arrow
            var textArrow = document.createElement("div");
            textArrow.className = "pin-text-arrow";
            div.appendChild(textArrow);

            // Pin text
            var text = document.createElement("div");
            text.className = "pin-text";
            text.textContent = this.text;
            div.appendChild(text);
            
            if (typeof (self.args.marker_id) !== 'undefined') {
                div.dataset.marker_id = self.args.marker_id;
            }
            
            // Add pin to the overlayImage pane
            var panes = this.getPanes();
            panes.overlayImage.appendChild(div);

            // Fires when the pin image wrapper is clicked
            google.maps.event.addDomListener(div.firstChild, "click", function (event) {
                // Clear selectedPin and hide text
                if (selectedPin !== null && selectedPin !== undefined) {
                    panes.floatPane.removeChild(parent);
                    panes.overlayImage.appendChild(parent);
                    selectedPin.siblings().css('display', 'none');
                }
                // Set selected pin & its parent node
                parent = div;
                selectedPin = $(this);
                // Weird bug has the pin image on the wrong side of the text
                selectedPin.css('display', 'none');
                selectedPin.css('display', 'inline-block');
                // Show text
                selectedPin.siblings().css('display', 'inline-block');
                // Add the pin to the floatPane (higher z-index than overlayImage)
                if (div.parentNode === panes.overlayImage) {
                    panes.overlayImage.removeChild(div);
                    panes.floatPane.appendChild(div);
                }
                
                // Send the current pin id to the backend
                $("#hidden-pin-form\\:pin-id-hidden-1").val(self.args.marker_id);
                $("#hidden-pin-form\\:pin-id-hidden-2").val(self.args.marker_id);
                $("#hidden-pin-form\\:pin-id-submit").click();
                
                // Force a resize of UI to get new comments list height
                resizeMapComponents($(window).width(), $(window).height() - 130, 300);
                
                // Show pin details
                if (!$("#map-menu-full-pin").hasClass("open")) {
                    $("#map-menu-full-pin").addClass("open");
                    $("#map-menu-full-pin").show();
                }

                // Pop out menu
                if (!$("#map-menu").hasClass("open")) {
                    $("#map-menu").addClass("open");

                    // Pop out side menu
                    $("#map-menu").animate({
                        left: "0px"
                    }, 200);

                    // Move messages over
                    $("#map-message-wrapper").animate({
                        left: "305px",
                        width: ($("#map-message-wrapper").width() - 305) + "px"
                    }, 200);

                    setMapCenter(self.latlng, 150, 0, true);
                } else {
                    setMapCenter(self.latlng, 150, 0, true);
                }
            });
        }

        // Position div to have the center of the profile picture
        // be the coordinates for the pin.
        var point = this.getProjection().fromLatLngToDivPixel(this.latlng);
        if (point) {
            div.style.left = (point.x - 22) + 'px';
            div.style.top = (point.y - 22) + 'px';
        }
        
        // Click the pin immediately
        if (this.expand) {
            this.div.firstChild.click();
            this.expand = false;
        }
        
        // Remove the id param from the URL as this causes problems later
        removeUrlParameter("id");
    };

    // Remove pin from map
    CustomMarker.prototype.remove = function () {
        if (this.div) {
            this.div.parentNode.removeChild(this.div);
            this.div = null;
        }
    };

    // ------------------------------
    // #endregion CustomMarker Object
    // ------------------------------
    
    
    // Grab pins from rest api
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            var data = xhr.responseText;
            var jsonResult = JSON.parse(data);
            // Loop through all objects from the JSON results
            for (var i = 0; i < jsonResult.length; i++) {
                var curPin = jsonResult[i];
                // Check if this pin should be expanded
                var expand = getUrlParameter("id") === curPin.id.toString();
                // Determine what the photo source is
                var photoFile;
                if (curPin.anonymous){
                    photoFile = "StreetSmartPhotoStorage/" + "p_" + curPin.id.toString() + ".png";
                }
                else if (curPin.photo){
                    photoFile = "StreetSmartPhotoStorage/" + "p_" + curPin.id.toString() + ".png";
                }
                else if (!curPin.photo){
                    photoFile = "StreetSmartPhotoStorage/" + "u_" + curPin.userId.toString() + ".png";
                }
                // Create the new overlay object
                overlay = new CustomMarker(
                    new google.maps.LatLng(curPin.latitude, curPin.longitude),
                    map,
                    {marker_id: curPin.id},
                    photoFile,
                    curPin.title,
                    expand
                );
            }
        }
    };
    // Change endpoint to venus when venus works.
    xhr.open('GET', 'http://jupiter.cs.vt.edu/StreetSmartREST-1.0/webresources/com.mycompany.streetsmartrest.pin', true);
    xhr.send(null);
}

/*
 * Draws the location marker on the map at the current user location.
 */
function drawUserLocMarker() {
    // Current location marker
    var myLocMarker = "resources/images/currlocmarker.png";
    var myloc = new google.maps.Marker({
        clickable: false,
        icon: myLocMarker,
        shadow: null,
        zIndex: 999,
        map: map
    });
    myloc.setPosition(userLoc);
}

/*
 * Display a message at the top of the map.
 */
function showMapMessage(message, displayTime) {
    // Show the wrapper and hide the message until we fade it in
    $("#map-message-wrapper").show();
    $("#map-message").css("opacity", "0");
    $("#map-message").css("visibility", "visible");
    // Set the text and fade the message in
    $("#map-message").text(message);
    $("#map-message").fadeTo(400, 1, function () {});
    // After [displayTime] fade the message out
    setTimeout(function () {
        $("#map-message").fadeTo(400, 0, function () {});
        setTimeout(function () {
            $("#map-message-wrapper").hide();
            $("#map-message").css("visibility", "hidden");
            $("#map-message-wrapper").hide();
        }, 400);
    }, displayTime);
}

/*
 * Sets the map center.
 * @param latlng google.maps.LatLng object
 * @param offsetx how many pixels to offset the center by in the horizontal direction
 * @param offsety how many pixels to offset the center by in the vertical direction
 * @param pan boolean if the map should pan instead of jumping
 */
function setMapCenter(latlng, offsetx, offsety, pan) {
    var center = latlng;
    
    // Offset the center
    if (offsetx !== 0 || offsety !== 0) {
        // Grab the map scale and bounds
        var scale = Math.pow(2, map.getZoom());
        var nw = new google.maps.LatLng(
            map.getBounds().getNorthEast().lat(),
            map.getBounds().getSouthWest().lng()
        );

        // Convert to pixels
        var worldCoordinateCenter = map.getProjection().fromLatLngToPoint(latlng);
        var pixelOffset = new google.maps.Point((offsetx / scale) || 0, (offsety / scale) || 0);

        // Calculate new center
        var worldCoordinateNewCenter = new google.maps.Point(
            worldCoordinateCenter.x - pixelOffset.x,
            worldCoordinateCenter.y + pixelOffset.y
        );

        // New center
        center = map.getProjection().fromPointToLatLng(worldCoordinateNewCenter);
    }
    
    // Set map to center on the new center location
    if (pan) {
        map.panTo(center);
    } else {
        map.setCenter(center);
    }
}

/*
 * Grabs the value from the specified parameter name in the URL.
 * NOTE: Modified from a stack overflow answer.
 */
function getUrlParameter(parameter) {
    // Grab url and split up parameters
    var pageURL = decodeURIComponent(window.location.search.substring(1)),
        urlVariables = pageURL.split('&'),
        parameterName,
        i;

    // For each param, check if it's the one we're requesting and return the value
    for (i = 0; i < urlVariables.length; i++) {
        parameterName = urlVariables[i].split('=');
        if (parameterName[0] === parameter) {
            return parameterName[1] === undefined ? true : parameterName[1];
        }
    }
};

/*
 * Removes the specified parameter from the URL and pushes the new state to 
 * the location bar.
 * NOTE: Modified from a stack overflow answer.
 */
function removeUrlParameter(parameter) {
    // Grab the url and params
    var url = document.location.href;
    var urlparts= url.split('?');

    if (urlparts.length >= 2)
    {
        var urlBase = urlparts.shift(); 
        var queryString = urlparts.join("?"); 

        // Calculate new URL by removing correct param
        var prefix = encodeURIComponent(parameter) + '=';
        var pars = queryString.split(/[&;]/g);
        for (var i = pars.length; i-- > 0;)               
            if (pars[i].lastIndexOf(prefix, 0) !== -1)   
                pars.splice(i, 1);
        url = urlBase + pars.join('&');
        // Push the modified url to the location bar
        window.history.pushState('', document.title, url);
    }
    return url;
}