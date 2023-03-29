public class NodeFactNegative extends NodeFact {

	/**
	 * The child Factor node.
	 */
	private NodeFact fact;

	public NodeFactNegative(NodeFact fact) {
		this.fact = fact;
	}

	/**
	 * Evaluates the value of this node.
	 */
	public double eval(Environment env) throws EvalException {
		return -1.0 * fact.eval(env);
	}

}