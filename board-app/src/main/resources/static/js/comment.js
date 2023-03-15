$(function(){
    var boardNo = $("#boardNo").val();
    var imageNo = $("#imageNo").val();
    var pageNum;

    if($("#pageNum").val() == null || $("#pageNum").val() == undefined)
        pageNum = 1;
    else
        pageNum = $("#pageNum").val();

    var str = "";

    if(boardNo != null){
        console.log("boardNo is not null");
        console.log("boardNo : " + boardNo);

        $.getJSON('/comment/boardComment/' + boardNo + "/" + pageNum, function(arr){
            $(arr).each(function(i, res){
                // str = commentEachParsing(result.content);

                console.log("res.toString : " + res.toString());
                console.log("res : " + res.uid);
                console.log("res.commentNo : " + res.commentNo);
                console.log("res.content.commentNo : " + res.content.commentNo);
                console.log("res.boardContent.commentDTO.commentNo" + res.boardContent.CommentDTO.commentNo);

                str += "<div id=\"comment\">" +
                        "<div class=\"comment-box\" id=\"comment-box\" value=\"" + res.commentNo + "\">" +
                            "<table class=\"table table-hover\">" +
                                "<tr>" +
                                    "<td>" +
                                        "<p text=\"" + res.content.userId + "\">" +
                                        "<p text=\"" + res.content.commentContent + "\"></p>" +
                                        "<button class=\"btn btn-outline-info btn-sm\" id=\"cReply\" type=\"button\" " +
                                            "onclick=\"cReply(this)\" value=\"" + res.content.commentNo + "\">답글</button>";

                if(res.content.userId == res.uid){
                    console.log("uid equals userId");
                    str += "<button class=\"btn btn-outline-info btn-sm\" id=\"cDel\" type=\"button\" " +
                        "onclick=\"cDel(this)\" value=\"" + res.commentNo + "\">삭제</button>";
                }

                str += "</p>" +
                    "</td>" +
                    "<input type=\"hidden\" id=\"commentNo\" value=\"" + res.content.commentNo + "\">" +
                    "<input type=\"hidden\" id=\"commentUpperNo\" value=\"" + res.content.commentUpperNo + "\">" +
                    "<input type=\"hidden\" id=\"commentGroupNo\" value=\"" + res.content.commentGroupNo + "\">" +
                    "<input type=\"hidden\" id=\"commentIndent\" value=\"" + res.content.commentIndent + "\">" +
                    "</tr>" +
                    "</table>" +
                    "</div>" +
                    "</div>";
                console.log("str : " + str);
            })
        })
    }if(imageNo != null){
        console.log("boardNo is null");
        console.log("imageBoard : " + imageNo);

        $.getJSON('/comment/imageComment/{imageNo}', {imageNo: imageNo}, function(arr){
            $(arr).each(function(i, result){
                str = commentEachParsing(result);
            })
        })
    }

    $(".comment-area").append(str);

})

function commentEachParsing(res){


    var resStr = "<div id=\"comment\">" +
        "<div class=\"comment-box\" id=\"comment-box\" value=\"" + res.commentNo + "\">" +
        "<table class=\"table table-hover\">" +
        "<tr>" +
        "<td>" +
        "<p text=\"" + res.userId + "\">" +
        "<p text=\"" + res.commentContent + "\"></p>" +
        "<button class=\"btn btn-outline-info btn-sm\" id=\"cReply\" type=\"button\" " +
        "onclick=\"cReply(this)\" value=\"" + res.commentNo + "\">답글</button>";

    if(res.userId == res.uid){
        console.log("uid equals userId");
        resStr += "<button class=\"btn btn-outline-info btn-sm\" id=\"cDel\" type=\"button\" " +
            "onclick=\"cDel(this)\" value=\"" + res.commentNo + "\">삭제</button>";
    }

    resStr += "</p>" +
        "</td>" +
        "<input type=\"hidden\" id=\"commentNo\" value=\"" + res.commentNo + "\">" +
        "<input type=\"hidden\" id=\"commentUpperNo\" value=\"" + res.commentUpperNo + "\">" +
        "<input type=\"hidden\" id=\"commentGroupNo\" value=\"" + res.commentGroupNo + "\">" +
        "<input type=\"hidden\" id=\"commentIndent\" value=\"" + res.commentIndent + "\">" +
        "</tr>" +
        "</table>" +
        "</div>" +
        "</div>";

    return resStr;
}