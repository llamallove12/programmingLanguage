import java.util.ArrayList;


/******************************
 *  Evaluator: Evaluates the 
 *  			SMyLang file
 *  
 *  Sasha Hedges
 * 
 * ****************************/

public class Evaluator {
	
	public Lexeme currentLexeme;
	public Lexer lexer;
	public Environment envOB = new Environment();
	public Lexeme global = envOB.create();
	public Evaluator(){}
	
	public Lexeme cons(String type, Lexeme left, Lexeme right){
		Lexeme lex = new Lexeme(type);
		lex.left = left;
		lex.right = right;
		return lex;
	}
	
	public Lexeme car(Lexeme list){
		return list.left;
	}
	
	public Lexeme cdr(Lexeme list){
		return list.right;
	}
	
	public Lexeme setCar(Lexeme list, Lexeme value){
		list.left = value;
		return list.left;
	}
	
	public Lexeme setCdr(Lexeme list, Lexeme value){
		list.right = value;
		return list.right;
	}
	
	
	public Lexeme eval(Lexeme tree, Lexeme env) throws Exception{
		
		switch (tree.type){
			case "PLUS": return evalPlus(tree,env); 
			case "MINUS": return evalMinus(tree,env);
			case "TIMES": return evalTimes(tree,env);
			case "DIVIDE": return evalDivide(tree,env);
			case "REAL": return tree;
			case "BOOLEAN": return tree;
			case "GTHAN" : return evalGthan(tree,env);
			case "LTHAN" : return evalLthan(tree,env);
			case "EQUALS" : return evalEquals(tree,env);
			case "GTOE" : return evalGTOE(tree,env);
			case "LTOE" : return evalLTOE(tree,env);
			case "STRING" : return tree;
			case "LAMBDEF" : return evalLambDef(tree,env);
			case "FUNCDEF" : return evalFuncDef(tree,env);
			case "OBOXSMILEY": return evalOBoxSmiley(tree.right,env);
			case "NEGATE" : return evalNegate(tree,env);
			case "NOT" : return evalNot(tree,env);
			case "AND" : return evalAnd(tree,env);
			case "OR" : return evalOr(tree,env);
			case "NOTEQUALS" : return evalNotEquals(tree,env);
			case "SMILE": return evalAssign(tree,env);
			case "INTEGER": return tree;
			case "VARIABLE": return envOB.lookup(tree,env);
			case "VAREXPR" : return evalVarExpr(tree.right,env);
			case "NIL" : return null;
			
		}		
		return null;
	}
	
	public Lexeme evalProgram(Lexeme t, Lexeme env) throws Exception{
		while(t!=null){
			evalDef(t.left,env);
			t = t.right;
		}
		return null;
	}
	
	public Lexeme evalDef(Lexeme t, Lexeme env) throws Exception{
		if(t.type.equals("VARDEF")){
			return evalAssign(t.right,env);
		}
		else if(t.type.equals("FUNCCALL")){
			return evalFuncCall(t.right,env);
		}
		else if(t.type.equals("FUNCDEF")){
			return evalFuncDef(t.right,env);
		}
		else if(t.type.equals("LAMBDEF")){
			return evalLambDef(t,env);
		}
		else if (t.type.equals("SETVAR")) {return evalSetVar(t,env);}
		return null;
	}
	
	public Lexeme evalSetVar(Lexeme t, Lexeme env) throws Exception{
		Lexeme value = eval(t.right.left,env);
		if (t.left.type.equals("VARIABLE")){
			envOB.update(t.left, env, value);
		}
		else if (t.left.type.equals("DOT")){
			Lexeme object = eval(t.left.left,env);
			Lexeme right = t.left.right;
			while (right.type.equals("DOT")){
				object = eval(right.left,object);
				right = right.right;
			}
			envOB.update(right, object, value);
		}
		
		return value;
		
	}
	
	public Lexeme evalSimpleOp(Lexeme t, Lexeme env) throws Exception{		
		if (t.type.equals("PLUS")) return evalPlus(t,env);
		if (t.type.equals("MINUS")) return evalMinus(t,env);
		return null;
	}
	
	public Lexeme evalLambDef(Lexeme t, Lexeme env) throws Exception{
		Lexeme closure = new Lexeme("CLOSURE");
		closure.left = env; //env
		closure.right = new Lexeme("JOIN"); //Var name
		closure.right.right = t.left; //body
		closure.right.left = t.right.left; // params
		
		if(t.right.right == null) return closure;
		else return evalLambCall(t,closure,env);
		
		
	}
	
	public Lexeme evalLambCall(Lexeme t, Lexeme closure, Lexeme env) throws Exception{
		Lexeme args = t.right.right; 
		Lexeme params = convertParams(closure.right.left.right);
		Lexeme senv = closure.left;		
		Lexeme eargs = evalOptArgList(args,env);
		Lexeme xenv = envOB.extend(params, eargs, senv); 
		return evalBlock(closure.right.right,xenv);
	}
	
	public Lexeme evalPlus (Lexeme t, Lexeme env) throws Exception{
		Lexeme left = eval(t.left,env);
		Lexeme right = eval(t.right,env);
		if (left.type.equals("INTEGER")&&right.type.equals("INTEGER")){
			Lexeme intLex = new Lexeme(left.integer+right.integer);
			return intLex;
		}
		else if (left.type.equals("REAL") && right.type.equals("REAL")){
			Lexeme realLex = new Lexeme(left.real + right.real);
			return realLex;
		}
		else {System.out.println("NAH");}
		return null;
	}
	
	public Lexeme evalMinus (Lexeme t, Lexeme env) throws Exception{
		Lexeme left = eval(t.left,env);
		Lexeme right = eval(t.right,env);
		if (left.type.equals("INTEGER")&&right.type.equals("INTEGER")){
			Lexeme intLex = new Lexeme(left.integer-right.integer);
			return intLex;
		}
		else if (left.type.equals("REAL") && right.type.equals("REAL")){
			Lexeme realLex = new Lexeme(left.real - right.real);
			return realLex;
		}
		else {System.out.println("NAH");}
		return null;
	}
	
	public Lexeme evalTimes (Lexeme t, Lexeme env) throws Exception{
		Lexeme left = eval(t.left,env);
		Lexeme right = eval(t.right,env);
		if (left.type.equals("INTEGER")&&right.type.equals("INTEGER")){
			Lexeme intLex = new Lexeme(left.integer*right.integer);
			return intLex;
		}
		else if (left.type.equals("REAL") && right.type.equals("REAL")){
			Lexeme realLex = new Lexeme(left.real * right.real);
			return realLex;
		}
		else {System.out.println("NAH");}
		return null;
	}
	
	public Lexeme evalDivide (Lexeme t, Lexeme env) throws Exception{
		Lexeme left = eval(t.left,env);
		Lexeme right = eval(t.right,env);
		if (left.type.equals("INTEGER")&&right.type.equals("INTEGER")){
			Lexeme intLex = new Lexeme(left.integer/right.integer);
			return intLex;
		}
		else if (left.type.equals("REAL") && right.type.equals("REAL")){
			Lexeme realLex = new Lexeme(left.real / right.real);
			return realLex;
		}
		else {System.out.println("NAH");}
		return null;
	}
	
	public Lexeme evalGthan(Lexeme t, Lexeme env) throws Exception{
		Lexeme left = eval(t.left,env);
		Lexeme right = eval(t.right,env);
		if (left.type.equals("INTEGER")&&right.type.equals("INTEGER")){
			return new Lexeme("BOOLEAN",left.integer > right.integer);
		}
		else if (left.type.equals("REAL")&&right.type.equals("REAL")){
			return new Lexeme("BOOLEAN",left.real > right.real);
		}
		else {System.out.println("ERROR IN GTHAN");}
		return null;
	}
	
	public Lexeme evalLthan(Lexeme t, Lexeme env) throws Exception{
		Lexeme left = eval(t.left,env);
		Lexeme right = eval(t.right,env);
		if (right==null){
			System.out.println(left.display());
		}
		
		if (left.type.equals("INTEGER")&&right.type.equals("INTEGER")){
			return new Lexeme("BOOLEAN",left.integer < right.integer);
		}
		else if (left.type.equals("REAL")&&right.type.equals("REAL")){
			return new Lexeme("BOOLEAN",left.real < right.real);
		}
		else {System.out.println("ERROR IN LTHAN");}
		return null;
	}
	
	public Lexeme evalEquals(Lexeme t, Lexeme env) throws Exception{
		Lexeme left = eval(t.left,env);
		Lexeme right = eval(t.right,env);
//		if (left!=null && right!=null){
//		System.out.println(left.display());
//		System.out.println(right.display()+"\n");}
		
//		System.out.println(left);
//		System.out.println(right+"\n");
		
		if (right == null){
			return new Lexeme("BOOLEAN", left == right);
		}
		else if (left.type.equals("INTEGER")&&right.type.equals("INTEGER")){
			return new Lexeme("BOOLEAN",left.integer == right.integer);
		}
		else if (left.type.equals("REAL")&&right.type.equals("REAL")){
			return new Lexeme("BOOLEAN",left.real == right.real);
		}
		else if (left.type.equals("STRING")&&right.type.equals("STRING")){
			return new Lexeme("BOOLEAN",left.string.equals(right.string));
		}
		else {System.out.println("ERROR IN EQUAL");}
		return null;
	}
	
	public Lexeme evalGTOE(Lexeme t, Lexeme env) throws Exception{
		Lexeme left = eval(t.left,env);
		Lexeme right = eval(t.right,env);
		if (left.type.equals("INTEGER")&&right.type.equals("INTEGER")){
			return new Lexeme("BOOLEAN",left.integer >= right.integer);
		}
		else if (left.type.equals("REAL")&&right.type.equals("REAL")){
			return new Lexeme("BOOLEAN",left.real >= right.real);
		}
		else {System.out.println("ERROR IN GTOE");}
		return null;
	}
	
	public Lexeme evalLTOE(Lexeme t, Lexeme env) throws Exception{
		Lexeme left = eval(t.left,env);
		Lexeme right = eval(t.right,env);
		if (left.type.equals("INTEGER")&&right.type.equals("INTEGER")){
			return new Lexeme("BOOLEAN",left.integer <= right.integer);
		}
		else if (left.type.equals("REAL")&&right.type.equals("REAL")){
			return new Lexeme("BOOLEAN",left.real <= right.real);
		}
		else {System.out.println("ERROR IN LTOE");}
		return null;
	}
	
	public Lexeme evalNotEquals(Lexeme t, Lexeme env) throws Exception{
		Lexeme left = eval(t.left,env);
		Lexeme right = eval(t.right,env);
		if (right == null){
			return new Lexeme("BOOLEAN", left != right);
		}
		else if (left.type.equals("INTEGER")&&right.type.equals("INTEGER")){
			return new Lexeme("BOOLEAN",left.integer != right.integer);
		}
		else if (left.type.equals("REAL")&&right.type.equals("REAL")){
			return new Lexeme("BOOLEAN",left.real != right.real);
		}
		
		else {System.out.println("ERROR IN LTOE");}
		return null;
	}
	
	public Lexeme evalNot(Lexeme t, Lexeme env) throws Exception{
		Lexeme value = eval(t.right,env);
		return new Lexeme ("BOOLEAN",!value.bool);
	}
	
	public Lexeme evalAnd(Lexeme t, Lexeme env) throws Exception{
		Lexeme right = eval(t.right,env);
		Lexeme left = eval(t.left,env);
		return new Lexeme ("BOOLEAN",right.bool && left.bool);
	}
	
	public Lexeme evalOr(Lexeme t, Lexeme env) throws Exception{
		Lexeme right = eval(t.right,env);
		Lexeme left = eval(t.left,env);
		return new Lexeme ("BOOLEAN",right.bool || left.bool);
	}
	
	public Lexeme evalNegate(Lexeme t, Lexeme env) throws Exception{
		Lexeme value = eval(t.right,env);
		if (value.type.equals("INTEGER")){
			return new Lexeme (value.integer*-1);
		}
		else if (value.type.equals("REAL")){
			return new Lexeme(value.real*-1);
		}
		return null;
		
	}
	
	public Lexeme evalOBoxSmiley(Lexeme t, Lexeme env) throws Exception{
		ArrayList<Lexeme> array = new ArrayList<Lexeme>();
		while (t!=null){
			Lexeme value = eval(t.left,env);
			array.add(value);
			t = t.right;
		}
		return new Lexeme("ARRAY",array);
		
	}
	
	public Lexeme evalArrayCall(Lexeme t, Lexeme env) throws Exception{
		Lexeme array = eval(t,env);
		Lexeme index = eval(t.right.left,env);
		return array.array.get(index.integer);
		
	}
	
	public Lexeme evalVarExpr(Lexeme t, Lexeme env) throws Exception{
		if (t.type.equals("FUNCCALL")) return evalFuncCall(t.right,env);
		else if (t.type.equals("ARRAYCALL"))return evalArrayCall(t.right,env); 
		else if (t.type.equals("VARIABLE")) return eval(t,env);
		else if (t.type.equals("DOT")) return evalDot(t,env);
		else {System.out.println("Error in evalVarExpr");}
		return null;
		
	}
	
	public Lexeme evalDot(Lexeme t, Lexeme env) throws Exception{
		Lexeme object = eval(t.left,env);
		Lexeme right = t.right;
		while (right.type.equals("DOT")){
			object = eval(right.left,object);
			right = right.right;
		}
		return eval(right,object);
	}
	
	public Lexeme evalAssign(Lexeme t, Lexeme env) throws Exception{
		Lexeme value = eval(t.right.right.left.left,env);
		if (envOB.lookup(t.right, env)==null){
			envOB.insert(t.right,value,env);
		}
		else{
			envOB.update(t.right, env,value);
		}			
		return env;
	}
	
	public Lexeme evalFuncDef(Lexeme t,Lexeme env){
		Lexeme closure = cons("CLOSURE",env,t);
		envOB.insert(t,closure, env);
		return closure;
	}
	
	public Lexeme evalFuncCall(Lexeme t, Lexeme env) throws Exception{
		Lexeme var = new Lexeme("VARIABLE",t.string);
		//System.out.println(var.string);
		Lexeme closure = eval(var,env);
		//System.out.println(var.display());
		if (closure.type.equals("BUILTIN")){
			//System.out.println(var.display());
			return evalBuiltin(var,env,t.right.right);
		}
		Lexeme args = t.right; 
		Lexeme params = convertParams(closure.right.left.right);
		Lexeme senv = closure.left;		
		Lexeme eargs = evalOptArgList(args,env);
		Lexeme xenv = envOB.extend(params, eargs, senv); 
		envOB.insert(new Lexeme("VARIABLE","this"), xenv,xenv);		
		//System.out.println();
		return evalBlock(closure.right.right,xenv);
		
	}
	
	//optarglist goes in here
	public Lexeme evalOptArgList(Lexeme t, Lexeme env) throws Exception{
		if(t.string.equals("EMPTY")){ 
			return null;
		}
		else if (t.string.equals("NOTEMPTY")){
			//arglist
			Lexeme args = t.right;
			Lexeme result = null;
			while(args!=null){
				Lexeme expr = args.left;
				result = append(result,eval(expr,env));
				args = args.right;
			}
			return result;
		}
		return null;
	}
	
	
	public Lexeme evalBlock(Lexeme t, Lexeme env) throws Exception{		
		return evalStatementList(t.right,env);
		
	}
	
	public Lexeme evalStatementList(Lexeme t, Lexeme env) throws Exception{
		Lexeme result= null;
		while(t!=null){
			result = evalStatement(t.left,env);
			t = t.right;
		}
		return result;
		
	}
		
	public Lexeme evalStatement(Lexeme t, Lexeme env) throws Exception{
		if (t.type.equals("EXPR")) return eval(t.left,env);
		else if (t.type.equals("OSMILEYS")) return evalOsmiley(t.right,env);
		else if (t.type.equals("VARDEF")) return evalAssign(t.right, env);
		else if (t.type.equals("SETVAR")) return evalSetVar(t,env);
		return null;
	}
	
	public Lexeme evalOsmiley(Lexeme t, Lexeme env) throws Exception{
		if (t.type.equals("WHILE")) return evalWhile(t.right,env);
		else if (t.type.equals("IF")) return evalIf(t.right,env);
		else if (t.type.equals("FUNCDEF")) return evalFuncDef(t.right,env);
		else System.out.println("ERROR IN EVALOSMILEY:");
		return null;
	}
	
	public Lexeme evalWhile(Lexeme t, Lexeme env) throws Exception{
		Lexeme expr = t;
		while(eval(expr.left,env).bool){
			evalBlock(t.right,env);
		}
		return null;
	}
	
	public Lexeme evalIf(Lexeme t, Lexeme env) throws Exception{
		if (eval(t.left,env).bool){
			return evalBlock(t.right.left,env);
		}
		else{
			if (t.right.right != null){
				return evalBlock(t.right.right,env);
			}
		}
		return null;
		
	}
	
	
	public Lexeme append(Lexeme lyst, Lexeme val){
		Lexeme join = new Lexeme("JOIN");
		join.left = val;
		if (lyst != null){
			join.right = lyst;
		}
		return join;
	}

	public Lexeme convertParams(Lexeme params){
		Lexeme result = null;
		while (params!=null){
			result = append(result,params.left.right);
			params = params.right;
		}
		return result;
	}
	
	
	public Lexeme evalBuiltin(Lexeme funcName, Lexeme env, Lexeme args) throws Exception{
		String name = funcName.string;
		if (name.equals("laugh")){
			evalLaugh(args.left,env);
		}
		return null;
		
	}
	
	public void evalLaugh(Lexeme args, Lexeme env) throws Exception{		
		if (args.type.equals("STRING")){
			System.out.println(args.string);
		}
		else if (args.type.equals("INTEGER")){
			System.out.println(args.integer);
		}
		else if (args.type.equals("REAL")){
			System.out.println(args.real);
		}
		else if (args.type.equals("NIL")){
			System.out.println("null");
		}
		else if (args.right.type.equals("VARIABLE")){
			//System.out.println("HERE "+args.right.display());
			
			System.out.println(eval(args.right,env).printVal());
		}
		else if (args.type.equals("BOOLEAN")){
			System.out.println(args.bool);
		}
		
		
		
	}
	
	public static void main(String[] args) throws Exception {
    	Evaluator evals = new Evaluator();
    	ParseTree parser = new ParseTree(args[0]);
    	Environment env = new Environment();    	
    	Lexeme global = env.create();
    	Lexeme parseTree = parser.parse();  	
    	Lexeme bi = new Lexeme("BUILTIN");
    	bi.right = new Lexeme("VAIRABLE","laugh");
    	env.insert(new Lexeme("VARIABLE","laugh"),bi,global);  
    	evals.evalProgram(parseTree, global);
    	
    	
    }	
}

