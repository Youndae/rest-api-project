$(function(){
    const boardNo = $("#boardNo").val();
    const imageNo = $("#imageNo").val();
    let pageNum;

    if($("#pageNum").val() == null || $("#pageNum").val() == undefined)
        pageNum = 1;
    else
        pageNum = $("#pageNum").val();

    if(boardNo != null)
        hierarchicalBoardComment(boardNo, pageNum);

    if(imageNo != null)
        imageBoardComment(imageNo, pageNum);

    $("#commentContent").keydown(function(key){
        if(key.keyCode == 13){
            $("#commentInsert").click();
        }
    })

})

$(document).on('click', '#commentInsert', function(){
    const boardNo = $("#boardNo").val();
    const imageNo = $("#imageNo").val();
    const content = $("#commentContent").val();

    let commentData = {
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
    const groupNo = $("#commentGroupNo").val();
    const indent = $("#commentIndent").val();
    const upperNo = $("#commentUpperNo").val();
    const content = $("#commentReplyContent").val();
    const boardNo = $("#boardNo").val();
    const imageNo = $("#imageNo").val();

    let commentData = {
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
        commentEachParsing(arr.content, arr.userStatus);
        commentPagingParsing(arr);
    })
}

function imageBoardComment(imageNo, pageNum){
    $.getJSON('/comment/imageComment/' + imageNo + "/" + pageNum, function(arr){
        commentEachParsing(arr.content, arr.userStatus);
        commentPagingParsing(arr);
    })
}

function commentPaging(obj){
    const boardNo = $("#boardNo").val();
    const imageNo = $("#imageNo").val();
    const pageNum = obj;

    if(boardNo != null)
        hierarchicalBoardComment(boardNo, pageNum);


    if(imageNo != null)
        imageBoardComment(imageNo, pageNum);

}

function commentPagingParsing(res){

    const comment_paging = $(".comment-paging");

    comment_paging.empty();

    let cpStr = "";
    const startPage = res.pageDTO.startPage;
    const endPage = res.pageDTO.endPage;
    const pageNum = res.pageDTO.cri.pageNum;
    const prev = res.pageDTO.prev;
    const next = res.pageDTO.next;

    if(prev){
        cpStr += "<ul>" +
            "<li>" +
            "<a href=\"#\" onclick=\"commentPaging(" + (startPage - 1) + ")\">이전</a>" +
            "</li>";
    }else {
        cpStr += "<ul>";
    }

    for(let i = startPage; i <= endPage; i++){
        if(startPage != endPage){
            if(i == pageNum){
                cpStr += "<li class=\"paginate_button\">" +
                    "<a href=\"#\" style=\"font-weight: bold; color: black;\" onclick=\"commentPaging(" + i + ")\">" + i + "</a>" +
                    "</li>"
            }else {
                cpStr += "<li class=\"paginate_button\">" +
                    "<a href=\"#\" onclick=\"commentPaging(" + i + ")\">" + i + "</a>" +
                    "</li>"
            }
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

function commentEachParsing(arr, userStatus){
    const comment_area = $(".comment-area");

    comment_area.empty();

    let commentStr = "";

    // const uid = document.getElementById('uid').getAttribute('value');

    const uid = userStatus.uid;

    $(arr).each(function(i, res){
        commentStr += "<div class=\"comment-box\" value=\"" + res.commentNo + "\">" +
            "<table class=\"table table-hover\">" +
            "<tr>" +
            "<td>";


        const commentContent = "삭제된 댓글입니다.";
        let writer = '';

        if(res.commentContent !== commentContent)
            writer = res.userId;



        let commentIndentClassName = '';

        if(res.commentIndent === 1)
            commentIndentClassName = ' indent_size_1';
        else if(res.commentIndent === 2)
            commentIndentClassName = ' indent_size_2';
        else if(res.commentIndent === 3)
            commentIndentClassName = ' indent_size_3';
        else if(res.commentIndent === 4)
            commentIndentClassName = ' indent_size_4';

        commentStr += "<span class=\"comment_userId" + commentIndentClassName + "\">" + writer + "</span>" +
                        "<span class=\"comment_date" + commentIndentClassName + "\">" + formatDate(res.commentDate) + "</span>" +
                        "<p class=\"comment_content" + commentIndentClassName + "\">" + res.commentContent + "</p>";

        if(uid != null && res.commentIndent !== 4 && res.commentContent !== commentContent)
            commentStr += "<button class=\"btn btn-outline-info btn-sm\" type=\"button\" " +
                            "onclick=\"cReply(this)\" value=\"" + res.commentNo + "\">답글</button>";

        if(res.userId === uid && res.commentContent !== commentContent)
            commentStr += "<button class=\"btn btn-outline-info btn-sm\" type=\"button\" " +
                "onclick=\"cDel(this)\" value=\"" + res.commentNo + "\">삭제</button>";

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

    comment_area.append(commentStr);
}

function formatDate(date){
    const d = new Date(date);
    let month = '' + (d.getMonth() + 1);
    let day = '' + d.getDate();
    const year = d.getFullYear();

    if(month.length < 2)
        month = '0' + month;
    if(day.length < 2)
        day = '0' + day;

    return [year, month, day].join('-');
}

function cReply(obj){
    const replyInput_div_val = $(".commentReplyContent").val();
    const replyInput_div = $(".commentReplyContent");

    if(replyInput_div_val != null){
        replyInput_div.remove();
    }

    const commentNo = obj.attributes['value'].value;
    const comment_box = $(".comment-box[value=" + commentNo + "]");
    const comment_groupNo = $(".comment-box[value=" + commentNo + "] .commentGroupNo").val();
    const comment_indent = $(".comment-box[value=" + commentNo + "] .commentIndent").val();
    const comment_upperNo = $(".comment-box[value=" + commentNo + "] .commentUpperNo").val();
    let upper_str = "";


    if(comment_upperNo == commentNo){ // upperNo와 commentNo가 같은 경우는 답글이 아니라는 의미. 그래서 upperNo의 value로 원글의 commentNo를 갖도록 한다.
        upper_str = "<input type=\"hidden\" id=\"commentUpperNo\" value=\"" + commentNo + "\">";
    }else{
        upper_str = "<input type=\"hidden\" id=\"commentUpperNo\" value=\"" + comment_upperNo + "\">";
    }

    const reply_str = "<div class=\"commentReplyContent\" value=\"replyContent\">" +
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
    const commentNo = obj.attributes['value'].value;

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