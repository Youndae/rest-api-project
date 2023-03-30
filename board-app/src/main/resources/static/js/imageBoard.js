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

        console.log("files.length : " + Object.keys(files).length);


        /**
         * 이미지 하나 등록시 step == 1 imageNo == 0
         * 이미지 삭제 시 step == 1
         * 새로운 이미지 등록 시 step == 2 imageNo == 1
         * 이대로 등록 버튼 동작 시 files.length == 1
         * files[0] == undefined, files[1] == 두번째 등록한 파일
         * for문에서 length 만큼 따지니까 files[0] 만 append하고 보내니 404 에러가 발생.
         * 즉, 파일이 안담김.
         *
         * 만약 사용자가 장난을 쳐서 이미지를 하나만 계속 등록 삭제 등록 삭제 등록 삭제
         * 이렇게 한다면.
         * 분명 문제가 발생할수밖에 없음.
         *
         * 이거에 대한 방지 코드가 필요함.
         *
         * oldPreview에 대해서는 문제가 없다고 볼 수 있음.
         * 기존 데이터중에서 삭제할것만 추리는것이니 deleteOldPreview를 통해서 동작할것이고
         * 겹치는 경우도 없을것이고.
         *
         * files의 index는 앞에 있던 데이터가 삭제된다고해서 앞으로 당겨지지 않는다.
         * 그럼 제일 끝의 index값은 step값과 동일할것.
         * 그러면 아래 반복문에서 step값만큼 반복을 하는데
         * files[index] != undefined인 경우만 formData에 append 해주고
         * undefined이라면 아무것도 안하고 넘기는 방법.
         */

        if(Object.keys(files).length == 0){
            alert("이미지를 최소 1장은 등록해야 합니다.");
        }else{
            for(var index = 0; index < step; index++){
                if(files[index] != undefined){
                    formData.append('files', files[index]);
                }
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
                error: function(request){
                    if(request.status == 400){
                        alert("파일 사이즈는 10MB를 초과할 수 없습니다.");
                    }
                }
            })
        }
    })

    var modifyImageNo = $("#modifyImageNo").val();

    if(modifyImageNo != undefined){
        $.getJSON("/imageBoard/modifyImageAttach", {imageNo: modifyImageNo}, function(arr){
            $(arr).each(function(i, attach){
                $("#preview").append(
                    "<div class=\"preview-box\" value=\"old" + attach.imageStep + "\">" +
                    "<img class=\"thumbnail\" id=\"imgName\" src=\"/imageBoard/display/" + attach.imageName + "\"\/>" +
                    "<p>" + attach.oldName + "</p>" +
                    "<a href=\"#\" value=\"" + attach.imageStep + "\" onclick=\"deleteOldPreview(this)\">" +
                    "삭제" + "</a>" +
                    "</div>"
                );
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

        console.log("formdata files : " + formData.get('files'));
        console.log("formData deleteFiles: " + formData.get('deleteFiles'));

        $.ajax({
            url: '/imageBoard/imageBoardModify',
            method: 'patch',
            enctype: 'multipart/form-data',
            processData: false,
            contentType: false,
            cache: false,
            data: formData,
            success: function(result){
                console.log("imageModify result : " + result);
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
                setPreviewForm(file);
            else
                break;
        }
    }else{
        alert("사진은 5장만 업로드가 가능합니다.");
    }
}

function setPreviewForm(file){
    var reader = new FileReader();
    reader.onload = function(img){
        var imgNum = step;

        $("#preview").append(
            "<div class=\"preview-box\" id=\"newImg\" value=\"" + imgNum +"\">" +
            "<img class=\"thumbnail\" id=\"imgName\" src=\"" + img.target.result + "\"\/>" +
            "<p>" + file.name + "</p>" +
            "<a href=\"#\" value=\"" + imgNum + "\" onclick=\"deletePreview(this)\">" +
            "삭제" + "</a>"
            + "</div>"
        );

        files[imgNum] = file;
        step++;
    };

    reader.readAsDataURL(file);
}

function validation(fileName){
    fileName = fileName + "";
    var fileNameExtensionIndex = fileName.lastIndexOf('.') + 1;
    var fileNameExtension = fileName.toLowerCase().substring(
        fileNameExtensionIndex, fileName.length);

    if((fileNameExtension === 'jpg') || (fileNameExtension === 'gif') || (fileNameExtension === 'png') || (fileNameExtension === 'jpeg')){
        return true;
    }else {
        alert('jpg, gif, png 확장자만 업로드가 가능합니다.');
        return false;
    }
}

function deletePreview(obj){
    console.log("deletePreview");
    var imgNum = obj.attributes['value'].value;
    delete files[imgNum];

    $("#preview .preview-box[value=" + imgNum + "]").remove();
}

function deleteOldPreview(obj){
    console.log("deleteOldPreview");
    var imgNum = obj.attributes['value'].value;
    var imgName = $("#preview .preview-box[value=old" + imgNum +"] .thumbnail").attr('src');
    var idx = imgName.lastIndexOf('/');
    var deleteImg = imgName.substring(idx + 1);

    deleteFiles[deleteNo] = deleteImg;
    deleteNo++;

    $("#preview .preview-box[value=old" + imgNum + "]").remove();
}