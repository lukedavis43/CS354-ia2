public class NodeBoolExpr extends Node {

    private NodeExpr o1, o2;
    private NodeRelop relop;

    public NodeBoolExpr(NodeExpr o1, NodeRelop rl, NodeExpr o2) {
        this.o1 = o1;
		this.o2 = o2;
		relop = rl;
    }

    public double eval(Environment env) throws EvalException {
		//Evaluate the expression. 7.0 is true 0.0 is false
	    return relop.op(o1.eval(env), o2.eval(env));
    }

}