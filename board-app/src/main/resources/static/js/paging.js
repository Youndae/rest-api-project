$(document).ready(function(){
    //Paging
    const actionForm = $("#pageActionForm");

    /*$(".paginate_button a").on('click', function(e){
        e.preventDefault();

        actionForm.find("input[name='pageNum']").val($(this).attr("href"));
        actionForm.submit();
    });*/

    $(".board-list-paging a").on('click', function(e){
        e.preventDefault();

        actionForm.find("input[name='pageNum']").val($(this).attr("href"));
        actionForm.submit();
    });

});