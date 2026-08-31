package com.coditramuntana.discography.report;

import com.coditramuntana.discography.report.dto.DiscographyReportRow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final DiscographyReportService discographyReportService;


    public ReportController(DiscographyReportService discographyReportService) {
        this.discographyReportService = discographyReportService;
    }

    
    @GetMapping("/discography")
    public Page<DiscographyReportRow> generateReport(Pageable pageable){
        return discographyReportService.generateReport(pageable);
    }
}
