package capstone.demo.dto;

import lombok.Data;

@Data
public class AnalysisRequestDto {

    private Long documentId;
    private String fileUrl;
    private CriteriaListDto criterion;
}
