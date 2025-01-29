import os
import subprocess

import glob

import os


# Call Java program
def parse_java_code(projectPath):
    jar_file_path = os.path.abspath("sourceCodeParser/target/my-project-1.0-SNAPSHOT.jar").replace("\\", "/")
    path = f"{jar_file_path}"
    #print("path = ", jar_file_path)
    projectPath = os.path.abspath(projectPath).replace("\\", "/")

    command = ["java", "-cp", path, "Javamain", projectPath]

    print("Running command:", command)
    try:
        # Run the Java command
        result = subprocess.run(
            command,
            text=True,
            capture_output=True,
            check=True,
            cwd=os.path.abspath("sourceCodeParser")

        )

        # Print the output of the Java program
        #print("Java program output:")
        #print(result.stdout)

    except subprocess.CalledProcessError as e:
        # Print error details if the Java program fails
        print("Error occurred while running the Java program:")
        print("Return code:", e.returncode)
        print("Error output:")
        print(e.stderr)
    except Exception as e:
        # Handle other exceptions
        print("An unexpected error occurred:")
        print(str(e))