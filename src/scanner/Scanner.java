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

	}
	
  // nextToken ritorna il prossimo token nel file di input e legge 
  // i caratteri del token ritornato (avanzando fino al carattere
  // successivo all'ultimo carattere del token)
	public Token nextToken() throws LexicalException{

		// nextChar contiene il prossimo carattere dell'input (non consumato).
			char nextChar = peekChar();
			

		// Avanza nel buffer leggendo i carattere in skipChars
		// incrementando riga se leggi '\n'.
		// Se raggiungi la fine del file ritorna il Token EOF
		if(skipChars.contains(nextChar)){
			readChar();
			nextChar=peekChar();
			while(skipChars.contains(nextChar)){
				if(nextChar == EOF){
					return new Token(TokenType.EOF,"EOF",riga);
				}
				if(nextChar == '\n'){
					riga++;
				}
				readChar();
				nextChar=peekChar();
			}
		}

		// Se nextChar e' in letters
		// return scanId()
		// che deve generare o un Token ID o parola chiave
		if(letters.contains(nextChar))
			return scanId();

		// Se nextChar e' o in operators oppure delimitatore
		// ritorna il Token associato con l'operatore o il delimitatore
		// Attenzione agli operatori di assegnamento!
		if(charTypeMap.containsKey((Object)nextChar)){
			return scanOperator();
		}

		// Se nextChar e' ; o = 
		// ritorna il Token associato
		if(keywordsMap.containsKey((Object)nextChar)){
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

		throw new LexicalException(line.toString(),riga,"Errore; carattere sconosciuto");
	}

	private Token scanId(){
		return null;
	}
	
	private Token scanOperator() throws LexicalException{
		char nextChar = readChar(); 
			boolean op = (nextChar == '+' || nextChar == '-' || nextChar == '*' || nextChar == '/');
			if(op){
				if(peekChar()=='='){
				//operatore assegnamento
				}
				
			}
		return null;
	}
		
	private Token scanNumber(){

		return null;
	}

	

	private char readChar() throws LexicalException {
		try{
			return ((char) this.buffer.read());
		}catch(IOException e){
			throw new LexicalException(line.toString(),riga,"Errore; non è stato possibile leggere il caratere,");
		}
	}

	private char peekChar() throws LexicalException {
		try{
			char c = (char) buffer.read();
			buffer.unread(c);	
			return c;
		}catch(IOException e){
			throw new LexicalException(line.toString(),riga,"Errore; non è stato possibile leggere il caratere,");
		}
	}
}
