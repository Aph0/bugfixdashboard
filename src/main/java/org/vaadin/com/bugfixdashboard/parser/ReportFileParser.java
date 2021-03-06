package org.vaadin.com.bugfixdashboard.parser;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.vaadin.com.bugfixdashboard.data.HierarchicalReport;
import org.vaadin.com.bugfixdashboard.data.ReportLevel;
import org.vaadin.com.bugfixdashboard.data.ReportSummary.ReportType;
import org.vaadin.com.bugfixdashboard.util.DateUtil;

public class ReportFileParser {

    private final PropertyReader.ApplicationReadableProperties properties;

    public ReportFileParser(
            PropertyReader.ApplicationReadableProperties properties) {
        this.properties = properties;
    }

    public HierarchicalReport parseReport(Date date, final ReportType reportType) {
        date = DateUtil.clearDateBelowDays(date);
        HierarchicalReport report = new HierarchicalReport(
                DateUtil.clearDateBelowDays(date), reportType);

        String fileName = constructFileName(date, reportType.getFilePrefix());
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("File did not exist: " + fileName);
            return null;
        }

        try {
            Document doc = Jsoup.parse(file, null);
            // TODO: When using Jsoup (because it is a very fast way to get your
            // hands on), the xml is wrapped with html. Let's start the root
            // searching from the <body> tag
            List<Element> rootBody = doc.getElementsByTag("measurementcontent");
            if (rootBody.size() <= 0) {
                System.out
                        .println("Warning! No data found in file or file format has changed: "
                                + fileName);
                return null;
            }
            if (rootBody.size() > 1) {
                System.out
                        .println("Warning! Found more than one <measurementcontent> elements: "
                                + fileName);
                return null;
            }
            for (Element element : rootBody.get(0).children()) {
                ReportLevel rootLevel = createReportLevelsRecursively(element,
                        null);
                report.addRootLevel(rootLevel);
            }

            return report;

        } catch (IOException e) {
            // TODO: handle this
            e.printStackTrace();
            return null;
        }

    }

    private ReportLevel createReportLevelsRecursively(Element element,
            ReportLevel reportParent) {
        String name = element.attr("name");
        Number value = getNumberFrom(element.attr("value"));
        ReportLevel reportLevel = new ReportLevel(reportParent, name, value);
        for (Element el : element.children()) {
            reportLevel
                    .addChild(createReportLevelsRecursively(el, reportLevel));
        }

        return reportLevel;

    }

    private Number getNumberFrom(String string) {
        if (string == null || string.equals("")) {
            return null;
        }
        Number number = null;
        try {
            return Integer.parseInt(string.trim());
        } catch (NumberFormatException nfe) {

        }

        try {
            return Double.parseDouble(string);
        } catch (NumberFormatException nfe) {
            System.err.println();
        }
        return null;
    }

    /**
     * Constrcuts a fileName in style with "rootPath\032014\bfp_01032014.xml"
     * 
     * @param date
     * @param filePrefix
     * @return
     */
    private String constructFileName(Date date, String filePrefix) {
        String root = properties.getRootScanDirectory();
        if (!root.endsWith(File.separator)) {
            root = root + File.separator;
        }
        SimpleDateFormat fileDateFormat = new SimpleDateFormat(
                properties.getFileDateFormat());
        SimpleDateFormat folderDateFormat = new SimpleDateFormat(
                properties.getFolderDateFormat());
        String fileName = root + folderDateFormat.format(date) + File.separator
                + filePrefix + fileDateFormat.format(date) + ".xml";
        return fileName;
    }

}
