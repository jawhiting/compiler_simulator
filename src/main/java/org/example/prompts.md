# Initial prompt
Write a program to simulate a compile. It should take the name of a folder on the command line, a desired size, a number of subfolders and a number of files.
When it runs, it should create a unique folder in the specified folder. Under this, it should create the appropriate number of subfolders, and then in each folder it should create the specified number of unique files, containing random binary data.
Once all the files have been created, it should then iterate across each subfolder and file in turn, and completely read the contents into a byte array, before discarding it.
Finally, it should delete each individual file one at a time.
During all of these operations, each step should be timed. At the end, print a summary of the min, average, max for each stage.

# Set defaults for run
Set sensible defaults for all the command line parameters so that the program can run without having to pass in any parameters

# Logs
During each stage, log the progress every 100 files

# Fix the pom
update the pom file so that Main can be run via mvn:exec

# Delete folders, better resolution timings
Change it to also delete all the folders that were created as a final step.
Additionally, for each of the steps, keep track of the timings in nanoseconds rather than milliseconds.

# Actually fix the timings
Because the project is using Instant.now() for timings, the resolution of the timings are still in milliseconds. Can you change them to use a more appropriate timer - isn't there a System nanotime or something similar?

# Better output
When the statistics are displayed, convert them into milliseconds, to 3 decimal places

