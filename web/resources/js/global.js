$(document).ready(function() {
    if ( /Android|webOS|iPhone|iPad|iPod|BlackBerry/i.test(navigator.userAgent) && window.orientation !== 0) {
        alert("For the best experience, please use our website in landscape mode.")
    }
});

// Listen for orientation changes
window.addEventListener("orientationchange", function() {
    if ( /Android|webOS|iPhone|iPad|iPod|BlackBerry/i.test(navigator.userAgent) && window.orientation !== 0) {
        alert("For the best experience, please use our website in landscape mode.")
    }
}, false);