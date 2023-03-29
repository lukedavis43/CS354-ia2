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
		String symbol = "<";
		if (curr().equals(new Token(symbol))) {
			match(symbol);
			return new NodeRelop(pos(),symbol);
		}
		symbol = "<=";
		if (curr().equals(new Token(symbol))) {
			match(symbol);
			return new NodeRelop(pos(),symbol);
		}
		symbol = ">";
		if (curr().equals(new Token(symbol))) {
			match(symbol);
			return new NodeRelop(pos(),symbol);
		}
		symbol = ">=";
		if (curr().equals(new Token(symbol))) {
			match(symbol);
			return new NodeRelop(pos(),symbol);
		}
		symbol = "<>";
		if (curr().equals(new Token(symbol))) {
			match(symbol);
			return new NodeRelop(pos(),symbol);
		}
		symbol = "==";
		if (curr().equals(new Token(symbol))) {
			match(symbol);
			return new NodeRelop(pos(),symbol);
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

	private NodeUnary parseUnary() throws SyntaxException {
		// Initialize a counter to keep track of the number of negations encountered
		int x = 1;
	
		// Parse a series of minus signs and flip the sign for each one
		while (curr().equals(new Token("-"))) {
			match("-");
			x *= -1;
		}
	
		// If the number of negations is odd, create a new NodeUnary object with a value of "-"
		if (x == -1) {
			return new NodeUnary("-");
		}
	
		// Otherwise, return null
		return null;
	}



	private NodeFact parseFact() throws SyntaxException {
		//Check if there's a negative
		NodeUnary negative = parseUnary();

		if (curr().equals(new Token("("))) {
			match("(");
			NodeExpr expr=parseExpr();
			match(")");
			//Create a new expression within the () and recurse
			return new NodeFactExpr(expr, negative);
		}
		if (curr().equals(new Token("id"))) {
			Token id=curr();
			match("id");
			//Create a new NodeFact that will obtain the value of an id later at evaluation time
			return new NodeFactId(pos(),id.lex(), negative);
		}
		Token num=curr();
		match("num");
		//Return a number
		return new NodeFactNum(num.lex(), negative);
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
		//First part of boolExpr is the left expr
		NodeExpr expr1 = parseExpr();
		NodeRelop relop = parseRelop();
		NodeExpr expr2 = parseExpr();
		return new NodeBoolExpr(expr1, relop, expr2);
    }

	private NodeStmt parseStmt() throws SyntaxException {
		//Parses assignment
		NodeStmt stmt;
		
		switch(curr().lex()){
			case "if":
				match("if");
				NodeBoolExpr ifExpr = parseBoolExpr();
				match("then");
				NodeStmt stmtIf = parseStmt();
				//Optional else
				if(curr().lex().equals("else")){
					match("else");
					NodeStmt stmtElse = parseStmt();
					stmt = new NodeStmtIfElse(ifExpr, stmtIf, stmtElse);
				}
				else{
					stmt = new NodeStmtIf(ifExpr, stmtIf);
				}
				break;
			case "while":
				match("while");
				NodeBoolExpr whileExpr = parseBoolExpr();
				match("do");
				NodeStmt stmtWhile = parseStmt();
				stmt = new NodeStmtWhile(whileExpr, stmtWhile);
				break;
			case "rd":
				match("rd");
				Token id = curr();
				match("id");
				stmt = new NodeStmtRd(id.lex());
				break;
			case "wr":
				match("wr");
				NodeExpr expr = parseExpr();
				stmt = new NodeStmtWr(expr);
				break;
			case "begin":
				match("begin");
				stmt = parseBlock();
				match("end");

				break;
			default:
				NodeAssn assn = parseAssn();
				stmt = new NodeStmtAssn(assn);
				break;
		}
		return stmt;
	}

	public NodeBlock parseBlock() throws SyntaxException
	{
		List<NodeStmt> statements = new ArrayList<NodeStmt>();
		NodeStmt statement;
		//Keep reading until it doesn't find anything
		while(true)
		{
			
			statement = parseStmt();
			statements.add(statement);

			if(curr().equals(new Token(";"))){
				//Expects semi after each statement
				match(";");
			}
			else{
				break;
			}
		}
		return new NodeBlock(statements);
	}

	public Node parse(String program) throws SyntaxException {
		scanner=new Scanner(program);
		scanner.next();
		//Starts parsing statement
		NodeBlock block=parseBlock();
		match("EOF");
		return block;
	}
}
