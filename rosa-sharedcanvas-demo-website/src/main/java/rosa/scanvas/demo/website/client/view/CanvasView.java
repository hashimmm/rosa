package rosa.scanvas.demo.website.client.view;

import rosa.scanvas.demo.website.client.disparea.DisplayAreaView;
import rosa.scanvas.demo.website.client.presenter.CanvasPanelPresenter;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

public class CanvasView extends Composite implements CanvasPanelPresenter.Display {
    private final Panel main;
    private final Label title;
    private final DisplayAreaView area_view;

    public CanvasView() {
        this.main = new FlowPanel();
        this.title = new Label();

        this.area_view = new DisplayAreaView();

        main.add(title);
        main.add(area_view);

        main.setStylePrimaryName("PanelView");
        
        initWidget(main);
    }

    @Override
    public Label getLabel() {
        return title;
    }

    @Override
    public DisplayAreaView getDisplayAreaWidget() {
        return area_view;
    }
}
