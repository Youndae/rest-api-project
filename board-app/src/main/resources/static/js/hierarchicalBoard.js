$(document).ready(function(){

    $("#insertProc").on('click', function(){
        const form = $("#insertBoardFrm");

        if($("#boardTitle").val() == ""){
            alert("제목을 입력해주세요");
            $("#boardTitle").focus();
        }else{
            form.submit();
        }


    })

    $("#modify").on('click', function(){
        const boardNo = $("#boardNo").val();

        location.href = '/board/boardModify/' + boardNo;
    })

    $("#modifyProc").on('click', function(){
        const form = $("#insertBoardFrm");

        form.submit();
    })

    $("#deleteBoard").on('click', function(){
        const boardNo = $("#boardNo").val();

        $.ajax({
            url: '/board/boardDelete/' + boardNo,
            method: 'delete',
            success: function(data){
                if(data == 1)
                    location.href='/board/boardList';
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
        location.href='/board/boardReply/' + boardNo;
    })

    $("#replyInsertProc").on('click', function(){
        const form = $("#insertBoardFrm");

        form.submit();
    })
})
