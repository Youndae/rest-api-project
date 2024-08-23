package com.example.boardrest.domain.entity;

import com.example.boardrest.domain.dto.comment.in.CommentInsertDTO;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicUpdate
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long commentNo;

    @ManyToOne
    @JoinColumn(name = "userId")
    private Member member;

    @CreationTimestamp
    private LocalDate commentDate;

    private String commentContent;

    private long commentGroupNo;

    private int commentIndent;

    private String commentUpperNo;

    @ManyToOne
    @JoinColumn(name = "imageNo")
    private ImageBoard imageBoard;

    @ManyToOne
    @JoinColumn(name = "boardNo")
    private HierarchicalBoard hierarchicalBoard;

    private int commentStatus;

    public void setCommentStatus(int commentStatus) {
        this.commentStatus = commentStatus;
    }

    public void setCommentPatchData(CommentInsertDTO dto) {
        String cno = String.valueOf(this.commentNo);
        HierarchicalBoard hBoard = new HierarchicalBoard();
        ImageBoard iBoard = new ImageBoard();

        if(dto.getBoardNo() != null)
            hBoard.setBoardNo(dto.getBoardNo());
        else
            hBoard = null;

        if(dto.getImageNo() != null)
            iBoard.setImageNo(dto.getImageNo());
        else
            iBoard = null;

        this.commentGroupNo = dto.getCommentGroupNo() == null ? this.commentNo : dto.getCommentGroupNo();
        this.commentIndent = dto.getCommentGroupNo() == null ? 0 : dto.getCommentIndent() + 1;
        this.commentUpperNo = dto.getCommentUpperNo() == null ? cno : dto.getCommentUpperNo() + "," + cno;
        this.hierarchicalBoard = hBoard;
        this.imageBoard = iBoard;
    }
}
