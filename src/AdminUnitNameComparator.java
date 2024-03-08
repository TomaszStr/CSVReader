import java.util.Comparator;

public class AdminUnitNameComparator implements Comparator<AdminUnit> {
    @Override
    public int compare(AdminUnit a1, AdminUnit a2) {
        return a1.name.compareTo(a2.name);
    }
}
