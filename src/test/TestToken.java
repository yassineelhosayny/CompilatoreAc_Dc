package test;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.Test;

import token.Token;
import token.TokenType;

class TestToken {
	Token token1;
	Token token2;

	@Test
	void testCostrutoreConVal(){
		token1 = new Token(TokenType.MINUS_ASSIGN,"-=", 10);
		token2 = new Token(TokenType.FLOAT,"float", 1);
		
		assertEquals("-=",token1.getVal());
		assertEquals("float",token2.getVal());
		assertEquals(token1.getRiga(),10);
		assertEquals(token1.getType(),TokenType.MINUS_ASSIGN);
		assertEquals(token2.getRiga(),1);
		assertEquals(token2.getType(),TokenType.FLOAT);
	}
	@Test
	void testCostrutore() {
		token1 = new Token(TokenType.ASSIGN, 10);
		token2 = new Token(TokenType.INT, 1);

		assertEquals(token1.getRiga(),10);
		assertEquals(token1.getType(),TokenType.ASSIGN);
		assertEquals(token2.getRiga(),1);
		assertEquals(token2.getType(),TokenType.INT);
	}
	@Test 
	void testToString(){
		token1 = new Token(TokenType.MINUS_ASSIGN,"-=", 10);
		token2 = new Token(TokenType.FLOAT, 1);
		assertEquals(token1.toString(),"Token: in Riga= 10 | Tipo = MINUS_ASSIGN | Value= < -= >");
		assertEquals(token2.toString(),"Token: in Riga= 1 | Tipo = FLOAT");
	}

}
