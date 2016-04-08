// Fired when DOM is finished loading
$(document).ready(function() {
    // Header button hover fix for mobile
    $(".header-btn").bind("touchstart touchend mouseover mouseout", function(e) {
        e.preventDefault();
        $(this).toggleClass("header-btn-hover");
    });
    
    positionFooter();
});

// Fired when window is resized
$(window).resize(function () {
    positionFooter();
});

function positionFooter() {
    // Footer positioning
    if ($("body").height() < $(window).height()) {
        $("#footer-wrapper").css("position", "absolute");
        $("#footer-wrapper").css("bottom", "0px");
        $("#page-bottom-padding").css("height", "120px");
    } else {
        $("#footer-wrapper").css("position", "relative");
        $("#footer-wrapper").css("bottom", "");
        $("#page-bottom-padding").css("height", "60px");
    }
}