package com.itsaky.androidide.treesitter;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Akash Yadav
 */
public class TestUtils {

  public static String readString(Path path) {
    try (final var reader = Files.newBufferedReader(path)) {
      final var sb = new StringBuilder();
      for (var line = reader.readLine(); line != null; line = reader.readLine()) {
        sb.append(line).append("\n");
      }
      return sb.toString();
    } catch (Throwable err) {
      throw new RuntimeException(err);
    }
  }
}
