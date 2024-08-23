package com.example.boardrest.domain.dto.responseDTO;

import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 *
 * @param <T>
 *     HierarchicalBoardListDTO
 *     ImageBoardDTO
 *     BoardCommentDTO
 *
 * 각 게시판 및 댓글 리스트 조회 결과 반환 DTO
 *
 * 클라이언트에서 필요할 수 있는 pageable 필드를 추려내서 응답
 *
 * 클라이언트의 로그인 상태 확인과 현재 로그인한 사용자의 아이디를 UserStatusDTO에 담아 전달.
 *
 * 목적
 * 매 요청마다 사용자의 로그인 여부와 아이디값을 전달하도록 해야 하는데
 * 반환되는 모든 DTO마다 UserStatusDTO 혹은 그 필드를 추가해주는 것 보다 하나의 틀을 만드는 것이 더 효율적이겠다는 판단으로 생성.
 * 단, BoardProject 처럼 하나의 틀로 여러 응답에 대한 처리를 할 수 있는 경우에만 유효하다고 생각.
 * 기능마다 다른 틀을 제공해야 한다면 이전처럼 기능별 응답 DTO에 직접 추가하는 것이 효율적일 것이라고 생각.
 */

@Getter
@ToString
@NoArgsConstructor
public class ResponsePageableListDTO<T> {

    //list content - board, imageBoard, comment
    private List<T> content;

    //pageable variable
    private boolean empty;

    private boolean first;

    private boolean last;

    private long number;

    private long totalPages;

    //userStatus
    private UserStatusDTO userStatus;

    public ResponsePageableListDTO(Page<T> pageableResponse, String nickname){
        this.content = pageableResponse.getContent();
        this.empty = pageableResponse.isEmpty();
        this.first = pageableResponse.isFirst();
        this.last = pageableResponse.isLast();
        this.number = pageableResponse.getNumber();
        this.totalPages = pageableResponse.getTotalPages();
        this.userStatus = new UserStatusDTO(nickname);
    }
}
