$(function () {
    "use strict";

    $('.main-nav').find('a').click(function (e) {
        e.preventDefault();
        var thisTarget = $(this).attr('href'),
            $thisTarget = $(thisTarget);

        // Activate active tab
        $(this).closest('li').addClass('active').siblings().removeClass('active');

        // Scroll to section
        $('body,html').animate({
            scrollTop: $(thisTarget).offset().top
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
    })
});