/*
 * Created by Jared Schwalbe on 2016.04.08  * 
 * Copyright © 2016 Jared Schwalbe. All rights reserved. * 
 */

/*
 * Fires when the DOM is finished loading. Check if the user is on mobile
 * and in landscape mode and then display a message warning them that the site
 * functions much better in portrait mode.
 */
$(document).ready(function() {
    if ( /Android|webOS|iPhone|iPad|iPod|BlackBerry/i.test(navigator.userAgent) && window.orientation !== 0) {
        alert("For the best experience, please use our website in portrait mode.")
    }
});

/*
 * Fires when the screen orientation changes. Check if the user is on mobile
 * and in landscape mode and then display a message warning them that the site
 * functions much better in portrait mode.
 */
window.addEventListener("orientationchange", function() {
    if ( /Android|webOS|iPhone|iPad|iPod|BlackBerry/i.test(navigator.userAgent) && window.orientation !== 0) {
        alert("For the best experience, please use our website in portrait mode.")
    }
}, false);