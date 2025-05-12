package capstone.demo.dto;

import lombok.Data;

import java.util.List;

@Data
public class ResultListDto {

    private List<ResultDto> resultList;

    private Integer documentSecurityLevel;  // 문서 전체 보안 등급
}
