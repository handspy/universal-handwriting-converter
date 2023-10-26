package pt.up.hs.uhc;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import pt.up.hs.uhc.models.Page;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static pt.up.hs.uhc.utils.FilenameUtils.getFilenameWithoutExtension;

public class Cli {

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("i", "input", true, "Specify input file or directory");
        options.addOption("o", "output", true, "Specify output directory");

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            String input = cmd.getOptionValue("input");
            String output = cmd.getOptionValue("output");

            if (input == null) {
                input = "";
            }

            if (output == null) {
                output = "";
            }

            Path inputPath = Paths.get(input);
            if (!Files.exists(inputPath)) {
                throw new IOException(String.format("File or directory '%s' does not exist.", input));
            }

            Path outputPath = Paths.get(output);
            if (!Files.exists(outputPath)) {
                throw new IOException(String.format("File or directory '%s' does not exist.", output));
            }
            if (!Files.isDirectory(outputPath)) {
                throw new IOException(String.format("Output ('%s') must be a directory.", output));
            }

            if (Files.isDirectory(inputPath)) {
                doConversionDirectory(inputPath, outputPath);
            } else {
                doConversionFile(inputPath, outputPath);
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void doConversionFile(Path inputPath, Path outputDirPath) {
        try {
            convertFile(inputPath.getParent(), outputDirPath, inputPath);
            System.out.printf("Processed '%s' successfully\n", inputPath);
        } catch (Exception e) {
            System.err.println(String.format("Error processing '%s': ", inputPath) + e.getMessage());
        }
    }

    private static void doConversionDirectory(Path inputDirPath, Path outputDirPath) throws IOException {
        try (Stream<Path> paths = Files.walk(inputDirPath)) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        try {
                            convertFile(inputDirPath, outputDirPath, file);
                            System.out.printf("Processed '%s' successfully\n", file.toString());
                        } catch (Exception e) {
                            System.err.println(String.format("Error processing '%s': ", file.toString()) + e.getMessage());
                        }
                    });
        }
    }

    private static void convertFile(Path inputDirPath, Path outputDirPath, Path filePath) throws Exception {
        Path relInputPath = inputDirPath.relativize(filePath);
        String outputFilename = getFilenameWithoutExtension(relInputPath.toString()) + ".csv";
        Path outputPath = outputDirPath.resolve(relInputPath).getParent().resolve(outputFilename);

        Files.createDirectories(outputPath.getParent());

        Page page = new UniversalHandwritingConverter().file(filePath.toFile()).getPage();

        List<String[]> dataLines = new ArrayList<>();
        page.getStrokes().forEach(stroke ->
                        stroke.getDots().forEach(dot ->
                                dataLines.add(new String[]{
                                        String.format("%.3f", dot.getX()),
                                        String.format("%.3f", dot.getY()),
                                        String.valueOf(dot.getTimestamp())
                                })
                        )
        );

        String csv = dataLines.stream().map(Cli::convertToCSV).collect(Collectors.joining("\n"));

        FileWriter fileWriter = new FileWriter(outputPath.toFile());
        fileWriter.write(convertToCSV(new String[]{"x", "y", "timestamp"}) + "\n");
        fileWriter.write(csv);
        fileWriter.close();
    }

    public static String convertToCSV(String[] data) {
        return Stream.of(data)
                .map(Cli::escapeSpecialCharacters)
                .collect(Collectors.joining(","));
    }

    public static String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }
}
