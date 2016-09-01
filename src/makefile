all: Environment.class Evaluator.class Parser.class ParseTree.class Type.class Lexer.class Type.class Lexeme.class 

Environment.class: Environment.java
	javac -d . -classpath . Environment.java

Evaluator.class: Evaluator.java
	javac -d . -classpath . Evaluator.java

Parser.class: Parser.java
	javac -d . -classpath . Parser.java

ParseTree.class: ParseTree.java
	javac -d . -classpath . ParseTree.java	

Lexer.class: Lexer.java
	javac -d  . -classpath . Lexer.java

Type.class: Type.java
	javac -d . -classpath . Type.java

Lexeme.class: Lexeme.java
	javac -d . -classpath . Lexeme.java

cat-error1:
	cat error1.txt

cat-error2:
	cat error2.txt

cat-error3:
	cat error3.txt	

run-error1:
	java Evaluator error1.txt

run-error2:
	java Evaluator error2.txt

run-error3:
	java Evaluator error3.txt

cat-arrays:
	cat array.txt

run-arrays:
	java Evaluator array.txt

cat-conditionals:
	cat conditionals			

run-conditionals:
	java Evaluator conditionals

cat-recursion:
	cat recursion.txt

run-recursion:
	java Evaluator recursion.txt

cat-iteration:
	cat iterations.txt

run-iteration:
	java Evaluator iterations.txt

cat-functions:
	cat functions.txt

run-functions:
	java Evaluator functions.txt

cat-dictionary:
	cat redBlack.txt

run-dictionary:
	java Evaluator redBlack.txt	

cat-problem:
	cat adder.txt		

run-problem:
	java Evaluator adder.txt

clean: rm -f *.class