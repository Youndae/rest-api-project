var idPattern = /^[A-Za-z0-9]{5,15}$/;
var pwPattern = /^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,16}$/;
var checkId = "";

$(function(){
    $('#userLogin').click(function(){

        var id = $('#userId').val();
        var pw = $('#userPw').val();

        if(id == ""){
            $("#nullId").text("아이디를 입력하세요");
            $("#userId").focus();
        }else if(pw == ""){
            $("#nullPw").text("비밀번호를 입력하세요");
            $("#userPw").focus();
        }else{
            $('#loginForm').submit();
        }
    })

    $("#userPw").keydown(function(key){
        if(key.keyCode == 13){
            $("#userLogin").click();
        }
    })

    $('#userId').on("propertychange change keyup paste input", function(){
        var userId = $("#userId").val();
        if(checkId != "" && checkId == userId){
            $("#check").val("check");
        }else{
            $("#check").val("");
        }
    })


    $('#idCheck').on('click', function(){
        var userId = {
            userId : $("#userId").val(),
        };

        if(userId.userId == ""){
            $("#overlap").text("아이디를 입력하세요");
        }else if(userId.userId != "" && idPattern.test(userId.userId) == false) {
            $("#idOverlap").text("영문자와 숫자를 사용한 5 ~ 15 자리만 가능합니다.");

        }else{
            $.ajaxSettings.traditional = true;
            $.ajax({
                url: '/member/checkUserId',
                type: 'post',
                data: userId,
                success: function(data){
                    if(data == 1){
                        $("#overlap").text("사용중인 아이디입니다.");
                    }else if(data == 0){
                        $("#check").val("check");
                        $("#overlap").text("사용 가능한 아이디입니다.");
                        checkId = $("#userId").val();
                    }else{
                        $("#overlap").text("오류가 발생했습니다. 문제가 계속되면 관리자에게 문의해주세요.");
                    }
                },
                error: function(request, status, error){
                    alert("code : " + request.status + "\n" +
                    "message : " + request.responseText + "\n" +
                    "error : " + error);
                }
            })
        }
    })

    $("#userPw").on("propertychange change keyup paste input", function(){
        if($("#userPw").val().length < 8){
            $("#pwOverlap").text("비밀번호는 8자리 이상이어야 합니다.");
            $("#pwStat").val("");
        }else if(pwPattern.test($("#userPw").val()) == false){
            $("#pwOverlap").text("비밀번호는 영어, 특수문자, 숫자가 포함되어야 합니다.");
            $("#pwStat").val("");
        }else if($("#userPw").val() == $("#checkUserPw").val()){
            $("#pwOverlap").text("");
            $("#pwStat").val("check");
        }else{
            $("#pwOverlap").text("");
            $("#pwStat").val("");
        }
    })

    $("#checkUserPw").on("propertychange change keyup paste input", function(){
        console.log("checkUserPw change");
        if($("#userPw").val() != $("#checkUserPw").val()){
            console.log("not equals");
            $("#checkPwOverlap").text("비밀번호가 일치하지 않습니다.");
            $("#pwStat").val("");
        }else {
            $("#checkPwOverlap").text("");
            $("#pwStat").val("check");
        }
    })

    $("#join").click(function(){

        var checkVal = $("#check").val();

        if(checkVal == ""){
            $("#overlap").text("아이디 중복체크를 해주세요.");
        }else if($("#pwStat") == ""){
            $("#checkUserPw").focus();
        }else if($("#userPw").val() == null){
            $("#userPw").focus();
        }else if($("#checkUserPw").val() == null){
            $("#checkUserPw").focus();
        }else if(checkVal == "check" && $("#pwStat").val() == "check"){

            var form = $('#joinForm')[0];
            var formData = new FormData(form);

            $.ajaxSettings.traditional = true;
            $.ajax({
                url: '/member/joinProc',
                contentType: false,
                processData: false,
                cache: false,
                type: 'post',
                data: formData,
                success: function(data){
                    if(data == 0){
                        alert("가입 실패\n 다시 시도해주세요");
                    }else{
                        alert("가입 되었습니다.")
                        location.href='/member/loginForm';
                    }
                },
                error: function(request, status, error){
                    alert("code : " + request.status + "\n"
                        + "message : " + request.responseText
                        + "\n" + "error : " + error);
                }
            })
        }else{
            alert("오류가 발생했습니다.\n 문제가 계속되면 관리자에게 문의 해주세요.");
        }
    })


})