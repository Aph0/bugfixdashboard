package org.vaadin.com.bugfixdashboard.parser;

import java.io.IOException;
import java.util.Date;

import org.vaadin.com.bugfixdashboard.data.ReportSummary;
import org.vaadin.com.bugfixdashboard.parser.PropertyReader.ApplicationReadableProperties;

public class ReportReader {

    // TODO, make cloner
    private static ReportSummary currentReport;

    private static ReportGenerator reportGenerator;

    private static Date lastGenerationDate = null;

    private static PropertyReader properties;

    public synchronized static ReportSummary getReportSummary(
            boolean forceNewGeneration) throws IOException {

        if (forceNewGeneration || currentReport == null || needsGeneration()) {
            // First re-read the properties
            properties = getUpdatedPropertyReader();
            // Then pass on the readable stuff
            currentReport = generateReport(properties.readableProperties);
            lastGenerationDate = new Date();
        }

        return currentReport;
    }

    public synchronized static ReportSummary refreshReportSummary()
            throws IOException {
        currentReport = null;
        return getReportSummary(true);
    }

    public synchronized static ApplicationReadableProperties getReadableProperties()
            throws IOException {
        if (properties == null) {
            properties = getUpdatedPropertyReader();
        }
        return properties.readableProperties;
    }

    private static PropertyReader getUpdatedPropertyReader() throws IOException {
        PropertyReader pr = new PropertyReader();
        pr.updateProperties();
        return pr;
    }

    private static boolean needsGeneration() {
        if (lastGenerationDate == null) {
            return true;
        }

        // The check will be between every three hours
        return (((new Date()).getTime() - 3 * 3600 * 1000) > lastGenerationDate
                .getTime());

    }

    private static ReportSummary generateReport(
            ApplicationReadableProperties readableProperties)
            throws IOException {
        ReportGenerator r = new ReportGenerator(readableProperties);
        return r.generate();
    }
}
