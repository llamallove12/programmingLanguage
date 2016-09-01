import java.io.IOException;

/****************************
 *  Environment: 
 *  
 *  Sasha Hedges
 * 
 * **************************/


public class Environment {
	
	public Environment(){
	}
	
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
	
	public Lexeme create(){
		return extend(null,null,null);
	}
	
	public boolean sameVariable(Lexeme variable, Lexeme vars){
		return (variable.string.equals(vars.string));
	}
	
	
	public Lexeme lookup(Lexeme variable, Lexeme env) {
		while (env != null){
			Lexeme vars = car(env);
			Lexeme vals = car(cdr(env));
			while (vars != null){
				if(sameVariable(variable,car(vars))){
					return car(vals);
				}
				vars = cdr(vars);
				vals = cdr(vals);
			}
			env = cdr(cdr(env));
		}
		return null;
				
	}
	
	public Lexeme update(Lexeme variable, Lexeme env, Lexeme newVal) throws Exception{
		while (env != null){
			Lexeme vars = car(env);
			Lexeme vals = car(cdr(env));
			while (vars != null){
				if(sameVariable(variable,car(vars))){
					setCar(vals, newVal);
					return car(vals);
				}
				vars = cdr(vars);
				vals = cdr(vals);
			}
			env = cdr(cdr(env));
		}
		System.out.println("Variable not found: "+variable.display());
		throw new Exception();
		
	}
	
	
	public Lexeme insert(Lexeme variable, Lexeme value, Lexeme env){
		setCar(env, cons("JOIN", variable,car(env)));
		setCar(cdr(env), cons("JOIN",value,car(cdr(env))));
		return value;
	}
	
	public Lexeme extend(Lexeme variables, Lexeme values, Lexeme env){
		return cons("ENV",variables, cons("ENV", values,env));
	}
	
	public void displayLocal(Lexeme local){
		System.out.printf("");
	}
	
	public static void main(String[] args) throws IOException {
    	Environment env = new Environment();
    	Lexeme global = env.create();
    	Lexeme variable = new Lexeme("VARIABLE","X");
    	Lexeme value = new Lexeme(1);
    	env.insert(variable, value, global);
    	Lexeme vars = new Lexeme("JOIN");
    	vars.left = new Lexeme("VARIABLE","x");
    	Lexeme vals = new Lexeme("JOIN"); 
    	vals.left = new Lexeme (1);
    	
	}
	
	
	
}
