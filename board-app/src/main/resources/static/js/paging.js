$(document).ready(function(){
    //Paging
    const actionForm = $("#pageActionForm");
    const searchActionForm = $("#pageSearchActionForm");

    $(".board-list-paging a").on('click', function(e){
        e.preventDefault();

        const searchType = $("input[name='searchType']").val();

        if(searchType === ''){
            actionForm.find("input[name='pageNum']").val($(this).attr("href"));
            actionForm.submit();
        }else{
            searchActionForm.find("input[name='pageNum']").val($(this).attr("href"));
            searchActionForm.submit();
        }

    });

});