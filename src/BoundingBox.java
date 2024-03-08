public class BoundingBox {
    double xmin;
    double ymin;
    double xmax;
    double ymax;
    BoundingBox(){
        xmax = Double.NaN;
        ymax = Double.NaN;
        xmin = Double.NaN;
        ymin = Double.NaN;
    }

    public String toString(){
        StringBuilder b = new StringBuilder();
        b.append(" xmin: "+xmin);
        b.append(" xmax: "+xmax);
        b.append(" ymin: "+ymin);
        b.append(" ymax: "+ymax);
        return b.toString();
    }

    /**
     * Powiększa BB tak, aby zawierał punkt (x,y)
     * Jeżeli był wcześniej pusty - wówczas ma zawierać wyłącznie ten punkt
     * @param x - współrzędna x
     * @param y - współrzędna y
     */
    void addPoint(double x, double y){
        if(isEmpty()) {
            xmax = x;
            xmin = x;
            ymax = y;
            ymin = y;
        }
        else {
            //xmax = (x>xmax) ? x : xmax;
            xmax = Math.max(x,xmax);
            xmin = Math.min(x,xmin);
            ymax = Math.max(y,ymax);
            ymin = Math.min(y,ymin);
        }
    }

    /**
     * Sprawdza, czy BB zawiera punkt (x,y)
     * @param x
     * @param y
     * @return
     */
    boolean contains(double x, double y){
        return  x<=xmax &&
                x>=xmin &&
                y<=ymax &&
                y>=ymin;
    }

    /**
     * Sprawdza czy dany BB zawiera bb
     * @param bb
     * @return
     */
    boolean contains(BoundingBox bb){
        return xmin <= bb.xmin &&
               xmax >= bb.xmax &&
               ymax >= bb.ymax &&
               ymin <= bb.ymin;
    }

    /**
     * Sprawdza, czy dany BB przecina się z bb
     * @param bb
     * @return
     */
    boolean intersects(BoundingBox bb){
        //przez zaprzeczenie
        if(bb.xmin>xmax || bb.xmax < xmin ||
                bb.ymin > ymax || bb.ymax < ymin)
            return false;
        //sprawdzanie rogow
        /*if(contains(bb.xmax,bb.ymax) || contains(bb.xmax,bb.ymin)
           || contains(bb.xmin,bb.ymax) || contains(bb.xmin,bb.ymin)
           || bb.contains(xmax,ymax) || bb.contains(xmax,ymin)
           || bb.contains(xmin,ymax) || bb.contains(xmin,ymax))
            return true;*/
        return true;
    }
    /**
     * Powiększa rozmiary tak, aby zawierał bb oraz poprzednią wersję this
     * Jeżeli był pusty - po wykonaniu operacji ma być równy bb
     * @param bb
     * @return
     */
    BoundingBox add(BoundingBox bb){
        if(isEmpty()) {
            xmax = bb.xmax;
            xmin = bb.xmin;
            ymax = bb.ymax;
            ymin = bb.ymin;
        }
        else {
            xmax = Math.max(bb.xmax, xmax);
            xmin = Math.min(bb.xmin, xmin);
            ymax = Math.max(bb.ymax, ymax);
            ymin = Math.min(bb.ymin, ymin);
        }
        return this;
    }
    /**
     * Sprawdza czy BB jest pusty
     * @return
     */
    boolean isEmpty(){
        return Double.isNaN(xmax) ||
                Double.isNaN(ymax) ||
                Double.isNaN(xmin) ||
                Double.isNaN(ymin);
    }

    /**
     * Sprawdza czy
     * 1) typem o jest BoundingBox
     * 2) this jest równy bb
     * @return
     */
    public boolean equals(Object o){
        if(this == o)
            return true;
        if(!(o instanceof BoundingBox b))//pattern variable
            return false;
        return b.ymax == ymax && b.ymin == ymin &&
                b.xmax == xmax && b.xmin == xmin;
    }

    /**
     * Oblicza i zwraca współrzędną x środka
     * @return if !isEmpty() współrzędna x środka else wyrzuca wyjątek
     * (sam dobierz typ)
     */
    double getCenterX(){
        if(isEmpty())
            throw new RuntimeException("Pusty");
        else return (xmax+xmin)/2;
    }
    /**
     * Oblicza i zwraca współrzędną y środka
     * @return if !isEmpty() współrzędna y środka else wyrzuca wyjątek
     * (sam dobierz typ)
     */
    double getCenterY(){
        if(isEmpty())
            throw new RuntimeException("Pusty");
        else return (ymax+ymin)/2;
    }

    /**
     * Oblicza odległość pomiędzy środkami this bounding box oraz bbx
     * @param bbx prostokąt, do którego liczona jest odległość
     * @return if !isEmpty odległość, else wyrzuca wyjątek lub zwraca maksymalną możliwą wartość double
     * Ze względu na to, że są to współrzędne geograficzne, zamiast odległości użyj wzoru haversine
     * (ang. haversine formula)
     *
     * Gotowy kod można znaleźć w Internecie...
     */
    double distanceTo(BoundingBox bbx){
        if(isEmpty() || bbx.isEmpty())
            throw new RuntimeException("pusty");

        double lat1Rad = Math.toRadians(getCenterX());
        double lat2Rad = Math.toRadians(bbx.getCenterX());
        double lon1Rad = Math.toRadians(getCenterY());
        double lon2Rad = Math.toRadians(bbx.getCenterY());

        double x = (lon2Rad - lon1Rad) * Math.cos((lat1Rad + lat2Rad) / 2);
        double y = (lat2Rad - lat1Rad);
        double distance = Math.sqrt(x * x + y * y) * 6371; // wynik w km

        return distance;
    }
}