package com.example.boardrest.repository;

import com.example.boardrest.domain.dto.BoardCommentDTO;
import com.example.boardrest.domain.dto.CommentInsertDTO;
import com.example.boardrest.domain.dto.Criteria;
import com.example.boardrest.domain.entity.Comment;
import com.example.boardrest.domain.entity.ImageBoard;
import com.example.boardrest.domain.entity.Member;
import com.example.boardrest.service.PrincipalService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.security.Principal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;

@SpringBootTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PrincipalService principalService;

    @Test
    @DisplayName("commentList test. boardNo is 99988")
    void listTest(){

        Criteria cri = new Criteria();

        Pageable pageable = PageRequest.of(cri.getPageNum() - 1
                                            , cri.getBoardAmount()
                                            , Sort.by("commentGroupNo").descending()
                                                    .and(Sort.by("commentUpperNo").ascending()));

        Page<BoardCommentDTO> dto = commentRepository.findAll(cri, pageable, null, "277");


    }

    @Test
    @DisplayName("save comment. boardNo is 99988.")
    @Transactional
    void saveTest() {
        CommentInsertDTO insertDTO = CommentInsertDTO.builder()
                                        .commentContent("testMethod comment")
                                        .commentGroupNo(0)
                                        .commentIndent(0)
                                        .commentUpperNo(null)
                                        .boardNo(99988L)
                                        .imageNo(null)
                                        .build();

        Member member = memberRepository.findByUserId("coco");

        Comment comment = Comment.builder()
                .member(member)
                .commentContent(insertDTO.getCommentContent())
                .commentDate(Date.valueOf(LocalDate.now()))
                .build();

        commentRepository.save(comment);
        comment.setCommentPatchData(insertDTO);
        long commentNo = commentRepository.save(comment).getCommentNo();

        Optional<Comment> commentOptional = commentRepository.findById(commentNo);

        Assertions.assertEquals(commentOptional.get().getCommentContent(), insertDTO.getCommentContent());
    }

    @Test
    @DisplayName("comment save and delete Test")
    @Transactional
    void saveAndDeleteTest() {
        CommentInsertDTO insertDTO = CommentInsertDTO.builder()
                                        .commentContent("testMethod comment")
                                        .commentGroupNo(0)
                                        .commentIndent(0)
                                        .commentUpperNo(null)
                                        .boardNo(99988L)
                                        .imageNo(null)
                                        .build();

        Principal principal = new Principal() {
            @Override
            public String getName() {
                return "coco";
            }
        };

        Member member = principalService.checkPrincipal(principal).toMemberEntity();

        Comment comment = Comment.builder()
                                .member(member)
                                .commentContent(insertDTO.getCommentContent())
                                .commentDate(Date.valueOf(LocalDate.now()))
                                .build();

        commentRepository.save(comment);
        comment.setCommentPatchData(insertDTO);
        long commentNo = commentRepository.save(comment).getCommentNo();

        Optional<Comment> commentOptional = commentRepository.findById(commentNo);

        Assertions.assertEquals(commentOptional.get().getCommentContent(), insertDTO.getCommentContent());

        comment.setCommentStatus(1);
        commentRepository.save(comment);

        commentOptional = commentRepository.findById(commentNo);

        Assertions.assertEquals(commentOptional.get().getCommentStatus(), 1);
    }

    @Test
    void saveImageComment(){
        long imageNo = 273;

        for(int i = 518; i < 761; i++){
            commentRepository.save(Comment.builder()
                    .member(Member.builder().userId("mozzi").build())
                    .commentDate(Date.valueOf(LocalDate.now()))
                    .commentContent("imageCommentContent" + i)
                    .commentGroupNo(i)
                    .commentIndent(1)
                    .commentUpperNo(String.valueOf(i))
                    .imageBoard(ImageBoard.builder().imageNo(imageNo).imageTitle("testInsert273").build())
                    .build()
            );
        }

    }



}