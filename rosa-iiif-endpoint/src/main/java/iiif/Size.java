package iiif;

public class Size {
    public enum Type {
        FULL, EXACT_WIDTH, EXACT_HEIGHT, PERCENTAGE, EXACT, BEST_FIT;
    }

    private int width, height;
    private double percentage;
    private Type type;

    public Size() {
        this.type = Type.FULL;
    }

    public Size(Type type, int width, int height) {
        this.width = width;
        this.height = height;
        this.type = type;
    }

    public Size(Type type) {
        this.type = type;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + height;
        long temp;
        temp = Double.doubleToLongBits(percentage);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + width;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Size other = (Size) obj;
        if (height != other.height)
            return false;
        if (Double.doubleToLongBits(percentage) != Double
                .doubleToLongBits(other.percentage))
            return false;
        if (type != other.type)
            return false;
        if (width != other.width)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Scale [width=" + width + ", height=" + height + ", percentage="
                + percentage + ", type=" + type + "]";
    }
}
