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

$(document).on('click', '#commentInsert', function(){
    var boardNo = $("#boardNo").val();
    var imageNo = $("#imageNo").val();
    var content = $("#commentContent").val();

    console.log("boardNo : " + boardNo);
    console.log("imageNo : " + imageNo);
    console.log("content : " + content);

    var commentData = {
        boardNo : boardNo,
        imageNo : imageNo,
        commentContent : content
    };

    if(content == ""){
        alert("댓글을 입력해주세요");
        $("#commentContent").focus();
    }else{

        commentData = JSON.stringify(commentData);

        $.ajax({
            url: '/comment/commentInsert',
            method: 'post',
            data: commentData,
            contentType: 'application/json; charset=UTF-8',
            success: function(result){
                if(result != -1)
                    location.reload();
            },
            error: function(request, status, error){
                alert("code : " + request.status + "\n" +
                "message : " + request.responseText + "\n" +
                "error : " + error);
            }

        })
    }


})

$(document).on('click', '#commentReplyInsert', function(){
    var groupNo = $("#commentGroupNo").val();
    var indent = $("#commentIndent").val();
    var upperNo = $("#commentUpperNo").val();
    var content = $("#commentReplyContent").val();
    var boardNo = $("#boardNo").val();
    var imageNo = $("#imageNo").val();
    console.log("commentReply Insert!");

    console.log("groupNo : " + groupNo);
    console.log("indent : " + indent);
    console.log("upperNo : " + upperNo);
    console.log("content : " + content);
    console.log("boardNo : " + boardNo);
    console.log("imageNo : " + imageNo);

    var commentData = {
        commentGroupNo : groupNo,
        commentIndent : indent,
        commentUpperNo : upperNo,
        commentContent : content,
        boardNo : boardNo,
        imageNo : imageNo
    };

    if(content == ""){
        alert("댓글을 입력해주세요");
        $("#commentReplyContent").focus();
    }else{
        commentData = JSON.stringify(commentData);

        $.ajax({
            url: '/comment/commentReplyInsert',
            method: 'post',
            data: commentData,
            contentType: 'application/json; charset=UTF-8',
            success: function(result){
                if(result != -1)
                    location.reload();
            },
            error: function(request, status, error){
                alert("code : " + request.status + "\n" +
                "message : " + request.responseText + "\n" +
                "error : " + error);
            }
        })
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
            "<a href=\"#\" onclick=\"commentPaging(" + (startPage - 1) + ")\">이전</a>" +
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
            "<a href=\"#\" onclick=\"commentPaging(" + (endPage + 1) + ")\">다음</a>" +
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

function cReply(obj){
    var replyInput_div_val = $(".commentReplyContent").val();
    var replyInput_div = $(".commentReplyContent");

    if(replyInput_div_val != null){
        replyInput_div.remove();
    }

    var commentNo = obj.attributes['value'].value;
    var comment_box = $(".comment-box[value=" + commentNo + "]");
    var comment_groupNo = $(".comment-box[value=" + commentNo + "] .commentGroupNo").val();
    var comment_indent = $(".comment-box[value=" + commentNo + "] .commentIndent").val();
    var comment_upperNo = $(".comment-box[value=" + commentNo + "] .commentUpperNo").val();
    var upper_str = "";


    if(comment_upperNo == commentNo){ // upperNo와 commentNo가 같은 경우는 답글이 아니라는 의미. 그래서 upperNo의 value로 원글의 commentNo를 갖도록 한다.
        upper_str = "<input type=\"hidden\" id=\"commentUpperNo\" value=\"" + commentNo + "\">";
    }else{
        upper_str = "<input type=\"hidden\" id=\"commentUpperNo\" value=\"" + comment_upperNo + "\">";
    }

    var reply_str = "<div class=\"commentReplyContent\" value=\"replyContent\">" +
                        "<input type=\"text\" id=\"commentReplyContent\">" +
                        "<button type=\"button\" class=\"btn btn-outline-info btn-sm\" id=\"commentReplyInsert\" value=\"" + commentNo + "\">작성</button>" +
                        "<input type=\"hidden\" id=\"commentGroupNo\" value=\"" + comment_groupNo + "\">" +
                        "<input type=\"hidden\" id=\"commentIndent\" value=\"" + comment_indent + "\">" +
                        upper_str +
                    "</div>";

    comment_box.append(reply_str);

    $("#commentReplyContent").focus();

}

function cDel(obj){
    var commentNo = obj.attributes['value'].value;

    console.log("delete commentNo : " + commentNo);

    $.ajax({
        url: '/comment/commentDelete/' + commentNo,
        method: 'delete',
        success: function(result){
            if(result != -1)
                location.reload();
        },
        error: function(request, status, error){
            alert("code : " + request.status + "\n" +
            "message : " + request.responseText + "\n" +
            "error : " + error);
        }
    })
}