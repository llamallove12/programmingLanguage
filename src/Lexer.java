
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PushbackInputStream;

/****************************
 *  Lexer : Lex the file
 *  
 *  Sasha Hedges
 * 
 * **************************/



public class Lexer
{
	PushbackInputStream file;
	int lineNumber = 1;
    
	public Lexer(String fileName) throws FileNotFoundException
    {
		file = new PushbackInputStream(new FileInputStream(fileName));
    }
	
	// returns the corresponding lexeme
    public Lexeme lex() throws IOException{

		char ch;
    	
		// skips spaces, tabs, and new lines
		skipWhiteSpace();
    	
    	int r = file.read();
		if (r == -1 || r == 255) {
    		return new Lexeme("ENDofINPUT");
    	}
		
    	ch = (char) r;
    	
    	// all of the built in values used in the language
		switch(ch){
			case '(':
				ch = (char) file.read();
				if (ch == ':'){
					return new Lexeme("CSMILEY");
				}
				return new Lexeme("OPAREN");
			case ')':
				return new Lexeme("CPAREN");
			case ':':
				ch = (char) file.read();
				if (ch == ')'){
					return new Lexeme("OSMILEY");
				}
				else if(ch == ']'){
					return new Lexeme("OBOXSMILEY");
				}
				else if(ch == '>'){
					return new Lexeme("OPOINTYSMILEY");
				}
				else if(ch == '|'){
					return new Lexeme("ENDLINESMILEY");
				}
				else{
					return new Lexeme("COLON");
				}
			case '?':
				return new Lexeme("QMARK");
			case ';':
				return new Lexeme("SEMI");
				
			case '>':
				return new Lexeme("GTHAN");
			case '<':
				ch = (char) file.read();
				if (ch == ':'){
					return new Lexeme("CPOINTYSMILEY");
				}
				file.unread(ch);
				return new Lexeme("LTHAN");	
			case '[':
				ch = (char) file.read();
				if (ch == ':'){
					return new Lexeme("CBOXSMILEY");
				}
				return new Lexeme("OBRACKET");
			case ']':
				return new Lexeme("CBRACKET");
			case '+':
				return new Lexeme("PLUS");
			case '-':
				return new Lexeme("MINUS");	
			case '*':
				return new Lexeme("TIMES");	
			case '.':
				return new Lexeme("DOT");
			case '/':
				return new Lexeme("DIVIDE");
			case '=':
				return new Lexeme("EQUALS");
			case '}':
				return new Lexeme("CQ");
			case '{':
				return new Lexeme("OQ");
			case '!':
				return new Lexeme("NOT");	
			case '&':
				return new Lexeme("AND");
			case '|':
				return new Lexeme("OR");
			default:
				// if it's a number
				if (Character.isDigit(ch)){
					file.unread(ch);
					return lexNumber();
				}
				// if it's a word
				else if (Character.isAlphabetic(ch)){
					file.unread(ch);
					return lexWord();
				}
				// if its a string
				else if (ch == '\"'){
					return lexString();
				}
		}
		return null;
		
	}
    
    // returns the entire number lexeme
    public Lexeme lexNumber() throws IOException{
    	
    	char ch = (char) file.read();
    	String buffer = "";
    	int realFlag= 0; // 0 if int, 1 if real
    	while (Character.isDigit(ch) || ch == '.'){
    		if (ch == '.'){
    			realFlag =1;
    		}
    		buffer+=ch;
    		ch = (char) file.read();
    	}
    	file.unread(ch);
    	if (realFlag == 0){
    		return new Lexeme(Integer.parseInt(buffer));
    	}
    	else{
    		return new Lexeme(Double.parseDouble(buffer));
    	}
    }
    
    // returns the entire word lexeme
    public Lexeme lexWord() throws IOException{
    	char ch = (char) file.read();
    	String buffer = "";
    	while(Character.isDigit(ch) || Character.isAlphabetic(ch)){
    		buffer +=ch;
    		ch = (char) file.read();
    	}
    	file.unread(ch);
    	if (buffer.equals("if")){return new Lexeme("IF");}
    	else if (buffer.equals("else")){return new Lexeme("ELSE");}
    	else if (buffer.equals("while")){return new Lexeme("WHILE");}
    	else if (buffer.equals("smile")){return new Lexeme("SMILE");}
    	else if (buffer.equals("smiley")){return new Lexeme("SMILEY");}
    	else if (buffer.equals("true")){return new Lexeme("BOOLEAN",true);}
    	else if (buffer.equals("false")){return new Lexeme("BOOLEAN",false);}
    	else if (buffer.equals("grin")){return new Lexeme("GRIN");}
    	else if (buffer.equals("nil")) {return new Lexeme("NIL");}
    	else{return new Lexeme("VARIABLE",buffer);}
    	
    }
    
    // returns the entire string (without quotes)
    public Lexeme lexString() throws IOException{
    	String buffer = "";
    	char ch = (char) file.read();
    	while (ch != '\"'){
    		if (ch == '\\'){
    			ch = (char) file.read();
    			if (ch == '\"'){
    				ch = (char) file.read();
    				buffer +='\"';
    			}
    			
    		}
    		else{
    			buffer +=ch;
    			ch = (char) file.read();
    		}
    		
    	}
    	return new Lexeme("STRING",buffer);
    }
    
    
    // skips the whitespace in the file
    public void skipWhiteSpace() throws IOException{
    	char ch = (char) file.read();
    	if (ch == ';'){
    		ch = (char) file.read();
    		if (ch == ')'){
    			while(ch!='\n'){
    	    		ch = (char) file.read();
    	    	}
    			
    		}
    		else {file.unread(ch);}
    	}
    	while (ch == ' ' || ch == '\n' || ch == '\r' || ch == '\t'){
    		ch =(char) file.read();
    		if (ch == '\n'){
    			lineNumber +=1;
    			}
    		if (ch == ';'){
        		ch = (char) file.read();
        		if (ch == ')'){
        			while(ch!='\n' && ch!='\r'){
        				int r = file.read();
        				ch = (char) r;
        	    		if (r == -1){
        	    			break;
        	    		}
        	    	}
        		}
        		else {file.unread(ch);}
        	}
    		
    		
    	}
    	file.unread(ch);
    }
    
    // iterates through the file and returns lexemes
    public void scanner() throws IOException {
    	Lexeme token = lex();
    	while (!token.type.equals("ENDofINPUT")){
    		System.out.println(token.display());
    		token = lex();
    	} 
    }
    
    public static void main(String[] args) throws IOException {
    	Lexer lexer = new Lexer("input.txt");
    	lexer.scanner();
	}
}
