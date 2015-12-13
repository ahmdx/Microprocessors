
public class HardwareOrganization {
	int pipelineWidth;
	int instructionBufferSize;
	int loadStoreReservationNumber;
	int loadStoreCycles;
	int multiplyReservationNumber;
	int multiplyCycles;
	int addSubtractReservationNumber;
	int addSubtractCycles;
	int robentries;
	
	public HardwareOrganization(){
		
	}
	
	public String toString(){
		return "" + this.addSubtractCycles + this.addSubtractReservationNumber + this.instructionBufferSize +
				this.loadStoreCycles + this.loadStoreReservationNumber + this.multiplyCycles+
				this.pipelineWidth; 
	}


}
