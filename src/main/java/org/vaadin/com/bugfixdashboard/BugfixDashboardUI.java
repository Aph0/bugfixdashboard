package org.vaadin.com.bugfixdashboard;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;

import org.vaadin.com.bugfixdashboard.data.ReportSummary;
import org.vaadin.com.bugfixdashboard.parser.ReportReader;
import org.vaadin.com.bugfixdashboard.views.ReportViewImpl;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("bfdtheme")
@SuppressWarnings("serial")
public class BugfixDashboardUI extends UI {

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = BugfixDashboardUI.class, widgetset = "org.vaadin.com.bugfixdashboard.AppWidgetSet")
    public static class Servlet extends VaadinServlet {
    }

    final VerticalLayout mainLayout = new VerticalLayout();

    @Override
    protected void init(VaadinRequest request) {

        setContent(mainLayout);

        mainLayout.setSizeFull();
        ReportSummary reportSummary = null;
        int visibleDaySpan = 1;
        try {
            reportSummary = ReportReader.getReportSummary(false);
        } catch (IOException e) {
            e.printStackTrace();
            Label l = new Label("Could not read the report data! "
                    + e.getMessage());
            mainLayout.addComponent(l);
            return;
        }

        try {
            visibleDaySpan = ReportReader.getReadableProperties()
                    .getHistoryRepresentationSpan();
        } catch (IOException e) {
            e.printStackTrace();
            Label l = new Label(
                    "Could not Find property 'representation_daysBackDefault' "
                            + e.getMessage());
            mainLayout.addComponent(l);
            return;
        }

        ReportViewImpl reportView = new ReportViewImpl(reportSummary,
                visibleDaySpan);
        reportView.init();
        setView(reportView);
    }

    public void setView(Component view) {
        mainLayout.removeAllComponents();
        mainLayout.addComponent(view);
        view.setSizeFull();
    }
}
