""" SPI - Simple Pascal Interpreter. Part 9."""
import argparse
import sys
from enum import Enum
import random

_SHOULD_LOG_SCOPE = False  
_SHOULD_LOG_STACK = False  

class ErrorCode(Enum):
    UNEXPECTED_TOKEN = 'Unexpected token'
    ID_NOT_FOUND     = 'Identifier not found'
    DUPLICATE_ID     = 'Duplicate id found'


class Scope(object):
    def __init__(self, scope_name, parent_scope=None):
        self.scope_name = scope_name
        self.parent_scope = parent_scope
        self._values = dict()

    def __setitem__(self, key, value):
        self._values[key] = value

    def __getitem__(self, item):
        return self._values[item]

    def __contains__(self, key):
        return key in self._values

    def __repr__(self):
        lines = [
            '{}:{}'.format(key, val) for key, val in self._values.items()
        ]
        title = '{}\n'.format(self.scope_name)
        return title + '\n'.join(lines)


class Frame(object):
    def __init__(self, frame_name, global_scope):
        self.frame_name = frame_name
        self.current_scope = Scope(
            '{}.scope_00'.format(frame_name),
            global_scope
        )
        self.scopes = [self.current_scope]

    def new_scope(self):
        self.current_scope = Scope(
            '{}{:02d}'.format(
                self.current_scope.scope_name[:-2],
                int(self.current_scope.scope_name[-2:]) + 1
            ),
            self.current_scope
        )
        self.scopes.append(self.current_scope)

    def del_scope(self):
        current_scope = self.current_scope
        self.current_scope = current_scope.parent_scope
        self.scopes.pop(-1)
        del current_scope

    def __contains__(self, key):
        return key in self.current_scope

    def __repr__(self):
        lines = [
            '{}\n{}'.format(
                scope,
                '-' * 40
            ) for scope in self.scopes
        ]

        title = 'Frame: {}\n{}\n'.format(
            self.frame_name,
            '*' * 40
        )

        return title + '\n'.join(lines)


class Stack(object):
    def __init__(self):
        self.frames = list()
        self.current_frame = None

    def __bool__(self):
        return bool(self.frames)

    def new_frame(self, frame_name, global_scope=None):
        frame = Frame(frame_name, global_scope=global_scope)
        self.frames.append(frame)
        self.current_frame = frame

    def del_frame(self):
        self.frames.pop(-1)
        self.current_frame = len(self.frames) and self.frames[-1] or None

    def __repr__(self):
        lines = [
            '{}'.format(frame) for frame in self.frames
        ]
        return '\n'.join(lines)


class Memory(object):
    def __init__(self):
        self.global_frame = Frame('GLOBAL_MEMORY', None)
        self.stack = Stack()

    def declare(self, key, value=random.randint(0, 2**32)):
        ins_scope = self.stack.current_frame.current_scope if self.stack.current_frame else self.global_frame.current_scope
        ins_scope[key] = value

    def __setitem__(self, key, value):
        ins_scope = self.stack.current_frame.current_scope if self.stack.current_frame else self.global_frame.current_scope
        curr_scope = ins_scope
        while curr_scope and key not in curr_scope:
            curr_scope = curr_scope.parent_scope
        ins_scope = curr_scope if curr_scope else ins_scope
        ins_scope[key] = value

    def __getitem__(self, item):
        curr_scope = self.stack.current_frame.current_scope if self.stack.current_frame else self.global_frame.current_scope
        while curr_scope and item not in curr_scope:
            curr_scope = curr_scope.parent_scope
        return curr_scope[item]

    def new_frame(self, frame_name):
        self.stack.new_frame(frame_name, self.global_frame.current_scope)

    def del_frame(self):
        self.stack.del_frame()

    def new_scope(self):
        self.stack.current_frame.new_scope()

    def del_scope(self):
        self.stack.current_frame.del_scope()

    def __repr__(self):
        return "{}\nStack\n{}\n{}".format(
            self.global_frame,
            '=' * 40,
            self.stack
        )

    def __str__(self):
        return self.__repr__()

class Error(Exception):
    def __init__(self, error_code=None, token=None, message=None):
        self.error_code = error_code
        self.token = token
        # add exception class name before the message
        self.message = f'{self.__class__.__name__}: {message}'


class LexerError(Error):
    pass


class ParserError(Error):
    pass


class SemanticError(Error):
    pass
###############################################################################
#                                                                             #
#  LEXER                                                                      #
#                                                                             #
###############################################################################

# Token types
#
# EOF (end-of-file) token is used to indicate that
# there is no more input left for lexical analysis
# (INTEGER, DOUBLE, PRINT, PLUS, MINUS, MUL, DIV, LPAREN, RPAREN, ID, ASSIGN,
# SEMI, EOF, MOD, LBRACE, RBRACE, VOID) = (
#     'INTEGER','DOUBLE','PRINT', 'PLUS', 'MINUS', 'MUL', 'DIV', '(', ')', 'ID', 'ASSIGN',
# 'SEMI', 'EOF', 'MOD', 'LBRACE','RBRACE' ,'VOID'
# )

class TokenType(Enum):
    # single-character token types
    PLUS          = '+'
    MINUS         = '-'
    MUL           = '*'
    DIV     = '/'
    MOD = '%'
    AND = '&'
    OR = '|'
    LPAREN        = '('
    RPAREN        = ')'
    SEMI          = ';'
    DOT           = '.'
    COLON         = ':'
    COMMA         = ','
    # block of reserved words
    VOID     = 'VOID'  # marks the beginning of the block
    INT       = 'INT'
    DOUBLE          = 'DOUBLE'
    PRINT = 'PRINT'
    VAR           = 'VAR'
    IF = 'IF'
    ELSE = 'ELSE'
    WHILE = 'WHILE'
   
    EQUALITY = '=='
    LSQBRAC = '['
    RSQBRAC = ']'
    CHILD = 'CHILD'
    # PROCEDURE     = 'PROCEDURE'
   # BEGIN         = 'BEGIN'
    LBRACE        = '{'
    # END           = 'END' 
    RBRACE        = '}'# marks the end of the block
    # misc
    ID            = 'ID'
    INT_CONST = 'INT_CONST'
    DOUBLE_CONST    = 'DOUBLE_CONST'
    ASSIGN        = '='
    EOF           = 'EOF'


class Token(object):
    def __init__(self, type, value,line=None):
        self.type = type
        self.value = value
        self.line = line


    def __str__(self):
        """String representation of the class instance.

        Examples:
            Token(INTEGER, 3)
            Token(PLUS, '+')
            Token(MUL, '*')
        """
        return 'Token({type}, {value}, position={line})'.format(
            type=self.type,
            value=repr(self.value),
            line = self.line
        )

    def __repr__(self):
        return self.__str__()

def _build_reserved_keywords():
    """Build a dictionary of reserved keywords.

    The function relies on the fact that in the TokenType
    enumeration the beginning of the block of reserved keywords is
    marked with PROGRAM and the end of the block is marked with
    the END keyword.

    Result:
        {'PROGRAM': <TokenType.PROGRAM: 'PROGRAM'>,
         'INTEGER': <TokenType.INTEGER: 'INTEGER'>,
         'REAL': <TokenType.REAL: 'REAL'>,
         'DIV': <TokenType.INTEGER_DIV: 'DIV'>,
         'VAR': <TokenType.VAR: 'VAR'>,
         'PROCEDURE': <TokenType.PROCEDURE: 'PROCEDURE'>,
         'BEGIN': <TokenType.BEGIN: 'BEGIN'>,
         'END': <TokenType.END: 'END'>}
    """
    # enumerations support iteration, in definition order
    tt_list = list(TokenType)
    start_index = tt_list.index(TokenType.VOID)
    end_index = tt_list.index(TokenType.RBRACE)
    reserved_keywords = {
        token_type.value: token_type
        for token_type in tt_list[start_index:end_index + 1]
    }
    return reserved_keywords
# RESERVED_KEYWORDS = {
#     'LBRACE': Token('LBRACE', '{'),
#   'RBRACE': Token('RBRACE', '}'),
#   'VOID': Token('VOID','VOID'),
#    'PRINT': Token('PRINT', 'PRINT'),
#     'INTEGER': Token('INTEGER', 'INTEGER'),
# }

RESERVED_KEYWORDS = _build_reserved_keywords()
class LexicalError(Exception):
    pass

class Lexer(object):
    def __init__(self, text):
        # client string input, e.g. "4 + 2 * 3 - 6 / 2"
        self.text = text
        # self.pos is an index into self.text
        self.pos = 0
        self.current_char = self.text[self.pos]
        self.line = 1
    
    def error(self):
        # s = "Lexer error on '{lexeme}' line: {linen} ".format(
        #     lexeme=self.current_char,
        #     line=self.line,
           
        
        raise LexicalError()

    def advance(self):
        if self.current_char == '\n':
            self.line += 1
            
        """Advance the `pos` pointer and set the `current_char` variable."""
        self.pos += 1
        if self.pos > len(self.text) - 1:
            self.current_char = None  # Indicates end of input
        else:
            self.current_char = self.text[self.pos]

    def peek(self):
        peek_pos = self.pos + 1
        if peek_pos > len(self.text) - 1:
            return None
        else:
            return self.text[peek_pos]

    def skip_whitespace(self):
        while self.current_char is not None and self.current_char.isspace():
            self.advance()

    def skip_comment(self):
        while self.current_char is not None:
            if self.current_char == '*' and self.peek() == '/':
                self.advance()
                self.advance()
                return
            self.advance()
        self.error()

    def number(self):
        """Return a (multidigit) integer or float consumed from the input."""

        # Create a new token with current line and column number
        token = Token(type=None, value=None, line=self.line)

        result = ''
        while self.current_char is not None and self.current_char.isdigit():
            result += self.current_char
            self.advance()

        if self.current_char == '.':
            result += self.current_char
            self.advance()

            while self.current_char is not None and self.current_char.isdigit():
                result += self.current_char
                self.advance()

            token.type = TokenType.DOUBLE_CONST
            token.value = float(result)
        else:
            token.type = TokenType.INT_CONST
            token.value = int(result)

        return token

    def _id(self):
        """Handle identifiers and reserved keywords"""

        # Create a new token with current line and column number
        token = Token(type=None, value=None, line=self.line)

        value = ''
        while self.current_char is not None and self.current_char.isalnum():
            value += self.current_char
            self.advance()

        token_type = RESERVED_KEYWORDS.get(value.upper())
        if token_type is None:
            token.type = TokenType.ID
            token.value = value
        else:
            # reserved keyword
            token.type = token_type
            token.value = value.upper()

        return token

    def get_next_token(self):
        """Lexical analyzer (also known as scanner or tokenizer)

        This method is responsible for breaking a sentence
        apart into tokens. One token at a time.
        """
        while self.current_char is not None:
            if self.current_char.isspace():
                self.skip_whitespace()
                continue

            if self.current_char == '/' and self.peek() == '*':
                self.advance()
                self.skip_comment()
                continue

            if self.current_char.isalpha():
                return self._id()

            if self.current_char.isdigit():
                return self.number()

            if self.current_char == '=' :
                token = Token(
                    type=TokenType.ASSIGN,
                    value=TokenType.ASSIGN.value,  # ':='
                    line=self.line,
                
                )
                self.advance()
                return token

            # single-character token
            try:
                # get enum member by value, e.g.
                # TokenType(';') --> TokenType.SEMI
                token_type = TokenType(self.current_char)
            except ValueError:
                # no enum member with value equal to self.current_char
                self.error()
            else:
                # create a token with a single-character lexeme as its value
                token = Token(
                    type=token_type,
                    value=token_type.value,  # e.g. ';', '.', etc
                    line=self.line,
                    
                )
                self.advance()
                return token

        # EOF (end-of-file) token indicates that there is no more
        # input left for lexical analysis
        return Token(type=TokenType.EOF, value=None)


###############################################################################
#                                                                             #
#  PARSER                                                                     #
#                                                                             #
###############################################################################

class AST(object):
    pass


class BinOp(AST):
    def __init__(self, left, op, right):
        self.left = left
        self.token = self.op = op
        self.right = right


class Num(AST):
    def __init__(self, token):
        self.token = token
        self.value = token.value


class UnaryOp(AST):
    def __init__(self, op, expr):
        self.token = self.op = op
        self.expr = expr

class VarDecl(AST):
    def __init__(self, var_node, type_node, line):
        AST.__init__(self)
        self.var_node = var_node
        self.type_node = type_node


class Compound(AST):
    """Represents a 'BEGIN ... END' block"""
    def __init__(self):
        self.children = []

class ReturnStmt(AST):
    def __init__(self, expression):
        self.expression = expression


class Assign(AST):
    def __init__(self, left, op, right):
        self.left = left
        self.token = self.op = op
        self.right = right
    

class Var(AST):
    """The Var node is constructed out of ID token."""
    def __init__(self, token):
        self.token = token
        self.value = token.value


class NoOp(AST):
    pass
class Type(AST):
    def __init__(self, token, line):
        AST.__init__(self)
        self.token = token
        self.value = token.value
        

class Program(AST):
    def __init__(self, name, block):
        self.name = name
        self.block = block

class Parser(object):
    def __init__(self, lexer):
        self.lexer = lexer
        # set current token to the first token taken from the input
        self.current_token = self.lexer.get_next_token()

    def error(self):
        raise Exception('Invalid syntax')

    def eat(self, token_type):
        # compare the current token type with the passed token
        # type and if they match then "eat" the current token
        # and assign the next token to the self.current_token,
        # otherwise raise an exception.
        if self.current_token.type == token_type:
            self.current_token = self.lexer.get_next_token()
        else:
            self.error()

    def program(self):
        """program : compound_statement DOT"""
        
        self.eat(TokenType.VOID)
        self.variable()

        self.eat(TokenType.LPAREN)
        self.eat(TokenType.RPAREN)
        node = self.compound_statement()
        return node

    def compound_statement(self):
        """
        compound_statement: BEGIN statement_list END
        """
       
        self.eat(TokenType.LBRACE)
        while self.current_token.type != TokenType.RBRACE:
            if self.current_token.type in (TokenType.INT, TokenType.DOUBLE):
                nodes = self.declaration_list()
            else:
                nodes = self.statement_list()
        self.eat(TokenType.RBRACE)

        root = Compound()
        for node in nodes:
            root.children.append(node)

        return root

    def statement_list(self):
        """
        statement_list : statement
                       | statement SEMI statement_list
        """
        node = self.statement()

        results = [node]

        while self.current_token.type == TokenType.SEMI:
            self.eat(TokenType.SEMI)
            results.append(self.statement())

        if self.current_token.type == TokenType.ID:
            self.error()

        return results
    def declaration_list(self):
        """
        declaration_list            : declaration+
        """
        result = self.declaration()
        while self.current_token.type == (TokenType.INT, TokenType.DOUBLE):
            result.extend(self.declaration())
        return result

    def declaration(self):
        """
        declaration                 : type_spec init_declarator_list SEMICOLON
        """
        result = list()
        type_node = self.type_spec()
        for node in self.init_declarator_list():
            if isinstance(node, Var):
                result.append(VarDecl(
                    type_node=type_node,
                    var_node=node,
                    line=self.lexer.line
                ))
            else:
                result.append(node)
        self.eat(TokenType.SEMI)
        return result

    def init_declarator_list(self):
        """
        init_declarator_list        : init_declarator (COMMA init_declarator)*
        """
        result = list()
        result.extend(self.init_declarator())
        while self.current_token.type == TokenType.COMMA:
            self.eat(TokenType.COMMA)
            result.extend(self.init_declarator())
        return result

    def init_declarator(self):
        """
        init_declarator             : variable (ASSIGN assignment_expression)?
        """
        var = self.variable()
        result = list()
        result.append(var)
        if self.current_token.type == TokenType.ASSIGN:
            token = self.current_token
            self.eat(TokenType.ASSIGN)
            result.append(Assign(
                left=var,
                op=token,
                right=self.expr(),
                
            ))
        return result
    def statement(self):
        """
        statement : compound_statement
                  | assignment_statement
                  | empty
        """
        # if self.current_token.type == TokenType.LBRACE:
        #     node = self.compound_statement()
        # if self.current_token.type == TokenType.INT:
        #     self.eat(TokenType.INT)
        #     expression = self.expr()
        #     node= ReturnStmt(expression)
        #     return node
        # elif self.current_token.type == TokenType.DOUBLE:
        #     self.eat(TokenType.DOUBLE)
        #     expression = self.expr()
        #     node= ReturnStmt(expression)
        #     return node
        if self.current_token.type == TokenType.ID:
            node = self.assignment_statement()
        
        else:
            node = self.empty()
        return node

    def assignment_statement(self):
        """
        assignment_statement : variable ASSIGN expr
        """
        if self.current_token.type == TokenType.PRINT:
            self.eat(TokenType.PRINT)
            expression = self.expr()
            node= ReturnStmt(expression)
            return node
        # elif self.current_token.type == TokenType.INT:
        #     self.eat(TokenType.INT)
        #     expression = self.expr()
        #     node= ReturnStmt(expression)
        #     return node
        # elif self.current_token.type == TokenType.DOUBLE:
        #     self.eat(TokenType.DOUBLE)
        #     expression = self.expr()
        #     node= ReturnStmt(expression)
        #     return node
        else:      
            left = self.variable()
            token = self.current_token
            self.eat(TokenType.ASSIGN)

            right = self.expr()
            node = Assign(left, token, right)
            return node

    def type_spec(self):
        """
        type_spec                   : TYPE
        """
        token = self.current_token
        if token.type in (TokenType.INT, TokenType.DOUBLE, TokenType.VOID):
            self.eat(token.type)
            return Type(
                token=token,
                line=self.lexer.line
            )

    def variable(self):
        """
        variable : ID
        """
        node = Var(self.current_token)
        self.eat(TokenType.ID)
        return node

    def empty(self):
        """An empty production"""
        return NoOp()

    def expr(self):
        """
        expr : term ((PLUS | MINUS) term)*
        """
        node = self.term()

        while self.current_token.type in (TokenType.PLUS, TokenType.MINUS):
            token = self.current_token
            if token.type == TokenType.PLUS:
                self.eat(TokenType.PLUS)
            elif token.type == TokenType.MINUS:
                self.eat(TokenType.MINUS)

            node = BinOp(left=node, op=token, right=self.term())

        return node

    def term(self):
        """term : factor ((MUL | DIV) factor)*"""
        node = self.factor()

        while self.current_token.type in (TokenType.MUL, TokenType.DIV, TokenType.MOD):
            token = self.current_token
            if token.type == TokenType.MUL:
                self.eat(TokenType.MUL)
            elif token.type == TokenType.DIV:
                self.eat(TokenType.DIV)
            elif token.type == TokenType.MOD:
                self.eat(TokenType.MOD)

            node = BinOp(left=node, op=token, right=self.factor())

        return node

    def factor(self):
        """factor : PLUS factor
                  | MINUS factor
                  | INTEGER
                  | LPAREN expr RPAREN
                  | variable
        """
        token = self.current_token
        if token.type == TokenType.PLUS:
            self.eat(TokenType.PLUS)
           
            return node
        elif token.type == TokenType.MINUS:
            self.eat(TokenType.MINUS)
      
            return node
        elif token.type == TokenType.INT_CONST:
            self.eat(TokenType.INT_CONST)
            return Num(token)
        elif token.type == TokenType.DOUBLE_CONST:
            self.eat(TokenType.DOUBLE_CONST)
            return Num(token)
        elif token.type == TokenType.LPAREN:
            self.eat(TokenType.LPAREN)
            node = self.expr()
            self.eat(TokenType.RPAREN)
            return node
        else:
            node = self.variable()
            return node


    # def type_spec(self):
    #     """type_spec : INTEGER
    #                  | REAL
    #     """
    #     token = self.current_token
    #     if self.current_token.type == DOUBLE:
    #         self.eat(DOUBLE)
    #     else:
    #         self.eat(INT)
    #     node = Type(token)
    #     return node

    def parse(self):
        """
        program : compound_statement DOT

        compound_statement : BEGIN statement_list END

        statement_list : statement
                       | statement SEMI statement_list

        statement : compound_statement
                  | assignment_statement
                  | empty

        assignment_statement : variable ASSIGN expr

        empty :

        expr: term ((PLUS | MINUS) term)*

        term: factor ((MUL | DIV) factor)*

        factor : PLUS factor
               | MINUS factor
               | INTEGER
               | LPAREN expr RPAREN
               | variable

        variable: ID
        """
        node = self.program()
        if self.current_token.type != TokenType.EOF:
            self.error()

        return node


###############################################################################
#                                                                             #
#  INTERPRETER                                                                #
#                                                                             #
###############################################################################

class NodeVisitor(object):
    def visit(self, node):
        method_name = 'visit_' + type(node).__name__
        visitor = getattr(self, method_name, self.generic_visit)
        return visitor(node)

    def generic_visit(self, node):
        raise Exception('No visit_{} method'.format(type(node).__name__))


class Interpreter(NodeVisitor):

    GLOBAL_SCOPE = {}

    def __init__(self, parser):
        self.parser = parser
        self.memory = Memory()

    def visit_BinOp(self, node):
        if node.op.type == TokenType.PLUS:
            return self.visit(node.left) + self.visit(node.right)
        elif node.op.type == TokenType.MINUS:
            return self.visit(node.left) - self.visit(node.right)
        elif node.op.type == TokenType.MUL:
            return self.visit(node.left) * self.visit(node.right)
        elif node.op.type == TokenType.DIV:
            return self.visit(node.left) / self.visit(node.right)
        elif node.op.type == TokenType.MOD:
            return self.visit(node.left) % self.visit(node.right)    

    def visit_Num(self, node):
        return node.value
    def visit_VarDecl(self, node):
        self.memory.declare(node.var_node.value)
    # def visit_UnaryOp(self, node):
    #     op = node.op.type
    #     if op == PLUS:
    #         return +self.visit(node.expr)
    #     elif op == MINUS:
    #         return -self.visit(node.expr)

    def visit_Compound(self, node):
        for child in node.children:
            self.visit(child)

    def visit_Assign(self, node):
        var_name = node.left.value
        self.GLOBAL_SCOPE[var_name] = self.visit(node.right)

    def visit_Var(self, node):
        var_name = node.value
        val = self.GLOBAL_SCOPE.get(var_name)
        if val is None:
            raise NameError(repr(var_name))
        else:
            return val

    def visit_NoOp(self, node):
        pass

    def interpret(self):
        tree = self.parser.parse()
        if tree is None:
            return ''
        return self.visit(tree)


def main():
    import sys
    text = open(sys.argv[1], 'r').read()

    lexer = Lexer(text)
    parser = Parser(lexer)
    interpreter = Interpreter(parser)
    result = interpreter.interpret()
    print(interpreter.GLOBAL_SCOPE)


if __name__ == '__main__':
    main()