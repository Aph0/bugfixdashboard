package org.vaadin.com.bugfixdashboard.component;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.vaadin.com.bugfixdashboard.data.HierarchicalReport;
import org.vaadin.com.bugfixdashboard.data.ReportDay.DateAndValue;
import org.vaadin.com.bugfixdashboard.data.ReportLevel;
import org.vaadin.com.bugfixdashboard.data.ReportLevelDelta;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.AxisType;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.addon.charts.model.Marker;
import com.vaadin.addon.charts.model.PlotOptionsPie;
import com.vaadin.addon.charts.model.PlotOptionsSpline;
import com.vaadin.addon.charts.model.Title;
import com.vaadin.addon.charts.model.ZoomType;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * This component should be populated with two different kind of data: Data for
 * current displayable report and data representing a history for this report's
 * levels
 * 
 */
public class MultiLevelVisualizationComponent extends CustomComponent {

    private enum ChartTypes {
        PIE, HISTORY
    };

    private VerticalLayout mainLayout = new VerticalLayout();
    private HorizontalLayout container = new HorizontalLayout();
    private Label headerLabel;
    private HorizontalLayout header = new HorizontalLayout();
    private GridLayout chartContainer = new GridLayout(1, 2);

    private HorizontalLayout chartFooter = new HorizontalLayout();
    private GridLayout grid = new GridLayout(3, 1);
    private Chart historyChart;
    private Chart pieChart;
    private Chart currentChart;
    private LinkedList<ChartTypes> chartLink = new LinkedList<ChartTypes>();
    private final String HAS_NO_REPORTS_STYLENAME = "is-empty";
    private String currentNumberFormat = "#.#";

    public MultiLevelVisualizationComponent() {
        addStyleName("report-component");

        headerLabel = new Label();
        headerLabel.setStyleName(ValoTheme.LABEL_H2);
        container.setSpacing(true);
        container.addStyleName("wrapping-container");

        header.addComponent(headerLabel);
        header.setExpandRatio(headerLabel, 1);
        header.setComponentAlignment(headerLabel, Alignment.MIDDLE_LEFT);
        header.setHeight(null);
        header.setWidth("100%");

        mainLayout.addComponent(header);
        mainLayout.addComponent(container);
        container.addComponent(grid);
        container.addComponent(chartContainer);
        chartContainer.addComponent(chartFooter, 0, 1);
        chartContainer.setWidth("300");
        chartContainer.setHeight("350px");
        chartContainer.setRowExpandRatio(0, 1);
        chartFooter.setWidth("100%");
        grid.setColumnExpandRatio(0, 1);
        setCompositionRoot(mainLayout);
        mainLayout.setWidth("100%");
        grid.setWidth("100%");
        grid.setMargin(new MarginInfo(true, false, false, false));
        grid.setHeight(null);

        container.setWidth("100%");
        initializeChartFooter();
        initializeAllCharts();
        selectChart(ChartTypes.HISTORY);

        chartLink.add(ChartTypes.PIE);
        chartLink.add(ChartTypes.HISTORY);

        grid.addLayoutClickListener(new LayoutClickListener() {

            @Override
            public void layoutClick(LayoutClickEvent event) {
                System.out.println(event.getClickedComponent());

            }
        });
    }

    public void addHeaderComponent(Component c) {
        header.addComponent(c);
        header.setComponentAlignment(c, Alignment.MIDDLE_RIGHT);
    }

    private void rotateList(LinkedList<ChartTypes> list, boolean left) {

        if (left) {
            list.add(list.remove());
        } else {
            list.addFirst(list.removeLast());
        }

    }

    private void initializeChartFooter() {

        Button prev = new Button("<<");
        prev.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                rotateList(chartLink, true);
                selectChart(chartLink.getFirst());
            }
        });
        prev.addStyleName(ValoTheme.BUTTON_TINY);
        Button next = new Button(">>");
        next.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                rotateList(chartLink, false);
                selectChart(chartLink.getFirst());

            }
        });
        next.addStyleName(ValoTheme.BUTTON_TINY);

        chartFooter.addComponent(prev);
        chartFooter.addComponent(next);
        chartFooter.setExpandRatio(prev, 1);
        chartFooter.setComponentAlignment(prev, Alignment.MIDDLE_LEFT);
        chartFooter.setComponentAlignment(next, Alignment.MIDDLE_RIGHT);
        chartFooter.addStyleName("chart-footer");

    }

    /**
     * Sets the data for the current report to be displayed.
     * 
     * @param string
     */
    public void setReportData(HierarchicalReport report, String numberFormat) {

        currentNumberFormat = numberFormat;
        grid.removeAllComponents();
        if (report == null) {
            showAsEmptyReport("<Empty report>");
            return;
        }
        // Removing the stylename in case it has been set at some point
        removeStyleName(HAS_NO_REPORTS_STYLENAME);
        headerLabel.setValue(report.getHeader() + " ("
                + formatDate(report.getReportDate()) + ")");
        if (report.isDue()) {
            headerLabel.addStyleName("is-due");
        }

        for (ReportLevel level : report.getReportRoots()) {
            traverseLevelsRecursively(report, level);
        }

    }

    public void showAsEmptyReport(String reason) {
        headerLabel.setValue(reason);
        chartContainer.removeAllComponents();
        addStyleName(HAS_NO_REPORTS_STYLENAME);
    }

    /**
     * Sets the data to be displayed in the pie chart. If a level has no value,
     * it is discarded
     * 
     * @param raportLevels
     */
    public void setPieChartData(List<ReportLevel> raportLevels) {

        DataSeries ds = new DataSeries();
        for (ReportLevel rl : raportLevels) {
            if (!rl.hasValue()) {
                continue;
            }
            ds.add(new DataSeriesItem(rl.getName(), rl.getValue()));
        }
        pieChart.getConfiguration().addSeries(ds);

    }

    /**
     * Adds date-value pairs and a name for the trend. Call reCreateChart() to
     * clear any added data to the chart
     * 
     * @param name
     * @param chartData
     */
    public void addHistoricalData(String name, List<DateAndValue> chartData) {
        DataSeries series = new DataSeries();
        series.setName(name);

        for (DateAndValue dAv : chartData) {
            final DataSeriesItem item = new DataSeriesItem(dAv.date, dAv.value);
            series.add(item);
        }
        historyChart.getConfiguration().addSeries(series);
    }

    public void reCreateCharts() {
        initializeAllCharts();
    }

    private void initializeAllCharts() {
        createHistoryChart();
        createPieChart();
    }

    /**
     * Selects a charts. If null provided, the Pie chart is used as default
     * 
     * @param chart
     */
    private void selectChart(ChartTypes chartType) {
        if (currentChart != null) {
            chartContainer.removeComponent(currentChart);
        }

        if (chartType == ChartTypes.PIE) {
            currentChart = pieChart;

        } else if (chartType == ChartTypes.HISTORY) {
            currentChart = historyChart;

        }

        currentChart.setSizeFull();

        chartContainer.addComponent(currentChart, 0, 0);

    }

    private void createPieChart() {
        pieChart = new Chart();
        Configuration configuration = new Configuration();
        configuration.getChart().setType(ChartType.PIE);
        PlotOptionsPie plotOptions = new PlotOptionsPie();
        plotOptions.setAnimation(true);

        configuration.getLegend().setEnabled(true);
        configuration.setTitle(new Title(""));

        pieChart.setConfiguration(configuration);

    }

    private void createHistoryChart() {

        historyChart = new Chart();
        Configuration configuration = new Configuration();
        configuration.getChart().setType(ChartType.SPLINE);
        configuration.getChart().setZoomType(ZoomType.XY);

        configuration.getTitle().setText("");
        configuration.disableCredits();

        configuration.getxAxis().setType(AxisType.DATETIME);
        configuration.getyAxis().setType(AxisType.LINEAR);
        configuration.getyAxis().setTitle("");
        configuration.getyAxis().setMin(0);

        PlotOptionsSpline plotOptions = new PlotOptionsSpline();

        configuration.setPlotOptions(plotOptions);
        Marker marker = new Marker();
        marker.setRadius(2.5d);
        plotOptions.setMarker(marker);

        configuration.getLegend().setEnabled(true);

        configuration.getChart().setSpacingLeft(0);

        historyChart.setConfiguration(configuration);

    }

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.YYYY");
        return sdf.format(date);
    }

    private Label createLeftSpacedLabel(String text, int level,
            boolean isHoverable) {
        String spacer = "";
        for (int i = 0; i < level; i++) {
            spacer += "&nbsp;&nbsp;&nbsp;&nbsp;";
        }
        Label l = new Label(spacer + text);
        l.addStyleName("level-" + level);
        l.setContentMode(ContentMode.HTML);
        if (isHoverable) {
            l.addStyleName("hoverable-label");
        }
        return l;

    }

    private void addGridRow(Component first, Component second, Component third) {
        grid.addComponent(first);
        grid.addComponent(second);
        grid.addComponent(third);
        grid.setComponentAlignment(third, Alignment.MIDDLE_RIGHT);

    }

    private void traverseLevelsRecursively(HierarchicalReport report,
            ReportLevel level) {
        DecimalFormat decimalFormatter = new DecimalFormat(currentNumberFormat);

        Label left = createLeftSpacedLabel(level.getName(), level.getLevel(),
                level.hasValue());
        Label right = null;
        Label deltaLabel = new Label("");
        deltaLabel.setWidth(null);
        if (level.hasValue()) {
            String rightText = decimalFormatter.format(level.getValue()) + "";
            ReportLevelDelta delta = report.getDeltaFor(level);
            if (delta != null && delta.getDelta() != null) {

                String deltaStr = decimalFormatter.format(delta.getDelta()
                        .doubleValue());

                // This is a bit ugly, but ensures that there is no delta for
                // floats by mistake

                if (delta.getDelta().doubleValue() < -0.00001D) {
                    deltaLabel.setValue("(" + deltaStr + ")");
                } else if (delta.getDelta().doubleValue() > 0.00001D) {
                    deltaLabel.setValue("(+" + deltaStr + ")");
                }
            }
            right = new Label(rightText);
            right.addStyleName("level-" + level.getLevel());
            deltaLabel.addStyleName("level-" + level.getLevel());
        } else {
            right = new Label("");
        }
        right.setWidth(null);
        addGridRow(left, right, deltaLabel);
        for (ReportLevel child : level.getChildren()) {
            traverseLevelsRecursively(report, child);
        }
    }

    public void setFullSize() {
        setSizeFull();
        mainLayout.setSizeFull();
        container.setSizeFull();
        mainLayout.setExpandRatio(container, 1);
        chartContainer.setSizeFull();
        chartContainer.setMargin(new MarginInfo(false, true, false, false));

    }
}
