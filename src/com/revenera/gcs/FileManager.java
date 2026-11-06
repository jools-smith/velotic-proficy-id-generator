package com.revenera.gcs;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class FileManager {

  public String logFilename() {
    return LocalDate.now() + ".revenera.log";
  }

  public String transactionFilename() {
    return LocalDate.now() + ".transactions.zip";
  }

  public List<Path> getInactiveFiles() throws IOException {
    final Path root = ExecutionContext.getLogPath().toRealPath();

    try (final Stream<Path> stream = Files.list(root)) {
      return stream.filter(Files::isRegularFile)
          /// kludgey mut simple
          .filter(path -> !path.getFileName().toString().equalsIgnoreCase(logFilename()))
          /// kludgey mut simple
          .filter(path -> !path.getFileName().toString().equalsIgnoreCase(transactionFilename()))
          .map(Path::toAbsolutePath)
          .collect(Collectors.toList());
    }
  }

  public void addFileToZipArchive(final Path file) throws IOException {

    final Path zipFilename = Paths.get(
        ExecutionContext.getLogPath().toRealPath().toString(), transactionFilename());

    final URI uri = URI.create("jar:" + zipFilename.toUri());

    final Map<String, String> env = new HashMap<String, String>() {
      {
        this.put("create", "true");
      }
    };
    // create ZIP if it doesn't exist

    try (final FileSystem zipFs = FileSystems.newFileSystem(uri, env)) {
      Path destination = zipFs.getPath("/" + file.getFileName());

      Files.copy(
          file,
          destination,
          StandardCopyOption.REPLACE_EXISTING
      );

      Files.delete(file);
    }
  }

  public void compressFile(final Path source) throws IOException {
    final Path zip = source.resolveSibling(source.getFileName() + ".zip");

    try (final ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(zip))) {
      out.putNextEntry(new ZipEntry(source.getFileName().toString()));

      Files.copy(source, out);

      Files.delete(source);
    }
  }

  /// requires execution context
  public void serializeFile(final String filename, final List<String> content) throws IOException {
    if (ExecutionContext.getLoggingManager().willEcho()) {

      final Path root = ExecutionContext.getLogPath().toRealPath();

      if (Files.exists(root)) {
        FileUtils.writeLines(
            Paths.get(root.toString(), filename).toAbsolutePath().toFile().getAbsoluteFile(),
            content,
            true);
      }
    }
  }

  /// requires execution context
  public void serializeLogFile(final List<String> content) throws IOException {
    serializeFile(logFilename(), content);
  }
}
