import java.io.FileNotFoundException;
import java.io.IOException;

/********************************
 *  ParseTree: creates a program 
 *  			parse tree
 *  
 *  Sasha Hedges
 * 
 * ******************************/


public class ParseTree {
	
	public Lexeme currentLexeme;
	public Lexer lexer;
	public Lexeme parseTree; 
	
	
	public ParseTree(String fileName) throws FileNotFoundException{
		lexer = new Lexer(fileName);
	}
	
	public boolean check(String type){
		return currentLexeme.type == type;
	}
	
	public void advance() throws IOException{
		currentLexeme = lexer.lex();
	}
	
	public Lexeme match(String type) throws IOException{
		matchNoAdvance(type);
		Lexeme now = currentLexeme;
		advance();
		return now;
	}
	
	public Lexeme parse() throws IOException{
		currentLexeme = lexer.lex();
		parseTree = program();
		match("ENDofINPUT");
		return parseTree;
	}
	
	public void matchNoAdvance(String type){
		if (!check(type)){
			System.out.println("Error on line "+lexer.lineNumber+" on token "+currentLexeme.display());
		}
	}
	
	public static void main(String[] args) throws IOException {
    	ParseTree parser = new ParseTree("input.txt");
    	Lexeme parseTree = parser.parse();
    	
	}
	
	

	
/* Pendings --------------------------------------------*/
	
	
	public boolean optArgListPending(){
		return check("OSMILEY");
	}
	
	
	public boolean functionDefinitionPending2(){
		return check("SMILEY");
		
	}
	
	public boolean functionDefinitionPending(){
		return check("OSMILEY");
	}
	
	public boolean setVarPending(){
		return check("GRIN");
	}
	
	public boolean exprPending(){
		return primaryPending();
	}
	
	public boolean primaryPending(){
			return check("VARIABLE") || check("STRING") || check("INTEGER") 
					|| check("REAL") || check("BOOLEAN") || check("MINUS") 
					|| check("NOT")|| check("OPOINTYSMILEY") || check("OSMILEY") 
					|| check("OBOXSMILEY") || check("NIL"); 
	}
	
	public boolean lambdaArgListPending(){
		return exprPending();
	}
	
	
	public boolean varDefinitionPending(){
		return check("SMILE");
	}
	
	public boolean functionCallPending(){
		return check("VARIABLE");
	}
	
	public boolean definitionPending(){
		return varDefinitionPending() || functionDefinitionPending() 
				|| functionCallPending() || setVarPending();
	}
	
	public boolean optInitPending(){
		return check("ENDLINESMILEY") || check("EQUALS");
	}
	
	public boolean whileLoopPending(){
		return check("WHILE");
	}
	
	public boolean ifStatementPending(){
		return check("IF");
		
	}
	
	public boolean osmileyStatementsPending(){
		return check("OSMILEY");
	}
	
	public boolean statementListPending(){
		return statementPending();
	}
	
	public boolean statementPending(){
		return varDefinitionPending() || osmileyStatementPending() 
				|| exprPending() || setVarPending();
	}
	
	public boolean osmileyStatementPending(){
		return check("OSMILEY");
	}
	
	public boolean argListPending(){
		return exprPending();
	}
	
	public boolean operatorPending(){
		return check("PLUS") || check("TIMES") || check("MINUS")
				|| check("DIVIDE") || check("AND") || check("OR")
				|| check("EQUALS") || check("GTHAN") || check("LTHAN")
				|| check("NOT");
	}
	
	
	public boolean lambdaDefinitionPending(){
		return optLambdaArgListPending();
	}
	
	public boolean optLambdaArgListPending(){
		return check("OPOINTYSMILEY");
	}
	
	public boolean optDotPending(){
		return check("DOT");
	}
	
// Parse Tree Functions: --------------------------------------------------
	
	public Lexeme program() throws IOException{
		Lexeme tree = definition();		
		if (definitionPending()){
			tree.right = program();
		}
		return tree;
		
	}
		
	public Lexeme definition() throws IOException{
		
		Lexeme tree = new Lexeme("DEF");
		if (varDefinitionPending()){
			tree.left = varDefinition();
		}
		else if(functionDefinitionPending()){
			tree.left = new Lexeme("FUNCDEF");
			tree.left.right = functionDefinition();
		}
		else if(functionCallPending()){
			tree.left = new Lexeme("FUNCCALL");
			tree.left.right = functionCall();
		}
		else if(lambdaDefinitionPending()){
			tree.left = lambdaDefinition();
		}
		else if (setVarPending()){
			tree.left = setVar();
		}
		else{System.out.println("Error in Definition");}
		return tree;
	}
	
	public Lexeme varDefinition() throws IOException{
		Lexeme tree = new Lexeme("VARDEF");
		tree.right = match("SMILE");
		tree.right.right = match("VARIABLE");
		tree.right.right.right = optInit();
		return tree;
	}
	
	
	public Lexeme optInit() throws IOException{
		Lexeme tree = null ; 
		if (check("ENDLINESMILEY")){
			tree = match("ENDLINESMILEY");
		}
		else if(check("EQUALS")){
			tree = match("EQUALS");
			tree.left = expr(true);
			tree.right = match("ENDLINESMILEY");
		}
		else{System.out.println("Error in optInit");}
		return tree;
	}

	public Lexeme setVar() throws IOException{

		Lexeme tree = new Lexeme("SETVAR");
		match("GRIN");
		if (check("VARIABLE")){
			tree.left = match("VARIABLE");
			if (optDotPending()){
				tree.left = dot(tree.left);
			}
			
		}
		match("EQUALS");
		tree.right = expr(true);
		match("ENDLINESMILEY");
		return tree;
		
	}
	
	public Lexeme dot(Lexeme prev) throws IOException{
		Lexeme tree = match("DOT");
		tree.left = prev;
		tree.right = match("VARIABLE");
		if (optDotPending()){
			tree.right = dot(tree.right);
		}
		return tree;
		
	}
	
	
	
	public Lexeme variableExpr() throws IOException{
		Lexeme tree = match("VARIABLE");
		
		// array call
		if (check("OBOXSMILEY")){
			
			Lexeme temp = tree;
			tree = new Lexeme("ARRAYCALL");
			tree.right = temp;
			match("OBOXSMILEY");
			tree.right.right = expr(true);
			match("CBOXSMILEY");
		}
		
		// functionCall
		else if (optArgListPending()){
			Lexeme temp = tree;
			tree = new Lexeme("FUNCCALL");
			tree.right = temp;
			tree.right.right = optArgList();
		}
		
		else if (optDotPending()){
			tree = dot(tree);
		}
		
		//else {System.out.println("Error in variableExpr");}
		return tree;
		
	}
	
	public Lexeme statement() throws IOException{
		Lexeme tree = new Lexeme("STATEMENT");
		if (varDefinitionPending()){
			tree.left = varDefinition();
		}
		else if (osmileyStatementsPending()){
			tree.left = new Lexeme("OSMILEYS");
			tree.left.right = osmileyStatements();
		}
		else if (exprPending()){
			tree.left = expr(true);
		}
		else if (setVarPending()){
			tree.left = setVar();
		}
		else {System.out.println("Error in statement");}
		return tree;
		
	}
	
	public Lexeme osmileyStatements() throws IOException{
		Lexeme tree = null;
		match("OSMILEY");
		if (whileLoopPending()){
			tree = new Lexeme("WHILE");
			tree.right = whileLoop();
		}
		else if (ifStatementPending()){
			tree = new Lexeme("IF");
			tree.right = ifStatement();
		}
		else if (functionDefinitionPending2()){
			tree = new Lexeme("FUNCDEF");
			tree.right = functionDefinition2();
		}
		else {System.out.println("Error in osmileyStatement");}
		match("CSMILEY");
		return tree;
	}
	
	public Lexeme statementList() throws IOException{
		Lexeme tree = null;
		if(statementPending()){
			if (!varDefinitionPending() && !osmileyStatementPending() && !setVarPending()){
				tree = statement();
				match("ENDLINESMILEY");
				tree.right = statementList();
			}
			else{
				tree = statement();
				tree.right = statementList();
			}

			
		}
		return tree;
	}
	
	public Lexeme operator() throws IOException{
		Lexeme tree = null;
		if (check("PLUS")){
			tree = match("PLUS");
		}
		else if (check("TIMES")){
			tree = match("TIMES");
		}
		else if (check("MINUS")){
			tree = match("MINUS");
		}
		else if (check("DIVIDE")){
			tree = match("DIVIDE");
		}
		else if (check("AND")){
			tree = match("AND");
		}
		else if (check("OR")){
			tree = match("OR");
		}
		else if (check("EQUALS")){
			tree = match("EQUALS");
		}
		else if (check("GTHAN")){
			tree = match("GTHAN");
			if (check("EQUALS")){
				match("EQUALS");
				tree = new Lexeme("GTOE");
			}
			
		}
		else if (check("LTHAN")){
			tree = match("LTHAN");
			if (check("EQUALS")){
				match("LTHAN");
				match("EQUALS");
				tree = new Lexeme("LTOE");
			}
			
		}
		else if (check("NOT")){
			tree = new Lexeme("NOTEQUALS");
			match("NOT");
			match("EQUALS");
		}
		else {System.out.println("Error in operator");}
		return tree;
	}
	
	public Lexeme ifStatement() throws IOException{		
		match("IF");
		Lexeme tree = expr(true);
		match("QMARK");
		tree.right = new Lexeme("JOIN");
		tree.right.left = block();
		tree.right.right = optElse();
		return tree;
		
	}
	
	public Lexeme optElse() throws IOException{
		
		Lexeme tree = null;
		if(check("ELSE")){
			match("ELSE");
			tree = block();
		}
		return tree;
	}
	
	public Lexeme whileLoop() throws IOException{
		
		match("WHILE");
		Lexeme tree = expr(true);
		match("QMARK");
		tree.right = block();
		return tree;
		
	}
	
	public Lexeme lambdaDefinition() throws IOException{
		Lexeme tree = new Lexeme("LAMBDEF"); 
		tree.right = new Lexeme("JOIN");
		tree.right.left = optLambdaArgList();
		tree.left = block();
		tree.right.right = optLambdaCall();
		return tree;
	}
	
	public Lexeme optLambdaCall() throws IOException{
		
		Lexeme tree = null;
		if (optArgListPending()){
			tree = optArgList();
		}
		return tree;
	}
	
	public Lexeme optLambdaArgList() throws IOException{
		boolean flag = true;
		Lexeme tree = null;
		match("OPOINTYSMILEY");
		if(argListPending()){
			tree = new Lexeme("NOTEMPTY");
			tree.right = argList();
			match("CPOINTYSMILEY");
			flag = false;
		}
		if (flag){
			tree = new Lexeme("EMPTY");
			match("CPOINTYSMILEY");
		}
		return tree;
	}
	
	public Lexeme block() throws IOException{
		Lexeme tree = new Lexeme("BLOCK");
		match("OSMILEY");
		if (statementListPending()){
			tree.right = statementList();
			match("CSMILEY");
		}
		else if(check("CSMILEY")){
			match("CSMILEY");
		}
		return tree;
	}
	
	public Lexeme functionDefinition() throws IOException{
		
		match("OSMILEY");
		match("SMILEY");
		Lexeme tree = match("VARIABLE");
		//System.out.println(tree.string);
		tree.left = optArgList();
		tree.right = block();
		match("CSMILEY");
		return tree;
	}
	
	public Lexeme functionDefinition2() throws IOException{
		match("SMILEY");
		Lexeme tree = match("VARIABLE");
		tree.left = optArgList();
		tree.right = block();
		return tree;
	}
	
	public Lexeme argList() throws IOException{
		Lexeme tree = expr(true);
		if (exprPending()){
			tree.right = argList();
		}
		return tree;
	}
	
	
	public Lexeme expr(boolean first) throws IOException{		
		Lexeme tree = primary();
		if (operatorPending()){
			Lexeme temp = tree;
			tree = operator();
			tree.left = temp;
			tree.right = expr(false);
		}
		
		if (first) {
			Lexeme expr = new Lexeme("EXPR");
			expr.left = tree; 
			return expr;
		}
		else return tree;
	}
	
	public Lexeme optArgList() throws IOException{
		Lexeme tree = null;
		match("OSMILEY");
		if (check("CSMILEY")){
			match("CSMILEY");
			tree = new Lexeme("EMPTY");
		}
		else if(argListPending()){
			tree = new Lexeme("NOTEMPTY");
			tree.right = argList();
			match("CSMILEY");
		}
		return tree;
	}
	
	public Lexeme functionCall() throws IOException{
		Lexeme tree = match("VARIABLE"); 
		tree.right = optArgList();
		return tree;
	}
	
	
	public Lexeme primary() throws IOException{
		Lexeme tree = null;
		// INTEGER
		if (check("INTEGER")){
			tree = match("INTEGER");
		}
		
		// REAL
		else if (check("REAL")){
			tree = match("REAL");
		}
		
		// STRING
		else if (check("STRING")){
			tree = match("STRING");
		}
		
		// BOOLEAN
		else if (check("BOOLEAN")){
			tree = match("BOOLEAN");
		}
		// varExpr
		else if (check("VARIABLE")){
			tree = new Lexeme("VAREXPR");
			tree.right = variableExpr();
		}
		// lambdaDefinition
		else if (lambdaDefinitionPending()){
			tree = lambdaDefinition();
			
		}
		

		else if (functionDefinitionPending()){
			tree = new Lexeme("FUNCDEF");
			tree.right = functionDefinition();
		}
		
		// array
		else if(check("OBOXSMILEY")){
			tree = match("OBOXSMILEY");
			tree.right = argList();
			match("CBOXSMILEY");
		}
		
		// MINUS PRIM
		else if (check("MINUS")){
			tree = match("MINUS");
			tree.type = "NEGATE";
			tree.right = primary();
		}
		
		// NOT PRIM
		else if (check("NOT")){
			tree = match("NOT");
			tree.right = primary();
		}
		
		else if (check("NIL")){
			tree = match("NIL");
		}
		else {
			System.out.println("Error in Primary");
			System.out.println(currentLexeme.display());
		}
		
		return tree;
		
	}	
}
