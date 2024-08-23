package com.example.boardrest.domain.entity;

import com.example.boardrest.domain.dto.hBoard.in.HierarchicalBoardModifyDTO;
import com.example.boardrest.domain.dto.hBoard.in.HierarchicalBoardReplyDTO;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HierarchicalBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long boardNo;

    private String boardTitle;

    @ManyToOne
    @JoinColumn(name = "userId")
    private Member member;

    private String boardContent;

    @CreationTimestamp
    private LocalDate boardDate;

    private long boardGroupNo;

    private int boardIndent;

    private String boardUpperNo;

    public void setBoardNo(long boardNo) {
        this.boardNo = boardNo;
    }

    public void setBoardTitle(String boardTitle) {
        this.boardTitle = boardTitle;
    }

    public void setBoardContent(String boardContent) {
        this.boardContent = boardContent;
    }

    public void setPatchDataAfterInsertion(HierarchicalBoardReplyDTO dto) {
        String bno = String.valueOf(this.boardNo);
        this.boardGroupNo = dto.getBoardGroupNo() == 0 ? this.boardNo : dto.getBoardGroupNo();
        this.boardUpperNo = dto.getBoardUpperNo() == null ? bno : dto.getBoardUpperNo() + "," + bno;
        this.boardIndent = dto.getBoardGroupNo() == 0 ? 0 : dto.getBoardIndent() + 1;
    }

    public void setPatchData(HierarchicalBoardModifyDTO dto) {
        this.boardTitle = dto.getBoardTitle();
        this.boardContent = dto.getBoardContent();
    }
}
