package capstone.demo.dto;

import lombok.Data;

@Data
public class AnalysisRequestDto {

    private String fileUrl;
    private CriteriaListDto criterion;
}
