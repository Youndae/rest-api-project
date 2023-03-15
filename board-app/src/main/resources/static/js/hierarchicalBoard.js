$(document).ready(function(){

    $("#insertProc").on('click', function(){
        var form = $("#insertBoardFrm");

        console.log("insert!");

        form.submit();
    })

    $("#modify").on('click', function(){
        var boardNo = $("#boardNo").val();

        console.log("boardNo : " + boardNo);

        location.href = '/board/boardModify/' + boardNo;
    })

    $("#modifyProc").on('click', function(){
        var form = $("#insertBoardFrm");

        console.log("modify btn click!");

        form.submit();
    })

    $("#deleteBoard").on('click', function(){

        var boardNo = $("#boardNo").val();

        console.log("delete boardNo : " + boardNo);

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
        var boardNo = $("#boardNo").val();
        location.href='/board/boardReply/' + boardNo;
    })

    $("#replyInsertProc").on('click', function(){
        var form = $("#insertBoardFrm");

        console.log("reply insert btn");

        form.submit();
    })
})
