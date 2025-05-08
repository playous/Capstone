package capstone.demo.controller;

import capstone.demo.domain.User;
import capstone.demo.dto.CreateUserDto;
import capstone.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final UserService userService;

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session){
        User logInUser = (User) session.getAttribute("logInUser");
        if (logInUser != null) {
            log.info("[로그아웃 완료] UserId : {} , UserName : {}", logInUser.getUserId(), logInUser.getName());
        }
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/signup")
    public String signup(Model model){
        model.addAttribute("createUserDto",new CreateUserDto());
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute("createUserDto") CreateUserDto createUserDto,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes){

        boolean hasBasicErrors = bindingResult.hasErrors();

        if (userService.isUserIdDuplicate(createUserDto.getUserId())) {
            bindingResult.rejectValue("userId", "duplicate", "이미 사용 중인 아이디입니다");
        }

        if(hasBasicErrors || bindingResult.hasErrors()){
            return "signup";
        }
        // 유효성 검사 통과, 회원 저장
        long id = userService.saveUser(createUserDto);
        log.info("[회원가입 완료] ID(primary key) = {}", id);
        redirectAttributes.addFlashAttribute("message", "회원가입이 완료되었습니다!");
        redirectAttributes.addFlashAttribute("messageType", "success");
        return "redirect:/login";
    }

    @PostMapping("/login-process")
    public String loginProcess(@RequestParam("userId") String userId,
                               @RequestParam("password") String password,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        User user = userService.authenticate(userId, password);
        if (user != null) {
            session.setAttribute("logInUser", user);
            return "redirect:/dashboard"; // 로그인 성공 후 대시보드로 이동
        } else {
            redirectAttributes.addFlashAttribute("message", "아이디 또는 비밀번호가 일치하지 않습니다.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/login";
        }
    }
}
