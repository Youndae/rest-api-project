package com.example.boardrest.service;

import com.example.boardrest.domain.dto.Criteria;
import com.example.boardrest.domain.dto.HierarchicalBoardDTO;
import com.example.boardrest.domain.dto.HierarchicalBoardListDTO;
import com.example.boardrest.domain.entity.HierarchicalBoard;
import com.example.boardrest.repository.HierarchicalBoardRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HierarchicalBoardServiceImplTest {

    @Autowired
    private HierarchicalBoardService service;

    @Autowired
    private HierarchicalBoardRepository repository;

    @Test
    void hTest() {
        long boardNo = 100018L;

//        service.deleteBoard(boardNo);
    }

    @Test
    void queryDSLPaginationTest() {
        Criteria cri = new Criteria(1, 20, 15, "9999", "t");

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