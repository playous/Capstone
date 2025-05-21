package capstone.demo.controller;

import capstone.demo.domain.User;
import capstone.demo.dto.CriteriaDto;
import capstone.demo.dto.CriteriaListDto;
import capstone.demo.service.SettingService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/settings")
public class SettingController {

    private final SettingService settingService;

    /**
     * 커스터마이징 화면
     */
    @GetMapping("")
    public String settings(Model model,
                           HttpSession session) {
        User user = (User) session.getAttribute("logInUser");

        List<CriteriaDto> criteria = settingService.getCriterionByUser(user);

        model.addAttribute("criteria", criteria);
        return "settings";
    }

    /**
     * 커스터마이징 저장
     */

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute CriteriaListDto criteriaList,
                       BindingResult bindingResult,
                       HttpSession session,
                       RedirectAttributes redirectAttributes) {

        User user = (User) session.getAttribute("logInUser");

        if (bindingResult.hasErrors()) {
            return "settings";
        }

        try {
            // 서비스로 데이터 전달
            settingService.saveCriteria(user, criteriaList.getCriteriaList());

            redirectAttributes.addFlashAttribute("message", "보안 기준이 저장되었습니다.");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "서버 오류");
            redirectAttributes.addFlashAttribute("messageType", "error");
        }

        return "redirect:/settings";
    }
}
