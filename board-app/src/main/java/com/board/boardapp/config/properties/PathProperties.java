package com.board.boardapp.config.properties;

public interface PathProperties {

    String BOARD_PATH = "/board/";

    String BOARD_PATH_VARIABLE = BOARD_PATH + "{boardNo}";

    String IMAGE_BOARD_PATH = "/image-board/";

    String IMAGE_BOARD_PATH_VARIABLE = IMAGE_BOARD_PATH + "{imageNo}";

    String COMMENT_PATH = "/comment/";

    String MEMBER_PATH = "/member/";
}
