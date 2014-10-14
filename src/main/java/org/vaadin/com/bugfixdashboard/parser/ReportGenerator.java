package org.vaadin.com.bugfixdashboard.parser;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.vaadin.com.bugfixdashboard.data.HierarchicalReport;
import org.vaadin.com.bugfixdashboard.data.ReportDay;
import org.vaadin.com.bugfixdashboard.data.ReportSummary;
import org.vaadin.com.bugfixdashboard.data.ReportSummary.ReportType;
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

        List<ReportType> reportTypes = properties.getReportTypes();
        ReportSummary summary = new ReportSummary(reportTypes);

        Date startDate = properties.getFileScanStart();
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);

        SimpleDateFormat sdfForLoggin = new SimpleDateFormat(
                properties.getFileDateFormat());

        // TODO: At the moment we create deltas with difference to one day back,
        // but we may want to change this behavior at some point

        ReportDay lastReportDay = null;

        System.out.println("Trying to parse day: "
                + sdfForLoggin.format(cal.getTime()));
        while (cal.getTime().before(now)) {
            LinkedHashMap<ReportType, HierarchicalReport> reportTypesToReportComponents = new LinkedHashMap<ReportSummary.ReportType, HierarchicalReport>();
            for (ReportType repType : reportTypes) {
                HierarchicalReport report = fileParser.parseReport(
                        cal.getTime(), repType);
                if (report != null) {
                    reportTypesToReportComponents.put(repType, report);
                }
            }

            if (lastReportDay != null) {

                for (HierarchicalReport report : reportTypesToReportComponents
                        .values()) {
                    createReportLevelDeltas(report,
                            lastReportDay.getReportByType(report
                                    .getReportType()));
                }

            }

            ReportDay reportDay = new ReportDay(cal.getTime(),
                    reportTypesToReportComponents);
            lastReportDay = reportDay;
            summary.addDay(reportDay);
            if (reportDay.hasReports()) {
                System.out.println("This day has atleast one report!");
            }

            cal.add(Calendar.DATE, 1);
        }

        return summary;
    }

    private void createReportLevelDeltas(HierarchicalReport currentReport,
            HierarchicalReport lastReport) {
        if (currentReport == null) {
            return;
        }

        currentReport.createReportLevelDeltas(lastReport);
    }

}
