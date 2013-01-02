package entrancer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class JDTParser {
	JDKAPIDBAccess jdkApiDBAccess = new JDKAPIDBAccess();
	//static String corpus = "/home/tathagata/Dropbox/research/entrancer/EAnciJDKSelfMethods.csv";
	static String corpus = "/tmp/test.csv";
	public static void parse(String str) throws JavaModelException {

		Properties prop = System.getProperties();
		String[] classPathEntries = prop.getProperty("java.class.path").split(
				":");
		String[] sourcePathEntries = new String[] { "/home/tathagata/projects/entracer/code/" };

		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setResolveBindings(true);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(str.toCharArray());
		parser.setEnvironment(classPathEntries, sourcePathEntries, null, false);
		parser.setUnitName("codeanalyzer/src/entrancer/codeanalyzer/main/TestClass1.java");
		

		final CompilationUnit root = (CompilationUnit) parser.createAST(null);
		final Set names = new HashSet(); 
		
		root.accept(new ASTVisitor() {
			
	
			public boolean visit(ConstructorInvocation node) {
				IMethodBinding methodBinding = node.resolveConstructorBinding();

				if (methodBinding != null) {
					StringBuilder str = new StringBuilder();
					String methodName;
					String[] methodNameSplit = node.resolveConstructorBinding()
							.toString().split(" ");
					if (methodNameSplit[1].equalsIgnoreCase("abstract")
							|| methodNameSplit[1].equalsIgnoreCase("final")
							|| methodNameSplit[1].equalsIgnoreCase("native")
							|| methodNameSplit[1].equalsIgnoreCase("static")
							|| methodNameSplit[1]
									.equalsIgnoreCase("synchronized"))
						methodName = methodNameSplit[3];
					else
						methodName = methodNameSplit[2];
					Pattern pattern = Pattern
							.compile(".*(\\s\\w+)\\s*(\\()(.*)(\\))");
					Matcher matcher = pattern.matcher(node
							.resolveConstructorBinding().toString());

					StringBuilder combineMatches = new StringBuilder("");
					while (matcher.find()) {
						for (int i = 1; i <= matcher.groupCount(); i++) {
							combineMatches.append(matcher.group(i));
						}
					}
					String queryString = combineMatches.toString()
							.replaceFirst(" ", "");
					System.out.print(queryString + "\t");

					StringBuilder sbr = new StringBuilder("");
					String signature = (methodBinding.getDeclaringClass()
							.getQualifiedName() + "." + queryString.toString()
							.replace("java.lang.", ""));

					str.append(sbr + signature);
					JDKAPIDBAccess jdkApiDBAccess = new JDKAPIDBAccess();
					String doc = jdkApiDBAccess.getDocumentation(signature,
							sbr.toString());
					doc = doc.replaceAll("\n", "").replaceAll(".\n", "");
					//System.out.println(doc);
				}
				return super.visit(node);
			}

			
			public boolean visit(VariableDeclarationFragment node) {
				SimpleName name = node.getName();
				names.add(name.getIdentifier());
				//System.out.println(name.propertyDescriptors(3).toString());
				return false; // do not continue to avoid usage info
			}
 
			public boolean visit(SimpleName node) {
				if (names.contains(node.getIdentifier())) {
				System.out.println(node + "\t");
				}
				
				try {
					PrintWriter out = new PrintWriter(new BufferedWriter(
							new FileWriter(corpus, true)));
					out.print("  "+node+"  ");
					out.close();
				} catch (IOException e) {
					// oh noes!
				}
				
				return true;
			}
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			public boolean visit(MethodInvocation node) {
				IMethodBinding methodBinding = node.resolveMethodBinding();

				if (methodBinding != null) {
					StringBuilder str = new StringBuilder();

					String methodName = "";
					String[] methodNameSplit = node.resolveMethodBinding()
							.toString().split(" ");
					if (methodNameSplit[1].equalsIgnoreCase("abstract")
							|| methodNameSplit[1].equalsIgnoreCase("final")
							|| methodNameSplit[1].equalsIgnoreCase("native")
							|| methodNameSplit[1].equalsIgnoreCase("abstract")
							|| methodNameSplit[1].equalsIgnoreCase("static")
							|| methodNameSplit[1].equalsIgnoreCase("abstract")
							|| methodNameSplit[1]
									.equalsIgnoreCase("synchronized"))
						methodName = methodNameSplit[3];
					else
						methodName = methodNameSplit[2];

					Pattern pattern = Pattern
							.compile(".*(\\s\\w+)\\s*(\\()(.*)(\\))");
					Matcher matcher = pattern.matcher(node
							.resolveMethodBinding().toString());

					StringBuilder combineMatches = new StringBuilder("");
					while (matcher.find()) {
						for (int i = 1; i <= matcher.groupCount(); i++) {
							combineMatches.append(matcher.group(i));
						}
					}
					String queryString = combineMatches.toString()
							.replaceFirst(" ", "");
					// System.out.print(queryString+"\t");

					StringBuilder sbr = new StringBuilder("");
					String signature = (methodBinding.getDeclaringClass()
							.getQualifiedName() + "." + queryString.toString()
							.replace("java.lang.", ""));

					str.append(sbr + signature);

					JDKAPIDBAccess jdkApiDBAccess = new JDKAPIDBAccess();
					String replace = jdkApiDBAccess.getDocumentation(signature,
							sbr.toString()) + " ";
					replace = replace.replaceAll("\n", "")
							.replaceAll(".\n", "");
					// System.out.print(replace);

					try {
						PrintWriter out = new PrintWriter(new BufferedWriter(
								new FileWriter(corpus, true)));
						out.print(replace);
						out.close();
					} catch (IOException e) {
						// oh noes!
					}

				}

				return super.visit(node);
			}
		});
	}

	// read file content into a string
	public static String readFileToString(String filePath) throws IOException {
		StringBuilder fileData = new StringBuilder(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));

		char[] buf = new char[10];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			// System.out.println(numRead);
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}

		reader.close();

		return fileData.toString();
	}

	public static void ParseFilesInDir() throws IOException, JavaModelException {
		File dirs = new File(".");
		String dirPath = dirs.getCanonicalPath() + File.separator + "src"
				+ File.separator;

		// File root = new File(dirPath);
		// File root = new
		// File("/home/tathagata/projects/entracer/code/codeanalyzer/src/entrancer/codeanalyzer/main");
		File root = new File(
				"/home/tathagata/projects/entracer/coest/EAnci/cc/");
		// System.out.println(root.listFiles());
		File[] files = root.listFiles();
		String filePath = null;

		for (File f : files) {
			// System.out.println("Processing:"+f);
			PrintWriter out;
			try {
				out = new PrintWriter(new BufferedWriter(new FileWriter(corpus,
						true)));
				out.print(f.toString().substring(49).replace(".txt", "") + "\t");
				System.out.println();
				out.close();
			} catch (IOException e) {
				// oh noes!
			}

			filePath = f.getAbsolutePath();
			if (f.isFile()) {
				parse(readFileToString(filePath));
				try {
					out = new PrintWriter(new BufferedWriter(new FileWriter(
							corpus, true)));
					out.println();
					System.out.println();
					out.close();
				} catch (IOException ioe) {

				}
			}

		}
	}

	public static void ParseSingleFile() throws IOException, JavaModelException {
		// File f = new
		// File("/home/tathagata/projects/entracer/code/codeanalyzer/src/entrancer/codeanalyzer/main/TestClass1.java");
		File f = new File(
				"/home/tathagata/projects/entracer/coest/EAnci/cc/EA149.txt");
		parse(readFileToString(f.getAbsolutePath()));
	}

	public static void main(String args[]) throws IOException,
			JavaModelException {
		System.out.println("Start");
		// ParseSingleFile();
		ParseFilesInDir();

	}
}