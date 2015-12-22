$(function () {
    "use strict";

    $('.main-nav').find('a').click(function (e) {
        e.preventDefault();
        var thisTarget = $(this).attr('href');

        // Activate active tab
        $(this).closest('li').addClass('active').siblings().removeClass('active');

        // Scroll to section
        $('body,html').animate({
            scrollTop: $(thisTarget).offset().top - 30
        }, 'fast')
    });

    // Scrolling
    $(document).scroll(function () {

        var winTop = $(window).scrollTop(),
            $navBar = $('.navbar');
        if (winTop > 0) {
            $navBar.addClass('navbar-fixed-top navbar-default');
        } else {
            $navBar.removeClass('navbar-fixed-top navbar-default');
        }

    });

    // Go top
    $('.go-top').click(function () {
        $('body,html').animate({
            scrollTop: 0
        }, 'fast')
    });

    var myFirebaseRef = new Firebase("https://pallive.firebaseio.com/config/download_url/android");
    myFirebaseRef.on("value", function(snapshot) {
        var url = snapshot.val();
        $(".google_play_link").each(function( index ) {
            $( this ).attr("href", url);
        });
    });
});