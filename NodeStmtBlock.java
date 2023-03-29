public class NodeStmtBlock extends NodeStmt {

	private NodeBlock block;
	
	public NodeStmtBlock(NodeBlock blk) {
		block = blk;
	}
	
	public double eval(Environment env) throws EvalException {
		return block.eval(env);
	}
}