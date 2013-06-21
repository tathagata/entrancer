package entrancer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;



public class JDTParser {
	static String corpus;
	
	public static void parse(String str,String filePath) throws JavaModelException {
		Properties prop = System.getProperties();
		String[] classPathEntries = prop.getProperty("java.class.path").split("path.separator");
		String[] sourcePathEntries = new String[] {new File(filePath).getParent()};
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setResolveBindings(true);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(str.toCharArray());
		parser.setEnvironment(classPathEntries, sourcePathEntries, null, false);
		//parser.setUnitName("path_to_target");
		parser.setUnitName(filePath);

		final CompilationUnit root = (CompilationUnit) parser.createAST(null);
		final Set names = new HashSet(); 
		final String[] source = str.split("\n");
		int count = 0;
		final Map<String,Integer> methodFreq = new HashMap<String,Integer>();
		root.accept(new ASTVisitor() {
			/*
			public boolean visit(ConstructorInvocation node) {
				System.out.println("Constructor invocation");
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
					System.out.println(doc);
				}
				return super.visit(node);
			}
				*/
			
			@Override
			public boolean visit(VariableDeclarationFragment node) {
				SimpleName name = node.getName();
				names.add(name.getIdentifier());
				//System.out.println(name.propertyDescriptors(3).toString());
				return false; // do not continue to avoid usage info
			}
 			
		    @Override
			public boolean visit(LineComment node) {

		        int startLineNumber = root.getLineNumber(node.getStartPosition()) - 1;
		        String lineComment = source[startLineNumber].trim();
		        //System.out.println(lineComment);
		        return true;
		    }

		    @Override
			public boolean visit(BlockComment node) {
		        int startLineNumber = root.getLineNumber(node.getStartPosition()) - 1;
		        int endLineNumber = root.getLineNumber(node.getStartPosition() + node.getLength()) - 1;

		        StringBuffer blockComment = new StringBuffer();

		        for (int lineCount = startLineNumber ; lineCount<= endLineNumber; lineCount++) {

		            String blockCommentLine = source[lineCount].trim();
		            blockComment.append(blockCommentLine);
		            if (lineCount != endLineNumber) {
		                blockComment.append("\n");
		            }
		        }
		        //System.out.println(blockComment.toString());
		        return true;
		    }
		
			
		    @Override
			public boolean visit(SimpleName node) {
				if (names.contains(node.getIdentifier())) {
				//System.out.println(node + "\t");
				}
				
				try {
					PrintWriter out = new PrintWriter(new BufferedWriter(
							new FileWriter(corpus, true)));
					out.print(node+", " );
					out.close();
				} catch (IOException e) {
					// oh noes!
				}
				
				//return true;
				return false;
			}
			
			
			@Override
			public boolean visit(MethodInvocation node) {
				IMethodBinding methodBinding = node.resolveMethodBinding();
				if (methodBinding==null)
					System.out.println("Method binding not working"+ node.toString());
				
				if (methodBinding != null) {
					StringBuilder str = new StringBuilder();

					String methodName = "";
					System.out.println("node:resolveBinding: "+node.resolveMethodBinding().toString());
					String[] methodNameSplit = node.resolveMethodBinding()
							.toString().split(" ");
					System.out.println(methodNameSplit);
					
					if (methodNameSplit[1].equalsIgnoreCase("abstract")
							|| methodNameSplit[1].equalsIgnoreCase("final")
							|| methodNameSplit[1].equalsIgnoreCase("native")
							|| methodNameSplit[1].equalsIgnoreCase("abstract")
							|| methodNameSplit[1].equalsIgnoreCase("static")
							|| methodNameSplit[1].equalsIgnoreCase("abstract")
							|| methodNameSplit[1]
									.equalsIgnoreCase("synchronized")){
						System.out.println(methodNameSplit.length);
						methodName = methodNameSplit[3];
						
					}else{
						System.out.println(methodNameSplit.length);
						methodName = methodNameSplit[methodNameSplit.length-1];
					}
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
				//	System.out.print(":"+queryString+"\t");

					StringBuilder sbr = new StringBuilder("");
					String signature = (methodBinding.getDeclaringClass()
							.getQualifiedName() + "." + queryString.toString()
							.replace("java.lang.", ""));

					str.append(sbr + signature);
					
					int count = methodFreq.containsKey(signature) ? methodFreq.get(signature) : 0;
					methodFreq.put(signature, count + 1);
					
					/*
					JDKAPIDBAccess jdkApiDBAccess = new JDKAPIDBAccess();
					
					String replace = jdkApiDBAccess.getDocumentation(signature,
							sbr.toString()) + " ";
					replace = replace.replaceAll("\n", "")
							.replaceAll(".\n", "");
					System.out.print("Replace:"+replace);
					*/
					try {
						PrintWriter out = new PrintWriter(new BufferedWriter(
								new FileWriter(corpus, true)));
						//out.print(replace);
						out.print(signature +", ");
						out.close();
					} catch (IOException e) {
						// oh noes!
					}
					
				}

				return super.visit(node);
			}
		});
		
		for (Map.Entry<String, Integer> entry : methodFreq.entrySet()) {
		    String key = entry.getKey();
		    Integer value = entry.getValue();
		    System.out.println(key+", "+value);
		}
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

	public static void ParseFilesInDir(String pathToDirectory) throws IOException, JavaModelException {
		File root = new File(pathToDirectory);
		File[] files = root.listFiles();
		String filePath = null;

		for (File f : files) {
			PrintWriter out;
			try {
				out = new PrintWriter(new BufferedWriter(new FileWriter(corpus,
						true)));
				out.print(f.toString().substring(pathToDirectory.length()) + ", ");
				System.out.println();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			filePath = f.getAbsolutePath();
			if (f.isFile()) {
				System.out.println("Processing File:"+f);
				parse(readFileToString(filePath),filePath);
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

	public static void ParseSingleFile(String pathToJavaFile) throws IOException, JavaModelException {
		File f = new File(pathToJavaFile);
		parse(readFileToString(f.getAbsolutePath()),f.getAbsolutePath());
	}

	public static void main(String args[]) throws IOException,
			JavaModelException {
		String fileName = new SimpleDateFormat("yyyyMMddhhmm'.csv'").format(new Date());
		if (args.length != 2) {
			System.out
					.println("usage: java -cp \"../lib/*;.\" entrancer.JDTParser\" \"filename.java \"output_path\"");
			System.exit(1);
		}
		corpus = args[1]+fileName;
		//ParseSingleFile(args[0]);
		ParseFilesInDir(args[0]);
	}
}