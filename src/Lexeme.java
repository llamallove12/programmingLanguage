import java.util.ArrayList;

/****************************
 *  Lexeme : Language Object
 *  
 *  Sasha Hedges
 * 
 * **************************/


public class Lexeme{
	String type;
	String string;
	int integer;
	Double real;
	Boolean bool;
	ArrayList<Lexeme> array;
	Lexeme left;
	Lexeme right;
	
	
	// for built in values
	public Lexeme(String val){
		type = new Type(val).type;
		string = val;
	}
	// for variables and strings
	public Lexeme(String ID, String val){
		type = ID;
		string = val;
	}
	// for integers
	public Lexeme(int val){
		type = new Type(val).type;
		integer = val;
	}
	// for reals
	public Lexeme(Double val){
		type = new Type(val).type;
		real = val;
	}
	// for reals
	public Lexeme(String ID, Boolean val){
		type = ID;
		bool = val;
	}
	// for arrays
	public Lexeme(String ID, ArrayList<Lexeme> arr){
		type = ID;
		array = arr;
	}
	
	
	public String printVal(){
		String val;
		if (type.equals("VARIABLE") || type.equals("STRING")){
			val = string;
		}
		else if (type.equals("BOOLEAN")){
			val = ""+bool;
		}
		else if(type.equals("INTEGER")){
			val = ""+integer;
		}
		else if (type.equals("REAL")){
			val = ""+real;
		}
		else val = type;
		return val;
	}
	
	public void printArr(){
		int i =0;
		while (i!=array.size()){
			System.out.print(array.get(i)+" ");
			i = i+1;
		}
	}
	
	
	//displays the values of the lexeme
	public String display(){
		String val;
		if (type.equals("VARIABLE") || type.equals("STRING")){
			val = type+" "+string;
			
		}
		else if (type.equals("BOOLEAN")){
			val = type+" "+bool;
		}
		else if(type.equals("INTEGER")){
			val = type+" "+integer;
		}
		else if (type.equals("REAL")){
			val = type +" "+real;
		}
		else if (type.equals("ARRAY")){
			val = type+" call printArr() fool";
		}
		else val = type;
		return val;
	}
	
	
	
	
	
}