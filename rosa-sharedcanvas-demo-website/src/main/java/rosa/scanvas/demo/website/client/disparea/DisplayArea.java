package rosa.scanvas.demo.website.client.disparea;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Represents a rectangular area where elements can be drawn. Each element has a
 * unique id. Elements do not change size or position. A viewport in the area
 * represents what the user sees. The top left of the area has coordinates 0,0.
 * 
 * The area can be zoomed at discrete levels. Changing the zoom maintains the
 * same relative dimensions and positions of the elements. Changing the zoom
 * preserves the current size and center of the viewport, but its base
 * dimensions change.
 * 
 * There are three different dimensions being tracked by this DisplayArea. The
 * dimensions of the area itself (canvas), called the "base" dimensions. The
 * dimensions of the viewport in relation to the pixel dimensions of the
 * browser, called the "viewport" dimensions. And the dimensions of the viewport 
 * in relation to the display area itself (the base).
 * 
 */
public class DisplayArea implements Iterable<DisplayElement> {
    private int base_width, base_height;
    private int vp_width, vp_height;

    // DisplayElement id -> DisplayElement
    private final Map<String, DisplayElement> content_map;

    // Ordered by stacking
    private List<DisplayElement> content_list;

    private ZoomLevels zoom_levels;
    // Index representing how far the DisplayArea zoomed
    private int zoom_level;
    // The scaling factor of the current zoom
    private double zoom;

    private int vp_base_center_x;
    private int vp_base_center_y;

    public DisplayArea(int width, int height, int vp_width, int vp_height) {
        this.content_map = new HashMap<String, DisplayElement>();
        this.content_list = new ArrayList<DisplayElement>();
        this.base_width = width;
        this.base_height = height;
        this.vp_base_center_x = base_width / 2;
        this.vp_base_center_y = base_height / 2;
        this.zoom_level = 0;
        
        resizeViewport(vp_width, vp_height);
    }

    /**
     * Changes the size of the viewport and recalculates the zoom levels.
     * 
     * @param vp_width
     * @param vp_height
     */
    public void resizeViewport(int vp_width, int vp_height) {
        this.vp_width = vp_width;
        this.vp_height = vp_height;
        
        this.zoom_levels = ZoomLevels.guess(base_width, base_height, vp_width,
                vp_height);

        // Preserve zoom level if possible
        
        if (zoom_level < zoom_levels.size()) {
            setZoomLevel(zoom_level);
        } else {
            setZoomLevel(zoom_levels.size() - 1);
        }
        
    }

    /**
     * Set the base width and height of this display area.
     * 
     * @param base_width
     * @param base_height
     */
    public void setBaseSize(int base_width, int base_height) {
    	this.base_width = base_width;
    	this.base_height = base_height;
    	
    	this.vp_base_center_x = base_width / 2;
    	this.vp_base_center_y = base_height / 2;
    }
    
    public int viewportBaseCenterX() {
        return vp_base_center_x;
    }

    public int viewportBaseLeft() {
        return (int) (vp_base_center_x - ((vp_width / zoom) / 2));
    }

    public int viewportBaseTop() {
        return (int) (vp_base_center_y - ((vp_height / zoom) / 2));
    }

    public int viewportBaseCenterY() {
        return vp_base_center_y;
    }

    public int viewportWidth() {
        return vp_width;
    }

    public int viewportHeight() {
        return vp_height;
    }

    public int viewportBaseWidth() {
        return (int) (vp_width / zoom);
    }

    public int viewportBaseHeight() {
        return (int) (vp_height / zoom);
    }

    /**
     * Set the center of the viewport in canvas space
     */
    public void setViewportBaseCenter(int x, int y) {
        this.vp_base_center_x = x;
        this.vp_base_center_y = y;
    }

    /**
     * Set the center of the viewport in browser space
     */
    public void setViewportCenter(int x, int y) {
        this.vp_base_center_x = (int) (x / zoom);
        this.vp_base_center_y = (int) (y / zoom);
    }

    /**
     * Pan the viewport by a distance at the current zoom.
     */
    public void panViewport(int dx, int dy) {
        this.vp_base_center_x += (dx / zoom);
        this.vp_base_center_y += (dy / zoom);
    }

    public int baseWidth() {
        return base_width;
    }

    public int baseHeight() {
        return base_height;
    }

    public int width() {
        return (int) (base_width * zoom);
    }

    public int height() {
        return (int) (base_height * zoom);
    }

    public void setContent(List<DisplayElement> els) {
        content_map.clear();
        content_list = els;

        for (DisplayElement el : content_list) {
            content_map.put(el.id(), el);
        }

        stackingOrderChanged();
    }

    /**
     * Must be called if the element stacking order changes.
     */
    public void stackingOrderChanged() {
        Collections.sort(content_list, new Comparator<DisplayElement>() {
            public int compare(DisplayElement e1, DisplayElement e2) {
                return e2.stackingOrder() - e1.stackingOrder();
            }
        });
    }

    public void remove(DisplayElement el) {
        content_map.remove(el.id());
        content_list.remove(el);
    }

    public DisplayElement get(String id) {
        return content_map.get(id);
    }

    @Override
    public Iterator<DisplayElement> iterator() {
        return content_list.iterator();
    }

    /**
     * Returns index of current zoom level
     */
    public int zoomLevel() {
        return zoom_level;
    }

    /**
     * Set current zoom level to a desired index
     */
    public void setZoomLevel(int level) {
        zoom_level = level;
        this.zoom = zoom_levels.zoom(zoom_level);
    }

    /**
     * Returns the total number of zoom levels
     */
    public int numZoomLevels() {
        return zoom_levels.size();
    }

    /**
     * Returns the scaling factor of the current zoom level
     */
    public double zoom() {
        return zoom;
    }

    /**
     * Returns all elements covered by or intersecting the viewport in stacking
     * order from bottom to top.
     * 
     * @return elements
     */
    public List<DisplayElement> findInViewport() {
        List<DisplayElement> result = new ArrayList<DisplayElement>();

        int vp_base_width = viewportBaseWidth();
        int vp_base_height = viewportBaseHeight();

        int vp_base_left = vp_base_center_x - (vp_base_width / 2);
        int vp_base_top = vp_base_center_y - (vp_base_height / 2);

        for (DisplayElement el : content_list) {
            if (el.inRectangle(vp_base_left, vp_base_top, vp_base_width,
                    vp_base_height)) {
                result.add(el);
            }
        }

        return result;
    }

    /**
     * Return all elements containing x,y (base coordinate system) in stacking
     * order from bottom to top.
     * 
     * @param x
     * @param y
     * @return
     */
    public List<DisplayElement> findContaining(int x, int y) {
        List<DisplayElement> result = new ArrayList<DisplayElement>();

        for (DisplayElement el : content_list) {
            if (el.contains(x, y)) {
                result.add(el);
            }
        }

        return result;
    }

    public int viewportTop() {
        return (int) ((vp_base_center_y * zoom) - (vp_height / 2));
    }

    public int viewportLeft() {
        return (int) ((vp_base_center_x * zoom) - (vp_width / 2));
    }

    public int viewportCenterX() {
        return (int) (vp_base_center_x * zoom);
    }

    public int viewportCenterY() {
        return (int) (vp_base_center_y * zoom);
    }

    public boolean atMaxZoom() {
        return zoom_level == zoom_levels.size() - 1;
    }

    public int maxZoomLevel() {
        return zoom_levels.size() - 1;
    }

    public boolean zoomIn() {
        if (zoom_level < zoom_levels.size() - 1) {
            setZoomLevel(++zoom_level);
            return true;
        }

        return false;
    }

    public boolean zoomOut() {
        if (zoom_level > 0) {
            setZoomLevel(--zoom_level);
            return true;
        }

        return false;
    }

    /**
     * @param id
     * @return whether or not the area contains a DisplayElement with the given id
     */
            
    public boolean contains(String id) {
        return content_map.containsKey(id);
    }
}
