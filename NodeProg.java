public class NodeProg extends Node {

    private NodeBlock block;

    public NodeProg(NodeBlock b) {
        block = b;
    }

    public double eval(Environment env) throws EvalException {
        //Put the variable and the evaluated value into the environment
	    return block.eval(env);
    }

}