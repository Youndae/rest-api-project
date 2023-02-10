package com.board.boardapp.connection;

import com.board.boardapp.dto.HierarchicalBoardDTO;
import com.board.boardapp.dto.HierarchicalBoardListDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.DataInput;
import java.util.Arrays;
import java.util.List;

@Service
public class RestCallWebClient {

    /**
     * return을 String으로 리턴할것인가
     * 아니면 이걸 다시 dto에 담아서 리턴을 할것인가
     * 아니면 Object로 parsing해서 리턴할것인가
     * <p>
     * 만약 String으로 return을 하는 경우 front에서 받아 처리할때 문제가 발생하지 않는가
     * Object 타입으로 리턴할 경우 이점이 무엇인가
     * dto 타입으로 리턴을 하게 되면 Collection 타입을 또 사용하게 될텐데 그 경우는 어떻게 할것이며
     * Page로 리턴이 되는 경우 그건 또 어떻게 처리를 할것인가
     *
     * 생각드는 의문점.
     * jquery에서 바로 API를 호출해서 처리해도 되는데
     * 굳이 서버에서 호출하도록 하는 이유는?
     * 이게 지금 검색에서 잘 나오질 않는데 생각되는 점.
     * jquery에서 api url로 요청을 보내게 되면
     * 바로 받아서 사용할 수 있으나 코드가 노출될 수 있다는 단점이 있다?
     * 아무래도 개발자도구에서 연결된 js 파일을 다 볼 수 있으니까.
     * 하지만 그렇다고 해도 어차피 request에는 인증방식(토큰이나 시큐리티 세션)정도 말고는
     * 넘어가는 데이터 정보는 중요한 데이터 정보가 없을것.
     * 근데 그건 어차피 단일 서버 방식에서도 동일한 조건이기 때문에 큰 문제는 없을것이라고 생각.
     *
     * 이걸 나눠서 사용할 때 서버에서 처리하도록 하는 이유는
     * 서버단에서 데이터 가공이 필요한 경우나, MVC 패턴을 유지하기 위한것이 아닌가 싶음.
     * 또 하나. API키가 꼭 있어야 하는 경우 이걸 클라이언트에서 보관하도록 할 수 없기 때문에 서버에서 API키를 보관하고 이 API키를 같이 보내야 하는 경우.
     * 아니면 이게 뭐 한 2020년 정도까지는 쓰고 그 뒤로는 안쓰는 방법이라거나?
     * MVC 패턴을 유지하기 위해 서버단에서 처리를 한다면 애초에 호출하는 위치가 페이지 생성 뒤가 아닌
     * 페이지 요청시에 바로 api를 호출해서 model에 담아 넘겨주는 방식이 유효하다고 보임.
     *
     * 하지만 이런 방식의 단점으로는 Application Server에도 DTO가 존재해야 하고.
     * 그럼 서버에서 수정이 발생할 때 모든 Application Server들의 DTO를 같이 수정해줘야 한다는 번거로움이 발생.
     * RESTAPI를 분리해서 사용하는 이유가 하나의 서버를 구축해놓고 그걸 호출해서 crud를 처리하도록 해
     * 다양한 구조의 서버(웹, 모바일 등)에서 처리할 수 있도록 하는게 목적인데
     * 애초에 설계를 잘 하면 되지! 라고 말하면 할말이 없지만 수정할 일은 존재할 수도 있는 것.
     * 그럼 그때마다 모든 서버의 DTO를 수정하는것이 맞는건지
     * 아니면 그렇더라도 DTO를 만들어서 처리하는것이 맞는건지
     * 아니면 MVC 패턴이 목적이 아니라 서버에서 꼭 연결을 해야하는 다른 이유가 있는것인지가 중요 포인트일듯.
     *
     */
    public List<HierarchicalBoardDTO> getHierarchicalBoardList() throws JsonProcessingException {
        WebClient client = WebClient.builder()
                .baseUrl("http://localhost:9095")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        Object response = client.get().uri(uriBuilder -> uriBuilder.path("/board/board-list").build()).retrieve().bodyToMono(Object.class).block();

        System.out.println("response ");
        System.out.println(response);
        System.out.println("-----------------");

        /*Object response2 = client.get().uri(uriBuilder -> uriBuilder.path("/board/board-list").build());

        JSONObject response3 = new JSONObject(client.get().uri(uriBuilder -> uriBuilder.path("/board/board-list").build()).retrieve().bodyToMono(JSONObject.class).block());

        JSONObject response4 = new JSONObject(client.get().uri(uriBuilder -> uriBuilder.path("/board/board-list").build()).retrieve().bodyToFlux(JSONObject.class).blockFirst());

        *//*ResponseEntity response2 = client.get()
                .uri(uriBuilder -> uriBuilder.path("/board/board-list").build())
                .retrieve().bodyToMono(ResponseEntity.class).block();

        System.out.println("restCall responseStatus : " + response2.getStatusCode());*//*

        ObjectMapper om = new ObjectMapper();

//        HierarchicalBoardDTO dto = om.readValue(response, HierarchicalBoardDTO.class);

//        List<HierarchicalBoardDTO> dtoList = om.convertValue(response2, om.getTypeFactory().constructCollectionType(List.class, HierarchicalBoardDTO.class));

//        List<HierarchicalBoardDTO> dtoList2 = om.readValue(response, om.getTypeFactory().constructCollectionType(List.class, HierarchicalBoardDTO.class));

        System.out.println("response3 : " + response3.has("content"));

        System.out.println("response4 : " + response4.has("content"));

        JSONObject content = response3.getJSONObject("content");

        List<HierarchicalBoardDTO> dtoList = om.convertValue(content, om.getTypeFactory().constructCollectionType(List.class, HierarchicalBoardDTO.class));

        System.out.println("dtoList get 1 : " + dtoList.get(1));
        System.out.println("dtoList size : " + dtoList.size());
*/

        ObjectMapper om = new ObjectMapper();

        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

//        HierarchicalBoardListDTO dtoList = om.convertValue(response, TypeFactory.defaultInstance().constructCollectionType(List.class, HierarchicalBoardListDTO.class));

        HierarchicalBoardListDTO dtoList = om.convertValue(response, HierarchicalBoardListDTO.class);


//        System.out.println("dto : " + dto);

        System.out.println(dtoList.getContent());

        System.out.println("------------------------------");

        System.out.println(dtoList.getPageDTO());


        String response2 = client.get().uri(uriBuilder -> uriBuilder.path("/board/board-list").build()).retrieve().bodyToMono(String.class).block();

        ObjectMapper om2 = new ObjectMapper();

        om2.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        HierarchicalBoardListDTO dto = om2.readValue(response2, HierarchicalBoardListDTO.class);

        System.out.println("----------------------------");
        System.out.println(dto.toString());
        System.out.println("----------------------------");



        return null;
    }



}
