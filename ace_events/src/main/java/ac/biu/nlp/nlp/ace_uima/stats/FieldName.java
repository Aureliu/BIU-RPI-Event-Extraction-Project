package ac.biu.nlp.nlp.ace_uima.stats;

public class FieldName {
	
	private String nameLvl1;
	private String nameLvl2;
	
	public String getLvl1() {
		return nameLvl1;
	}
	
	public String getLvl2() {
		return nameLvl2;
	}

	public FieldName(String nameLvl1, String nameLvl2) {
		super();
		this.nameLvl1 = normalize(nameLvl1);
		this.nameLvl2 = normalize(nameLvl2);
	}
	
	public String toString() {
		return String.format("(%s,%s)", nameLvl1, nameLvl2);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((nameLvl1 == null) ? 0 : nameLvl1.hashCode());
		result = prime * result
				+ ((nameLvl2 == null) ? 0 : nameLvl2.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		FieldName other = (FieldName) obj;
		if (nameLvl1 == null) {
			if (other.nameLvl1 != null) {
				return false;
			}
		} else if (!nameLvl1.equals(other.nameLvl1)) {
			return false;
		}
		if (nameLvl2 == null) {
			if (other.nameLvl2 != null) {
				return false;
			}
		} else if (!nameLvl2.equals(other.nameLvl2)) {
			return false;
		}
		return true;
	}
	
	private String normalize(String input) {
		if (input==null || input.equals("")) {
			return " ";
		}
		return input;
	}
}
