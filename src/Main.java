import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Locale;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void test1() {
        try {
            CSVReader reader = new CSVReader(new FileReader("titanic-part.csv"), ",", true);
            while (reader.next()) {
                int id = reader.getInt("PassengerId");
                String name = reader.get("Name");
                Double fare = reader.getDouble("Fare");
                System.out.printf(Locale.US, "%d %s %f\n", id, name, fare);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void test2() {
        try {
            CSVReader reader = new CSVReader("titanic-part.csv", ",", true);
            while (reader.next()) {
                Integer id = reader.getInt(0);
                String name = reader.get(3);
                Double fare = reader.getDouble(9);
                System.out.printf(Locale.US, "%d %s %f\n", id, name, fare);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void test3() {
        try {
            CSVReader reader = new CSVReader(new FileReader("with-header.csv", Charset.forName("Cp1250")), ";", true);
            while (reader.next()) {
                Integer id = reader.getInt(0);
                String name = reader.get(1);
                String surname = reader.get(2);
                System.out.printf("%d %s %s\n", id, name, surname);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void test4() {
        try {
            CSVReader reader = new CSVReader(new FileReader("missing-values.csv",Charset.forName("Cp1250")), ";", true);
            while (reader.next()) {
                Integer id = reader.getInt(0);
                String name = reader.get(2);
                Integer population = reader.getInt(4);
                System.out.printf("%d %s %d\n", id, name, population);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void admintest(){
        try {
            CSVReader reader = new CSVReader(new FileReader("admin-units.csv"), ",", true);
            int limit =10;
            System.out.println(reader.columnLabels.toString());
            while(reader.next() && limit > 0){
                AdminUnit au = new AdminUnit(reader);
                Integer id = reader.getInt(0);
                String name = reader.get(2);
                Integer population = reader.getInt(4);
                System.out.printf("%d %s %d\n", id, name, population);
                System.out.printf("%s %f\n", au.name, au.population);
                limit--;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void adminlisttest(){
        AdminUnitList list = new AdminUnitList();
        list.read("admin-units.csv");
        //list.list(System.out,100,100);
        int cnt = 0;
        for(var e: list.units) {
            //if(e.area==0 || e.population==0 || e.density==0)
            //    System.out.println(e.toString());
            if(e.adminLevel == 4) {
                cnt++;
                System.out.println(e.toString());
            }
        }
        System.out.println(cnt);
    }

    public static void distancetest(){
        AdminUnitList list = new AdminUnitList();
        list.read("admin-units.csv");
        AdminUnit tbg = new AdminUnit();
        AdminUnit stw = new AdminUnit();
        for(var e: list.units) {
            if(e.name.equals("powiat tarnobrzeski"))
                tbg = e;
            if(e.name.equals("powiat stalowowolski"))
                stw = e;
            if(!tbg.isMissing() && !stw.isMissing())
                break;
        }
        System.out.println(tbg.toString());
        System.out.println(stw.toString());
        //https://www.geometrymapper.com/
        System.out.println(tbg.getWKT());
        System.out.println(stw.getWKT());

        System.out.println("Dystans: " + tbg.bbox.distanceTo(stw.bbox));

    }

    public static void neighbourstest(){
        AdminUnitList list = new AdminUnitList();
        list.read("admin-units.csv");
        AdminUnit tbg = new AdminUnit();
        for(var e: list.units) {
            if(e.name.equals("powiat tarnobrzeski")){
                tbg = e;
                break;
            }
        }
        System.out.println(tbg.toString());
        //https://www.geometrymapper.com/
        System.out.println(tbg.getWKT());

        double t1 = System.nanoTime()/1e6;
        AdminUnitList hlp = list.getNeighbors(tbg,15);
        double t2 = System.nanoTime()/1e6;
        System.out.printf(Locale.US,"\nREGULAR:\nt2-t1=%f\n\n",t2-t1);
        System.out.println(hlp.units.size());


        for(var e: hlp.units)
            System.out.println(e.toString());
    }

    public static void optimizedneighbourstest(){
        AdminUnitList list = new AdminUnitList();
        list.read("admin-units.csv");
        AdminUnit tbg = new AdminUnit();
        for(var e: list.units) {
            if(e.name.equals("powiat tarnobrzeski")){
                tbg = e;
                break;
            }
        }
        System.out.println(tbg.toString());
        //https://www.geometrymapper.com/
        //System.out.println(tbg.getWKT());

        double t1 = System.nanoTime()/1e6;
        AdminUnitList hlp = list.OptimizedGetNeighbors(tbg,15);
        double t2 = System.nanoTime()/1e6;
        System.out.printf(Locale.US,"\nOPTIMIZED:\nt2-t1=%f\n\n",t2-t1);
        System.out.println(hlp.units.size());
        for(var e: hlp.units)
            System.out.println(e.toString());
    }

    public static void Voivodoptimizedneighbourstest(){
        AdminUnitList list = new AdminUnitList();
        list.read("admin-units.csv");
        AdminUnit tbg = new AdminUnit();
        for(var e: list.units) {
            if(e.name.equals("powiat tarnobrzeski")){
                tbg = e;
                break;
            }
        }
        System.out.println(tbg.toString());
        //https://www.geometrymapper.com/
        //System.out.println(tbg.getWKT());
        //System.out.println(tbg.bbox.distanceTo(stw.bbox)); // sprawdzam distance

        ArrayList<AdminUnit> voivod = new ArrayList<>();
        //trzeba znalezc wszystkie wojewodztwa
        for(var e: list.units)
            if(e.adminLevel == 4)
                voivod.add(e);

        double t1 = System.nanoTime()/1e6;
        AdminUnitList hlp = list.VoivodOptimizedGetNeighbors(tbg,15, voivod);
        double t2 = System.nanoTime()/1e6;
        System.out.printf(Locale.US,"\nOPTIMIZED WITH READY VOIVODESHIP LIST:\nt2-t1=%f\n\n",t2-t1);
        System.out.println(hlp.units.size());
        for(var e: hlp.units)
            System.out.println(e.toString());
    }

    public static void OptimizedDifferentVoivod(){
        AdminUnitList list = new AdminUnitList();
        list.read("admin-units.csv");
        AdminUnit stw = new AdminUnit();
        for(var e: list.units) {
            if(e.name.equals("powiat stalowowolski")){
                stw = e;
                break;
            }
        }
        System.out.println(stw.toString());
        //https://www.geometrymapper.com/
        //System.out.println(tbg.getWKT());

        ArrayList<AdminUnit> voivod = new ArrayList<>();
        //trzeba znalezc wszystkie wojewodztwa
        for(var e: list.units)
            if(e.adminLevel == 4)
                voivod.add(e);

        double t1 = System.nanoTime()/1e6;
        AdminUnitList hlp = list.VoivodOptimizedGetNeighbors(stw,15, voivod);
        double t2 = System.nanoTime()/1e6;
        System.out.printf(Locale.US,"\nOPTIMIZED WITH READY VOIVODESHIP LIST:\nt2-t1=%f\n\n",t2-t1);
        System.out.println(hlp.units.size());
        for(var e: hlp.units) {
            System.out.println(e.parent.name);
            System.out.println(e.toString());
        }
    }

    public static void AdminUnitQueryTest(){
        AdminUnitList list = new AdminUnitList();
        list.read("admin-units.csv");
        AdminUnitQuery query = new AdminUnitQuery()
                .selectFrom(list)
                .where(a->a.area>1000)
                .or(a->a.name.startsWith("Sz"))
                .sort((a,b)->Double.compare(a.area,b.area))
                .limit(100);
        query.execute().list(System.out);
    }

    public static void MostPopulous10() {
        AdminUnitList list = new AdminUnitList();
        list.read("admin-units.csv");
        AdminUnitQuery query = new AdminUnitQuery()
                .selectFrom(list)
                .where(a->a.population>100000)
                .sort((a,b)->Double.compare(b.population,a.population)).limit(10);
        //query.offset(query.execute().units.size()-10);
        System.out.println("10 najludniejszych jednostek administracyjnych");
        query.execute().list(System.out);
    }

    public static void BiggestPowiat10(){
        AdminUnitList list = new AdminUnitList();
        list.read("admin-units.csv");
        AdminUnitQuery query = new AdminUnitQuery()
                .selectFrom(list)
                .where(a->a.adminLevel == 6)
                .and(a -> a.area > 1000)
                .sort((a,b)->Double.compare(a.area,b.area));
        query.offset(query.execute().units.size()-10);
        System.out.println("10 najludniejszych jednostek administracyjnych");
        query.execute().list(System.out);
    }

    public static void FurthestNorthMiejscowosc10(){
        AdminUnitList list = new AdminUnitList();
        list.read("admin-units.csv");
        AdminUnitQuery query = new AdminUnitQuery()
                .selectFrom(list)
                .where(a->a.adminLevel == 11)
                .and(a -> a.bbox.ymax > 50)
                .sort((a,b)->Double.compare(a.bbox.ymax,b.bbox.ymax));
        query.offset(query.execute().units.size()-10);
        query.execute().list(System.out);
    }

    public static void Voivodeships(){
        AdminUnitList list = new AdminUnitList();
        list.read("admin-units.csv");
        AdminUnitQuery query = new AdminUnitQuery()
                .selectFrom(list)
                .where(a->a.adminLevel == 4);
        AdminUnitList found = query.execute();
        found.list(System.out);
        double sum = 0;
        for(var e: found.units)
            sum += e.population;
        System.out.printf("Populacja Polski według dokumentu: %d",(int)sum);
    }


    public static void main(String[] args) {
        //test1();
        //test2();
        //test3();
        //test4();
        //admintest();
        //adminlisttest();
        //neighbourstest();

        //distancetest();

        //FOR TIME AND EFFECT COMPARISON
       /* neighbourstest();

        optimizedneighbourstest();

        Voivodoptimizedneighbourstest();*/

        //OptimizedDifferentVoivod();

        //AdminUnitQueryTest();

        MostPopulous10();

        //BiggestPowiat10();

        //Voivodeships();

        //FurthestNorthMiejscowosc10();

    }
}