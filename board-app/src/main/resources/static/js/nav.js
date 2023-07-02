/*$(document).ready(function(){
   console.log("nav log");
   var lsc = document.cookie.match("lsc");
   console.log("lsc : " + lsc);

   /!*if(lsc != null){
      $(".login ul").append(
          "<a href=\"/member/loginout\" class=\"nav-link\">" + "로그아웃" + "</a>"
      )
   }else{
      $(".login ul").append(
          "<a href=\"/member/loginForm\" class=\"nav-link\">" + "로그인" + "</a>"
      )
   }*!/
});*/

var closing_window = false;
$(window).on('focus', function () {
   closing_window = false;
   //if the user interacts with the window, then the window is not being
   //closed
});

$(window).on('blur', function () {
   console.log("blur");
   closing_window = true;
   if (!document.hidden) { //when the window is being minimized
      closing_window = false;
   }
   $(window).on('resize', function (e) { //when the window is being maximized
      closing_window = false;
   });
   $(window).off('resize'); //avoid multiple listening
});

$('html').on('mouseleave', function () {
   closing_window = true;
   //if the user is leaving html, we have more reasons to believe that he's
   //leaving or thinking about closing the window
});

$('html').on('mouseenter', function () {
   closing_window = false;
   //if the user's mouse its on the page, it means you don't need to logout
   //them, didn't it?
});

$(document).on('keydown', function (e) {

   if (e.keyCode == 91 || e.keyCode == 18) {
      closing_window = false; //shortcuts for ALT+TAB and Window key
   }

   if (e.keyCode == 116 || (e.ctrlKey && e.keyCode == 82)) {
      closing_window = false; //shortcuts for F5 and CTRL+F5 and CTRL+R
   }
});

// Prevent logout when clicking in a hiperlink
$(document).on("click", "a", function () {
   closing_window = false;
});

// Prevent logout when clicking in a button (if these buttons rediret to some page)
$(document).on("click", "button", function () {
   closing_window = false;

});
// Prevent logout when submiting
$(document).on("submit", "form", function () {
   closing_window = false;
});
// Prevent logout when submiting
$(document).on("click", "input[type=submit]", function () {
   closing_window = false;
});


window.addEventListener("beforeunload", function(e){
   if(closing_window){
      unloadEvent().then(e);
   }
});

async function unloadEvent(){
   await ajaxUnloadEvent();
}

function ajaxUnloadEvent(){
   return new Promise(function(resolve, reject){
      $.ajax({
         url: '/member/logout',
         method: 'post',
         async: true,
         success: function(response) {

         }
      });
   });
}

$(function(){
   console.log("nav log");
   var lsc = document.cookie.match("lsc");
   console.log("lsc : " + lsc);

   if(lsc != null){
      $(".login ul").append(
          "<form action=\"/member/logout\" method=\"post\" class=\"logoutForm\" id=\"logoutBtn\">" +
          "<button class=\"user_status_btn\">" + "로그아웃" + "</button>" +
          "</form>"
      )
   }else{
      $(".login ul").append(
          "<button class=\"user_status_btn\" onclick=\"location.href=\'/member/loginForm\'\">" + "로그인" + "</button>"
      )
   }
})
