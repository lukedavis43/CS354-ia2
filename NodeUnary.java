/**
 * This is a class to help with unary minus identification
 * @author austinplatt
 *
 */
public class NodeUnary extends Node{
    private String sign;

    public NodeUnary(String sign) {
	    this.sign = sign;
    }

    public double eval(Environment env) throws EvalException {
        return env.put(sign, pos);
    }

}