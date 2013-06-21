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
	
would generate a  yyyyMMddhhmm.csv for each .java file under subject application containing all the identifiers and the fully qualified method signatures in the code.		

Obligatory Dogfooding example:

    java -cp "../lib/*;." entrancer.JDTParser "../src/entrancer/"  "."
	
would generate a yyyyMMddhhmm.csv file with first line
	
    JDKAPIDBAccess.java, entrancer, com, mongodb, Mongo, com, mongodb, MongoException, com, mongodb, WriteConcern, com, mongodb, DB, com, mongodb, DBCollection, com, mongodb, BasicDBObject, com, mongodb, DBObject, com, mongodb, DBCursor, com, mongodb, ServerAddress, JDKAPIDBAccess, Mongo, DBCollection, DBCollection, DBCollection, DBCursor, JDKAPIDBAccess, m, Mongo, DB, jdk_coll, com.mongodb.DB.getCollection(String), db, getCollection, ext_coll, com.mongodb.DB.getCollection(String), db, getCollection, self_coll, com.mongodb.DB.getCollection(String), db, getCollection, Exception, ex, java.lang.Throwable.getMessage(), ex, getMessage, String, getDocumentation, String, code, String, typeDescription, java.io.PrintStream.println(String), System, out, println, StringBuffer, cursor, com.mongodb.DBCollection.find(com.mongodb.DBObject, com.mongodb.DBObject), jdk_coll, find, BasicDBObject, code, com.mongodb.BasicDBObject.append(String, Object), BasicDBObject, typeDescription, append, java.io.PrintStream.println(String), System, out, println, code, typeDescription, String, com.mongodb.DBCursor.hasNext(), cursor, hasNext, description, String, org.bson.BSONObject.get(String), com.mongodb.DBCursor.next(), cursor, next, get, java.lang.StringBuffer.append(String), sbr, append, description, com.mongodb.DBCursor.close(), cursor, close, java.lang.StringBuffer.toString(), sbr, toString, 	
	
	
JDKAPI
======
To expand method calls with corresponding java docs, one needed programmatic access to the API documentation. However, there is no way to get javadoc statements for a method programmatically. Accordingly, I scraped the jdk documentation and stored them in MongoDB. JDKAPIDBAccess.java is meant for access this database.

This repo contains only the java code and not the tracelab components and the python text processing toolbelt written for entrancer.