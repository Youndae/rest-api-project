const idPattern = /^[A-Za-z0-9]{5,15}$/;
const pwPattern = /^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,16}$/;
let checkId = "";

$(function(){
    $('#userLogin').click(function(){
        const id = $('#userId').val();
        const pw = $('#userPw').val();
        let formData = {
            userId: id,
            userPw: pw,
        }

        if(id == ""){
            $("#nullId").text("아이디를 입력하세요");
            $("#userId").focus();
        }else if(pw == ""){
            $("#nullPw").text("비밀번호를 입력하세요");
            $("#userPw").focus();
        }else{
            formData = JSON.stringify(formData);

            $.ajax({
                url: '/member/login',
                method: 'post',
                data: formData,
                contentType: 'application/json; charset=UTF-8',
                success: function(result){
                    console.log("result : " + result);
                    if(result == 1){
                        location.href='/board/';
                    }
                },
                error: function(request, status, error){
                    console.log("status : "  + request.status);
                    if(request.status == 900){
                        $(".login_form_overlap").text("아이디나 비밀번호가 일치하지 않습니다.");
                    }
                }
            })
        }
    })

    $("#userPw").keydown(function(key){
        if(key.keyCode == 13){
            $("#userLogin").click();
        }
    })

    $('#userId').on("propertychange change keyup paste input", function(){
        const userId = $("#userId").val();
        if(checkId != "" && checkId == userId){
            $("#check").val("check");
        }else{
            $("#check").val("");
        }
    })


    $('#idCheck').on('click', function(){
        const userId = {
            userId : $("#userId").val(),
        };

        if(userId.userId == ""){
            $("#overlap").text("아이디를 입력하세요");
            $("#overlap").css("color", "red");
        }else if(userId.userId != "" && idPattern.test(userId.userId) == false) {
            $("#overlap").text("영문자와 숫자를 사용한 5 ~ 15 자리만 가능합니다.");
            $("#overlap").css("color", "red");
        }else{
            $.ajaxSettings.traditional = true;
            $.ajax({
                url: '/member/checkUserId',
                method: 'get',
                data: userId,
                success: function(data){

                    console.log("return data : " + data);
                    if(data == 1){
                        $("#overlap").text("사용중인 아이디입니다.");
                        $("#check").val("dupl");
                        $("#overlap").css("color", "red");
                    }else if(data == 0){
                        $("#check").val("check");
                        $("#overlap").text("사용 가능한 아이디입니다.");
                        checkId = $("#userId").val();
                        $("#overlap").css("color", "black");
                    }else{
                        $("#overlap").text("오류가 발생했습니다. 문제가 계속되면 관리자에게 문의해주세요.");
                        $("#overlap").css("color", "red");
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
        if($("#userPw").val() != $("#checkUserPw").val()){
            $("#checkPwOverlap").text("비밀번호가 일치하지 않습니다.");
            $("#pwStat").val("");
        }else {
            $("#checkPwOverlap").text("");
            $("#pwStat").val("check");
        }
    })

    $("#join").click(function(){
        const checkVal = $("#check").val();

        if(checkVal == ""){
            $("#overlap").text("아이디 중복체크를 해주세요.");
            $("#userId").focus();
            $("#overlap").css("color", "red");
        }else if(checkVal == "dupl"){
            $("#overlap").text("이미 사용중인 아이디입니다.");
            $("#userId").focus();
            $("#overlap").css("color", "red");
        }else if($("#pwStat").val() == ""){
            $("#checkUserPw").focus();
        }else if($("#userPw").val() == null){
            $("#userPw").focus();
        }else if($("#checkUserPw").val() == null){
            $("#checkUserPw").focus();
        }else if(checkVal == "check" && $("#pwStat").val() == "check"){

            const form = $('#joinForm')[0];
            let formData = new FormData(form);

            $.ajaxSettings.traditional = true;
            $.ajax({
                url: '/member/join',
                contentType: false,
                processData: false,
                cache: false,
                method: 'post',
                data: formData,
                success: function(data){
                    if(data == 0){
                        alert("가입 실패\n 다시 시도해주세요");
                    }else if(data == 1){
                        alert("가입 되었습니다.")
                        location.href='/member/login';
                    }else{
                        alert('error');
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