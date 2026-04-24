package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import eccezioni.LexicalException;
import scanner.Scanner;
import token.Token;
import token.TokenType;

class TestScanner {
	String nomeFile;
	final String path = "src/test/data/testScanner/";
	Scanner s1;
	LexicalException msg;

	@BeforeEach
	void drop(){
		nomeFile = null;
		s1 = null;
	}

	@Test
	void testGenerale()  throws FileNotFoundException, LexicalException {
		nomeFile = "testGenerale.txt";
		s1 = new Scanner(path + nomeFile);
		//int temp;
		
		assertEquals("Token: in Riga= 1 | Tipo = TYINT",s1.nextToken().toString());
		assertEquals("Token: in Riga= 1 | Tipo = ID | Value= < temp >",s1.nextToken().toString());
		assertEquals("Token: in Riga= 1 | Tipo = SEMI | Value= < ; >",s1.nextToken().toString());
		
		//int temp1;
		assertEquals("Token: in Riga= 2 | Tipo = TYINT",s1.nextToken().toString());
		assertEquals("Token: in Riga= 2 | Tipo = ID | Value= < temp1 >",s1.nextToken().toString());
		assertEquals(TokenType.SEMI,s1.nextToken().getType());
		
		//temp += 5.;
		assertEquals("temp",s1.nextToken().getVal());
		assertEquals("Token: in Riga= 3 | Tipo = PLUS_ASSIGN | Value= < += >",s1.nextToken().toString());
		assertThrows(LexicalException.class,()->{
			s1.nextToken().toString();    //  " 5. " non è un float valido 
		});
		assertEquals("Token: in Riga= 3 | Tipo = SEMI | Value= < ; >",s1.nextToken().toString());
		
		//float b;
		assertEquals("Token: in Riga= 5 | Tipo = TYFLOAT",s1.nextToken().toString());
		assertEquals("Token: in Riga= 5 | Tipo = ID | Value= < b >",s1.nextToken().toString());
		assertEquals("Token: in Riga= 5 | Tipo = SEMI | Value= < ; >",s1.nextToken().toString());
		
		//b = temp1 + 3.2;
		assertEquals("Token: in Riga= 6 | Tipo = ID | Value= < b >",s1.nextToken().toString());
		assertEquals("Token: in Riga= 6 | Tipo = ASSIGN | Value= < = >",s1.nextToken().toString());
		assertEquals(TokenType.ID,s1.nextToken().getType());
		assertEquals("Token: in Riga= 6 | Tipo = PLUS | Value= < + >",s1.nextToken().toString());
		assertEquals("Token: in Riga= 6 | Tipo = FLOAT | Value= < 3.2 >",s1.nextToken().toString());
		assertEquals(TokenType.SEMI,s1.nextToken().getType());
		
		//print b;
		assertEquals("Token: in Riga= 7 | Tipo = PRINT",s1.nextToken().toString());
		assertEquals("b",s1.nextToken().getVal());
		assertEquals("Token: in Riga= 7 | Tipo = SEMI | Value= < ; >",s1.nextToken().toString());
		
		//EOF
		assertEquals("Token: in Riga= 8 | Tipo = EOF",s1.nextToken().toString());
	}
	@Test
	void testEOF() throws FileNotFoundException,LexicalException{
		nomeFile = "testEOF.txt";
		s1 = new Scanner(path+ nomeFile);
		assertEquals("Token: in Riga= 4 | Tipo = EOF",s1.nextToken().toString());
		assertEquals(4,s1.nextToken().getRiga());
		
		assertEquals(TokenType.EOF,s1.nextToken().getType());
	}

	@Test
	void testCaratteriSkip() throws FileNotFoundException,LexicalException{
		nomeFile = "caratteriSkip.txt";
		s1 = new Scanner(path+ nomeFile);
		assertEquals(TokenType.EOF,s1.nextToken().getType());
		assertEquals("Token: in Riga= 8 | Tipo = EOF",s1.nextToken().toString());
		assertEquals(8,s1.nextToken().getRiga());
	}

	@Test
	void testErroriNumbers() throws FileNotFoundException,LexicalException{
		nomeFile = "erroriNumbers.txt";
		s1 = new Scanner(path + nomeFile);
		// 0 33 
		assertEquals("Token: in Riga= 1 | Tipo = INT | Value= < 0 >",s1.nextToken().toString());
		assertEquals("33",s1.nextToken().getVal());

		// 123.121212 -> parte frazionaria troppo lunga >5
		msg = assertThrows(LexicalException.class,()->{
			s1.nextToken();
		});
		assertEquals("Errore; parte frazionaria del numero non valida alla Riga: 3\n\tcodice valido fino a : '3|  123.121212'",msg.getMessage());

		// 123.12335 -> perfetto
		assertEquals("Token: in Riga= 4 | Tipo = FLOAT | Value= < 123.12335 >",s1.nextToken().toString());

		// 123.123.123 ; (123.123) -> token float
		assertEquals("Token: in Riga= 7 | Tipo = FLOAT | Value= < 123.123 >",s1.nextToken().toString());
		//. errore lexicale
		msg= assertThrows(LexicalException.class,()->{
			s1.nextToken();
		});
		assertEquals("Errore; carattere sconosciuto '.'  alla Riga: 7\n\tcodice valido fino a : '7|  123.123'",msg.getMessage());
	}
	@Test
	void testFloat() throws FileNotFoundException,LexicalException{
		nomeFile = "testFloat.txt";
		s1 = new Scanner(path + nomeFile);
		//		098.8095
		assertEquals("Token: in Riga= 1 | Tipo = FLOAT | Value= < 098.8095 >",s1.nextToken().toString());
		//	0.	
		msg= assertThrows(LexicalException.class,()->{
			s1.nextToken();
		});
		assertEquals("Errore; parte frazionaria del numero non valida alla Riga: 2\n\tcodice valido fino a : '2| \t0.'",msg.getMessage());		
		//		98.
		msg= assertThrows(LexicalException.class,()->{
			s1.nextToken();
		});
		assertEquals("Errore; parte frazionaria del numero non valida alla Riga: 3\n\tcodice valido fino a : '3| \t\t98.'",msg.getMessage());	
		//89.99999
		assertEquals("Token: in Riga= 5 | Tipo = FLOAT | Value= < 89.99999 >",s1.nextToken().toString());

	}
	@Test
	void testId() throws FileNotFoundException,LexicalException{
		nomeFile = "testId.txt";
		s1 = new Scanner(path + nomeFile);
		//riga 1: jskjdsf2jdshkf
		assertEquals("Token: in Riga= 1 | Tipo = ID | Value= < jskjdsf2jdshkf >",s1.nextToken().toString());
		assertEquals(TokenType.ID,s1.nextToken().getType());
		assertEquals("Token: in Riga= 4 | Tipo = ID | Value= < ffloat >",s1.nextToken().toString());		
		assertEquals("hhhjj",s1.nextToken().getVal());

	}
	@Test
	void testInt() throws FileNotFoundException,LexicalException{
		nomeFile = "testInt.txt";
		s1 = new Scanner(path + nomeFile);
		//riga 1: jskjdsf2jdshkf
		assertEquals("0050",s1.nextToken().getVal());
		assertEquals("698",s1.nextToken().getVal());
		assertEquals("560099",s1.nextToken().getVal());		
		assertEquals("1234",s1.nextToken().getVal());
	
	}
	@Test
	void testIdKeyWords() throws FileNotFoundException,LexicalException{
		nomeFile = "testIdKeyWords.txt";
		s1 = new Scanner(path + nomeFile);
		//riga 1: int    inta
		assertEquals("Token: in Riga= 1 | Tipo = TYINT",s1.nextToken().toString());
		//uso di peekToken
		assertEquals("inta",s1.peekToken().getVal());
		assertEquals("Token: in Riga= 1 | Tipo = ID | Value= < inta >",s1.nextToken().toString());
		
		assertEquals(null,s1.peekToken().getVal());
		assertEquals(TokenType.TYFLOAT,s1.peekToken().getType());
		assertEquals("Token: in Riga= 2 | Tipo = TYFLOAT",s1.nextToken().toString());
		assertEquals("Token: in Riga= 3 | Tipo = PRINT",s1.nextToken().toString());
		assertEquals("Token: in Riga= 4 | Tipo = ID | Value= < nome >",s1.nextToken().toString());
		assertEquals("intnome",s1.nextToken().getVal());
		
		assertEquals("Token: in Riga= 6 | Tipo = TYINT",s1.nextToken().toString());
		
		assertEquals("nome",s1.peekToken().getVal());
		assertEquals(TokenType.ID,s1.peekToken().getType());
		assertEquals("Token: in Riga= 6 | Tipo = ID | Value= < nome >",s1.nextToken().toString());
	}
	@Test
	void testOpsDels() throws FileNotFoundException,LexicalException{
		nomeFile = "testOpsDels.txt";
		s1 = new Scanner(path + nomeFile);

		//1.   +    /=
		assertEquals("Token: in Riga= 1 | Tipo = PLUS | Value= < + >",s1.nextToken().toString());
		assertEquals("/=",s1.nextToken().getVal());
		//2. 		- *
		assertEquals(TokenType.MINUS,s1.nextToken().getType());
		assertEquals(TokenType.MULTI,s1.nextToken().getType());
		//riga 3: /
		assertEquals("/",s1.nextToken().getVal());
		//5.  +=   
		assertEquals("Token: in Riga= 5 | Tipo = PLUS_ASSIGN | Value= < += >",s1.nextToken().toString());
		//6.  =         -=
		assertEquals("=",s1.nextToken().getVal());
		assertEquals("-=",s1.nextToken().getVal());
		//8.-		=  *=  
		assertEquals("-",s1.nextToken().getVal());
		assertEquals(TokenType.ASSIGN,s1.nextToken().getType());
		assertEquals("Token: in Riga= 8 | Tipo = MULTI_ASSIGN | Value= < *= >",s1.nextToken().toString());
		//10.	;
		assertEquals(TokenType.SEMI,s1.nextToken().getType());

	}
	@Test
	void testCommenti() throws FileNotFoundException,LexicalException{
		nomeFile = "testCommentiConKeyWords.txt";
		s1 = new Scanner(path + nomeFile);

		assertEquals("Token: in Riga= 4 | Tipo = TYFLOAT",s1.nextToken().toString());
		assertEquals("Token: in Riga= 10 | Tipo = PRINT",s1.nextToken().toString());
		assertEquals("Token: in Riga= 11 | Tipo = ID | Value= < nome >",s1.nextToken().toString());
		assertEquals("Token: in Riga= 13 | Tipo = DIVID | Value= < / >",s1.nextToken().toString());
		assertEquals("Token: in Riga= 15 | Tipo = EOF",s1.nextToken().toString());
	}
	@Test
	void testPeekToken() throws FileNotFoundException,LexicalException{
		nomeFile = "testGenerale.txt";
		s1 = new Scanner(path + nomeFile);
		Token pT = s1.peekToken();
		Token nT = s1.nextToken();
		assertEquals(pT.getRiga(),nT.getRiga());
		assertEquals(pT.getType(),nT.getType());
		assertEquals(pT.toString(),nT.toString());
		//peekToken : non consuma il token
		for(int i = 0;i<3;i++){
			assertEquals("Token: in Riga= 1 | Tipo = ID | Value= < temp >",s1.peekToken().toString());
		}
		s1.nextToken();
		//restiusce il prossimo dopo che nextToken l'ha consumato il precedente
		assertEquals("Token: in Riga= 1 | Tipo = SEMI | Value= < ; >",s1.nextToken().toString());
	}


}
