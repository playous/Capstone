package capstone.demo.controller;

import capstone.demo.domain.Document;
import capstone.demo.domain.User;
import capstone.demo.service.DocumentService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;


@Controller
@RequiredArgsConstructor
public class MainController {

    private final DocumentService documentService;

    @GetMapping("/")
    public String main(){
        return "redirect:/login";
    }


    @GetMapping("/dashboard")
        public String dashboard(Model model,
                                HttpSession session){

        User user = (User)session.getAttribute("logInUser");
        Long userId = user.getId();

        int[] securityLevelCounts = new int[4];
        for (int i = 1; i <= 4; i++) {
            securityLevelCounts[i-1] = documentService.getDocumentsBySecurityLevel(i,userId);
        }

        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Document> recentDocuments = documentService.getDocumentsByUser(userId, pageable);
        model.addAttribute("recentDocuments", recentDocuments);

        model.addAttribute("securityLevelCounts", securityLevelCounts);

        return "dashboard";
    }

    @GetMapping("/api/docs/{documentId}/status")
    @ResponseBody
    public Map<String, Object> getDocumentStatus(@PathVariable Long documentId) {
        Document document = documentService.getDocument(documentId);
        Map<String, Object> response = new HashMap<>();

        response.put("status", document.getStatus());

        return response;
    }

}
