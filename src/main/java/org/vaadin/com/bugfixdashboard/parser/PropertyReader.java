package org.vaadin.com.bugfixdashboard.parser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

public class PropertyReader {

    private static final String ROOTSCAN_DIRECTORY = "rootscan_directory";
    private static final String FILEPREFIX_REVIEW = "fileprefix_review";
    private static final String FILEPREFIX_BFP = "fileprefix_bfp";
    private static final String FILEPREFIX_SUPPORT = "fileprefix_support";
    private static final String FILEPREFIX_TEAMCITY = "fileprefix_failingtests";

    private static final String DAY_DATE_FORMAT = "file_date_format";
    private static final String FOLDER_DATE_FORMAT = "folder_date_format";

    public static final String FILENAMESCAN_START_DATE = "filename_scan_startdate";

    public static final String HISTORY_REPRESENTATION_DAYS_BACK = "representation_historySpan_daysBackDefault";

    private Properties currentProperties = null;

    private Date fileScanStart = null;

    private String rootScanDirectory;

    private String filePrefixReview;
    private String filePrefixBFP;
    private String filePrefixSupport;
    private String filePrefixTeamCity;

    private String fileDateFormat;
    private String folderDateFormat;

    private int historyRepresentationSpan;

    public final ApplicationReadableProperties readableProperties;

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
        filePrefixReview = currentProperties.getProperty(FILEPREFIX_REVIEW);
        filePrefixBFP = currentProperties.getProperty(FILEPREFIX_BFP);
        filePrefixSupport = currentProperties.getProperty(FILEPREFIX_SUPPORT);
        filePrefixTeamCity = currentProperties.getProperty(FILEPREFIX_TEAMCITY);
        fileDateFormat = currentProperties.getProperty(DAY_DATE_FORMAT);
        folderDateFormat = currentProperties.getProperty(FOLDER_DATE_FORMAT);
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

        public String getFilePrefixReview() {
            return filePrefixReview;
        }

        public String getFilePrefixBFP() {
            return filePrefixBFP;
        }

        public String getFilePrefixSupport() {
            return filePrefixSupport;
        }

        public String getFilePrefixTeamCity() {
            return filePrefixTeamCity;
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
