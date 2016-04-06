// Fired when DOM is finished loading
$(document).ready(function() {
    // Header button hover fix for mobile
    $(".header-btn").bind("touchstart touchend mouseover mouseout", function(e) {
        e.preventDefault();
        $(this).toggleClass("header-btn-hover");
    });
});