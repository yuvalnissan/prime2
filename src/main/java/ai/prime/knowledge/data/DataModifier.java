package ai.prime.knowledge.data;

public enum DataModifier {
	POSITIVE(""),
	NEGATIVE("NOT");

	private String displayName;

	private DataModifier(String displayName){
		this.displayName = displayName;
	}

	public String getDisplayName(){
		return displayName;
	}

	public DataModifier applyModifier(DataModifier other){
		
		if (this.equals(POSITIVE) && other.equals(POSITIVE)){
			return POSITIVE;
		}else if (this.equals(POSITIVE) && other.equals(NEGATIVE)){
			return NEGATIVE;
		}else if (this.equals(NEGATIVE) && other.equals(POSITIVE)){
			return NEGATIVE;
		}else if (this.equals(NEGATIVE) && other.equals(NEGATIVE)){
			return POSITIVE;
		}else{
			throw new RuntimeException("Should not happen");
		}
		
	}
	
	
}
