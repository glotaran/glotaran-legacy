### **Part 1: Architectural Documentation**

**1. High-Level Overview**
Glotaran is a Java-based desktop application for analyzing time-resolved spectroscopy and microscopy data. It serves as a graphical user interface (GUI) for the R package "TIMP," which performs the actual data analysis. The application is built on the NetBeans Platform (version 8.0.2), which provides its modular architecture and UI components. It is compiled with Java 1.7.

**2. Module Breakdown**
The application is highly modular, consisting of numerous Maven modules. Key module categories include:
- **Core Application:** `application`, `branding`, `CoreMain`, `CoreInterfaces`, `CoreUI`.
- **Data Modeling & Analysis:** `CoreModels`, `CoreAnalysis`, `CoreSimulation`.
- **R Integration:** `JRConnect` (low-level connection via Rserve), `TIMPController` (high-level logic for the TIMP package).
- **Data Loaders:** A suite of `*Dataloader` modules for various file formats (ASCII, CSV, PicoQuant, etc.).
- **File Type Support:** `*FileSupport` modules to integrate custom file types (`.gta`, `.tgm`) into the IDE.
- **Visualization:** `CoreDataDisplayers`, `CoreResultDisplayers`, `CoreVisualModelling`, and wrappers for `JFreeChart` and `JIDE`.

**3. Dependency Analysis**
The project suffers from significant technical debt in its dependency management:
- **Outdated Stack:** It uses Java 1.7 and NetBeans Platform 8.0.2.
- **Unmanaged JARs:** A `libs/` directory contains dozens of JARs that are not managed by Maven.
- **Local Repository:** A `repo/` directory is checked into version control and used as a local Maven repository, which is an anti-pattern. This contains libraries not found on public repositories and a partial mirror of old NetBeans dependencies.

**4. Build Process**
The project is built with Apache Maven, using the `nbm-maven-plugin` to handle the complexities of building a NetBeans Platform application. The build process creates NetBeans Modules (`.nbm` files) and assembles them into a final application with native installers.

**5. R Integration**
Glotaran's core analysis capability is delegated to an external R process.
- **Engine:** The `TIMP` R package.
- **Bridge:** The `Rserve` library, which runs a TCP/IP server in R.
- **Mechanism:** The Java application acts as a client, connecting to the Rserve server via the `JRConnect` module to execute TIMP commands, which are orchestrated by the `TIMPController` module.

### **Part 2: Modernization Plan**

**1. Acknowledgment of Deprecation**
It is critical to note that the project is officially deprecated in favor of **`pyglotaran`**, a complete rewrite in Python. Before proceeding with any modernization, the stakeholders should confirm that investing in the legacy Java application is the desired path.

**2. Proposed Modernization Strategy**
The goal is to update the application to modern standards, improve maintainability, and enhance security without breaking functionality.

**3. Phased Modernization Roadmap**

*   **Phase 1: Dependency Management Overhaul**
    1.  **Objective:** Eliminate the `libs/` and `repo/` directories.
    2.  **Actions:**
        - Identify every JAR in `libs/` and `repo/`.
        - Find and replace them with corresponding dependencies from Maven Central in the `pom.xml`.
        - For any private or un-findable JARs, deploy them to a private artifact repository (e.g., Nexus, Artifactory).
    3.  **Outcome:** A clean, portable, and standard Maven build.

*   **Phase 2: JDK and Build Tools Upgrade**
    1.  **Objective:** Move from Java 1.7 to a modern LTS version (e.g., Java 17).
    2.  **Actions:**
        - Update the `<java.version>` property in `pom.xml`.
        - Update all Maven plugin versions in the parent POM to their latest stable versions.
        - Fix any compilation errors that arise from deprecated or removed APIs (e.g., JAXB will need to be added as an explicit dependency).
    3.  **Outcome:** The application runs on a modern, secure JVM, and the build process is up-to-date.

*   **Phase 3: NetBeans Platform Upgrade**
    1.  **Objective:** Upgrade from NetBeans Platform 8.0.2 to a recent Apache NetBeans release (e.g., NetBeans 19+).
    2.  **Actions:**
        - Update the `<netbeans.version>` property.
        - This will be the most challenging phase, likely requiring significant code changes to adapt to new NetBeans APIs, especially in UI and windowing system code.
        - Thoroughly test all UI components and application functionality.
    3.  **Outcome:** A more modern look and feel, better performance, and access to new platform features.

*   **Phase 4: Code Quality and Refactoring**
    1.  **Objective:** Improve the internal quality of the codebase.
    2.  **Actions:**
        - Introduce a static analysis tool (e.g., SonarQube) to identify code smells and bugs.
        - Refactor legacy code patterns to use modern Java features (e.g., streams, lambdas).
        - Improve and expand the test suite to increase code coverage and confidence in changes.
    3.  **Outcome:** A more robust, maintainable, and future-proof codebase.
