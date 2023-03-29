public class NodeStmtAssn extends NodeStmt {

    private NodeAssn assn;

    public NodeStmtAssn(NodeAssn as) {
        assn=as; 
    }

    public double eval(Environment env) throws EvalException {
	    return assn.eval(env);
    }

}