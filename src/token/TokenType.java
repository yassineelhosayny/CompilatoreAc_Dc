package token;

public enum TokenType {
	PRINT,				//print: parola chiave
	TYINT,				//int: parola chiave
	TYFLOAT,		    //float : parola chiave
	INT,			//int:costante  [0-9]+
	FLOAT,			//float:costante  [0-9]+\.[0-9]{1,5}
	ID,					//identificatore. una lettera minuscola + insiemi di lettere minuscole o num   [a-z][a-z0-9]*
	ASSIGN,				// = operatore assegna
	PLUS,				// + operatore
	MINUS,				// - operatore
	MULTI,				// * operatore
	DIVID,				// / operatore
	PLUS_ASSIGN,				// +=  operatore
	MINUS_ASSIGN,				//-=operatore
	MULTI_ASSIGN,				// *=  operatore
	DIVID_ASSIGN,				///= operatore
	SEMI,				// ; delimitatore
	EOF;				//fine  (char) -1 0xFFFF oppure 2^16-1 = 65535
	//ignore ' ','\n', '\t','\r'
}
