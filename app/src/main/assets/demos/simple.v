module smpl_circuit (A,B,AND,NAND,OR,NOR,XOR,XNOR,BUF,NOT);
input A,B;
output AND,NAND,OR,NOR,XOR,XNOR,BUF,NOT;

and #10 g0(AND,A,B);
nand #10 g1(NAND,A,B);
or #10 g2(OR,A,B);
nor #10 g3(NOR,A,B);
xor #10 g4(XOR,A,B);
xnor #10 g5(XNOR,A,B);
buf #10 g6(BUF,A);
not #10 (NOT,A);

endmodule

