package pepse.util;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;

/**
 * A class for generating an HTML table from a data map and writing it to a file.
 */
public class HtmlTableGenerator {

  /**
   * Reads an HTML table from a file and returns the data as a map.
   * Each key in the map represents a row in the table, and the corresponding value is a list
   * of the cell values for that row.
   * @param fileName The name of the file to read from
   * @return The data from the HTML table as a map
   */
  public static Map<String, ArrayList<String>> readTable(String fileName) {
    Map<String, ArrayList<String>> data = new HashMap<>();
    try {
      File inputFile = new File(fileName);
      BufferedReader reader = new BufferedReader(new FileReader(inputFile));
      String line;
      boolean inTable = false;
      while ((line = reader.readLine()) != null) {
        if (line.contains("<table>")) {
          inTable = true;
          continue;
        }
        if (line.contains("</table>")) {
          break;
        }
        if (inTable) {
          Matcher matcher = Pattern.compile("<td>(.*?)</td><td>(.*?)</td>").matcher(line);
          if (matcher.find()) {
            String name = matcher.group(1);
            String id = matcher.group(2);
            if (!data.containsKey(name)) {
              data.put(name, new ArrayList<>());
            }
            data.get(name).add(id);
          }
        }
      }
      reader.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return data;
  }
  /**
   * Generates an HTML table from a data map and writes it to a file.
   * @param data The data to include in the table, as a map. Each key in the map represents a row
   *             in the table, and the corresponding value is a list of the cell values for that row.
   * @param fileName The name of the file to write the HTML table to
   */
  public static void generateTable(Map<String, ArrayList<String>> data, String fileName) {
    try {
      File outputFile = new File(fileName);
      FileWriter fileWriter = new FileWriter(outputFile);
      BufferedWriter writer = new BufferedWriter(fileWriter);

      writer.write("<html><body><table>");
      writer.newLine();
      writer.write("<tr><th>SEED</th><th>MESSAGE</th></tr>");
      writer.newLine();

      for (Map.Entry<String, ArrayList<String>> entry : data.entrySet()) {
        String name = entry.getKey();
        ArrayList<String> ids = entry.getValue();
        for (String id : ids) {
          writer.write("<tr><td>" + name + "</td><td>" + id + "</td></tr>");
          writer.newLine();
        }
      }

      writer.write("</table></body></html>");
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
