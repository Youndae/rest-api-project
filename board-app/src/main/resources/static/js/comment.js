$(function(){
    var boardNo = $("#boardNo").val();
    var imageNo = $("#imageNo").val();
    var pageNum;

    if($("#pageNum").val() == null || $("#pageNum").val() == undefined)
        pageNum = 1;
    else
        pageNum = $("#pageNum").val();

    if(boardNo != null){
        console.log("hierarchicalBoardComment");
        console.log("boardNo : " + boardNo);

        hierarchicalBoardComment(boardNo, pageNum);

    }

    if(imageNo != null){
        console.log("imageBoardComment");
        console.log("imageBoard : " + imageNo);

        imageBoardComment(imageNo, pageNum);
    }

})

function hierarchicalBoardComment(boardNo, pageNum){
    $.getJSON('/comment/boardComment/' + boardNo + "/" + pageNum, function(arr){

        commentEachParsing(arr.content, arr.uid);

        commentPagingParsing(arr);

    })
}

function imageBoardComment(imageNo, pageNum){
    $.getJSON('/comment/imageComment/' + imageNo + "/" + pageNum, function(arr){

        commentEachParsing(arr.content, arr.uid);

        commentPagingParsing(arr);
    })
}

function commentPaging(obj){
    var boardNo = $("#boardNo").val();
    var imageNo = $("#imageNo").val();
    var pageNum = obj;

    if(boardNo != null){
        hierarchicalBoardComment(boardNo, pageNum);
    }

    if(imageNo != null){
        imageBoardComment(imageNo, pageNum);
    }
}

function commentPagingParsing(res){

    var comment_paging = $(".comment-paging");

    comment_paging.empty();

    var cpStr = "";
    var startPage = res.pageDTO.startPage;
    var endPage = res.pageDTO.endPage;
    var pageNum = res.pageDTO.cri.pageNum;
    var prev = res.pageDTO.prev;
    var next = res.pageDTO.next;

    if(prev){
        cpStr += "<ul>" +
            "<li>" +
            "<a onclick=\"commentPrev('prev')\">이전</a>" +
            "</li>";
    }else {
        cpStr += "<ul>";
    }

    for(var i = startPage; i <= endPage; i++){
        if(i == pageNum){
            cpStr += "<li>" +
                        "<a href=\"#\" style=\"font-weight: bold; color: black;\" onclick=\"commentPaging(" + i + ")\">" + i + "</a>" +
                    "</li>"
        }else {
            cpStr += "<li>" +
                        "<a href=\"#\" onclick=\"commentPaging(" + i + ")\">" + i + "</a>" +
                    "</li>"
        }
    }

    if(next){
        cpStr += "<li>" +
            "<a onclick=\"commentNext('next')\">다음</a>" +
            "</li>" +
            "</ul>";
    }else{
        cpStr += "</ul>";
    }

    comment_paging.append(cpStr);
}

function commentEachParsing(arr, uid){

    var comment_area = $(".comment-area");

    comment_area.empty();

    var commentStr = "";

    $(arr).each(function(i, res){
        commentStr += "<div class=\"comment-box\" value=\"" + res.commentNo + "\">" +
                    "<table class=\"table table-hover\">" +
                        "<tr>" +
                            "<td>" +
                                "<p>" + res.userId +
                                    "<p>" + res.commentContent + "</p>" +
                                    "<button class=\"btn btn-outline-info btn-sm\" type=\"button\" " +
                                        "onclick=\"cReply(this)\" value=\"" + res.commentNo + "\">답글</button>";

        if(res.userId == uid){
            console.log("uid equals userId");
            commentStr += "<button class=\"btn btn-outline-info btn-sm\" type=\"button\" " +
                "onclick=\"cDel(this)\" value=\"" + res.commentNo + "\">삭제</button>";
        }

        commentStr += "</p>" +
            "</td>" +
            "<input type=\"hidden\" class=\"commentNo\" value=\"" + res.commentNo + "\">" +
            "<input type=\"hidden\" class=\"commentUpperNo\" value=\"" + res.commentUpperNo + "\">" +
            "<input type=\"hidden\" class=\"commentGroupNo\" value=\"" + res.commentGroupNo + "\">" +
            "<input type=\"hidden\" class=\"commentIndent\" value=\"" + res.commentIndent + "\">" +
            "</tr>" +
            "</table>" +
            "</div>";
    })

    console.log("str : " + commentStr);

    comment_area.append(commentStr);
}