package hu.edudroid.modulerewriter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.WhileStatement;

public class ModuleRewriter {

	public static void main(String[] args) {
		try {
			addMethod(args[0]);
			ParseFiles(args[0],args[1]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// use ASTParse to parse string
	public static void parse(final String source, String destination) throws IOException {

		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(source.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);

		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);

		final AST ast = cu.getAST();
		
		cu.accept(new ASTVisitor() {

			public boolean visit(MethodDeclaration node) {
				
				SimpleName declarationname = node.getName();
				String name = declarationname.toString();
				
				if(name.compareTo("run") == 0 ||name.compareTo("onTimerEvent") == 0){
					
					MethodInvocation newInvocation1 = ast.newMethodInvocation();
					newInvocation1.setName(ast.newSimpleName("setTid"));
					Statement newStatement1 = ast
							.newExpressionStatement(newInvocation1);
					
					MethodInvocation newInvocation2 = ast.newMethodInvocation();
					newInvocation2.setName(ast.newSimpleName("threadSleeper"));
					Statement newStatement2 = ast
							.newExpressionStatement(newInvocation2);
					
					Block b = (Block) node.getBody();
					b.statements().add(0, newStatement1);
					b.statements().add(1, newStatement2);
					node.setBody((Block) ASTNode.copySubtree(b.getAST(), b));
					
				}
				
				return true;
			}

			public boolean visit(ForStatement node) {

				MethodInvocation newInvocation = ast.newMethodInvocation();
				newInvocation.setName(ast.newSimpleName("threadSleeper"));
				Statement newStatement = ast
						.newExpressionStatement(newInvocation);
				Block b = (Block) node.getBody();
				b.statements().add(0, newStatement);
				node.setBody((Statement) ASTNode.copySubtree(b.getAST(), b));
	
				return true;
			}

			public boolean visit(WhileStatement node) {
				MethodInvocation newInvocation = ast.newMethodInvocation();
				newInvocation.setName(ast.newSimpleName("threadSleeper"));
				Statement newStatement = ast
						.newExpressionStatement(newInvocation);
				Block b = (Block) node.getBody();
				b.statements().add(0, newStatement);
				node.setBody((Statement) ASTNode.copySubtree(b.getAST(), b));
				
				return true;
			}

			public boolean visit(DoStatement node) {
				MethodInvocation newInvocation = ast.newMethodInvocation();
				newInvocation.setName(ast.newSimpleName("threadSleeper"));
				Statement newStatement = ast
						.newExpressionStatement(newInvocation);
				Block b = (Block) node.getBody();
				b.statements().add(0, newStatement);
				node.setBody((Statement) ASTNode.copySubtree(b.getAST(), b));
				
				return true;
			}
		});
		
		String outfilePath = destination;
		
		FileWriter fstream = new FileWriter(outfilePath);
		BufferedWriter out = new BufferedWriter(fstream);
		out.write(cu.toString());
		out.close();
	}

	public static String convertStreamToString(InputStream is)
			throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line).append("\n");
		}
		return sb.toString();
	}

	public static String getStringFromFile(String filePath) throws IOException {
		File file = new File(filePath);
		FileInputStream fis = new FileInputStream(file);
		String ret = convertStreamToString(fis);
		fis.close();
		return ret;
	}

	public static void addMethod(String sourcefile) throws IOException {
		String filePath = sourcefile;
		String file = getStringFromFile(filePath);
		String methods = "public void setTid(){\n"
				+ "mThreadSemaphore.setThreadId();\n"
				+ "}\n"
				+ "public void threadSleeper(){\n"
				+ "if (mThreadSemaphore.availablePermits() == 0) {\n"
				+ "long time = 500;\n"
				+ "try {\n"
				+ "Thread.sleep(time);\n"
				+ "} catch (InterruptedException e) {\n"
				+ "e.printStackTrace();\n"
				+ "}\n"
				+ "}\n"
				+ "}\n" 
				+ "}";
		String declaration = "mThreadSemaphore.setThreadId();";
		if (file.indexOf(declaration) == -1) {
			file = file.substring(0, file.lastIndexOf("}")) + methods;

			FileWriter fstream = new FileWriter(filePath);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(file);
			out.close();
		}
	}

	public static void ParseFiles(String sourcefile, String destination) throws IOException {
		String filePath = sourcefile;
		parse(getStringFromFile(filePath),destination);

	}
}