package edu.berkeley.nlp.assignments;

import edu.berkeley.nlp.io.PennTreebankReader;
import edu.berkeley.nlp.ling.Tree;
import edu.berkeley.nlp.ling.Trees;
import edu.berkeley.nlp.ling.Trees.PennTreeRenderer;
import edu.berkeley.nlp.parser.EnglishPennTreebankParseEvaluator;
import edu.berkeley.nlp.util.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
import java.util.PriorityQueue;

/**
 * Harness for PCFG Parser project.
 * 
 * @author Dan Klein
 */
public class PCFGParserTester {

	/**
	 * Parsers are required to map sentences to trees. How a parser is
	 * constructed and trained is not specified.
	 */
	static interface Parser {
		Tree<String> getBestParse(List<String> sentence);
	}
	
	static class NodeInfo implements Comparable<NodeInfo>{
		final String tag;
		final double prob;
		final NodeInfo firstChild;
		final NodeInfo secondChild;
		
		public NodeInfo(String s){
			this(s, 1, null);
		}
		
		public NodeInfo(String s, double p, NodeInfo n){
			this(s, p, n, null);
		}
		
		public NodeInfo(String s, double p, NodeInfo left, NodeInfo right){
			tag = s;
			prob = p;
			firstChild = left;
			secondChild = right;
		}
		
		public boolean isUnary(){
			return secondChild == null;
		}

		public int compareTo(NodeInfo other) {
			if (other.prob < prob)
				return -1;
			else if (other.prob > prob)
				return 1;
			else
				return 0;
		}
		
		public boolean equals(Object other){
			boolean result = other instanceof NodeInfo;
			if (result){
				NodeInfo o = (NodeInfo) other;
				if (this.prob != o.prob
						|| (!testSameChildrenName(this.firstChild.tag, o.firstChild.tag))
						|| (!testSameChildrenName(this.secondChild.tag, o.secondChild.tag)))
					result = false;
			}
			return result;
		}
		
		private boolean testSameChildrenName(String f, String s){
			if (f == null && s == null)
				return true;
			if ((f == null && s != null) || f != null && s == null)
				return false;
			return !f.equals(s);
		}
		
		public int hashCode(){
			return tag.hashCode();
		}
	}

	static class MyParser implements Parser {
		//CounterMap<List<String>, Tree<String>> knownParses;
		CounterMap<Integer, String> spanToCategories;
		Lexicon lexicon;
		Grammar grammar;
		UnaryClosure uc;
		PrintWriter output;
		boolean secondOrderVert;
		boolean auxVerbSplit;
		boolean conjSplit;
		
		//@Override
		public Tree<String> getBestParseThree(List<String> sentence){
			// int -> int -> string -> NodeInfo
			Map<Integer, Map<Integer, Map<String, NodeInfo>>> chart = new HashMap<Integer, Map<Integer, Map<String, NodeInfo>>>();
			// first go through the words and list their tags with probabilities
			for (int i = 0; i < sentence.size(); i++){
				checkChart(chart, i, i+1);
				for (String tag : lexicon.getAllTags()){
					//make nodeInfo object for word
					NodeInfo word = new NodeInfo(sentence.get(i));
					NodeInfo wordTag = new NodeInfo(tag, lexicon.scoreTagging(sentence.get(i), tag), word);
					chart.get(i).get(i+1).put(tag, wordTag);
				}
				addUnaryRuleThree(chart, i, i+1);
			}
			// next fill out the chart
			for (int diff = 2; diff <= sentence.size(); diff++){
				for (int i = 0; i <= sentence.size() - diff; i++){
					int j = i + diff;
					//if (i == 0 && j == sentence.size())
					//	System.out.println("Filling in the top of the chart.");
					
					checkChart(chart, i, j);
					for (int k = i + 1; k < j; k++){
						for (String leftChild : chart.get(i).get(k).keySet()){
							for (BinaryRule br : grammar.getBinaryRulesByLeftChild(leftChild)){
								
								//System.out.println(br.getLeftChild());
							//	if (chart.get(k).get(j).containsKey(br.getRightChild())){
									//System.out.println("Made it here!");
									if (chart.get(i).get(k).containsKey(br.getLeftChild()) && chart.get(k).get(j).containsKey(br.getRightChild())){
										addBinaryRuleThree(chart, i, j, k, br);
									}
								//}
							}
						}
					}
					addUnaryRuleThree(chart, i, j);
				}
			}
			
			/*
			int diff = 1;
			for (int i = 0; i <= sentence.size() - diff; i++){
				String best = getSingleHighestScoring(chart.get(i).get(i + diff));
				System.out.println(chart.get(i).get(i+diff).get(best).firstChild.tag + " : " + best + " : " + chart.get(i).get(i+diff).get(best).prob);
			}
			*/
			
			//System.out.println(chart.get(0).get(sentence.size()).keySet().size());
			String best = getSingleHighestScoring(chart.get(0).get(sentence.size()));
			System.out.println("Prob of RBS -> cat: " + lexicon.scoreTagging("cat", "RBS"));
			System.out.println("Prob of highest scoring tree: " + chart.get(0).get(sentence.size()).get(best).prob);
			Tree<String> t = growTreeHelper(chart.get(0).get(sentence.size()).get(best));
			return buildTree(t);
			
		}
		
		private void addUnaryRuleThree(Map<Integer, Map<Integer, Map<String, NodeInfo>>> chart, int i, int j){
			Map<String, NodeInfo> tmpMap = new HashMap<String, NodeInfo>();
			// loop over the unary rules with i,j as it's child.
			for (String childTag : chart.get(i).get(j).keySet()){
				for (UnaryRule ur : uc.getClosedUnaryRulesByChild(childTag)){
					// if the head of the rule isn't in the chart yet at i,j, or if the prob is lower than this prob, creating the backing path,
					// then add it to the chart.
					if (!chart.get(i).get(j).containsKey(ur.getParent()) 
							|| chart.get(i).get(j).get(ur.getParent()).prob < chart.get(i).get(j).get(ur.getChild()).prob * ur.getScore()){
						NodeInfo parent = createBackingPath(chart, i, j, ur);
						tmpMap.put(ur.getParent(), parent);
					}
				}
			}
			for (String s : tmpMap.keySet()){
				if (!chart.get(i).get(j).containsKey(s) 
					|| (chart.get(i).get(j).get(s).prob < tmpMap.get(s).prob)) {
					chart.get(i).get(j).put(s, tmpMap.get(s));
				}
			}

		}
		
		private NodeInfo createBackingPath(Map<Integer, Map<Integer, Map<String, NodeInfo>>> chart, int i, int j, UnaryRule ur){
			// grab the bottom node
			NodeInfo child = chart.get(i).get(j).get(ur.getChild());
			double prob = child.prob * ur.getScore();
			List<String> path = uc.getPath(ur);

			for (int k = path.size() - 2; k >= 0; k--){
				NodeInfo n = new NodeInfo(path.get(k), prob, child);
				child= n;
			}
			return child;
		}
		
		private void addBinaryRuleThree(Map<Integer, Map<Integer, Map<String, NodeInfo>>> chart, int i, int j, int k, BinaryRule br){
			double prob = br.score * chart.get(i).get(k).get(br.getLeftChild()).prob
					* chart.get(k).get(j).get(br.getRightChild()).prob;
			if ((!chart.get(i).get(j).containsKey(br.getParent()) 
					|| (chart.get(i).get(j).get(br.getParent()).prob < prob))
					&& prob > 0){
				NodeInfo n = new NodeInfo(br.getParent(), prob, chart.get(i).get(k).get(br.getLeftChild()), 
						chart.get(k).get(j).get(br.getRightChild()));
				chart.get(i).get(j).put(br.getParent(), n);
			}
		}
		
		private void checkChart(Map<Integer, Map<Integer, Map<String, NodeInfo>>> chart, int i, int j){
			if (!chart.containsKey(i))
				chart.put(i, new HashMap<Integer, Map<String, NodeInfo>>());
			if (!chart.get(i).containsKey(j))
				chart.get(i).put(j, new HashMap<String, NodeInfo>());
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		//@Override
		public Tree<String> getBestParseTwo(List<String> sentence){
			//testingStuff(sentence);
			List<List<Map<String, NodeInfo>>> score = new ArrayList<List<Map<String, NodeInfo>>>();
			// initialize my chart
			for (int i = 0; i < sentence.size(); i++){
				score.add(new ArrayList<Map<String, NodeInfo>>());
				for (int j = 0; j < sentence.size()+1; j++){
					score.get(i).add(new HashMap<String, NodeInfo>());
				}
			}
			// add the word tags
			for (int i = 0; i < sentence.size(); i++){
				NodeInfo wordNode = new NodeInfo(sentence.get(i));
				for (String tag : lexicon.getAllTags()){
					if (lexicon.scoreTagging(sentence.get(i), tag) > 0){
						double prob = lexicon.scoreTagging(sentence.get(i), tag);
						NodeInfo tagNode = new NodeInfo(tag, prob, wordNode);
						score.get(i).get(i+1).put(tag, tagNode);
						//if (prob >= .000001)
						//	System.out.println(sentence.get(i) + " : " + tag + " : " + prob);
					}
				}
				addUnaryRulesTwo(score, i, i+1);
			}
			
			
			// to test that the preterminals are making sense
			/*
			for (int i = 0; i < sentence.size(); i++){
				String best = getSingleHighestScoring(score.get(i).get(i+1));
				System.out.println(sentence.get(i) + " : " + best + " : " + score.get(i).get(i+1).get(best).prob);
			}
			for (int i = 0; i < sentence.size(); i++){
				addUnaryRulesTwo(score, i, i+1);
			}
			for (int i = 0; i < sentence.size(); i++){
				String best = getSingleHighestScoring(score.get(i).get(i+1));
				System.out.println(sentence.get(i) + " : " + best + " : " + score.get(i).get(i+1).get(best).prob);
			}
			*/
			
			// fill out the chart
			
			for (int diff = 2; diff < sentence.size() + 1; diff++){
				for (int i = 0; i < sentence.size() - diff + 1; i++){
					int j = i + diff;
					addBinaryRules(score, i, j);
					addUnaryRulesTwo(score, i, j);
				}
			}
			return buildTree(score, sentence);
		}
		
		public void addBinaryRules(List<List<Map<String, NodeInfo>>> score, int i, int j){
			Map<String, NodeInfo> tmpMap = new HashMap<String, NodeInfo>();
			for (int k = i + 1; k <= j - 1; k++){
				for (String leftChild : score.get(i).get(k).keySet()){
					for (BinaryRule br : grammar.getBinaryRulesByLeftChild(leftChild)){
						// add binary rules
						// left: i,k. right: k,j.
						if (score.get(i).get(k).containsKey(br.getLeftChild()) && score.get(k).get(j).containsKey(br.getRightChild())){
							double newProb = br.getScore() * score.get(i).get(k).get(br.getLeftChild()).prob 
									* score.get(k).get(j).get(br.getRightChild()).prob;
							if ((!score.get(i).get(j).containsKey(br.parent) && newProb > 0)
									|| score.get(i).get(j).get(br.parent).prob < newProb){
								NodeInfo n = new NodeInfo(br.parent, newProb, score.get(i).get(k).get(br.leftChild),
										score.get(k).get(j).get(br.rightChild));
								tmpMap.put(br.parent, n);
								//score.get(i).get(j).put(br.parent, n);
							}
						}
					}
				}
			}
			addRulesToChart(score, tmpMap, i, j);
		}
		
		public void addUnaryRulesTwo(List<List<Map<String, NodeInfo>>> score, int i, int j){
			Map<String, NodeInfo> tmpMap = new HashMap<String, NodeInfo>();
			for (String tag : score.get(i).get(j).keySet()){
				for (UnaryRule ur : uc.getClosedUnaryRulesByChild(tag)){
					double newProb = ur.getScore() * score.get(i).get(j).get(ur.getChild()).prob;
					if ((!score.get(i).get(j).containsKey(ur.parent) && newProb > 0) ||
							newProb > score.get(i).get(j).get(ur.parent).prob){
						NodeInfo n = buildUnaryPath(ur, newProb, score, i, j);
						tmpMap.put(ur.parent, n);
					}
				}
			}
			addRulesToChart(score, tmpMap, i, j);
		}
		
		// to put the new items into the chart
		private void addRulesToChart(List<List<Map<String, NodeInfo>>> score, Map<String, NodeInfo> tmpMap, int i, int j){
			for (String s : tmpMap.keySet()){
				if (!score.get(i).get(j).containsKey(s) || score.get(i).get(j).get(s).prob < tmpMap.get(s).prob)
					score.get(i).get(j).put(s, tmpMap.get(s));
			}
		}
		
		private NodeInfo buildUnaryPath(UnaryRule ur, double newProb, List<List<Map<String, NodeInfo>>> score, int i, int j){
			// need loop to go over path. 
			// instantiate current to be the end of the list
			List<String> path = uc.getPath(ur);
			NodeInfo current = score.get(i).get(j).get(ur.getChild());

			for (int k = path.size() - 2; k >= 0; k--){
				NodeInfo n = new NodeInfo(path.get(k), newProb, current);
				current = n;
			}
			return current;
		}
		
		private void testingStuff(List<String> sentence){
			String word = "sleeps";
			System.out.println("Word: " + word);
			for (String tag : lexicon.getAllTags()){
				if (tag.startsWith("V"))
					System.out.println(tag + " : " + lexicon.scoreTagging(word, tag));
			}
			System.exit(1);
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// TODO: go over this method, and make a new addNodeToChart method, that takes a chart, i, j, node, and checks 
		// to make sure new prob is higher than the old prob, or that the tag isn't in that node.
		
		//@Override
		public Tree<String> getBestParse(List<String> sentence) {
			List<List<Map<String, NodeInfo>>> score = new ArrayList<List<Map<String, NodeInfo>>>();
			List<List<Map<String, NodeInfo>>> testScore = new ArrayList<List<Map<String, NodeInfo>>>();
			// initializing score
			for (int i = 0; i < sentence.size(); i++){
				score.add(new ArrayList<Map<String, NodeInfo>>());
				testScore.add(new ArrayList<Map<String, NodeInfo>>());
				for (int j = 0; j < sentence.size() + 1; j++){
					score.get(i).add(new HashMap<String, NodeInfo>());
					testScore.get(i).add(new HashMap<String, NodeInfo>());
				}
			}
			// first pass over score and give a tagscore to each of the words.
			for (int i = 0; i < score.size(); i++){
				for (String tag : lexicon.getAllTags()){
					NodeInfo word = new NodeInfo(sentence.get(i));
					double sc = lexicon.scoreTagging(sentence.get(i), tag);
					if (sc > 0 && (!score.get(i).get(i+1).containsKey(tag) || 
							score.get(i).get(i+1).get(tag).prob < sc)){
						NodeInfo n = new NodeInfo(tag, sc, word);
						score.get(i).get(i + 1).put(tag, n);
						testScore.get(i).get(i+1).put(tag, n);
					}
				}
				addUnaryRules(i, i+1, score);
				addUnaryRules(i, i+1, testScore);
			}
			
			/*
			// to test the number of possible tags in each 
			for (int i = 0; i < score.size(); i++){
				System.out.print(sentence.get(i) + ": ");
				String best = "";
				double bestP = -1;
				for (String s : score.get(i).get(i+1).keySet()){
					if (score.get(i).get(i+1).get(s).prob>bestP){
						bestP = score.get(i).get(i+1).get(s).prob;
						best = s;
					}
				}
				System.out.println(best + " at " + bestP);
				System.out.println("The list of tags using this as a parent:");
				for (UnaryRule s : uc.getClosedUnaryRulesByParent(best)){
					System.out.println(s.child);
				}
				System.out.println(score.get(i).get(i+1).keySet().size());
			}
			System.exit(0);
			*/
			
			// fill in the rest of the table with scores
			for (int diff = 2; diff < sentence.size() + 1; diff++){
				for (int i = 0; i < sentence.size() - diff + 1; i++){
					int j = i + diff;
					Map<String, NodeInfo> tmpMap = new HashMap<String, NodeInfo>();
					for (BinaryRule bRule : grammar.getBinaryRules()){
						for (int k = i + 1; k < j; k++){
							NodeInfo n = bestScoreB(bRule, i, j, k, score);
							// check to make sure the head of bRule isn't already in the node.
							if (n.prob > 0){
								if (!score.get(i).get(j).containsKey(bRule.parent) 
										|| score.get(i).get(j).get(bRule.parent).prob < n.prob){
									tmpMap.put(bRule.parent, n);
									score.get(i).get(j).put(bRule.parent, n);
									
								}
							}
						}
					}
					System.out.println("Size of testScore[i,j]: " + testScore.get(i).get(j).size());
					System.out.println("Is @NP->_NP_CONJP in tmpMap? " 
							+ tmpMap.containsKey("@NP->_NP_CONJP"));
					System.out.println("It's probability: " + tmpMap.get("@NP->_NP_CONJP").prob);
					System.out.println("The size of tmpMap and the current node in score: (Note: should be same.) " +
							"tmpMap: " + tmpMap.size() + "    score: " + score.get(i).get(j).size());
					addRulesToChart(testScore, tmpMap, i, j);
					System.out.println("The size of testScore after adding rules: " + testScore.get(i).get(j).size());
					System.out.println("Is @NP->_NP_CONJP in testScore after adding tmpMap? " 
							+ testScore.get(i).get(j).containsKey("@NP->_NP_CONJP"));
					addUnaryRules(i,j, score);
					addUnaryRules(i,j, testScore);
					
					testMethodsForAddingToChart(score, testScore, i, j);
					
				}
			}
			return buildTree(score, sentence);
		}
		
		private void testMethodsForAddingToChart(List<List<Map<String, NodeInfo>>> score,
				List<List<Map<String, NodeInfo>>> testScore, int i, int j){
			testHaveSameValues(score, testScore, i, j);
			System.out.println("Size of current node in score: " + score.get(i).get(j).size());
			System.out.println("Size of current node in testScore: " + testScore.get(i).get(j).size());

			Queue<NodeInfo> q = findDiffForTestScore(score, testScore, i, j);
			System.out.println("A list of tags in score but not in testScore: ");
			for (int k = 0; k < 10; k++){
				System.out.println(q.remove().tag);
			}

			
			System.exit(0);
			
			
		}
		
		// search through one score, and if the other has a matching key, and 
		// the value has the same probability and it's children have the same names,
		// don't add it to the queue.
		private Queue<NodeInfo> findDiffForTestScore(List<List<Map<String, NodeInfo>>> score,
				List<List<Map<String, NodeInfo>>> testScore, int i, int j){
			
			Collection<NodeInfo> scoreNodeInfos = score.get(i).get(j).values();
			Collection<NodeInfo> testScoreNodeInfos = testScore.get(i).get(j).values();
			
			Set<NodeInfo> symmetricDiff = new HashSet<NodeInfo>(scoreNodeInfos);
			symmetricDiff.addAll(testScoreNodeInfos);
			Set<NodeInfo> tmp = new HashSet<NodeInfo>(scoreNodeInfos);
			tmp.retainAll(testScoreNodeInfos);
			symmetricDiff.removeAll(tmp);
			
			System.out.println("Size of symmetric diff: " + symmetricDiff.size());
			return new PriorityQueue<NodeInfo>(symmetricDiff);
		}
		

		
		private void testHaveSameValues(List<List<Map<String, NodeInfo>>> score,
				List<List<Map<String, NodeInfo>>> testScore, int i, int j){
			for (String tag : score.get(i).get(j).keySet()){
				if (!testScore.get(i).get(j).containsKey(tag))
					System.out.println(tag + " was not found in testScore, but was in score.");
				else if (testScore.get(i).get(j).get(tag).prob != score.get(i).get(j).get(tag).prob){
					System.out.println(tag + " had different probabilities in score and testScore.");
				}
			}
			for (String tag : testScore.get(i).get(j).keySet()){
				if (!score.get(i).get(j).containsKey(tag))
					System.out.println(tag + " was not found in score, but was in testScore.");
			}
			
		}
		
		private void addUnaryRules(int i, int j, List<List<Map<String, NodeInfo>>> score){
			Map<String, NodeInfo> newTags = new HashMap<String, NodeInfo>();
			for (String tag : score.get(i).get(j).keySet()){
				// to add the unary rules
				for (UnaryRule uRule : uc.getClosedUnaryRulesByChild(tag)){
					// multiplying the unary rule prob by the prob of the tag
					double prob = uRule.getScore() * score.get(i).get(j).get(tag).prob;
					if ((score.get(i).get(j).containsKey(uRule.parent) && 
							score.get(i).get(j).get(uRule.parent).prob < prob) ||
							(!score.get(i).get(j).containsKey(uRule.parent) &&
							prob != 0 	)){
						NodeInfo cur = new NodeInfo(uRule.getParent(), prob, score.get(i).get(j).get(tag));
						newTags.put(uRule.getParent(), cur);
					}
				}
			}
			addRulesToChart(score, newTags, i, j);
			//score.get(i).get(j).putAll(newTags);
		}
		
		private NodeInfo bestScoreB(BinaryRule br, int i, int j, int k, List<List<Map<String, NodeInfo>>> score){
			if (!score.get(i).get(k).containsKey(br.leftChild) || !score.get(k).get(j).containsKey(br.rightChild))
				return new NodeInfo("", 0, null);
			double prob = br.score * score.get(i).get(k).get(br.leftChild).prob * score.get(k).get(j).get(br.rightChild).prob;
			return new NodeInfo(br.parent, prob, score.get(i).get(k).get(br.leftChild), score.get(k).get(j).get(br.rightChild));
		}
		
		private Tree<String> buildTree(List<List<Map<String, NodeInfo>>> score, List<String> sentence){
			/*
			System.out.println("Printing the labels at the top of the tree");
			System.out.println("Left rule for top thing: " + score.get(0).get(sentence.size()).get("S").firstChild.tag);
			*/
			boolean getSingleTop = true;
			Tree<String> ut = null;
			
			if (getSingleTop){
				String best = getSingleHighestScoring(score.get(0).get(sentence.size()));
				// to get the tree from the top scoring tag
				Tree<String> t = growTreeHelper(score.get(0).get(sentence.size()).get(best));
				ut = buildTree(t);
			} else{
				List<String> highest = getHighestScoring(score, sentence);
	
				//System.out.println("Number of possibilities in top node: " + score.get(0).get(sentence.size()).keySet().size());
				for (String best : highest){
					double bestP = score.get(0).get(sentence.size()).get(best).prob;
					//System.out.println("Tag at head of tree: " + best + " at " + bestP);
					Tree<String> t = growTreeHelper(score.get(0).get(sentence.size()).get(best));
					ut = buildTree(t);
					//System.out.println(Trees.PennTreeRenderer.render(t));
					output.println(ut);
					//System.out.println();
					//System.out.println();
					//System.exit(0);
				}
				output.close();
			}
			return ut;
		}
		
		private Tree<String> buildTree(Tree<String> t){
			List<Tree<String>> tmpList = new ArrayList<Tree<String>>();
			tmpList.add(t);
			Tree<String> withRoot = new Tree<String>("ROOT", tmpList);
			Tree<String> unannotated = TreeAnnotations.unAnnotateTree(withRoot);
			// undoing the vertical second-order markov assumption
			if (secondOrderVert)
				removeVertAnnotations(unannotated);
			// undoing the auxiliary verb splitting
			if (auxVerbSplit)
				removeAuxVerbSplit(unannotated);
			// undoing the specific annotation of "and" and "but".
			if (conjSplit)
				removeConjSplit(unannotated);
			return unannotated;
		}
		
		// to remove the "-BUTAND" annotation from the tags of "and" and "but".
		private void removeConjSplit(Tree<String> t){
			if (t.isPreTerminal()){
				String l = t.getLabel();
				if (l.endsWith("-BUTAND"))
					t.setLabel(l.substring(0, l.indexOf("-BUTAND")));
			}
			for (Tree<String> child : t.getChildren()){
				removeConjSplit(child);
			}
		}
		
		// to remove the "-AUX" suffix  from the tags of auxiliary verbs
		private void removeAuxVerbSplit(Tree<String> t){
			if (t.isPreTerminal()){
				if (t.getLabel().endsWith("-AUX"))
					t.setLabel(t.getLabel().substring(0, t.getLabel().indexOf("-AUX")));
			}
			for (Tree<String> child : t.getChildren()){
				removeAuxVerbSplit(child);
			}
		}
		
		// to remove the vertical second-order markov annotations
		private void removeVertAnnotations(Tree<String> t){
			if (t.isLeaf())
				return;
			// removing the annotation
			t.setLabel(t.getLabel().substring(0,t.getLabel().indexOf('^')));
			// pass children
			for (Tree<String> child : t.getChildren()){
				removeVertAnnotations(child);
			}
		}
		
		private String getSingleHighestScoring(Map<String, NodeInfo> map){
			double topProb = -1;
			String topString = "";
			for (String s : map.keySet()){
				if (map.get(s).prob > topProb) {
					topProb = map.get(s).prob;
					topString = s;
				}
			}
			return topString;
		}
		
		private List<String> getHighestScoring(List<List<Map<String, NodeInfo>>> score, List<String> sentence){
			List<String> l = new ArrayList<String>();
			Queue<NodeInfo> ordered = new PriorityQueue<NodeInfo>();
			int size = score.get(0).get(sentence.size()).keySet().size();
			ordered.addAll(score.get(0).get(sentence.size()).values());
			for (int i = 0; i < size; i++){
				l.add(ordered.remove().tag);
			}
			return l;
		}
		
		private Tree<String> growTreeHelper(NodeInfo n){
			if (n.firstChild == null){
				return new Tree<String>(n.tag);
			} else {
				List<Tree<String>> l = new ArrayList<Tree<String>>();
				l.add(growTreeHelper(n.firstChild));
				if (!n.isUnary())
					l.add(growTreeHelper(n.secondChild));
					
				return new Tree<String>(n.tag, l);
			}
		}
				
		private String getBestTag(String word) {
			double bestScore = Double.NEGATIVE_INFINITY;
			String bestTag = null;
			for (String tag : lexicon.getAllTags()) {
				double score = lexicon.scoreTagging(word, tag);
				if (bestTag == null || score > bestScore) {
					bestScore = score;
					bestTag = tag;
				}
			}
			return bestTag;
		}

		public MyParser(List<Tree<String>> trainTrees, boolean verticalSecondOrder, 
				boolean auxVerbsSplit, boolean conjunctionSplit) throws FileNotFoundException {
			conjSplit = conjunctionSplit;
			secondOrderVert = verticalSecondOrder;
			auxVerbSplit = auxVerbsSplit;
			output = new PrintWriter(new File("scratch.txt"));
			//printSomeTrees(trainTrees);

			if (conjSplit){
				System.out.print("Adding additional tag for conjunctions ... ");
				trainTrees = splitConjunctions(trainTrees);
				System.out.println("done.");
			}
			if (auxVerbSplit){
				System.out.print("Adding additional tag for auxiliary verbs ... ");
				trainTrees = splitAuxVerbs(trainTrees);
				System.out.println("done.");
			}
			if (secondOrderVert){
				System.out.print("Adding second order vertical markov assumption ... ");
				trainTrees = addVerticalAnnotations(trainTrees);
				System.out.println("done.");
			}
			
			System.out.print("Annotating / binarizing training trees ... ");
			List<Tree<String>> annotatedTrainTrees = annotateTrees(trainTrees);
			System.out.println("done.");

			System.out.print("Building grammar ... ");
			grammar = new Grammar(annotatedTrainTrees);
			System.out.println("done. (" + grammar.getStates().size()
					+ " states)");
			uc = new UnaryClosure(grammar);
			//System.out.println(uc);
			//testUnaryClosure();

			lexicon = new Lexicon(annotatedTrainTrees);
			System.out.println("The score for \"cat\" -> NN : " + lexicon.scoreTagging("cat", "NN"));
			System.out.println("The score for \"cat\" -> . : " + lexicon.scoreTagging("cat", "."));
			System.out.println("The score for \"eat\" -> UH : " + lexicon.scoreTagging("eat", "UH"));
			System.out.println("The score for \"eat\" -> VB : " + lexicon.scoreTagging("eat", "VB"));
			//knownParses = new CounterMap<List<String>, Tree<String>>();
			spanToCategories = new CounterMap<Integer, String>();
			//for (Tree<String> trainTree : annotatedTrainTrees) {
			//	List<String> tags = trainTree.getPreTerminalYield();
				//knownParses.incrementCount(tags, trainTree, 1.0);
			//	tallySpans(trainTree, 0);
			//}
			System.out.println("done.");
			// printSpans();
			
			/*
			spanToCategories = new CounterMap<Integer, String>();
			for (Tree<String> trainTree : annotatedTrainTrees) {
				List<String> tags = trainTree.getPreTerminalYield();
				knownParses.incrementCount(tags, trainTree, 1.0);
				tallySpans(trainTree, 0);
			}
			System.out.println("done.");
			 */
		}
		
		// To separate "&" and "but" from other conjunctions
		private List<Tree<String>> splitConjunctions(List<Tree<String>> trainTrees){
			List<Tree<String>> splitTrainTrees = new ArrayList<Tree<String>>();
			for (Tree<String> t : trainTrees){
				splitTrainTrees.add(singleTreeSplitConj(t));
			}
			return splitTrainTrees;
		}
		
		private Tree<String> singleTreeSplitConj(Tree<String> t){
			if (t.isPreTerminal()){
				String l = t.getChildren().get(0).getLabel();
				if (l.equals("but") || l.equals("and"))
					t.setLabel(t.getLabel() + "-BUTAND");
				return t;
			}
			List<Tree<String>> newTree = new ArrayList<Tree<String>>();
			for (Tree<String> child : t.getChildren()){
				newTree.add(singleTreeSplitConj(child));
			}
			return new Tree<String>(t.getLabel(), newTree);
		}
		
		// To append "-AUX" to the tags of auxiliary verbs.
		private List<Tree<String>> splitAuxVerbs(List<Tree<String>> trainTrees){
			List<Tree<String>> splitTrainTrees = new ArrayList<Tree<String>>();
			String[] auxVs = {"be", "am", "are", "is", "was", "were", "being", "can", "could", "do", "did", "does",
					"doing", "have", "had", "has", "having", "may", "might", "must", "shall", "should", "will", "would"};
			Set<String> auxVerbs = new HashSet<String>(Arrays.asList(auxVs));
			for (Tree<String> t : trainTrees){
				//System.out.println(Trees.PennTreeRenderer
				//		.render(t));
				Tree<String> curTree = singleTreeSplitAuxVerbs(t, auxVerbs);
				//System.out.println(Trees.PennTreeRenderer.render(curTree));
				splitTrainTrees.add(curTree);
			}
			return splitTrainTrees;
		}
		
		// Recursing over the tree. Helper for splitAuxVerbs
		private Tree<String> singleTreeSplitAuxVerbs(Tree<String> t, Set<String> auxVerbs){
			if (t.isPreTerminal()){
				if (auxVerbs.contains(t.getChildren().get(0).getLabel()))
					t.setLabel(t.getLabel() + "-AUX");
				return t;
			}
			List<Tree<String>> newTree = new ArrayList<Tree<String>>();
			for (Tree<String> child : t.getChildren()){
				newTree.add(singleTreeSplitAuxVerbs(child, auxVerbs));
			}
			return new Tree<String>(t.getLabel(), newTree);
			
		}
		
		// To add the second order vertical markov assumption.
		private List<Tree<String>> addVerticalAnnotations(List<Tree<String>> trainTrees){
			List<Tree<String>> vertTrainTrees = new ArrayList<Tree<String>>();
			for (Tree<String> t : trainTrees){
				//System.out.println(Trees.PennTreeRenderer
				//		.render(t));
				//System.out.println(Trees.PennTreeRenderer.render(singleTreeVertAnnotations(t, "ROOT", 2)));
				vertTrainTrees.add(singleTreeVertAnnotations(t, "ROOT", 2));
				//System.exit(0);
			}
			return vertTrainTrees;
		}
		
		// To recurse over the tree. A helper for addVerticalAnnotations.
		private Tree<String> singleTreeVertAnnotations(Tree<String> t, String parentLabel, int sizeOfVertMarkov){
			if (t.isPreTerminal()){
				return t;
			}
			List<Tree<String>> newTree = new ArrayList<Tree<String>>();
			for (Tree<String> child : t.getChildren()){
				newTree.add(singleTreeVertAnnotations(child, t.getLabel(), sizeOfVertMarkov));
			}
			return new Tree<String>(t.getLabel() + "^" + parentLabel, newTree);
		}
		
		private void testUnaryClosure(){
			for (UnaryRule ur : uc.getClosedUnaryRulesByChild("NP")){
				System.out.println(ur);
			}			
			System.exit(0);
		}
		
		private void printSpans(){
			for (int i : spanToCategories.keySet()){
				for (String s : spanToCategories.getCounter(i).keySet()){
					System.out.println("span size " + i + " with category " + s + " : " + spanToCategories.getCount(i, s));
				}
			}
		}
		
		private void printSomeTrees(List<Tree<String>> trainTrees) {
			int counter = 0;
			for (Tree<String> tree : trainTrees) {
				System.out.println(Trees.PennTreeRenderer.render(tree));
				System.out.println();
				System.out.println(Trees.PennTreeRenderer
						.render(TreeAnnotations.annotateTree(tree)));
				System.out.println("\n\n\n");
				if (counter == 10)
					break;
				counter++;
			}
		}

		private List<Tree<String>> annotateTrees(List<Tree<String>> trees) {
			// try traversing the tree, and adding a second-order vertical markov assumption. 
			// this is before the 'annotation', or the horizontal infinite-order binarization occurs.
			// don't forget remove the vertical annotations when parsing.
			List<Tree<String>> annotatedTrees = new ArrayList<Tree<String>>();
			for (Tree<String> tree : trees) {
				annotatedTrees.add(TreeAnnotations.annotateTree(tree));
			}
			return annotatedTrees;
		}
		
		private int tallySpans(Tree<String> tree, int start) {
			if (tree.isLeaf() || tree.isPreTerminal())
				return 1;
			int end = start;
			for (Tree<String> child : tree.getChildren()) {
				int childSpan = tallySpans(child, end);
				end += childSpan;
			}
			String category = tree.getLabel();
			if (!category.equals("ROOT"))
				spanToCategories.incrementCount(end - start, category, 1.0);
			return end - start;
		}

	}

	/**
	 * Baseline parser (though not a baseline I've ever seen before). Tags the
	 * sentence using the baseline tagging method, then either retrieves a known
	 * parse of that tag sequence, or builds a right-branching parse for unknown
	 * tag sequences.
	 */
	static class BaselineParser implements Parser {
		CounterMap<List<String>, Tree<String>> knownParses;
		CounterMap<Integer, String> spanToCategories;
		Lexicon lexicon;

		public Tree<String> getBestParse(List<String> sentence) {
			List<String> tags = getBaselineTagging(sentence);
			Tree<String> annotatedBestParse = null;
			if (knownParses.keySet().contains(tags)) {
				annotatedBestParse = getBestKnownParse(tags);
			} else {
				annotatedBestParse = buildRightBranchParse(sentence, tags);
			}
			return TreeAnnotations.unAnnotateTree(annotatedBestParse);
		}

		private Tree<String> buildRightBranchParse(List<String> words,
				List<String> tags) {
			Grammar g;
			int currentPosition = words.size() - 1;
			Tree<String> rightBranchTree = buildTagTree(words, tags,
					currentPosition);
			while (currentPosition > 0) {
				currentPosition--;
				rightBranchTree = merge(
						buildTagTree(words, tags, currentPosition),
						rightBranchTree);
			}
			rightBranchTree = addRoot(rightBranchTree);
			return rightBranchTree;
		}

		private Tree<String> merge(Tree<String> leftTree, Tree<String> rightTree) {
			int span = leftTree.getYield().size() + rightTree.getYield().size();
			String mostFrequentLabel = spanToCategories.getCounter(span)
					.argMax();
			List<Tree<String>> children = new ArrayList<Tree<String>>();
			children.add(leftTree);
			children.add(rightTree);
			return new Tree<String>(mostFrequentLabel, children);
		}

		private Tree<String> addRoot(Tree<String> tree) {
			return new Tree<String>("ROOT", Collections.singletonList(tree));
		}

		private Tree<String> buildTagTree(List<String> words,
				List<String> tags, int currentPosition) {
			Tree<String> leafTree = new Tree<String>(words.get(currentPosition));
			Tree<String> tagTree = new Tree<String>(tags.get(currentPosition),
					Collections.singletonList(leafTree));
			return tagTree;
		}

		private Tree<String> getBestKnownParse(List<String> tags) {
			return knownParses.getCounter(tags).argMax();
		}

		private List<String> getBaselineTagging(List<String> sentence) {
			List<String> tags = new ArrayList<String>();
			for (String word : sentence) {
				String tag = getBestTag(word);
				tags.add(tag);
			}
			return tags;
		}

		private String getBestTag(String word) {
			double bestScore = Double.NEGATIVE_INFINITY;
			String bestTag = null;
			for (String tag : lexicon.getAllTags()) {
				double score = lexicon.scoreTagging(word, tag);
				if (bestTag == null || score > bestScore) {
					bestScore = score;
					bestTag = tag;
				}
			}
			return bestTag;
		}

		public BaselineParser(List<Tree<String>> trainTrees) {

			// printSomeTrees(trainTrees);

			System.out.print("Annotating / binarizing training trees ... ");
			List<Tree<String>> annotatedTrainTrees = annotateTrees(trainTrees);
			System.out.println("done.");

			System.out.print("Building grammar ... ");
			Grammar grammar = new Grammar(annotatedTrainTrees);
			System.out.println("done. (" + grammar.getStates().size()
					+ " states)");
			UnaryClosure uc = new UnaryClosure(grammar);
			//System.out.println(uc);
			
			System.out
					.print("Discarding grammar and setting up a baseline parser ... ");
			lexicon = new Lexicon(annotatedTrainTrees);
			knownParses = new CounterMap<List<String>, Tree<String>>();
			spanToCategories = new CounterMap<Integer, String>();
			for (Tree<String> trainTree : annotatedTrainTrees) {
				List<String> tags = trainTree.getPreTerminalYield();
				knownParses.incrementCount(tags, trainTree, 1.0);
				tallySpans(trainTree, 0);
			}
			System.out.println("done.");
		}

		private List<Tree<String>> annotateTrees(List<Tree<String>> trees) {
			List<Tree<String>> annotatedTrees = new ArrayList<Tree<String>>();
			for (Tree<String> tree : trees) {
				annotatedTrees.add(TreeAnnotations.annotateTree(tree));
			}
			return annotatedTrees;
		}

		private int tallySpans(Tree<String> tree, int start) {
			if (tree.isLeaf() || tree.isPreTerminal())
				return 1;
			int end = start;
			for (Tree<String> child : tree.getChildren()) {
				int childSpan = tallySpans(child, end);
				end += childSpan;
			}
			String category = tree.getLabel();
			if (!category.equals("ROOT"))
				spanToCategories.incrementCount(end - start, category, 1.0);
			return end - start;
		}
	}

	/**
	 * Class which contains code for annotating and binarizing trees for the
	 * parser's use, and debinarizing and unannotating them for scoring.
	 */
	static class TreeAnnotations {
		public static Tree<String> annotateTree(Tree<String> unAnnotatedTree) {
			// Currently, the only annotation done is a lossless binarization
			// TODO : change the annotation from a lossless binarization to a
			// finite-order markov process (try at least 1st and 2nd order)
			// TODO : mark nodes with the label of their parent nodes, giving a
			// second order vertical markov process
			return binarizeTree(unAnnotatedTree);
		}

		private static Tree<String> binarizeTree(Tree<String> tree) {
			String label = tree.getLabel();
			if (tree.isLeaf())
				return new Tree<String>(label);
			if (tree.getChildren().size() == 1) {
				return new Tree<String>(label,
						Collections.singletonList(binarizeTree(tree
								.getChildren().get(0))));
			}
			// otherwise, it's a binary-or-more local tree, so decompose it into
			// a sequence of binary and unary trees.
			String intermediateLabel = "@" + label + "->";
			Tree<String> intermediateTree = binarizeTreeHelper(tree, 0,
					intermediateLabel);
			return new Tree<String>(label, intermediateTree.getChildren());
		}

		private static Tree<String> binarizeTreeHelper(Tree<String> tree,
				int numChildrenGenerated, String intermediateLabel) {
			Tree<String> leftTree = tree.getChildren()
					.get(numChildrenGenerated);
			List<Tree<String>> children = new ArrayList<Tree<String>>();
			children.add(binarizeTree(leftTree));
			if (numChildrenGenerated < tree.getChildren().size() - 1) {
				Tree<String> rightTree = binarizeTreeHelper(tree,
						numChildrenGenerated + 1, intermediateLabel + "_"
								+ leftTree.getLabel());
				children.add(rightTree);
			}
			return new Tree<String>(intermediateLabel, children);
		}

		public static Tree<String> unAnnotateTree(Tree<String> annotatedTree) {
			// Remove intermediate nodes (labels beginning with "@"
			// Remove all material on node labels which follow their base symbol
			// (cuts at the leftmost -, ^, or : character)
			// Examples: a node with label @NP->DT_JJ will be spliced out, and a
			// node with label NP^S will be reduced to NP
			Tree<String> debinarizedTree = Trees.spliceNodes(annotatedTree,
					new Filter<String>() {
						public boolean accept(String s) {
							return s.startsWith("@");
						}
					});
			Tree<String> unAnnotatedTree = (new Trees.FunctionNodeStripper())
					.transformTree(debinarizedTree);
			return unAnnotatedTree;
		}
	}

	/**
	 * Simple default implementation of a lexicon, which scores word, tag pairs
	 * with a smoothed estimate of P(tag|word)/P(tag).
	 */
	static class Lexicon {
		CounterMap<String, String> wordToTagCounters = new CounterMap<String, String>();
		double totalTokens = 0.0;
		double totalWordTypes = 0.0;
		Counter<String> tagCounter = new Counter<String>();
		Counter<String> wordCounter = new Counter<String>();
		Counter<String> typeTagCounter = new Counter<String>();

		public Set<String> getAllTags() {
			return tagCounter.keySet();
		}

		public boolean isKnown(String word) {
			return wordCounter.keySet().contains(word);
		}

		public double scoreTagging(String word, String tag) {
			double p_tag = tagCounter.getCount(tag) / totalTokens;
			double c_word = wordCounter.getCount(word);
			double c_tag_and_word = wordToTagCounters.getCount(word, tag);
			if (c_word < 10) { // rare or unknown
				c_word += 1.0;
				c_tag_and_word += typeTagCounter.getCount(tag) / totalWordTypes;
			}
			double p_word = (1.0 + c_word) / (totalTokens + 1.0);
			double p_tag_given_word = c_tag_and_word / c_word;
			return p_tag_given_word / p_tag * p_word;
		}

		public Lexicon(List<Tree<String>> trainTrees) {
			for (Tree<String> trainTree : trainTrees) {
				List<String> words = trainTree.getYield();
				List<String> tags = trainTree.getPreTerminalYield();
				for (int position = 0; position < words.size(); position++) {
					String word = words.get(position);
					String tag = tags.get(position);
					tallyTagging(word, tag);
				}
			}
		}

		private void tallyTagging(String word, String tag) {
			if (!isKnown(word)) {
				totalWordTypes += 1.0;
				typeTagCounter.incrementCount(tag, 1.0);
			}
			totalTokens += 1.0;
			tagCounter.incrementCount(tag, 1.0);
			wordCounter.incrementCount(word, 1.0);
			wordToTagCounters.incrementCount(word, tag, 1.0);
		}
	}

	/**
	 * Simple implementation of a PCFG grammar, offering the ability to look up
	 * rules by their child symbols. Rule probability estimates are just
	 * relative frequency estimates off of training trees.
	 */
	static class Grammar {
		Map<String, List<BinaryRule>> binaryRulesByLeftChild = new HashMap<String, List<BinaryRule>>();
		Map<String, List<BinaryRule>> binaryRulesByRightChild = new HashMap<String, List<BinaryRule>>();
		Map<String, List<BinaryRule>> binaryRulesByParent = new HashMap<String, List<BinaryRule>>();
		List<BinaryRule> binaryRules = new ArrayList<BinaryRule>();
		Map<String, List<UnaryRule>> unaryRulesByChild = new HashMap<String, List<UnaryRule>>();
		Map<String, List<UnaryRule>> unaryRulesByParent = new HashMap<String, List<UnaryRule>>();
		List<UnaryRule> unaryRules = new ArrayList<UnaryRule>();
		Set<String> states = new HashSet<String>();

		public List<BinaryRule> getBinaryRulesByLeftChild(String leftChild) {
			return CollectionUtils.getValueList(binaryRulesByLeftChild,
					leftChild);
		}

		public List<BinaryRule> getBinaryRulesByRightChild(String rightChild) {
			return CollectionUtils.getValueList(binaryRulesByRightChild,
					rightChild);
		}

		public List<BinaryRule> getBinaryRulesByParent(String parent) {
			return CollectionUtils.getValueList(binaryRulesByParent, parent);
		}

		public List<BinaryRule> getBinaryRules() {
			return binaryRules;
		}

		public List<UnaryRule> getUnaryRulesByChild(String child) {
			return CollectionUtils.getValueList(unaryRulesByChild, child);
		}

		public List<UnaryRule> getUnaryRulesByParent(String parent) {
			return CollectionUtils.getValueList(unaryRulesByParent, parent);
		}

		public List<UnaryRule> getUnaryRules() {
			return unaryRules;
		}

		public Set<String> getStates() {
			return states;
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			List<String> ruleStrings = new ArrayList<String>();
			for (String parent : binaryRulesByParent.keySet()) {
				for (BinaryRule binaryRule : getBinaryRulesByParent(parent)) {
					ruleStrings.add(binaryRule.toString());
				}
			}
			for (String parent : unaryRulesByParent.keySet()) {
				for (UnaryRule unaryRule : getUnaryRulesByParent(parent)) {
					ruleStrings.add(unaryRule.toString());
				}
			}
			for (String ruleString : CollectionUtils.sort(ruleStrings)) {
				sb.append(ruleString);
				sb.append("\n");
			}
			return sb.toString();
		}

		private void addBinary(BinaryRule binaryRule) {
			states.add(binaryRule.getParent());
			states.add(binaryRule.getLeftChild());
			states.add(binaryRule.getRightChild());
			binaryRules.add(binaryRule);
			CollectionUtils.addToValueList(binaryRulesByParent,
					binaryRule.getParent(), binaryRule);
			CollectionUtils.addToValueList(binaryRulesByLeftChild,
					binaryRule.getLeftChild(), binaryRule);
			CollectionUtils.addToValueList(binaryRulesByRightChild,
					binaryRule.getRightChild(), binaryRule);
		}

		private void addUnary(UnaryRule unaryRule) {
			states.add(unaryRule.getParent());
			states.add(unaryRule.getChild());
			unaryRules.add(unaryRule);
			CollectionUtils.addToValueList(unaryRulesByParent,
					unaryRule.getParent(), unaryRule);
			CollectionUtils.addToValueList(unaryRulesByChild,
					unaryRule.getChild(), unaryRule);
		}

		public Grammar(List<Tree<String>> trainTrees) {
			Counter<UnaryRule> unaryRuleCounter = new Counter<UnaryRule>();
			Counter<BinaryRule> binaryRuleCounter = new Counter<BinaryRule>();
			Counter<String> symbolCounter = new Counter<String>();
			for (Tree<String> trainTree : trainTrees) {
				tallyTree(trainTree, symbolCounter, unaryRuleCounter,
						binaryRuleCounter);
			}
			for (UnaryRule unaryRule : unaryRuleCounter.keySet()) {
				double unaryProbability = unaryRuleCounter.getCount(unaryRule)
						/ symbolCounter.getCount(unaryRule.getParent());
				unaryRule.setScore(unaryProbability);
				addUnary(unaryRule);
			}
			for (BinaryRule binaryRule : binaryRuleCounter.keySet()) {
				double binaryProbability = binaryRuleCounter
						.getCount(binaryRule)
						/ symbolCounter.getCount(binaryRule.getParent());
				binaryRule.setScore(binaryProbability);
				addBinary(binaryRule);
			}
		}

		private void tallyTree(Tree<String> tree,
				Counter<String> symbolCounter,
				Counter<UnaryRule> unaryRuleCounter,
				Counter<BinaryRule> binaryRuleCounter) {
			if (tree.isLeaf())
				return;
			if (tree.isPreTerminal())
				return;
			if (tree.getChildren().size() == 1) {
				UnaryRule unaryRule = makeUnaryRule(tree);
				symbolCounter.incrementCount(tree.getLabel(), 1.0);
				unaryRuleCounter.incrementCount(unaryRule, 1.0);
			}
			if (tree.getChildren().size() == 2) {
				BinaryRule binaryRule = makeBinaryRule(tree);
				symbolCounter.incrementCount(tree.getLabel(), 1.0);
				binaryRuleCounter.incrementCount(binaryRule, 1.0);
			}
			if (tree.getChildren().size() < 1 || tree.getChildren().size() > 2) {
				throw new RuntimeException(
						"Attempted to construct a Grammar with an illegal tree (unbinarized?): "
								+ tree);
			}
			for (Tree<String> child : tree.getChildren()) {
				tallyTree(child, symbolCounter, unaryRuleCounter,
						binaryRuleCounter);
			}
		}

		private UnaryRule makeUnaryRule(Tree<String> tree) {
			return new UnaryRule(tree.getLabel(), tree.getChildren().get(0)
					.getLabel());
		}

		private BinaryRule makeBinaryRule(Tree<String> tree) {
			return new BinaryRule(tree.getLabel(), tree.getChildren().get(0)
					.getLabel(), tree.getChildren().get(1).getLabel());
		}
	}

	static class BinaryRule {
		String parent;
		String leftChild;
		String rightChild;
		double score;

		public String getParent() {
			return parent;
		}

		public String getLeftChild() {
			return leftChild;
		}

		public String getRightChild() {
			return rightChild;
		}

		public double getScore() {
			return score;
		}

		public void setScore(double score) {
			this.score = score;
		}

		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (!(o instanceof BinaryRule))
				return false;

			final BinaryRule binaryRule = (BinaryRule) o;

			if (leftChild != null ? !leftChild.equals(binaryRule.leftChild)
					: binaryRule.leftChild != null)
				return false;
			if (parent != null ? !parent.equals(binaryRule.parent)
					: binaryRule.parent != null)
				return false;
			if (rightChild != null ? !rightChild.equals(binaryRule.rightChild)
					: binaryRule.rightChild != null)
				return false;

			return true;
		}

		public int hashCode() {
			int result;
			result = (parent != null ? parent.hashCode() : 0);
			result = 29 * result
					+ (leftChild != null ? leftChild.hashCode() : 0);
			result = 29 * result
					+ (rightChild != null ? rightChild.hashCode() : 0);
			return result;
		}

		public String toString() {
			return parent + " -> " + leftChild + " " + rightChild + " %% "
					+ score;
		}

		public BinaryRule(String parent, String leftChild, String rightChild) {
			this.parent = parent;
			this.leftChild = leftChild;
			this.rightChild = rightChild;
		}
	}

	static class UnaryRule {
		String parent;
		String child;
		double score;

		public String getParent() {
			return parent;
		}

		public String getChild() {
			return child;
		}

		public double getScore() {
			return score;
		}

		public void setScore(double score) {
			this.score = score;
		}

		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (!(o instanceof UnaryRule))
				return false;

			final UnaryRule unaryRule = (UnaryRule) o;

			if (child != null ? !child.equals(unaryRule.child)
					: unaryRule.child != null)
				return false;
			if (parent != null ? !parent.equals(unaryRule.parent)
					: unaryRule.parent != null)
				return false;

			return true;
		}

		public int hashCode() {
			int result;
			result = (parent != null ? parent.hashCode() : 0);
			result = 29 * result + (child != null ? child.hashCode() : 0);
			return result;
		}

		public String toString() {
			return parent + " -> " + child + " %% " + score;
		}

		public UnaryRule(String parent, String child) {
			this.parent = parent;
			this.child = child;
		}
	}

	/**
	 * Calculates and provides accessors for the REFLEXIVE, TRANSITIVE closure
	 * of the unary rules in the provided Grammar. Each rule in this closure
	 * stands for zero or more unary rules in the original grammar. Use the
	 * getPath() method to retrieve the full sequence of symbols (from parent to
	 * child) which support that path.
	 */
	static class UnaryClosure {
		Map<String, List<UnaryRule>> closedUnaryRulesByChild = new HashMap<String, List<UnaryRule>>();
		Map<String, List<UnaryRule>> closedUnaryRulesByParent = new HashMap<String, List<UnaryRule>>();
		Map<UnaryRule, List<String>> pathMap = new HashMap<UnaryRule, List<String>>();

		public List<UnaryRule> getClosedUnaryRulesByChild(String child) {
			return CollectionUtils.getValueList(closedUnaryRulesByChild, child);
		}

		public List<UnaryRule> getClosedUnaryRulesByParent(String parent) {
			return CollectionUtils.getValueList(closedUnaryRulesByParent,
					parent);
		}

		public List<String> getPath(UnaryRule unaryRule) {
			return pathMap.get(unaryRule);
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			for (String parent : closedUnaryRulesByParent.keySet()) {
				for (UnaryRule unaryRule : getClosedUnaryRulesByParent(parent)) {
					List<String> path = getPath(unaryRule);
					// if (path.size() == 2) continue;
					sb.append(unaryRule);
					sb.append("  ");
					sb.append(path);
					sb.append("\n");
				}
			}
			return sb.toString();
		}

		public UnaryClosure(Collection<UnaryRule> unaryRules) {
			Map<UnaryRule, List<String>> closureMap = computeUnaryClosure(unaryRules);
			for (UnaryRule unaryRule : closureMap.keySet()) {
				addUnary(unaryRule, closureMap.get(unaryRule));
			}
		}

		public UnaryClosure(Grammar grammar) {
			this(grammar.getUnaryRules());
		}

		private void addUnary(UnaryRule unaryRule, List<String> path) {
			CollectionUtils.addToValueList(closedUnaryRulesByChild,
					unaryRule.getChild(), unaryRule);
			CollectionUtils.addToValueList(closedUnaryRulesByParent,
					unaryRule.getParent(), unaryRule);
			pathMap.put(unaryRule, path);
		}

		private static Map<UnaryRule, List<String>> computeUnaryClosure(
				Collection<UnaryRule> unaryRules) {

			Map<UnaryRule, String> intermediateStates = new HashMap<UnaryRule, String>();
			Counter<UnaryRule> pathCosts = new Counter<UnaryRule>();
			Map<String, List<UnaryRule>> closedUnaryRulesByChild = new HashMap<String, List<UnaryRule>>();
			Map<String, List<UnaryRule>> closedUnaryRulesByParent = new HashMap<String, List<UnaryRule>>();

			Set<String> states = new HashSet<String>();

			for (UnaryRule unaryRule : unaryRules) {
				relax(pathCosts, intermediateStates, closedUnaryRulesByChild,
						closedUnaryRulesByParent, unaryRule, null,
						unaryRule.getScore());
				states.add(unaryRule.getParent());
				states.add(unaryRule.getChild());
			}

			for (String intermediateState : states) {
				List<UnaryRule> incomingRules = closedUnaryRulesByChild
						.get(intermediateState);
				List<UnaryRule> outgoingRules = closedUnaryRulesByParent
						.get(intermediateState);
				if (incomingRules == null || outgoingRules == null)
					continue;
				for (UnaryRule incomingRule : incomingRules) {
					for (UnaryRule outgoingRule : outgoingRules) {
						UnaryRule rule = new UnaryRule(
								incomingRule.getParent(),
								outgoingRule.getChild());
						double newScore = pathCosts.getCount(incomingRule)
								* pathCosts.getCount(outgoingRule);
						relax(pathCosts, intermediateStates,
								closedUnaryRulesByChild,
								closedUnaryRulesByParent, rule,
								intermediateState, newScore);
					}
				}
			}

			for (String state : states) {
				UnaryRule selfLoopRule = new UnaryRule(state, state);
				relax(pathCosts, intermediateStates, closedUnaryRulesByChild,
						closedUnaryRulesByParent, selfLoopRule, null, 1.0);
			}

			Map<UnaryRule, List<String>> closureMap = new HashMap<UnaryRule, List<String>>();

			for (UnaryRule unaryRule : pathCosts.keySet()) {
				unaryRule.setScore(pathCosts.getCount(unaryRule));
				List<String> path = extractPath(unaryRule, intermediateStates);
				closureMap.put(unaryRule, path);
			}

			System.out.println("SIZE: " + closureMap.keySet().size());

			return closureMap;

		}

		private static List<String> extractPath(UnaryRule unaryRule,
				Map<UnaryRule, String> intermediateStates) {
			List<String> path = new ArrayList<String>();
			path.add(unaryRule.getParent());
			String intermediateState = intermediateStates.get(unaryRule);
			if (intermediateState != null) {
				List<String> parentPath = extractPath(
						new UnaryRule(unaryRule.getParent(), intermediateState),
						intermediateStates);
				for (int i = 1; i < parentPath.size() - 1; i++) {
					String state = parentPath.get(i);
					path.add(state);
				}
				path.add(intermediateState);
				List<String> childPath = extractPath(new UnaryRule(
						intermediateState, unaryRule.getChild()),
						intermediateStates);
				for (int i = 1; i < childPath.size() - 1; i++) {
					String state = childPath.get(i);
					path.add(state);
				}
			}
			if (path.size() == 1
					&& unaryRule.getParent().equals(unaryRule.getChild()))
				return path;
			path.add(unaryRule.getChild());
			return path;
		}

		private static void relax(Counter<UnaryRule> pathCosts,
				Map<UnaryRule, String> intermediateStates,
				Map<String, List<UnaryRule>> closedUnaryRulesByChild,
				Map<String, List<UnaryRule>> closedUnaryRulesByParent,
				UnaryRule unaryRule, String intermediateState, double newScore) {
			if (intermediateState != null
					&& (intermediateState.equals(unaryRule.getParent()) || intermediateState
							.equals(unaryRule.getChild())))
				return;
			boolean isNewRule = !pathCosts.containsKey(unaryRule);
			double oldScore = (isNewRule ? Double.NEGATIVE_INFINITY : pathCosts
					.getCount(unaryRule));
			if (oldScore > newScore)
				return;
			if (isNewRule) {
				CollectionUtils.addToValueList(closedUnaryRulesByChild,
						unaryRule.getChild(), unaryRule);
				CollectionUtils.addToValueList(closedUnaryRulesByParent,
						unaryRule.getParent(), unaryRule);
			}
			pathCosts.setCount(unaryRule, newScore);
			intermediateStates.put(unaryRule, intermediateState);
		}

	}

	public static void main(String[] args) throws FileNotFoundException {
		// Parse command line flags and arguments
		Map<String, String> argMap = CommandLineUtils
				.simpleCommandLineParser(args);

		// Set up default parameters and settings
		String basePath = ".";
		boolean verbose = true;
		String testMode = "validate";
		int maxTrainLength = 1000;
		int maxTestLength = 20;

		// Update defaults using command line specifications
		if (argMap.containsKey("-path")) {
			basePath = argMap.get("-path");
			System.out.println("Using base path: " + basePath);
		}
		if (argMap.containsKey("-test")) {
			testMode = "test";
			System.out.println("Testing on final test data.");
		} else {
			System.out.println("Testing on validation data.");
		}
		if (argMap.containsKey("-maxTrainLength")) {
			maxTrainLength = Integer.parseInt(argMap.get("-maxTrainLength"));
		}
		System.out.println("Maximum length for training sentences: "
				+ maxTrainLength);
		if (argMap.containsKey("-maxTestLength")) {
			maxTestLength = Integer.parseInt(argMap.get("-maxTestLength"));
		}
		System.out.println("Maximum length for test sentences: "
				+ maxTestLength);
		if (argMap.containsKey("-verbose")) {
			verbose = true;
		}
		if (argMap.containsKey("-quiet")) {
			verbose = false;
		}
		File f = new File(basePath);
		System.out.println("File path: " + f.getAbsolutePath());

		System.out.print("Loading training trees (sections 2-21) ... ");
		List<Tree<String>> trainTrees = readTrees(basePath, 200, 2199,maxTrainLength);
		System.out.println("done. (" + trainTrees.size() + " trees)");
		List<Tree<String>> testTrees = null;
		if (testMode.equalsIgnoreCase("validate")) {
			System.out.print("Loading validation trees (section 22) ... ");
			testTrees = readTrees(basePath, 2200, 2200, maxTestLength);
		} else {
			System.out.print("Loading test trees (section 23) ... ");
			testTrees = readTrees(basePath, 2300, 2319, maxTestLength);
		}
		System.out.println("done. (" + testTrees.size() + " trees)");

		// 
		boolean verticalSecondOrder = false;
		boolean auxVerbsSplit = false;
		boolean conjunctionSplit = false;

		MyParser parser = new MyParser(trainTrees, verticalSecondOrder, auxVerbsSplit, conjunctionSplit);

		List<String> test = new ArrayList<String>();
		test.add("The");
		test.add("cat");
		test.add("sat");
		//test.add("and");
		//test.add("would");
		//test.add("read");
		//test.add("a");
		//test.add("book");
		test.add(".");

		//System.out.println(PennTreeRenderer.render(parser.getBestParseOne(test)));
		//System.out.println(PennTreeRenderer.render(parser.getBestParseTwo(test)));
		//System.out.println(PennTreeRenderer.render(parser.getBestParse(test)));
		testParser(parser, testTrees, verbose);
	}

	private static void testParser(Parser parser, List<Tree<String>> testTrees,
			boolean verbose) {
		EnglishPennTreebankParseEvaluator.LabeledConstituentEval<String> eval = new EnglishPennTreebankParseEvaluator.LabeledConstituentEval<String>(
				Collections.singleton("ROOT"),
				new HashSet<String>(Arrays.asList(new String[] { "''", "``",
						".", ":", "," })));
		int counter = 0;
		for (Tree<String> testTree : testTrees) {
			System.out.println("Trees left: " + (testTrees.size() - counter));
			List<String> testSentence = testTree.getYield();
			Tree<String> guessedTree = parser.getBestParse(testSentence);
			if (verbose) {
				System.out.println("Guess:\n"
						+ Trees.PennTreeRenderer.render(guessedTree));
				System.out.println("Gold:\n"
						+ Trees.PennTreeRenderer.render(testTree));
			}
			eval.evaluate(guessedTree, testTree);
			counter++;
		}
		eval.display(true);
	}

	private static List<Tree<String>> readTrees(String basePath, int low,
			int high, int maxLength) {
		Collection<Tree<String>> trees = PennTreebankReader.readTrees(basePath,
				low, high);
		// normalize trees
		Trees.TreeTransformer<String> treeTransformer = new Trees.StandardTreeNormalizer();
		List<Tree<String>> normalizedTreeList = new ArrayList<Tree<String>>();
		for (Tree<String> tree : trees) {
			Tree<String> normalizedTree = treeTransformer.transformTree(tree);
			if (normalizedTree.getYield().size() > maxLength)
				continue;
			// System.out.println(Trees.PennTreeRenderer.render(normalizedTree));
			normalizedTreeList.add(normalizedTree);
		}
		return normalizedTreeList;
	}
}
