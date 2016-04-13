// Global variables
var map;
var selectedPinLoc;
var pos;

// Called when map loads
function initialize() {
    var showLocationError = false;

    // Turn off points of interest
    var myStyles = [
        {
            featureType: "poi",
            elementType: "labels",
            stylers: [{visibility: "off"}]
        }
    ];

    // Create map
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

        // Show location error message
        if (showLocationError) {
            showMapMessage("Couldn't get current location. Try entering an address in the menu.", 5000);
            showLocationError = false;
        }
    });

    // Initially set map center to their current location
    setMapCenterCurrLoc();

    //#region CustomMarker

    // TODO: put this in it's own class (file)

    // CustomMarker constructor
    function CustomMarker(latlng, map, args, imgsrc, text) {
        this.latlng = latlng;
        this.args = args;
        this.setMap(map);
        this.imgsrc = imgsrc;
        this.text = text;
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
            var pic = document.createElement("img");
            pic.className = "pin-pic";
            pic.src = this.imgsrc;
            div.appendChild(pic);

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

            google.maps.event.addDomListener(div, "click", function (event) {
                $("#map-menu-pin-pic").attr("src", self.imgsrc);
                $("#map-menu-pin-message").text(self.text);
                selectedPinLoc = self.latlng;

                // Show pin details
                if (!$("#map-menu-pin").hasClass("open")) {
                    $("#map-menu-pin").addClass("open");
                    $("#map-menu-pin").show();
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

                    offsetCenter(self.latlng, 150, 0);
                } else {
                    offsetCenter(self.latlng, 150, 0);
                }
            });

            var panes = this.getPanes();
            panes.overlayImage.appendChild(div);
        }

        // Position div to have the center of the profile picture
        // be the coordinates for the pin.
        var point = this.getProjection().fromLatLngToDivPixel(this.latlng);
        if (point) {
            div.style.left = (point.x - 22) + 'px';
            div.style.top = (point.y - 22) + 'px';
        }
    };

    // Remove pin from map
    CustomMarker.prototype.remove = function () {
        if (this.div) {
            this.div.parentNode.removeChild(this.div);
            this.div = null;
        }
    };

    //#endregion
    
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            var data = xhr.responseText;
            var jsonResult = JSON.parse(data);
            for (var i = 0; i < jsonResult.length; i++){
                var curPin = jsonResult[i];
                overlay = new CustomMarker(
                    new google.maps.LatLng(curPin.latitude, curPin.longitude),
                    map,
                    {marker_id: curPin.id},
                    "resources/images/profile-picture.png",
                    curPin.description
                    );
            }
        }
    };
    // Change endpoint to venus when venus works.
    xhr.open('GET', 'http://jupiter.cs.vt.edu/StreetSmartREST-1.0/webresources/com.mycompany.streetsmartrest.pin', true);
    xhr.send(null);

    
    
    
    
//    // Add pins
//    overlay = new CustomMarker(
//            new google.maps.LatLng(37.2277411, -80.422268),
//            map,
//            {marker_id: '1'},
//            "resources/images/profile-picture.png",
//            "This is an example pin. Something is going on " +
//            "at this location!"
//            );
//    overlay = new CustomMarker(
//            new google.maps.LatLng(37.2237411, -80.429268),
//            map,
//            {marker_id: '2'},
//            "resources/images/profile-picture-2.png",
//            "This is also an example pin. Something else is going " +
//            "on at this location!"
//            );
//    overlay = new CustomMarker(
//            new google.maps.LatLng(37.2270411, -80.436268),
//            map,
//            {marker_id: '3'},
//            "resources/images/profile-picture-3.png",
//            "This is another example pin. I'm running out of " +
//            "example text to write."
//            );
}

// Display a message at the top of the map
function showMapMessage(message, displayTime) {
    $("#map-message-wrapper").show();
    $("#map-message").css("opacity", "0");
    $("#map-message").css("visibility", "visible");
    $("#map-message").text(message);
    $("#map-message").fadeTo(400, 1, function () {});
    setTimeout(function () {
        $("#map-message").fadeTo(400, 0, function () {});
        setTimeout(function () {
            $("#map-message-wrapper").hide();
            $("#map-message").css("visibility", "hidden");
            $("#map-message-wrapper").hide();
        }, 400);
    }, displayTime);
}

// Pan to a location on map that is offset by offsetx and offsety pixels
function offsetCenter(latlng, offsetx, offsety) {
    var scale = Math.pow(2, map.getZoom());
    var nw = new google.maps.LatLng(
            map.getBounds().getNorthEast().lat(),
            map.getBounds().getSouthWest().lng()
            );

    var worldCoordinateCenter = map.getProjection().fromLatLngToPoint(latlng);
    var pixelOffset = new google.maps.Point((offsetx / scale) || 0, (offsety / scale) || 0);

    var worldCoordinateNewCenter = new google.maps.Point(
            worldCoordinateCenter.x - pixelOffset.x,
            worldCoordinateCenter.y + pixelOffset.y
            );

    var newCenter = map.getProjection().fromPointToLatLng(worldCoordinateNewCenter);
    map.panTo(newCenter);
}

function setMapCenterCurrLoc() {
    // Try HTML5 geolocation
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(function (position) {
            var pos = {
                lat: position.coords.latitude,
                lng: position.coords.longitude,
                coordinates: function () {
                           return this.lat + " " + this.lng;
                       }
                   };
            // Set the hidden element that holds current coordinates if it exists
            // on the current xhtml page
            if(document.getElementById('pinForm:pinDataHiddenContainer') !== null) {
                document.getElementById('pinForm:pinDataHiddenContainer').value = pos.coordinates();
            }
        }, function () {
            // Error getting geolocation
            showLocationError = true;
            if ($("#map-loading").css("display") === "none") {
                showMapMessage("Couldn't get current location. Try entering an address in the menu.", 5000);
            }
        });
    }
}

function setMapCenterFromAddress(address) {
    var geocoder = new google.maps.Geocoder();
    geocoder.geocode({'address': address}, function (results, status) {
        if (status == google.maps.GeocoderStatus.OK) {
            map.setCenter(results[0].geometry.location);
            // Clear style changes made from error
            $("#map-menu-change-loc").css("background-color", "#ffffff");
            $("#map-menu-change-loc").css("border-color", "#dcdcdc");
        } else {
            // Change text box color indicating error
            $("#map-menu-change-loc").css("background-color", "#fad8d8");
            $("#map-menu-change-loc").css("border-color", "#de9191");
        }
    });
}

window.onload = function() {
    // Set center if map is visible
    if(map !== null) {
        setMapCenterCurrLoc();
    }
}
