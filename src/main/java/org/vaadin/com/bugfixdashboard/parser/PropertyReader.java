package org.vaadin.com.bugfixdashboard.parser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.vaadin.com.bugfixdashboard.data.ReportSummary.ReportType;

public class PropertyReader {

    private static final String ROOTSCAN_DIRECTORY = "rootscan_directory";

    private static final String DAY_DATE_FORMAT = "file_date_format";
    private static final String FOLDER_DATE_FORMAT = "folder_date_format";
    private static final String COMPONENT_IDS = "component_ids";

    private static final String COMPONENT_FILEPREFIX_ = "fileprefix_";
    private static final String COMPONENT_NAMEPREFIX_ = "component_";
    private static final String COMPONENT_NUMBER_FORMAT_ = "number_representation_";
    private static final String COMPONENT_PIECHART_PARSELEVEL_ = "piechart_parse_level_";

    public static final String FILENAMESCAN_START_DATE = "filename_scan_startdate";

    public static final String HISTORY_REPRESENTATION_DAYS_BACK = "representation_historySpan_daysBackDefault";

    private Properties currentProperties = null;

    private Date fileScanStart = null;

    private String rootScanDirectory;

    private String fileDateFormat;
    private String folderDateFormat;

    private final List<ReportType> reportTypes = new ArrayList<ReportType>();

    private int historyRepresentationSpan;

    public final ApplicationReadableProperties readableProperties;

    public static String DEFAULT_NUMBER_FORMAT = "0.#";

    public PropertyReader() {
        readableProperties = new ApplicationReadableProperties();
    }

    public void updateProperties() throws IOException {
        currentProperties = readProperties();

        currentProperties.getProperty(PropertyReader.FILENAMESCAN_START_DATE);

        fileScanStart = getFileScanStart(currentProperties
                .getProperty(FILENAMESCAN_START_DATE));

        historyRepresentationSpan = Integer.parseInt(currentProperties
                .getProperty(HISTORY_REPRESENTATION_DAYS_BACK));

        rootScanDirectory = currentProperties.getProperty(ROOTSCAN_DIRECTORY);
        fileDateFormat = currentProperties.getProperty(DAY_DATE_FORMAT);
        folderDateFormat = currentProperties.getProperty(FOLDER_DATE_FORMAT);

        readComponentSpecificData();
    }

    private void readComponentSpecificData() {
        reportTypes.clear();
        String ids = currentProperties.getProperty(COMPONENT_IDS);
        if (ids == null || ids.length() <= 0) {
            return;
        }

        List<String> idList = Arrays.asList(ids.split(","));

        for (String idStr : idList) {
            String filePrefix = getObligatoryComponentProperty(
                    COMPONENT_FILEPREFIX_, idStr);
            String componentName = getObligatoryComponentProperty(
                    COMPONENT_NAMEPREFIX_, idStr);
            String numberFormat = getOptionalOrDefaultComponentProperty(
                    COMPONENT_NUMBER_FORMAT_, idStr, DEFAULT_NUMBER_FORMAT);
            String pieChartParseLevelStartStr = getOptionalOrDefaultComponentProperty(
                    COMPONENT_PIECHART_PARSELEVEL_, idStr, "2");
            int idInt = Integer.parseInt(idStr);

            Integer pieChartParseLevelStart = Integer
                    .parseInt(pieChartParseLevelStartStr);

            ReportType reporType = new ReportType(componentName, numberFormat,
                    filePrefix, idInt, pieChartParseLevelStart);
            reportTypes.add(reporType);
        }
    }

    /**
     * Throws a RTE if property not found. Will never return null
     * 
     * @param key
     * @return
     */
    private String getObligatoryComponentProperty(String componentPrefixKey,
            String idString) {
        String property = currentProperties.getProperty(componentPrefixKey
                + idString);
        if (property == null) {
            throw new RuntimeException("Could not find obligatory value for ["
                    + componentPrefixKey + idString + "]");
        }

        return property;
    }

    /**
     * If key not found, default value will be returned. Default value may be
     * null
     * 
     * @param key
     * @param string
     * @return
     */
    private String getOptionalOrDefaultComponentProperty(
            String componentPrefixKey, String id, String defaultValue) {
        return currentProperties.getProperty(componentPrefixKey + id,
                defaultValue);
    }

    // public String getValue(String key) {
    // return currentProperties.getProperty(key);
    // }

    private Date getFileScanStart(String startDateString) {
        if (startDateString == null || startDateString.length() != 8) {
            throw new RuntimeException(
                    "Startdate string in properties must be in format DDMMYYY");
        }
        Calendar c = Calendar.getInstance();
        int day = Integer.parseInt(startDateString.substring(0, 2));
        int month = Integer.parseInt(startDateString.substring(2, 4)) - 1;
        int year = Integer.parseInt(startDateString.substring(4, 8));

        c.set(Calendar.MILLISECOND, 0);
        c.set(year, month, day, 0, 0, 0);
        return c.getTime();

    }

    private Properties readProperties() throws IOException {

        if (currentProperties != null) {
            return currentProperties;
        }

        Properties properties = new Properties();
        String propFileName = "application.properties";

        InputStream is = getClass().getClassLoader().getResourceAsStream(
                propFileName);
        properties.load(is);
        if (is == null) {
            throw new FileNotFoundException("property file '" + propFileName
                    + "' not found in the classpath");
        }
        return properties;
    }

    /**
     * This is what the user should see
     * 
     */
    public class ApplicationReadableProperties {

        /**
         * Returns file scan start with hours and below set to zero
         * 
         * @return
         */
        public Date getFileScanStart() {
            return fileScanStart;
        }

        public String getRootScanDirectory() {
            return rootScanDirectory;
        }

        public List<ReportType> getReportTypes() {
            return Collections.unmodifiableList(reportTypes);
        }

        public String getFileDateFormat() {
            return fileDateFormat;
        }

        public String getFolderDateFormat() {
            return folderDateFormat;
        }

        public int getHistoryRepresentationSpan() {
            return historyRepresentationSpan;
        }
    }

}
