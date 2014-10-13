package org.vaadin.com.bugfixdashboard.views;

import java.util.Date;

import org.vaadin.com.bugfixdashboard.component.MultiLevelVisualizationComponent;
import org.vaadin.com.bugfixdashboard.data.ReportSummary;
import org.vaadin.com.bugfixdashboard.data.ReportSummary.ReportType;
import org.vaadin.com.bugfixdashboard.views.ReportPresenter.ReportViewDTO;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
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
    public void showReport(final ReportViewDTO dto) {

        MultiLevelVisualizationComponent multiComponent = createReportComponent(dto);
        Button fullScreenButton = new Button();
        fullScreenButton.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        fullScreenButton.addStyleName(ValoTheme.BUTTON_TINY);
        fullScreenButton
                .setIcon(new ThemeResource("icons/fullscreen16x16.png"));
        multiComponent.addHeaderComponent(fullScreenButton);
        multiComponent.setWidth("720px");
        multiComponent.addStyleName("small-left-margin");
        componentContainer.addComponent(multiComponent);

        fullScreenButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                presenter.requestFullScreenFor(dto.type);

            }
        });

    }

    private MultiLevelVisualizationComponent createReportComponent(
            ReportViewDTO reportViewDTO) {
        MultiLevelVisualizationComponent multiComponent = new MultiLevelVisualizationComponent();

        multiComponent.setReportData(reportViewDTO.currentReportToShow,
                reportViewDTO.type.getNumberFormat());

        for (String key : reportViewDTO.historyData.keySet()) {
            multiComponent.addHistoricalData(key,
                    reportViewDTO.historyData.get(key));
        }

        multiComponent.setPieChartData(reportViewDTO.pieChartData);
        return multiComponent;

    }

    @Override
    public void init() {
        presenter.init();

    }

    @Override
    public void showEmptyReport(ReportType type) {
        MultiLevelVisualizationComponent multiComponent = new MultiLevelVisualizationComponent();
        multiComponent.showAsEmptyReport("No Report for '" + type.getName()
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

    @Override
    public void showFullScreen(ReportViewDTO reportViewDTO) {
        MultiLevelVisualizationComponent multiComponent = createReportComponent(reportViewDTO);
        multiComponent.setFullSize();
        Window w = new Window();
        w.setContent(multiComponent);
        w.setSizeFull();
        w.setModal(false);
        w.setResizable(true);
        w.setDraggable(true);
        w.setWidth("90%");

        UI.getCurrent().addWindow(w);

    }
}
