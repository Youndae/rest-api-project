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
          "<a href=\"/member/loginout\" class=\"nav-link\">" + "로그아웃" + "</a>"
      )
   }else{
      $(".login ul").append(
          "<a href=\"/member/loginForm\" class=\"nav-link\">" + "로그인" + "</a>"
      )
   }
})