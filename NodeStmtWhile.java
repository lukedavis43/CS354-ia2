public class NodeStmtWhile extends NodeStmt {

    private NodeBoolexpr boolexpr;
	private NodeStmt stmt;

	public NodeStmtWhile(NodeBoolexpr cond, NodeStmt st) {
		boolexpr = cond;
		stmt = st;
	}

	/**
	 * Evaluates the boolean expression, and if it returns true, the statement
	 * will be evaluated. This process will repeat until the boolean expression
	 * returns false.
	 * 
	 * @param env
	 *            (Environment)
	 * @throws EvalException
	 */
	public double eval(Environment env) throws EvalException {
		while (boolexpr.eval(env) == 1.0) {
			stmt.eval(env);
		}

		return 0.0;
	}

}