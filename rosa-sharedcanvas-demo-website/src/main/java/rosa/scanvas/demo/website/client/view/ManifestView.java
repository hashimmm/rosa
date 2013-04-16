package rosa.scanvas.demo.website.client.view;

import java.util.List;

import rosa.scanvas.model.client.Reference;
import rosa.scanvas.model.client.Sequence;
import rosa.scanvas.demo.website.client.presenter.ManifestPresenter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class ManifestView extends Composite implements ManifestPresenter.Display {
    // TODO Put in css
	private final String LIST_WIDTH = "30em";
	
	private FlexTable manifestList = new FlexTable();
	private Label viewLabel = new Label();
	
	public ManifestView() {
		DockPanel mainPanel = new DockPanel();
		ScrollPanel scrollPanel = new ScrollPanel();
		initWidget(mainPanel);
		
		mainPanel.add(scrollPanel, DockPanel.CENTER);
		mainPanel.add(viewLabel, DockPanel.NORTH);
		scrollPanel.add(manifestList);
		scrollPanel.setSize(LIST_WIDTH, "10em");
	}
	
	public int getSelectedRow(ClickEvent event) { 
		int selectedRow = -1;
		HTMLTable.Cell cell = manifestList.getCellForEvent(event);
		
		if (cell != null) {
			selectedRow = cell.getRowIndex();
		}
		
		return selectedRow; 
	}

	public void setData(List<Reference<Sequence>> seq) {
		manifestList.removeAllRows();
		
		for (int i=0; i<seq.size(); i++) {
			DecoratorPanel panel = new DecoratorPanel();
			panel.setWidth("100%");
//			panel.add(new Label(seq.get(i).label()));			labels for Rose data blank??
			panel.add(new Label(seq.get(i).uri()));
			
			manifestList.setWidget(i, 0, panel);
		}
	}
	
	public HasText getViewLabel() { return viewLabel; }
	public HasClickHandlers getList() { return manifestList; }
	public Widget asWidget() { return this; }
}