package com.coditramuntana.discography.report.dto;

import com.coditramuntana.discography.lp.Lp;

public record DiscographyReportRow(
        String lpName,
        String artistName,
        long songCount,
        String authorsCsv
) {
    public static DiscographyReportRow from(Lp lp, long songCount, String authorsCsv) {
        return new DiscographyReportRow(
                lp.getName(),
                lp.getArtist().getName(),
                songCount,
                authorsCsv
        );
    }
}
