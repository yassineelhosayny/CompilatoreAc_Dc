package test;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import token.Token;
import token.TokenType;

class TestToken {

	@Test
	void test() {
		Token token1 = new Token(TokenType.ASSIGN, 10);
		assertEquals(token1.getRiga(),10);
		
		
	}

}
