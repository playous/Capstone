package capstone.demo.controller;

import capstone.demo.service.AIAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequiredArgsConstructor
@RequestMapping("/analysis")
public class AnalysisController {
    private final AIAnalysisService aiAnalysisService;

    @GetMapping("/{id}")
    public String analyzeDocumentGet(@PathVariable Long id){
        return analysis(id); // POST 메서드 재사용
    }

    @PostMapping("/{id}")
    public String analysis(@PathVariable Long id){

        aiAnalysisService.analyzeDocument(id);

        return "redirect:/docs/status/" + id;
    }
}
