import java.util.Arrays;
import java.util.regex.*;

public class ProgramParser {
	/**
	 * Regular expressions to check the format of the instructions
	 */
	private String origin = "(\\.ORG)\\s+([0-9]|[1-9][0-9]{1,3}|[1-2][0-9]{4}|3[0-2][0-7][0-6][0-7])(?:\\s*\\#.*)?";
	private String data = "(\\.DATA)\\s+(-([0-9]|[1-9][0-9]{1,3}|[1-2][0-9]{4}|3[0-2][0-7][0-6][0-8])|([0-9]|[1-9][0-9]{1,3}|[1-2][0-9]{4}|3[0-2][0-7][0-6][0-7])),\\s+([0-9]|[1-9][0-9]{1,3}|[1-2][0-9]{4}|3[0-2][0-7][0-6][0-7])(?:\\s*\\#.*)?";
	private String jump = "(JMP)\\s+(R[0-7]),\\s+(([0-9]|[1-5][0-9]|6[0-3])|(\\-[0-9]|\\-[1-5][0-9]|\\-6[0-4]))(?:\\s*\\#.*)?";
	private String load = "(LW)\\s+(R[1-7]),\\s+(R[0-7]),\\s+(([0-9]|[1-5][0-9]|6[0-3])|(\\-[0-9]|\\-[1-5][0-9]|\\-6[0-4]))(?:\\s*\\#.*)?";
	private String store = "(SW)\\s+(R[0-7]),\\s+(R[0-7]),\\s+(([0-9]|[1-5][0-9]|6[0-3])|(\\-[0-9]|\\-[1-5][0-9]|\\-6[0-4]))(?:\\s*\\#.*)?";
	private String branch = "(BEQ)\\s+(R[0-7]),\\s+(R[0-7]),\\s+(([0-9]|[1-5][0-9]|6[0-3])|(\\-[0-9]|\\-[1-5][0-9]|\\-6[0-4]))(?:\\s*\\#.*)?";
	private String call = "(JALR)\\s+(R[1-7]),\\s+(R[0-7])(?:\\s*\\#.*)?";
	private String returnCall = "(RET)\\s+(R[0-7])(?:\\s*\\#.*)?";
	private String arithmetic = "(ADD|SUB|NAND|MUL)\\s+(R[1-7]),\\s+(R[0-7]),\\s+(R[0-7])(?:\\s*\\#.*)?";
	private String immediate = "(ADDI)\\s+(R[1-7]),\\s+(R[0-7]),\\s+(([0-9]|[1-5][0-9]|6[0-3])|(\\-[0-9]|\\-[1-5][0-9]|\\-6[0-4]))(?:\\s*\\#.*)?";

	/**
	 * 
	 * @param string
	 *            String to be matched against the regular expressions
	 * @return Array of strings containing the instruction and its operands if
	 *         it matched against one of the regular expressions, otherwise null
	 */
	public String[] match(String string) {
		Pattern pattern = Pattern.compile("(\\.ORG|\\.DATA|JMP|LW|SW|BEQ|JALR|RET|ADDI|ADD|SUB|NAND|MUL).*");
		Matcher matcher = pattern.matcher(string);
		Matcher m;
		if (matcher.matches()) {
			matcher.group(1);
			if (matcher.group(1).equals(".ORG")) {
				m = matcher(string, origin);
				if (m.matches()) {
					return new String[] { m.group(1), m.group(2) };
				}
			} else if (matcher.group(1).equals(".DATA")) {
				m = matcher(string, data);
				if (m.matches()) {
					return new String[] { m.group(1), m.group(2), m.group(5) };
				}
			} else if (matcher.group(1).equals("JMP")) {
				m = matcher(string, jump);
				if (m.matches()) {
					return new String[] { m.group(1), m.group(2), m.group(3) };
				}
			} else if (matcher.group(1).equals("LW")) {
				m = matcher(string, load);
				if (m.matches()) {
					return new String[] { m.group(1), m.group(2), m.group(3), m.group(4) };
				}
			} else if (matcher.group(1).equals("SW")) {
				m = matcher(string, store);
				if (m.matches()) {
					return new String[] { m.group(1), m.group(2), m.group(3), m.group(4) };
				}
			} else if (matcher.group(1).equals("BEQ")) {
				m = matcher(string, branch);
				if (m.matches()) {
					return new String[] { m.group(1), m.group(2), m.group(3), m.group(4) };
				}
			} else if (matcher.group(1).equals("JALR")) {
				m = matcher(string, call);
				if (m.matches()) {
					return new String[] { m.group(1), m.group(2), m.group(3) };
				}
			} else if (matcher.group(1).equals("RET")) {
				m = matcher(string, returnCall);
				if (m.matches()) {
					return new String[] { m.group(1), m.group(2) };
				}
			} else if (matcher.group(1).equals("ADDI")) {
				m = matcher(string, immediate);
				if (m.matches()) {
					return new String[] { m.group(1), m.group(2), m.group(3), m.group(4) };
				}
			} else if (matcher.group(1).equals("ADD") || matcher.group(1).equals("SUB")
					|| matcher.group(1).equals("NAND") || matcher.group(1).equals("MUL")) {
				m = matcher(string, arithmetic);
				if (m.matches()) {
					return new String[] { m.group(1), m.group(2), m.group(3), m.group(4) };
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @param string String to be matched
	 * @param regex Regular expression to be matched against
	 * @return
	 */
	public Matcher matcher(String string, String regex) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(string);
		return matcher;
	}

	public static void main(String[] args) {
		ProgramParser pp = new ProgramParser();

		System.out.println(Arrays.toString(pp.match(".ORG 31765 #$$$$")));
		System.out.println(Arrays.toString(pp.match(".DATA -32768, 32767 #$#$#$#")));
		System.out.println(Arrays.toString(pp.match("JMP R4, 10")));
		System.out.println(Arrays.toString(pp.match("LW R1, R5, -11")));
		System.out.println(Arrays.toString(pp.match("SW R0, R5, -10")));
		System.out.println(Arrays.toString(pp.match("BEQ R0, R0, -40")));
		System.out.println(Arrays.toString(pp.match("JALR R1, R5")));
		System.out.println(Arrays.toString(pp.match("RET R0")));
		System.out.println(Arrays.toString(pp.match("ADD R1, R6, R0")));
		System.out.println(Arrays.toString(pp.match("ADDI R3, R5, 40# this is a comment")));
		System.out.println(Arrays.toString(pp.match("ADDI R3, R5, -40; this is not a comment"))); // returns
																									// null
		System.out.println(Arrays.toString(pp.match("ADDI R3, R5, -65"))); // returns
																			// null:
																			// Immediate
																			// value
																			// beyond
																			// range
																			// [-64,
																			// 63]
		System.out.println(Arrays.toString(pp.match("ADDI R3, R5, 64"))); // returns
																			// null:
																			// Immediate
																			// value
																			// beyond
																			// range
																			// [-64,
																			// 63]
	}
}
