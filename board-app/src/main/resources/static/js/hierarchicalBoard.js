$(document).ready(function(){

    $("#insertProc").on('click', function(){
        const form = $("#insertBoardFrm");

        if($("#boardTitle").val() == "") {
            alert("제목을 입력해주세요");
            $("#boardTitle").focus();
        }else
            form.submit();
    })

    $("#modify").on('click', function(){
        console.log("modify click");
        const boardNo = $("#boardNo").val();

        location.href = '/board/patch/' + boardNo;
    })

    $("#modifyProc").on('click', function(){
        const form = $("#patchBoardFrm");
        /*const boardNo = $("#boardNo").val();

        form.attr('action', '/board/' + boardNo).submit();*/

        form.submit();
    })

    $("#deleteBoard").on('click', function(){
        const boardNo = $("#boardNo").val();

        $.ajax({
            url: '/board/' + boardNo,
            method: 'delete',
            success: function(data){
                if(data == 1)
                    location.href='/board/';
                else
                    location.href='/error/error';
            },
            error: function(request, status, error){
                alert("code : " + request.status + "\n"
                    + "message : " + request.responseText + "\n"
                    + "error : " + error);
            }
        })
    })

    $("#reply").on('click', function(){
        const boardNo = $("#boardNo").val();
        location.href='/board/reply/' + boardNo;
    })

    $("#replyInsertProc").on('click', function(){
        const form = $("#insertBoardFrm");

        form.submit();
    })
})
