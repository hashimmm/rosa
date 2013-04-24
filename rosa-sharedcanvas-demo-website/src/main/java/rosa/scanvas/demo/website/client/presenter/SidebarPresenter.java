package rosa.scanvas.demo.website.client.presenter;

import java.util.Iterator;
import java.util.List;

import rosa.scanvas.demo.website.client.PanelData;
import rosa.scanvas.demo.website.client.PanelState;
import rosa.scanvas.demo.website.client.event.AnnotationSelectionEvent;
import rosa.scanvas.demo.website.client.event.PanelRequestEvent;
import rosa.scanvas.demo.website.client.event.PanelRequestEvent.PanelAction;
import rosa.scanvas.demo.website.client.widgets.AnnotationListWidget;
import rosa.scanvas.demo.website.client.widgets.ManifestListWidget;
import rosa.scanvas.model.client.Annotation;
import rosa.scanvas.model.client.AnnotationList;
import rosa.scanvas.model.client.Manifest;
import rosa.scanvas.model.client.ManifestCollection;
import rosa.scanvas.model.client.Sequence;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class SidebarPresenter implements IsWidget {
    public interface Display extends IsWidget {
        HasClickHandlers getAddPanelButton();

        HasClickHandlers getRemovePanelButton();

        HasEnabled getRemovePanelEnabler();

        ListBox getPanelList();

        AnnotationListWidget getAnnoListWidget();

        ManifestListWidget getMetaListWidget();
    }

    private final Display display;
    private final HandlerManager eventBus;

    public SidebarPresenter(Display display, HandlerManager eventBus) {
        this.display = display;
        this.eventBus = eventBus;

        bind();
        display.getRemovePanelEnabler().setEnabled(false);

    }

    private void bind() {
        // event handlers for the Panel List list and buttons
        display.getAddPanelButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                doAddPanel();
            }
        });

        display.getRemovePanelButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                doRemovePanel();
            }
        });

        display.getPanelList().addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                doPanelListChange();
            }
        });

        // Metadata list hide/show checkboxes
        display.getMetaListWidget().getCollectionCheckBox()
                .addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                    public void onValueChange(ValueChangeEvent<Boolean> event) {
                        doCollectionHide(event.getValue());
                    }
                });

        display.getMetaListWidget().getManifestCheckBox()
                .addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                    public void onValueChange(ValueChangeEvent<Boolean> event) {
                        doManifestHide(event.getValue());
                    }
                });

        display.getMetaListWidget().getSequenceCheckBox()
                .addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                    public void onValueChange(ValueChangeEvent<Boolean> event) {
                        doSequenceHide(event.getValue());
                    }
                });
    }

    public void setSize(int width, int height) {
        display.asWidget().setSize(width + "px", height + "px");
    }

    /**
     * Removes item at specified index from the list. Panels in list are renamed
     * 
     * @param index
     *            int of item to remove. Selects the first item in list.
     */
    private void removePanel(int index) {
        display.getPanelList().removeItem(index);

        for (int i = 0; i < display.getPanelList().getItemCount(); i++) {
            display.getPanelList().setItemText(i, "Panel " + (i + 1));
        }

        display.getPanelList().setSelectedIndex(0);
    }

    /**
     * Adds a new item to the list and sets it to be selected. UNUSED?
     * 
     * @param item
     */
    private void addPanel(String item) {
        display.getPanelList().addItem(item);

        int selectedPanel = display.getPanelList().getItemCount() - 1;
        display.getPanelList().setSelectedIndex(selectedPanel);
    }

    // -------------- DOM Event Actions --------------

    private void doAddPanel() {
        if (!display.getRemovePanelEnabler().isEnabled()) {
            display.getRemovePanelEnabler().setEnabled(true);
        }

        PanelRequestEvent event = new PanelRequestEvent(
                PanelRequestEvent.PanelAction.ADD, new PanelState());
        eventBus.fireEvent(event);
    }

    private void doRemovePanel() {
        int selectedPanel = display.getPanelList().getSelectedIndex();
        eventBus.fireEvent(new PanelRequestEvent(PanelAction.REMOVE,
                selectedPanel));

        // if there is now only 1 item in the list, disable Remove button
        if (display.getPanelList().getItemCount() == 1) {
            display.getRemovePanelEnabler().setEnabled(false);
        }
    }

    private void doPanelListChange() {
        int selectedPanel = display.getPanelList().getSelectedIndex();
        eventBus.fireEvent(new PanelRequestEvent(PanelAction.CHANGE,
                selectedPanel));
    }

    // Metadata List hide/show labels
    private int metaChecks = 0;

    private void doCollectionHide(Boolean enabled) {
        if (enabled) {
            display.getMetaListWidget()
                    .getMainPanel()
                    .insert(display.getMetaListWidget().getCollectionPanel(), 1);
            metaChecks--;
        } else {
            display.getMetaListWidget().getMainPanel()
                    .remove(display.getMetaListWidget().getCollectionPanel());
            metaChecks++;
        }
    }

    private void doManifestHide(Boolean enabled) {
        if (enabled) {
            display.getMetaListWidget()
                    .getMainPanel()
                    .insert(display.getMetaListWidget().getManifestPanel(),
                            5 - metaChecks);
            metaChecks--;
        } else {
            display.getMetaListWidget().getMainPanel()
                    .remove(display.getMetaListWidget().getManifestPanel());
            metaChecks++;
        }
    }

    private void doSequenceHide(Boolean enabled) {
        if (enabled) {
            display.getMetaListWidget()
                    .getMainPanel()
                    .insert(display.getMetaListWidget().getSequencePanel(),
                            7 - metaChecks);
        } else {
            display.getMetaListWidget().getMainPanel()
                    .remove(display.getMetaListWidget().getSequencePanel());
        }
    }

    // -------------- End DOM Event Actions --------------

    /**
     * Place data from selected panel in appropriate place in sidebar
     */
    public void setData(PanelData data) {

        // Window.alert(String.valueOf(!this.data.getCollection().uri().equals(data.getCollection().uri())));

        /*
         * if (this.data.getCollection() == null ||
         * !this.data.getCollection().uri().equals(data.getCollection().uri()))
         * { this.data.setCollection(data.getCollection()); } if
         * (this.data.getManifest() == null ||
         * !this.data.getManifest().uri().equals(data.getManifest().uri())) {
         * this.data.setManifest(data.getManifest());
         * this.data.getAnnotationLists().addAll(data.getAnnotationLists());
         * this.data.getImageAnnotations().addAll(data.getImageAnnotations()); }
         * if (this.data.getSequence() == null ||
         * !this.data.getSequence().uri().equals(data.getSequence().uri())) {
         * this.data.setSequence(data.getSequence()); } if
         * (this.data.getCanvas() == null ||
         * !this.data.getCanvas().uri().equals(data.getCanvas().uri())) {
         * this.data.setCanvas(data.getCanvas()); }
         * 
         * setMetadata(); setAnnotations();
         */
        setMetadata(data);
        setAnnotations(data);
    }

    private void setMetadata(PanelData data) {
        // TODO display a list of sequences for the 'sequence picker'
        display.getMetaListWidget().clearLabels();
        display.getAnnoListWidget().clearLists();

        ManifestCollection collection = data.getManifestCollection();
        if (collection != null) {
            display.getMetaListWidget().newCollectionLabel(collection.label());
            display.getMetaListWidget().newCollectionLabel(
                    "Number of items: " + collection.manifests().size());
        }

        Manifest manifest = data.getManifest();
        if (manifest != null) {
            display.getMetaListWidget().newManifestLabel(manifest.label());
            display.getMetaListWidget().newManifestLabel(
                    "Agent: " + manifest.agent());
            display.getMetaListWidget().newManifestLabel(
                    "Location: " + manifest.location());
            display.getMetaListWidget().newManifestLabel(
                    "Date: " + manifest.date());
            display.getMetaListWidget().newManifestLabel(
                    "Description: " + manifest.description());
            display.getMetaListWidget().newManifestLabel("");
            display.getMetaListWidget().newManifestLabel(
                    "Rights: " + manifest.rights());
        }

        Sequence sequence = data.getSequence();
        if (sequence != null) {
            display.getMetaListWidget().newSequenceLabel(sequence.label());
            display.getMetaListWidget().newSequenceLabel(sequence.uri());
            display.getMetaListWidget().newSequenceLabel(
                    "Number of canvases: " + sequence.size());
        }
    }

    private void setAnnotations(PanelData data) {
        List<AnnotationList> list = data.getAnnotationLists();
        if (list.size() > 0) {
            // iterate through the list of annotation lists
            Iterator<AnnotationList> listIterator = list.iterator();
            int i = 0, j = 0;
            while (listIterator.hasNext()) {
                // for each list, put each annotation in appropriate area
                AnnotationList annotationList = listIterator.next();
                Iterator<Annotation> annotationIterator = annotationList
                        .iterator();
                while (annotationIterator.hasNext()) {
                    Annotation annotation = annotationIterator.next();
                    if (annotation.body().isImage()) {
                        // TODO: ensure that image conformsTo() IIIF
                        // add to image annotation listbox
                        display.getAnnoListWidget().getImageAnnoList()
                                .setWidget(i, 1, new Label(annotation.label()));
                        display.getAnnoListWidget().getImageAnnoList()
                                .setWidget(i, 0, new CheckBox());
                        bindImageRow(i, annotation);
                        i++;
                    } else if (annotation.body().isText()) {
                        // add to text annotation listbox
                        display.getAnnoListWidget()
                                .getNontargetedTextAnnoList()
                                .setWidget(j, 1, new Label(annotation.label()));
                        display.getAnnoListWidget()
                                .getNontargetedTextAnnoList()
                                .setWidget(j, 0, new CheckBox());
                        bindNontargetedTextRow(j, annotation);
                        j++;
                    }
                }
            }
        }
    }

    /**
     * Add handlers to the image annotations list to listen to value changes of
     * checkboxes
     */
    private void bindImageRow(int row, final Annotation annotation) {
        ((CheckBox) display.getAnnoListWidget().getImageAnnoList()
                .getWidget(row, 0))
                .addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                    public void onValueChange(ValueChangeEvent<Boolean> event) {
                        // fire event on eventBus indicating an annotation
                        // should be
                        // shown or hidden
                        boolean value = event.getValue();
                        int panel = display.getPanelList().getSelectedIndex();

                        // data.getVisibleAnnotations().add(annotation);
                        eventBus.fireEvent(new AnnotationSelectionEvent(
                                annotation, value, panel));
                    }
                });
    }

    /**
     * Add handlers to the nontargeted text annotations list to listen to value
     * changes of checkboxes
     */
    private void bindNontargetedTextRow(int row, final Annotation annotation) {
        ((CheckBox) display.getAnnoListWidget().getNontargetedTextAnnoList()
                .getWidget(row, 0))
                .addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                    public void onValueChange(ValueChangeEvent<Boolean> event) {
                        // fire event on eventBus indicating an annotation
                        // should be
                        // shown or hidden
                        boolean value = event.getValue();
                        int panel = display.getPanelList().getSelectedIndex();

                        // data.getVisibleAnnotations().add(annotation);
                        eventBus.fireEvent(new AnnotationSelectionEvent(
                                annotation, value, panel));
                    }
                });
    }

    @Override
    public Widget asWidget() {
        return display.asWidget();
    }
}
