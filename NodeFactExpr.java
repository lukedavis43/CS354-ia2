public class NodeFactExpr extends NodeFact {

	private NodeExpr expr;
	//private NodeUnary negative;

	public NodeFactExpr(NodeExpr expr) {
		this.expr=expr;
	}

	public double eval(Environment env) throws EvalException {
		//return negative != null ? -1*expr.eval(env) : expr.eval(env);
		return expr.eval(env);
	}

}
