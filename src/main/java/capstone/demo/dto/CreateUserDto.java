package capstone.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserDto {

    @NotEmpty(message = "이름을 입력해주세요")
    private String name;

    @NotEmpty(message = "아이디를 입력해주세요")
    @Size(min = 4, max = 20, message = "아이디는 4~20자 사이여야 합니다")
    private String userId;

    @NotEmpty(message = "비밀번호를 입력해주세요")
    @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다")
    private String password;

    @NotEmpty(message = "이메일을 입력해주세요")
    @Email(message = "이메일 형식에 맞지 않습니다")
    private String email;

    @NotEmpty(message = "회사명을 입력해주세요")
    private String company;

}
