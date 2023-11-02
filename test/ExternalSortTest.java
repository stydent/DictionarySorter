package test;

import app.ExternalSort;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;


class ExternalSortTest {

  private File inputFile = new File("test/resources/inputFile.txt");
  private File outputFile = new File("test/resources/outputFile.txt");

  @Test
  void externalSort_check_size_and_sort() throws IOException {
    ExternalSort.externalSort(inputFile.getPath(), outputFile.getPath());
    List<String> inputLines = new ArrayList<>(Files.lines(inputFile.toPath()).toList());
    List<String> outputLines = new ArrayList<>(Files.lines(outputFile.toPath()).toList());

    assertEquals(inputLines.size(), outputLines.size());

    Collections.sort(inputLines);
    assertArrayEquals(inputLines.toArray(), outputLines.toArray());
  }

  @Test
  void createTempFile_check_lines_saves_to_file() throws IOException {
    List<String> block = new ArrayList<>(Arrays.asList("abc", "bca", "cba"));
    String tmpFile = ExternalSort.createTempFile(block);

    List<String> outputLines = new ArrayList<>(Files.lines(Path.of(tmpFile)).toList());

    assertArrayEquals(block.toArray(), outputLines.toArray());
  }

  @Test
  void mergeSortedFiles_check_files_saves_to_file() throws IOException {
    List<String> fileNames = new ArrayList<>(Arrays.asList("test/resources/path1.txt", "test/resources/path2.txt", "test/resources/path3.txt"));
    String outputFile = "test/resources/outputFile.txt";

    ExternalSort.mergeSortedFiles(fileNames, outputFile);

    List<String> outputLines = new ArrayList<>(Files.lines(Path.of(outputFile)).toList());
    List<String> inputLines = new ArrayList<>();
    fileNames.forEach(file -> {
      try {
        List<String> tmp = new ArrayList<>(Files.lines(Path.of(file)).toList());
        inputLines.addAll(tmp);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });

    assertEquals(outputLines.size(), inputLines.size());
  }
}