package com.example.boardrest.domain.entity;

import com.example.boardrest.domain.dto.comment.in.CommentRequest;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE comment SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Member member;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "group_no")
    private long groupNo;

    private int indent;

    @Column(name = "upper_no")
    private String upperNo;

    @ManyToOne
    @JoinColumn(name = "image_board_id")
    private ImageBoard imageBoard;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    @CreationTimestamp
    @Column(
            name = "created_at",
            nullable = false,
            columnDefinition = "DATETIME(3) DEFAULT CURRENT_TIMESTAMP(3)"
    )
    private LocalDateTime createdAt;

    @Column(name = "deleted_at", columnDefinition = "DATETIME(3)")
    private LocalDateTime deletedAt;

    public void initializeRootPath(){
        this.groupNo = this.id;
        updateUpperNo(String.valueOf(this.id));
    }

    public void initializeReplyPath(String targetUpperNo) {
        String saveValue = String.join(",", targetUpperNo, String.valueOf(this.id));
        updateUpperNo(saveValue);
    }

    private void updateUpperNo(String upperNo) {
        this.upperNo = upperNo;
    }
}
