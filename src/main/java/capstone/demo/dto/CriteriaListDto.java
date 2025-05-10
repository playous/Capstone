package capstone.demo.dto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class CriteriaListDto {

    @Valid
    private List<CriteriaDto> criteriaList;
}
