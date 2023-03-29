public class NodeStmtIfThen extends NodeStmt {

    private NodeBoolexpr boolexpr;
	private NodeStmt thenStmt;
	private NodeStmt elseStmt;

	public NodeStmtIfThen(NodeBoolexpr boolexpr, NodeStmt ts) {
		this.boolexpr = boolexpr;
		thenStmt = ts;
		elseStmt = null;
	}

	public NodeStmtIfThen(NodeBoolexpr boolexpr, NodeStmt ts, NodeStmt es) {
		this.boolexpr = boolexpr;
		thenStmt = ts;
		elseStmt = es;
	}

	/**
	 * If the boolean expression returns true, then the provided 'then'
	 * statement will be evaluated. Otherwise, the 'else' statement will be
	 * evaluated (if it exists).
	 * 
	 * @return
	 * @throws EvalException
	 */
	public double eval(Environment env) throws EvalException {
		double bool = boolexpr.eval(env);
		if (bool == 1.0) {
			return thenStmt.eval(env);
		} else if (elseStmt != null) {
			return elseStmt.eval(env);
		}

		return 0.0;
	}

}