// This is the main class/method for the interpreter/compiler.
// Each command-line argument is a complete program,
// which is scanned, parsed, and evaluated.
// All evaluations share the same environment,
// so they can share variables.

public class Main {

	public static void main(String[] args) {
		Parser parser=new Parser();
		Environment env=new Environment();
		for (String stmt: args)
			try {
				double value = parser.parse(stmt).eval(env);
				if(!Double.isNaN(value)){
				System.out.println(value);
			}
	    	} catch (SyntaxException e) {
				System.err.println(e);
			} catch (Exception e) {
				System.err.println(e);
			}
	}

}
