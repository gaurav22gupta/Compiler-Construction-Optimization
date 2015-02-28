package Frontend;
import java.util.*;

import Frontend.Result.Type;

public class BasicBlock {
	public enum BlockType{
		main,iftrue,ifelse,join,whileblock,doblock,follow
	}
	public static int block_id;
	public static BasicBlock mainblock;
	public static HashMap<Integer,BasicBlock>basicblocks=new HashMap<Integer,BasicBlock>();
	private int blockno;
	private BlockType kind;
	private BasicBlock nextblock;	// for if,while and do
	private BasicBlock ifelseblock;	// for if
	private BasicBlock joinblock;   // for if
	private BasicBlock prevblock;	
	private BasicBlock prevblock2;  // for joinblock only
	private BasicBlock followblock;	//for while
	private BasicBlock dotowhileblock;
	
	public int start_instruction_index;
	public int end_instruction_index;
	
	public Instruction start_Instr;	//starting instruction
	public Instruction end_Instr;	//ending instruction
	
	public ArrayList<Instruction> 	 inst_list;
	public ArrayList<Integer> out_set;	//live set after end of basic block
	public ArrayList<Integer> in_set;	//live set before beginning of basic block
	
	BasicBlock(){
		inst_list = new ArrayList<Instruction>();
		this.kind=BlockType.main;
		this.block_id = 0;
		this.blockno = 0;
		basicblocks.put(block_id, this);
	}
	
	BasicBlock(BlockType kind){
		inst_list = new ArrayList<Instruction>();
		this.kind	=kind;
		this.blockno = block_id;
		basicblocks.put(block_id, this);
	}
	
	public BasicBlock createIfTrue(){
		
		BasicBlock iftrue=new BasicBlock(BlockType.iftrue);
		this.nextblock=iftrue;
		iftrue.prevblock=this;
		basicblocks.put(block_id, iftrue);
		return iftrue;
	}
	
	public BasicBlock createElse(){
		BasicBlock ifelse=new BasicBlock(BlockType.ifelse);
		this.ifelseblock=ifelse;
		ifelse.prevblock=this;
		basicblocks.put(block_id, ifelse);
		return ifelse;
	}
	
	public BasicBlock createWhile(){
		BasicBlock whileblock=new BasicBlock(BlockType.whileblock);
		this.nextblock=whileblock;
		whileblock.prevblock=this;
		basicblocks.put(block_id, whileblock);
		return whileblock;
	}
	
	public BasicBlock createdo(){
		BasicBlock doblock=new BasicBlock(BlockType.doblock);
		this.nextblock=doblock;
		doblock.prevblock=this;
		basicblocks.put(block_id, doblock);
	//	doblock.nextblock=this;
		//this.prevblock2=doblock;
		return doblock;
	}
	
	public BasicBlock createfollow(){
		BasicBlock follow=new BasicBlock(BlockType.follow);
		this.followblock=follow;
		follow.prevblock=this;
		basicblocks.put(block_id, follow);
		return follow;
	}
		
	public BasicBlock createjoin(){		//only if will do this
		BasicBlock join=new BasicBlock(BlockType.join);
		this.joinblock=join;
		join.prevblock=this;
		basicblocks.put(block_id, join);
		return join;
	}
	
	public void setjoin(BasicBlock phi_block){		//only else and main(when no else is there) can do this
	//	if (this.kind==BlockType.ifelse){
			this.joinblock = phi_block;
			//this.joinblock=this.prevblock.nextblock.joinblock;	
			this.joinblock.prevblock2=this;	
		/*}
		else{
			this.joinblock=this.nextblock.joinblock;	
			this.nextblock.joinblock.prevblock2=this;
		}*/
	}
	public void setdotowhile(BasicBlock while_block){	
		this.dotowhileblock=while_block;
	}
	public boolean checkdotowhile(){
		boolean b;
		if(this.dotowhileblock!=null)
			b=true;
		else
			b=false;
		return b;
	}
	public BasicBlock getdotowhile(){	
		return this.dotowhileblock;
	}
	public void setStartInstructionIndex(int index){
		start_instruction_index=index;
	}
	public void setEndInstructionIndex(int index){
		end_instruction_index=index;
	}
	public BlockType getType(){
		return this.kind;
	}
	public int getblockno(){
		return this.blockno;
	}
	public void changeType(BlockType bbtype){
		this.kind=bbtype;
	}
	public BasicBlock getprevblock(){
		return this.prevblock;
	}
	public BasicBlock getprevblock2(){
		return this.prevblock2;
	}
	public BasicBlock getnextblock(){
		return this.nextblock;
	}
	public BasicBlock getifelseblock(){
		return this.ifelseblock;
	}
	public BasicBlock getjoinblock(){
		return this.joinblock;
	}
	public BasicBlock getfollowblock(){
		return this.followblock;
	}
	public void setfollowblocknull(){
		this.followblock=null;
	}
	public int getStartInstructionIndex(){
		return this.start_instruction_index;
	}
	public int getEndInstructionIndex(){
		return this.end_instruction_index;
	}
	public static void decblockid(){
		block_id--;
	}
	
	public ArrayList<String> printInstructions(){
		ArrayList<String> bb_insts=new ArrayList<>();
		for(Instruction inst:inst_list){
			String oper1;String oper2;
			StringBuilder instruction_print= new StringBuilder(Integer.toString(Parser.insts.indexOf(inst))).append(":").append(inst.getOperator());
			ArrayList<Result> operands=inst.getOperands();
			if (operands!= null){
			if(operands.size()==2){
				Result op1=operands.get(0);
				if(op1.getType()==Type.number)
					oper1= new StringBuilder(" #").append(op1.getValue()).toString();
				else if(op1.getType()==Type.instruction)
					oper1= new StringBuilder(" (").append(Parser.insts.indexOf(op1.getInstruction())).append(") ").toString();
				else if (op1.getType()==Type.variable)
					oper1= new StringBuilder(" ").append(op1.getName()).toString();
				else if(op1.getType()==Type.arr)
					oper1= new StringBuilder(" ").append(op1.getName()).toString();
				else
					oper1="error"+op1.getType().toString();
			
				Result op2=operands.get(1);
				if(op2.getType()==Type.number)
					oper2= new StringBuilder(" #").append(op2.getValue()).toString();
				else if(op2.getType()==Type.instruction)
					oper2= new StringBuilder(" (").append(Parser.insts.indexOf(op2.getInstruction())).append(") ").toString();
				else if (op2.getType()==Type.variable)
					oper2= new StringBuilder(" ").append(op2.getName()).toString();
				else if(op2.getType()==Type.arr)
					oper2= new StringBuilder(" ").append(op2.getName()).toString();
				else
					oper2="error"+op2.getType().toString();
				
				if(inst.getOperator()=="phi"){
					String phivar=inst.getPhiVar();
					instruction_print.append(" ").append(phivar).append(oper1).append(oper2);
				}
				else
					instruction_print.append(oper1).append(oper2);
			}
			else if (operands.size()==1){
				Result op1=operands.get(0);
				if(op1.getType()==Type.number)
					oper1= new StringBuilder(" #").append(op1.getValue()).toString();
				else if(op1.getType()==Type.instruction)
					oper1= new StringBuilder(" (").append(Parser.insts.indexOf(op1.getInstruction())).append(") ").toString();
				else
					oper1="error";
				
				instruction_print.append(oper1);
			}
			}	
			  bb_insts.add(instruction_print.toString());
		}
		return bb_insts;
	}


}
