package scanner;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.util.HashMap;
import java.util.HashSet;


import token.*;

public class Scanner {
	final char EOF = (char) -1; 
	private int riga;
	private PushbackReader buffer;

	private StringBuilder line= new StringBuilder();
	private Token currentToken;

	/* caratteri non considerati dallo scanner */
	private HashSet<Character> skipChars;
	/* lettere dell'alfabeto */
	private HashSet<Character> letters;
	/* insieme di cifre */
	private HashSet<Character> digits;
	/* insieme dei caratteri '+', '-', '*', '/', ';', '=' */
	private HashMap<Character, TokenType> charTypeMap;
	/* insieme delle parole chiave "print", "float", "int" */
	private HashMap<String, TokenType> keywordsMap;

	public Scanner(String fileName) throws FileNotFoundException {

		this.buffer = new PushbackReader(new FileReader(fileName));
		riga = 1;
		// inizializzare campi
		//carattere da ignorare (skipChars)
		skipChars = new HashSet<Character>();
		skipChars.add(' '); /*white space */
		skipChars.add('\n');
		skipChars.add('\r');
		skipChars.add('\t');

		//lettere (letters)
		letters = new HashSet<Character>();
		for(char c ='a';c <= 'z';c++)
			letters.add(c);

		//numbers
		digits = new HashSet<Character>();
		for(char c= '0'; c<='9'; c++)
			digits.add(c);

		//insieme dei caratteri
		charTypeMap = new HashMap<Character, TokenType>();
		charTypeMap.put('-',TokenType.MINUS);
		charTypeMap.put('+',TokenType.PLUS);
		charTypeMap.put('*',TokenType.MULTI);
		charTypeMap.put('/',TokenType.DIVID);
		charTypeMap.put('=',TokenType.ASSIGN);
		charTypeMap.put(';',TokenType.SEMI);

		//parole chiave
		keywordsMap = new HashMap<String, TokenType>();
		keywordsMap.put("print",TokenType.PRINT);
		keywordsMap.put("int",TokenType.TYINT);
		keywordsMap.put("float",TokenType.TYFLOAT);

		//token attuale
		currentToken = null;
	}
	
  // nextToken ritorna il prossimo token nel file di input e legge 
  // i caratteri del token ritornato (avanzando fino al carattere
  // successivo all'ultimo carattere del token)
	public Token nextToken() throws LexicalException{
		//consuma il token
		if (currentToken != null) {
			Token tem = currentToken;
			currentToken = null;
			return tem;
		}
		// nextChar contiene il prossimo carattere dell'input (non consumato).
			char nextChar = peekChar();
			

		// Avanza nel buffer leggendo i carattere in skipChars
		// incrementando riga se leggi '\n'.
		// Se raggiungi la fine del file ritorna il Token EOF
		while(skipChars.contains(nextChar)){
			if(nextChar == '\n'){
				riga++;
			}
			readChar();
			nextChar=peekChar();
		}
		if(nextChar == EOF){
			return new Token(TokenType.EOF,riga);
		}		

		// Se nextChar e' in letters
		// return scanId()
		// che deve generare o un Token ID o parola chiave
		if(letters.contains(nextChar))
			return scanId();

		// Se nextChar e' o in operators oppure delimitatore
		// ritorna il Token associato con l'operatore o il delimitatore
		// Attenzione agli operatori di assegnamento!
		if(charTypeMap.containsKey(nextChar)){
			return scanOperator();
		}

		// Se nextChar e' in numbers
		// return scanNumber()
		// che legge sia un intero che un float e ritorna il Token INUM o FNUM
		// i caratteri che leggete devono essere accumulati in una stringa
		// che verra' assegnata al campo valore del Token
		if(digits.contains(nextChar)){
			return scanNumber();
		}
		// Altrimenti il carattere NON E' UN CARATTERE LEGALE sollevate una
		// eccezione lessicale dicendo la riga e il carattere che la hanno
		// provocata. 

		throw new LexicalException(line.toString(),riga,nextChar,"Errore; carattere sconosciuto");
	}

	private void aggiornaLine(char c) {
		if(c == '\n')
			this.line.setLength(0);
		else if(c != EOF)	
			this.line.append(c);
	}

	private Token scanId() throws LexicalException{
		StringBuilder parola = new StringBuilder();
		parola.append(readChar());
 
		while(letters.contains(peekChar()) || digits.contains(peekChar())){
			parola.append(readChar());
		}
		if(keywordsMap.containsKey(parola.toString())){
			return new Token(keywordsMap.get(parola.toString()),riga);
		}
		//else id
		return new Token(TokenType.ID,parola.toString(),riga);

	}
	private boolean assignOp() throws LexicalException{
		return peekChar() == '=';
	}
	private int isCommento() throws LexicalException{
		char nextChar = peekChar();
		//commento tipo //
		if(nextChar == '/'){
			readChar();
			while(true){
				char c = peekChar();
				if(c == EOF){
					return 2;
					}
				if(c == '\n'){
					readChar();
					riga++;
					return 1; //commento di lenea stato saltato
				}
				readChar();
			}
		}
		//commento tipo /* */
		else if(nextChar=='*'){
			readChar();
			char ultimo='\0';

			while(true){
			char c = readChar();
				if(c == EOF){
					throw new LexicalException(line.toString(), riga, "Errore: Commento multilinea non è chiuso");
				}
				if(c == '\n'){
					riga++;
				}
				if (ultimo == '*' && c == '/') {
                	return 1;
           		}
				ultimo = c;	
			}
		}
		
		//non è un commento
		else return 0;
		
	}
	
	private Token scanOperator() throws LexicalException{
		char nextChar = readChar(); 
		StringBuilder op = new StringBuilder();
		op.append(nextChar);

		switch(nextChar){
			case '-':
				if(assignOp()){
					op.append(readChar());
					return new Token(TokenType.MINUS_ASSIGN,op.toString(),riga);
				}
			  return new Token(TokenType.MINUS,op.toString(),riga);

			case '+':
				if(assignOp()){
					op.append(readChar());
					return new Token(TokenType.PLUS_ASSIGN,op.toString(),riga);
				}
			  return new Token(TokenType.PLUS,op.toString(),riga);

			case '*':
				if(assignOp()){
					op.append(readChar());
					return new Token(TokenType.MULTI_ASSIGN,op.toString(),riga);
				}
			  return new Token(TokenType.MULTI,op.toString(),riga);

			case '/':
				if(assignOp()){
					op.append(readChar());
					return new Token(TokenType.DIVID_ASSIGN,op.toString(),riga);
				}
				int statoIfCommento = isCommento();
				if(statoIfCommento == 1){
			  			return nextToken();
				}
				if(statoIfCommento == 2){
						return new Token(TokenType.EOF,riga);
				}

				return new Token(TokenType.DIVID,op.toString(),riga);

			case '=':
			  return new Token(TokenType.ASSIGN,op.toString(),riga);

			case ';':
			  return new Token(TokenType.SEMI,op.toString(),riga);
			default:
				throw new LexicalException(line.toString(), riga, "Errore; carattere sconosciuto Mentre la lettura del operatore.");
		}	
	}

	private Token scanNumber() throws LexicalException{
		StringBuilder num = new StringBuilder(); 
		while(digits.contains(peekChar())){
			num.append(readChar());
		}
		if(peekChar() == '.'){
			num.append(readChar());
			int len = 0;
				while(digits.contains(peekChar())){
					num.append(readChar());
					len++;
				}
				if(len == 0  || len > 5){
					throw new LexicalException(line.toString(),riga,"Errore; parte frazionaria del numero non valida");
				}
				else{
					return new Token(TokenType.FLOAT,num.toString(),riga);
				}
		}
		return new Token(TokenType.INT,num.toString(),riga);

	}

	public Token peekToken() throws LexicalException{
		if(this.currentToken == null){
			this.currentToken = nextToken();
		}
		return this.currentToken;
	}

	private char readChar() throws LexicalException {
		try{
			int v = this.buffer.read();
			if(v == -1){
				return EOF;
			}
			char c = (char) v;
			aggiornaLine(c);
			return c;

		}catch(IOException e){
			throw new LexicalException(line.toString(),riga,"Errore; non è stato possibile leggere il caratere,");
		}
	}

	private char peekChar() throws LexicalException {
		try{
			int c = buffer.read();

			if(c == -1)
				 return EOF;

			buffer.unread(c);	
			return (char)c;
		}catch(IOException e){
			throw new LexicalException(line.toString(),riga,"Errore; non è stato possibile leggere il caratere,");
		}
	}
}
