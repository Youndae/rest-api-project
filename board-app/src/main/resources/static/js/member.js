const idPattern = /^[A-Za-z0-9]{5,15}$/;
const pwPattern = /^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,16}$/;
const emailPattern = /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*\.[a-zA-Z]{2,3}$/i;
const naverEmail = 'naver.com';
const daumEmail = 'daum.net';
let checkId = "";
let userEmail = '';
let profileFile = null;
let deleteFile = null;

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
                    if(result == "SUCCESS"){
                        location.href='/board/';
                    }
                },
                error: function(request, status, error){
                    console.log("status : "  + request.status);
                    if(request.status == 403){
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

    $('#nicknameCheck').click(function () {
        const nickname = $('#nickname');
        // const nickname = $('#nickname');
        const val = nickname.val();
        const overlap = $('#nicknameOverlap');
        const nicknameStat = $('#nicknameStat');

        console.log('nickname : ', val);

        if(val === ''){
            overlap.text('닉네임을 입력해주세요');
            nickname.focus();
        }else {
            const nickname = {
                nickname : val,
            }
            $.ajax({
                url: '/member/check-nickname',
                method: 'get',
                data: nickname,
                success: function(data) {
                    if(data === "FAIL"){
                        overlap.text('이미 사용중인 닉네임입니다.')
                        nicknameStat.val('dupl');
                        nickname.focus();
                        overlap.css("color", "red");
                    }else if(data === "SUCCESS") {
                        nicknameStat.val('check');
                        overlap.text('사용 가능한 닉네임입니다.');
                    }else {
                        overlap.text('오류가 발생했습니다. 문제가 계속되면 관리자에게 문의해주세요.');
                        overlap.css("color", "red");
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

    $('.email-select').change(function () {
        const optionValue = $('.email-select option:selected').val();
        console.log('select change value : ', optionValue);

        if(optionValue === '')
            $('#emailSuffix').css("display", "inline");
        else
            $('#emailSuffix').css("display", "none");

        checkEmail();
    })

    $('#email').on('propertychange chage keyup paste input', function () {
        checkEmail();
    })

    $('#emailSuffix').on('propertychange chage keyup paste input', function () {
        checkEmail();
    })

    function checkEmail() {
        const emailPrefix = $('#email').val();
        let emailSuffix = '';
        const selectValue = $('#email-suffix-select option:selected').val();
        if(selectValue === 'naver')
            emailSuffix = naverEmail;
        else if(selectValue === 'daum')
            emailSuffix = daumEmail;
        else
            emailSuffix = $('#emailSuffix').val();

        const email = emailPrefix + '@' + emailSuffix;

        if(!emailPattern.test(email)) {
            $('#emailStat').val('');
            $('#emailOverlap').text('유효하지 않은 이메일입니다.');
        }else {
            $('#emailOverlap').text("");
            $('#emailStat').val('check');
            userEmail = email;
        }
    }

    $('#profile-image').change(function () {
        const file = $(this)[0].files[0];

        profileFile = file;

        const url = window.URL.createObjectURL(file);

        $('#profile-image-label').empty();

        $('#profile-image-label').append(
            "<img class=\"profile-image\" src=\"" + url + "\"\/>"
        );
        $('.profile-preview').append(
            "<button class=\"btn btn-outline-info btn-sm profile-button\" onclick=\"deleteProfile()\">삭제</button>"
        );
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
                url: '/member/check-userid',
                method: 'get',
                data: userId,
                success: function(data){
                    if(data === "DUPLICATED"){
                        $("#overlap").text("사용중인 아이디입니다.");
                        $("#check").val("dupl");
                        $("#overlap").css("color", "red");
                    }else if(data === "AVAILABLE"){
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
        }else if($('#nickname').val() == null) {
            $("#nicknameOverlap").text("닉네임을 입력해주세요")
            $('#nickname').focus();
        }else if($("#nicknameStat").val() === '' || $('#nicknameStat').val() === 'dupl'){
            $("#nicknameOverlap").text("닉네임 중복체크를 해주세요");
            $('#nickname').focus();
        }else if($("#email").val() === null) {
            $("#emailOverlap").text('이메일을 입력해주세요');
            $("#email").focus();
        }else if(checkVal === "check" && $("#pwStat").val() === "check" && $("#nicknameStat").val() === "check" && $("#emailStat").val() === "check"){

            const userId = $('#userId').val();
            const userPw = $('#userPw').val();
            const userName = $('#userName').val()
            const nickname = $('#nickname').val()
            const email = userEmail;
            const profile = profileFile;

            let formData = new FormData();
            formData.append('userId', userId);
            formData.append('userPw', userPw);
            formData.append('userName', userName);
            formData.append('nickname', nickname);
            formData.append('email', email);
            if(profileFile !== null)
                formData.append('profileThumbnail', profile);

            $.ajaxSettings.traditional = true;
            $.ajax({
                url: '/member/join',
                contentType: false,
                processData: false,
                cache: false,
                method: 'post',
                data: formData,
                success: function(data){
                    if(data === "FAIL"){
                        alert("가입 실패\n 다시 시도해주세요");
                    }else if(data === "SUCCESS"){
                        alert("가입 되었습니다.")
                        location.href='/member/login';
                    }else if(data === "SIZE") {
                        alert('프로필 이미지 사이즈는 10MB를 넘길 수 없습니다.')
                    }
                    else{
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


    $('#joinOAuth').click(function() {
        const formData = getFormData();
        const btnText = $('#joinOAuth').text();

        $.ajaxSettings.traditional = true;
        $.ajax({
            url: '/member/join-oauth-profile',
            contentType: false,
            processData: false,
            cache: false,
            method: 'patch',
            data: formData,
            success: function(data){
                if(data === 0){
                    alert("요청 실패\n 다시 시도해주세요");
                }else if(data === "SUCCESS"){
                    alert(btnText + " 되었습니다.")
                    if(btnText === '등록'){
                        const prev = window.sessionStorage.getItem('prev');
                        const idx = prev.lastIndexOf('/');
                        const checkPrev = prev.substring(idx);
                        let redirectUrl = '';
                        if(checkPrev === '/login' || checkPrev === '/join')
                            redirectUrl = '/board';
                        else {
                            redirectUrl = prev;
                        }
                        location.href = redirectUrl;
                    }
                }else if(data === "SIZE") {
                    alert('프로필 이미지 사이즈는 10MB를 넘길 수 없습니다.')
                }
                else{
                    alert('error');
                }
            },
            error: function(request, status, error){
                alert("code : " + request.status + "\n"
                    + "message : " + request.responseText
                    + "\n" + "error : " + error);
            }
        })
    })

})

function getFormData() {
    const nicknameElem = $('#nickname');
    const nicknameValue = nicknameElem.val();
    const nicknameOverlap = $('#nicknameOverlap');

    if(nicknameValue === null){
        nicknameOverlap.text("닉네임을 입력해주세요");
        nicknameElem.focus();
    }else if($("#nicknameStat").val() === '' || $('#nicknameStat').val() === 'dupl'){
        nicknameOverlap.text("닉네임 중복체크를 해주세요");
        nicknameElem.focus();
    }else if($("#nicknameStat").val() === "check"){
        const formData = new FormData();

        formData.append('nickname', nicknameValue);
        if(profileFile !== null)
            formData.append('profileThumbnail', profileFile);

        if(deleteFile !== null)
            formData.append('deleteProfileThumbnail', deleteFile);

        return formData;
    }
}

function deleteProfile() {
    $('#profile-image-label').empty();
    profileFile = null;

    $('.profile-button').remove();

    $('#profile-image-label').append(
        "<img class=\"profile-image\" src=\"/image/default-profile.png\"\/>"
    );
}

function oAuthLogin(provider) {
    const referrer = document.referrer;
    window.sessionStorage.setItem('prev', referrer);
    window.location.href = '/member/' + provider;
}

function deleteOldProfile() {
    const imageName = $('.profile-image').attr('src');
    const idx = imageName.lastIndexOf('/');
    deleteFile = imageName.substring(idx + 1);

    deleteProfile();
}