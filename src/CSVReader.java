import java.io.*;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVReader {
    BufferedReader reader;
    String delimiter;
    boolean hasHeader;

    /** OGÓLNY KONSTRUKTOR
     * @param reader - klasa do odczytywania
     * @param delimiter - separator pól
     * @param hasHeader - czy plik ma wiersz nagłówkowy
     */
    public CSVReader(Reader reader, String delimiter,boolean hasHeader){
        this.reader = new BufferedReader(reader);
        this.delimiter = delimiter;
        this.hasHeader = hasHeader;
        if(hasHeader)
            parseHeader();
    }
    /**
     *
     * @param filename - nazwa pliku
     * @param delimiter - separator pól
     * @param hasHeader - czy plik ma wiersz nagłówkowy
     */

    public CSVReader(String filename,String delimiter,boolean hasHeader) throws IOException
    {
        this(new FileReader(filename),delimiter,hasHeader);
    }

    public CSVReader(String filename,String delimiter) throws IOException {
        this(new FileReader(filename),delimiter,true);
    }

    public CSVReader(String filename) throws IOException{
        this(new FileReader(filename),";",true);
    }


    // nazwy kolumn w takiej kolejności, jak w pliku
    List<String> columnLabels = new ArrayList<>();
    // odwzorowanie: nazwa kolumny -> numer kolumny
    Map<String,Integer> columnLabelsToInt = new HashMap<>();

    void parseHeader() {
        // wczytaj wiersz
        try {
            String line = reader.readLine();

            if (line == null) {
                return;
            }
            // podziel na pola
            String[] header = line.split(delimiter);
            // przetwarzaj dane w wierszu
            for (int i = 0; i < header.length; i++) {
                // dodaj nazwy kolumn do columnLabels i numery do columnLabelsToInt
                columnLabels.add(header[i]);
                columnLabelsToInt.put(header[i], i);
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    String[]current;
    boolean next(){
        // czyta następny wiersz, dzieli na elementy i przypisuje do current
        try {
            String line = reader.readLine();
            if(line == null)
                return false;
            else {
                String d = String.format("%s(?=([^\"]*\"[^\"]*\")*[^\"]*$)", delimiter);
                //String[] splitted = s.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                //current = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                current = line.split(d);
                return true;
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }


    //getters

    public List<String> getColumnLabels(){
        return columnLabels;
    }

    public int getRecordLength(){
        return current.length;
    }

    boolean isMissing(int columnIndex){
        if(columnIndex < current.length && columnIndex >= 0
                && !current[columnIndex].equals(""))
            return false;
        else
            return true;
    }

    boolean isMissing(String columnLabel){
        if(columnLabelsToInt.get(columnLabel) >= 0 && columnLabelsToInt.get(columnLabel) < current.length
                && !current[columnLabelsToInt.get(columnLabel)].equals("") && columnLabels.contains(columnLabel))
            return false;
        else
            return true;
    }

    public Integer getInt(String columnLabel) {
        if(columnLabels.contains(columnLabel)) {
            try {
                return Integer.valueOf(current[columnLabelsToInt.get(columnLabel)]);
            }
            catch (NumberFormatException e) {
                //System.out.println("Invalid integer input");
            }
        }
        return null;
    }

    public Double getDouble(String columnLabel) {
        if(columnLabels.contains(columnLabel)) {
            try {
                return Double.valueOf(current[columnLabelsToInt.get(columnLabel)]);
            }
            catch (NumberFormatException e) {
                //System.out.println("Invalid double input");
            }
        }
        return null;
    }

    Long getLong(String columnLabel){
        if(columnLabels.contains(columnLabel)) {
            try {
                return Long.valueOf(current[columnLabelsToInt.get(columnLabel)]);
            }
            catch (NumberFormatException e) {
                //System.out.println("Invalid double input");
            }
        }
        return null;
    }

    public String get(String columnLabel) {
        if(columnLabels.contains(columnLabel)) {
            return current[columnLabelsToInt.get(columnLabel)];
        }
        return "";
    }

    public Integer getInt(int columnIndex){
        if(columnIndex < current.length) {
            try {
                return Integer.valueOf(current[columnIndex]);
            }
            catch (NumberFormatException e) {
                //System.out.println("Invalid integer input");
            }
        }
        return null;
    }

    public Double getDouble(int columnIndex){
        if(columnIndex < current.length) {
            try {
                return Double.valueOf(current[columnIndex]);
            }
            catch (NumberFormatException e) {
                //System.out.println("Invalid double input");
            }
        }
        return null;
    }

    Long getLong(int columnIndex){
        if(columnIndex < current.length) {
            try {
                return Long.valueOf(current[columnIndex]);
            }
            catch (NumberFormatException e) {
                //System.out.println("Invalid long input");
            }
        }
        return null;
    }
    public String get(int columnIndex){
        if(columnIndex < current.length) {
            return current[columnIndex];
        }
        return "";
    }

    public LocalTime getTime(int columnIndex,String format){
        if(columnIndex < current.length) {
            return LocalTime.parse(current[columnIndex], DateTimeFormatter.ofPattern(format));
        }
        return null;
    }

    public LocalTime getTime(String columnLabel,String format){
        if(columnLabels.contains(columnLabel)) {
            return LocalTime.parse(current[columnLabelsToInt.get(columnLabel)],DateTimeFormatter.ofPattern(format));
        }
        return null;
    }

    public LocalDate getDate(int columnIndex,String format){
        if(columnIndex < current.length) {
            return LocalDate.parse(current[columnIndex],DateTimeFormatter.ofPattern(format));
        }
        return null;
    }

    public LocalDate getDate(String columnLabel,String format){
        if(columnLabels.contains(columnLabel)) {
            return LocalDate.parse(current[columnLabelsToInt.get(columnLabel)],DateTimeFormatter.ofPattern(format));
        }
        return null;
    }

    public LocalDateTime getDateTime(int columnIndex,String format){
        if(columnIndex < current.length) {
            return LocalDateTime.parse(current[columnIndex],DateTimeFormatter.ofPattern(format));
        }
        return null;
    }

    public LocalDateTime getDateTime(String columnLabel,String format){
        if(columnLabels.contains(columnLabel)) {
            return LocalDateTime.parse(current[columnLabelsToInt.get(columnLabel)],DateTimeFormatter.ofPattern(format));
        }
        return null;
    }
}
