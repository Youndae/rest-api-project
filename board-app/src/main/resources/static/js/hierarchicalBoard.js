$(document).ready(function(){
    $("#insertProc").on('click', function(){
        var form = $("#insertBoardFrm");

        console.log("insert!");

        form.submit();
    })
})