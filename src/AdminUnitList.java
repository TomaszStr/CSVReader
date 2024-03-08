import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.function.Predicate;

public class AdminUnitList {
    List<AdminUnit> units = new ArrayList<>();


    /**
     * Czyta rekordy pliku i dodaje do listy
     *
     * @param filename nazwa pliku
     */
    public void read(String filename) {
        try {
            CSVReader reader = new CSVReader(new FileReader("admin-units.csv"), ",", true);
            Map<Long, AdminUnit> LAdm = new HashMap<>();
            Map<AdminUnit, Long> AdmL = new HashMap<>();
            Map<AdminUnit, Long> child2parent = new HashMap<>();
            Map<Long, List<AdminUnit>> parentid2child = new HashMap<>();
            while (reader.next()) {
                //zmienic na lambdy ?
                AdminUnit au = new AdminUnit();

                child2parent.put(au, reader.getLong(1));//child -> parentId
                LAdm.put(reader.getLong(0), au);//AdmUnitId -> object
                AdmL.put(au, reader.getLong(0));//object -> AdmUnitId

                if (!parentid2child.containsKey(reader.getLong(1))) // pierwsze wystapienie
                    parentid2child.put(reader.getLong(1), new ArrayList<>());

                parentid2child.get(reader.getLong(1)).add(au);
                if (!reader.isMissing(2))
                    au.name = reader.get(2);
                if (!reader.isMissing(3))
                    au.adminLevel = reader.getInt(3);
                if (!reader.isMissing(4))
                    au.population = reader.getDouble(4);
                if (!reader.isMissing(5))
                    au.area = reader.getDouble(5);
                if (!reader.isMissing(6))
                    au.density = reader.getDouble(6);

                //BBOX
                for (int i = 7; i < 16; i += 2)
                    if (!reader.isMissing(i) && !reader.isMissing(i + 1))
                        au.bbox.addPoint(reader.getDouble(i), reader.getDouble(i + 1));

                //zapis do listy
                this.units.add(au);
            }
            //spisywanie relacji parent -> child
            for (var e : units) {
                e.parent = LAdm.get(child2parent.get(e));
                if (parentid2child.containsKey(AdmL.get(e)))
                    e.children = parentid2child.get(AdmL.get(e));
            }
            //fix missing population and density
            fixMissingValues();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Wypisuje zawartość korzystając z AdminUnit.toString()
     *
     * @param out
     */
    void list(PrintStream out) {
        //out.println();//header?
        for (var e : units) {
            out.println(e.toString());
        }
    }

    /**
     * Wypisuje co najwyżej limit elementów począwszy od elementu o indeksie offset
     *
     * @param out    - strumień wyjsciowy
     * @param offset - od którego elementu rozpocząć wypisywanie
     * @param limit  - ile (maksymalnie) elementów wypisać
     */
    void list(PrintStream out, int offset, int limit) {
        for (int i = 0; i < limit; i++) {
            if (i + offset >= units.size())
                break;
            out.print(units.get(offset + i).toString());
        }
    }

    /**
     * Zwraca nową listę zawierającą te obiekty AdminUnit, których nazwa pasuje do wzorca
     *
     * @param pattern - wzorzec dla nazwy
     * @param regex   - jeśli regex=true, użyj finkcji String matches(); jeśli false użyj funkcji contains()
     * @return podzbiór elementów, których nazwy spełniają kryterium wyboru
     */
    AdminUnitList selectByName(String pattern, boolean regex) {
        AdminUnitList ret = new AdminUnitList();
        // przeiteruj po zawartości units
        // jeżeli nazwa jednostki pasuje do wzorca dodaj do ret
        if (regex) {
            for (var e : units) {
                if (e.name.matches(pattern))
                    ret.units.add(e); // copy?
            }
        } else {
            for (var e : units) {
                if (e.name.contains(pattern))
                    ret.units.add(e); // copy?
            }
        }
        return ret;
    }

    private void fixMissingValues() {
        for (var e : units)
            e.fixMissingValues();
    }

    /**
     * Zwraca listę jednostek sąsiadujących z jendostką unit na tym samym poziomie hierarchii admin_level.
     * Czyli sąsiadami wojweództw są województwa, powiatów - powiaty, gmin - gminy, miejscowości - inne miejscowości
     *
     * @param unit        - jednostka, której sąsiedzi mają być wyznaczeni
     * @param maxdistance - parametr stosowany wyłącznie dla miejscowości, maksymalny promień odległości od środka unit,
     *                    w którym mają sie znaleźć punkty środkowe BoundingBox sąsiadów
     * @return lista wypełniona sąsiadami
     */
    AdminUnitList getNeighbors(AdminUnit unit, double maxdistance) {
        if (unit.bbox.isEmpty())
            throw new RuntimeException("pusty");
        AdminUnitList neighbours = new AdminUnitList();

        if (unit.adminLevel < 11)
            for (var e : units) {
                if (!e.bbox.isEmpty() && e.adminLevel == unit.adminLevel && e != unit && unit.bbox.intersects(e.bbox))
                    neighbours.units.add(e);
            }
        else
            for (var e : units) {
                if (!e.bbox.isEmpty() && e.adminLevel == unit.adminLevel && e != unit && unit.bbox.distanceTo(e.bbox) < maxdistance)
                    neighbours.units.add(e);
            }
        return neighbours;
    }

    AdminUnitList OptimizedGetNeighbors(AdminUnit unit, double maxdistance) {
        if (unit.bbox.isEmpty())
            throw new RuntimeException("pusty");
        AdminUnitList neighbours = new AdminUnitList();
        //we need voivodeships
        ArrayList<AdminUnit> voivod = new ArrayList<>();
        //trzeba znalezc wszystkie wojewodztwa
        for (var e : units)
            if (e.adminLevel == 4)
                voivod.add(e);
        for (var e : voivod)
            if (e.bbox.intersects(unit.bbox)) {
                neighbours.units.addAll(Rsearch(unit, e, maxdistance));
            }

        return neighbours;
    }

    AdminUnitList VoivodOptimizedGetNeighbors(AdminUnit unit, double maxdistance, ArrayList<AdminUnit> voivod) {
        if (unit.bbox.isEmpty())
            throw new RuntimeException("pusty");
        AdminUnitList neighbours = new AdminUnitList();
        for (var e : voivod)
            if (e.bbox.intersects(unit.bbox)) {
                neighbours.units.addAll(Rsearch(unit, e, maxdistance));
            }

        return neighbours;
    }

    // DOPYTAC NA ZAJECIACH
    ArrayList<AdminUnit> Rsearch(AdminUnit unit, AdminUnit parent, double maxdistance) {
        ArrayList<AdminUnit> result = new ArrayList<>();
        //warunek dla miejscowosci
        if (parent.adminLevel == 11) {
            if (unit.bbox.distanceTo(parent.bbox) < maxdistance)
                result.add(parent);
            return result;
        }

        //jesli nie parent nie graniczy, nie ma sensu szukac w dzieciach
        if (!parent.bbox.intersects(unit.bbox))
            return result;

        //ten sam poziom i graniczy
        if (parent.adminLevel == unit.adminLevel && parent != unit) {
            result.add(parent);
        } //przeszukujemy dzieci
        else if (parent.adminLevel < unit.adminLevel && !Objects.isNull(parent.children)) {
            for (var e : parent.children)
                result.addAll(Rsearch(unit, e, maxdistance));
        }
        return result;
    }


    /**
     * Sortuje daną listę jednostek (in place = w miejscu)
     *
     * @return this
     */
    AdminUnitList sortInplaceByName() {
        units.sort(new AdminUnitNameComparator());
        return this;
    }

    /**
     * Sortuje daną listę jednostek (in place = w miejscu)
     *
     * @return this
     */
    AdminUnitList sortInplaceByArea() {
        units.sort(new Comparator() {
            @Override
            public int compare(Object a1, Object a2) {
                return Double.compare(((AdminUnit) a1).area, ((AdminUnit) a2).area);
            }
        });
        return this;
    }

    /**
     * Sortuje daną listę jednostek (in place = w miejscu)
     *
     * @return this
     */
    AdminUnitList sortInplaceByPopulation() {
        units.sort((a1, a2) -> Double.compare(a1.population, a2.population));
        return this;
    }

    AdminUnitList sortInplace(Comparator<AdminUnit> cmp) {
        units.sort(cmp);
        return this;
    }

    AdminUnitList sort(Comparator<AdminUnit> cmp) {
        // Tworzy wyjściową listę
        // Kopiuje wszystkie jednostki
        // woła sortInPlace
        AdminUnitList result = new AdminUnitList();
        result.units = units;
        result.units.sort(cmp);
        return result;
    }

    /**
     * @param pred referencja do interfejsu Predicate
     * @return nową listę, na której pozostawiono tylko te jednostki,
     * dla których metoda test() zwraca true
     */
    AdminUnitList filter(Predicate<AdminUnit> pred) {
        AdminUnitList result = new AdminUnitList();
        for(var e: units)
            if(pred.test(e))
                result.units.add(e);
        return result;
    }

    /**
     * Zwraca co najwyżej limit elementów spełniających pred
     * @param pred - predykat
     * @param limit - maksymalna liczba elementów
     * @return nową listę
     */
    AdminUnitList filter(Predicate<AdminUnit> pred, int limit){
        AdminUnitList result = new AdminUnitList();
        int cnt=0;
        for(var e: units) {
            if(pred.test(e)){
                result.units.add(e);
                cnt++;
            }
            //System.out.printf("%d %d\n",cnt,limit);
            if(cnt >= limit)
                break;
        }
        return result;
    }

    /**
     * Zwraca co najwyżej limit elementów spełniających pred począwszy od offset
     * Offest jest obliczany po przefiltrowaniu
     * @param pred - predykat
     * @param - od którego elementu
     * @param limit - maksymalna liczba elementów
     * @return nową listę
     */
    AdminUnitList filter(Predicate<AdminUnit> pred, int offset, int limit){
        /*AdminUnitList result = filter(pred,limit);
        for(int i = 0; i < offset && !result.units.isEmpty(); i++)
            result.units.remove(0);*/

        AdminUnitList result = new AdminUnitList();
        int cntValid=0;
        int cntAdded=0;
        for(int i = 0; i < units.size() && cntAdded<limit;i++)
            if(pred.test(units.get(i))){
                cntValid++;
                if(offset <= cntValid){
                    result.units.add(units.get(i));
                    cntAdded++;}
            }
        return result;

    }
}