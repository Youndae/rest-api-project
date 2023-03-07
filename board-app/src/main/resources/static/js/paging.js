$(document).ready(function(){
    //Paging
    var actionForm = $("#pageActionForm");

    $(".paginate_button a").on('click', function(e){
        e.preventDefault();

        actionForm.find("input[name='pageNum']").val($(this).attr("href"));
        actionForm.submit();
    });

});