package com.coditramuntana.discography.report;

import com.coditramuntana.discography.author.Author;
import com.coditramuntana.discography.lp.Lp;
import com.coditramuntana.discography.lp.LpRepository;
import com.coditramuntana.discography.report.dto.DiscographyReportRow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true)
public class DiscographyReportService {
    private final LpRepository lpRepository;


    public DiscographyReportService(LpRepository lpRepository) {
        this.lpRepository = lpRepository;
    }

    public Page<DiscographyReportRow> generateReport(Pageable pageable) {

        Pageable effective = pageable.getSort().isUnsorted()
                ? PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by("artist.name", "name"))
                : pageable;

        return lpRepository.findAllForReport(effective)
                .map(lp-> {
                    long songCount = lp.getSongs().size();
                    String authorsCsv = lp.getSongs().stream()
                            .flatMap(song -> song.getAuthors().stream())
                            .map(Author::getName)
                            .distinct()
                            .sorted()
                            .collect(Collectors.joining(", "));
                    return DiscographyReportRow.from(lp,songCount,authorsCsv);
                });
    }
}
