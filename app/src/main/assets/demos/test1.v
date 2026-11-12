module circuit(A,B,C,y);
input  A,B;
output y;
wire e;

  and #30 g1(e,A,B); 
  or #30 g2(y,e,C);

endmodule
