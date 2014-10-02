package org.vaadin.com.bugfixdashboard.views;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.vaadin.com.bugfixdashboard.component.MultiLevelVisualizationComponent;
import org.vaadin.com.bugfixdashboard.data.HierarchicalReport;
import org.vaadin.com.bugfixdashboard.data.ReportDay.DateAndValue;
import org.vaadin.com.bugfixdashboard.data.ReportLevel;
import org.vaadin.com.bugfixdashboard.data.ReportSummary;
import org.vaadin.com.bugfixdashboard.data.ReportSummary.ReportType;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public class ReportViewImpl extends VerticalLayout implements ReportView {

    private ReportPresenter presenter;

    private HorizontalLayout footer;

    private CssLayout componentContainer;

    private DateField from;

    private DateField to;

    private ValueChangeListener dateSpanChangeListener;

    public ReportViewImpl(ReportSummary reportSummary, int visibleDaySpan) {
        presenter = new ReportPresenter(this, reportSummary, visibleDaySpan);
        addStyleName("view");
        addStyleName("report-view");
        footer = new HorizontalLayout();
        footer.setWidth("100%");
        footer.addComponent(from = new DateField("From", reportSummary
                .getSearchStartDate()));
        footer.addComponent(to = new DateField("To", reportSummary
                .getMostRecentReport().getDate()));
        from.addStyleName(ValoTheme.DATEFIELD_TINY);
        to.addStyleName(ValoTheme.DATEFIELD_TINY);
        footer.setExpandRatio(to, 1);
        footer.setSpacing(true);
        footer.setMargin(new MarginInfo(false, false, false, true));
        footer.setComponentAlignment(from, Alignment.MIDDLE_LEFT);
        footer.setComponentAlignment(to, Alignment.MIDDLE_LEFT);

        componentContainer = new CssLayout();
        Panel p = new Panel(componentContainer);
        p.setSizeFull();

        addComponent(p);

        addComponent(footer);

        setExpandRatio(p, 1);

        dateSpanChangeListener = new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                presenter.dateSpanChanged(from.getValue(), to.getValue());
            }
        };
        from.addValueChangeListener(dateSpanChangeListener);
        to.addValueChangeListener(dateSpanChangeListener);

    }

    @Override
    public void showReport(ReportType type,
            Map<String, List<DateAndValue>> historyData,
            HierarchicalReport currentReportToShow,
            List<ReportLevel> pieChartData) {

        MultiLevelVisualizationComponent multiComponent = new MultiLevelVisualizationComponent();

        multiComponent.setReportData(currentReportToShow);

        for (String key : historyData.keySet()) {
            multiComponent.addHistoricalData(key, historyData.get(key));
        }

        // Pie chart

        multiComponent.setPieChartData(pieChartData);

        multiComponent.setWidth("700px");
        componentContainer.addComponent(multiComponent);

    }

    @Override
    public void init() {
        presenter.init();

    }

    @Override
    public void showEmptyReport(ReportType type) {
        MultiLevelVisualizationComponent multiComponent = new MultiLevelVisualizationComponent();
        multiComponent.showAsEmptyReport("No Report for '" + type.realName()
                + "'");
        multiComponent.setWidth("650px");
        componentContainer.addComponent(multiComponent);
    }

    @Override
    public void updateDateSpanSilently(Date start, Date end) {
        from.removeValueChangeListener(dateSpanChangeListener);
        to.removeValueChangeListener(dateSpanChangeListener);

        from.setValue(start);
        to.setValue(end);

        from.addValueChangeListener(dateSpanChangeListener);
        to.addValueChangeListener(dateSpanChangeListener);

    }

    @Override
    public void clearCurrentReports() {
        componentContainer.removeAllComponents();

    }
}
