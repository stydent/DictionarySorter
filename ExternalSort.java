import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class ExternalSort {
  private static final int BLOCK_SIZE = 2;

  public static void main(String[] args) throws IOException {
    BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

    System.out.print("Input file dictionary:");
    String inputFile = stdin.readLine();
    System.out.print("Output file name:");
    String outputFile = stdin.readLine();

    externalSort(inputFile, outputFile);
    System.out.println(outputFile + " is ready");
  }

  public static void externalSort(String inputFile, String outputFile) throws IOException {
    List<String> tempFiles = new ArrayList<>();

    BufferedReader reader = new BufferedReader(new FileReader(inputFile));
    List<String> block = new ArrayList<>();
    String line;
    while ((line = reader.readLine()) != null) {
      block.add(line);
      if (block.size() >= BLOCK_SIZE) {
        Collections.sort(block);
        String tempFile = createTempFile(block);
        tempFiles.add(tempFile);
        block.clear();
      }
    }

    if (!block.isEmpty()) {
      Collections.sort(block);
      String tempFile = createTempFile(block);
      tempFiles.add(tempFile);
      block.clear();
    }

    mergeSortedFiles(tempFiles, outputFile);
  }

  private static String createTempFile(List<String> block) throws IOException {
    File tempFile = File.createTempFile("temp", ".txt");
    BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
    for (String line : block) {
      writer.write(line + "\n");
    }
    writer.close();
    tempFile.deleteOnExit();
    return tempFile.getAbsolutePath();
  }

  private static void mergeSortedFiles(List<String> tempFiles, String outputFile) throws IOException {
    PriorityQueue<String> pqs = new PriorityQueue<>();
    BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

    for (String file : tempFiles) {
      pqs.addAll(Files.readAllLines(new File(file).toPath()));
    }

    while (!pqs.isEmpty()) {
      writer.write(pqs.poll() + "\n");
    }

    writer.close();
  }

}
