import java.io.FileNotFoundException;
import java.io.IOException;

/******************************
 *  Parser : Parses the File
 *  
 *  Sasha Hedges
 * 
 * ***************************/


public class Parser{
	
	public Lexeme currentLexeme;
	public Lexer lexer;

	
	
	public Parser(String fileName) throws FileNotFoundException{
		lexer = new Lexer(fileName);
	}
	
	public boolean check(String type){
		return currentLexeme.type == type;
	}
	
	public void advance() throws IOException{
		currentLexeme = lexer.lex();
	}
	
	public void match(String type) throws IOException{
		matchNoAdvance(type);
		advance();
	}
	
	public void parse() throws IOException{
		currentLexeme = lexer.lex();
		program();
		match("ENDofINPUT");
	}
	
	public void matchNoAdvance(String type){
		if (!check(type)){
			System.out.println("Error on line "+lexer.lineNumber+" on token "+currentLexeme.display());
		}
	}
	
	public static void main(String[] args) throws IOException {
    	Parser parser = new Parser("redBlack.txt");
    	parser.parse();
    	
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
	
	public boolean exprPending(){
		return primaryPending();
	}
	
	public boolean setVarPending(){
		return check("GRIN");
	}
	
	public boolean primaryPending(){
			return check("VARIABLE") || check("STRING") || check("INTEGER") 
					|| check("REAL") || check("BOOLEAN") || check("MINUS") 
					|| check("NOT")|| check("OPOINTYSMILEY") || functionDefinitionPending() 
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
	
	public boolean optDotPending(){
		return check("DOT");
	}
	
	public boolean lambdaDefinitionPending(){
		return optLambdaArgListPending();
	}
	
	public boolean optLambdaArgListPending(){
		return check("OPOINTYSMILEY");
	}
	

	
	
	
	
	/* functions -------------------------------------*/
	
	// primary
	public void primary() throws IOException{
		
		// INTEGER
		if (check("INTEGER")){
			match("INTEGER");
		}
		
		// REAL
		else if (check("REAL")){
			match("REAL");
		}
		
		// STRING
		else if (check("STRING")){
			match("STRING");
		}
		
		// BOOLEAN
		else if (check("BOOLEAN")){
			match("BOOLEAN");
		}
		// varExpr
		else if (check("VARIABLE")){
			//System.out.println("HERE in primary");
			variableExpr();
			
		}
		// lambdaDefinition
		else if (lambdaDefinitionPending()){
			lambdaDefinition();
			
		}
		
		else if (functionDefinitionPending()){
			functionDefinition();
		}
		
		// array
		else if(check("OBOXSMILEY")){
			match("OBOXSMILEY");
			argList();
			match("CBOXSMILEY");
		}
		
		// MINUS PRIM
		else if (check("MINUS")){
			match("MINUS");
			primary();
		}
		
		// NOT PRIM
		else if (check("NOT")){
			match("NOT");
			primary();
		}
		
		else if (check("NIL")){
			match("NIL");
		} 
		
		
	}
	
	public void program() throws IOException{
		definition();
		if (definitionPending()){
			program();
		}
	}
	
	public void definition() throws IOException{
		if (varDefinitionPending()){
			varDefinition();
		}
		else if(functionDefinitionPending()){
			functionDefinition();
		}
		else if(functionCallPending()){
			functionCall();
		}

		else if(lambdaDefinitionPending()){
			lambdaDefinition();
		}
		else if (setVarPending()){
			setVar();
		}
	}
	
	public void varDefinition() throws IOException{
		match("SMILE");		
		match("VARIABLE");
		optInit();
		
	}
	
	public void setVar() throws IOException {
		match("GRIN");
		match("VARIABLE");
		if (optDotPending()){
			dot();
		}
		match("EQUALS");
		expr();
		match("ENDLINESMILEY");
	}
	
	public void optInit() throws IOException{
		if (check("ENDLINESMILEY")){
			match("ENDLINESMILEY");
		}
		else if(check("EQUALS")){
			match("EQUALS");
			expr();
			match("ENDLINESMILEY");
		}
	}
	
	
	public void variableExpr() throws IOException{
		match("VARIABLE");
		if (check("OBOXSMILEY")){
			
			match("OBOXSMILEY");
			expr();
			match("CBOXSMILEY");
		}
		
		else if (optArgListPending()){
			optArgList();
		}
		else if (optDotPending()){
			dot();
			
		}
	}
	
	public void dot() throws IOException {
		match("DOT");
		match("VARIABLE");
		if (optDotPending()){
			dot();
		}
	}
	
	public void statement() throws IOException{
		if (varDefinitionPending()){
			varDefinition();
		}
		else if (osmileyStatementsPending()){
			osmileyStatements();
		}
		else if (exprPending()){
			expr();
		}
		else if (functionCallPending()){
			functionCall();
		}
		else if (setVarPending()){
			setVar();
		}
		
	}
	
	public void osmileyStatements() throws IOException{
		match("OSMILEY");
		if (whileLoopPending()){
			whileLoop();
		}
		else if (ifStatementPending()){
			ifStatement();
		}
		else if (functionDefinitionPending2()){
			functionDefinition2();
		}
		match("CSMILEY");
	}

	
	public void statementList() throws IOException{
		if(statementPending()){
			if (!varDefinitionPending() && !osmileyStatementPending() && !setVarPending()){
				statement();
				match("ENDLINESMILEY");
			}
			else{
				statement();
			}
			statementList();
		}
		
		
		
	}
	
	public void operator() throws IOException{
		if (check("PLUS")){
			match("PLUS");
		}
		else if (check("TIMES")){
			match("TIMES");
		}
		else if (check("MINUS")){
			match("MINUS");
		}
		else if (check("DIVIDE")){
			match("DIVIDE");
		}
		else if (check("AND")){
			match("AND");
		}
		else if (check("OR")){
			match("OR");
		}
		else if (check("EQUALS")){
			match("EQUALS");
		}
		else if (check("GTHAN")){
			match("GTHAN");
			if (check("EQUALS")){
				match("EQUALS");
			}
			
		}
		else if (check("LTHAN")){
			match("LTHAN");
			if (check("EQUALSs")){
				match("EQUALS");
			}
		}
		else if (check("NOT")){
			match("NOT");
			match("EQUALS");
		}
	}
	
	public void ifStatement() throws IOException{
		match("IF");
		expr();
		match("QMARK");
		block();
		optElse();
	}
	
	public void optElse() throws IOException{
		if(check("ELSE")){
			match("ELSE");
			block();
		}
	}
	
	public void whileLoop() throws IOException{
		match("WHILE");
		expr();
		match("QMARK");
		block();
	}
	
	public void lambdaDefinition() throws IOException{
		optLambdaArgList();
		block();
		optLambdaCall();

	}
	
	public void optLambdaCall() throws IOException{
		if (optArgListPending()){
			optArgList();
		}
		
	}
	
	
	public void optLambdaArgList() throws IOException{
		
		match("OPOINTYSMILEY");
		if(argListPending()){
			argList();
		}
		match("CPOINTYSMILEY");
	}
	
	public void block() throws IOException{
		match("OSMILEY");
		
		if (statementListPending()){
			statementList();
			match("CSMILEY");
		}
		else if(check("CSMILEY")){
			match("CSMILEY");
		}
	}
	
	public void functionDefinition() throws IOException{
		match("OSMILEY");
		match("SMILEY");
		match("VARIABLE");
		optArgList();
		block();
		match("CSMILEY");
	}
	
	public void functionDefinition2() throws IOException{
		match("SMILEY");
		match("VARIABLE");
		optArgList();
		block();
	}
	
	public void argList() throws IOException{
		expr();
		
		if (exprPending()){
			argList();
		}
	}
	
	public void expr() throws IOException{
		primary();
		if (operatorPending()){
			operator();
			expr();
		}
	}
	
	public void optArgList() throws IOException{
		match("OSMILEY");
		if (check("CSMILEY")){
			match("CSMILEY");
		}
		else if(argListPending()){
			argList();
			match("CSMILEY");
		}
	}
	public void functionCall() throws IOException{
		match("VARIABLE");
		optArgList();
	}
	

		
	
}