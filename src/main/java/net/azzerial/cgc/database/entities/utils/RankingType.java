package net.azzerial.cgc.database.entities.utils;

public enum RankingType {
	IDOL_COLLECTION(0),
	BALANCE(1);

	private int type;

	RankingType(int type) {
		this.type = type;
	}

	public int getValue() {
		return (type);
	}

}
