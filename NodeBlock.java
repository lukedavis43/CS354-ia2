import java.util.ArrayList;
import java.util.List;

public class NodeBlock extends NodeStmt {

    private NodeStmt stmt;
	private NodeBlock block;

	public NodeBlock(NodeStmt st) {
		stmt = st;
		block = null;
	}

	public NodeBlock(NodeStmt st, NodeBlock bl) {
		stmt = st;
		block = bl;
	}

	/**
	 * Evaluates the statement inside this block. A semicolon at the end of the
	 * statement denotes another statement to be evaluated.
	 */
	public double eval(Environment env) throws EvalException {
		if (block != null) {
			stmt.eval(env);
			return block.eval(env);
		}

		return stmt.eval(env);

	}

}