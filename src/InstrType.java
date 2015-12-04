
/**
 *
 * @author andrewmagdy
 */

/*
 Categories
 Cat 1
 op regA, regB, Imm

 Cat 2
 op regA, Imm

 Cat 3 
 op regA, regB

 Cat 4
 op regA
    
 Cat 5
 op regA, regB, regC
 */
public enum InstrType {

    LW(1), SW(1), BEQ(1), ADDI(1),
    JMP(2),
    JALR(3),
    RET(4),
    ADD(5), SUB(5), NAND(5), MUL(5);

    private int category;

    public int getCategory() {
        return category;
    }

    InstrType(int type) {
        this.category = type;
    }
}
