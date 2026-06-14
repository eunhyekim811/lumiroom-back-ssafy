package com.ssafy.lumiroom.batch.reader;

import com.ssafy.lumiroom.batch.dto.SecurityFacilityDto;
import org.springframework.batch.item.ItemReader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

public class SecurityFacilityApiReader implements ItemReader<SecurityFacilityDto> {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String serviceKey;
    private final int numOfRows;

    private int pageNo;
    private int totalCount = -1;
    private int totalPages = -1;
    private boolean finished = false;
    private final Queue<SecurityFacilityDto> currentPageData = new LinkedList<>();

    public SecurityFacilityApiReader(String baseUrl, String serviceKey, int startPage, int numOfRows) {
        this.baseUrl = baseUrl;
        this.serviceKey = serviceKey;
        this.pageNo = startPage;
        this.numOfRows = numOfRows;
        this.restTemplate = new RestTemplate();
        this.restTemplate.setInterceptors(Collections.singletonList((request, body, execution) -> {
            HttpHeaders headers = request.getHeaders();
            headers.set(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
            headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML_VALUE);
            headers.set(HttpHeaders.ACCEPT_LANGUAGE, "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
            return execution.execute(request, body);
        }));
    }

    @Override
    public SecurityFacilityDto read() throws Exception {
        if (finished) {
            return null;
        }

        if (currentPageData.isEmpty()) {
            fetchNextPage();
        }

        return currentPageData.poll();
    }

    private void fetchNextPage() {
        if (totalPages != -1 && pageNo > totalPages) {
            finished = true;
            return;
        }

        try {
            String url = baseUrl + "?serviceKey=" + serviceKey
                    + "&pageNo=" + pageNo
                    + "&numOfRows=" + numOfRows
                    + "&returnType=xml";

            ResponseEntity<byte[]> response = restTemplate.getForEntity(new URI(url), byte[].class);
            byte[] responseBody = response.getBody();
            logResponsePreview(responseBody);
            Document document = parseXml(responseBody);

            if (totalCount == -1) {
                totalCount = extractTotalCount(document);
                totalPages = (int) Math.ceil((double) totalCount / numOfRows);
                System.out.println(">>> [SecurityFacilityReader] totalCount: " + totalCount + ", totalPages: " + totalPages);
            }

            NodeList itemNodes = document.getElementsByTagName("item");
            if (itemNodes.getLength() > 0) {
                System.out.println(">>> [SecurityFacilityReader] " + pageNo + "페이지 수집 성공, 가져온 데이터 수: " + itemNodes.getLength());
                for (int i = 0; i < itemNodes.getLength(); i++) {
                    currentPageData.offer(toDto((Element) itemNodes.item(i)));
                }
            }

            pageNo++;
            if ((totalPages != -1 && pageNo > totalPages) || currentPageData.isEmpty()) {
                finished = currentPageData.isEmpty();
            }

            Thread.sleep(1000);
        } catch (Exception e) {
            throw new RuntimeException("치안시설 API 데이터 호출 중 에러 발생", e);
        }
    }

    private void logResponsePreview(byte[] responseBody) {
        if (responseBody == null) {
            System.out.println(">>> [SecurityFacilityReader] response body preview: <null>");
            return;
        }

        String preview = new String(responseBody, 0, Math.min(500, responseBody.length), java.nio.charset.StandardCharsets.UTF_8);
        System.out.println(">>> [SecurityFacilityReader] response body preview: " + preview);
    }

    private Document parseXml(byte[] responseBody) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);
        return factory.newDocumentBuilder().parse(new InputSource(new ByteArrayInputStream(responseBody)));
    }

    private int extractTotalCount(Document document) {
        NodeList totalCountNodes = document.getElementsByTagName("totalCount");
        if (totalCountNodes.getLength() == 0) {
            return 0;
        }

        String totalCountText = totalCountNodes.item(0).getTextContent();
        if (totalCountText == null || totalCountText.isBlank()) {
            return 0;
        }

        return Integer.parseInt(totalCountText.trim());
    }

    private SecurityFacilityDto toDto(Element itemElement) {
        SecurityFacilityDto dto = new SecurityFacilityDto();
        dto.setObjtId(getText(itemElement, "objt_id"));
        dto.setPolice(getText(itemElement, "police"));
        dto.setPolcsttn(getText(itemElement, "polcsttn"));
        dto.setFcltyTy(getText(itemElement, "fclty_ty"));
        dto.setFcltyCd(getText(itemElement, "fclty_cd"));
        dto.setFcltyNm(getText(itemElement, "fclty_nm"));
        dto.setAdres(getText(itemElement, "adres"));
        dto.setRnAdres(getText(itemElement, "rn_adres"));
        dto.setTelno(getText(itemElement, "telno"));
        dto.setCtprvnCd(getText(itemElement, "ctprvn_cd"));
        dto.setSggCd(getText(itemElement, "sgg_cd"));
        dto.setX(getBigDecimal(itemElement, "x"));
        dto.setY(getBigDecimal(itemElement, "y"));
        dto.setTmpX(getBigDecimal(itemElement, "tmp_x"));
        dto.setTmpY(getBigDecimal(itemElement, "tmp_y"));
        return dto;
    }

    private String getText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() == 0) {
            return null;
        }

        String text = nodes.item(0).getTextContent();
        return text == null || text.isBlank() ? null : text.trim();
    }

    private BigDecimal getBigDecimal(Element parent, String tagName) {
        String text = getText(parent, tagName);
        return text == null ? null : new BigDecimal(text);
    }
}
