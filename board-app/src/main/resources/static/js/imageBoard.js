var files = {};
var previewIndex = 0;
var deleteFiles = {};
var step = 0;
var deleteNo = 0;

$(function(){

    var imageNo = $("#imageNo").val();

    $("#modify").on('click', function(){
        location.href='/imageBoard/imageBoardModify/' + imageNo;
    })

    $("#deleteBoard").on('click', function(){

        $.ajax({
            url: '/imageBoard/imageBoardDelete/' + imageNo,
            method: 'delete',
            success: function(result){
                if(result == 1){
                    location.href='/imageBoard/imageBoardList';
                }else{
                    alert("삭제 실패");
                }
            }
        })
    })

    $(".attach input[type=file]").change(function(){
        addPreview($(this));
    });

    $("#imageInsert").on('click', function(){
        var form = $("#uploadForm")[0];
        var formData = new FormData(form);

        for(var index = 0; index < Object.keys(files).length; index++){
            formData.append('files', files[index]);
        }

        for(var key of formData.keys()){
            console.log("key : " + key + ", value : " + formData.get(key));
        }

        $.ajax({
            url: '/imageBoard/imageBoardInsert',
            type: 'post',
            enctype: 'multipart/form-data',
            processData: false,
            contentType: false,
            cache: false,
            data: formData,
            success: function(data){
                if(data == -1){
                    alert("오류 발생");
                }else if(data == 2){
                    alert("파일 사이즈 초과");
                }else{
                    alert("성공?");
                    location.href="/imageBoard/imageBoardDetail/" + data;
                }
            },
            error: function(request, status, error){
                alert("code : " + request.status + "\n" +
                "message : " + request.responseText + "\n" +
                "error : " + error);
            }
        })
    })

    var modifyImageNo = $("#modifyImageNo").val();

    if(modifyImageNo != undefined){
        $.getJSON("/imageBoard/modifyImageAttach", {imageNo: modifyImageNo}, function(arr){
            $(arr).each(function(i, attach){
                $("#preview").append(
                    "<div class=\"preview-box\" value=\"" + attach.imageStep + "\">" +
                    "<img class=\"thumbnail\" id=\"imgName\" src=\"/imageBoard/display/" + attach.imageName + "\"\/>" +
                    "<p>" + attach.oldName + "</p>" +
                    "<a href=\"#\" value=\"" + attach.imageStep + "\" onclick=\"deleteOldPreview(this)\">" +
                    "삭제" + "</a>" +
                    "</div>"
                );
                step = attach.imageStep;
            });
        })
    }

    $("#imageModify").on('click', function(){
        var form = $("#uploadForm")[0];
        var formData = new FormData(form);

        for(var index = 0; index < Object.keys(files).length; index++){
            formData.append('files', files[index]);
        }

        console.log("deleteFiles : " + deleteFiles[0]);

        for(var index = 0; index < Object.keys(deleteFiles).length; index++){
            formData.append('deleteFiles', deleteFiles[index]);
        }

        console.log("formData : " + formData.get('deleteFiles'));

        $.ajax({
            url: '/imageBoard/imageBoardModify',
            method: 'patch',
            enctype: 'multipart/form-data',
            processData: false,
            contentType: false,
            cache: false,
            data: formData,
            success: function(result){
                if(result == -1){
                    alert("오류가 발생했습니다 다시 시도해주세요.\n 문제가 계속되면 관리자에게 문의해주세요.");
                }else if(result == -2){
                    alert("파일 사이즈를 초과했습니다.");
                }else{
                    location.href='/imageBoard/imageBoardDetail/' + result;
                }
            },
            error: function(request, status, error){
                alert("code : " + request.status + "\n" +
                "message : " + request.responseText + "\n" +
                "error : " + error);
            }
        })
    })
})

function addPreview(input){
    if(input[0].files.length <= (5 - ($('.preview-box').length))){
        for(var fileIndex = 0; fileIndex < input[0].files.length; fileIndex++){
            var file = input[0].files[fileIndex];

            if(validation(file.name))
                continue;

            setPreviewForm(file);
        }
    }else{
        alert("사진은 5장만 업로드가 가능합니다.");
    }
}

function setPreviewForm(file){
    var reader = new FileReader();
    reader.onload = function(img){
        var imgNum = ++step;

        $("#preview").append(
            "<div class=\"preview-box\" id=\"newImg\" value=\"" + imgNum +"\">" +
            "<img class=\"thumbnail\" id=\"imgName\" src=\"" + img.target.result + "\"\/>" +
            "<p>" + file.name + "</p>" +
            "<a href=\"#\" value=\"" + imgNum + "\" onclick=\"deletePreview(this)\">" +
            "삭제" + "</a>"
            + "</div>"
        );

        files[previewIndex] = file;
        previewIndex++;
    };

    reader.readAsDataURL(file);
}

function validation(fileName){
    fileName = fileName + "";
    var fileNameExtensionIndex = fileName.lastIndexOf('.') + 1;
    var fileNameExtension = fileName.toLowerCase().substring(
        fileNameExtensionIndex, fileName.length);

    if(!((fileNameExtension === 'jpg') || (fileNameExtension === 'gif') || (fileNameExtension === 'png') || (fileNameExtension === 'jpeg'))){
        alert('jpg, gif, png 확장자만 업로드가 가능합니다.');
        return true;
    }else {
        return false;
    }
}

function deletePreview(obj){
    console.log("deletePreview");
    var imgNum = obj.attributes['value'].value;
    delete files[imgNum];

    $("#preview .preview-box[value=]" + imgNum + "]").remove();
}

function deleteOldPreview(obj){
    console.log("deleteOldPreview");
    var imgNum = obj.attributes['value'].value;
    var imgName = $("#preview .preview-box[value=" + imgNum +"] .thumbnail").attr('src');
    var idx = imgName.lastIndexOf('/');
    var deleteImg = imgName.substring(idx + 1);

    deleteFiles[deleteNo] = deleteImg;
    deleteNo++;

    $("#preview .preview-box[value=" + imgNum + "]").remove();
}