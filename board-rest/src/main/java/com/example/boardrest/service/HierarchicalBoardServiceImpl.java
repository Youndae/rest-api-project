package com.example.boardrest.service;

import com.example.boardrest.customException.CustomAccessDeniedException;
import com.example.boardrest.customException.ErrorCode;
import com.example.boardrest.domain.dto.*;
import com.example.boardrest.domain.dto.responseDTO.ResponseDetailAndModifyDTO;
import com.example.boardrest.domain.dto.responseDTO.ResponsePageableListDTO;
import com.example.boardrest.domain.entity.HierarchicalBoard;
import com.example.boardrest.repository.HierarchicalBoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HierarchicalBoardServiceImpl implements HierarchicalBoardService {

    private final PrincipalService principalService;

    private final HierarchicalBoardRepository hierarchicalBoardRepository;

    // 계층형 게시판 insert
    @Override
    @Transactional(rollbackOn = Exception.class)
    public long insertBoard(HierarchicalBoardDTO dto, Principal principal) {

        return insertBoardProc(dto, principal);
    }

    // 계층형 게시판 답글 insert
    @Override
    @Transactional(rollbackOn = Exception.class)
    public long insertBoardReply(HierarchicalBoardReplyDTO dto, Principal principal) {
        HierarchicalBoardDTO boardDTO = HierarchicalBoardDTO.builder()
                                                            .boardTitle(dto.getBoardTitle())
                                                            .boardContent(dto.getBoardContent())
                                                            .boardGroupNo(dto.getBoardGroupNo())
                                                            .boardIndent(dto.getBoardIndent())
                                                            .boardUpperNo(dto.getBoardUpperNo())
                                                            .build();

        return insertBoardProc(boardDTO, principal);
    }


    public long insertBoardProc(HierarchicalBoardDTO dto, Principal principal) {
        HierarchicalBoard board = HierarchicalBoard.builder()
                                                    .boardTitle(dto.getBoardTitle())
                                                    .boardContent(dto.getBoardContent())
                                                    .member(principalService.checkPrincipal(principal))
                                                    .boardDate(Date.valueOf(LocalDate.now()))
                                                    .build();

        hierarchicalBoardRepository.save(board);
        board.setPatchBoardData(dto);
        hierarchicalBoardRepository.save(board);

        return board.getBoardNo();
    }

    // 계층형 게시판 delete
    @Override
    @Transactional(rollbackOn = Exception.class)
    public long deleteBoard(long boardNo, Principal principal) {
        //principal을 받고 작성자와 비교 필요.
        if(!principal.getName().equals(hierarchicalBoardRepository.checkWriter(boardNo)))
            new AccessDeniedException("AccessDenied");

        DeleteBoardDTO deleteData = hierarchicalBoardRepository.getDeleteData(boardNo);

        if(deleteData.getBoardIndent() == 0)
            hierarchicalBoardRepository.deleteByBoardGroupNo(boardNo);
        else {
            List<DeleteGroupListDTO> groupList = hierarchicalBoardRepository.getGroupList(deleteData.getBoardGroupNo());
            List<Long> deleteList = addDeleteDataList(groupList, boardNo, deleteData.getBoardIndent());

            hierarchicalBoardRepository.deleteAllByBoardNoList(deleteList);
        }

        return 1L;
    }

    public List<Long> addDeleteDataList(List<DeleteGroupListDTO> groupList, long delNo, int indent) {
        List<Long> deleteList = new ArrayList<>();

        for(int i = 0; i < groupList.size(); i++){
            String[] upperArr = groupList.get(i).getBoardUpperNo().split(",");

            if(upperArr.length > indent && upperArr[indent].equals(String.valueOf(delNo)))
                deleteList.add(groupList.get(i).getBoardNo());
        }

        return deleteList;
    }

    // 계층형 게시판 List
    @Override
    public ResponsePageableListDTO<HierarchicalBoardListDTO> getHierarchicalBoardList(Criteria cri, Principal principal) {

        Pageable pageable = PageRequest.of(cri.getPageNum() - 1
                                            , cri.getBoardAmount()
                                            , Sort.by("boardGroupNo").descending()
                                                    .and(Sort.by("boardUpperNo").ascending()));

        Page<HierarchicalBoardListDTO> listDTO = hierarchicalBoardRepository.findAll(cri, pageable);
        ResponsePageableListDTO<HierarchicalBoardListDTO> responseDTO = new ResponsePageableListDTO<>(listDTO, principal);

        return responseDTO;
    }

    // 계층형 게시판 patch
    @Override
    @Transactional(rollbackOn = Exception.class)
    public long patchBoard(HierarchicalBoardModifyDTO dto, long boardNo, Principal principal) {
        HierarchicalBoard board = hierarchicalBoardRepository
                                    .findById(boardNo)
                                    .orElseThrow(() -> new NullPointerException("NullPointerException"));

        String writer = board.getMember().getUserId();

        if(writer.equals(principal.getName())){
            board.setBoardTitle(dto.getBoardTitle());
            board.setBoardContent(dto.getBoardContent());
            hierarchicalBoardRepository.save(board);

            return boardNo;
        }else
            throw new CustomAccessDeniedException(ErrorCode.ACCESS_DENIED, "AccessDenied");
    }

    @Override
    public ResponseDetailAndModifyDTO<HierarchicalBoardModifyDTO> getModifyData(long boardNo, Principal principal) {
        String userId = hierarchicalBoardRepository.checkWriter(boardNo);

        if(!principal.getName().equals(userId))
            throw new NullPointerException();

        HierarchicalBoardModifyDTO dto = hierarchicalBoardRepository.getModifyData(boardNo);
        ResponseDetailAndModifyDTO<HierarchicalBoardModifyDTO> responseDTO = new ResponseDetailAndModifyDTO<>(dto, principal);


        return responseDTO;
    }

    @Override
    public ResponseDetailAndModifyDTO<HierarchicalBoardDetailDTO> getBoardDetail(long boardNo, Principal principal) {
        HierarchicalBoardDetailDTO dto = hierarchicalBoardRepository.findBoardDetailByBoardNo(boardNo);
        ResponseDetailAndModifyDTO<HierarchicalBoardDetailDTO> responseDTO = new ResponseDetailAndModifyDTO<>(dto, principal);

        return responseDTO;
    }

    @Override
    public ResponseDetailAndModifyDTO<HierarchicalBoardReplyInfoDTO> getReplyInfo(long boardNo, Principal principal) {
        HierarchicalBoardReplyInfoDTO dto = hierarchicalBoardRepository.findReplyInfoByBoardNo(boardNo);
        ResponseDetailAndModifyDTO<HierarchicalBoardReplyInfoDTO> responseDTO = new ResponseDetailAndModifyDTO<>(dto, principal);

        return responseDTO;
    }
}
