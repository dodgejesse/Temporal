package edu.uw.cs.lil.tiny.tempeval.featuresets;

import java.util.Arrays;
import java.util.List;

import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.tempeval.structures.TemporalMention;
import edu.uw.cs.lil.tiny.tempeval.types.TemporalDate;
import edu.uw.cs.lil.tiny.tempeval.util.TemporalJoda;
import edu.uw.cs.lil.tiny.utils.hashvector.IHashVector;
import edu.uw.cs.lil.tiny.utils.hashvector.IHashVectorImmutable;
import edu.uw.cs.utils.composites.Pair;

public class TemporalDayOfWeekFeatureSet extends TemporalFeatureSet{
	protected IHashVectorImmutable setTemporalFeats(LogicalExpression logic, IHashVector feats, IDataItem<Pair<Sentence, TemporalMention>> dataItem) {
		List<String> weekdays = Arrays.asList("monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday");
		for (String s : weekdays){
			if (logic.toString().contains(s)){
				TemporalDate referenceTime = TemporalDate.readDocumentDate(dataItem.getSample().second().getSentence().getReferenceTime());
				if (weekdays.get((TemporalJoda.convertISOToLocalDate(referenceTime).dayOfWeek().get())-1).equals(s))
					feats.set(getFeatureTag() + "sameDay_" + getOuterPred(logic.toString()), 1);
				else
					feats.set(getFeatureTag() + "notSameDay" + getOuterPred(logic.toString()), 1);
			}
		}
		
		return feats;
	}

	@Override
	protected String getFeatureTag() {
		return "TEMPORAL_DAY_";
	}
}
