package com.example.boardrest.domain.entity;

import com.example.boardrest.domain.dto.board.in.BoardRequest;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "board")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Member member;

    @Column(columnDefinition = "TEXT")
    private String content;

    @CreationTimestamp
    @Column(
            name = "created_at",
            nullable = false,
            columnDefinition = "DATETIME(3) DEFAULT CURRENT_TIMESTAMP(3)"
    )
    private LocalDateTime createdAt;

    @Column(name = "group_no")
    private long groupNo;

    private int indent;

    @Column(name = "upper_no")
    private String upperNo;

    public void initializeRootPath() {
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

    public void setPatchData(BoardRequest dto) {
        this.title = dto.getTitle();
        this.content = dto.getContent();
    }
}
