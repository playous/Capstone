package capstone.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CriteriaDto {

    @NotBlank(message = "보안 요소를 입력해주세요.")
    private String name;
    private Integer importance;
}
