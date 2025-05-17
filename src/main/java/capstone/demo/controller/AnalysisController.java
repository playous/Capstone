package capstone.demo.controller;

import capstone.demo.dto.ResultDto;
import capstone.demo.dto.ResultListDto;
import capstone.demo.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/analysis")
public class AnalysisController {

    /**
     * 구현중
     */

    private final DocumentService documentService;

    @GetMapping("/{id}")
    public String analyzeDocumentGet(@PathVariable Long id){
        return analysis(id); // POST 메서드 재사용
    }

    @PostMapping("/{id}")
    public String analysis(@PathVariable Long id){

        /*
        AI쪽으로 분석 넘김
         */

        ResultListDto resultListDto = new ResultListDto();
        Integer level = ((id.intValue() % 4 )+ 1);
        resultListDto.setDocumentSecurityLevel(level);
        List<ResultDto> list = new ArrayList<>();

        ResultDto resultDto1 = new ResultDto();
        ResultDto resultDto2 = new ResultDto();
        ResultDto resultDto4 = new ResultDto();
        ResultDto resultDto3 = new ResultDto();
        ResultDto resultDto5 = new ResultDto();

        resultDto1.setLevel(1);
        resultDto1.setName("개인정보");
        resultDto1.setDescription("이름, 연락처, 주민번호가 포함되어 있습니다.");
        resultDto2.setLevel(2);
        resultDto2.setName("~~정보");
        resultDto2.setDescription("~~가 포함되어 있습니다.");
        resultDto3.setLevel(3);
        resultDto3.setName("~~정보");
        resultDto3.setDescription("~~가 포함되어 있습니다.");
        resultDto4.setLevel(4);
        resultDto4.setName("~~정보");
        resultDto4.setDescription("~~가 포함되어 있습니다.");
        resultDto5.setLevel(4);
        resultDto5.setName("~~정보");
        resultDto5.setDescription("~~가 포함되어 있습니다.");

        list.add(resultDto1);
        list.add(resultDto2);
        list.add(resultDto3);
        list.add(resultDto4);
        list.add(resultDto5);

        resultListDto.setResultList(list);

        log.info(resultListDto.toString());
        documentService.saveDocumentAnalysisResults(id, resultListDto);

        return "redirect:/docs/" + id;
    }
}
