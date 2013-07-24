package edu.uw.cs.lil.temporal.structures;

import edu.uw.cs.lil.temporal.TemporalMain;
import edu.uw.cs.lil.tiny.data.ILabeledDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.utils.composites.Pair;

public class TemporalMention implements java.io.Serializable, ILabeledDataItem<Pair<Sentence, TemporalMention>, TemporalResult>{
	private static final long serialVersionUID = -5859852309847402300L;
	private TemporalSentence sentence;
	private String type;
	private String value;
	private int tokenStart;
	private int tokenEnd; //inclusive-exclusive

	// Temporary variables used only during preprocessing
	private int charStart; //character offset
	private int charEnd;

	// Used during preprocessing
	public TemporalMention(TemporalSentence sentence, String type, String value, int charStart) {
		this.sentence = sentence;
		this.type = type;
		this.value = value;
		this.charStart = charStart;
	}

	// Used during detection
	public TemporalMention(TemporalSentence sentence, int tokenStart, int tokenEnd) {
		this.sentence = sentence;
		setTokenRange(tokenStart, tokenEnd);
	}

	public void setTokenRange(int tokenStart, int tokenEnd) {
		this.tokenStart = tokenStart;
		this.tokenEnd = tokenEnd;
	}

	public void setCharEnd(String text) {
		charEnd = charStart + text.length();
	}

	public int getStartChar() {
		return charStart;
	}

	public int getEndChar() {
		return charEnd;
	}

	public int getStartToken() {
		return tokenStart;
	}

	public int getEndToken() {
		return tokenEnd;
	}

	public String getType() {
		return type;
	}

	public String getValue() {
		return value;
	}
	
	public TemporalSentence getSentence() {
		return sentence;
	}
	
	public String toString() {
		String s = "";
		s += "Phrase:            " + getPhrase().toString() + "\n";
		s += "Sentence:          " + getSentence().toString() + "\n";
		s += "Reference time:    " + getSentence().getReferenceTime() + "\n";
		s += "Gold val:          " + value + "\n";
		s += "Gold type:         " + type + "\n";
		return s;
	}
	
	public Sentence getPhrase() {
		return new Sentence(sentence.getTokens().subList(tokenStart, tokenEnd));
	}

	public boolean matches(TemporalMention other) {
		if (TemporalMain.STRICT_MATCHING)
			return this.tokenStart == other.tokenStart && this.tokenEnd == other.tokenEnd;
		else
			return overlapsWith(other);
	}

	public void mergeWith(TemporalMention gm) {
		this.type = gm.type;
		this.value = gm.value;
	}

	public boolean overlapsWith(TemporalMention other) {
		return this.tokenEnd >= other.tokenStart && this.tokenStart <= other.tokenEnd;
	}

	@Override
	public double calculateLoss(TemporalResult label) {
		return 0;
	}

	@Override
	public boolean prune(TemporalResult y) {
		throw new IllegalArgumentException("Cannot prune a TemporalSentence becuase it doesn't contain logic.");
	}

	@Override
	public double quality() {
		return 1.0D;
	}

	@Override
	public Pair<Sentence, TemporalMention> getSample() {
		return Pair.of(getPhrase(), this);
	}

	@Override
	public TemporalResult getLabel() {
		return new TemporalResult(null, type, value, null, null, null);
	}

	@Override
	public boolean isCorrect(TemporalResult other) {
		return other.type.equals(type) && other.value.equals(value);
	}

	public void setSentence(TemporalSentence sentence) {
		this.sentence = sentence;
	}

}