/****************************
 *  Type: Determines the Type
 *  		of a Lexeme
 *  
 *  Sasha Hedges
 * 
 * **************************/


public class Type{
	String type;
	public Type(int val){
		type = "INTEGER";
	}
	public Type(Double real){
		type = "REAL";
	}
	public Type(String string){
		type = types(string);
	}
	
	public String types(String val){
		
		if (val.equals("OPAREN")){
			return val;
		}
		else if (val.equals("CPAREN")){
			return val;
		}
		else if (val.equals("COLON")){
			return val;
		}
		
		else if (val.equals("OSMILEY")){
			return val;
		}
		else if (val.equals("CSMILEY")){
			return val;
		}
		else if (val.equals("OBOXSMILEY")){
			return val;
		}
		else if (val.equals("CBOXSMILEY")){
			return val;
		}
		else if (val.equals("OPOINTYSMILEY")){
			return val;
		}
		else if (val.equals("CPOINTYSMILEY")){
			return val;
		}
		else if (val.equals("ENDLINESMILEY")){
			return val;
		}
		else if (val.equals("QMARK")){
			return val;
		}
		else if (val.equals("SEMI")){
			return val;
		}
		else if (val.equals("GTHAN")){
			return val;
		}
		else if (val.equals("OBRACKET")){
			return val;
		}
		else if (val.equals("CBRACKET")){
			return val;
		}
		else if (val.equals("PLUS")){
			return val;
		}
		else if (val.equals("MINUS")){
			return val;
		}
		else if (val.equals("TIMES")){
			return val;
		}
		else if (val.equals("DIVIDE")){
			return val;
		}
		else if (val.equals("EQUALS")){
			return val;
		}
		else if (val.equals("ENDofINPUT")){
			return val;
		}
		else if (val.equals("WHILE")){
			return val;
		}
		else if (val.equals("IF")){
			return val;
		}
		else if (val.equals("NOT")){
			return val;
		}
		else if (val.equals("GRIN")){
			return val;
		}
		else if (val.equals("ELSE")){
			return val;
		}
		
		else if (val.equals("SMILE")){ // VARIABLE DEFINE
			return val;
		}
		else if (val.equals("SMILEY")){ // FUNTION DEFINE
			return val;
		}
		else if (val.equals("LTHAN")){ 
			return val;
		}
		else if (val.equals("AND")){
			return val;
		}
		else if (val.equals("OR")){ 
			return val;
		}
		else if (val.equals("ENV")){
			return val;
		}
		else if (val.equals("VALUES")){
			return val;
		}
		else if (val.equals("JOIN")){
			return val;
		}
		else if (val.equals("BUILTIN")){
			return val;
		}
		else if (val.equals("CLOSURE")){
			return val;
		}
		else if(val.equals("VARDEF")){
			return val;
		}
		else if(val.equals("ARRAYCALL")){
			return val;
		}
		else if(val.equals("FUNCCALL")){
			return val;
		}
		else if(val.equals("EMPTY")){
			return val;
		}
		else if(val.equals("NOTEMPTY")){
			return val;
		}
		else if(val.equals("EXPR")){
			return val;
		}
		else if(val.equals("LOOKUP")){
			return val;
		}
		else if(val.equals("FUNCDEF")){
			return val;
		}
		else if(val.equals("STATEMENT")){
			return val;
		}
		else if(val.equals("OSMILEYS")){
			return val;
		}
		else if(val.equals("VAREXPR")){
			return val;
		}
		else if(val.equals("LAMBDEF")){
			return val;
		}
		else if(val.equals("NEGATE")){
			return val;
		}
		else if(val.equals("LTOE")){
			return val;
		}
		else if(val.equals("GTOE")){
			return val;
		}
		else if(val.equals("DEF")){
			return val;
		}
		else if(val.equals("BLOCK")){
			return val;
		}
		else if(val.equals("SETVAR")){
			return val;
		}
		else if (val.equals("NOTEQUALS")){
			return val;
		}
		else if (val.equals("DOT")){
			return val;
		}
		else if (val.equals("NIL")){
			return val;
		}
		else{
			return "NOT HERE...";
		}
	}
	
}
