
/**
 *
 * @author andrewmagdy
 */

/*

 Type 1
 op regA, regB, Imm

 Type 2
 op regA, Imm

 Type 3 
 op regA, regB

 Type 4
 op regA
    
 Type 5
 op regA, regB, regC
 */
public class Instruction {

    private InstrType instrType;
    private int regA = 0, regB = 0, regC = 0;
    private int imm = -1;

    private int parseField(String[] strInstr, int field) {

        return Integer.parseInt(strInstr[field].substring(1));
    }

    public Instruction(String[] strInstr) {
        instrType = InstrType.valueOf(strInstr[0]);

        switch (instrType.getCategory()) {
            case 1:
                regA = parseField(strInstr, 1);
                regB = parseField(strInstr, 2);
                imm = parseField(strInstr, 3);
                break;
            case 2:
                regA = parseField(strInstr, 1);
                imm = parseField(strInstr, 2);
                break;
            case 3:
                regA = parseField(strInstr, 1);
                regB = parseField(strInstr, 2);
                break;
            case 4:
                regA = parseField(strInstr, 1);
                break;
            case 5:
                regA = parseField(strInstr, 1);
                regB = parseField(strInstr, 2);
                regC = parseField(strInstr, 3);
                break;
        }
    }

    public InstrType getInstrType() {
        return instrType;
    }

    public int getRegA() {
        return regA;
    }

    public int getRegB() {
        return regB;
    }

    public int getRegC() {
        return regC;
    }

    public int getImm() {
        return imm;
    }

}
