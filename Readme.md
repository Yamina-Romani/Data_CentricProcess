# Data centric process for microservices identification

This project identifies potential microservices from a monolithic application using a data-driven approach. It uses the data model to generate a set of potential microservices then it involves the source code of the monolith to generate the final decomposition based on clustering-based modularity algorithm.

# Usage Example

### Step 1: Prepare Your Data

1. Place your data.json file (containing the data model) in the Data/monolith-project/ directory.
2. Place the monolithic application's source code in the Data/monolith-project/monolith-sourceCode/ directory.

Example directory structure of Spring PetClinic:
```
Data/
├── spring-petclinic/
│   ├── data.json                # Data model file
│   └── spring-petclinic/        # Monolith source code
│       ├── src/
│       ├── pom.xml
│       └── ...
```
### Step 2: Run the Project
Use the following command to analyze the monolith and identify microservices:

```bash 
python main.py --dataPath Data/spring-petclinic/data.json --sourcePath Data/spring-petclinic/spring-petclinic --cluster 4
```

Command Arguments:

- **--dataPath**: Path to the data.json file (e.g., Data/spring-petclinic/data.json).

- **--sourcePath**: Path to the monolith's source code directory (e.g., Data/spring-petclinic/spring-petclinic).

- **--cluster**: Number of potential microservices (clusters) for database (e.g., 4).
