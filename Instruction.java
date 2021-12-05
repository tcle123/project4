package cpsc3300.project4.simulator;

public class Instruction {

	private int   instr;
	private byte  opcode;
	private byte  rs;
	private byte  rt;
	private short tail;

	public Instruction(int instr) {
		this.instr = instr;
		// TODO: Set opcode, rs, rt, and tail based on binary data in instr
		// logical bit shift to the right to get the opcode
		opcode = (byte) (instr >>> 26);
		// left shift instruction to get rid of opcode, then logical right shift to get rs
		int leftshifted_rs = (byte) (instr << 6);
		rs = (byte) (leftshifted_rs >>> 27);

		// left shift instruction to get rid of opcode, rs, then logical right shift to get rt
		int leftshifted_rt = (byte) (instr << 11);
		rt = (byte) (leftshifted_rt >>> 27);

		// if i-type instruction, left shift by 16 to get rid of opcode, rs, rt and then right shift by 16
		if((opcode == (byte) 35) || (opcode == (byte) 43) || (opcode == (byte) 4)) {
			int leftshifted_tail = (byte) (instr << 16);
			tail = (byte) (leftshifted_tail >>> 16);
		}

		// if j-type instruction
		else if ((opcode == (byte) 2)) {
			// left shift instruction to get rid of opcode, rs, rt, then logical right shift to get tail
			int leftshifted_tail = (byte) (instr << 6);
			tail = (byte) (leftshifted_tail >>> 6);
		}

		// if r-type instruction
		// idk it doesnt have a tail really
		else {
			tail = 0;
		}
	}

	public int getInstr() {
		return instr;
	}

	public byte getOpcode() {
		return opcode;
	}

	public byte getRs() {
		return rs;
	}

	public byte getRt() {
		return rt;
	}

	public short getAddress() {
		return tail;
	}

	public int getExtendedAddress() {
		return tail;
	}

	public byte getRd() {
		// TODO: Isolate Rd value from instruction
		byte rd;
		int leftshifted_rd = (byte) (instr << 16);
		rd = (byte) (leftshifted_rd >>> 27);
		return rd;
	}

	public byte getShamt() {
		// TODO: Isolate Shamt value from instruction
		byte shamt;
		int leftshifted_shamt = (byte) (instr << 21);
		shamt = (byte) (leftshifted_shamt >>> 27);
		return shamt;
	}

	public byte getFunct() {
		// TODO: Isolate Funct value from instruction
		byte funct;
		int leftshifted_funct = (byte) (instr << 26);
		funct = (byte) (leftshifted_funct >>> 26);
		return funct;
	}

	public int getJumpAddress() {
		// TODO: Isolate address value from jump instruction
		byte jump;
		int leftshifted_jump = (byte) (instr << 6);
		jump = (byte) (leftshifted_jump >>> 6);
		return jump;
	}

	public int getAluOp() {
		int aluOp_val = 0;
		switch (opcode) {
			// TODO: Make cases for various types based on opcode, returning the appropriate ALU Op
			// r-types have an opcode of 0
			// and an ALU op of 10
			case (byte)0:
				aluOp_val = 10;
				break;

			//i-type instructions LW and SW have an opcode of 100011 (35) and 101011 (43)
			// and an ALU op of 0
			case (byte)35:
			case (byte)43:
				aluOp_val = 0;
				break;

			// i-type instruction beq has an opcode of 000100 (4)
			// and an ALU op of 01
			case (byte)4:
				aluOp_val = 1;
				break;

			// j-type instruction jump has an opcode of 000010 (2)
			// and no ALU op
			case (byte)2:
				aluOp_val = 23;
				break;

			default:
				aluOp_val = 0;
		}
		return aluOp_val;
	}

	public String decode() {
		//TODO convert instruction back to assembly code
		String instruction_assembly = "";
		int ALU_op = getAluOp();

		// r type
		// add/sub/and/or rd,rs,rt
		if(ALU_op == 10) {
			int func_code = Byte.toUnsignedInt(getFunct());

			// if statement for and
			if(func_code == 36){
				instruction_assembly = instruction_assembly + "and";
			}
			// if for add
			if(func_code == 32){
				instruction_assembly = instruction_assembly + "add";
			}
			// if for sub
			if(func_code == 34){
				instruction_assembly = instruction_assembly + "sub";
			}
			// if for OR
			if(func_code == 37){
				instruction_assembly = instruction_assembly + "or";
			}

			// variables
			int rd_code = Byte.toUnsignedInt(getRd());
			int rs_code = Byte.toUnsignedInt(getRs());
			int rt_code = Byte.toUnsignedInt(getRt());

			int code[] = {rd_code, rs_code, rt_code};

			for(int i = 0; i < code.length; i++){
				if(code[i] == 0){
					instruction_assembly = instruction_assembly + "$zero";
				}
				else if(code[i] == 1){
					instruction_assembly = instruction_assembly + "$at";
				}
				else if(code[i] > 1 && code[i] < 4){
					instruction_assembly = instruction_assembly + "$v" + (code[i] - 2);
				}
				else if(code[i] > 3 && code[i] < 8){
					instruction_assembly = instruction_assembly + "$a" + (code[i] - 4);
				}
				else if(code[i] > 7 && code[i] < 16){
					instruction_assembly = instruction_assembly + "$t" + (code[i] - 8);
				}
				else if(code[i] > 15 && code[i] < 24){
					instruction_assembly = instruction_assembly + "$s" + (code[i] - 16);
				}
				else if(code[i] > 23 && code[i] < 26){
					instruction_assembly = instruction_assembly + "$t" + (code[i] - 16);
				}
				else if(code[i] > 25 && code[i] < 28){
					instruction_assembly = instruction_assembly + "$k" + (code[i] - 26);
				}
				else if(code[i] == 28){
					instruction_assembly = instruction_assembly + "$gp";
				}
				else if(code[i] == 29){
					instruction_assembly = instruction_assembly + "$sp";
				}
				else if(code[i] == 30){
					instruction_assembly = instruction_assembly + "$fp";
				}
				else if(code[i] == 31){
					instruction_assembly = instruction_assembly + "$ra";
				}
				else{
					System.out.println("Error loading assembly instructions");
					System.exit(0);
				}

				instruction_assembly = instruction_assembly + ", ";
			}
		}

		// i-type: LW and SW
		// i-type: beq
		else if(ALU_op == 0 || ALU_op == 1) {
			// lw is 100011 (35)
			if(opcode == (byte)35) {
				instruction_assembly = instruction_assembly + "lw ";
			}

			// sw is 101011 (43)
			else if(opcode == (byte)43) {
				instruction_assembly = instruction_assembly + "sw ";
			}

			// beq is 00100
			else if(opcode == (byte)4) {
				instruction_assembly = instruction_assembly + "beq ";
			}

			// turn rt and rs into ints
			int rt_code = Byte.toUnsignedInt(getRt());
			int rs_code = Byte.toUnsignedInt(getRs());

			// rt and rs as strings
			String rs_string = "";
			String rt_string = "";

			int code[] = {rt_code, rs_code};
			String[] string_code = {rt_string, rs_string};

			for(int i = 0; i < code.length; i++) {
				if (code[i] == 0) {
					string_code[i] = "$zero";
				}
				else if (code[i] == 1) {
					string_code[i] = "$at";
				}
				else if (code[i] > 1 && code[i] < 4) {
					string_code[i] = "$v" + (code[i] - 2);
				}
				else if (code[i] > 3 && code[i] < 8) {
					string_code[i] = "$a" + (code[i] - 4);
				}
				else if (code[i] > 7 && code[i] < 16) {
					string_code[i] = "$t" + (code[i] - 8);
				}
				else if (code[i] > 15 && code[i] < 24) {
					string_code[i] = "$s" + (code[i] - 16);
				}
				else if (code[i] > 23 && code[i] < 26) {
					string_code[i] = "$t" + (code[i] - 16);
				}
				else if (code[i] > 25 && code[i] < 28) {
					string_code[i] = "$k" + (code[i] - 26);
				}
				else if (code[i] == 28) {
					string_code[i] = "$gp";
				}
				else if (code[i] == 29) {
					string_code[i] = "$sp";
				}
				else if (code[i] == 30) {
					string_code[i] = "$fp";
				}
				else if (code[i] == 31) {
					string_code[i] = "$ra";
				}
				else {
					System.out.println("Error loading assembly instructions");
					System.exit(0);
				}
			}

			// address
			short immediate = getAddress();

			// for LW and SW
			// lw/sw rt, imm(rs)
			if(ALU_op == 0) {
				instruction_assembly = instruction_assembly + string_code[0] + ", " + immediate + "(" + string_code[1] + ")";
			}

			// for beq
			// beq rs, rt, label
			else {
				instruction_assembly = instruction_assembly + string_code[1] + ", " + string_code[0] + ", " + immediate;
			}
		}

		// j-type: jump
		// format: j target
		else {
			instruction_assembly = instruction_assembly + "j ";
			int address = getJumpAddress();
			instruction_assembly = instruction_assembly + address;
		}

		return instruction_assembly;
	}

	@Override
	public String toString() {
		String bin = Integer.toBinaryString(instr);
		//left pad with zeros to be full 32 bit width
		return "00000000000000000000000000000000".substring(bin.length()) + bin;
	}
}
