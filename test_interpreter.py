import unittest


class LexerTestCase(unittest.TestCase):
    def makeLexer(self, text):
        from spi import Lexer
        lexer = Lexer(text)
        return lexer

    # def test_lexer_integer(self):
    #     from spi import TokenType
    #     lexer = self.makeLexer('234')
    #     token = lexer.get_next_token()
    #     self.assertEqual(token.type, TokenType.INT_CONST)
    #     self.assertEqual(token.value, 234)

    # def test_lexer_mul(self):
    #     from spi import TokenType
    #     lexer = self.makeLexer('*')
    #     token = lexer.get_next_token()
    #     self.assertEqual(token.type, TokenType.MUL)
    #     self.assertEqual(token.value, '*')

    # def test_lexer_div(self):
    #     from spi import TokenType
    #     lexer = self.makeLexer(' / ')
    #     token = lexer.get_next_token()
    #     self.assertEqual(token.type, TokenType.DIV)
    #     self.assertEqual(token.value, '/')
        
    # def test_lexer_div(self):
    #     from spi import TokenType
    #     lexer = self.makeLexer('%')
    #     token = lexer.get_next_token()
    #     self.assertEqual(token.type, TokenType.MOD)
    #     self.assertEqual(token.value, '%')

    # def test_lexer_plus(self):
    #     from spi import TokenType
    #     lexer = self.makeLexer('+')
    #     token = lexer.get_next_token()
    #     self.assertEqual(token.type, TokenType.PLUS)
    #     self.assertEqual(token.value, '+')

    # def test_lexer_minus(self):
    #     from spi import TokenType
    #     lexer = self.makeLexer('-')
    #     token = lexer.get_next_token()
    #     self.assertEqual(token.type, TokenType.MINUS)
    #     self.assertEqual(token.value, '-')

    # def test_lexer_lparen(self):
    #     from spi import TokenType
    #     lexer = self.makeLexer('(')
    #     token = lexer.get_next_token()
    #     self.assertEqual(token.type, TokenType.LPAREN)
    #     self.assertEqual(token.value, '(')

    # def test_lexer_rparen(self):
    #     from spi import TokenType
    #     lexer = self.makeLexer(')')
    #     token = lexer.get_next_token()
    #     self.assertEqual(token.type, TokenType.RPAREN)
    #     self.assertEqual(token.value, ')')

    def test_lexer_new_tokens(self):
        from spi import TokenType
        records = (
            ('=', TokenType.ASSIGN, '='),
            ('number', TokenType.ID, 'number'),
            (';', TokenType.SEMI, ';'),
            ('{', TokenType.LBRACE, '{'), 
            ('}', TokenType.RBRACE, '}'),
             ('(', TokenType.LPAREN, '('),
            (')',TokenType.RPAREN, ')'),
            
            ('INT', TokenType.INT,'INT'),
            ('DOUBLE', TokenType.DOUBLE,'DOUBLE'),
        )
        for text, tok_type, tok_val in records:
            lexer = self.makeLexer(text)
            token = lexer.get_next_token()
            self.assertEqual(token.type, tok_type)
            self.assertEqual(token.value, tok_val)


class InterpreterTestCase(unittest.TestCase):
    def makeInterpreter(self, text):
        from spi import Lexer, Parser, Interpreter
        lexer = Lexer(text)
        parser = Parser(lexer)
        interpreter = Interpreter(parser)
        return interpreter

    def test_arithmetic_expressions(self):
        for expr, result in (
           
            ('5 + 3', 8),
            ('5 +  (3 + 4) - 2', 10),
        ):
            interpreter = self.makeInterpreter('VOID MAIN(){  INT a,b,c; a = %s; }' % expr)
            interpreter.interpret()
            globals = interpreter.GLOBAL_SCOPE
            self.assertEqual(globals['a'], result)

    def test_expression_invalid_syntax1(self):
        interpreter = self.makeInterpreter(' VOID MAIN(){ INT a = 10*  ; }')
        with self.assertRaises(Exception):
            interpreter.interpret()

    def test_expression_invalid_syntax2(self):
        interpreter = self.makeInterpreter('VOID MAIN(){  INT a = 1 (1 + 2); }')
        with self.assertRaises(Exception):
            interpreter.interpret()

    def test_statements(self):
        text = """
VOID MAIN(){

  
   int x = 11;




}
"""
        interpreter = self.makeInterpreter(text)
        interpreter.interpret()

        globals = interpreter.GLOBAL_SCOPE
        self.assertEqual(len(globals.keys()), 2)
        # self.assertEqual(globals['n'], 2)
        # self.assertEqual(globals['a'], 2)
        # self.assertEqual(globals['b'], 40)
        # self.assertEqual(globals['c'], -38)
        self.assertEqual(globals['x'], 11)
        # self.assertEqual(globals['m'], 19)
        


if __name__ == '__main__':
    unittest.main()