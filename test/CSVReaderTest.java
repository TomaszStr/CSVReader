import org.junit.Test;

import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


import static org.junit.Assert.*;

public class CSVReaderTest {

    double precision = 0.000001;


    @Test
    public void parseHeader() {
        //PassengerId,Survived,Pclass,Name,Sex,Age,SibSp,Parch,Ticket,Fare,Cabin,Embarked
        try {
            CSVReader reader = new CSVReader(new FileReader("titanic-part.csv"), ",", true);
            assertEquals("PassengerId", reader.columnLabels.get(0));
            assertEquals("Age", reader.columnLabels.get(5));
            assertEquals("Embarked", reader.columnLabels.get(11));
            assertEquals(12,reader.columnLabels.size());
        }
        catch (IOException e){
            assertEquals(1,0);
        }
    }

    @Test
    public void fromString1(){
        String text = "a,b,c\n123.4,567.8,91011.12";
        CSVReader reader = new CSVReader(new StringReader(text),",",true);
        assertEquals("a",reader.columnLabels.get(0));
        assertEquals("b",reader.columnLabels.get(1));
        assertEquals("c",reader.columnLabels.get(2));
        reader.next();
        assertEquals(123.4,reader.getDouble(reader.columnLabelsToInt.get("a")).doubleValue(),precision);
        assertEquals(567.8,reader.getDouble(1).doubleValue(),precision);
        assertEquals(91011.12,reader.getDouble(2).doubleValue(),precision);
        assertFalse(reader.next());
    }
    @Test
    public void fromString2(){
        String text = """
            a,b,c
            123.4,567.8,91011.12""";
        CSVReader reader = new CSVReader(new StringReader(text),",",true);
        assertEquals("a",reader.columnLabels.get(0));
        assertEquals("b",reader.columnLabels.get(1));
        assertEquals("c",reader.columnLabels.get(2));
        reader.next();
        assertEquals(123.4,reader.getDouble(reader.columnLabelsToInt.get("a")).doubleValue(),precision);
        assertEquals(567.8,reader.getDouble(1).doubleValue(),precision);
        assertEquals(91011.12,reader.getDouble(2).doubleValue(),precision);
        assertFalse(reader.next());
    }
    @Test
    public void next() {
        try {
            CSVReader reader = new CSVReader(new FileReader("titanic-part.csv"), ",", true);
            reader.next();
            assertEquals("\"Braund, Mr. Owen Harris\"", reader.current[3]);
            reader.next();
            assertEquals("\"Cumings, Mrs. John Bradley (Florence Briggs Thayer)\"", reader.current[3]);
            while(reader.next()) {}
            //check last
            assertEquals("\"Williams, Mr. Charles Eugene\"", reader.current[3]);
        }
        catch (IOException e){
            assertEquals(1,0);
        }

    }

    @Test
    public void getColumnLabels() {
        //PassengerId,Survived,Pclass,Name,Sex,Age,SibSp,Parch,Ticket,Fare,Cabin,Embarked
        try {
            CSVReader reader = new CSVReader(new FileReader("titanic-part.csv"), ",", true);
            assertEquals("PassengerId", reader.getColumnLabels().get(0));
            assertEquals("Age", reader.getColumnLabels().get(5));
            assertEquals("Embarked", reader.getColumnLabels().get(11));
            CSVReader reader2 = new CSVReader(new FileReader("no-header.csv", Charset.forName("Cp1250")), ";", false);
            assertEquals(0,reader2.getColumnLabels().size());
        }
        catch (IOException e){
            assertEquals(1,0);
        }
    }

    @Test
    public void getRecordLength() {
        try {
            CSVReader reader = new CSVReader(new FileReader("titanic-part.csv"), ",", true);
            reader.next();
            assertEquals(12,reader.getRecordLength());
            CSVReader reader2 = new CSVReader(new FileReader("no-header.csv", Charset.forName("Cp1250")), ";", false);
            reader2.next();
            assertEquals(6,reader2.getRecordLength());
        }
        catch (IOException e){
            assertEquals(1,0);
        }
    }

    @Test
    public void isMissingIndex() {
        try {
            CSVReader reader = new CSVReader(new FileReader("missing-values.csv",Charset.forName("Cp1250")), ";", true);
            reader.next();
            assertFalse(reader.isMissing(4));
            reader.next();
            assertTrue(reader.isMissing(4));
        } catch (IOException e) {
            assertEquals(1,0);
        }
    }

    @Test
    public void tIsMissingLabel() {
        try {
            CSVReader reader = new CSVReader(new FileReader("missing-values.csv",Charset.forName("Cp1250")), ";", true);
            reader.next();
            assertFalse(reader.isMissing("population"));
            reader.next();
            assertTrue(reader.isMissing("population"));
        } catch (IOException e) {
            assertEquals(1,0);
        }
    }

    @Test
    public void getIntIndex() {
        try {
            CSVReader reader = new CSVReader(new FileReader("titanic-part.csv"), ",", true);
            reader.next();
            assertNull(reader.getInt(150));
            assertEquals(1, reader.getInt(0).intValue());
            reader.next();
            assertNull(reader.getInt(4));
            assertEquals(2,reader.getInt(0).intValue());
        }
        catch (IOException e){
            assertEquals(1,0);
        }
    }

    @Test
    public void getDoubleIndex() {
        try {
            CSVReader reader = new CSVReader(new FileReader("titanic-part.csv"), ",", true);
            reader.next();
            assertNull(reader.getDouble(150));
            assertEquals(7.25, reader.getDouble(9).doubleValue(),precision);
            reader.next();
            assertNull(reader.getDouble(4));
            assertEquals(71.2833, reader.getDouble(9).doubleValue(),precision);
        }
        catch (IOException e){
            assertEquals(1,0);
        }
    }

    @Test
    public void getLongIndex() {
        try {
            CSVReader reader = new CSVReader(new FileReader("missing-values.csv"), ";", true);
            reader.next();
            assertNull(reader.getLong(150));
            assertEquals(11670, reader.getLong(0).longValue());
            reader.next();
            assertNull(reader.getLong(2));
            assertEquals(11672, reader.getLong(0).longValue());
        }
        catch (IOException e){
            assertEquals(1,0);
        }
    }

    @Test
    public void getIndex() {
        try {
            CSVReader reader = new CSVReader(new FileReader("titanic-part.csv"), ",", true);
            reader.next();
            assertEquals("",reader.get(150));
            assertEquals("\"Braund, Mr. Owen Harris\"", reader.get(3));
            reader.next();
            assertEquals("",reader.get(150));
            assertEquals("\"Cumings, Mrs. John Bradley (Florence Briggs Thayer)\"", reader.get(3));
            while(reader.next()) {}
            //check last
            assertEquals("\"Williams, Mr. Charles Eugene\"", reader.get(3));
        }
        catch (IOException e){
            assertEquals(1,0);
        }
    }

    @Test
    public void GetIntLabel() {
        try {
            CSVReader reader = new CSVReader(new FileReader("titanic-part.csv"), ",", true);
            reader.next();
            assertNull(reader.getInt("abcdefgh"));
            assertEquals(1, reader.getInt("PassengerId").intValue());
            reader.next();
            assertNull(reader.getInt("Name"));
            assertEquals(2,reader.getInt("PassengerId").intValue());
        }
        catch (IOException e){
            assertEquals(1,0);
        }
    }

    @Test
    public void testGetDoubleLabel() {
        try {
            CSVReader reader = new CSVReader(new FileReader("titanic-part.csv"), ",", true);
            reader.next();
            assertEquals(7.25, reader.getDouble("Fare").doubleValue(),precision);
            assertNull(reader.getDouble("abcdefgh"));
            reader.next();
            assertNull(reader.getDouble("Name"));
            assertEquals(71.2833, reader.getDouble("Fare").doubleValue(),precision);
        }
        catch (IOException e){
            assertEquals(1,0);
        }
    }

    @Test
    public void testGetLongLabel() {
        try {
            CSVReader reader = new CSVReader(new FileReader("missing-values.csv"), ";", true);
            reader.next();
            assertNull(reader.getLong("abcdefgh"));
            assertEquals(11670, reader.getLong("id").longValue());
            reader.next();
            assertNull(reader.getLong("name"));
            assertEquals(11672, reader.getLong("id").longValue());
        }
        catch (IOException e){
            assertEquals(1,0);
        }

    }

    @Test
    public void testGetLabel() {
        try {
            CSVReader reader = new CSVReader(new FileReader("titanic-part.csv"), ",", true);
            reader.next();
            assertEquals("",reader.get("abcdefgh"));
            assertEquals("\"Braund, Mr. Owen Harris\"", reader.get("Name"));
            reader.next();
            assertEquals("\"Cumings, Mrs. John Bradley (Florence Briggs Thayer)\"", reader.get("Name"));
            while(reader.next()) {}
            //check last
            assertEquals("\"Williams, Mr. Charles Eugene\"", reader.get("Name"));
        }
        catch (IOException e){
            assertEquals(1,0);
        }
    }

    @Test
    public void getTime() {
        try {
            CSVReader reader = new CSVReader("datetime.csv", ";", true);
            reader.next();
            assertEquals(LocalTime.parse("12:55"), reader.getTime(1,"HH:mm"));
            assertNull(reader.getTime(10,""));
            reader.next();
            assertEquals(LocalTime.parse("23:13"), reader.getTime(1,"mm:HH"));
            assertNull(reader.getTime(10,""));
            reader.next();
            assertEquals(LocalTime.parse("23:44"), reader.getTime(1,"HH-mm"));
            assertNull(reader.getTime(10,""));
        }
        catch (IOException e){
            assertEquals(1,0);
        }
    }

    @Test
    public void testGetTime() {
        try {
            CSVReader reader = new CSVReader("datetime.csv", ";", true);
            reader.next();
            assertEquals(LocalTime.parse("12:55"), reader.getTime("godzina","HH:mm"));
            assertNull(reader.getTime("abcdef",""));
            reader.next();
            assertEquals(LocalTime.parse("23:13"), reader.getTime("godzina","mm:HH"));
            assertNull(reader.getTime("abcdef",""));
            reader.next();
            assertEquals(LocalTime.parse("23:44"), reader.getTime("godzina","HH-mm"));
            assertNull(reader.getTime("abcdef",""));
        }
        catch (IOException e){
            assertEquals(1,0);
        }
    }

    @Test
    public void getDate() {
        try {
            CSVReader reader = new CSVReader("datetime.csv", ";", true);
            reader.next();
            assertEquals(LocalDate.parse("01.01.2022",DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    reader.getDate(0,"dd.MM.yyyy"));
            assertNull(reader.getTime(10,""));
            reader.next();
            assertEquals(LocalDate.parse("17.12.2023",DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    reader.getDate(0,"dd.MM.yyyy"));
            assertNull(reader.getTime(10,""));
            reader.next();
            assertEquals(LocalDate.parse("2044-01-19",DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    reader.getDate(0,"yyyy-MM-dd"));
            assertNull(reader.getTime(10,""));
        }
        catch (IOException e){
            assertEquals(1,0);
        }
    }

    @Test
    public void testGetDate() {
        try {
            CSVReader reader = new CSVReader("datetime.csv", ";", true);
            reader.next();
            assertEquals("data",reader.columnLabels.get(0));// bug związany z kodowaniem pliku -> utf BOM
            assertEquals(LocalDate.parse("01.01.2022",DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    reader.getDate("data","dd.MM.yyyy"));
            assertNull(reader.getDate("abcdef",""));
            reader.next();
            assertEquals(LocalDate.parse("17.12.2023",DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    reader.getDate("data","dd.MM.yyyy"));
            assertNull(reader.getDate("abcdef",""));
            reader.next();
            assertEquals(LocalDate.parse("2044-01-19",DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    reader.getDate("data","yyyy-MM-dd"));
            assertNull(reader.getDate("abcdef",""));
        }
        catch (IOException e){
            assertEquals(1,0);
        }
    }

    @Test
    public void getDateTime() {
        try{
            CSVReader reader = new CSVReader("datetime.csv", ";", true);
            reader.next();
            assertEquals(LocalDateTime.parse("01.01.2024 11:34",DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                    reader.getDateTime(2,"dd.MM.yyyy HH:mm"));
            assertNull(reader.getDateTime(10,""));
            reader.next();
            assertEquals(LocalDateTime.parse("08.04.1986 12:30",DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                    reader.getDateTime(2,"dd.MM.yyyy HH:mm"));
            assertNull(reader.getDateTime(10,""));
            reader.next();
            assertEquals(LocalDateTime.parse("08.04.1986 12:30",DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                    reader.getDateTime(2,"HH:mm dd.MM.yyyy"));
            assertNull(reader.getDateTime(10,""));

        }
        catch (IOException e){
        assertEquals(1,0);
        }
    }

    @Test
    public void testGetDateTime() {
        try{
            CSVReader reader = new CSVReader("datetime.csv", ";", true);
            reader.next();
            assertEquals(LocalDateTime.parse("01.01.2024 11:34",DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                    reader.getDateTime("datetime","dd.MM.yyyy HH:mm"));
            assertNull(reader.getDateTime("abcdef",""));
            reader.next();
            assertEquals(LocalDateTime.parse("08.04.1986 12:30",DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                    reader.getDateTime("datetime","dd.MM.yyyy HH:mm"));
            assertNull(reader.getDateTime("abcdef",""));
            reader.next();
            assertEquals(LocalDateTime.parse("08.04.1986 12:30",DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                    reader.getDateTime("datetime","HH:mm dd.MM.yyyy"));
            assertNull(reader.getDateTime("abcdef",""));

        }
        catch (IOException e){
            assertEquals(1,0);
        }
    }
}