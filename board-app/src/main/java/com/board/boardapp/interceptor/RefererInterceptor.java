package com.board.boardapp.interceptor;

import com.board.boardapp.dto.JwtProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Slf4j
public class RefererInterceptor implements HandlerInterceptor {

    /**
     *
     * 처리 과정
     * 1. referer에 상관없이 접근할 수 있는 페이지는 exclude 상태이므로 referer가 null인 경우 바로 차단.
     * 2. requestMethod에 따라 1차적으로 분류.
     *      get을 제외한 모든 메소드는 referer를 도메인명만 체크하는 것이 아닌 정확하게 체크가 필요.
     * 3-1. method가 get이 아닌 경우
     *      정확한 url을 확인.
     *      예를들어 board/boardInsert라는 post 요청이 들어왔을 때, referer는 무조건 8080/board/boardInsert 가 될 수 밖에 없다.
     *      만약 referer가 이 값이 아니라면 false를 리턴.
     * 3-2. method가 get이지만 체크 해야 하는 경우
     *      이 프로젝트에는 해당되지 않지만 사용자 정보 수정 같은 민감한 정보를 받는 요청이나
     *      게시글의 수정 페이지를 위한 게시글 데이터 get 요청 등은 확실한 referer 체크로 처리한다.
     * 3-3. 위 두가지 경우에 모두 해당하지 않는 경우
     *      내 도메인에서 온 요청인지 확인만 한다. 이 경우 대부분은 WebConfig에서 Exclude 되어있기 때문에 크게 신경쓰지 않아도 될 부분.
     *      여기에 해당하는 페이지는 각 게시판 List, 로그인페이지, 회원가입 페이지 정도.
     *
     * 구조
     * preHandle
     *      @Param HttpServletRequest
     *      @Param HttpServletResponse
     *      @Param Object
     *      HandlerInterceptor 구현체.
     *      요청 메소드를 확인하고 처리 결과에 따라 리턴한다.
     *
     * checkReferer
     *      @Param HttpServletRequest
     *      @Return Boolean
     *      요청 메소드가 get이 아닌 경우 호출되는 메소드
     *      requestURL 값에 따라 Referer 값을 검증하는 메소드.
     *
     * checkGetReferer
     *      @Param HttpServletRequest
     *      @Return Boolean
     *      요청메소드가 get이지만 referer 체크를 해야 하는 요청에 대한 검증 처리.
     */

    /**
     * @PostMapping
     * board/boardInsert                            -> board/boardInsert
     * board/boardModify                            -> board/boardModify/{boardNo}
     * board/boardDelete/{imageNo}                  -> board/boardDetail/{boardNo}
     * board/boardReplyInsert                       -> board/ReplyInsert
     *
     * imageBoard/imageBoardInsert                  -> imageBoard/imageBoardInsert
     * imageBoard/imageBoardModify                  -> imageBoard/imageBoardModify/{imageNo}
     * imageBoard/imageBoardDelete/{imageNo}        -> imageBoard/imageBoardDetail
     *
     * comment/commentInsert                        -> board/boardDetail/{boardNo}  /  imageBoard/imageBoardDetail/{imageNo}
     * comment/commentReplyInsert                   -> board/boardDetail/{boardNo}  /  imageBoard/imageBoardDetail/{imageNo}
     * comment/commentDelete/{commentNo}            -> board/boardDetail/{boardNo}  /  imageBoard/imageBoardDetail/{imageNo}
     *
     * member/login                                 -> member/loginForm
     * member/joinProc                              -> member/join
     *
     * @GetMapping
     * board/boardModify                            -> board/boardDetail/{boardNo}
     *
     * imageBoard/imageBoardModify                  -> imageBoard/imageBoardDetail/{imageNo}
     *
     */


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        log.info("TokenInterceptor");

        log.info("TokenInterceptor referer : " + request.getHeader("Referer"));

        log.info("request url : " + request.getRequestURL());

        String referer = request.getHeader("Referer");

        if (referer == null){
            new Exception();
        }

        if(referer == null || !referer.contains(request.getHeader("host")))
            new Exception();


        if(request.getMethod() != "get") {
            if (checkReferer(request)){
                return HandlerInterceptor.super.preHandle(request, response, handler);
            }else{
                new Exception();
            }
        }

        /**
         * referer 체크를 하는데
         * 특정 url(post, patch, delete) 요청이나 사용자 정보 같은 중요 정보를 get 요청 하는 경우에는
         * 이전 url을 localhost:8080/** 형태가 아닌 아예 버튼 누르기 전 이전 url을 Referer로 갖고 있는지 체크.
         * 굳이 이렇게 까지 해야하나 싶었었는데 확실하게 차단하기 위해서는 이렇게 하는게 낫다고 생각.
         * 더 좋은 방법이 있을 수 있겠지만 현재로서는 이게 최선인듯.
         */

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    public boolean checkReferer(HttpServletRequest request){
        /**
         * 1. reqUrl이 어느 분류(board, imageBoard, comment, member)에 해당하는지 체크한다.
         * 2. String path = baseUrl + 분류별 String;
         * 3. reqUrl에 따라 Referer가 갖고 있어야 할 값 검증.(각 엔티티별 메소드 분리?)
         */

        String reqUrl = request.getRequestURL().toString();
        String refer = request.getHeader("Referer");
        String path = null;


        if(reqUrl.substring(InterceptorProperties.BASE_URL.length()).startsWith("board")) {
            path = InterceptorProperties.BASE_URL + InterceptorProperties.BOARD;
            return checkBoardReferer(reqUrl, refer, path);
        }else if(reqUrl.substring(InterceptorProperties.BASE_URL.length()).startsWith("image")) {
            path = InterceptorProperties.BASE_URL + InterceptorProperties.IMAGE_BOARD;
            return checkImageBoardReferer(reqUrl, refer, path);
        }else if(reqUrl.substring(InterceptorProperties.BASE_URL.length()).startsWith("comment")) {
            path = InterceptorProperties.BASE_URL;
            return checkCommentReferer(reqUrl, refer, path);
        }else if(reqUrl.substring(InterceptorProperties.BASE_URL.length()).startsWith("member")) {
            path = InterceptorProperties.BASE_URL + InterceptorProperties.MEMBER;
            return checkMemberReferer(reqUrl, refer, path);
        }else {
            return false;
        }

    }

    public boolean checkBoardReferer(String reqUrl, String refer, String path){
        /**
         * boardInsert              -> boardInsert
         * boardReplyInsert         -> boardReplyInsert
         * boardModify              -> boardModify/{boardNo}
         * boardDelete/{boardNo}    -> boardDetail/{boardNo}
         */

        if(reqUrl.equals(path + InterceptorProperties.BOARD_INSERT)){//boardInsert
            return reqUrl.equals(refer);
        }else if(reqUrl.equals(path + InterceptorProperties.BOARD_REPLY_INSERT)){//boardReplyInsert
            return reqUrl.equals(refer);
        }else if(reqUrl.startsWith(path + InterceptorProperties.BOARD_MODIFY)){//boardModify/
            String boardNo = reqUrl.substring(reqUrl.lastIndexOf("/") + 1);
            return refer.equals(path + InterceptorProperties.BOARD_MODIFY + boardNo);
        }else if(reqUrl.startsWith(path + InterceptorProperties.BOARD_DELETE)){//boardDelete/
            String boardNo = reqUrl.substring(reqUrl.lastIndexOf("/") + 1);
            return refer.equals(path + InterceptorProperties.BOARD_DETAIL + boardNo);
        }

        return false;
    }

    public boolean checkImageBoardReferer(String reqUrl, String refer, String path){
        /**
         * imageBoardInsert             -> imageBoardInsert
         * imageBoardModify/{imageNo}   -> imageBoardModify/{imageNo}
         * imageBoardDelete/{imageNo}   -> imageBoardDetail/{imageNo}
         */


        if(reqUrl.equals(path + InterceptorProperties.IMAGE_BOARD_INSERT)){//imageBoardInsert
            return reqUrl.equals(refer);
        }else if(reqUrl.startsWith(path + InterceptorProperties.IMAGE_BOARD_MODIFY)){//imageBoardModify/
            String imageNo = reqUrl.substring(reqUrl.lastIndexOf("/") + 1);
            return refer.equals(path + InterceptorProperties.IMAGE_BOARD_MODIFY + imageNo);
        }else if(reqUrl.startsWith(path + InterceptorProperties.IMAGE_BOARD_DELETE)){//imageBoardDelete/
            String imageNo = reqUrl.substring(reqUrl.lastIndexOf("/") + 1);
            return refer.equals(path + InterceptorProperties.IMAGE_BOARD_DETAIL + imageNo);
        }

        return false;
    }

    public boolean checkCommentReferer(String reqUrl, String refer, String path){
        /**
         * @Param String path
         *      8080/comment가 아닌 8080/까지만 받아야함.
         *
         * commentInsert                -> board/boardDetail/{boardNo} || imageBoard/imageBoardDetail/{imageNo}
         * commentDelete/{commentNo}    -> board/boardDetail/{boardNo} || imageBoard/imageBoardDetail/{imageNo}
         * commentReplyInsert           -> board/boardDetail/{boardNo} || imageBoard/imageBoardDetail/{imageNo}
         */

        String commentURL = path + InterceptorProperties.COMMENT;

        if(reqUrl.equals(commentURL + InterceptorProperties.COMMENT_INSERT) ||
                reqUrl.equals(commentURL + InterceptorProperties.COMMENT_REPLY_INSERT) ||
                reqUrl.startsWith(commentURL + InterceptorProperties.COMMENT_DELETE)) {

            if(refer.startsWith(path + InterceptorProperties.BOARD + InterceptorProperties.BOARD_DETAIL) ||
                    refer.startsWith(path + InterceptorProperties.IMAGE_BOARD + InterceptorProperties.IMAGE_BOARD_DETAIL)){
                return true;
            }
        }

        return false;
    }

    public boolean checkMemberReferer(String reqUrl, String refer, String path){
        /**
         * member/login     -> member/loginForm
         * member/joinProc  -> member/join
         */
        if(reqUrl.equals(path + InterceptorProperties.MEMBER_LOGIN)){
            return refer.equals(path + InterceptorProperties.MEMBER_LOGIN_FORM);
        }else if(reqUrl.equals(path + InterceptorProperties.MEMBER_JOIN_PROC)){
            return refer.equals(path + InterceptorProperties.MEMBER_JOIN);
        }

        return false;
    }
}
