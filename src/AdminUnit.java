import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AdminUnit {
    String name;
    int adminLevel;
    double population;
    double area;
    double density;
    AdminUnit parent = null;
    List<AdminUnit> children;
    BoundingBox bbox = new BoundingBox();

    //zły konstruktor spinający klasy
    AdminUnit(CSVReader reader){
        if(!reader.isMissing(2))
            name = reader.get(2);
        if(!reader.isMissing(3))
            adminLevel = reader.getInt(3);
        if(!reader.isMissing(4))
            population = reader.getDouble(4);
        if(!reader.isMissing(5))
            area = reader.getDouble(5);
        if(!reader.isMissing(6))
            density = reader.getDouble(6);
        //bbox = new BoundingBox();
    }
    AdminUnit(String name, int adminLevel, double population, double area, double density){
        this.name = name;
        this.adminLevel = adminLevel;
        this.population = population;
        this.area = area;
        this.density = density;
    }

    AdminUnit(){}

    public String toString(){
        StringBuilder b = new StringBuilder();
        b.append("name: " + name);
        b.append(", adminLevel: " + adminLevel);
        b.append(", population: " + population);
        b.append(", area: " + area +" ");
        b.append(bbox.toString());
        return b.toString();
    }


    public boolean isMissing(){
        if(population == 0)
            return true;
        return false;
    }


    //fixes missing population and density
    //if area is missing new population will be 0
    public void fixMissingValues() {
        if(population == 0) {
            if (parent != null) {
                if (parent.isMissing())
                    parent.fixMissingValues();
                this.population = parent.density * this.area;
                this.density = parent.density;
            }
        }
    }

    //for visualization
    public String getWKT(){
        StringBuilder b = new StringBuilder();
        if(isMissing())
            throw new RuntimeException("pusty");
        b.append("LINESTRING(");
        b.append(bbox.xmax+" "+ bbox.ymax+", ");
        b.append(bbox.xmax+" "+ bbox.ymin+", ");
        b.append(bbox.xmin+" "+ bbox.ymin+", ");
        b.append(bbox.xmin+" "+ bbox.ymax+", ");
        b.append(bbox.xmax+" "+ bbox.ymax);

        b.append(")");
        return b.toString();
    }
}

