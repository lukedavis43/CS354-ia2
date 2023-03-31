public class NodeBlock extends Node {

    private NodeStmt stmt;
	private NodeBlock block;

	public NodeBlock(NodeStmt stmt, NodeBlock block) {
		this.stmt = stmt;
		this.block = block;
	}

	/**
	 * Evaluates the statement inside this block. A semicolon at the end of the
	 * statement denotes another statement to be evaluated.
	 */
	public double eval(Environment env) throws EvalException {
		double ev = stmt.eval(env);
        if (block == null){
            return ev;
        } else {
            return block.eval(env);
        }

	}

}