package com.example.boardrest.service;

import com.example.boardrest.domain.dto.hBoard.out.HierarchicalBoardDetailDTO;
import com.example.boardrest.domain.dto.hBoard.out.HierarchicalBoardListDTO;
import com.example.boardrest.domain.dto.hBoard.in.HierarchicalBoardModifyDTO;
import com.example.boardrest.domain.dto.hBoard.in.HierarchicalBoardReplyDTO;
import com.example.boardrest.domain.dto.paging.Criteria;
import com.example.boardrest.domain.entity.HierarchicalBoard;
import com.example.boardrest.domain.entity.Member;
import com.example.boardrest.repository.HierarchicalBoardRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.Duration;

@SpringBootTest
class HierarchicalBoardServiceImplTest {

    @Autowired
    private HierarchicalBoardService service;

    @Autowired
    private PrincipalService principalService;

    @Autowired
    private HierarchicalBoardRepository repository;

    @Test
    @DisplayName("boardInsert, modify test")
    @Transactional
    void insertTest() {
        Principal principal = new Principal() {
            @Override
            public String getName() {
                return "coco";
            }
        };
        Member memberEntity = principalService.checkPrincipal(principal).toMemberEntity();

        HierarchicalBoardReplyDTO boardDTO = HierarchicalBoardReplyDTO.builder()
                                            .boardTitle("test Title1")
                                            .boardContent("test Content 1")
                                            .boardGroupNo(0L)
                                            .boardIndent(0)
                                            .boardUpperNo(null)
                                            .build();

        HierarchicalBoard board = boardDTO.toEntity(memberEntity);

        long saveBoardNo = repository.save(board).getBoardNo();
        board.setPatchBoardData(boardDTO);
        repository.save(board);

        HierarchicalBoard saveBoard = repository.findById(saveBoardNo).orElseThrow(() -> new NullPointerException("nullPointerException"));

        Assertions.assertEquals(saveBoard.getBoardContent(), "test Content 1");

        //patchTest
        HierarchicalBoardModifyDTO modifyDTO = HierarchicalBoardModifyDTO.builder()
                .boardNo(saveBoardNo)
                .boardTitle("test title1")
                .boardContent("modifyContent1")
                .build();

        saveBoard.setBoardTitle(modifyDTO.getBoardTitle());
        saveBoard.setBoardContent(modifyDTO.getBoardContent());
        repository.save(saveBoard);

        saveBoard = repository.findById(saveBoardNo).orElseThrow(() -> new NullPointerException("nullPointerException"));

        Assertions.assertEquals(saveBoard.getBoardContent(), "modifyContent1");


    }

    @Test
    void detailTest() {
        HierarchicalBoardDetailDTO dto = repository.findBoardDetailByBoardNo(99988L);

        System.out.println(dto);
    }

    @Test
    void hTest() {
        long boardNo = 100018L;

//        service.deleteBoard(boardNo);
    }

    @Test
    void queryDSLPaginationTest() {
//        Criteria cri = new Criteria(1, 20, 15, "9999", "t");

        Criteria cri = new Criteria();

        Pageable pageable = PageRequest.of(cri.getPageNum()
                , cri.getBoardAmount()
                , Sort.by("boardGroupNo").descending()
                        .and(Sort.by("boardUpperNo").ascending()));

        Page<HierarchicalBoardListDTO> result = repository.findAll(cri, pageable);

        System.out.println("totalPages : " + result.getTotalPages());
        System.out.println("total Elements : " + result.getTotalElements());
    }


    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    void redisTest() throws InterruptedException {
        ValueOperations<String, String> stringStringValueOperations = redisTemplate.opsForValue();
//        stringStringValueOperations.set("testKey", "testValue");

        String uid = "coco";

        /*System.out.println("val : " + redisTemplate.opsForValue().get(uid));


        Set<String> keys = redisTemplate.keys("*" + uid);

        List<String> deleteKeys = new ArrayList<>();
        int prefixLength = 6;

        for(String key : keys){
            if(key.substring(prefixLength).equals(uid))
                deleteKeys.add(key);
        }

        for(String key : deleteKeys){
            System.out.println("key : " + key);
        }*/



        /*String key = "rtasdfcoco";
        String value = "cocoTokenValue";
        Duration expire = Duration.ofMinutes(2L);

        redisTemplate.opsForValue().set(key, value, expire);

        String redisVal = redisTemplate.opsForValue().get(key);
        long keyExpire = redisTemplate.getExpire(key);
        long time = 60;

        System.out.println(redisVal);
        System.out.println(keyExpire);
       System.out.println(keyExpire < 60);*/



        String key = "rtasdfcoco";

        redisTemplate.opsForValue().set(key, "refreshTokenVal", Duration.ofDays(14L));
    }

}