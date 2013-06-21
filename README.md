Entrancer
=========
Entrancer stands "Enhancing Software Traceability By Automatically Expanding Corpora With Relevant Documentation". This was one of the projects I worked with  Grechanik, M., Moritz, E., Dit, B., and Poshyvanyk, D. The work has been published at the 29th IEEE International Conference on Software Maintenance (ICSM'13), Eindhoven, the Netherlands, September 22-28, 2013, to appear 10 pages (22% acceptance ratio).

The crux of entrancer is the following: Traceability between source code and documentation is a difficult problem. While studying all the recent works in the area I realized, all previous attempts have been considering source code as natural language texts and using various information retrieval methodologies to relate (requirement) documentation with source code. However, source code is an abstract form which highly encapsulates information and as a result increases the semantic distance between the natural language (requirement) documentation and source code. Expanding API method calls with corresponding developer documentation and identifier splitting ("setEnvironment(filePath)" becomes "set environment file path") can significantly increase precision.

More details, data, experimental setup http://www.cs.uic.edu/~drmark/entrancer.htm

AST Parser
==========
The Abstract Syntax tree parser is built by reusing selected components from Eclipse Java Development Toolkit (JDT). I needed only the java identifier and the method calls given a bunch of java files, so I selectively implemented only those API calls required for the purpose.

To run
    
	java -cp "../lib/*;." entrancer.JDTParser "path/to/java/files"  "output/path"
	
JDKAPI
======
To expand method calls with corresponding java docs, one needed programmatic access to the API documentation. However, there is no way to get javadoc statements for a method programmatically. Accordingly, I scraped the jdk documentation and stored them in MongoDB. JDKAPIDBAccess.java is meant for access this database.
