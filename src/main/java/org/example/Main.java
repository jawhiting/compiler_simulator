package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        // Default values
        String folderPath = ".";  // Current directory
        int fileSize = 1024;      // 1KB
        int numSubfolders = 30;    // 3 subfolders
        int numFiles = 50;         // 5 files per subfolder

        try {
            // Override with command line arguments if provided
            if (args.length > 0) folderPath = args[0];
            if (args.length > 1) fileSize = Integer.parseInt(args[1]);
            if (args.length > 2) numSubfolders = Integer.parseInt(args[2]);
            if (args.length > 3) numFiles = Integer.parseInt(args[3]);

            // Display usage information
            System.out.println("Compiler Simulator");
            System.out.println("------------------");
            System.out.println("Using parameters:");
            System.out.println("  Folder path: " + folderPath);
            System.out.println("  File size: " + fileSize + " bytes");
            System.out.println("  Number of subfolders: " + numSubfolders);
            System.out.println("  Number of files per subfolder: " + numFiles);
            System.out.println();

            // Run the compiler simulator
            runCompilerSimulator(folderPath, fileSize, numSubfolders, numFiles);
        } catch (NumberFormatException e) {
            System.err.println("Error parsing numeric arguments: " + e.getMessage());
            System.out.println("Usage: java -jar compiler_simulator.jar [<folder_path> [<file_size_in_bytes> [<num_subfolders> [<num_files_per_subfolder>]]]]");
            System.out.println("All parameters are optional with the following defaults:");
            System.out.println("  folder_path: Current directory (.)");
            System.out.println("  file_size_in_bytes: 1024 (1KB)");
            System.out.println("  num_subfolders: 3");
            System.out.println("  num_files_per_subfolder: 5");
        } catch (Exception e) {
            System.err.println("Error running compiler simulator: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void runCompilerSimulator(String folderPath, int fileSize, int numSubfolders, int numFiles) {
        // Statistics tracking
        List<Long> createFolderTimes = new ArrayList<>();
        List<Long> createFileTimes = new ArrayList<>();
        List<Long> readFileTimes = new ArrayList<>();
        List<Long> deleteFileTimes = new ArrayList<>();
        List<Long> deleteFolderTimes = new ArrayList<>();

        try {
            // Create a unique root folder
            long start = System.nanoTime();
            String uniqueFolderName = "compiler_sim_" + UUID.randomUUID().toString();
            Path rootFolder = Paths.get(folderPath, uniqueFolderName);
            Files.createDirectories(rootFolder);
            long end = System.nanoTime();
            long createRootTime = end - start;
            createFolderTimes.add(createRootTime);
            System.out.println("Created root folder: " + rootFolder);

            // Create subfolders and files
            List<Path> allFilePaths = new ArrayList<>();
            List<Path> allFolderPaths = new ArrayList<>();
            int fileCreationCount = 0;

            for (int i = 0; i < numSubfolders; i++) {
                // Create subfolder
                start = System.nanoTime();
                Path subfolderPath = rootFolder.resolve("subfolder_" + i);
                Files.createDirectories(subfolderPath);
                end = System.nanoTime();
                createFolderTimes.add(end - start);
                allFolderPaths.add(subfolderPath);

                // Create files in subfolder
                for (int j = 0; j < numFiles; j++) {
                    start = System.nanoTime();
                    Path filePath = subfolderPath.resolve("file_" + j + ".dat");
                    createFile(fileSize, filePath);
                    end = System.nanoTime();
                    createFileTimes.add(end - start);
                    allFilePaths.add(filePath);

                    // Log progress every 100 files
                    fileCreationCount++;
                    if (fileCreationCount % 100 == 0) {
                        System.out.println("File creation progress: " + fileCreationCount + " files created");
                    }
                }
            }

            // Read all files
            int fileReadCount = 0;
            for (Path filePath : allFilePaths) {
                start = System.nanoTime();
                byte[] data = Files.readAllBytes(filePath);
                end = System.nanoTime();
                readFileTimes.add(end - start);

                // Log progress every 100 files
                fileReadCount++;
                if (fileReadCount % 100 == 0) {
                    System.out.println("File reading progress: " + fileReadCount + " files read");
                }
            }

            // Delete all files
            int fileDeletionCount = 0;
            for (Path filePath : allFilePaths) {
                start = System.nanoTime();
                Files.delete(filePath);
                end = System.nanoTime();
                deleteFileTimes.add(end - start);

                // Log progress every 100 files
                fileDeletionCount++;
                if (fileDeletionCount % 100 == 0) {
                    System.out.println("File deletion progress: " + fileDeletionCount + " files deleted");
                }
            }

            // Delete all folders
            int folderDeletionCount = 0;
            // Delete subfolders in reverse order to ensure they are empty
            for (int i = allFolderPaths.size() - 1; i >= 0; i--) {
                Path subfolderPath = allFolderPaths.get(i);
                start = System.nanoTime();
                Files.delete(subfolderPath);
                end = System.nanoTime();
                deleteFolderTimes.add(end - start);

                // Log progress every 10 folders
                folderDeletionCount++;
                if (folderDeletionCount % 10 == 0) {
                    System.out.println("Folder deletion progress: " + folderDeletionCount + " folders deleted");
                }
            }

            // Delete root folder
            start = System.nanoTime();
            Files.delete(rootFolder);
            end = System.nanoTime();
            deleteFolderTimes.add(end - start);
            System.out.println("Deleted root folder: " + rootFolder);

            // Print statistics
            System.out.println("\nOperation Statistics (in milliseconds):");
            printStats("Folder Creation", createFolderTimes);
            printStats("File Creation", createFileTimes);
            printStats("File Reading", readFileTimes);
            printStats("File Deletion", deleteFileTimes);
            printStats("Folder Deletion", deleteFolderTimes);

        } catch (IOException e) {
            System.err.println("Error during compiler simulation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void printStats(String operation, List<Long> times) {
        if (times.isEmpty()) {
            System.out.println(operation + ": No data");
            return;
        }

        double min = times.stream().min(Long::compare).orElse(0L) / 1_000_000.0;
        double max = times.stream().max(Long::compare).orElse(0L) / 1_000_000.0;
        double avg = times.stream().mapToLong(Long::valueOf).average().orElse(0.0) / 1_000_000.0;

        System.out.printf("%s: Min = %.3f ms, Avg = %.3f ms, Max = %.3f ms%n", 
                operation, min, avg, max);
    }

    // function to create a file of a certain size containing random data
    public static void createFile(int size, Path target) {
        // create a file of a certain size containing random data
        // first get a random byte array of the desired size
        byte[] data = new byte[size];
        for (int i = 0; i < size; i++) {
            data[i] = (byte) (Math.random() * 256);
        }
        // then write the data to the file
        try {
            Files.write(target, data);
        } catch (IOException e) {
            System.err.println("Error writing file: " + e.getMessage());
        }
    }
}
