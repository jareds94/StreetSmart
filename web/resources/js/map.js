// Global variables
var map;
var userLoc;
var selectedPin;
var parent;

// Called when map loads
function initialize() {
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
        
        if (locationError && !locationDialogOpen) {
            $("#enter-loc-dialog").dialog("open");
        }
    });
    
    // Initialize map by centering on current location and showing location dot
    if ($("#hidden\\:userLocationHidden").val() !== "") {
        var value = $("#hidden\\:userLocationHidden").val();
        value = value.replace("(", "");
        value = value.replace(")", "");
        var split = value.split(", ");
        userLoc = new google.maps.LatLng(split[0], split[1]);
        map.setCenter(userLoc);
        drawUserLocMarker();
    } else {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(function (position) {
                userLoc = {
                    lat: position.coords.latitude,
                    lng: position.coords.longitude
                };
                $("#hidden\\:userLocationHidden").val("(" + userLoc.lat + ", " + userLoc.lng + ")");
                $("#hidden\\:hiddenSubmit").click();
                map.setCenter(userLoc);
                drawUserLocMarker();
            }, function() {
                locationError = true;
                if ($("#map-loading").css("display") === "none") {
                    $("#enter-loc-dialog").dialog("open");
                    locationDialogOpen = true;
                }
            });
        }
    }
    
    
    //#region CustomMarker

    // TODO: put this in it's own file

    // CustomMarker constructor
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
            
            var panes = this.getPanes();
            panes.overlayImage.appendChild(div);

            google.maps.event.addDomListener(div.firstChild, "click", function (event) {
                if (selectedPin !== null && selectedPin !== undefined) {
                    panes.floatPane.removeChild(parent);
                    panes.overlayImage.appendChild(parent);
                    selectedPin.siblings().hide();
                }
                parent = div;
                selectedPin = $(this);
                selectedPin.siblings().show();
                panes.overlayImage.removeChild(div);
                panes.floatPane.appendChild(div);
                
                
                /* TODO: Inject pin details into the side menu */
                

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
        
        if (this.expand) {
            this.div.firstChild.click();
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
    
    
    // Grab pins from rest api
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            var data = xhr.responseText;
            var jsonResult = JSON.parse(data);
            for (var i = 0; i < jsonResult.length; i++){
                var curPin = jsonResult[i];
                var expand = getUrlParameter("id") === curPin.id.toString();
                var photoFile;
                if (curPin.anonymous){
                    photoFile = "resources/images/profile-picture-" + (curPin.id % 3) + ".png";
                }
                else if (curPin.photo){
                    photoFile = "StreetSmartPhotoStorage/" + curPin.id.toString() + ".png";
                }
                else if (!curPin.photo){
                    photoFile = "resources/images/profile-picture-" + (curPin.id % 3) + ".png";
                }
                overlay = new CustomMarker(
                    new google.maps.LatLng(curPin.latitude, curPin.longitude),
                    map,
                    {marker_id: curPin.id},
                    photoFile,
                    curPin.description,
                    expand
                );
            }
        }
    };
    // Change endpoint to venus when venus works.
    xhr.open('GET', 'http://jupiter.cs.vt.edu/StreetSmartREST-1.0/webresources/com.mycompany.streetsmartrest.pin', true);
    xhr.send(null);
   
   // Pins for testing
   /*
   overlay = new CustomMarker(
        new google.maps.LatLng(37.2277411, -80.422268),
        map,
        {marker_id: 1},
        "resources/images/default-1.png",
        "Ultimate frisbee on the drillfield"
    );
        
    overlay = new CustomMarker(
        new google.maps.LatLng(37.2327411, -80.420268),
        map,
        {marker_id: 2},
        "resources/images/default-2.png",
        "Studying all night"
    );
    
    overlay = new CustomMarker(
        new google.maps.LatLng(37.2157411, -80.421268),
        map,
        {marker_id: 3},
        "resources/images/default-3.png",
        "Free ice cream"
    );
    */
}

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

// Sets the map center
function setMapCenter(latlng, offsetx, offsety, pan) {
    var center = latlng;
    
    if (offsetx !== 0 || offsety !== 0) {
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

        center = map.getProjection().fromPointToLatLng(worldCoordinateNewCenter);
    }
    
    if (pan) {
        map.panTo(center);
    } else {
        map.setCenter(center);
    }
}

var getUrlParameter = function getUrlParameter(sParam) {
    var sPageURL = decodeURIComponent(window.location.search.substring(1)),
        sURLVariables = sPageURL.split('&'),
        sParameterName,
        i;

    for (i = 0; i < sURLVariables.length; i++) {
        sParameterName = sURLVariables[i].split('=');

        if (sParameterName[0] === sParam) {
            return sParameterName[1] === undefined ? true : sParameterName[1];
        }
    }
};