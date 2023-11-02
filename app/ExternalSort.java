package app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExternalSort {
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

    long maxMemory = Runtime.getRuntime().maxMemory()/7;

    try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
      List<String> lines = new ArrayList<>();
      long currentMemory = 0;
      String line;

      while ((line = reader.readLine()) != null) {
        long lineMemory = line.getBytes().length;

        if (currentMemory + lineMemory > maxMemory) {
          Collections.sort(lines);
          tempFiles.add(createTempFile(lines));
          lines.clear();
          currentMemory = 0;
        }

        lines.add(line);
        currentMemory += lineMemory;
      }

      if (!lines.isEmpty()) {
        Collections.sort(lines);
        tempFiles.add(createTempFile(lines));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    mergeSortedFiles(tempFiles, outputFile);
  }

  public static String createTempFile(List<String> block) throws IOException {
    File tempFile = File.createTempFile("temp", ".txt");
    try (FileWriter writer = new FileWriter(tempFile)) {
      for (String line : block) {
        writer.write(line);
        writer.write(System.lineSeparator());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    tempFile.deleteOnExit();
    return tempFile.getAbsolutePath();
  }

  public static void mergeSortedFiles(List<String> fileNames, String outputFile) throws IOException {
    List<BufferedReader> readers = new ArrayList<>();
    List<String> lines = new ArrayList<>();

    for (String fileName : fileNames) {
      readers.add(new BufferedReader(new FileReader(fileName)));
    }

    for (BufferedReader reader : readers) {
      String line = reader.readLine();
      lines.add(line);
    }

    BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

    while (!lines.isEmpty()) {
      String minLine = Collections.min(lines);

      writer.write(minLine);
      writer.newLine();

      int fileIndex = lines.indexOf(minLine);

      BufferedReader reader = readers.get(fileIndex);
      String newLine = reader.readLine();

      if (newLine != null) {
        lines.set(fileIndex, newLine);
      } else {
        readers.get(fileIndex).close();
        readers.remove(fileIndex);
        lines.remove(fileIndex);
      }
    }

    writer.close();
  }

}
