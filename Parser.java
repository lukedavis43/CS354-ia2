// This class is a recursive-descent parser,
// modeled after the programming language's grammar.
// It constructs and has-a Scanner for the program
// being parsed.
import java.util.*;

public class Parser {

	private Scanner scanner;

	private void match(String s) throws SyntaxException {
		scanner.match(new Token(s));
	}

	private Token curr() throws SyntaxException {
		return scanner.curr();
	}

	private int pos() {
		return scanner.pos();
	}

	//Parse multiplication operators
    private NodeRelop parseRelop() throws SyntaxException {
		if (curr().equals(new Token("<"))) {
			match("<");
			return new NodeRelop(pos(), "<");
		}
		if (curr().equals(new Token("<="))) {
			match("<=");
			return new NodeRelop(pos(), "<=");
		}
		if (curr().equals(new Token(">"))) {
			match(">");
			return new NodeRelop(pos(), ">");
		}
		if (curr().equals(new Token(">="))) {
			match(">=");
			return new NodeRelop(pos(), ">=");
		}
		if (curr().equals(new Token("<>"))) {
			match("<>");
			return new NodeRelop(pos(), "<>");
		}
		if (curr().equals(new Token("=="))) {
			match("==");
			return new NodeRelop(pos(), "==");
		}
		return null;
	}

	private NodeMulop parseMulop() throws SyntaxException {
		if (curr().equals(new Token("*"))) {
			match("*");
			return new NodeMulop(pos(), "*");
		}
		if (curr().equals(new Token("/"))) {
			match("/");
			return new NodeMulop(pos(), "/");
		}
		return null;
	}

	private NodeAddop parseAddop() throws SyntaxException {
		if (curr().equals(new Token("+"))) {
			match("+");
			return new NodeAddop(pos(), "+");
		}
		if (curr().equals(new Token("-"))) {
			match("-");
			return new NodeAddop(pos(), "-");
		}
		return null;
	}
	// This function attempts to parse a unary value from the current input token.
	// If successful, it returns a new NodeUnary object with a value of "-" if the number 
	// of negations encountered is odd. If no negations are encountered, it returns null.

	//private NodeUnary parseUnary() throws SyntaxException {
		// Initialize a counter to keep track of the number of negations encountered
	//	int x = 1;
	
		// Parse a series of minus signs and flip the sign for each one
	//	while (curr().equals(new Token("-"))) {
	//		match("-");
	//		x *= -1;
	//	}
	
		// If the number of negations is odd, create a new NodeUnary object with a value of "-"
	//	if (x == -1) {
	//		return new NodeUnary("-");
	//	}
	
		// Otherwise, return null
	//	return null;
	//}



	private NodeFact parseFact() throws SyntaxException {
		if (curr().equals(new Token("("))) {
			match("(");
			NodeExpr expr = parseExpr();
			match(")");
			return new NodeFactExpr(expr);
		}
		if (curr().equals(new Token("id"))) {
			Token id = curr();
			match("id");
			return new NodeFactId(pos(), id.lex());
		}
		if (curr().equals(new Token("-"))) { // my attempt at shoehorning a
												
			match("-");
			NodeFact fact = parseFact();
			return new NodeFactNegative(fact);
		}
		Token num = curr();
		match("num");
		return new NodeFactNum(num.lex());
	}

	private NodeTerm parseTerm() throws SyntaxException {
		NodeFact fact=parseFact();
		//Parse a multiplicative operator. This is done above add ops so that they evaluate first
		NodeMulop mulop=parseMulop();
		if (mulop==null)
	    	return new NodeTerm(fact,null,null);
		NodeTerm term=parseTerm();
		term.append(new NodeTerm(fact,mulop,null));
		return term;
	}

	private NodeExpr parseExpr() throws SyntaxException {
		NodeTerm term = parseTerm();
		NodeAddop addop = parseAddop();
		if (addop == null)
			return new NodeExpr(term, null, null);
		NodeExpr expr = parseExpr();
		expr.append(new NodeExpr(term, addop, null));
		return expr;
	}

	private NodeAssn parseAssn() throws SyntaxException {
		//First part of assignment is the id
		Token id=curr();
		//Make sure the token we just got is an id
		match("id");
		//Next up should be an equals sign
		match("=");
		//Parse out the expression after the equals sign
		NodeExpr expr=parseExpr();
		//Assign the expression to the id and return the new object
		NodeAssn assn=new NodeAssn(id.lex(),expr);
		return assn;
	}

	private NodeBoolExpr parseBoolExpr() throws SyntaxException {
		NodeExpr o1 = parseExpr();
		NodeRelop relop = parseRelop();
		NodeExpr o2 = parseExpr();

		NodeBoolExpr boolexpr = new NodeBoolExpr(o1, relop, o2);
		return boolexpr;
	}

	private NodeStmt parseStmt() throws SyntaxException {
		if (curr().equals(new Token("rd"))) {
			match("rd");
			Token id = curr();
			
			NodeStmtRd stmtRd = new NodeStmtRd(id.lex());
			return stmtRd;
		}
		
    	if (curr().equals(new Token("wr"))) {
    		match("wr");
    		NodeExpr expr = parseExpr();
    		NodeStmtWr stmtWr = new NodeStmtWr(expr);
    		return stmtWr;
    	}
    	
    	if (curr().equals(new Token("if"))) {
    		match("if");
    		NodeBoolExpr boolexpr = parseBoolExpr();
    		match("then");
    		NodeStmt tStmt = parseStmt();
    		if (curr().equals(new Token("else"))) {
    			match("else");
    			NodeStmt fStmt = parseStmt();
    			NodeStmtIfThen stmtIf = new NodeStmtIfThen(boolexpr, tStmt, fStmt);
    			return stmtIf;
    		}
    		NodeStmtIfThen stmtIf = new NodeStmtIfThen(boolexpr, tStmt);
    		return stmtIf;
    	}
    	
    	if (curr().equals(new Token("while"))) {
    		match("while");
    		NodeBoolExpr boolexpr = parseBoolExpr();
    		match("do");
    		NodeStmt stmt = parseStmt();
    		NodeStmtWhile stmtWhile = new NodeStmtWhile(boolexpr, stmt);
    		return stmtWhile;
    	}
    	
    	if (curr().equals(new Token("begin"))) {
    		match("begin");
    		NodeBlock block = parseBlock();
    		match("end");
    		NodeStmtBlock stmtBlock = new NodeStmtBlock(block);
    		return stmtBlock;
    	}

    	NodeAssn assn = parseAssn();
    	NodeStmtAssn stmtAssn = new NodeStmtAssn(assn);
		return stmtAssn;
	}

	public NodeBlock parseBlock() throws SyntaxException
	{
		NodeStmt stmt = parseStmt();
		NodeBlock nextBlock = null;
		
		if (curr().equals(new Token(";"))) {
			match(";");
			nextBlock = parseBlock();
		}

		NodeBlock block = new NodeBlock(stmt, nextBlock);
		return block;
	}

	public Node parse(String program) throws SyntaxException {
		if (program.contains("#")) { // comment parsing
			program = program.substring(0, program.indexOf("#"));
		}
		scanner = new Scanner(program);
		scanner.next();
		return parseBlock();
	}
}
