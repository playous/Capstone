package capstone.demo.dto;

import capstone.demo.domain.Document;
import lombok.Data;

@Data
public class DocumentViewDto {
    private Document document;
    private ResultListDto resultList;
    private String documentUrl;
    private String contentType;

    public DocumentViewDto(Document document, ResultListDto resultList, String documentUrl, String contentType) {
        this.document = document;
        this.resultList = resultList;
        this.documentUrl = documentUrl;
        this.contentType = contentType;
    }

    public String getDocumentName() {
        return document != null ? document.getTitle() : "";
    }
}
