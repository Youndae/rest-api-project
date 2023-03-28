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

$(function(){
   console.log("nav log");
   var lsc = document.cookie.match("lsc");
   console.log("lsc : " + lsc);

   if(lsc != null){
      $(".login ul").append(
          "<form action=\"/member/logout\" method=\"post\">" +
          "<button class=\"user_status_btn\">" + "로그아웃" + "</button>" +
          "</form>"
      )
   }else{
      $(".login ul").append(
          "<button class=\"user_status_btn\" onclick=\"location.href=\'/member/loginForm\'\">" + "로그인" + "</button>"
      )
   }
})
