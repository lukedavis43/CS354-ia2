public class NodeStmtWr extends NodeStmt {

    private NodeExpr expr;
	
	public NodeStmtWr(NodeExpr ex) {
		expr = ex;
	}
	
	public double eval(Environment env) throws EvalException {
		double result = expr.eval(env);
		System.out.println(result);
		return result;
	}

}