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

	private NodeBoolexpr parseBoolexpr() throws SyntaxException {
		NodeExpr o1 = parseExpr();
		NodeRelop relop = parseRelop();
		NodeExpr o2 = parseExpr();

		NodeBoolexpr boolexpr = new NodeBoolexpr(o1, relop, o2);
		return boolexpr;
	}

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
												// unary minus -KC
			match("-");
			NodeFact fact = parseFact();
			return new NodeFactNeg(fact);
		}
		Token num = curr();
		match("num");
		return new NodeFactNum(num.lex());
	}

	private NodeTerm parseTerm() throws SyntaxException {
		NodeFact fact = parseFact();
		NodeMulop mulop = parseMulop();
		if (mulop == null)
			return new NodeTerm(fact, null, null);
		NodeTerm term = parseTerm();
		term.append(new NodeTerm(fact, mulop, null));
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
		Token id = curr();
		match("id");
		match("=");
		NodeExpr expr = parseExpr();
		NodeAssn assn = new NodeAssn(id.lex(), expr);
		return assn;
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
    		NodeBoolexpr boolexpr = parseBoolexpr();
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
    		NodeBoolexpr boolexpr = parseBoolexpr();
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

	private NodeBlock parseBlock() throws SyntaxException {
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
