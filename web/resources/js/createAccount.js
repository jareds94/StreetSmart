// All of this fixes the styles for the password field because of the
// feedback on password strength
$(document).ready(function () {
    setTimeout(function () {
        var valFailed = false;
        
        if ($("#form\\:password").hasClass("validation-failed")) {
            valFailed = true;
        }
        
        $("#form\\:password_panel").removeClass();
        $("#form\\:password_panel").addClass("ui-password-panel");
        $("#form\\:password").removeClass();
        $("#form\\:password").addClass("input-control");
        
        if (valFailed) {
            $("#form\\:password").addClass("validation-failed");
        }
        
        $("#form\\:password_panel").detach().appendTo("#password_panel-wrapper");
    }, 200);

    $("#form\\:password").bind("blur", function () {
        $("#password_panel-wrapper").css("display", "none");
    });

    $("#form\\:password").bind("focus", function () {
        $("#password_panel-wrapper").css("display", "inline-block");
    });

    $("#form\\:password").mouseover(function () {
        $("#form\\:password").css("background-color", "#ffffff");
        if ($("#form\\:password").hasClass("validation-failed")) {
            $("#form\\:password").css("background-color", "#f7e2e2");
        }
    });
});