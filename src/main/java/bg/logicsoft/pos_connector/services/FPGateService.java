package bg.logicsoft.pos_connector.services;

import bg.logicsoft.pos_connector.config.AppProperties;
import bg.logicsoft.pos_connector.dto.ERPNextSalesInvoiceResponseDTO;
import bg.logicsoft.pos_connector.dto.FPGJsonRpcPrintRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class FPGateService {

    private final RestTemplate restTemplate;
    private final AppProperties appProperties;

    public FPGateService(
            RestTemplateBuilder restTemplateBuilder,
            AppProperties appProperties
    ) {
        this.restTemplate = restTemplateBuilder
                .connectTimeout(Duration.ofSeconds(5))
                .readTimeout(Duration.ofSeconds(15))
                .build();
        this.appProperties = appProperties;
    }

    // Build and send JSON-RPC PrintFiscalCheck command based on ERPNextSalesInvoiceResponseDTO
    public Map<String, Object> printFiscalReceipt(ERPNextSalesInvoiceResponseDTO invoice) {
        final String endpoint = appProperties.getFpGateUrl(); // include path if your JSON-RPC endpoint isn't root

        FPGJsonRpcPrintRequestDTO requestBody = buildPrintRequestFromInvoiceResponse(invoice);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<FPGJsonRpcPrintRequestDTO> request = new HttpEntity<>(requestBody, headers);

        try {
            log.info("FPGate JSON-RPC POST: method=PrintFiscalCheck, url={}", endpoint);
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    endpoint,
                    HttpMethod.POST,
                    request,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            log.info("FPGate JSON-RPC done: status={}", response.getStatusCode().value());
            return response.getBody();
        } catch (RestClientResponseException ex) {
            var status = ex.getStatusCode();
            String body = sanitizeForLog(ex.getResponseBodyAsString());
            if (status.is5xxServerError()) {
                log.error("FPGate 5xx response: status={}, body={}", status.value(), body);
            } else {
                log.warn("FPGate 4xx response: status={}, body={}", status.value(), body);
            }
            throw ex;
        } catch (ResourceAccessException ex) {
            log.warn("FPGate access error (timeout/connection): {}", rootMessage(ex));
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error calling FPGate: {}", rootMessage(ex), ex);
            throw ex;
        }
    }

    // Converts ERPNext response to JSON-RPC print request
    private FPGJsonRpcPrintRequestDTO buildPrintRequestFromInvoiceResponse(ERPNextSalesInvoiceResponseDTO invoice) {
        FPGJsonRpcPrintRequestDTO req = new FPGJsonRpcPrintRequestDTO();
        req.setJsonrpc("2.0");
        req.setMethod("PrintFiscalCheck");
        req.setId(1); // static correlation id; adjust if needed

        FPGJsonRpcPrintRequestDTO.Params params = new FPGJsonRpcPrintRequestDTO.Params();
        FPGJsonRpcPrintRequestDTO.Printer printer = new FPGJsonRpcPrintRequestDTO.Printer();
        printer.setId("PR1"); // adjust to your configured printer ID
        params.setPrinter(printer);
        params.setCommand("PrintFiscalCheck");

        List<String> args = new ArrayList<>();

        // Header: workstation/shift info; use invoice number (name) or a static workstation id
        String workstation = invoice != null && invoice.getData() != null && invoice.getData().getName() != null
                ? invoice.getData().getName()
                : "pc1";
        args.add("SON\t" + safe(workstation));

        // Items from ERPNext response
        if (invoice != null && invoice.getData() != null && invoice.getData().getItems() != null) {
            for (ERPNextSalesInvoiceResponseDTO.Item it : invoice.getData().getItems()) {
                if (it == null) continue;
                String title = it.getItemName() != null ? it.getItemName()
                        : (it.getItemCode() != null ? it.getItemCode() : "");
                BigDecimal qty = it.getQty() != null ? it.getQty() : BigDecimal.ZERO;
                BigDecimal rate = it.getRate() != null ? it.getRate() : BigDecimal.ZERO;
                // STG\t<Title>\tA\t<Qty>\t0\t<Price>
                args.add("STG\t" + safe(title) + "\tA\t" + format(qty) + "\t0\t" + format(rate));
            }
        }

        // Close items section
        args.add("STL");

        // Payment total (prefer first payment amount; fallback to grand_total)
        BigDecimal total = BigDecimal.ZERO;
        if (invoice != null && invoice.getData() != null) {
            var payments = invoice.getData().getPayments();
            if (payments != null && !payments.isEmpty() && payments.get(0) != null && payments.get(0).getAmount() != null) {
                total = payments.get(0).getAmount();
            } else if (invoice.getData().getGrandTotal() != null) {
                total = invoice.getData().getGrandTotal();
            }
        }

        // TTL\tTotal:\tCASH\t<Amount>
        args.add("TTL\tTotal:\tCASH\t" + format(total));

        params.setArguments(args);
        req.setParams(params);
        return req;
    }

    private String safe(String s) {
        return s == null ? "" : s.replace('\t', ' ');
    }

    private String format(BigDecimal bd) {
        if (bd == null) return "0.00";
        return String.format(java.util.Locale.US, "%.2f", bd);
    }

    private String sanitizeForLog(String s) {
        if (s == null) return null;
        String oneLine = s.replaceAll("[\\r\\n]+", " ");
        oneLine = oneLine.replaceAll("(?i)(token|secret|password)\\s*[:=]\\s*[^,\\s}{\"]+", "***");
        return oneLine.length() > 1000 ? oneLine.substring(0, 1000) + "..." : oneLine;
    }

    private String rootMessage(Throwable t) {
        Throwable cur = t;
        while (cur.getCause() != null) cur = cur.getCause();
        return cur.getMessage();
    }
}
