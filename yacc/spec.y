%{
#include <stdio.h>
#include <stdlib.h>
#define YYDEGUG 1
%}

%token ID
%token CONST
%token IF
%token WHILE
%token ELSE
%token BOOL
%token CHAR
%token INT
%token STRING
%token FLOAT
%token UNARY_OPERATOR
%token BINARY_OPERATOR
%token ASSIGN
%token SEPARATOR
%token INT_CONST
%token READ
%token WRITE
%token SEMICOLON
%token OPEN_SQUARE
%token CLOSED_SQUARE
%token OPEN_ROUND
%token CLOSED_ROUND
%token OPEN_BRACKET
%token CLOSED_BRACKET

%left UNARY_OPERATOR
%left BINARY_OPERATOR

%%
program:        statement_list                  {printf("program -> statement_list\n");}
                ;

statement_list: statement                       {printf("statement_list -> statement\n");}
                | statement statement_list      {printf("statement_list -> statement statement_list\n");}
                ;

statement:      simple_statement                {printf("statement -> simple_statement\n");}
                | compound_statement            {printf("statement -> compound_statement\n");}
                ;

simple_statement: assignment_statement SEMICOLON        {printf("simple_statement -> assignement_statement;\n");}
                | iostatement SEMICOLON                 {printf("simple_statement -> iostatement;\n");}
                | declaration_statement SEMICOLON       {printf("simple_statement -> declaration_statement;\n");}

                ;

compound_statement: if_statement                {printf("compound_statement -> if_statement\n");}
                    | while_statement           {printf("compound_statement -> while_statement\n");}
                    ;
simple_type:    CHAR                            {printf("simple_type -> char\n");}
                | INT                           {printf("simple_type -> int\n");}
                | STRING                        {printf("simple_type -> string\n");}
                | FLOAT                         {printf("simple_type -> float\n");}
                | BOOL                          {printf("simple_type -> bool\n");}
                ;

array_type:     simple_type OPEN_SQUARE INT_CONST CLOSED_SQUARE {printf("array_type -> simple_type[INT_CONST]\n");}
                ;

type:           simple_type                     {printf("type -> simple_type\n");}
                | array_type                    {printf("type -> array_type\n");}
                ;

constant:       CONST                           {printf("constant -> CONST\n");}
                | INT_CONST                     {printf("constant -> INT_CONST\n");}
                ;
number_or_id:   ID                              {printf("number_or_id -> ID\n");}
                | INT_CONST                     {printf("number_or_id -> INT_CONST\n");}

expression:     constant                        {printf("expression -> constant\n");}
                | ID                            {printf("expression -> ID\n");}
                | ID OPEN_SQUARE number_or_id CLOSED_SQUARE             {printf("epxression -> ID[INT_CONST]\n");}
                | expression BINARY_OPERATOR expression {printf("expression -> expression BINARY_OPERATOR expression\n");}
                | UNARY_OPERATOR expression     {printf("expression -> UNARY_OPERATOR expression\n");}
                | OPEN_ROUND expression CLOSED_ROUND            {printf("expression -> (expression)\n");}
                ;

declaration_statement:  type ID                 {printf("declaration_statement -> type ID\n");}
                        | type ID ASSIGN expression {printf("declaration_statement -> type ID=expression");}
                        ;
iostatement:    READ ID                         {printf("iostatement -> << ID\n");}
                | READ ID OPEN_SQUARE number_or_id CLOSED_SQUARE {printf("iostatement -> <<id[number_or_id]\n");}
                | WRITE expression              {printf("iostatement -> >> expression\n");}
                ;
assignment_statement: ID ASSIGN expression      {printf("assignment_statement -> ID = expression\n");}
                        ;


if_statement: IF OPEN_ROUND expression CLOSED_ROUND OPEN_BRACKET statement_list CLOSED_BRACKET else_branch {printf("if_statement -> if(expression){statement_list} else_branch\n");}
                ;

else_branch:    {printf("else_branch -> epsilon\n");}
                | ELSE OPEN_BRACKET statement_list CLOSED_BRACKET {printf("else_branch -> else{statement_list}\n");}
                ;

while_statement: WHILE OPEN_ROUND expression CLOSED_ROUND OPEN_BRACKET statement_list CLOSED_BRACKET    {printf("while_statement -> while(expression){statement_list}\n");}
                ;
%%

yyerror(char *s, int line){
        if(line>=0 && line <=9999)
                printf("Error: %s on line %d\n", s, line);
        else
                printf("%s\n", s);
}

extern FILE *yyin;

main(int argc, char **argv){
  if(argc>1) yyin = fopen(argv[1], "r");
  //if((argc>2)&&(!strcmp(argv[2],"-d"))) yydebug = 1;
  if(!yyparse()) fprintf(stderr,"\tO.K.\n");
}