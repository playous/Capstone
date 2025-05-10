package capstone.demo.controller;

import capstone.demo.domain.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("user")
    public User addUserToModel(HttpSession session) {
        return (User) session.getAttribute("logInUser");
    }
}
