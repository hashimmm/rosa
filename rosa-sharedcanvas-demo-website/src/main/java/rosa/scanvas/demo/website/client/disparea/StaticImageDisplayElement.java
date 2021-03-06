package rosa.scanvas.demo.website.client.disparea;

/**
 * A display element representing a image. This image may or may not be
 * IIIF compliant.
 */
public class StaticImageDisplayElement extends DisplayElement {
	private final String uri;

    public StaticImageDisplayElement(String id, String uri, int x, int y, int width, int height) {
        super(id, x, y, width, height);
        this.uri = uri;
    }
    
    public String uri() {
    	return uri;
    }
}
