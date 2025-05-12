package capstone.demo.controller;

import capstone.demo.domain.Document;
import capstone.demo.domain.User;
import capstone.demo.dto.ResultListDto;
import capstone.demo.service.AIAnalysisService;
import capstone.demo.service.DocumentService;
import capstone.demo.service.S3Service;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final S3Service s3Service;

    @PostMapping("/docs/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             @RequestParam("title") String title,
                             RedirectAttributes redirectAttributes,
                             HttpSession httpSession) {

        try {
            if (file.isEmpty()) {
                redirectAttributes.addFlashAttribute("message", "파일을 선택해주세요.");
                redirectAttributes.addFlashAttribute("messageType", "error");
                return "upload";
            }

            String contentType = file.getContentType();
            if (!contentType.equals("text/plain") &&
                    !contentType.equals("application/pdf") &&
                    !contentType.equals("image/jpeg")) {
                redirectAttributes.addFlashAttribute("message", "지원하지 않는 파일 형식입니다.");
                redirectAttributes.addFlashAttribute("messageType", "error");
                return "upload";
            }

            User user = (User) httpSession.getAttribute("logInUser");

            Document document = documentService.uploadDocument(file,user,title);

            redirectAttributes.addFlashAttribute("message", "문서가 성공적으로 업로드되었습니다.");
            redirectAttributes.addFlashAttribute("messageType", "success");
            return "redirect:/docs/" + document.getDocumentId() + "/analysis";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "오류 발생: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/docs/upload";
        }
    }

    @GetMapping("/docs/{id}")
    public String showDocument(@PathVariable Long id,
                               Model model) {
        Document document = documentService.getDocument(id);
        ResultListDto resultListDto = documentService.documentToDto(document);
        byte[] rawFileContent = s3Service.getFileContent(document.getS3Key());

        // byte[]를 String으로 변환
        String fileContent = new String(rawFileContent, StandardCharsets.UTF_8);

        model.addAttribute("resultList", resultListDto);
        model.addAttribute("fileContent", fileContent);

        return "document-view";
    }

    @PostMapping("/docs/{id}/delete")
    public String deleteDocument(@PathVariable Long id,
                                 RedirectAttributes redirectAttributes,
                                 HttpSession httpSession) {
        User user = (User) httpSession.getAttribute("logInUser");
        documentService.deleteDocument(id,user);

        redirectAttributes.addFlashAttribute("message", "문서가 삭제되었습니다.");
        redirectAttributes.addFlashAttribute("messageType", "success");

        return "redirect:/docs/list";
    }

}
