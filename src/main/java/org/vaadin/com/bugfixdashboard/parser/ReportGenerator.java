package org.vaadin.com.bugfixdashboard.parser;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.vaadin.com.bugfixdashboard.data.HierarchicalReport;
import org.vaadin.com.bugfixdashboard.data.ReportDay;
import org.vaadin.com.bugfixdashboard.data.ReportSummary;
import org.vaadin.com.bugfixdashboard.parser.PropertyReader.ApplicationReadableProperties;

public class ReportGenerator {

    ApplicationReadableProperties properties;

    ReportFileParser fileParser;

    public ReportGenerator(ApplicationReadableProperties properties)
            throws IOException {
        // Generating new properties, if they have changed(probably not :))
        this.properties = properties;

        fileParser = new ReportFileParser(properties);
    }

    public ReportSummary generate() {

        ReportSummary summary = new ReportSummary();

        Date startDate = properties.getFileScanStart();
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);

        SimpleDateFormat sdfForLoggin = new SimpleDateFormat(
                properties.getFileDateFormat());

        while (cal.getTime().before(now)) {
            // TODO: Logger
            System.out.println("Trying to parse day: "
                    + sdfForLoggin.format(cal.getTime()));
            HierarchicalReport reviewReport = fileParser.parseReviewReport(cal
                    .getTime());
            HierarchicalReport bfpReport = fileParser.parseBFPReport(cal
                    .getTime());
            HierarchicalReport supportReport = fileParser
                    .parseSupportReport(cal.getTime());
            HierarchicalReport tcReport = fileParser.parseTeamCityReport(cal
                    .getTime());

            ReportDay reportDay = new ReportDay(cal.getTime(), reviewReport,
                    bfpReport, supportReport, tcReport);
            summary.addDay(reportDay);
            if (reportDay.hasReports()) {
                System.out.println("DAY WAS FOUND!");
            }

            cal.add(Calendar.DATE, 1);
        }

        return summary;
    }

}
