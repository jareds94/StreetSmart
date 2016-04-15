// Fired when DOM is finished loading
$(document).ready(function () {
    // Hide home button
    $("#header-links-form\\:home-btn").hide();
    
    resizeWelcomeMap($(window).width(), $(window).height() - 130, 0);
});

// Fired when window is resized by the user
$(window).resize(function () {
    resizeWelcomeMap($(window).width(), $(window).height() - 130, 100);
});

// Adjust the heights and width to be window dependent
function resizeWelcomeMap(width, height, delay) {
    setTimeout(function () {
        $("#content-container").css("height", height + "px");
        $("#welcome-wrapper").css("height", height + "px");
        $("#welcome-title").css("margin-top", height/4 + "px");
        $("#welcome-map").css("height", height + "px");
    }, delay);
}